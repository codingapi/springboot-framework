package com.codingapi.springboot.framework.domain.event;

import lombok.Getter;

/**
 * 实体持久化事件
 */
@Getter
public class DomainPersistEvent extends DomainEvent {

    public DomainPersistEvent(Object entity) {
        super(entity);
    }
}
