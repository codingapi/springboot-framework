package com.codingapi.springboot.framework.domain.event;

import lombok.Getter;

/**
 * 实体创建事件
 */
@Getter
public class DomainCreateEvent extends DomainEvent {

    public DomainCreateEvent(Object entity) {
        super(entity);
    }
}
