package com.codingapi.springboot.script;

import com.codingapi.springboot.script.runner.TempClearRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = "com.codingapi.springboot.script")
public class AutoConfiguration {

    @Bean
    public TempClearRunner tempClearRunner() {
        return new TempClearRunner();
    }

}
