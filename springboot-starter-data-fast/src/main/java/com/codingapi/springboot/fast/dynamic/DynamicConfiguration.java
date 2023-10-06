package com.codingapi.springboot.fast.dynamic;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.persistence.EntityManager;

@Configuration
public class DynamicConfiguration {

    @Bean
    public DynamicQuery dynamicQuery(EntityManager entityManager){
        return new DynamicQuery(entityManager);
    }

    @Bean
    public DynamicQueryContextRegister dynamicQueryContextRegister(DynamicQuery dynamicQuery){
        return new DynamicQueryContextRegister(dynamicQuery);
    }

}
