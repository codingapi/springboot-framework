package com.codingapi.springboot.framework.handler;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DemoConfiguration {

    @Bean
    public DemoChangeLogHandler demoChangeLogHandler() {
        return new DemoChangeLogHandler();
    }


}
