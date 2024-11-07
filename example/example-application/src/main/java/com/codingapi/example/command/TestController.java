package com.codingapi.example.command;

import com.codingapi.example.event.TestEvent;
import com.codingapi.example.infra.entity.TestEntity;
import com.codingapi.example.infra.jpa.TestEntityRepository;
import com.codingapi.springboot.framework.event.EventPusher;
import lombok.AllArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/open/test")
@AllArgsConstructor
public class TestController {

    private final TestEntityRepository testEntityRepository;


    @GetMapping("/hi")
    @Transactional
    public void hi(){
        TestEntity testEntity = new TestEntity("test");
        testEntityRepository.save(testEntity);

        TestEvent event = new TestEvent(testEntity.getName());
        EventPusher.push(event);
    }
}
