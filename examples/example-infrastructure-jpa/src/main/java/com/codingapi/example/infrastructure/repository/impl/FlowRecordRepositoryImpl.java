package com.codingapi.example.infrastructure.repository.impl;

import com.codingapi.springboot.flow.domain.FlowRecord;
import com.codingapi.springboot.flow.repository.FlowRecordRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class FlowRecordRepositoryImpl implements FlowRecordRepository {

    @Override
    public void save(FlowRecord flowRecord) {

    }

    @Override
    public List<FlowRecord> findAllFlowRecordByProcessId(long processId) {
        return List.of();
    }

    @Override
    public List<FlowRecord> findChildrenFlowRecordByParentId(long parentId) {
        return List.of();
    }

    @Override
    public List<FlowRecord> findAllFlowRecordByOperatorId(long operatorId) {
        return List.of();
    }

    @Override
    public List<FlowRecord> findTodoFlowRecordByOperatorId(long operatorId) {
        return List.of();
    }

    @Override
    public List<FlowRecord> findDoneFlowRecordByOperatorId(long operatorId) {
        return List.of();
    }

    @Override
    public void delete(FlowRecord flowRecord) {

    }

    @Override
    public FlowRecord getFlowRecordById(long id) {
        return null;
    }
}
