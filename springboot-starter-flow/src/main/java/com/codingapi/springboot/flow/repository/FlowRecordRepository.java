package com.codingapi.springboot.flow.repository;

import com.codingapi.springboot.flow.record.FlowRecord;

import java.util.List;


/**
 *  流转记录数据仓库
 */
public interface FlowRecordRepository {

    /**
     * 保存流程记录
     *
     * @param records 流程记录
     */
    void save(List<FlowRecord> records);

    /**
     * 更新流程记录
     *
     * @param flowRecord 流程记录
     */
    void update(FlowRecord flowRecord);

    /**
     * 根据ID查询流程记录
     *
     * @param id 流程记录ID
     * @return FlowRecord
     */
    FlowRecord getFlowRecordById(long id);

    /**
     * 根据前置ID查询流程记录
     *
     * @param preId 前置ID
     * @return List of FlowRecord
     */
    List<FlowRecord> findFlowRecordByPreId(long preId);


    /**
     * 根据流程id查询流程记录
     * @param processId 流程id
     */
    List<FlowRecord> findFlowRecordByProcessId(String processId);


    /**
     * 查询所有未完成的流程记录
     * @param processId 流程id
     * @return List of FlowRecord
     */
    List<FlowRecord> findTodoFlowRecordByProcessId(String processId);

    /**
     * 根据流程id 修改所有的记录状态为已完成
     *
     * @param processId 流程id
     */
    void finishFlowRecordByProcessId(String processId);

    /**
     * 删除流程记录
     * @param childrenRecords 流程记录
     */
    void delete(List<FlowRecord> childrenRecords);
}
