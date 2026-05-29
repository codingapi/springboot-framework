package com.codingapi.springboot.script;

import com.codingapi.springboot.script.properties.GroovyScriptProperties;
import com.codingapi.springboot.script.properties.PropertiesContext;
import com.codingapi.springboot.script.runner.GroovyScriptEngineRunner;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = "com.codingapi.springboot.script")
public class AutoConfiguration {

    @Bean
    public GroovyScriptEngineRunner tempClearRunner() {
        return new GroovyScriptEngineRunner();
    }

    @Bean
    @ConfigurationProperties(prefix = "codingapi.script")
    public GroovyScriptProperties groovyScriptProperties(){
        GroovyScriptProperties properties = new GroovyScriptProperties();
        PropertiesContext.getInstance().setProperties(properties);
        return properties;
    }

}
