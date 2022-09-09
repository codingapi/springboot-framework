package com.codingapi.springboot.example;

import com.codingapi.springboot.example.application.executor.DemoExecutor;
import com.codingapi.springboot.example.domain.entity.Demo;
import com.codingapi.springboot.example.domain.repository.DemoRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

@SpringBootTest
@Slf4j
@Rollback(value = false)
class ExampleApplicationTests {

    @Autowired
    private DemoRepository demoRepository;

    @Autowired
    private DemoExecutor demoExecutor;

    @Test
    @Transactional
    void save() {
        Demo demo = new Demo("xiaoming");
        demoRepository.save(demo);

        Assert.isTrue(demo.getId() > 0, "demoRepository save error.");
        log.info("id:{}", demo.getId());
    }

    @Test
    void swap() {
        Demo demo1 = new Demo("demo1");
        Demo demo2 = new Demo("demo2");
        demoExecutor.swap(demo1, demo2);
        Assert.isTrue(demo1.getName().equals("demo2"), "swap service error.");
        Assert.isTrue(demo2.getName().equals("demo1"), "swap service error.");
    }


}
