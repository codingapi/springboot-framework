package com.codingapi.springboot.framework;

import com.codingapi.springboot.framework.repository.MyTestRepository;
import com.codingapi.springboot.framework.service.MyTestService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class FrameworkApplicationEventTests {

    @Autowired
    private MyTestService myTestService;
    @Autowired
    private MyTestRepository repository;

    @Test
    void transactionTest1()  {
        repository.deleteAll();
        assertThrows(ArithmeticException.class,()->{
            myTestService.save1();
        });
        assertEquals(0, repository.findAll().size());
    }

    @Test
    void transactionTest2()  {
        repository.deleteAll();
        myTestService.save2();
        assertEquals(1, repository.findAll().size());
    }

}
