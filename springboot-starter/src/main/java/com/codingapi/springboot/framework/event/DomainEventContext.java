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

    private void push(IEvent event, boolean sync) {
        if (context != null) {
            context.publishEvent(new DomainEvent(event, sync));
        }
    }

    /**
     * @param event
     * @see EventPusher
     * 默认 同步事件
     */
    public void push(IEvent event) {
        if (event instanceof IAsyncEvent) {
            this.push(event, false);
        } else if (event instanceof ISyncEvent) {
            this.push(event, true);
        } else {
            this.push(event, true);
        }
    }


    protected void initContext(ApplicationContext context) {
        this.context = context;
    }


}
