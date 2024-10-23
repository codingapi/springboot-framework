package com.codingapi.springboot.flow.repository;

import com.codingapi.springboot.flow.record.FlowRecord;

import java.util.List;

public interface FlowRecordRepository {

    void save(List<FlowRecord> records);

    void update(FlowRecord flowRecord);

    FlowRecord getFlowRecordById(long id);

    List<FlowRecord> findAll();

    /**
     * 根据前置ID查询流程记录
     * @param preId 前置ID
     * @return List of FlowRecord
     */
    List<FlowRecord> findFlowRecordByPreId(long preId);




}
