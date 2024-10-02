package com.codingapi.example.infrastructure.repository.impl;

import com.codingapi.example.infrastructure.convert.FlowWorkConvertor;
import com.codingapi.example.infrastructure.entity.flow.FlowWorkEntity;
import com.codingapi.example.infrastructure.jpa.FlowWorkEntityRepository;
import com.codingapi.springboot.flow.context.FlowRepositoryContext;
import com.codingapi.springboot.flow.domain.FlowWork;
import com.codingapi.springboot.flow.repository.FlowWorkRepository;
import org.springframework.stereotype.Repository;

@Repository
public class FlowWorkRepositoryImpl implements FlowWorkRepository {

    private final FlowWorkEntityRepository flowWorkEntityRepository;

    public FlowWorkRepositoryImpl(FlowWorkEntityRepository flowWorkEntityRepository) {
        this.flowWorkEntityRepository = flowWorkEntityRepository;
        FlowRepositoryContext.getInstance().bind(this);
    }

    @Override
    public void save(FlowWork flowWork) {
        FlowWorkEntity entity = FlowWorkConvertor.convert(flowWork);
        entity = flowWorkEntityRepository.save(entity);
        flowWork.setId(entity.getId());
    }

    @Override
    public FlowWork getFlowWorkById(long flowWorkId) {
        return FlowWorkConvertor.convert(flowWorkEntityRepository.getFlowWorkEntityById(flowWorkId));
    }
}
