package com.codingapi.springboot.flow.content;

import com.codingapi.springboot.flow.bind.IBindData;
import com.codingapi.springboot.flow.domain.FlowNode;
import com.codingapi.springboot.flow.domain.FlowWork;
import com.codingapi.springboot.flow.domain.Opinion;
import com.codingapi.springboot.flow.user.IFlowOperator;
import lombok.Getter;

@Getter
public class FlowContent {

    private FlowWork flowWork;
    private FlowNode flowNode;
    private IFlowOperator createOperator;
    private IFlowOperator currentOperator;
    private IBindData bindData;
    private Opinion opinion;


    public FlowContent(FlowWork flowWork, FlowNode flowNode, IFlowOperator createOperator, IFlowOperator currentOperator, IBindData bindData, Opinion opinion) {
        this.flowWork = flowWork;
        this.flowNode = flowNode;
        this.createOperator = createOperator;
        this.currentOperator = currentOperator;
        this.bindData = bindData;
        this.opinion = opinion;
    }
}
