package com.codingapi.example.handler;

import com.codingapi.example.event.TestEvent;
import com.codingapi.example.infra.entity.TestEntity;
import com.codingapi.example.infra.jpa.TestEntityRepository;
import com.codingapi.springboot.framework.handler.IHandler;
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

        throw new IllegalArgumentException("123");

    }


}
