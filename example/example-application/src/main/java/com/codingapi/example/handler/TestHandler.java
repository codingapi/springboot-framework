package com.codingapi.example.handler;

import com.codingapi.example.event.AEvent;
import com.codingapi.example.event.TestEvent;
import com.codingapi.example.infra.entity.TestEntity;
import com.codingapi.example.infra.jpa.TestEntityRepository;
import com.codingapi.springboot.framework.event.EventPusher;
import com.codingapi.springboot.framework.event.IHandler;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@AllArgsConstructor
public class TestHandler implements IHandler<TestEvent> {

    private TestEntityRepository testEntityRepository;

    @Override
    public void handler(TestEvent event) {
        TestEntity entity = new TestEntity(event.getName()+"123");
        testEntityRepository.save(entity);

        new Thread(()->{
            EventPusher.push(new AEvent());
        }).start();

        new Thread(()->{
            EventPusher.push(new AEvent());
        }).start();
    }


}
