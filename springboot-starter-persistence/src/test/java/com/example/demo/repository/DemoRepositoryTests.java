package com.example.demo.repository;

import com.example.demo.domain.Demo;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

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

}
