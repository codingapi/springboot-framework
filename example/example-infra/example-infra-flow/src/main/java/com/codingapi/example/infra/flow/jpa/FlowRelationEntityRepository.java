package com.codingapi.example.infra.flow.jpa;

import com.codingapi.example.infra.flow.entity.FlowRelationEntity;
import com.codingapi.springboot.fast.jpa.repository.FastRepository;
import org.springframework.data.jpa.repository.Modifying;

import java.util.List;

public interface FlowRelationEntityRepository extends FastRepository<FlowRelationEntity,String> {

    List<FlowRelationEntity> findFlowRelationEntityByWorkId(long id);


    @Modifying
    void deleteAllByWorkId(long workId);

}
