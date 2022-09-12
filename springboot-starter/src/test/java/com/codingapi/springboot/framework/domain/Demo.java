package com.codingapi.springboot.framework.domain;

import com.codingapi.springboot.framework.event.DemoChangeEvent;
import com.codingapi.springboot.framework.event.EventPusher;
import com.codingapi.springboot.framework.serializable.JsonSerializable;
import com.codingapi.springboot.framework.serializable.MapSerializable;
import lombok.Getter;

public class Demo implements JsonSerializable, MapSerializable {

    @Getter
    private long id;
    @Getter
    private String name;

    public Demo(String name) {
        this.name = name;
        this.id = System.currentTimeMillis();
    }

    public void changeName(String name) {
        String beforeName = this.name;
        this.name = name;
        //push event
        EventPusher.push(new DemoChangeEvent(beforeName, name));
    }

}
