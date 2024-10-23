package com.codingapi.springboot.flow.query;

import com.codingapi.springboot.flow.record.FlowRecord;

import java.util.List;

public interface FlowRecordQuery {

    List<FlowRecord> findAll();

    /**
     * 查看个人的待办数据
     * @param operatorId 操作人
     * @return 流程
     */
    List<FlowRecord> findTodoByOperatorId(long operatorId);

}
