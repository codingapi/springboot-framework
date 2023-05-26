package com.codingapi.springboot.framework.domain;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class EntityProxyFactory {

    public static <T> T createEntity(Class<T> entityClass, Object... args) {
        try {
            Class<?>[] parameterTypes = new Class<?>[args.length];
            for (int i = 0; i < args.length; i++) {
                parameterTypes[i] = args[i].getClass();
            }
            Constructor<T> constructor = entityClass.getConstructor(parameterTypes);
            T entity =  constructor.newInstance(args);
            EntityEventInterceptor interceptor = new EntityEventInterceptor(entity);
            return (T)interceptor.createProxy();
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException("Failed to create entity", e);
        }
    }
}
