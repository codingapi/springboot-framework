package com.codingapi.springboot.framework.domain;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

@Slf4j
public class EntityEventInterceptor implements MethodInterceptor {

    private final Object target;

    public EntityEventInterceptor(Object target) {
        this.target = target;
    }

    public Object createProxy() {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(target.getClass());
        enhancer.setCallback(this);
        return enhancer.create();
    }


    @Override
    public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
        String methodName = method.getName();
        log.info("EntityEventProxy invoke method:{}", methodName);
        log.info("EntityEventProxy invoke args:{}", args);
        log.info("EntityEventProxy invoke target:{}", target);
        return method.invoke(target, args);
    }
}
