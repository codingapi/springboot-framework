package com.codingapi.springboot.example.application.executor;

import com.codingapi.springboot.example.domain.entity.Demo;
import com.codingapi.springboot.example.domain.repository.DemoRepository;
import com.codingapi.springboot.example.domain.service.DemoChangeService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class DemoExecutor {

    private final DemoRepository demoRepository;

    public void swap(Demo demo1, Demo demo2) {
        DemoChangeService demoChangeService = new DemoChangeService(demo1, demo2);
        demoChangeService.swap();
    }

    public void create(String name) {
        Demo demo = new Demo(name);
        demoRepository.save(demo);
    }
}
