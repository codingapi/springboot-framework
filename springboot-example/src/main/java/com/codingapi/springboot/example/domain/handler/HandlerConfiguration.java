package com.codingapi.springboot.example.domain.handler;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author lorne
 * @since 1.0.0
 */
@Configuration
public class HandlerConfiguration {

    @Bean
    public DemoNameLogHandler demoNameLogHandler(){
        return new DemoNameLogHandler();
    }
}
