package com.codingapi.springboot.flow.repository;

import com.codingapi.springboot.flow.domain.FlowNode;
import com.codingapi.springboot.flow.repository.FlowNodeRepository;

import java.util.ArrayList;
import java.util.List;

public class FlowNodeRepositoryImpl implements FlowNodeRepository {

    private final List<FlowNode> nodes = new ArrayList<>();

    @Override
    public void save(FlowNode flowNode) {
        nodes.add(flowNode);
        flowNode.setUpdateTime(System.currentTimeMillis());
    }

    @Override
    public FlowNode getFlowNodeById(String id) {
        return nodes.stream().filter(flowNode -> flowNode.getId() .equals(id)).findFirst().orElse(null);
    }

    @Override
    public void delete(FlowNode flowNode) {
        nodes.remove(flowNode);
    }
}
