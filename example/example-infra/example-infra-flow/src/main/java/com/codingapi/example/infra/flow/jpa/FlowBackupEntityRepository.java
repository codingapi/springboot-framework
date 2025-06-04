package com.codingapi.example.infra.flow.jpa;

import com.codingapi.example.infra.flow.entity.FlowBackupEntity;
import com.codingapi.springboot.fast.jpa.repository.FastRepository;

public interface FlowBackupEntityRepository extends FastRepository<FlowBackupEntity,Long> {


    FlowBackupEntity getFlowBackupEntityById(long id);


    FlowBackupEntity getFlowBackupEntityByWorkIdAndWorkVersion(long workId,long workVersion);


}
