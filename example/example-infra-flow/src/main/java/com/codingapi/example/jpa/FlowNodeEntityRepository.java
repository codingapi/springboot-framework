package com.codingapi.example.jpa;

import com.codingapi.example.entity.FlowNodeEntity;
import com.codingapi.springboot.fast.jpa.repository.FastRepository;

import java.util.List;

public interface FlowNodeEntityRepository extends FastRepository<FlowNodeEntity,String> {


    List<FlowNodeEntity> findFlowNodeEntityByWorkId(long workId);
}
