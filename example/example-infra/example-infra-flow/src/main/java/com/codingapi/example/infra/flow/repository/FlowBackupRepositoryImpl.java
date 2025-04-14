package com.codingapi.example.infra.flow.repository;

import com.codingapi.example.infra.flow.convert.FlowBackupConvertor;
import com.codingapi.example.infra.flow.entity.FlowBackupEntity;
import com.codingapi.example.infra.flow.jpa.FlowBackupEntityRepository;
import com.codingapi.springboot.flow.domain.FlowWork;
import com.codingapi.springboot.flow.record.FlowBackup;
import com.codingapi.springboot.flow.repository.FlowBackupRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@AllArgsConstructor
public class FlowBackupRepositoryImpl implements FlowBackupRepository {

    private final FlowBackupEntityRepository flowBackupEntityRepository;

    @Override
    public FlowBackup backup(FlowWork flowWork) {
        FlowBackup flowBackup = new FlowBackup(flowWork);
        FlowBackupEntity entity = FlowBackupConvertor.convert(flowBackup);
        entity = flowBackupEntityRepository.save(entity);
        flowBackup.setId(entity.getId());
        return flowBackup;
    }

    @Override
    public FlowBackup getFlowBackupByWorkIdAndVersion(long workId, long workVersion) {
        return FlowBackupConvertor.convert(flowBackupEntityRepository.getFlowBackupEntityByWorkIdAndWorkVersion(workId, workVersion));
    }

    @Override
    public FlowBackup getFlowBackupById(long backupId) {
        return FlowBackupConvertor.convert(flowBackupEntityRepository.getFlowBackupEntityById(backupId));
    }
}
