package com.codingapi.springboot.flow.repository;

import com.codingapi.springboot.flow.record.FlowRecord;

import java.util.ArrayList;
import java.util.List;

public class FlowRecordRepositoryImpl implements FlowRecordRepository {

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
    public List<FlowRecord> findRecordByWorkIdAndOperatorId(long workId, long operatorId) {
        return cache.stream().filter(record ->
                record.getWorkId() == workId
                        && record.getCurrentOperatorId() == operatorId
        ).toList();
    }

    @Override
    public List<FlowRecord> findAll() {
        return cache;
    }
}
