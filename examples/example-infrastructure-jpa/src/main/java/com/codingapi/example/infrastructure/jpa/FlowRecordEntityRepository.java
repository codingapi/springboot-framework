package com.codingapi.example.infrastructure.jpa;

import com.codingapi.example.infrastructure.entity.flow.FlowRecordEntity;
import com.codingapi.springboot.fast.jpa.repository.FastRepository;
import com.codingapi.springboot.flow.em.NodeStatus;

import java.util.List;

public interface FlowRecordEntityRepository extends FastRepository<FlowRecordEntity,Long> {

    FlowRecordEntity getFlowRecordEntityById(long id);

    List<FlowRecordEntity> findFlowRecordEntityByPreRecordId(long preRecordId);

    List<FlowRecordEntity> findFlowRecordEntityByProcessId(long processId);

    List<FlowRecordEntity> findFlowRecordEntityByOperatorUserId(long operatorUserId);

    List<FlowRecordEntity> findFlowRecordEntityByOperatorUserIdAndNodeStatus(long operatorUserId, NodeStatus nodeStatus);

    List<FlowRecordEntity> findFlowRecordEntityByOperatorUserIdAndPreRecordId(long operatorUserId,long preRecordId);

}
