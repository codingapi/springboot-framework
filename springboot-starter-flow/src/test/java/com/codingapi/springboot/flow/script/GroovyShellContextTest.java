package com.codingapi.springboot.flow.script;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GroovyShellContextTest {

    @Test
    void getInstance() {
        long t1 = System.currentTimeMillis();
        int count = 12000;
        GroovyShellContext.ShellScript[] scripts = new GroovyShellContext.ShellScript[count];
        for (int i = 0; i < count; i++) {
            scripts[i] =  GroovyShellContext.getInstance().parse("def run(content){ return '" + i + "';}");
        }

        long t2 = System.currentTimeMillis();
        System.out.println("t2 time :" + (t2 - t1));
        System.out.println("size:" + GroovyShellContext.getInstance().size());

        for (int i = 0; i < count; i++) {
            assertEquals(scripts[i].invokeMethod("run", i), String.valueOf(i));
        }

        long t3 = System.currentTimeMillis();
        System.out.println("t3 time :" + (t3 - t2));

    }
}
