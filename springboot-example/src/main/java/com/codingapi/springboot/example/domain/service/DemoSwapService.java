package com.codingapi.springboot.example.domain.service;

import com.codingapi.springboot.example.domain.Demo;
import org.springframework.stereotype.Service;

@Service
public class DemoSwapService {

    public void swap(Demo demo1,Demo demo2){
        String demo1Name = demo1.getName();
        String demo2Name = demo2.getName();

        demo1.changeName(demo2Name);
        demo2.changeName(demo1Name);
    }

}
