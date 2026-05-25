package com.codingapi.springboot.framework.script;

import com.codingapi.springboot.framework.script.request.GroovyRunningScript;
import com.codingapi.springboot.framework.script.request.MyScriptRequest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GroovyScriptRunningContextTest {

    @Test
    void voidRun() {

        String script = """
                def run(request){
                    println($request.getRequests())
                }
                """;
        GroovyScriptRunningContext.getInstance().compile(script);

        GroovyRunningScript<Void> request = new GroovyRunningScript<>(script, Void.class, 100);
        request.addBindObject("$request", request);

        long t1 = System.currentTimeMillis();
        GroovyScriptRunningContext.getInstance().run(request);
        long t2 = System.currentTimeMillis();
        System.out.println("groovy time:" + (t2 - t1));

    }


    @Test
    void metaTest(){
        String script = """
                def run(request){
                    return request.count;
                }
                """;

        MyScriptRequest request = new MyScriptRequest(100);
        GroovyRunningScript<Integer> runningScript = new GroovyRunningScript<>(script, Integer.class, request);

        assertEquals(1,runningScript.getGroovyMetadata().getRequests().size());

        long t1 = System.currentTimeMillis();
        int result = GroovyScriptRunningContext.getInstance().run(runningScript);
        long t2 = System.currentTimeMillis();
        System.out.println("groovy time:" + (t2 - t1));
        assertEquals(100, result);
    }

    @Test
    void run() {

        String script = """
                def run(request){
                    println($request.getRequests())
                    return request;
                }
                """;

        GroovyRunningScript<Integer> request = new GroovyRunningScript<>(script, Integer.class, 100);
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