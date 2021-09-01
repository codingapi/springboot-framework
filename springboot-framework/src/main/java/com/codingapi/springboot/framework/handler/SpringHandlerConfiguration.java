package com.codingapi.springboot.framework.handler;

import java.util.List;

import com.codingapi.springboot.framework.event.IEvent;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringHandlerConfiguration {


    @Bean
    public SpringEventHandler springEventHandler(List<IHandler<IEvent>> handleres){
        return new SpringEventHandler(handleres);
    }
    
}
