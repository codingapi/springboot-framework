package com.codingapi.springboot.framework.domain.field;

import java.lang.reflect.InvocationTargetException;

/**
 * 实体代理工厂
 */
public class FieldProxyFactory {

    public static <T> T create(Class<T> entityClass, Object... args) {
        FieldValueInterceptor interceptor = null;
        try {
            interceptor = new FieldValueInterceptor(entityClass, args);
        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        return (T) interceptor.createProxy();
    }
}
