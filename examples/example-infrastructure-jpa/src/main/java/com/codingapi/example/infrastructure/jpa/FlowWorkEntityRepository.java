package com.codingapi.example.infrastructure.jpa;

import com.codingapi.example.infrastructure.entity.flow.FlowWorkEntity;
import com.codingapi.springboot.fast.jpa.repository.FastRepository;

public interface FlowWorkEntityRepository extends FastRepository<FlowWorkEntity,Long> {


    FlowWorkEntity getFlowWorkEntityById(long id);
}
