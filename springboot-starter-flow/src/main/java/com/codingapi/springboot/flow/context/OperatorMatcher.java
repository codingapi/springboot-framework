package com.codingapi.springboot.flow.context;

import com.codingapi.springboot.flow.domain.FlowRecord;
import com.codingapi.springboot.flow.matcher.IOperatorMatcher;
import com.codingapi.springboot.flow.operator.IFlowOperator;

import java.util.List;

/**
 * 操作者匹配器
 */
public class OperatorMatcher {

    /**
     * 匹配操作者
     *
     * @param operator 操作者
     * @return 是否匹配
     */
    public static List<? extends IFlowOperator> matcher(IOperatorMatcher operatorMatcher, FlowRecord context, IFlowOperator operator) {
        List<Long> operatorIds = operatorMatcher.matcherOperatorIds(context, operator);
        return FlowRepositoryContext.getInstance().findOperatorByIds(operatorIds);
    }


    public static List<? extends IFlowOperator> matcher(IOperatorMatcher operatorMatcher, IFlowOperator operator) {
        return matcher(operatorMatcher, null, operator);
    }

    public static List<? extends IFlowOperator> matcher(IOperatorMatcher operatorMatcher, FlowRecord context) {
        return matcher(operatorMatcher, context, null);
    }

}
