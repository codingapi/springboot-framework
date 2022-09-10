package com.codingapi.springboot.framework.handler;

import com.codingapi.springboot.framework.registrar.RegisterBeanScanner;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

import java.util.List;

/**
 * handler bean注册器
 */
@Slf4j
@Configuration
public class HandlerBeanDefinitionRegistrar implements ImportBeanDefinitionRegistrar {


    @SneakyThrows
    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        RegisterBeanScanner registerBeanScanner = new RegisterBeanScanner(importingClassMetadata, Handler.class);
        List<BeanDefinition> beanDefinitions = registerBeanScanner.findBeanDefinitions();

        //register Bean
        for (BeanDefinition candidateComponent : beanDefinitions) {
            String beanName = candidateComponent.getBeanClassName();
            registry.registerBeanDefinition(beanName, candidateComponent);
        }
    }


}
