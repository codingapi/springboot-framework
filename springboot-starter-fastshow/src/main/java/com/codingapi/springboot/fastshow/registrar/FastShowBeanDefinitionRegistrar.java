package com.codingapi.springboot.fastshow.registrar;

import com.codingapi.springboot.fastshow.annotation.FastController;
import com.codingapi.springboot.framework.registrar.RegisterBeanDefinition;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

import java.util.Set;

@Slf4j
@Configuration
public class FastShowBeanDefinitionRegistrar implements ImportBeanDefinitionRegistrar {


    @SneakyThrows
    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        RegisterBeanDefinition registerBeanDefinition = new RegisterBeanDefinition(importingClassMetadata, FastController.class);
        Set<Class<?>> classSet = registerBeanDefinition.findTypes();

        //注册Bean
        for (Class<?> clazz : classSet) {
            log.info("FastController class:{}",clazz);
            MvcMappingRegistrar.classSet.add(clazz);
        }
    }




}
