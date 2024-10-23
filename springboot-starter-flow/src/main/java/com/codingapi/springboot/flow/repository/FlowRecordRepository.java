package com.codingapi.springboot.flow.repository;

import com.codingapi.springboot.flow.record.FlowRecord;

import java.util.List;

public interface FlowRecordRepository {

    /**
     * 保存流程记录
     * @param records 流程记录
     */
    void save(List<FlowRecord> records);

    /**
     * 更新流程记录
     * @param flowRecord 流程记录
     */
    void update(FlowRecord flowRecord);

    /**
     * 根据ID查询流程记录
     * @param id 流程记录ID
     * @return FlowRecord
     */
    FlowRecord getFlowRecordById(long id);

    /**
     * 根据前置ID查询流程记录
     * @param preId 前置ID
     * @return List of FlowRecord
     */
    List<FlowRecord> findFlowRecordByPreId(long preId);




}
