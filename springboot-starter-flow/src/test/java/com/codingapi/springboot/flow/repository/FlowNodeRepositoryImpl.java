package com.codingapi.springboot.flow.repository;

import com.codingapi.springboot.flow.domain.FlowNode;
import com.codingapi.springboot.flow.repository.FlowNodeRepository;

import java.util.ArrayList;
import java.util.List;

public class FlowNodeRepositoryImpl implements FlowNodeRepository {

    private final List<FlowNode> nodes = new ArrayList<>();

    @Override
    public void save(FlowNode flowNode) {
        if(flowNode.getId()==0){
            nodes.add(flowNode);
            flowNode.setId(nodes.size());
        }else {
            flowNode.setUpdateTime(System.currentTimeMillis());
        }
    }
}
