package com.codingapi.springboot.flow.repository;

import com.codingapi.springboot.flow.record.FlowProcess;
import com.codingapi.springboot.flow.domain.FlowWork;

import java.util.ArrayList;
import java.util.List;

public class FlowProcessRepositoryImpl implements FlowProcessRepository {

    private final List<FlowProcess> cache = new ArrayList<>();

    @Override
    public void save(FlowProcess flowProcess) {
        List<String> ids = cache.stream().map(FlowProcess::getId).toList();
        if (!ids.contains(flowProcess.getId())) {
            cache.add(flowProcess);
        }
    }

    @Override
    public FlowWork getFlowWorkByProcessId(String processId) {
        return cache.stream().filter(flowProcess -> flowProcess.getId().equals(processId)).findFirst().map(FlowProcess::getFlowWork).orElse(null);
    }
}
