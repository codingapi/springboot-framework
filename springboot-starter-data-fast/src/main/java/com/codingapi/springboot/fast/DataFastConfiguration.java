package com.codingapi.springboot.fast;

import com.codingapi.springboot.fast.jpa.JPAQuery;
import com.codingapi.springboot.fast.manager.EntityManagerInitializer;
import com.codingapi.springboot.fast.mapping.MvcMappingRegister;
import jakarta.persistence.EntityManager;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

@Configuration
@ConditionalOnClass(WebMvcConfigurer.class)
public class DataFastConfiguration {


    @Bean
    @ConditionalOnMissingBean
    public MvcMappingRegister mvcMappingRegister(RequestMappingHandlerMapping handlerMapping, JPAQuery JPAQuery, JdbcTemplate jdbcTemplate) {
        return new MvcMappingRegister(handlerMapping, JPAQuery, jdbcTemplate);
    }


    @Bean
    @ConditionalOnMissingBean
    public EntityManagerInitializer entityManagerInitializer(EntityManager entityManager) {
        return new EntityManagerInitializer(entityManager);
    }


}
