package com.codingapi.springboot.flow.repository;

import com.codingapi.springboot.flow.record.FlowProcess;
import com.codingapi.springboot.flow.domain.FlowWork;

/**
 *  流程仓库，每一个流程都会在创建时被创建一条process数据，用于标识流程
 */
public interface FlowProcessRepository {

    void save(FlowProcess flowProcess);

    FlowWork getFlowWorkByProcessId(String processId);

    FlowProcess getFlowProcessByProcessId(String processId);

    void deleteByProcessId(String processId);

}
