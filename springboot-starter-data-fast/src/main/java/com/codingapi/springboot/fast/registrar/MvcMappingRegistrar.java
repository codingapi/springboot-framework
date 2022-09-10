package com.codingapi.springboot.fast.registrar;

import com.codingapi.springboot.fast.annotation.FastMapping;
import com.codingapi.springboot.fast.exception.FastMappingErrorException;
import com.codingapi.springboot.fast.executor.JpaExecutor;
import com.codingapi.springboot.fast.executor.MvcMethodProxy;
import com.codingapi.springboot.fast.mapping.MvcEndpointMapping;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashSet;
import java.util.Set;

@Slf4j
@AllArgsConstructor
public class MvcMappingRegistrar {
    protected final static Set<Class<?>> classSet = new HashSet<>();
    private final MvcEndpointMapping mvcEndpointMapping;
    private final JpaExecutor jpaExecutor;

    @SneakyThrows
    public void registerMvcMapping() {
        for (Class<?> clazz : classSet) {
            Method[] methods = clazz.getDeclaredMethods();
            for (Method method : methods) {
                FastMapping fastMapping = method.getAnnotation(FastMapping.class);
                if (verify(fastMapping, method)) {
                    MvcMethodProxy handler = new MvcMethodProxy(jpaExecutor);
                    Object methodProxy = Proxy.newProxyInstance(clazz.getClassLoader(), new Class[]{clazz}, handler);
                    mvcEndpointMapping.addMapping(fastMapping.mapping(), fastMapping.method(), methodProxy, method);
                }
            }
        }
    }

    private boolean verify(FastMapping fastMapping, Method method) throws FastMappingErrorException {
        if (fastMapping == null) {
            return false;
        }

        if (!StringUtils.hasText(fastMapping.mapping())) {
            throw new FastMappingErrorException(String.format("fast method %s missing mapping .", method.getName()));
        }

        if (!StringUtils.hasText(fastMapping.value())) {
            throw new FastMappingErrorException(String.format("fast mapping %s missing value .", fastMapping.mapping()));
        }

        Class<?>[] parameterTypes = method.getParameterTypes();
        for (Class<?> parameter : parameterTypes) {
            if (Pageable.class.isAssignableFrom(parameter)) {
                if (!StringUtils.hasText(fastMapping.countQuery())) {
                    throw new FastMappingErrorException(String.format("fast mapping %s missing countQuery .", fastMapping.mapping()));
                }
            }
        }

        return true;
    }

}
