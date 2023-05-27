package com.codingapi.springboot.framework.domain;

import com.codingapi.springboot.framework.event.DemoChangeEvent;
import com.codingapi.springboot.framework.event.EventPusher;
import com.codingapi.springboot.framework.serializable.JsonSerializable;
import com.codingapi.springboot.framework.serializable.MapSerializable;
import lombok.Getter;

public class Demo implements JsonSerializable, MapSerializable, IDomain {

    @Getter
    private final long id;

    @Getter
    private String name;

    @Getter
    private Animal animal;

    public Demo(String name) {
        this.name = name;
        this.id = System.currentTimeMillis();
        this.animal = new Animal();
        this.animal.setName("cat");
    }

    public void changeName(String name) {
        String beforeName = this.name;
        this.name = name;

        if (beforeName.equals(name)) {
            return;
        }
        //  push event
        EventPusher.push(new DemoChangeEvent(beforeName, name));
    }

    public void changeAnimalName(String name) {
        this.animal.setName(name);
    }
}
