package com.example.demo.repository;

import com.codingapi.springboot.persistence.DomainPersistence;
import com.example.demo.domain.Demo;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;

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

    public void delete(int id) {
        domainPersistence.delete(Demo.class, id);
    }

    public void update(Demo demo) {
        domainPersistence.update(demo);
    }

    public List<Demo> findByName(String name) {
        return domainPersistence.find(Demo.class, "select * from demo where name = ?", name);
    }

}
