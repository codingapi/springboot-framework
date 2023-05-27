package com.codingapi.springboot.framework.domain.event;

import com.codingapi.springboot.framework.event.IEvent;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class DomainPersistEvent implements IEvent {

    private final Object entity;
    private final String simpleName;
    private final long timestamp;

    public DomainPersistEvent(Object entity) {
        this.entity = entity;
        this.simpleName = entity.getClass().getSimpleName();
        this.timestamp = System.currentTimeMillis();
    }
}
