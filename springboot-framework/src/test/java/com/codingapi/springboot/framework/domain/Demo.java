package com.codingapi.springboot.framework.domain;

import com.codingapi.springboot.framework.event.DomainEventContext;
import com.codingapi.springboot.framework.event.DemoChangeEvent;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class Demo {

    private String name;

    public void changeName(String name){
        String beforeName = this.name;
        this.name = name;
        //push event
        DomainEventContext.getInstance().push(new DemoChangeEvent(beforeName,name));
    }
    
}
