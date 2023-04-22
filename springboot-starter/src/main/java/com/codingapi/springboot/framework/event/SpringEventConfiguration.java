package com.codingapi.springboot.framework.event;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringEventConfiguration {

    @Bean
    public SpringEventInitializer springEventInitializer(ApplicationContext context) {
        return new SpringEventInitializer(context);
    }

}
