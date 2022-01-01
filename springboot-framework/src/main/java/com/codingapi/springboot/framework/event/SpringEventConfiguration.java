package com.codingapi.springboot.framework.event;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringEventConfiguration {

    @Autowired
    private ApplicationContext context;

    @PostConstruct
    public void init(){
        DomainEventContext.getInstance().initContext(context);
    }
    
}
