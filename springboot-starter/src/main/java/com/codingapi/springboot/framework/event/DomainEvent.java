package com.codingapi.springboot.framework.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * Event包装对象
 */
public class DomainEvent extends ApplicationEvent {

    @Getter
    private final IEvent event;
    /**
     * 同步或异步事件
     * true 同步
     * false 异步
     */
    @Getter
    private final boolean sync;

    public DomainEvent(Object source, boolean sync) {
        super(source);
        this.event = (IEvent) source;
        this.sync = sync;
    }

}
