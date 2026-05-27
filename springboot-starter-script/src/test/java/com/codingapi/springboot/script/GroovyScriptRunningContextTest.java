package com.codingapi.springboot.script;

import com.alibaba.fastjson.JSON;
import com.codingapi.springboot.script.request.GroovyBindObjectBuilder;
import com.codingapi.springboot.script.request.GroovyRunningScript;
import com.codingapi.springboot.script.request.MyScriptRequest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GroovyScriptRunningContextTest {

    @Test
    void voidInvoke() {

        String script = """
                def run(request){
                    println(request)
                }
                """;
        GroovyScriptRunningContext.getInstance().compile(script);

        GroovyRunningScript<Void> request = new GroovyRunningScript<>(
                script,
                Void.class,
                GroovyBindObjectBuilder.builder().add("request", 100)
        );

        long t1 = System.currentTimeMillis();
        GroovyScriptRunningContext.getInstance().invoke(request);
        long t2 = System.currentTimeMillis();
        System.out.println("groovy time:" + (t2 - t1));

    }


    @Test
    void metaTest() {
        String script = """
                
                def test(request){
                    return request.get('count')
                }
                
                def run(request){
                    return test(request);
                }
                """;

        MyScriptRequest request = new MyScriptRequest(100);

        GroovyRunningScript<Integer> runningScript = new GroovyRunningScript<>(
                script,
                "这是一个run函数，返回的格式为int类型。",
                Integer.class,
                GroovyBindObjectBuilder.builder().add("$request", request),
                GroovyBindObjectBuilder.builder().add("request", request)
        );

        runningScript.buildMetadata();
        System.out.println(JSON.toJSONString(runningScript.getMetadata()));
        assertEquals(1, runningScript.getMetadata().getRequests().size());

        long t1 = System.currentTimeMillis();
        int result = GroovyScriptRunningContext.getInstance().invoke(runningScript);
        long t2 = System.currentTimeMillis();
        System.out.println("groovy time:" + (t2 - t1));
        assertEquals(100, result);
    }

    @Test
    void run() {

        String script = " return $request; ";

        GroovyRunningScript<Integer> request = new GroovyRunningScript<>(
                script,
                Integer.class,
                GroovyBindObjectBuilder.builder().add("$request", 100),
                null
        );

        long t1 = System.currentTimeMillis();
        int result = GroovyScriptRunningContext.getInstance().invoke(request);
        long t2 = System.currentTimeMillis();
        System.out.println("groovy time:" + (t2 - t1));

        assertEquals(100, result);
    }

    @Test
    void invoke() {

        String script = """
                def run(request){
                    return request;
                }
                """;

        GroovyRunningScript<Integer> request = new GroovyRunningScript<>(script,
                Integer.class,
                GroovyBindObjectBuilder.builder().add("request", 100)
        );

        long t1 = System.currentTimeMillis();
        int result = GroovyScriptRunningContext.getInstance().invoke(request);
        long t2 = System.currentTimeMillis();
        System.out.println("groovy time:" + (t2 - t1));

        assertEquals(100, result);
    }

    @Test
    void batchInvoke() {
        int count = 100;
        invoke();
        long t1 = System.currentTimeMillis();
        for (int i = 0; i < count; i++) {
            invoke();
        }
        long t2 = System.currentTimeMillis();
        System.out.println("total time:" + (t2 - t1));
    }
}