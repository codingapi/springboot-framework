package com.codingapi.springboot.flow.event;

import com.codingapi.springboot.flow.bind.IBindData;
import com.codingapi.springboot.flow.domain.FlowWork;
import com.codingapi.springboot.flow.record.FlowRecord;
import com.codingapi.springboot.flow.user.IFlowOperator;
import com.codingapi.springboot.framework.event.ISyncEvent;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

/**
 * 流程审批事件
 */
@Slf4j
@Getter
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
    public static final int STATE_RECALL = 5;
    // 流程完成
    public static final int STATE_FINISH = 6;
    // 创建待办
    public static final int STATE_TODO = 7;
    // 催办
    public static final int STATE_URGE = 8;
    // 抄送
    public static final int STATE_CC = 9;


    private final int state;
    private final IFlowOperator operator;
    private final FlowRecord flowRecord;
    private final FlowWork flowWork;
    private final IBindData bindData;

    public FlowApprovalEvent(int state, FlowRecord flowRecord, IFlowOperator operator, FlowWork flowWork, IBindData bindData) {
        this.state = state;
        this.operator = operator;
        this.flowRecord = flowRecord;
        this.flowWork = flowWork;
        this.bindData = bindData;
        log.debug("FlowApprovalEvent:{}", this);
    }


    public boolean match(Class<?> bindDataClass) {
        return bindDataClass.isInstance(bindData);
    }

    public boolean isUrge() {
        return state == STATE_URGE;
    }

    public boolean isTodo() {
        return state == STATE_TODO;
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

    public boolean isRecall() {
        return state == STATE_RECALL;
    }

    public boolean isFinish() {
        return state == STATE_FINISH;
    }
}
