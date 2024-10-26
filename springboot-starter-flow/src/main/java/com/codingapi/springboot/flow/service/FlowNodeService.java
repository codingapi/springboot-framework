package com.codingapi.springboot.flow.service;

import com.codingapi.springboot.flow.bind.BindDataSnapshot;
import com.codingapi.springboot.flow.bind.IBindData;
import com.codingapi.springboot.flow.domain.FlowNode;
import com.codingapi.springboot.flow.domain.FlowWork;
import com.codingapi.springboot.flow.domain.Opinion;
import com.codingapi.springboot.flow.em.FlowSourceDirection;
import com.codingapi.springboot.flow.record.FlowRecord;
import com.codingapi.springboot.flow.repository.FlowBindDataRepository;
import com.codingapi.springboot.flow.repository.FlowOperatorRepository;
import com.codingapi.springboot.flow.user.IFlowOperator;
import lombok.Getter;

import java.util.List;

class FlowNodeService {


    private final FlowOperatorRepository flowOperatorRepository;
    private final FlowBindDataRepository flowBindDataRepository;

    private final FlowRecordService2 flowRecordService2;
    private final String processId;

    private final FlowRecord flowRecord;
    private final FlowWork flowWork;
    private final FlowNode flowNode;
    private final IBindData bindData;
    private final Opinion opinion;
    private final IFlowOperator currentOperator;

    // load Object
    @Getter
    private FlowSourceDirection flowSourceDirection;
    @Getter
    private List<FlowRecord> childrenRecords;
    @Getter
    private List<FlowRecord> historyRecords;

    @Getter
    private BindDataSnapshot snapshot;

    public FlowNodeService(FlowOperatorRepository flowOperatorRepository,
                           FlowBindDataRepository flowBindDataRepository,
                           FlowRecordService2 flowRecordService2,
                           IBindData bindData,
                           Opinion opinion) {

        this.flowOperatorRepository = flowOperatorRepository;
        this.flowBindDataRepository = flowBindDataRepository;

        this.flowRecordService2 = flowRecordService2;
        this.bindData = bindData;
        this.opinion = opinion;
        this.currentOperator = flowRecordService2.getCurrentOperator();

        this.flowRecord = flowRecordService2.getFlowRecord();
        this.flowWork = flowRecordService2.getFlowWork();
        this.flowNode = flowRecordService2.getFlowNode();

        this.processId = flowRecord.getProcessId();

    }


    /**
     * 解析当前的审批方向
     */
    public void loadFlowSourceDirection() {
        if (opinion.isSuccess()) {
            flowSourceDirection = FlowSourceDirection.PASS;
        }
        if (opinion.isReject()) {
            flowSourceDirection = FlowSourceDirection.REJECT;
        }
    }

    /**
     * 提交流程记录
     */
    public void submitFlowRecord() {
        flowRecord.submitRecord(currentOperator, snapshot, opinion, flowSourceDirection);
        flowRecordService2.flowRecordRepository.update(flowRecord);
    }


    /**
     * 重新加载审批方向
     * 根据会签结果判断是否需要重新设置审批方向
     */
    public void reloadFlowSourceDirection() {
        if (flowNode.isSign()) {
            boolean allPass = historyRecords.stream().filter(item -> !item.isTransfer()).allMatch(FlowRecord::isPass);
            if (!allPass) {
                flowSourceDirection = FlowSourceDirection.REJECT;
            }
        }
    }


    /**
     * 判断当前流程节点是否已经完成，是否可以继续流转
     */
    public boolean hasCurrentFlowNodeIsDone() {
        // 会签处理流程
        if (flowNode.isSign()) {
            // 会签下所有人尚未提交时，不执行下一节点
            boolean allDone = historyRecords.stream().filter(item -> !item.isTransfer()).allMatch(FlowRecord::isDone);
            if (!allDone) {
                // 流程尚未审批结束直接退出
                return true;
            }
        }
        return false;
    }

    /**
     * 检测当前流程是否已经完成
     * 即流程已经进行到了最终节点且审批意见为同意
     */
    public boolean hasCurrentFlowIsFinish() {
        if (flowSourceDirection == FlowSourceDirection.PASS && flowNode.isOverNode()) {
            return true;
        }
        return false;
    }


    /**
     *  完成所有流程
     */
    public void finishFlow() {
        flowRecord.finish();
        flowRecord.submitRecord(currentOperator, snapshot, opinion, flowSourceDirection);
        flowRecordService2.flowRecordRepository.update(flowRecord);
        flowRecordService2.flowRecordRepository.finishFlowRecordByProcessId(processId);
    }


    /**
     * 保存流程表单数据
     */
    public void updateSnapshot() {
        BindDataSnapshot snapshot = new BindDataSnapshot(flowRecord.getSnapshotId(), bindData);
        flowBindDataRepository.update(snapshot);
    }


    /**
     * 若非会签模式，则再第一人提交以后其他的人的流程记录将自动通过提交
     */
    public void autoSubmitUnSignReload() {
        // 非会签处理流程
        if (flowNode.isUnSign()) {
            // 非会签下，默认其他将所有人未提交的流程，都自动提交然后再执行下一节点
            for (FlowRecord record : historyRecords) {
                if (record.isTodo() && record.getId() != flowRecord.getId()) {
                    record.autoPass(currentOperator, snapshot);
                    flowRecordService2.flowRecordRepository.update(flowRecord);
                }
            }
        }
    }


    /**
     * 加载快照数据
     */
    public void loadOrCreateSnapshot() {
        // 保存绑定数据
        if (flowNode.isEditable()) {
            snapshot = new BindDataSnapshot(bindData);
            flowBindDataRepository.save(snapshot);
        } else {
            snapshot = flowBindDataRepository.getBindDataSnapshotById(flowRecord.getSnapshotId());
        }
    }


    /**
     * 加载同级的审批记录
     * 即加载当前节点流程下产生的所有记录数据
     */
    public void loadHistoryRecords() {
        historyRecords = flowRecordService2.flowRecordRepository.findFlowRecordByPreId(flowRecord.getPreId());
    }


    /**
     * 更新流程记录
     */
    public void updateFlowRecord() {
        flowRecord.setOpinion(opinion);
        flowRecordService2.flowRecordRepository.update(flowRecord);
    }


    public void loadChildrenRecords() {
        childrenRecords = flowRecordService2.flowRecordRepository.findFlowRecordByPreId(flowRecord.getId());
    }


    public void verifyChildrenRecordsIsEmpty() {
        if (!childrenRecords.isEmpty()) {
            throw new IllegalArgumentException("flow node is done");
        }
    }


    public void verifyFlowSourceDirection() {
        if (flowSourceDirection == null) {
            throw new IllegalArgumentException("flow source direction is null");
        }
        if (flowNode.isStartNode() && flowSourceDirection == FlowSourceDirection.REJECT) {
            throw new IllegalArgumentException("flow node is start node");
        }
    }
}
