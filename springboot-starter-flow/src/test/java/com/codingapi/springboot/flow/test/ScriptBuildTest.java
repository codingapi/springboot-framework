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
        String script = "{\"nodes\":[{\"id\":\"e7699cab-e20b-4606-af66-fb8d782fd0f8\",\"type\":\"start-node\",\"x\":1005,\"y\":153,\"properties\":{\"name\":\"开始节点\",\"code\":\"start\",\"type\":\"START\",\"view\":\"default\",\"operatorMatcher\":\"def run(content) {return [content.getCreateOperator().getUserId()];}\",\"editable\":true,\"titleGenerator\":\"def run(content){ return content.getCreateOperator().getName() + '-' + content.getFlowWork().getTitle() + '-' + content.getFlowNode().getName();}\",\"errTrigger\":\"\",\"approvalType\":\"UN_SIGN\",\"timeout\":0,\"id\":\"e7699cab-e20b-4606-af66-fb8d782fd0f8\",\"width\":200,\"height\":45,\"operatorMatcherType\":\"creator\",\"titleGeneratorType\":\"default\",\"errTriggerType\":\"custom\"}},{\"id\":\"0d6638e9-0f98-45de-a5ad-8d55702158f7\",\"type\":\"node-node\",\"x\":722,\"y\":392,\"properties\":{\"name\":\"部门审批\",\"code\":\"dept\",\"type\":\"APPROVAL\",\"view\":\"default\",\"operatorMatcher\":\"def run(content) {return [content.getCurrentOperator().getUserId()];}\",\"editable\":true,\"titleGenerator\":\"def run(content){ return content.getCreateOperator().getName() + '-' + content.getFlowWork().getTitle() + '-' + content.getFlowNode().getName();}\",\"errTrigger\":\"\",\"approvalType\":\"SIGN\",\"timeout\":0,\"id\":\"0d6638e9-0f98-45de-a5ad-8d55702158f7\",\"width\":200,\"height\":45}},{\"id\":\"c4f8dd1b-53f2-4a87-8f57-9828b79fc4d0\",\"type\":\"over-node\",\"x\":918,\"y\":773,\"properties\":{\"name\":\"结束节点\",\"code\":\"over\",\"type\":\"OVER\",\"view\":\"default\",\"operatorMatcher\":\"def run(content) {return [content.getCurrentOperator().getUserId()];}\",\"editable\":true,\"titleGenerator\":\"def run(content){ return content.getCreateOperator().getName() + '-' + content.getFlowWork().getTitle() + '-' + content.getFlowNode().getName();}\",\"errTrigger\":\"\",\"approvalType\":\"UN_SIGN\",\"timeout\":0,\"id\":\"c4f8dd1b-53f2-4a87-8f57-9828b79fc4d0\",\"width\":200,\"height\":45}},{\"id\":\"ed654f48-c94c-4fdd-9b14-91f6295ae17a\",\"type\":\"node-node\",\"x\":1227,\"y\":530,\"properties\":{\"name\":\"老板审批\",\"code\":\"boss\",\"type\":\"APPROVAL\",\"view\":\"default\",\"operatorMatcher\":\"def run(content) {return [content.getCurrentOperator().getUserId()];}\",\"editable\":true,\"titleGenerator\":\"def run(content){ return content.getCreateOperator().getName() + '-' + content.getFlowWork().getTitle() + '-' + content.getFlowNode().getName();}\",\"errTrigger\":\"\",\"approvalType\":\"SIGN\",\"timeout\":0,\"id\":\"ed654f48-c94c-4fdd-9b14-91f6295ae17a\",\"width\":200,\"height\":45}}],\"edges\":[{\"id\":\"6854a4ef-89cf-48d3-9aa7-af1e5b87e93c\",\"type\":\"bezier\",\"properties\":{\"outTrigger\":\"def run(content) {return true;}\",\"order\":2,\"back\":false},\"sourceNodeId\":\"e7699cab-e20b-4606-af66-fb8d782fd0f8\",\"targetNodeId\":\"0d6638e9-0f98-45de-a5ad-8d55702158f7\",\"startPoint\":{\"x\":1005,\"y\":175.5},\"endPoint\":{\"x\":722,\"y\":369.5},\"pointsList\":[{\"x\":1005,\"y\":175.5},{\"x\":1005,\"y\":275.5},{\"x\":722,\"y\":269.5},{\"x\":722,\"y\":369.5}]},{\"id\":\"7c2c01fc-ded1-46e7-baf7-9fa664898d74\",\"type\":\"bezier\",\"properties\":{\"outTrigger\":\"def run(content) {return true;}\",\"order\":1,\"back\":false},\"sourceNodeId\":\"0d6638e9-0f98-45de-a5ad-8d55702158f7\",\"targetNodeId\":\"c4f8dd1b-53f2-4a87-8f57-9828b79fc4d0\",\"startPoint\":{\"x\":722,\"y\":414.5},\"endPoint\":{\"x\":918,\"y\":750.5},\"pointsList\":[{\"x\":722,\"y\":414.5},{\"x\":722,\"y\":514.5},{\"x\":918,\"y\":650.5},{\"x\":918,\"y\":750.5}]},{\"id\":\"67ee0fe7-b88c-4fde-be82-e23276f567e6\",\"type\":\"bezier\",\"properties\":{\"outTrigger\":\"def run(content) {return true;}\",\"order\":1,\"back\":false},\"sourceNodeId\":\"e7699cab-e20b-4606-af66-fb8d782fd0f8\",\"targetNodeId\":\"ed654f48-c94c-4fdd-9b14-91f6295ae17a\",\"startPoint\":{\"x\":1005,\"y\":175.5},\"endPoint\":{\"x\":1227,\"y\":507.5},\"pointsList\":[{\"x\":1005,\"y\":175.5},{\"x\":1005,\"y\":275.5},{\"x\":1227,\"y\":407.5},{\"x\":1227,\"y\":507.5}]},{\"id\":\"9c6f3b0c-03f5-46eb-be39-c79528a824dc\",\"type\":\"bezier\",\"properties\":{\"outTrigger\":\"def run(content) {return true;}\",\"order\":1,\"back\":false},\"sourceNodeId\":\"ed654f48-c94c-4fdd-9b14-91f6295ae17a\",\"targetNodeId\":\"c4f8dd1b-53f2-4a87-8f57-9828b79fc4d0\",\"startPoint\":{\"x\":1227,\"y\":552.5},\"endPoint\":{\"x\":918,\"y\":750.5},\"pointsList\":[{\"x\":1227,\"y\":552.5},{\"x\":1227,\"y\":652.5},{\"x\":918,\"y\":650.5},{\"x\":918,\"y\":750.5}]}]}";
        FlowWork flowWork = FlowWorkBuilder.builder(user)
                .title("请假流程")
                .schema(script)
                .build();
        assertEquals("请假流程", flowWork.getTitle());
        assertEquals(4, flowWork.getNodes().size());
        assertEquals(4, flowWork.getRelations().size());


        FlowNode startNode = flowWork.getStartNode();

        FlowWork copyWork = flowWork.copy();
        assertNotEquals(copyWork.getCode(), flowWork.getCode());

        assertEquals("请假流程", copyWork.getTitle());
        assertEquals(4, copyWork.getNodes().size());
        assertEquals(4, copyWork.getRelations().size());

        copyWork.verify();

        FlowNode startNode2 = copyWork.getNodeByCode("start");
        assertNotEquals(startNode.getId(), startNode2.getId());


    }
}
