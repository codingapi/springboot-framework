package com.codingapi.springboot.flow.repository;

import com.codingapi.springboot.flow.domain.FlowWork;
import com.codingapi.springboot.flow.record.FlowBackup;

import java.util.ArrayList;
import java.util.List;

public class FlowBackupRepositoryImpl implements FlowBackupRepository{

    private final List<FlowBackup> cache = new ArrayList<>();

    @Override
    public FlowBackup backup(FlowWork flowWork) {
        FlowBackup flowBackup = new FlowBackup(flowWork);
        cache.add(flowBackup);
        return flowBackup;
    }

    @Override
    public FlowBackup getFlowBackupByWorkIdAndVersion(long workId, long workVersion) {
        return cache.stream().filter(flowBackup -> flowBackup.getWorkId() == workId && flowBackup.getWorkVersion() == workVersion).findFirst().orElse(null);
    }

    @Override
    public FlowBackup getFlowBackupById(long backupId) {
        return cache.stream().filter(flowBackup -> flowBackup.getId() == backupId).findFirst().orElse(null);
    }
}
