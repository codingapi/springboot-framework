package com.codingapi.springboot.framework.event;

import org.springframework.context.ApplicationContext;

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

    private void push(IEvent event, boolean sync,boolean hasLoopEvent) {
        if (context != null) {
            String traceId = EventTraceContext.getInstance().getOrCreateTrace();
            if(hasLoopEvent){
                EventTraceContext.getInstance().clearTrace();
                traceId = EventTraceContext.getInstance().getOrCreateTrace();
            }
            EventTraceContext.getInstance().addEvent(traceId,event);
            context.publishEvent(new DomainEvent(event, sync,traceId));
        }
    }

    /**
     * @param event
     * @see EventPusher
     * 默认 同步事件
     */
    public void push(IEvent event,boolean hasLoopEvent) {
        if (event instanceof IAsyncEvent) {
            this.push(event, false,hasLoopEvent);
        } else if (event instanceof ISyncEvent) {
            this.push(event, true,hasLoopEvent);
        } else {
            this.push(event, true,hasLoopEvent);
        }
    }


    protected void initContext(ApplicationContext context) {
        this.context = context;
    }


}
