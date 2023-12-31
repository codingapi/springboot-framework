package com.codingapi.springboot.fast;

import com.codingapi.springboot.fast.dynamic.DynamicQuery;
import com.codingapi.springboot.fast.executor.JpaExecutor;
import com.codingapi.springboot.fast.manager.EntityManagerInitializer;
import com.codingapi.springboot.fast.mapping.DynamicMappingRegister;
import com.codingapi.springboot.fast.mapping.DynamicScriptRegister;
import com.codingapi.springboot.fast.mapping.MvcEndpointMapping;
import com.codingapi.springboot.fast.registrar.MvcMappingRegistrar;
import org.springframework.aop.Advisor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import jakarta.persistence.EntityManager;
import java.util.List;

@Configuration
@ConditionalOnClass(WebMvcConfigurer.class)
public class DataFastConfiguration {


    @Bean
    @ConditionalOnMissingBean
    public MvcEndpointMapping mvcEndpointMapping(RequestMappingHandlerMapping handlerMapping) {
        return new MvcEndpointMapping(handlerMapping);
    }

    @Bean
    @ConditionalOnMissingBean
    public DynamicMappingRegister dynamicMapping(MvcEndpointMapping mvcEndpointMapping, DynamicQuery dynamicQuery, JdbcTemplate jdbcTemplate) {
        return new DynamicMappingRegister(mvcEndpointMapping, dynamicQuery, jdbcTemplate);
    }

    @Bean
    @ConditionalOnMissingBean
    public DynamicScriptRegister dynamicScriptRegister(MvcEndpointMapping mvcEndpointMapping, DynamicQuery dynamicQuery, JdbcTemplate jdbcTemplate) {
        return new DynamicScriptRegister(mvcEndpointMapping, dynamicQuery, jdbcTemplate);
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
    public EntityManagerInitializer entityManagerInitializer(EntityManager entityManager){
        return new EntityManagerInitializer(entityManager);
    }

    @Bean
    @ConditionalOnMissingBean
    public JpaExecutor jpaExecutor(EntityManager entityManager) {
        return new JpaExecutor(entityManager);
    }

}
