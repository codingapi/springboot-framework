package com.codingapi.springboot.flow.pojo;

import com.codingapi.springboot.flow.domain.FlowNode;
import com.codingapi.springboot.flow.domain.FlowWork;
import com.codingapi.springboot.flow.user.IFlowOperator;
import lombok.Getter;

import java.util.List;

@Getter
public class FlowSubmitResult {

    private final FlowWork flowWork;
    private final FlowNode flowNode;
    private final List<? extends IFlowOperator> operators;

    public FlowSubmitResult(FlowWork flowWork, FlowNode flowNode, List<? extends IFlowOperator> operators) {
        this.flowWork = flowWork;
        this.flowNode = flowNode;
        this.operators = operators;
    }

}
