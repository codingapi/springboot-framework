package com.codingapi.springboot.flow.repository;

import com.codingapi.springboot.flow.domain.FlowRecord;

import java.util.List;

public interface FlowRecordRepository {

    void save(FlowRecord flowRecord);

    /**
     * 根据流程id查询流程记录
     *
     * @param processId 流程id
     * @return 流程记录
     */
    List<FlowRecord> findAllFlowRecordByProcessId(long processId);


    /**
     * 根据父节点id查询子节点
     * @param parentId 父节点id
     * @return 子节点
     */
    List<FlowRecord> findChildrenFlowRecordByParentId(long parentId);


    /**
     * 查询用户下的所有流程记录
     *
     * @param operatorId 用户id
     * @return 流程记录
     */
    List<FlowRecord> findAllFlowRecordByOperatorId(long operatorId);

    /**
     * 查询用户下的待办流程记录
     *
     * @param operatorId 用户id
     * @return 流程记录
     */
    List<FlowRecord> findTodoFlowRecordByOperatorId(long operatorId);

    /**
     * 查询用户下的已办流程记录
     *
     * @param operatorId 用户id
     * @return 流程记录
     */
    List<FlowRecord> findDoneFlowRecordByOperatorId(long operatorId);

    /**
     * 删除流程记录
     * @param flowRecord 流程记录
     */
    void delete(FlowRecord flowRecord);

    /**
     * 根据id查询流程记录
     * @param id 流程记录id
     * @return 流程记录
     */
    FlowRecord getFlowRecordById(long id);
}
