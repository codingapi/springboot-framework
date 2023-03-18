package com.codingapi.springboot.framework;

import com.codingapi.springboot.framework.servlet.ServletExceptionHandler;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerExceptionResolver;

@Configuration
public class AutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public HandlerExceptionResolver servletExceptionHandler() {
        return new ServletExceptionHandler();
    }

}
