package com.codingapi.springboot.framework.reflect.pojo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;

/**
 * 字段目标注解
 *
 * @param <T> 目标字段值类型
 */
@Getter
@AllArgsConstructor
public class AnnotationTargetField<T> {

    /**
     * 字段对象
     */
    private final Field field;
    /**
     * 绑定对象
     */
    private final Object target;
    /**
     * 字段值对象
     */
    private final T value;


    /**
     * 更新字段值
     * @param value 新值
     */
    public void update(T value) {
        this.field.setAccessible(true);
        ReflectionUtils.setField(this.field, target, value);
    }

}
