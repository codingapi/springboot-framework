package com.codingapi.springboot.flow.service.impl;

import com.codingapi.springboot.flow.bind.BindDataSnapshot;
import com.codingapi.springboot.flow.bind.IBindData;
import com.codingapi.springboot.flow.domain.FlowNode;
import com.codingapi.springboot.flow.domain.FlowWork;
import com.codingapi.springboot.flow.domain.Opinion;
import com.codingapi.springboot.flow.em.FlowSourceDirection;
import com.codingapi.springboot.flow.event.FlowApprovalEvent;
import com.codingapi.springboot.flow.pojo.FlowResult;
import com.codingapi.springboot.flow.pojo.FlowSubmitResult;
import com.codingapi.springboot.flow.record.FlowRecord;
import com.codingapi.springboot.flow.repository.FlowBindDataRepository;
import com.codingapi.springboot.flow.repository.FlowRecordRepository;
import com.codingapi.springboot.flow.service.FlowDirectionService;
import com.codingapi.springboot.flow.service.FlowNodeService;
import com.codingapi.springboot.flow.service.FlowRecordVerifyService;
import com.codingapi.springboot.flow.service.FlowServiceRepositoryHolder;
import com.codingapi.springboot.flow.user.IFlowOperator;
import com.codingapi.springboot.framework.event.EventPusher;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Transactional
public class FlowSubmitService {


    private final IFlowOperator currentOperator;
    private final IBindData bindData;
    private final Opinion opinion;
    private final FlowServiceRepositoryHolder flowServiceRepositoryHolder;
    private final FlowRecordVerifyService flowRecordVerifyService;


    private FlowRecord flowRecord;
    private FlowWork flowWork;
    private FlowNode flowNode;
    private FlowNode nextNode;


    private BindDataSnapshot snapshot;
    private FlowNodeService flowNodeService;
    private FlowDirectionService flowDirectionService;
    private FlowSourceDirection flowSourceDirection;


    public FlowSubmitService(long recordId,
                             IFlowOperator currentOperator,
                             IBindData bindData,
                             Opinion opinion,
                             FlowServiceRepositoryHolder flowServiceRepositoryHolder) {
        this.flowServiceRepositoryHolder = flowServiceRepositoryHolder;
        this.currentOperator = currentOperator;
        this.bindData = bindData;
        this.opinion = opinion;
        this.flowRecordVerifyService = new FlowRecordVerifyService(
                flowServiceRepositoryHolder.getFlowWorkRepository(),
                flowServiceRepositoryHolder.getFlowRecordRepository(),
                flowServiceRepositoryHolder.getFlowProcessRepository(),
                recordId,
                currentOperator);
    }


    public FlowSubmitService(FlowRecord flowRecord,
                             FlowWork flowWork,
                             IFlowOperator currentOperator,
                             IBindData bindData,
                             Opinion opinion,
                             FlowServiceRepositoryHolder flowServiceRepositoryHolder) {
        this.flowWork = flowWork;
        this.flowServiceRepositoryHolder = flowServiceRepositoryHolder;
        this.currentOperator = currentOperator;
        this.bindData = bindData;
        this.opinion = opinion;
        this.flowRecordVerifyService = new FlowRecordVerifyService(
                flowServiceRepositoryHolder.getFlowWorkRepository(),
                flowServiceRepositoryHolder.getFlowRecordRepository(),
                flowServiceRepositoryHolder.getFlowProcessRepository(),
                flowRecord,
                flowWork,
                currentOperator);
    }


    // 加载流程
    private void loadFlow(boolean testSubmit) {
        // 验证流程的提交状态
        flowRecordVerifyService.verifyFlowRecordSubmitState();
        // 验证当前操作者
        flowRecordVerifyService.verifyFlowRecordCurrentOperator();

        // 加载流程设计
        flowRecordVerifyService.loadFlowWork();
        // 加载流程节点
        flowRecordVerifyService.loadFlowNode();
        // 验证没有子流程
        flowRecordVerifyService.verifyChildrenRecordsIsEmpty();

        if (testSubmit) {
            this.flowRecord = flowRecordVerifyService.getFlowRecord().copy();
        } else {
            this.flowRecord = flowRecordVerifyService.getFlowRecord();
        }
        this.flowNode = flowRecordVerifyService.getFlowNode();
        this.flowWork = flowRecordVerifyService.getFlowWork();
    }

    // 保存流程表单快照数据
    private void saveSnapshot(boolean testSubmit) {
        FlowBindDataRepository flowBindDataRepository = flowServiceRepositoryHolder.getFlowBindDataRepository();
        if (flowNode.isEditable()) {
            snapshot = new BindDataSnapshot(bindData);
            if (!testSubmit) {
                flowBindDataRepository.save(snapshot);
            }
        } else {
            snapshot = flowBindDataRepository.getBindDataSnapshotById(flowRecord.getSnapshotId());
        }
    }

    // 加载流程审批方向
    private void loadFlowDirection() {
        // 审批方向判断服务
        flowDirectionService = new FlowDirectionService(flowRecordVerifyService.getFlowNode(), flowRecordVerifyService.getFlowWork(), opinion);

        // 加载流程审批方向
        flowDirectionService.loadFlowSourceDirection();
        // 验证审批方向
        flowDirectionService.verifyFlowSourceDirection();

        flowSourceDirection = flowDirectionService.getFlowSourceDirection();
    }


    // 与当前流程同级的流程记录
    private List<FlowRecord> loadHistoryRecords(boolean testSubmit) {
        FlowRecordRepository flowRecordRepository = flowServiceRepositoryHolder.getFlowRecordRepository();
        // 与当前流程同级的流程记录
        List<FlowRecord> historyRecords;
        if (flowRecord.isStartRecord()) {
            historyRecords = new ArrayList<>();
        } else {
            if (testSubmit) {
                // copy 流程数据防止影响原有数据
                historyRecords = flowRecordRepository.findFlowRecordByPreId(flowRecord.getPreId()).stream().map(FlowRecord::copy).collect(Collectors.toList());
                // 更新当前流程记录, 由于try测试过程中没有对数据落库，所以这里需要手动更新
                for (FlowRecord record : historyRecords) {
                    if (record.getId() == flowRecord.getId()) {
                        record.submitRecord(currentOperator, snapshot, opinion, flowSourceDirection);
                    }
                }

            } else {
                historyRecords = flowRecordRepository.findFlowRecordByPreId(flowRecord.getPreId());
            }
        }
        return historyRecords;
    }


    // 保存流程记录
    private void saveFlowRecord(FlowRecord flowRecord) {
        FlowRecordRepository flowRecordRepository = flowServiceRepositoryHolder.getFlowRecordRepository();
        flowRecordRepository.update(flowRecord);
    }


    // 生成下一节点的流程记录
    private void loadNextNode(List<FlowRecord> historyRecords) {
        // 获取流程的发起者
        IFlowOperator createOperator = flowRecord.getCreateOperator();

        // 构建流程创建器
        flowNodeService = new FlowNodeService(
                flowServiceRepositoryHolder.getFlowOperatorRepository(),
                flowServiceRepositoryHolder.getFlowRecordRepository(),
                snapshot,
                opinion,
                createOperator,
                currentOperator,
                historyRecords,
                flowWork,
                flowRecord,
                flowRecord.getProcessId(),
                flowRecord.getId()
        );

        // 审批通过并进入下一节点
        if (flowDirectionService.isPassRecord()) {
            flowNodeService.loadNextPassNode(flowNode);
            // 审批拒绝返回上一节点
        } else if (flowDirectionService.isDefaultBackRecord()) {
            flowNodeService.loadDefaultBackNode(flowRecord.getPreId());
        } else {
            // 审批拒绝，并且自定了返回节点
            flowNodeService.loadCustomBackNode(flowNode, flowRecord.getPreId());
        }
        this.nextNode = flowNodeService.getNextNode();
    }


    // 更新流程记录
    private void updateFinishFlowRecord() {
        flowServiceRepositoryHolder.getFlowRecordRepository().finishFlowRecordByProcessId(flowRecord.getProcessId());
    }

    // 保存流程记录
    private void saveNextFlowRecords(List<FlowRecord> flowRecords) {
        flowServiceRepositoryHolder.getFlowRecordRepository().save(flowRecords);
    }

    // 推送审批事件消息
    private void pushEvent(FlowRecord flowRecord, int eventState) {
        EventPusher.push(new FlowApprovalEvent(eventState,
                flowRecord,
                flowRecord.getCurrentOperator(),
                flowWork,
                snapshot.toBindData()
        ), true);
    }


    /**
     * 提交流程 根据流程的是否跳过相同审批人来判断是否需要继续提交
     *
     * @return 流程结果
     */
    public FlowResult submitFlow() {
        FlowResult flowResult = this.submitCurrentFlow();
        if (this.isSkipIfSameApprover() && !flowResult.isOver()) {
            List<FlowRecord> flowRecords = flowResult.matchRecordByOperator(currentOperator);
            FlowResult result = flowResult;
            if (!flowRecords.isEmpty()) {
                for (FlowRecord flowRecord : flowRecords) {
                    FlowSubmitService flowSubmitService = new FlowSubmitService(flowRecord.getId(), currentOperator, bindData, opinion, flowServiceRepositoryHolder);
                    result = flowSubmitService.submitFlow();
                }
            }
            return result;
        } else {
            return flowResult;
        }
    }

    /**
     * 提交当前流程
     *
     * @return 流程结果
     */
    private FlowResult submitCurrentFlow() {
        // 加载流程信息
        this.loadFlow(false);

        // 保存流程表单快照数据
        this.saveSnapshot(false);

        // 审批方向判断服务
        this.loadFlowDirection();

        // 提交流程记录
        flowRecord.submitRecord(currentOperator, snapshot, opinion, flowSourceDirection);
        this.saveFlowRecord(flowRecord);

        // 与当前流程同级的流程记录
        List<FlowRecord> historyRecords = this.loadHistoryRecords(false);
        flowDirectionService.bindHistoryRecords(historyRecords);

        // 判断流程是否结束（会签时需要所有人都通过）
        if (flowNode.isSign()) {
            boolean isDone = flowDirectionService.hasCurrentFlowNodeIsDone();
            if (!isDone) {
                List<FlowRecord> todoRecords = historyRecords.stream().filter(FlowRecord::isTodo).collect(Collectors.toList());
                return new FlowResult(flowWork, todoRecords);
            }
        }

        // 非会签下，当有人提交以后，将所有未提交的流程都自动提交，然后再执行下一节点
        if (flowNode.isUnSign()) {
            for (FlowRecord record : historyRecords) {
                if (record.isTodo() && record.getId() != flowRecord.getId()) {
                    record.autoPass(currentOperator, snapshot);
                    FlowRecordRepository flowRecordRepository = flowServiceRepositoryHolder.getFlowRecordRepository();
                    flowRecordRepository.update(flowRecord);
                }
            }
        }

        // 根据所有提交意见，重新加载审批方向
        flowSourceDirection = flowDirectionService.reloadFlowSourceDirection();

        this.loadNextNode(historyRecords);

        // 生成下一节点的流程记录
        List<FlowRecord> nextRecords = flowNodeService.createRecord();

        // 判断流程是否完成
        if (flowNodeService.nextNodeIsOver()) {
            flowRecord.submitRecord(currentOperator, snapshot, opinion, flowSourceDirection);
            flowRecord.finish();
            this.saveFlowRecord(flowRecord);
            this.updateFinishFlowRecord();

            this.pushEvent(flowRecord, FlowApprovalEvent.STATE_FINISH);

            if (!nextRecords.isEmpty()) {
                return new FlowResult(flowWork, nextRecords.get(0));
            }
            return new FlowResult(flowWork, flowRecord);
        }

        // 保存流程记录
        this.saveNextFlowRecords(nextRecords);

        // 推送审批事件消息
        int eventState = flowSourceDirection == FlowSourceDirection.PASS ? FlowApprovalEvent.STATE_PASS : FlowApprovalEvent.STATE_REJECT;
        this.pushEvent(flowRecord, eventState);

        // 推送待办事件消息
        for (FlowRecord record : nextRecords) {
            if(record.isTodo()) {
                this.pushEvent(record, FlowApprovalEvent.STATE_TODO);
            }
        }

        return new FlowResult(flowWork, nextRecords);
    }

    /**
     * 提交流程
     **/
    public FlowSubmitResult trySubmitFlow() {
        // 加载流程信息
        this.loadFlow(true);

        // 保存流程表单快照数据
        this.saveSnapshot(true);

        // 审批方向判断服务
        this.loadFlowDirection();

        // 提交流程记录
        flowRecord.submitRecord(currentOperator, snapshot, opinion, flowSourceDirection);

        // 与当前流程同级的流程记录
        List<FlowRecord> historyRecords = this.loadHistoryRecords(true);
        flowDirectionService.bindHistoryRecords(historyRecords);

        // 判断流程是否结束（会签时需要所有人都通过）
        if (flowNode.isSign()) {
            boolean isDone = flowDirectionService.hasCurrentFlowNodeIsDone();
            if (!isDone) {
                List<FlowRecord> todoRecords = historyRecords.stream().filter(FlowRecord::isTodo).collect(Collectors.toList());
                return new FlowSubmitResult(flowWork, flowNode, todoRecords.stream().map(FlowRecord::getCurrentOperator).collect(Collectors.toList()));
            }
        }

        // 非会签下，当有人提交以后，将所有未提交的流程都自动提交，然后再执行下一节点
        if (flowNode.isUnSign()) {
            for (FlowRecord record : historyRecords) {
                if (record.isTodo() && record.getId() != flowRecord.getId()) {
                    record.autoPass(currentOperator, snapshot);
                }
            }
        }

        // 根据所有提交意见，重新加载审批方向
        flowSourceDirection = flowDirectionService.reloadFlowSourceDirection();

        this.loadNextNode(historyRecords);

        while (nextNode.isCirculate()){
            flowNodeService.skipCirculate();
            this.nextNode = flowNodeService.getNextNode();
        }

        List<? extends IFlowOperator> operators = flowNodeService.loadNextNodeOperators();
        return new FlowSubmitResult(flowWork, nextNode, operators);
    }


    // 是否跳过相同审批人
    public boolean isSkipIfSameApprover() {
        return flowWork.isSkipIfSameApprover() && !nextNode.isOverNode();
    }
}
