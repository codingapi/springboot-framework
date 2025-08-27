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
    public static final int STATE_CIRCULATE = 9;
    // 保存
    public static final int STATE_SAVE = 10;
    // 删除
    public static final int STATE_DELETE = 11;
    // 退回
    public static final int STATE_BACK = 12;
    // 作废
    public static final int STATE_VOIDED = 13;
    // 停止
    public static final int STATE_STOP = 14;


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


    public boolean match(String matchKey) {
        return bindData.match(matchKey);
    }

    /**
     * 匹配类名
     * 当前bingData下的clazzName变成了普通的key字段了，推荐使用match(String matchKey)方法
     * @param clazz 类名
     * @return 是否匹配
     */
    @Deprecated
    public boolean match(Class<?> clazz) {
        return bindData.match(clazz.getName());
    }

    public <T> T toJavaObject(Class<T> clazz) {
        return bindData.toJavaObject(clazz);
    }

    public boolean isUrge() {
        return state == STATE_URGE;
    }

    public boolean isTodo() {
        return state == STATE_TODO;
    }

    public boolean isSave() {
        return state == STATE_SAVE;
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

    public boolean isDelete() {
        return state == STATE_DELETE;
    }

    public boolean isVoided() {
        return state == STATE_VOIDED;
    }

    public boolean isBack() {
        return state == STATE_BACK;
    }
}
