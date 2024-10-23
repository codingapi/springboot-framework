package com.codingapi.springboot.flow.record;

import com.codingapi.springboot.flow.domain.FlowWork;
import com.codingapi.springboot.flow.user.IFlowOperator;
import com.codingapi.springboot.flow.utils.IDGenerator;
import lombok.Getter;

/**
 * 流程记录
 */
@Getter
public class FlowProcess {

    /**
     * 流程id
     */
    private String id;

    /**
     * 流程设计（可以删除schema数据存储）
     */
    private FlowWork flowWork;

    /**
     * 创建时间
     */
    private long createTime;

    /**
     * 创建者id
     */
    private long createOperatorId;


    public FlowProcess(FlowWork flowWork, IFlowOperator createOperator) {
        this.id = IDGenerator.generate();
        this.flowWork = flowWork;
        this.createOperatorId = createOperator.getUserId();
        this.createTime = System.currentTimeMillis();
    }
}
