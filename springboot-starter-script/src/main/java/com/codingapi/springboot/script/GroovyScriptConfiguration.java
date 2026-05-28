package com.codingapi.springboot.script;

import com.codingapi.springboot.script.repository.DefaultGroovyScriptRepository;
import com.codingapi.springboot.script.repository.GroovyScriptRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = "com.codingapi.springboot.script")
public class GroovyScriptConfiguration {


    @Bean
    @ConditionalOnMissingBean
    public GroovyScriptRepository groovyScriptRepository() {
        return new DefaultGroovyScriptRepository();
    }

}
