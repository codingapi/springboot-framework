package com.codingapi.example.infrastructure.repository.impl;

import com.codingapi.example.infrastructure.convert.FlowNodeConvertor;
import com.codingapi.example.infrastructure.entity.flow.FlowNodeEntity;
import com.codingapi.example.infrastructure.jpa.FlowNodeEntityRepository;
import com.codingapi.springboot.flow.domain.FlowNode;
import com.codingapi.springboot.flow.repository.FlowNodeRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@AllArgsConstructor
public class FlowNodeRepositoryImpl implements FlowNodeRepository {

    private final FlowNodeEntityRepository flowNodeEntityRepository;

    @Override
    public void save(FlowNode flowNode) {
        FlowNodeEntity entity = FlowNodeConvertor.convert(flowNode);
        entity =  flowNodeEntityRepository.save(entity);
        flowNode.setId(entity.getId());
    }
}
