package com.codingapi.springboot.example.domain.service;

import com.codingapi.springboot.example.domain.entity.Demo;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class DemoChangeService {

    private final Demo demo1;
    private final Demo demo2;

    public void swap() {
        String demo1Name = demo1.getName();
        String demo2Name = demo2.getName();

        demo1.changeName(demo2Name);
        demo2.changeName(demo1Name);
    }

}
