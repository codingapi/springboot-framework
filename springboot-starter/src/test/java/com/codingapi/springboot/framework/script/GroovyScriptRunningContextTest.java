package com.codingapi.springboot.framework.script;

import com.codingapi.springboot.framework.script.request.GroovyMethodScript;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GroovyScriptRunningContextTest {

    @Test
    void voidRun() {

        String script = " def run(request){ println($request.getRequest())} ";

        GroovyScriptRunningContext.getInstance().compile(script);

        GroovyMethodScript<Void> request = new GroovyMethodScript<>(script, Void.class, 100);
        request.addBindObject("$request", request);

        long t1 = System.currentTimeMillis();
        GroovyScriptRunningContext.getInstance().run(request);
        long t2 = System.currentTimeMillis();
        System.out.println("groovy time:" + (t2 - t1));

    }

    @Test
    void run() {

        String script = " def run(request){ println($request.getRequest()) return request;} ";

        GroovyMethodScript<Integer> request = new GroovyMethodScript<>(script, Integer.class, 100);
        request.addBindObject("$request", request);

        long t1 = System.currentTimeMillis();
        int result = GroovyScriptRunningContext.getInstance().run(request);
        long t2 = System.currentTimeMillis();
        System.out.println("groovy time:" + (t2 - t1));

        assertEquals(100, result);
    }

    @Test
    void batchTest() {
        int count = 100;
        run();
        long t1 = System.currentTimeMillis();
        for (int i = 0; i < count; i++) {
            run();
        }
        long t2 = System.currentTimeMillis();
        System.out.println("total time:" + (t2 - t1));
    }
}