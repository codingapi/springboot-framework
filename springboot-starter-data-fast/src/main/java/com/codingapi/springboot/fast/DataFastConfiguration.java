package com.codingapi.springboot.fast;

import com.codingapi.springboot.fast.manager.EntityManagerInitializer;
import com.codingapi.springboot.fast.mapping.MvcMappingRegister;
import com.codingapi.springboot.fast.script.ScriptMappingRegister;
import jakarta.persistence.EntityManager;
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
    public MvcMappingRegister mvcMappingRegister(RequestMappingHandlerMapping handlerMapping) {
        return new MvcMappingRegister(handlerMapping);
    }


    @Bean
    @ConditionalOnMissingBean
    public EntityManagerInitializer entityManagerInitializer(EntityManager entityManager) {
        return new EntityManagerInitializer(entityManager);
    }


    @Bean
    public ScriptMappingRegister scriptMappingRegister(MvcMappingRegister mvcMappingRegister) {
        return new ScriptMappingRegister(mvcMappingRegister);
    }

}
