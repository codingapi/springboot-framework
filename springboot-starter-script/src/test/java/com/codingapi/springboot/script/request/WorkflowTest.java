package com.codingapi.springboot.script.request;

import com.codingapi.springboot.script.scanner.GroovyScriptAnnotationScannerUtils;
import com.codingapi.springboot.script.scanner.GroovyScriptFieldResult;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class WorkflowTest {


    @Test
    void test() {

        Workflow workflow = new Workflow();
        workflow.setName("测试");
        Node node = new Node();
        node.setId("1");
        node.setScript("K123123");
        workflow.addNode(node);

        List<String> keys = GroovyScriptAnnotationScannerUtils.findGroovyScriptFields(workflow).getKeys();
        assertEquals(1, keys.size());
        assertEquals("K123123", keys.get(0));

        GroovyScriptFieldResult result = GroovyScriptAnnotationScannerUtils.findGroovyScriptFields(workflow);

        result.update((value)->{
            return "K123456";
        });

        keys = GroovyScriptAnnotationScannerUtils.findGroovyScriptFields(workflow).getKeys();
        assertEquals(1, keys.size());
        assertEquals("K123456", keys.get(0));

    }

}