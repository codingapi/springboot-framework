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
     * 流程的字节码
     */
    private FlowWork flowWork;

    /**
     * 创建时间
     */
    private long createTime;

    /**
     * 流程的版本号
     * 以流程的修改时间为准
     */
    private long workVersion;

    /**
     * 流程的设计id
     */
    private long workId;

    /**
     * 创建者id
     */
    private long createOperatorId;


    public FlowProcess(FlowWork flowWork, IFlowOperator createOperator) {
        this.id = IDGenerator.generate();
        this.flowWork = flowWork;
        this.workVersion = flowWork.getUpdateTime();
        this.workId = flowWork.getId();
        this.createOperatorId = createOperator.getUserId();
        this.createTime = System.currentTimeMillis();
    }
}
