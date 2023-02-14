package com.codingapi.springboot.fast.executor;

import com.codingapi.springboot.fast.annotation.FastMapping;
import lombok.AllArgsConstructor;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import java.lang.reflect.Method;

@AllArgsConstructor
public class MvcMethodInterceptor implements MethodInterceptor {

    private final JpaExecutor jpaExecutor;

    @Override
    public Object invoke(MethodInvocation invocation)
            throws Throwable {
        Method method = invocation.getMethod();
        Object[] args = invocation.getArguments();

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