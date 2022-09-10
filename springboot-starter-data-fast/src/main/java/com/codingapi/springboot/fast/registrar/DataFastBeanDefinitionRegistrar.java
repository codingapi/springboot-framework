package com.codingapi.springboot.fast.registrar;

import com.codingapi.springboot.fast.annotation.FastController;
import com.codingapi.springboot.framework.registrar.RegisterBeanScanner;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

import java.util.Set;

@Slf4j
@Configuration
public class DataFastBeanDefinitionRegistrar implements ImportBeanDefinitionRegistrar {


    @SneakyThrows
    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        RegisterBeanScanner registerBeanScanner = new RegisterBeanScanner(importingClassMetadata, FastController.class);
        Set<Class<?>> classSet = registerBeanScanner.findTypes();

        //register bean
        for (Class<?> clazz : classSet) {
            log.info("scanner @FastController class:{}", clazz);
            MvcMappingRegistrar.classSet.add(clazz);
        }
    }


}
