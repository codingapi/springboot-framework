package com.codingapi.springboot.framework.handler;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringHandlerConfiguration {


    @Bean
    public SpringEventHandler springEventHandler(List<IHandler> handleres){
        return new SpringEventHandler(handleres);
    }
    
}
