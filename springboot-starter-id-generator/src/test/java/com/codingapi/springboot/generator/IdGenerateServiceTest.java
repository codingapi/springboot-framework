package com.codingapi.springboot.generator;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
@SpringBootTest
class IdGenerateServiceTest {

    @Test
    void generateId() {
        for (int i = 0; i < 1000; i++) {
            long id = IdGeneratorContext.getInstance().generateId(IdGenerateServiceTest.class);
            log.info("id=>{}", id);
            assertTrue(id > 0, "id generator error.");
        }
    }
}