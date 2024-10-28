package com.codingapi.springboot.flow.repository;

import com.codingapi.springboot.flow.query.FlowRecordQuery;
import com.codingapi.springboot.flow.record.FlowRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.ArrayList;
import java.util.List;

public class FlowRecordRepositoryImpl implements FlowRecordRepository, FlowRecordQuery {

    private final List<FlowRecord> cache = new ArrayList<>();

    @Override
    public void save(List<FlowRecord> records) {
        for (FlowRecord record : records) {
            if (record.getId() == 0) {
                cache.add(record);
                record.setId(cache.size());
            }
        }
    }

    @Override
    public FlowRecord getFlowRecordById(long id) {
        return cache.stream().filter(record -> record.getId() == id).findFirst().orElse(null);
    }


    @Override
    public void update(FlowRecord flowRecord) {
        if (flowRecord.getId() == 0) {
            cache.add(flowRecord);
            flowRecord.setId(cache.size());
        }
    }

    @Override
    public List<FlowRecord> findFlowRecordByPreId(long preId) {
        return cache.stream().filter(record -> record.getPreId() == preId).toList();
    }

    @Override
    public List<FlowRecord> findFlowRecordByProcessId(String processId) {
        return cache.stream().filter(record -> record.getProcessId().equals(processId))
                .sorted((o1, o2) -> (int) (o1.getCreateTime() - o2.getCreateTime()))
                .toList();
    }

    @Override
    public List<FlowRecord> findTodoFlowRecordByProcessId(String processId) {
        return cache.stream().filter(record -> record.isTodo() && record.getProcessId().equals(processId)).toList();
    }

    public Page<FlowRecord> findAll(PageRequest pageRequest) {
        return new PageImpl<>(cache);
    }

    @Override
    public Page<FlowRecord> findDoneByOperatorId(long operatorId,PageRequest pageRequest) {
        List<FlowRecord> flowRecords = cache.stream().filter(record -> record.isDone() && record.getCurrentOperatorId() == operatorId).toList();
        return new PageImpl<>(flowRecords);
    }

    @Override
    public Page<FlowRecord> findInitiatedByOperatorId(long operatorId,PageRequest pageRequest) {
        List<FlowRecord> flowRecords = cache.stream().filter(record -> record.isInitiated() && record.getCreateOperatorId() == operatorId).toList();
        return new PageImpl<>(flowRecords);
    }

    @Override
    public Page<FlowRecord> findTodoByOperatorId(long operatorId,PageRequest pageRequest) {
        List<FlowRecord> flowRecords = cache.stream().filter(record -> record.isTodo() && record.getCurrentOperatorId() == operatorId).toList();
        return new PageImpl<>(flowRecords);
    }

    @Override
    public Page<FlowRecord> findTimeoutTodoByOperatorId(long operatorId,PageRequest pageRequest) {
        List<FlowRecord> flowRecords = cache.stream().filter(record -> record.isTimeout() && record.isTodo() && record.getCurrentOperatorId() == operatorId).toList();
        return new PageImpl<>(flowRecords);
    }


    @Override
    public Page<FlowRecord> findPostponedTodoByOperatorId(long operatorId,PageRequest pageRequest) {
        List<FlowRecord> flowRecords = cache.stream().filter(record -> record.isPostponed() && record.isTodo() && record.getCurrentOperatorId() == operatorId).toList();
        return new PageImpl<>(flowRecords);
    }

    @Override
    public void finishFlowRecordByProcessId(String processId) {
        cache.stream()
                .filter(record -> record.getProcessId().equals(processId))
                .forEach(FlowRecord::finish);
    }

    @Override
    public void delete(List<FlowRecord> childrenRecords) {
        cache.removeAll(childrenRecords);
    }
}
