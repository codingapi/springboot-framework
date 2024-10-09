package com.codingapi.springboot.flow.repository;

import com.codingapi.springboot.flow.domain.FlowNode;

public interface FlowNodeRepository {

    void save(FlowNode flowNode);

    FlowNode getFlowNodeById(String id);

    void delete(FlowNode flowNode);
}
