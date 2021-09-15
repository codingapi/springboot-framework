package com.codingapi.springboot.permission;

import com.codingapi.springboot.permission.entity.Demo;
import com.codingapi.springboot.permission.repository.DemoRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.Assert;

@Slf4j
@SpringBootTest
public class DemoRepositoryTest {

    @Autowired
    private DemoRepository demoRepository;

    @Test
    void save() {
        Demo demo = new Demo("xiaoming");
        demoRepository.save(demo);
        Assert.isTrue(demo.getId()>0,"demoRepository save error.");
    }

    @Test
    void changeJdbcTest(){
        demoRepository.findById(1).ifPresent(demo -> {
            log.info("demo:{}",demo);
            Assert.isTrue(demo.getUserId()==1,"update user_id error.");
        });
    }

}
