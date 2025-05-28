package com.codingapi.springboot.flow.test;

import com.codingapi.springboot.flow.build.FlowWorkBuilder;
import com.codingapi.springboot.flow.domain.FlowNode;
import com.codingapi.springboot.flow.domain.FlowWork;
import com.codingapi.springboot.flow.user.User;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ScriptBuildTest {


    @Test
    void copy() {
        User user = new User("张三");
        String script = "{\"nodes\":[{\"id\":\"b82a84e7-2c1d-4e15-a3c5-6f7f6e263acd\",\"type\":\"start-node\",\"x\":593,\"y\":96,\"properties\":{\"name\":\"开始节点\",\"code\":\"start\",\"type\":\"START\",\"view\":\"default\",\"operatorMatcher\":\"def run(content) {return [content.getCurrentOperator().getUserId()];}\",\"editable\":true,\"mergeable\":true,\"titleGenerator\":\"def run(content){ return content.getCurrentOperator().getName() + '-' + content.getFlowWork().getTitle() + '-' + content.getFlowNode().getName();}\",\"errTrigger\":\"\",\"approvalType\":\"UN_SIGN\",\"timeout\":0,\"id\":\"b82a84e7-2c1d-4e15-a3c5-6f7f6e263acd\",\"width\":200,\"height\":45,\"operatorMatcherType\":\"any\",\"titleGeneratorType\":\"default\",\"errTriggerType\":\"custom\"}},{\"id\":\"3c2c420a-003b-4f51-9489-3cdcda0bbe35\",\"type\":\"node-node\",\"x\":620,\"y\":239,\"properties\":{\"name\":\"流程节点\",\"code\":\"flow\",\"type\":\"APPROVAL\",\"view\":\"default\",\"operatorMatcher\":\"def run(content) {return [content.getCurrentOperator().getUserId()];}\",\"editable\":true,\"mergeable\":true,\"titleGenerator\":\"def run(content){ return content.getCurrentOperator().getName() + '8899-' + content.getFlowWork().getTitle() + '-' + content.getFlowNode().getName();}\",\"errTrigger\":\"\",\"approvalType\":\"SIGN\",\"timeout\":10,\"id\":\"3c2c420a-003b-4f51-9489-3cdcda0bbe35\",\"width\":200,\"height\":45,\"operatorMatcherType\":\"any\",\"titleGeneratorType\":\"custom\",\"errTriggerType\":\"custom\"}},{\"id\":\"b527b4a5-f11f-4052-9848-2c0426da970c\",\"type\":\"over-node\",\"x\":828,\"y\":582,\"properties\":{\"name\":\"结束节点\",\"code\":\"over\",\"type\":\"OVER\",\"view\":\"default\",\"operatorMatcher\":\"def run(content) {return [content.getCurrentOperator().getUserId()];}\",\"editable\":true,\"mergeable\":true,\"titleGenerator\":\"def run(content){ return content.getCurrentOperator().getName() + '-' + content.getFlowWork().getTitle() + '-' + content.getFlowNode().getName();}\",\"errTrigger\":\"\",\"approvalType\":\"UN_SIGN\",\"timeout\":0,\"id\":\"b527b4a5-f11f-4052-9848-2c0426da970c\",\"width\":200,\"height\":45,\"operatorMatcherType\":\"any\",\"titleGeneratorType\":\"default\",\"errTriggerType\":\"custom\"}},{\"id\":\"2ecdb8aa-00b2-42af-b3ed-c776d2431b38\",\"type\":\"circulate-node\",\"x\":839,\"y\":409,\"properties\":{\"name\":\"抄送节点\",\"code\":\"circulate\",\"type\":\"CIRCULATE\",\"view\":\"default\",\"operatorMatcher\":\"def run(content) {return [content.getCreateOperator().getUserId()];}\",\"editable\":true,\"mergeable\":true,\"titleGenerator\":\"def run(content){ return content.getCurrentOperator().getName() + '-' + content.getFlowWork().getTitle() + '-' + content.getFlowNode().getName();}\",\"errTrigger\":\"\",\"approvalType\":\"CIRCULATE\",\"timeout\":0,\"id\":\"2ecdb8aa-00b2-42af-b3ed-c776d2431b38\",\"width\":200,\"height\":45}}],\"edges\":[{\"id\":\"b68837fb-dca8-41d2-908c-dc079a7f61de\",\"type\":\"bezier\",\"properties\":{\"outTrigger\":\"def run(content) {return true;}\",\"order\":1,\"back\":false},\"sourceNodeId\":\"b82a84e7-2c1d-4e15-a3c5-6f7f6e263acd\",\"targetNodeId\":\"3c2c420a-003b-4f51-9489-3cdcda0bbe35\",\"startPoint\":{\"x\":593,\"y\":118.5},\"endPoint\":{\"x\":620,\"y\":216.5},\"pointsList\":[{\"x\":593,\"y\":118.5},{\"x\":593,\"y\":218.5},{\"x\":620,\"y\":116.5},{\"x\":620,\"y\":216.5}]},{\"id\":\"73e04b95-50f6-44cc-a960-d3007d27fd48\",\"type\":\"bezier\",\"properties\":{\"outTrigger\":\"def run(content) {return true;}\",\"order\":2,\"back\":false},\"sourceNodeId\":\"3c2c420a-003b-4f51-9489-3cdcda0bbe35\",\"targetNodeId\":\"2ecdb8aa-00b2-42af-b3ed-c776d2431b38\",\"startPoint\":{\"x\":720,\"y\":239},\"endPoint\":{\"x\":739,\"y\":409},\"pointsList\":[{\"x\":720,\"y\":239},{\"x\":820,\"y\":239},{\"x\":639,\"y\":409},{\"x\":739,\"y\":409}]},{\"id\":\"f6929c79-b168-4c3c-9f8f-9dc21fcaf29d\",\"type\":\"bezier\",\"properties\":{\"outTrigger\":\"def run(content) {return true;}\",\"order\":1,\"back\":false},\"sourceNodeId\":\"2ecdb8aa-00b2-42af-b3ed-c776d2431b38\",\"targetNodeId\":\"b527b4a5-f11f-4052-9848-2c0426da970c\",\"startPoint\":{\"x\":839,\"y\":431.5},\"endPoint\":{\"x\":828,\"y\":559.5},\"pointsList\":[{\"x\":839,\"y\":431.5},{\"x\":839,\"y\":531.5},{\"x\":828,\"y\":459.5},{\"x\":828,\"y\":559.5}]}]}";
        FlowWork flowWork = FlowWorkBuilder.builder(user)
                .title("请假流程")
                .schema(script)
                .build();
        assertEquals("请假流程", flowWork.getTitle());
        assertEquals(4, flowWork.getNodes().size());
        assertEquals(3, flowWork.getRelations().size());


        FlowNode startNode = flowWork.getStartNode();

        FlowWork copyWork = flowWork.copy();
        assertNotEquals(copyWork.getCode(), flowWork.getCode());

        assertEquals("请假流程", copyWork.getTitle());
        assertEquals(4, copyWork.getNodes().size());
        assertEquals(3, copyWork.getRelations().size());

        copyWork.verify();

        FlowNode startNode2 = copyWork.getNodeByCode("start");
        assertNotEquals(startNode.getId(), startNode2.getId());


    }
}
