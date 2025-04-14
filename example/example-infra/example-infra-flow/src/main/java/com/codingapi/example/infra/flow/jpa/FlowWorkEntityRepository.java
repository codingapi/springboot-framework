package com.codingapi.example.infra.flow.jpa;

import com.codingapi.example.infra.flow.entity.FlowWorkEntity;
import com.codingapi.springboot.fast.jpa.repository.FastRepository;

public interface FlowWorkEntityRepository extends FastRepository<FlowWorkEntity,Long> {


    FlowWorkEntity getFlowWorkEntityById(long id);

    FlowWorkEntity getFlowWorkEntityByCode(String code);

}
