package com.codingapi.springboot.framework.domain;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class EntityProxyFactoryTest {

    @Test
    void createEntity() {
        Demo demo = EntityProxyFactory.createEntity(Demo.class, "test");
        System.out.println(demo);
        demo.changeName("123");
    }
}