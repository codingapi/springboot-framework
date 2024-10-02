package com.codingapi.example.infrastructure.repository.impl;

import com.codingapi.example.infrastructure.convert.FlowRecordConvertor;
import com.codingapi.example.infrastructure.entity.flow.FlowRecordEntity;
import com.codingapi.example.infrastructure.jpa.FlowRecordEntityRepository;
import com.codingapi.springboot.flow.domain.FlowRecord;
import com.codingapi.springboot.flow.em.NodeStatus;
import com.codingapi.springboot.flow.repository.FlowRecordRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@AllArgsConstructor
public class FlowRecordRepositoryImpl implements FlowRecordRepository {

    private final FlowRecordEntityRepository flowRecordEntityRepository;

    @Override
    public void save(FlowRecord flowRecord) {
        FlowRecordEntity entity = FlowRecordConvertor.convert(flowRecord);
        entity = flowRecordEntityRepository.save(entity);
        flowRecord.setId(entity.getId());
    }

    @Override
    public List<FlowRecord> findAllFlowRecordByProcessId(long processId) {
        return flowRecordEntityRepository.findFlowRecordEntityByProcessId(processId)
                .stream().map(FlowRecordConvertor::convert).toList();
    }

    @Override
    public List<FlowRecord> findChildrenFlowRecordByParentId(long parentId) {
        return flowRecordEntityRepository.findFlowRecordEntityByParentId(parentId)
                .stream().map(FlowRecordConvertor::convert).toList();
    }

    @Override
    public List<FlowRecord> findAllFlowRecordByOperatorId(long operatorId) {
        return  flowRecordEntityRepository.findFlowRecordEntityByOperatorUserId(operatorId)
                .stream().map(FlowRecordConvertor::convert).toList();
    }

    @Override
    public List<FlowRecord> findTodoFlowRecordByOperatorId(long operatorId) {
        return flowRecordEntityRepository.findFlowRecordEntityByOperatorUserIdAndNodeStatus(operatorId, NodeStatus.TODO)
                .stream().map(FlowRecordConvertor::convert).toList();
    }

    @Override
    public List<FlowRecord> findDoneFlowRecordByOperatorId(long operatorId) {
        return flowRecordEntityRepository.findFlowRecordEntityByOperatorUserIdAndNodeStatus(operatorId, NodeStatus.DONE)
                .stream().map(FlowRecordConvertor::convert).toList();
    }

    @Override
    public void delete(FlowRecord flowRecord) {
        flowRecordEntityRepository.deleteById(flowRecord.getId());
    }

    @Override
    public FlowRecord getFlowRecordById(long id) {
        return FlowRecordConvertor.convert(flowRecordEntityRepository.getFlowRecordEntityById(id));
    }

}
