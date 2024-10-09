package com.codingapi.springboot.flow.repository;

import com.codingapi.springboot.flow.domain.FlowRecord;

import java.util.List;

public interface FlowRecordRepository {

    /**
     * 保存记录
     */
    void save(FlowRecord flowRecord);

    /**
     * 根据流程id查询流程记录
     *
     * @param processId 流程id
     * @return 流程记录
     */
    List<FlowRecord> findAllFlowRecordByProcessId(long processId);


    /**
     * 根据上一节查询下一节点的审批列表
     * @param preRecordId 上一节点的id
     * @return 子节点
     */
    List<FlowRecord> findChildrenFlowRecordByParentId(long preRecordId);


    /**
     * 查询用户下的所有流程记录
     *
     * @param operatorId 用户id
     * @return 流程记录
     */
    List<FlowRecord> findAllFlowRecordByOperatorId(long operatorId);

    /**
     * 查询用户下的发起的流程记录
     *
     * @param operatorId 用户id
     * @return 流程记录
     */
    List<FlowRecord> findCreateFlowRecordByOperatorId(long operatorId);

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
