package com.codingapi.springboot.framework.domain.event;

import com.codingapi.springboot.framework.event.IEvent;
import lombok.Getter;
import lombok.ToString;

/**
 * 实体事件
 */
@Getter
@ToString(exclude = "entity")
public abstract class DomainEvent implements IEvent {

    /**
     * 实体类型
     */
    private final Class<?> entityClass;
    /**
     * 时间戳
     */
    private final long timestamp;
    /**
     * 实体
     */
    private final Object entity;

    public DomainEvent(Object entity) {
        this.entity = entity;
        this.entityClass = entity.getClass();
        this.timestamp = System.currentTimeMillis();
    }
}
