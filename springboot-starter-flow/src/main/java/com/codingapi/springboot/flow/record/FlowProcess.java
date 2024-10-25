package com.codingapi.springboot.flow.record;

import com.codingapi.springboot.flow.user.IFlowOperator;
import com.codingapi.springboot.flow.utils.IDGenerator;
import lombok.Getter;
import lombok.Setter;

/**
 * 流程process记录
 */
@Getter
public class FlowProcess {

    /**
     * 数据id
     */
    @Setter
    private long id;

    /**
     * 流程id
     */
    private String processId;

    /**
     * 流程的字节码
     */
    private long backupId;

    /**
     * 创建者id
     */
    private long createOperatorId;


    public FlowProcess(long backupId, IFlowOperator createOperator) {
        this.processId = IDGenerator.generate();
        this.backupId = backupId;
        this.createOperatorId = createOperator.getUserId();
    }
}
