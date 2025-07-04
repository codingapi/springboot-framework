package com.codingapi.springboot.flow.repository;

import com.codingapi.springboot.flow.domain.FlowWork;
import com.codingapi.springboot.flow.record.FlowBackup;
import com.codingapi.springboot.flow.record.FlowProcess;
import lombok.AllArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
public class FlowProcessRepositoryImpl implements FlowProcessRepository {

    private final List<FlowProcess> cache = new ArrayList<>();
    private final FlowBackupRepository flowBackupRepository;
    private final UserRepository userRepository;


    @Override
    public void save(FlowProcess flowProcess) {
        List<String> ids = cache.stream().map(FlowProcess::getProcessId).collect(Collectors.toList());
        if (!ids.contains(flowProcess.getProcessId())) {
            cache.add(flowProcess);
        }
    }

    @Override
    public FlowWork getFlowWorkByProcessId(String processId) {
        FlowProcess process = cache.stream()
                .filter(flowProcess -> flowProcess.getProcessId().equals(processId))
                .filter(flowProcess -> !flowProcess.isVoided())
                .findFirst().orElse(null);
        if (process == null) {
            return null;
        }
        FlowBackup flowBackup = flowBackupRepository.getFlowBackupById(process.getBackupId());
        return flowBackup.resume(userRepository);
    }

    @Override
    public FlowProcess getFlowProcessByProcessId(String processId) {
        return cache.stream().filter(flowProcess -> flowProcess.getProcessId().equals(processId)).findFirst().orElse(null);
    }

    @Override
    public void deleteByProcessId(String processId) {
        cache.removeIf(flowProcess -> flowProcess.getProcessId().equals(processId));
    }
}
