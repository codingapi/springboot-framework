package com.codingapi.springboot.flow.repository;

import com.codingapi.springboot.flow.record.FlowProcess;
import com.codingapi.springboot.flow.domain.FlowWork;

public interface FlowProcessRepository {

    void save(FlowProcess flowProcess);


    FlowWork getFlowWorkByProcessId(String processId);

}
