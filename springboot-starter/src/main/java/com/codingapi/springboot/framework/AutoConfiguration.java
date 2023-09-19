package com.codingapi.springboot.framework;

import com.codingapi.springboot.framework.servlet.ServletExceptionHandler;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;


@Configuration
public class AutoConfiguration {

    @Bean
    @ConditionalOnClass(value = {HandlerExceptionResolver.class, ModelAndView.class, MappingJackson2JsonView.class})
    public HandlerExceptionResolver servletExceptionHandler() {
        return new ServletExceptionHandler();
    }

}
