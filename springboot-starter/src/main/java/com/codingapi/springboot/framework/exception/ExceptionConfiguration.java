package com.codingapi.springboot.framework.exception;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ExceptionConfiguration {

    @Bean(initMethod = "init")
    public LocaleMessage exceptionLocaleMessage(MessageSource messageSource) {
        return new LocaleMessage(messageSource);
    }

}
