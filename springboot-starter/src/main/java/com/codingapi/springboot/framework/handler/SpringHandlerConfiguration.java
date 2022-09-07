package com.codingapi.springboot.framework.handler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SpringHandlerConfiguration {

    @Bean
    public SpringEventHandler springEventHandler(@Autowired(required = false) List<IHandler> handlers) {
        return new SpringEventHandler(handlers);
    }

}
