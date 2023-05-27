package com.codingapi.springboot.framework.domain;

import com.codingapi.springboot.framework.domain.proxy.DomainProxyFactory;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class DomainProxyFactoryTest {

    @Test
    void createEntity() {
        Demo demo = DomainProxyFactory.create(Demo.class, "test");
        demo.changeAnimalName("123");
        demo.changeAnimalName("234");
        demo.changeName("test");
        demo.changeName("test123");
        System.out.println(demo);
    }
}