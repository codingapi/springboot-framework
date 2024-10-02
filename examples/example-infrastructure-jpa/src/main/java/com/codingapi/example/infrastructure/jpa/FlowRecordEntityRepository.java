package com.codingapi.example.infrastructure.jpa;

import com.codingapi.example.infrastructure.entity.flow.FlowRecordEntity;
import com.codingapi.springboot.fast.jpa.repository.FastRepository;
import com.codingapi.springboot.flow.em.NodeStatus;

import java.util.List;

public interface FlowRecordEntityRepository extends FastRepository<FlowRecordEntity,Long> {

    FlowRecordEntity getFlowRecordEntityById(long id);

    List<FlowRecordEntity> findFlowRecordEntityByParentId(long parentId);

    List<FlowRecordEntity> findFlowRecordEntityByProcessId(long processId);

    List<FlowRecordEntity> findFlowRecordEntityByOperatorUserId(long operatorUserId);

    List<FlowRecordEntity> findFlowRecordEntityByOperatorUserIdAndNodeStatus(long operatorUserId, NodeStatus nodeStatus);

}
