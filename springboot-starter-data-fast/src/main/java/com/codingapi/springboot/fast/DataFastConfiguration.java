package com.codingapi.springboot.fast;

import com.codingapi.springboot.fast.executor.JpaExecutor;
import com.codingapi.springboot.fast.mapping.MvcEndpointMapping;
import com.codingapi.springboot.fast.registrar.MvcMappingRegistrar;
import org.springframework.aop.Advisor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import javax.persistence.EntityManager;
import java.util.List;

@Configuration
@ConditionalOnClass(WebMvcConfigurer.class)
public class DataFastConfiguration {


    @Bean
    @ConditionalOnMissingBean
    public MvcEndpointMapping mvcEndpointMapping(RequestMappingHandlerMapping handlerMapping) {
        return new MvcEndpointMapping(handlerMapping);
    }

    @Bean(initMethod = "registerMvcMapping")
    @ConditionalOnMissingBean
    public MvcMappingRegistrar mappingRegistrar(MvcEndpointMapping mvcEndpointMapping,
                                                JpaExecutor jpaExecutor,
                                                List<Advisor> advisors) {
        return new MvcMappingRegistrar(mvcEndpointMapping, jpaExecutor,advisors);
    }

    @Bean
    @ConditionalOnMissingBean
    public JpaExecutor jpaExecutor(EntityManager entityManager) {
        return new JpaExecutor(entityManager);
    }

}
