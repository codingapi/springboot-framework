package com.codingapi.springboot.framework;

import com.codingapi.springboot.framework.properties.BootProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AutoConfiguration {


    @Bean
    @ConfigurationProperties(prefix = "codingapi.boot")
    public BootProperties bootProperties(){
        return new BootProperties();
    }

}
