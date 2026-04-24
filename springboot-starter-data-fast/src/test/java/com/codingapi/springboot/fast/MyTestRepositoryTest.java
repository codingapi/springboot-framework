package com.codingapi.springboot.fast;

import com.codingapi.springboot.fast.entity.MyTest;
import com.codingapi.springboot.fast.repository.MyTestRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@Slf4j
@SpringBootTest
public class MyTestRepositoryTest {

    @Autowired
    private MyTestRepository myTestRepository;

    @Test
    void test() {
        MyTest demo = new MyTest();
        demo.setName("123");
        myTestRepository.save(demo);
    }



}
