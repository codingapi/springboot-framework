package com.codingapi.example.jpa;

import com.codingapi.example.entity.FlowProcessEntity;
import com.codingapi.springboot.fast.jpa.repository.FastRepository;

public interface FlowProcessEntityRepository extends FastRepository<FlowProcessEntity,String> {

    FlowProcessEntity getFlowProcessEntityByProcessId(String processId);


    void deleteByProcessId(String processId);

}
