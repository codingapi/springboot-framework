package com.codingapi.springboot.flow.record;

import com.codingapi.springboot.flow.user.IFlowOperator;
import com.codingapi.springboot.framework.utils.RandomGenerator;
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

    /**
     * 是否作废
     */
    private boolean voided;

    /**
     * 作废流程
     */
    public void voided(){
        this.voided = true;
    }


    public FlowProcess(long backupId, IFlowOperator createOperator) {
        this.processId = RandomGenerator.generateUUID();
        this.createTime = System.currentTimeMillis();
        this.backupId = backupId;
        this.createOperatorId = createOperator.getUserId();
        this.voided = false;
    }
}
