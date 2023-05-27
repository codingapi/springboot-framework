package com.codingapi.springboot.framework.domain.event;

import com.codingapi.springboot.framework.event.IEvent;
import lombok.Getter;
import lombok.ToString;

/**
 * 实体创建事件
 */
@Getter
@ToString
public class DomainCreateEvent implements IEvent {

    /**
     * 实体对象
     */
    private final Object entity;

    /**
     * 实体类名称
     */
    private final String simpleName;

    /**
     * 时间戳
     */
    private final long timestamp;

    public DomainCreateEvent(Object entity) {
        this.entity = entity;
        this.simpleName = entity.getClass().getSimpleName();
        this.timestamp = System.currentTimeMillis();
    }

}
