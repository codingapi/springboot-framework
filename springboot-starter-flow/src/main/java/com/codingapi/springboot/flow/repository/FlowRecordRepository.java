package com.codingapi.springboot.flow.repository;

import com.codingapi.springboot.flow.record.FlowRecord;

import java.util.List;

public interface FlowRecordRepository {


    void save(List<FlowRecord> records);

}
