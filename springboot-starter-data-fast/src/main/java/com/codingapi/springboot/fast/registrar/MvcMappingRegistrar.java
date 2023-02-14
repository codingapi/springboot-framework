package com.codingapi.springboot.fast.registrar;

import com.codingapi.springboot.fast.annotation.FastMapping;
import com.codingapi.springboot.fast.exception.FastMappingErrorException;
import com.codingapi.springboot.fast.executor.JpaExecutor;
import com.codingapi.springboot.fast.executor.MvcMethodInterceptor;
import com.codingapi.springboot.fast.mapping.MvcEndpointMapping;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.Advisor;
import org.springframework.aop.framework.AdvisedSupport;
import org.springframework.aop.framework.AopProxy;
import org.springframework.aop.framework.AopProxyFactory;
import org.springframework.aop.framework.DefaultAopProxyFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
public class MvcMappingRegistrar {
    protected final static Set<Class<?>> classSet = new HashSet<>();
    private final MvcEndpointMapping mvcEndpointMapping;

    private final AopProxyFactory proxyFactory;

    private final List<Advisor> advisors;

    private final MvcMethodInterceptor interceptor;

    public MvcMappingRegistrar(MvcEndpointMapping mvcEndpointMapping,
                               JpaExecutor jpaExecutor,
                               List<Advisor> advisors) {
        this.mvcEndpointMapping = mvcEndpointMapping;
        this.advisors = advisors;
        this.interceptor = new MvcMethodInterceptor(jpaExecutor);
        this.proxyFactory = new DefaultAopProxyFactory();
    }

    @SneakyThrows
    public void registerMvcMapping() {
        for (Class<?> clazz : classSet) {
            Method[] methods = clazz.getDeclaredMethods();
            for (Method method : methods) {
                FastMapping fastMapping = method.getAnnotation(FastMapping.class);
                if (verify(fastMapping, method)) {
                    AdvisedSupport advisedSupport = createAdvisedSupport(clazz);
                    AopProxy proxy = proxyFactory.createAopProxy(advisedSupport);
                    mvcEndpointMapping.addMapping(fastMapping.mapping(), fastMapping.method(),
                            proxy.getProxy(), method);
                }
            }
        }
    }

    private AdvisedSupport createAdvisedSupport(Class<?> clazz) {
        AdvisedSupport advisedSupport = new AdvisedSupport(clazz);
        advisedSupport.setTarget(interceptor);
        advisedSupport.addAdvisors(advisors);
        advisedSupport.addAdvice(interceptor);
        return advisedSupport;
    }

    private boolean verify(FastMapping fastMapping, Method method) throws FastMappingErrorException {
        if (fastMapping == null) {
            return false;
        }

        if (!StringUtils.hasText(fastMapping.mapping())) {
            throw new FastMappingErrorException(String.format("fast method %s missing mapping .",
                    method.getName()));
        }

        if (!StringUtils.hasText(fastMapping.value())) {
            throw new FastMappingErrorException(String.format("fast mapping %s missing value .",
                    fastMapping.mapping()));
        }

        Class<?>[] parameterTypes = method.getParameterTypes();
        for (Class<?> parameter : parameterTypes) {
            if (Pageable.class.isAssignableFrom(parameter)) {
                if (!StringUtils.hasText(fastMapping.countQuery())) {
                    throw new FastMappingErrorException(String.format("fast mapping %s missing countQuery .",
                            fastMapping.mapping()));
                }
            }
        }
        return true;
    }

}
