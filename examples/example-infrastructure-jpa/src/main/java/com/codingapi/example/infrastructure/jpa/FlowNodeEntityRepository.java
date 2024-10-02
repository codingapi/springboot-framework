package com.codingapi.example.infrastructure.jpa;

import com.codingapi.example.infrastructure.entity.flow.FlowNodeEntity;
import com.codingapi.springboot.fast.jpa.repository.FastRepository;

public interface FlowNodeEntityRepository extends FastRepository<FlowNodeEntity,Long> {

}
