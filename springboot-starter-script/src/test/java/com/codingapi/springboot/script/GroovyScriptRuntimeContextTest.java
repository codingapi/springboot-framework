package com.codingapi.springboot.script;

import com.alibaba.fastjson.JSON;
import com.codingapi.springboot.script.meta.GroovyMetadata;
import com.codingapi.springboot.script.request.MyScriptRequest;
import org.apache.groovy.util.Maps;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GroovyScriptRuntimeContextTest {

    @Test
    void voidInvoke() {

        String script = " def run(request){ println(request)}";

        GroovyScript groovyScript = GroovyScript.createInvoke("voidInvoke", script, "run", Void.class, Maps.of("request", Integer.class));
        long t1 = System.currentTimeMillis();
        groovyScript.invoke(100);
        long t2 = System.currentTimeMillis();
        System.out.println("groovy time:" + (t2 - t1));

    }


    @Test
    void metaTest() {
        String script = "def run(request){return request.count;}";

        MyScriptRequest request = new MyScriptRequest(100);

        GroovyScript groovyScript = GroovyScript.createInvoke("metaTest",
                script, "这是一个run函数，返回的格式为int类型。","run",  Integer.class, Maps.of("request", MyScriptRequest.class));

        GroovyMetadata metadata = groovyScript.toMetadata();
        System.out.println(JSON.toJSONString(metadata));
        assertEquals(1, metadata.getRequests().size());

        long t1 = System.currentTimeMillis();
        int result = groovyScript.invoke(request);
        long t2 = System.currentTimeMillis();
        System.out.println("groovy time:" + (t2 - t1));
        assertEquals(100, result);
    }

    @Test
    void run() {

        String script = " return $request; ";


        GroovyScript groovyScript = GroovyScript.createRun("run",
                script, Integer.class, Maps.of("$request", Integer.class));


        long t1 = System.currentTimeMillis();
        int result = groovyScript.run(Maps.of("$request",100));
        long t2 = System.currentTimeMillis();
        System.out.println("groovy time:" + (t2 - t1));

        assertEquals(100, result);
    }

    @Test
    void invoke() {

        String script = "def run(request){return request;}";

        GroovyScript groovyScript = GroovyScript.createInvoke("invoke",
                script, "run", Integer.class, Maps.of("request", Integer.class));

        long t1 = System.currentTimeMillis();
        int result = groovyScript.invoke(100);
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