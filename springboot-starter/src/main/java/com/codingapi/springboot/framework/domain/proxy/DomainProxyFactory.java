package com.codingapi.springboot.framework.domain.proxy;

import com.codingapi.springboot.framework.domain.event.DomainCreateEvent;
import com.codingapi.springboot.framework.event.EventPusher;

import java.lang.reflect.InvocationTargetException;

/**
 * 实体代理工厂
 */
public class DomainProxyFactory {

    public static <T> T create(Class<T> entityClass, Object... args) {
        DomainChangeInterceptor interceptor = null;
        try {
            interceptor = new DomainChangeInterceptor(entityClass, args);
        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        T result = (T) interceptor.createProxy();
        EventPusher.push(new DomainCreateEvent(result));
        return result;
    }
}
