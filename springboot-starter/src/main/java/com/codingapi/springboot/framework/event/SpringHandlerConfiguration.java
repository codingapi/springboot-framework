package com.codingapi.springboot.framework.event;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SpringHandlerConfiguration {

    @Bean
    @ConditionalOnProperty(value = "codingapi.framework.event.transaction.enable",havingValue = "true")
    public SpringEventHandler springTransactionEventHandler(@Autowired(required = false) List<IHandler> handlers) {
        return new SpringTransactionEventHandler(handlers);
    }

    @Bean
    @ConditionalOnMissingBean
    public SpringEventHandler springDefaultEventHandler(@Autowired(required = false) List<IHandler> handlers) {
        return new SpringDefaultEventHandler(handlers);
    }



}
