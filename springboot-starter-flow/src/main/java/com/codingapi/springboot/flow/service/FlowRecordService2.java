package com.codingapi.springboot.flow.service;

import com.codingapi.springboot.flow.domain.FlowNode;
import com.codingapi.springboot.flow.domain.FlowWork;
import com.codingapi.springboot.flow.record.FlowRecord;
import com.codingapi.springboot.flow.repository.FlowProcessRepository;
import com.codingapi.springboot.flow.repository.FlowRecordRepository;
import com.codingapi.springboot.flow.user.IFlowOperator;
import lombok.Getter;

/**
 * 流程记录服务（流程内部服务）
 */
class FlowRecordService2 {

    // constructor params
    private final long recordId;
    @Getter
    private final IFlowOperator currentOperator;

    // register repository
    final FlowRecordRepository flowRecordRepository;
    final FlowProcessRepository flowProcessRepository;

    // load Object
    @Getter
    private FlowWork flowWork;
    @Getter
    private FlowNode flowNode;
    @Getter
    private FlowRecord flowRecord;

    public FlowRecordService2(FlowRecordRepository flowRecordRepository,
                              FlowProcessRepository flowProcessRepository,
                              long recordId,
                              IFlowOperator currentOperator) {
        this.flowRecordRepository = flowRecordRepository;
        this.flowProcessRepository = flowProcessRepository;

        this.currentOperator = currentOperator;
        this.recordId = recordId;
    }



    /**
     *  校验流程记录是否已提交状态
     */
    public void verifyFlowRecordSubmitState() {
        flowRecord.submitStateVerify();
    }

    /**
     * 校验流程是否当前操作者可操作的
     */
    public void verifyFlowRecordCurrentOperator() {
        if(!currentOperator.isFlowManager()) {
            flowRecord.matcherOperator(currentOperator);
        }
    }

    /**
     *  校验流程是否已审批
     */
    public void verifyFlowRecordNotDone() {
        if (flowRecord.isDone()) {
            throw new IllegalArgumentException("flow record is done");
        }
    }


    /**
     *  校验流程是否已审批
     */
    public void verifyFlowRecordIsDone() {
        if (!flowRecord.isDone()) {
            throw new IllegalArgumentException("flow record is not done");
        }
    }



    /**
     *  校验流程是否未审批
     */
    public void verifyFlowRecordNotTodo() {
        if (flowRecord.isTodo()) {
            throw new IllegalArgumentException("flow record is todo");
        }
    }

    /**
     *  校验流程是未审批
     */
    public void verifyFlowRecordIsTodo() {
        if (!flowRecord.isTodo()) {
            throw new IllegalArgumentException("flow record is not todo");
        }
    }

    /**
     *  校验流程是否已完成
     */
    public void verifyFlowRecordIsFinish() {
        if (!flowRecord.isFinish()) {
            throw new IllegalArgumentException("flow record is not finish");
        }
    }

    /**
     *  校验流程是否已完成
     */
    public void verifyFlowRecordNotFinish() {
        if (flowRecord.isFinish()) {
            throw new IllegalArgumentException("flow record is finish");
        }
    }

    /**
     *  校验流程节点是否可编辑
     */
    public void verifyFlowNodeEditableState(boolean editable) {
        // 流程节点不可编辑时，不能保存
        if (flowNode.isEditable() == editable) {
            throw new IllegalArgumentException("flow node is not editable");
        }
    }


    /**
     *  校验转办人员不能是当前操作者
     */
    public void verifyTargetOperatorIsNotCurrentOperator(IFlowOperator targetOperator) {
        if(currentOperator.getUserId() == targetOperator.getUserId()){
            throw new IllegalArgumentException("current operator is target operator");
        }
    }


    /**
     *  获取流程记录对象
     */
    public void loadFlowRecord() {
        FlowRecord flowRecord = flowRecordRepository.getFlowRecordById(recordId);
        if (flowRecord == null) {
            throw new IllegalArgumentException("flow record not found");
        }
        this.flowRecord = flowRecord;
    }

    /**
     *  获取流程设计对象
     */
    public void loadFlowWork() {
        FlowWork flowWork = flowProcessRepository.getFlowWorkByProcessId(flowRecord.getProcessId());
        if (flowWork == null) {
            throw new IllegalArgumentException("flow work not found");
        }
        flowWork.enableValidate();
        this.flowWork = flowWork;
    }


    /**
     *  获取流程节点对象
     */
    public void loadFlowNode() {
        FlowNode flowNode = flowWork.getNodeByCode(flowRecord.getNodeCode());
        if (flowNode == null) {
            throw new IllegalArgumentException("flow node not found");
        }
        this.flowNode = flowNode;
    }

    /**
     *  标记流程为已读状态
     */
    public void flagReadFlowRecord() {
        if (currentOperator != null) {
            if(flowRecord.isOperator(currentOperator)) {
                if (!flowRecord.isRead()) {
                    flowRecord.read();
                    flowRecordRepository.update(flowRecord);
                }
            }
        }
    }

}
