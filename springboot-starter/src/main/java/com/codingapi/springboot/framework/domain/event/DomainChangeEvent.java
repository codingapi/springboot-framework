package com.codingapi.springboot.framework.domain.event;

import lombok.Getter;
import lombok.ToString;

/**
 * 实体字段变更事件
 */
@Getter
@ToString(callSuper = true)
public class DomainChangeEvent extends DomainEvent {

    /**
     * 字段名称
     */
    private final String fieldName;
    /**
     * 旧的值
     */
    private final Object oldValue;
    /**
     * 新的值
     */
    private final Object newValue;

    public DomainChangeEvent(Object entity, String fieldName, Object oldValue, Object newValue) {
        super(entity);
        this.fieldName = fieldName;
        this.oldValue = oldValue;
        this.newValue = newValue;
    }

}
