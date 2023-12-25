package com.example.demo.repository;

import com.codingapi.springboot.persistence.DomainPersistence;
import com.example.demo.domain.Demo;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
@AllArgsConstructor
public class DemoRepository {

    private final DomainPersistence domainPersistence;

    public void save(Demo demo) {
        domainPersistence.save(demo);
    }

    public Demo get(int id) {
        return domainPersistence.get(Demo.class, id);
    }

}
