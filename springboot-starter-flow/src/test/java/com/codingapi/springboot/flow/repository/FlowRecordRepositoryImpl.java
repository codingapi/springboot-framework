package com.codingapi.springboot.flow.repository;

import com.codingapi.springboot.flow.domain.FlowRecord;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class FlowRecordRepositoryImpl implements FlowRecordRepository {

    private final List<FlowRecord> records = new ArrayList<>();

    @Override
    public void save(FlowRecord flowRecord) {
        if (flowRecord.getId() == 0) {
            records.add(flowRecord);
            flowRecord.setId(records.size());
        } else {
            flowRecord.setUpdateTime(System.currentTimeMillis());
        }
    }

    @Override
    public List<FlowRecord> findAllFlowRecordByProcessId(long processId) {
        return records.stream().filter(flowRecord -> flowRecord.getProcessId() == processId).collect(Collectors.toList());
    }

    @Override
    public List<FlowRecord> findAllFlowRecordByOperatorId(long operatorId) {
        return records.stream().filter(flowRecord -> flowRecord.getOperatorUser().getId() == operatorId).collect(Collectors.toList());
    }

    @Override
    public List<FlowRecord> findTodoFlowRecordByOperatorId(long operatorId) {
        return records.stream().filter(flowRecord -> flowRecord.getOperatorUser().getId() == operatorId && flowRecord.isTodo() && !flowRecord.isFinish()).collect(Collectors.toList());
    }

    @Override
    public List<FlowRecord> findDoneFlowRecordByOperatorId(long operatorId) {
        return records.stream().filter(flowRecord -> flowRecord.getOperatorUser().getId() == operatorId && flowRecord.isDone()).collect(Collectors.toList());
    }

    @Override
    public List<FlowRecord> findChildrenFlowRecordByParentId(long parentId) {
        return records.stream().filter(flowRecord -> flowRecord.getPreRecordId() == parentId).collect(Collectors.toList());
    }

    @Override
    public void delete(FlowRecord flowRecord) {
        records.remove(flowRecord);
    }

    @Override
    public FlowRecord getFlowRecordById(long id) {
        for (FlowRecord record : records) {
            if (record.getId() == id) {
                return record;
            }
        }
        return null;
    }
}
