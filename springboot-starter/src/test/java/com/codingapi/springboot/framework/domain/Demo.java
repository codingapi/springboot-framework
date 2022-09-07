package com.codingapi.springboot.framework.domain;

import com.codingapi.springboot.framework.event.DemoChangeEvent;
import com.codingapi.springboot.framework.event.EventPusher;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class Demo {

    private String name;

    public void changeName(String name) {
        String beforeName = this.name;
        this.name = name;
        //push event
        EventPusher.push(new DemoChangeEvent(beforeName, name));
    }

}
