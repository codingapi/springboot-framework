package com.codingapi.springboot.framework.handler;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.filter.AnnotationTypeFilter;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Map;

@Slf4j
@Configuration
public class HandlerBeanDefinitionRegistrar implements ImportBeanDefinitionRegistrar {


    @SneakyThrows
    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        final Map<String, Object> attributes =  importingClassMetadata.getAnnotationAttributes(SpringBootApplication.class.getName());
        if(attributes==null){
            return;
        }

        String defaultPackage = (Class.forName(importingClassMetadata.getClassName())).getPackage().getName();
        log.debug("defaultPackage:{}",defaultPackage);
        //获取包扫描
        ClassPathScanningCandidateComponentProvider pathScanningCandidateComponentProvider = new ClassPathScanningCandidateComponentProvider(false);

        //添加过滤 带有Handler这个注解的类
        pathScanningCandidateComponentProvider.addIncludeFilter(new AnnotationTypeFilter(Handler.class));

        LinkedHashSet<BeanDefinition> candidateComponents = new LinkedHashSet<>();
        String[] scanBasePackages = (String[])attributes.getOrDefault("scanBasePackages", Collections.singletonList(defaultPackage));

        for (String basePackages : scanBasePackages) {
            candidateComponents.addAll(pathScanningCandidateComponentProvider.findCandidateComponents(basePackages));
        }
        log.debug("candidateComponents:{}",candidateComponents);
        //注册Bean
        for (BeanDefinition candidateComponent : candidateComponents) {
            String beanName = candidateComponent.getBeanClassName();
            registry.registerBeanDefinition(beanName, candidateComponent);
        }
    }


}
