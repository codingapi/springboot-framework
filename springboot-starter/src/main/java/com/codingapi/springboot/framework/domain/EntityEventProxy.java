package com.codingapi.springboot.framework.domain;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cglib.proxy.Proxy;


@Slf4j
public class EntityEventProxy {

    public static <T> T createProxy(T target) {
         return (T) Proxy.newProxyInstance(
                target.getClass().getClassLoader(),
                target.getClass().getInterfaces(),
                 (proxy, method, args) -> {
                     String methodName = method.getName();
                     log.info("EntityEventProxy invoke method:{}", methodName);
                     log.info("EntityEventProxy invoke args:{}", args);
                     log.info("EntityEventProxy invoke target:{}", target);
                     return method.invoke(target, args);
                 });
    }
}
