package com.codingapi.springboot.fast;

import com.codingapi.springboot.fast.repository.TestRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@Slf4j
@SpringBootTest
public class TestRepositoryTest {

    @Autowired
    private TestRepository testRepository;

    @Test
    void test1() {
        com.codingapi.springboot.fast.entity.Test demo = new com.codingapi.springboot.fast.entity.Test();
        demo.setName("123");
        testRepository.save(demo);
    }



}
