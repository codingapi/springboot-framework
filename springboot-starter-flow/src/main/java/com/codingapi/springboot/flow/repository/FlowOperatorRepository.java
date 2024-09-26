package com.codingapi.springboot.flow.repository;

import com.codingapi.springboot.flow.operator.IFlowOperator;

import java.util.List;

public interface FlowOperatorRepository {

    List<? extends IFlowOperator> findOperatorByIds(List<Long> operatorIds);

    IFlowOperator getOperatorById(long id);

}
