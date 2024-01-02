package com.codingapi.springboot.fast.script;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class ScriptRuntimeTest {

    @Test
    void running() {
        Object res = ScriptRuntime.running("return 1");
        assertEquals(1, res);
    }
}