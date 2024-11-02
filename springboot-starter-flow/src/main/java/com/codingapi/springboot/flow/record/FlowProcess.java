package com.codingapi.springboot.flow.record;

import com.codingapi.springboot.flow.user.IFlowOperator;
import com.codingapi.springboot.flow.utils.RandomGenerator;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 流程process记录
 */
@Getter
@AllArgsConstructor
public class FlowProcess {

    /**
     * 流程id
     */
    private String processId;

    /**
     * 创建时间
     */
    private long createTime;

    /**
     * 流程的字节码
     */
    private long backupId;

    /**
     * 创建者id
     */
    private long createOperatorId;


    public FlowProcess(long backupId, IFlowOperator createOperator) {
        this.processId = RandomGenerator.generateUUID();
        this.createTime = System.currentTimeMillis();
        this.backupId = backupId;
        this.createOperatorId = createOperator.getUserId();
    }
}
