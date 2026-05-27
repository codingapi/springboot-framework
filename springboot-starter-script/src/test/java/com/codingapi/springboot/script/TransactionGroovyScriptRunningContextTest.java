package com.codingapi.springboot.script;

import com.codingapi.springboot.script.entity.MyTest;
import com.codingapi.springboot.script.repository.MyTestRepository;
import com.codingapi.springboot.script.request.GroovyBindObjectBuilder;
import com.codingapi.springboot.script.request.GroovyRunningScript;
import com.codingapi.springboot.script.request.MyScriptRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Transactional
class TransactionGroovyScriptRunningContextTest {

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
        GroovyScriptRunner scriptRunner = new GroovyScriptRunner(100,GroovyScriptRunner.TransactionMode.COMMIT);
        scriptRunner.compile(script);
        GroovyRunningScript<Void> runningScript = new GroovyRunningScript<>(script,
                Void.class,
                GroovyBindObjectBuilder.builder().add("$repository",myTestRepository),
                GroovyBindObjectBuilder.builder().add("request",request)
        );

        long t1 = System.currentTimeMillis();
        scriptRunner.invoke(runningScript);
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

        GroovyScriptRunner scriptRunner = new GroovyScriptRunner(100, GroovyScriptRunner.TransactionMode.READONLY);
        scriptRunner.compile(script);

        GroovyRunningScript<Void> runningScript = new GroovyRunningScript<>(script,
                Void.class,
                GroovyBindObjectBuilder.builder().add("$repository",myTestRepository),
                GroovyBindObjectBuilder.builder().add("request",request)
        );

        long t1 = System.currentTimeMillis();
        scriptRunner.invoke(runningScript);
        long t2 = System.currentTimeMillis();
        System.out.println("groovy time:" + (t2 - t1));

        list = myTestRepository.findAll();
        System.out.println(list.size());
        assertTrue(list.isEmpty());

    }

}