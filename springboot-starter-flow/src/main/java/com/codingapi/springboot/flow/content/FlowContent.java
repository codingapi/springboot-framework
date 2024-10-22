package com.codingapi.springboot.flow.content;

import com.codingapi.springboot.flow.bind.BindDataSnapshot;
import com.codingapi.springboot.flow.domain.FlowNode;
import com.codingapi.springboot.flow.domain.FlowWork;
import com.codingapi.springboot.flow.domain.Opinion;
import com.codingapi.springboot.flow.user.IFlowOperator;
import lombok.Getter;

@Getter
public class FlowContent {

    private FlowWork flowWork;
    private FlowNode currentNode;
    private IFlowOperator createOperator;
    private IFlowOperator currentOperator;
    private BindDataSnapshot snapshot;
    private Opinion opinion;


    public FlowContent(FlowWork flowWork, FlowNode currentNode, IFlowOperator createOperator, IFlowOperator currentOperator, BindDataSnapshot snapshot, Opinion opinion) {
        this.flowWork = flowWork;
        this.currentNode = currentNode;
        this.createOperator = createOperator;
        this.currentOperator = currentOperator;
        this.snapshot = snapshot;
        this.opinion = opinion;
    }
}
