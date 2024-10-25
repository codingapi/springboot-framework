package com.codingapi.springboot.flow.repository;

import com.codingapi.springboot.flow.domain.FlowWork;
import com.codingapi.springboot.flow.record.FlowProcess;
import com.codingapi.springboot.flow.serializable.FlowWorkSerializable;
import lombok.AllArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
public class FlowProcessRepositoryImpl implements FlowProcessRepository {

    private final List<FlowProcess> cache = new ArrayList<>();
    private final UserRepository userRepository;

    @Override
    public void save(FlowProcess flowProcess) {
        List<String> ids = cache.stream().map(FlowProcess::getId).toList();
        if (!ids.contains(flowProcess.getId())) {
            cache.add(flowProcess);
        }
    }

    @Override
    public FlowWork getFlowWorkByProcessId(String processId) {
        byte[] bytes = cache.stream().filter(flowProcess -> flowProcess.getId().equals(processId)).findFirst().map(FlowProcess::getBytes).orElse(null);
        return FlowWorkSerializable.fromSerializable(bytes).toFlowWork(userRepository);
    }
}
