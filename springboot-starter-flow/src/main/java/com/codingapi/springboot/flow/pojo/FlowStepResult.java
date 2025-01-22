package com.codingapi.springboot.flow.pojo;

import com.codingapi.springboot.flow.domain.FlowNode;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class FlowStepResult {

    private final List<FlowNode> flowNodes;

    public FlowStepResult() {
        this.flowNodes = new ArrayList<>();
    }

    public void addFlowNode(FlowNode flowNode) {
        this.flowNodes.add(flowNode);
    }


    public void print(){
        for (FlowNode flowNode : flowNodes) {
            System.out.println("flowNode = " + flowNode.getName());
        }
    }


    public static class FlowStepNode{

    }
}
