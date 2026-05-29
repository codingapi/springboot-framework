package com.codingapi.springboot.script;

import com.alibaba.fastjson.JSON;
import com.codingapi.springboot.script.meta.GroovyMetadata;
import com.codingapi.springboot.script.request.MyScriptRequest;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GroovyScriptRuntimeContextTest {

    @Test
    void voidInvoke() {

        String script = """
                def run(request){
                    println(request)
                }
                """;

        GroovyScript groovyScript =
                GroovyScript.builder("voidInvoke")
                        .script(script)
                        .method("run")
                        .returnType(Void.class)
                        .requests(Map.of("request", Integer.class))
                        .build();

        long t1 = System.currentTimeMillis();
        groovyScript.invoke(Map.of("request", 100));
        long t2 = System.currentTimeMillis();
        System.out.println("groovy time:" + (t2 - t1));

    }


    @Test
    void metaTest() {
        String script = """
                
                def run(request){
                    return request.count;
                }
                """;



        GroovyScript groovyScript =
                GroovyScript.builder("metaTest")
                        .script(script)
                        .description("这是一个run函数，返回的格式为int类型。")
                        .method("run")
                        .tag("123")
                        .returnType(Integer.class)
                        .requests(Map.of("request", MyScriptRequest.class))
                        .build();

        GroovyMetadata metadata = groovyScript.toMetadata();
        System.out.println(JSON.toJSONString(metadata));
        assertEquals(1, metadata.getRequests().size());

        MyScriptRequest request = new MyScriptRequest(100);
        long t1 = System.currentTimeMillis();
        int result = groovyScript.invoke(request);
        long t2 = System.currentTimeMillis();
        System.out.println("groovy time:" + (t2 - t1));
        assertEquals(100, result);
    }

    @Test
    void run() {

        String script = " return $request; ";

        GroovyScript groovyScript =
                GroovyScript.builder("run")
                        .script(script)
                        .returnType(Integer.class)
                        .binds(Map.of("$request", Integer.class))
                        .build();

        long t1 = System.currentTimeMillis();
        int result = groovyScript.run(Map.of("$request", 100));
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
        GroovyScript groovyScript =
                GroovyScript.builder("invoke")
                        .method("run")
                        .script(script)
                        .returnType(Integer.class)
                        .binds(Map.of("request", Integer.class))
                        .build();

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