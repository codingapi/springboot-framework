package com.example.demo.repository;

import com.example.demo.domain.Demo;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@Slf4j
@SpringBootTest
public class DemoRepositoryTests {


    @Autowired
    private DemoRepository demoRepository;

    @Test
    void save1() {
        Demo demo = new Demo();
        demo.setName("demo");
        demoRepository.save(demo);
        System.out.println(demo.getId());
    }

    @Test
    void save2() {
        Demo demo = new Demo();
        demo.setName("demo");
        demo.setId(100);
        demoRepository.save(demo);
        System.out.println(demo.getId());
    }


    @Test
    void get() {
        Demo demo =  demoRepository.get(100);
        log.info("demo: {}", demo);
    }

    @Test
    void delete() {
        demoRepository.delete(100);
    }


    @Test
    void update() {
        Demo demo = new Demo();
        demo.setId(100);
        demo.setName("123456");
        demoRepository.update(demo);
    }

    @Test
    void getByName() {
        List<Demo> list = demoRepository.findByName("demo");
        log.info("list: {}", list);
    }
}