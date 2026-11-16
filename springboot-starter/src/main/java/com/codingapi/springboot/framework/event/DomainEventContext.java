package com.codingapi.springboot.framework.event;

import org.springframework.context.ApplicationContext;

import java.util.List;

class DomainEventContext {

    private static DomainEventContext instance;
    private ApplicationContext context;

    private DomainEventContext() {
    }

    public static DomainEventContext getInstance() {
        if (instance == null) {
            synchronized (DomainEventContext.class) {
                if (instance == null) {
                    instance = new DomainEventContext();
                }
            }
        }
        return instance;
    }

    private boolean isAsync(IEvent event) {
        return event instanceof IAsyncEvent;
    }

    private void publish(IEvent event, String traceId) {
        context.publishEvent(new DomainEvent(event, !isAsync(event), traceId));
    }

    private void doPush(IEvent event, boolean hasLoopEvent) {
        if (context == null) {
            return;
        }
        EventTraceContext traceContext = EventTraceContext.getInstance();
        String traceId = hasLoopEvent
                ? traceContext.newTrace()
                : traceContext.getOrCreateTrace();
        traceContext.addEvent(traceId, event);
        publish(event, traceId);
    }

    /**
     * 批量推送：整批事件共享同一 traceId，从而按顺序串行分发。
     * 批量内的同类事件不会被误判为循环调用（防环只针对 handler 内嵌套 push）。
     */
    void pushBatch(List<IEvent> events) {
        if (context == null || events == null || events.isEmpty()) {
            return;
        }
        EventTraceContext traceContext = EventTraceContext.getInstance();
        String previous = traceContext.getBatchTrace();
        String traceId = traceContext.newTrace();
        traceContext.setBatchTrace(traceId);
        try {
            for (IEvent event : events) {
                traceContext.addEvent(traceId, event);
                publish(event, traceId);
            }
        } finally {
            if (previous == null) {
                traceContext.clearBatchTrace();
            } else {
                traceContext.setBatchTrace(previous);
            }
        }
    }

    /**
     * 默认 同步事件
     *
     * @see EventPusher
     */
    public void push(IEvent event, boolean hasLoopEvent) {
        doPush(event, hasLoopEvent);
    }

    protected void initContext(ApplicationContext context) {
        this.context = context;
    }
}
