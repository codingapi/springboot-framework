package com.codingapi.springboot.script;

import com.codingapi.springboot.script.entity.MyTest;
import com.codingapi.springboot.script.repository.MyTestRepository;
import com.codingapi.springboot.script.request.MyScriptRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Transactional
class TransactionGroovyScriptRuntimeContextTest {

    @Autowired
    private MyTestRepository myTestRepository;

    @Test
    void transactionCommitRun() {

        String script = """
                def run(request){
                    request.addData($repository);
                    println(request.count);
                }
                """;

        MyScriptRequest request = new MyScriptRequest(100);

        myTestRepository.deleteAll();
        List<MyTest> list = myTestRepository.findAll();
        assertTrue(list.isEmpty());

        GroovyScript groovyScript =
                GroovyScript.builder("transactionCommitRun")
                        .script(script)
                        .method("run")
                        .returnType(Void.class)
                        .binds(Map.of("$repository", myTestRepository.getClass()))
                        .requests(Map.of("request", request.getClass()))
                        .build();

        long t1 = System.currentTimeMillis();
        groovyScript.invoke(TransactionMode.COMMIT, Map.of("$repository", myTestRepository), request);
        long t2 = System.currentTimeMillis();
        System.out.println("groovy time:" + (t2 - t1));

        list = myTestRepository.findAll();
        assertFalse(list.isEmpty());

    }

    @Test
    void transactionOnlyReadRun() {

        String script = """
                def run(request){
                    request.addData($repository);
                    println(request.count);
                }
                """;

        MyScriptRequest request = new MyScriptRequest(100);


        myTestRepository.deleteAll();
        List<MyTest> list = myTestRepository.findAll();
        assertTrue(list.isEmpty());

        GroovyScript groovyScript =
                GroovyScript.builder("transactionOnlyReadRun")
                        .script(script)
                        .method("run")
                        .returnType(Void.class)
                        .binds(Map.of("$repository", myTestRepository.getClass()))
                        .requests(Map.of("request", request.getClass()))
                        .build();


        long t1 = System.currentTimeMillis();
        groovyScript.invoke(TransactionMode.READONLY, Map.of("$repository", myTestRepository), request);
        long t2 = System.currentTimeMillis();
        System.out.println("groovy time:" + (t2 - t1));

        list = myTestRepository.findAll();
        System.out.println(list.size());
        assertTrue(list.isEmpty());

    }

}