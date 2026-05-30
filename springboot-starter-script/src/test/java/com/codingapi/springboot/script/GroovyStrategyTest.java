package com.codingapi.springboot.script;

import com.alibaba.fastjson.JSON;
import com.codingapi.springboot.script.meta.GroovyFunction;
import com.codingapi.springboot.script.meta.GroovyMetadata;
import com.codingapi.springboot.script.meta.GroovyType;
import com.codingapi.springboot.script.request.MyScriptRequest;
import com.codingapi.springboot.script.strategy.*;
import org.apache.groovy.util.Maps;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class GroovyStrategyTest {


    @BeforeEach
    void beforeRun(){
        GroovyMetadataGenerateStrategyContext.getInstance().clear();
        GroovyTypeFixStrategyContext.getInstance().clear();
        ScriptTypeMappingContext.getInstance().clear();
    }

    @Test
    void test1(){
        String script = "def run(request){return request.count;}";

        ScriptTypeMappingContext.getInstance().addMapping(new ScriptTypeMapping() {
            @Override
            public boolean support(Class<?> target) {
                return Integer.class.equals(target);
            }

            @Override
            public Class<?> mapping(Class<?> target) {
                return int.class;
            }
        });


        GroovyTypeFixStrategyContext.getInstance().addFixStrategy(new GroovyTypeFixStrategy() {
            @Override
            public boolean support(Class<?> clazz) {
                return MyScriptRequest.class.equals(clazz);
            }

            @Override
            public void fix(GroovyScript groovyScript, GroovyType groovyType) {
                GroovyFunction groovyFunction = new GroovyFunction();
                groovyFunction.setReturnType("int");
                groovyFunction.setName("hi");
                groovyFunction.setDescription("测试");
                groovyType.addFunction(groovyFunction);
            }
        });


        GroovyScript groovyScript =
                GroovyScript.builder("metaTest")
                        .script(script)
                        .description("这是一个run函数，返回的格式为int类型。")
                        .method("run")
                        .tag("123")
                        .returnType(Integer.class)
                        .requests(Maps.of("request", MyScriptRequest.class))
                        .build();

        GroovyMetadata metadata = groovyScript.toMetadata();
        System.out.println(JSON.toJSONString(metadata));
        assertEquals(1, metadata.getRequests().size());
        assertEquals("int",metadata.getReturnType());
        assertEquals(3,metadata.getType("MyScriptRequest").getFunctions().size());

        MyScriptRequest request = new MyScriptRequest(100);
        long t1 = System.currentTimeMillis();
        int result = groovyScript.invoke(request);
        long t2 = System.currentTimeMillis();
        System.out.println("groovy time:" + (t2 - t1));
        assertEquals(100, result);
    }



    @Test
    void test2(){
        String script = "def run(request){return request.count;}";

        GroovyMetadataGenerateStrategyContext.getInstance().addGenerateStrategy(new GroovyMetadataGenerateStrategy() {
            @Override
            public boolean support(GroovyScript script) {
                return true;
            }

            @Override
            public GroovyMetadata generate(GroovyScript script) {
                return new GroovyMetadata("hello","int","hello");
            }
        });


        GroovyScript groovyScript =
                GroovyScript.builder("metaTest")
                        .script(script)
                        .description("这是一个run函数，返回的格式为int类型。")
                        .method("run")
                        .tag("123")
                        .returnType(Integer.class)
                        .requests(Maps.of("request", MyScriptRequest.class))
                        .build();

        GroovyMetadata metadata = groovyScript.toMetadata();
        System.out.println(JSON.toJSONString(metadata));
        assertEquals("int",metadata.getReturnType());
        assertEquals("hello",metadata.getDescription());
        assertEquals("hello",metadata.getMainMethod());

        MyScriptRequest request = new MyScriptRequest(100);
        long t1 = System.currentTimeMillis();
        int result = groovyScript.invoke(request);
        long t2 = System.currentTimeMillis();
        System.out.println("groovy time:" + (t2 - t1));
        assertEquals(100, result);
    }
}
