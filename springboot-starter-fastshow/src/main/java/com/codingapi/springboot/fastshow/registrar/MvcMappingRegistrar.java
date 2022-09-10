package com.codingapi.springboot.fastshow.registrar;

import com.codingapi.springboot.fastshow.annotation.FastMapping;
import com.codingapi.springboot.fastshow.executor.JpaExecutor;
import com.codingapi.springboot.fastshow.mapping.MvcEndpointMapping;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashSet;
import java.util.Set;

@Slf4j
@AllArgsConstructor
public class MvcMappingRegistrar {
    protected final static  Set<Class<?>> classSet = new HashSet<>();
    private final MvcEndpointMapping mvcEndpointMapping;

    private final JpaExecutor jpaExecutor;

    @SneakyThrows
    public void registerMvcMapping() {
       for(Class<?> clazz:classSet){
           Method[] methods = clazz.getDeclaredMethods();
           for(Method method:methods){
               FastMapping fastMapping =  method.getAnnotation(FastMapping.class);
               if(fastMapping!=null) {
                   MethodProxy handler = new MethodProxy(jpaExecutor);
                   Object methodProxy =  Proxy.newProxyInstance(clazz.getClassLoader(), new Class[]{clazz}, handler);
                   mvcEndpointMapping.addMapping(fastMapping.mapping(), fastMapping.method(), methodProxy, method);
               }
           }
       }
    }

    @Slf4j
    @AllArgsConstructor
    public static class MethodProxy implements InvocationHandler {

        private final JpaExecutor jpaExecutor;

        @Override
        public Object invoke(Object proxy, Method method, Object[] args)
                throws Throwable {
            if(method.equals(Object.class.getMethod("equals", Object.class))){
                return false;
            }
            if(method.equals(Object.class.getMethod("hashCode"))){
                return hashCode();
            }
            log.info("method.name:{}",method.getName());
            log.info("xxx->{}",args);

            FastMapping fastMapping = method.getAnnotation(FastMapping.class);
            return jpaExecutor.execute(fastMapping.hql(),args);
        }
    }


}
