package com.codingapi.springboot.fast.jpa;

import jakarta.persistence.EntityManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class JPAQueryConfiguration {

    @Bean
    public JPAQuery dynamicQuery(EntityManager entityManager){
        return new JPAQuery(entityManager);
    }

    @Bean
    public JPAQueryContextRegister jpaQueryContextRegister(JPAQuery JPAQuery){
        return new JPAQueryContextRegister(JPAQuery);
    }

}
