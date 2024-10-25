package com.codingapi.springboot.flow.repository;

import com.codingapi.springboot.flow.domain.FlowWork;
import com.codingapi.springboot.flow.record.FlowBackup;
import com.codingapi.springboot.flow.record.FlowProcess;
import com.codingapi.springboot.flow.serializable.FlowWorkSerializable;
import lombok.AllArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
public class FlowProcessRepositoryImpl implements FlowProcessRepository {

    private final List<FlowProcess> cache = new ArrayList<>();
    private final FlowBackupRepository flowBackupRepository;
    private final UserRepository userRepository;


    @Override
    public void save(FlowProcess flowProcess) {
        List<String> ids = cache.stream().map(FlowProcess::getProcessId).toList();
        if (!ids.contains(flowProcess.getProcessId())) {
            cache.add(flowProcess);
            flowProcess.setId(cache.size());
        }
    }

    @Override
    public FlowWork getFlowWorkByProcessId(String processId) {
        FlowProcess process = cache.stream().filter(flowProcess -> flowProcess.getProcessId().equals(processId)).findFirst().orElse(null);
        if (process == null) {
            return null;
        }
        FlowBackup flowBackup = flowBackupRepository.getFlowBackupById(process.getBackupId());
        return FlowWorkSerializable.fromSerializable(flowBackup.getBytes()).toFlowWork(userRepository);
    }


}
