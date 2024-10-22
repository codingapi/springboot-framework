package com.codingapi.springboot.flow.error;

import com.codingapi.springboot.flow.user.IFlowOperator;
import lombok.Getter;

import java.util.List;

@Getter
public class OperatorResult extends ErrorResult{

    private List<IFlowOperator> operators;

    public OperatorResult(List<IFlowOperator> operators) {
        this.operators = operators;
    }
}
