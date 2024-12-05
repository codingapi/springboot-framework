package com.codingapi.springboot.flow.query;

import com.codingapi.springboot.flow.record.FlowRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

/**
 *  流程记录查询服务
 */
public interface FlowRecordQuery {


    Page<FlowRecord> findAll(PageRequest pageRequest);


    /**
     * 查看个人的未读与待办数据
     *
     * @param operatorId 操作人
     * @return 流程记录
     */
    Page<FlowRecord> findUnReadByOperatorId(long operatorId, PageRequest pageRequest);


    /**
     * 查看个人的未读与待办数据(指定流程)
     *
     * @param operatorId 操作人
     * @param workCode 流程编码
     * @return 流程记录
     */
    Page<FlowRecord> findUnReadByOperatorId(long operatorId,String workCode, PageRequest pageRequest);

    /**
     * 查看个人的待办数据
     *
     * @param operatorId 操作人
     * @return 流程记录
     */
    Page<FlowRecord> findTodoByOperatorId(long operatorId, PageRequest pageRequest);


    /**
     * 查看个人的待办数据(指定流程)
     *
     * @param operatorId 操作人
     * @param workCode 流程编码
     * @return 流程记录
     */
    Page<FlowRecord> findTodoByOperatorId(long operatorId,String workCode, PageRequest pageRequest);


    /**
     * 查看个人的已办数据
     * @param operatorId 操作人
     * @return 流程记录
     */
    Page<FlowRecord> findDoneByOperatorId(long operatorId, PageRequest pageRequest);


    /**
     * 查看个人的已办数据 (指定流程)
     * @param operatorId 操作人
     * @param workCode 流程编码
     * @return 流程记录
     */
    Page<FlowRecord> findDoneByOperatorId(long operatorId,String workCode, PageRequest pageRequest);

    /**
     * 查看个人的发起数据 （含待办与已办）
     * @param operatorId 操作人
     * @return 流程记录
     */
    Page<FlowRecord> findInitiatedByOperatorId(long operatorId, PageRequest pageRequest);


    /**
     * 查看个人的发起数据 （含待办与已办、指定流程）
     * @param operatorId 操作人
     * @param workCode 流程编码
     * @return 流程记录
     */
    Page<FlowRecord> findInitiatedByOperatorId(long operatorId,String workCode, PageRequest pageRequest);

    /**
     * 查看个人的超时的待办流程
     * @param operatorId 操作人
     * @return 流程记录
     */
    Page<FlowRecord> findTimeoutTodoByOperatorId(long operatorId, PageRequest pageRequest);

    /**
     * 查看个人的超时的待办流程 （指定流程）
     * @param operatorId 操作人
     * @param workCode 流程编码
     * @return 流程记录
     */
    Page<FlowRecord> findTimeoutTodoByOperatorId(long operatorId,String workCode, PageRequest pageRequest);

    /**
     * 查看个人的延期的待办流程
     * @param operatorId 操作人
     * @return 流程记录
     */
    Page<FlowRecord> findPostponedTodoByOperatorId(long operatorId, PageRequest pageRequest);


    /**
     * 查看个人的延期的待办流程
     * @param operatorId 操作人
     * @param workCode 流程编码
     * @return 流程记录
     */
    Page<FlowRecord> findPostponedTodoByOperatorId(long operatorId,String workCode, PageRequest pageRequest);

}
