package com.codingapi.springboot.fast;

import com.codingapi.springboot.fast.manager.EntityManagerInitializer;
import com.codingapi.springboot.fast.mapping.FastMvcMappingRegister;
import com.codingapi.springboot.fast.script.FastScriptMappingRegister;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

@Configuration
@ConditionalOnClass(WebMvcConfigurer.class)
public class DataFastConfiguration {


    @Bean
    @ConditionalOnMissingBean
    public FastMvcMappingRegister fastMvcMappingRegister(@Qualifier("requestMappingHandlerMapping") RequestMappingHandlerMapping requestMappingHandlerMapping) {
        return new FastMvcMappingRegister(requestMappingHandlerMapping);
    }


    @Bean
    @ConditionalOnMissingBean
    public EntityManagerInitializer entityManagerInitializer(EntityManager entityManager) {
        return new EntityManagerInitializer(entityManager);
    }


    @Bean
    public FastScriptMappingRegister fastScriptMappingRegister(FastMvcMappingRegister fastMvcMappingRegister) {
        return new FastScriptMappingRegister(fastMvcMappingRegister);
    }

}
