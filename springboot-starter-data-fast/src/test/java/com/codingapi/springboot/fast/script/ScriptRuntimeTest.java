package com.codingapi.springboot.fast.script;

import com.codingapi.springboot.fast.mapping.MvcRunningContext;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ScriptRuntimeTest {

    @Test
    void running() {
        Object res = ScriptRuntime.running("return 1", MvcRunningContext.getInstance());
        assertEquals(1,res);
    }
}