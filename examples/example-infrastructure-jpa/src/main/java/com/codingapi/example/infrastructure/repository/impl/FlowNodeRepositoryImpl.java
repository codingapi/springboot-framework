package com.codingapi.example.infrastructure.repository.impl;

import com.codingapi.example.infrastructure.convert.FlowNodeConvertor;
import com.codingapi.example.infrastructure.entity.flow.FlowNodeEntity;
import com.codingapi.example.infrastructure.jpa.FlowNodeEntityRepository;
import com.codingapi.springboot.flow.context.FlowRepositoryContext;
import com.codingapi.springboot.flow.domain.FlowNode;
import com.codingapi.springboot.flow.repository.FlowNodeRepository;
import org.springframework.stereotype.Repository;

@Repository
public class FlowNodeRepositoryImpl implements FlowNodeRepository {

    private final FlowNodeEntityRepository flowNodeEntityRepository;

    public FlowNodeRepositoryImpl(FlowNodeEntityRepository flowNodeEntityRepository) {
        this.flowNodeEntityRepository = flowNodeEntityRepository;
        FlowRepositoryContext.getInstance().bind(this);
    }

    @Override
    public void save(FlowNode flowNode) {
        FlowNodeEntity entity = FlowNodeConvertor.convert(flowNode);
        entity =  flowNodeEntityRepository.save(entity);
        flowNode.setId(entity.getId());
    }

    @Override
    public FlowNode getFlowNodeById(String id) {
        return FlowNodeConvertor.convert(flowNodeEntityRepository.getFlowNodeEntityById(id));
    }

    @Override
    public void delete(FlowNode flowNode) {
        flowNodeEntityRepository.deleteById(flowNode.getId());
    }
}
