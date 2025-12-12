package com.codingapi.springboot.fast.jpa;

import javax.persistence.EntityManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class JpaQueryConfiguration {

    @Bean
    public JpaQuery dynamicQuery(EntityManager entityManager){
        return new JpaQuery(entityManager);
    }

    @Bean
    public JpaQueryContextRegister jpaQueryContextRegister(JpaQuery JPAQuery){
        return new JpaQueryContextRegister(JPAQuery);
    }

}
