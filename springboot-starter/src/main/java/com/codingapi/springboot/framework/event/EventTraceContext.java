package com.codingapi.springboot.framework.event;

import com.codingapi.springboot.framework.exception.EventLoopException;
import com.codingapi.springboot.framework.utils.RandomGenerator;
import lombok.Getter;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 事件跟踪上下文。
 *
 * <p>traceId 是异步事件串行分发的维度。为让「同一业务操作里连续 push 的多个事件」
 * 真正共享同一 traceId，这里按以下优先级解析：</p>
 * <ol>
 *   <li>handler 内嵌套 push：复用当前事件链的 traceId；</li>
 *   <li>显式批量 push：复用批量上下文的 traceId；</li>
 *   <li>活跃事务内：绑定并复用事务级 traceId，事务完成后解绑（避免线程复用串 trace）；</li>
 *   <li>其余：每次 push 新建独立 traceId（保持单次推送既有语义）。</li>
 * </ol>
 */
public class EventTraceContext {

    @Getter
    private final static EventTraceContext instance = new EventTraceContext();

    /**
     * 事务级 traceId 的绑定 key。
     */
    private static final Object TRANSACTION_TRACE_KEY = new Object();

    // trace key，异步事件由线程池线程并发读写，需使用并发容器
    private final Set<String> traceKeys = ConcurrentHashMap.newKeySet();

    // 当前线程正在执行的事件 key（traceId#random）
    private final ThreadLocal<String> threadLocal = new ThreadLocal<>();

    // event listenerKey state，异步事件由线程池线程并发读写，需使用并发容器
    private final Map<String, Boolean> eventKeyState = new ConcurrentHashMap<>();

    // 显式批量 push 时共享的 traceId
    private final ThreadLocal<String> batchTraceLocal = new ThreadLocal<>();

    private EventTraceContext() {
    }

    /**
     * 创建一个全新的 traceId。
     */
    String newTrace() {
        String traceId = UUID.randomUUID().toString().replaceAll("-", "");
        traceKeys.add(traceId);
        return traceId;
    }

    /**
     * 解析本次 push 使用的 traceId。
     */
    String getOrCreateTrace() {
        String eventKey = threadLocal.get();
        if (eventKey != null) {
            return eventKey.split("#")[0];
        }
        String batchTrace = batchTraceLocal.get();
        if (batchTrace != null) {
            return batchTrace;
        }
        if (TransactionSynchronizationManager.isActualTransactionActive()) {
            return getOrCreateTransactionTrace();
        }
        return newTrace();
    }

    /**
     * 在同一事务内绑定并共享 traceId；事务完成后解绑，避免连接池线程复用串 trace。
     */
    private String getOrCreateTransactionTrace() {
        Object existing = TransactionSynchronizationManager.getResource(TRANSACTION_TRACE_KEY);
        if (existing instanceof String) {
            return (String) existing;
        }
        String traceId = newTrace();
        TransactionSynchronizationManager.bindResource(TRANSACTION_TRACE_KEY, traceId);
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCompletion(int status) {
                TransactionSynchronizationManager.unbindResourceIfPossible(TRANSACTION_TRACE_KEY);
            }
        });
        return traceId;
    }

    /**
     * 当前线程是否正处于 handler 执行中（即本次 push 为嵌套 push）。
     */
    boolean isNestedPush() {
        return threadLocal.get() != null;
    }

    String getBatchTrace() {
        return batchTraceLocal.get();
    }

    void setBatchTrace(String traceId) {
        batchTraceLocal.set(traceId);
    }

    void clearBatchTrace() {
        batchTraceLocal.remove();
    }

    /**
     * get event key
     * traceId = eventKey.split("#")[0]
     */
    public String getEventKey() {
        return threadLocal.get();
    }

    /**
     * create event key
     *
     * @param traceId traceId
     */
    void createEventKey(String traceId) {
        String eventKey = traceId + "#" + RandomGenerator.randomString(8);
        eventKeyState.put(eventKey, false);
        threadLocal.set(eventKey);
    }

    /**
     * check event state
     */
    void checkEventState() {
        String eventKey = threadLocal.get();
        if (eventKey != null) {
            Boolean state = eventKeyState.get(eventKey);
            if (state != null && !state) {
                String traceId = eventKey.split("#")[0];
                traceKeys.remove(traceId);
                EventStackContext.getInstance().remove(traceId);
            }
            eventKeyState.remove(eventKey);
        }
        threadLocal.remove();
    }

    /**
     * 注册事件到事件栈。
     *
     * <p>循环检测只对 handler 内嵌套 push 生效：业务线程中连续 push 的多个
     * （可能同类的）事件属于同一批次，不应被误判为循环调用。</p>
     */
    void addEvent(String traceId, IEvent event) {
        if (isNestedPush() && EventStackContext.getInstance().checkEventLoop(traceId, event)) {
            List<Class<?>> stack = EventStackContext.getInstance().getEventClasses(traceId);
            traceKeys.remove(traceId);
            EventStackContext.getInstance().remove(traceId);
            eventKeyState.remove(traceId);
            threadLocal.remove();
            throw new EventLoopException(stack == null ? new ArrayList<>() : new ArrayList<>(stack), event);
        }
        EventStackContext.getInstance().addEvent(traceId, event);
    }

    /**
     * clear trace
     */
    public void clearTrace() {
        String eventKey = threadLocal.get();
        if (eventKey != null) {
            String traceId = eventKey.split("#")[0];
            traceKeys.remove(traceId);
            EventStackContext.getInstance().remove(traceId);
            eventKeyState.remove(eventKey);
            threadLocal.remove();
        }
    }
}
