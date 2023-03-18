package com.codingapi.springboot.framework;

import com.codingapi.springboot.framework.servlet.ServletExceptionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerExceptionResolver;

@Configuration
public class AutoConfiguration {

    @Bean
    public HandlerExceptionResolver servletExceptionHandler() {
        return new ServletExceptionHandler();
    }

}
