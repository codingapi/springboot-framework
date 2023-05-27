package com.codingapi.springboot.framework.domain;

import com.codingapi.springboot.framework.domain.field.FieldProxyFactory;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class FieldProxyFactoryTest {

    @Test
    void createEntity() {
        Demo demo = FieldProxyFactory.create(Demo.class, "test");
        demo.changeAinimalName("123");
        demo.changeAinimalName("234");
        demo.changeName("test");
        demo.changeName("test123");
    }
}