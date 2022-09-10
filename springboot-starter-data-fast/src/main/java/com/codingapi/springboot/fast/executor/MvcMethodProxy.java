package com.codingapi.springboot.fast.executor;

import com.codingapi.springboot.fast.annotation.FastMapping;
import lombok.AllArgsConstructor;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

@AllArgsConstructor
public class MvcMethodProxy implements InvocationHandler {

    private final JpaExecutor jpaExecutor;

    @Override
    public Object invoke(Object proxy, Method method, Object[] args)
            throws Throwable {

        if (method.equals(Object.class.getMethod("equals", Object.class))) {
            return false;
        }
        if (method.equals(Object.class.getMethod("hashCode"))) {
            return hashCode();
        }
        FastMapping fastMapping = method.getAnnotation(FastMapping.class);
        if (fastMapping != null) {
            Class<?> returnType = method.getReturnType();
            return jpaExecutor.execute(fastMapping.value(), fastMapping.countQuery(), args, returnType);
        }

        // mvc mapping proxy can't execute return null.
        return null;
    }
}