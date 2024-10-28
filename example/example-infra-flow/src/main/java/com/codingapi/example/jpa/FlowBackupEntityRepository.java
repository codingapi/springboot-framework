package com.codingapi.example.jpa;

import com.codingapi.example.entity.FlowBackupEntity;
import com.codingapi.springboot.fast.jpa.repository.FastRepository;

public interface FlowBackupEntityRepository extends FastRepository<FlowBackupEntity,Long> {


    FlowBackupEntity getFlowBackupEntityById(long id);


    FlowBackupEntity getFlowBackupEntityByWorkIdAndWorkVersion(long workId,long workVersion);


}
