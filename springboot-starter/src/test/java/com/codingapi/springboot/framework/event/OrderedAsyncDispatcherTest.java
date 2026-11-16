package com.codingapi.springboot.framework.event;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OrderedAsyncDispatcherTest {

    @Test
    void same_trace_tasks_execute_in_submit_order() throws Exception {
        ExecutorService pool = Executors.newFixedThreadPool(8);
        OrderedAsyncDispatcher dispatcher = new OrderedAsyncDispatcher(pool, true);
        List<String> order = new ArrayList<>();
        CountDownLatch done = new CountDownLatch(3);

        for (int i = 0; i < 3; i++) {
            final int index = i;
            dispatcher.execute("trace-1", () -> {
                order.add("start-" + index);
                try {
                    Thread.sleep(30);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                order.add("end-" + index);
                done.countDown();
            });
        }

        assertTrue(done.await(5, TimeUnit.SECONDS));
        assertEquals(Arrays.asList(
                "start-0", "end-0",
                "start-1", "end-1",
                "start-2", "end-2"), order);
        pool.shutdownNow();
    }

    @Test
    void different_trace_tasks_run_in_parallel() throws Exception {
        ExecutorService pool = Executors.newFixedThreadPool(4);
        OrderedAsyncDispatcher dispatcher = new OrderedAsyncDispatcher(pool, true);
        CountDownLatch bothStarted = new CountDownLatch(2);
        CountDownLatch release = new CountDownLatch(1);
        AtomicBoolean parallel = new AtomicBoolean(false);

        for (int i = 0; i < 2; i++) {
            dispatcher.execute("trace-" + i, () -> {
                bothStarted.countDown();
                try {
                    if (!release.await(3, TimeUnit.SECONDS)) {
                        return;
                    }
                    if (bothStarted.getCount() == 0) {
                        parallel.set(true);
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
        }

        assertTrue(bothStarted.await(3, TimeUnit.SECONDS), "不同 trace 的任务应能并行启动");
        release.countDown();
        Thread.sleep(100);
        assertTrue(parallel.get(), "不同 trace 的任务应并行执行");
        pool.shutdownNow();
    }

    @Test
    void exception_does_not_break_chain() throws Exception {
        ExecutorService pool = Executors.newFixedThreadPool(4);
        OrderedAsyncDispatcher dispatcher = new OrderedAsyncDispatcher(pool, true);
        List<String> order = new ArrayList<>();
        CountDownLatch done = new CountDownLatch(2);

        dispatcher.execute("trace-ex", () -> {
            order.add("first");
            done.countDown();
            throw new RuntimeException("boom");
        });
        dispatcher.execute("trace-ex", () -> {
            order.add("second");
            done.countDown();
        });

        assertTrue(done.await(5, TimeUnit.SECONDS));
        assertEquals(Arrays.asList("first", "second"), order);
        pool.shutdownNow();
    }

    @Test
    void completed_trace_is_removed() throws Exception {
        ExecutorService pool = Executors.newFixedThreadPool(4);
        OrderedAsyncDispatcher dispatcher = new OrderedAsyncDispatcher(pool, true);
        CountDownLatch done = new CountDownLatch(2);

        dispatcher.execute("trace-clean", done::countDown);
        dispatcher.execute("trace-clean", done::countDown);

        assertTrue(done.await(5, TimeUnit.SECONDS));
        long deadline = System.currentTimeMillis() + 3000;
        while (dispatcher.pendingTraceCount() > 0 && System.currentTimeMillis() < deadline) {
            Thread.sleep(20);
        }
        assertEquals(0, dispatcher.pendingTraceCount(), "已完成的 trace 应被清理，避免内存泄漏");
        pool.shutdownNow();
    }

    @Test
    void unordered_mode_keeps_legacy_behavior() throws Exception {
        ExecutorService pool = Executors.newFixedThreadPool(4);
        OrderedAsyncDispatcher dispatcher = new OrderedAsyncDispatcher(pool, false);
        assertEquals(0, dispatcher.pendingTraceCount());

        CountDownLatch done = new CountDownLatch(1);
        dispatcher.execute("trace-x", done::countDown);
        assertTrue(done.await(5, TimeUnit.SECONDS));
        assertEquals(0, dispatcher.pendingTraceCount(), "关闭有序模式时不应维护 trace 队列");
        pool.shutdownNow();
    }
}
