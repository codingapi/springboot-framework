package com.codingapi.springboot.fastshow;

import com.codingapi.springboot.fastshow.mapping.MvcEndpointMapping;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

@Configuration
@ConditionalOnClass(WebMvcConfigurer.class)
public class FastShowConfiguration {


    @Bean
    @ConditionalOnMissingBean
    public MvcEndpointMapping mvcEndpointMapping(RequestMappingHandlerMapping handlerMapping){
        return new MvcEndpointMapping(handlerMapping);
    }

}
