package com.codingapi.springboot.fast.strategy;

import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.generator.BeforeExecutionGenerator;
import org.hibernate.generator.EventType;

import java.util.EnumSet;
import java.util.Random;

import static org.hibernate.generator.EventTypeSets.INSERT_ONLY;

public class MyIdGenerator implements BeforeExecutionGenerator {

    private final Random random = new Random();

    @Override
    public Object generate(SharedSessionContractImplementor session, Object owner, Object currentValue, EventType eventType) {
        int id = random.nextInt(1000);
        System.out.println("id:"+id);
        return id;
    }

    @Override
    public EnumSet<EventType> getEventTypes() {
        return INSERT_ONLY;
    }
}
