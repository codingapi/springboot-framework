package com.codingapi.example.jpa;

import com.codingapi.example.entity.FlowRelationEntity;
import com.codingapi.springboot.fast.jpa.repository.FastRepository;

import java.util.List;

public interface FlowRelationEntityRepository extends FastRepository<FlowRelationEntity,String> {

    List<FlowRelationEntity> findFlowRelationEntityByWorkId(long id);

}
