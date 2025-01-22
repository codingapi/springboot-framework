package com.codingapi.springboot.flow.pojo;

import com.codingapi.springboot.flow.domain.FlowNode;
import com.codingapi.springboot.flow.em.NodeType;
import com.codingapi.springboot.flow.user.IFlowOperator;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class FlowStepResult {

    private final List<FlowStepNode> flowNodes;

    public FlowStepResult() {
        this.flowNodes = new ArrayList<>();
    }

    public void addFlowNode(FlowNode flowNode,List<? extends IFlowOperator> operators) {
        this.flowNodes.add(new FlowStepNode(flowNode.getId(), flowNode.getCode(),flowNode.getName(),flowNode.getType(),operators));
    }


    public void print(){
        for (FlowStepNode flowNode : flowNodes) {
            System.out.println("flowNode = " + flowNode.getName());
        }
    }


    @Getter
    public static class FlowStepNode{
        private final String id;
        private final String code;
        private final String name;
        private final NodeType type;
        private final List<? extends IFlowOperator> operators;

        public FlowStepNode(String id, String code, String name, NodeType type,List<? extends IFlowOperator> operators) {
            this.id = id;
            this.code = code;
            this.name = name;
            this.type = type;
            this.operators = operators;
        }
    }
}
