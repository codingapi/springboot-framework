package com.codingapi.example.infrastructure.jpa;

import com.codingapi.example.infrastructure.entity.flow.FlowRecordEntity;
import com.codingapi.springboot.fast.jpa.repository.FastRepository;

public interface FlowRecordEntityRepository extends FastRepository<FlowRecordEntity,Long> {

}
