package com.codingapi.springboot.fast.jpa;

import jakarta.persistence.EntityManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class JpaQueryConfiguration {

    @Bean
    public JpaQuery dynamicQuery(EntityManager entityManager){
        return new JpaQuery(entityManager);
    }

    @Bean
    public JpaQueryContextRegister jpaQueryContextRegister(JpaQuery jpaQuery){
        return new JpaQueryContextRegister(jpaQuery);
    }

}
