package com.codingapi.springboot.flow.matcher;

import com.codingapi.springboot.flow.domain.FlowRecord;
import com.codingapi.springboot.flow.operator.IFlowOperator;

import java.util.List;

/**
 * 任意操作者匹配器
 * 传人任意操作者，返回操作者id
 */
public class AnyOperatorMatcher implements IOperatorMatcher {

    @Override
    public List<Long> matcherOperatorIds(FlowRecord context, IFlowOperator operator) {
        return List.of(operator.getId());
    }

}
