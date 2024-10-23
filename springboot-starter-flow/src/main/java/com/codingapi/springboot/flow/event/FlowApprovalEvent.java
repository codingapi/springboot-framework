package com.codingapi.springboot.flow.event;

import com.codingapi.springboot.framework.event.ISyncEvent;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

/**
 * 流程审批事件
 */
@Slf4j
@ToString
public class FlowApprovalEvent implements ISyncEvent {
    // 创建流程
    public static final int STATE_CREATE = 1;
    // 流程审批通过
    public static final int STATE_PASS = 2;
    // 流程审批拒绝
    public static final int STATE_REJECT = 3;
    // 流程转办
    public static final int STATE_TRANSFER = 4;
    // 流程撤销
    public static final int STATE_REVOKE = 5;
    // 流程完成
    public static final int STATE_FINISH = 6;

    private final int state;

    public FlowApprovalEvent(int state) {
        this.state = state;
        log.info("FlowApprovalEvent:{}", this);
    }

    public boolean isCreate() {
        return state == STATE_CREATE;
    }

    public boolean isPass() {
        return state == STATE_PASS;
    }

    public boolean isReject() {
        return state == STATE_REJECT;
    }

    public boolean isTransfer() {
        return state == STATE_TRANSFER;
    }

    public boolean isRevoke() {
        return state == STATE_REVOKE;
    }

    public boolean isFinish() {
        return state == STATE_FINISH;
    }
}