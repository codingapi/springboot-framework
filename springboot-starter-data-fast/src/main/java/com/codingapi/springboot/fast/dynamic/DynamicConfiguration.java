package com.codingapi.springboot.fast.dynamic;

import jakarta.persistence.EntityManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


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
