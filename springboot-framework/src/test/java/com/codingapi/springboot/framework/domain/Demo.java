package com.codingapi.springboot.framework.domain;

import com.codingapi.springboot.framework.event.ApplicationEventUtils;
import com.codingapi.springboot.framework.event.DemoChangeEvent;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class Demo {

    private String name;

    public void changeName(String name){
        String beforeName = this.name;
        this.name = name;
        //push event
        ApplicationEventUtils.getInstance().push(new DemoChangeEvent(beforeName,name));
    }
    
}
