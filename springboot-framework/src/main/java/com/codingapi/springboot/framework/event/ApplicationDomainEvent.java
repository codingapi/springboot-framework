package com.codingapi.springboot.framework.event;

import org.springframework.context.ApplicationEvent;

import lombok.Getter;


public class ApplicationDomainEvent extends ApplicationEvent{

    @Getter
    private IEvent event;

    public ApplicationDomainEvent(Object source) {
        super(source); 
        this.event = (IEvent)source;      
    }
    
}
