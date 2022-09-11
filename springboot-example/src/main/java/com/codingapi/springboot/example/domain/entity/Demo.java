package com.codingapi.springboot.example.domain.entity;

import com.codingapi.springboot.example.domain.event.DemoNameChangeEvent;
import com.codingapi.springboot.framework.event.EventPusher;
import com.codingapi.springboot.generator.IdGenerate;
import lombok.Getter;

/**
 * @author lorne
 * @since 1.0.0
 */
public class Demo implements IdGenerate {

    @Getter
    private Integer id;

    @Getter
    private String name;

    public Demo(String name) {
        this.id = generateIntId();
        this.name = name;
    }

    public void changeName(String name) {
        String oldName = this.name;
        this.name = name;

        EventPusher.push(new DemoNameChangeEvent(oldName, name));
    }

}
