package com.codingapi.springboot.script.request;

import com.codingapi.springboot.script.parser.GroovyScriptParser;
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

        GroovyScriptParser parser = new GroovyScriptParser(workflow);
        List<String> keys = parser.parser();
        assertEquals(1, keys.size());
        assertEquals("K123123",keys.get(0));
    }

}