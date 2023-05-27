package com.codingapi.springboot.framework.domain.event;

import com.codingapi.springboot.framework.event.IEvent;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 实体字段变更事件
 */
@Setter
@Getter
@ToString(exclude = {"entity"})
public class FieldChangeEvent implements IEvent {

    /**
     * 实体对象
     */
    private Object entity;

    /**
     * 实体类名称
     */
    private String simpleName;

    /**
     * 时间戳
     */
    private long timestamp;

    /**
     * 字段名称
     */
    private String fieldName;
    /**
     * 旧的值
     */
    private Object oldValue;
    /**
     * 新的值
     */
    private Object newValue;



}
