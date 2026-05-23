package com.codingapi.springboot.flow.script;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GroovyShellContextTest {

    @Test
    void getInstance() {
        long t1 = System.currentTimeMillis();
        int count = 12000;
        for (int i = 0; i < count; i++) {
            String result  =  GroovyShellContext.getInstance().run("def run(content){ return '" + i + "';}",String.class,i);
            assertEquals(result, String.valueOf(i));
        }

        long t2 = System.currentTimeMillis();
        System.out.println("time :" + (t2 - t1));
        System.out.println("size:" + GroovyShellContext.getInstance().size());

    }
}
