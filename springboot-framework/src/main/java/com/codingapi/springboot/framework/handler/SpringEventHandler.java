package com.codingapi.springboot.framework.handler;

import java.util.List;

import com.codingapi.springboot.framework.event.ApplicationDomainEvent;

import org.springframework.context.ApplicationListener;

public class SpringEventHandler implements ApplicationListener<ApplicationDomainEvent>{


    public SpringEventHandler(List<IHandler> handlers){
        ApplicationHandlerUtils.getInstance().addHandlers(handlers);
    }

    @Override
    public void onApplicationEvent(ApplicationDomainEvent event){
        ApplicationHandlerUtils.getInstance().handler(event.getEvent());    
        
    }
    
}
