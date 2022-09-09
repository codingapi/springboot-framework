package com.codingapi.springboot.example.application.executor;

import com.codingapi.springboot.example.domain.entity.Demo;
import com.codingapi.springboot.example.domain.service.DemoChangeService;
import org.springframework.stereotype.Component;

@Component
public class DemoExecutor {

    public void swap(Demo demo1, Demo demo2) {
        DemoChangeService demoChangeService = new DemoChangeService(demo1,demo2);
        demoChangeService.swap();
    }
}
