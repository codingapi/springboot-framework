package com.codingapi.springboot.framework.event;

import com.codingapi.springboot.framework.exception.EventLoopException;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;

/**
 * 按 traceId 串行、跨 traceId 并行的异步任务分发器。
 *
 * <p>同一 traceId 的任务按提交顺序串行执行（前一个任务完成后才开始下一个），
 * 不同 traceId 的任务仍交由同一个线程池并行处理，不牺牲吞吐。</p>
 */
@Slf4j
class OrderedAsyncDispatcher {

    /**
     * 每个 traceId 当前队尾任务。任务完成后若仍是队尾则移除，避免内存泄漏。
     */
    private final ConcurrentMap<String, CompletableFuture<Void>> tails = new ConcurrentHashMap<>();

    private final ExecutorService executorService;

    private final boolean ordered;

    OrderedAsyncDispatcher(ExecutorService executorService, boolean ordered) {
        this.executorService = executorService;
        this.ordered = ordered;
    }

    /**
     * 提交一个异步任务。
     *
     * @param key  串行维度（事件场景为 traceId）
     * @param task 任务
     */
    void execute(String key, Runnable task) {
        Runnable wrapped = wrap(task);
        if (!ordered) {
            executorService.execute(wrapped);
            return;
        }

        CompletableFuture<Void> tail = tails.compute(key, (traceId, previous) -> {
            CompletableFuture<Void> base = previous == null
                    ? CompletableFuture.completedFuture(null)
                    : previous.handle((ignored, throwable) -> null);
            return base.thenRunAsync(wrapped, executorService);
        });
        tail.whenComplete((ignored, throwable) -> tails.remove(key, tail));
    }

    /**
     * 统一两条路径的异常语义：捕获任务异常并记录，保证异常不会静默消失，
     * 也不会中断同一 trace 的后续任务。循环事件异常额外给出明确日志。
     */
    private Runnable wrap(Runnable task) {
        return () -> {
            try {
                task.run();
            } catch (EventLoopException e) {
                log.error("event loop detected while executing async event task", e);
            } catch (Exception e) {
                log.error("async event task execute error", e);
            }
        };
    }

    /**
     * 当前仍在排队/执行的 trace 数量，用于测试与内存泄漏校验。
     */
    int pendingTraceCount() {
        return tails.size();
    }
}
