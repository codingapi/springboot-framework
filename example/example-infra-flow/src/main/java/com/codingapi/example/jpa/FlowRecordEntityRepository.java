package com.codingapi.example.jpa;

import com.codingapi.example.entity.FlowRecordEntity;
import com.codingapi.springboot.fast.jpa.repository.FastRepository;

import java.util.List;

public interface FlowRecordEntityRepository extends FastRepository<FlowRecordEntity,Long> {


    FlowRecordEntity getFlowRecordEntityById(long id);


    List<FlowRecordEntity> findFlowRecordEntityByPreId(long preId);

    List<FlowRecordEntity> findFlowRecordEntityByProcessId(String processId);


    List<FlowRecordEntity> findFlowRecordEntityByProcessIdAndRecodeTypeAndFlowStatus(String processId, String recodeType, String flowStatus);


}
