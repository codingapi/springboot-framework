package com.codingapi.springboot.flow.matcher;

import com.codingapi.springboot.flow.domain.FlowRecord;
import com.codingapi.springboot.flow.operator.IFlowOperator;

import java.util.List;

/**
 * 操作者匹配器，用于匹配操作者，根据流程的设计，匹配操作者，
 * 该匹配起主要用在发起节点时判断哪些人员可以发起该流程，
 * 当流程发起以后下一节点的操作者将会明确的，因此在发起以后查询代办时直接获取流程记录中的操作者即可。
 */
public interface IOperatorMatcher {

    /**
     * 匹配操作者
     * @param context 流程记录，在发起节点时，context为null
     * @param operator 当前操作者
     * @return 操作者id列表，后续将根据id查询操作者
     */
    List<Long> matcherOperatorIds(FlowRecord context, IFlowOperator operator);

}
