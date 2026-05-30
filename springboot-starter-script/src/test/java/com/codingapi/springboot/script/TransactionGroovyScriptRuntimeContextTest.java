package com.codingapi.springboot.script;

import com.codingapi.springboot.script.entity.MyTest;
import com.codingapi.springboot.script.repository.MyTestRepository;
import com.codingapi.springboot.script.request.MyScriptRequest;
import com.codingapi.springboot.script.strategy.GroovyMetadataGenerateStrategyContext;
import com.codingapi.springboot.script.strategy.GroovyTypeFixStrategyContext;
import com.codingapi.springboot.script.strategy.ScriptTypeMappingContext;
import org.apache.groovy.util.Maps;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Transactional
class TransactionGroovyScriptRuntimeContextTest {

    @Autowired
    private MyTestRepository myTestRepository;

    @BeforeEach
    void beforeRun(){
        GroovyMetadataGenerateStrategyContext.getInstance().clear();
        GroovyTypeFixStrategyContext.getInstance().clear();
        ScriptTypeMappingContext.getInstance().clear();
    }

    @Test
    void transactionCommitRun() {

        String script = " def run(request){  request.addData($repository);  println(request.count); } ";

        MyScriptRequest request = new MyScriptRequest(100);

        myTestRepository.deleteAll();
        List<MyTest> list = myTestRepository.findAll();
        assertTrue(list.isEmpty());

        GroovyScript groovyScript =
                GroovyScript.builder("transactionCommitRun")
                        .script(script)
                        .method("run")
                        .returnType(Void.class)
                        .binds(Maps.of("$repository", myTestRepository.getClass()))
                        .requests(Maps.of("request", request.getClass()))
                        .build();

        long t1 = System.currentTimeMillis();
        groovyScript.invoke(TransactionMode.COMMIT, Maps.of("$repository", myTestRepository), request);
        long t2 = System.currentTimeMillis();
        System.out.println("groovy time:" + (t2 - t1));

        list = myTestRepository.findAll();
        assertFalse(list.isEmpty());

    }

    @Test
    void transactionOnlyReadRun() {

        String script = " def run(request){  request.addData($repository);  println(request.count); } ";

        MyScriptRequest request = new MyScriptRequest(100);


        myTestRepository.deleteAll();
        List<MyTest> list = myTestRepository.findAll();
        assertTrue(list.isEmpty());

        GroovyScript groovyScript =
                GroovyScript.builder("transactionOnlyReadRun")
                        .script(script)
                        .method("run")
                        .returnType(Void.class)
                        .binds(Maps.of("$repository", myTestRepository.getClass()))
                        .requests(Maps.of("request", request.getClass()))
                        .build();


        long t1 = System.currentTimeMillis();
        groovyScript.invoke(TransactionMode.READONLY, Maps.of("$repository", myTestRepository), request);
        long t2 = System.currentTimeMillis();
        System.out.println("groovy time:" + (t2 - t1));

        list = myTestRepository.findAll();
        System.out.println(list.size());
        assertTrue(list.isEmpty());

    }

}