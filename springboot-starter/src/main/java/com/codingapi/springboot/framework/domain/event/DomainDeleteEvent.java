package com.codingapi.springboot.framework.domain.event;

import lombok.Getter;

/**
 * 实体删除事件
 */
@Getter
public class DomainDeleteEvent extends DomainEvent {

    public DomainDeleteEvent(Object entity) {
        super(entity);
    }

}
