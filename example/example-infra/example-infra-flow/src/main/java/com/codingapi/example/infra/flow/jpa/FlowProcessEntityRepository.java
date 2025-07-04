package com.codingapi.example.infra.flow.jpa;

import com.codingapi.example.infra.flow.entity.FlowProcessEntity;
import com.codingapi.springboot.fast.jpa.repository.FastRepository;

public interface FlowProcessEntityRepository extends FastRepository<FlowProcessEntity,String> {

    FlowProcessEntity getFlowProcessEntityByProcessId(String processId);

    void deleteByProcessId(String processId);

}
