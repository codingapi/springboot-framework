package com.codingapi.example.jpa;

import com.codingapi.example.entity.FlowWorkEntity;
import com.codingapi.springboot.fast.jpa.repository.FastRepository;

public interface FlowWorkEntityRepository extends FastRepository<FlowWorkEntity,Long> {


    FlowWorkEntity getFlowWorkEntityById(long id);

}
