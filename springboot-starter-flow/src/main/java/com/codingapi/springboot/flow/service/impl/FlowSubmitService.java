package com.codingapi.springboot.flow.service.impl;

import com.codingapi.springboot.flow.bind.BindDataSnapshot;
import com.codingapi.springboot.flow.bind.IBindData;
import com.codingapi.springboot.flow.domain.FlowNode;
import com.codingapi.springboot.flow.domain.FlowWork;
import com.codingapi.springboot.flow.domain.Opinion;
import com.codingapi.springboot.flow.em.FlowSourceDirection;
import com.codingapi.springboot.flow.event.FlowApprovalEvent;
import com.codingapi.springboot.flow.pojo.FlowResult;
import com.codingapi.springboot.flow.record.FlowRecord;
import com.codingapi.springboot.flow.repository.*;
import com.codingapi.springboot.flow.service.FlowDirectionService;
import com.codingapi.springboot.flow.service.FlowNodeService;
import com.codingapi.springboot.flow.service.FlowRecordVerifyService;
import com.codingapi.springboot.flow.user.IFlowOperator;
import com.codingapi.springboot.framework.event.EventPusher;
import lombok.AllArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Transactional
@AllArgsConstructor
public class FlowSubmitService {

    private final FlowRecordRepository flowRecordRepository;
    private final FlowBindDataRepository flowBindDataRepository;
    private final FlowOperatorRepository flowOperatorRepository;
    private final FlowProcessRepository flowProcessRepository;

    /**
     * 提交流程
     *
     * @param recordId        流程记录id
     * @param currentOperator 当前操作者
     * @param bindData        绑定数据
     * @param opinion         审批意见
     */
    public FlowResult submitFlow(long recordId, IFlowOperator currentOperator, IBindData bindData, Opinion opinion) {

        FlowRecordVerifyService flowRecordVerifyService = new FlowRecordVerifyService(flowRecordRepository, flowProcessRepository, recordId, currentOperator);

        // 加载流程
        flowRecordVerifyService.loadFlowRecord();
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

        // 获取流程记录对象
        FlowRecord flowRecord = flowRecordVerifyService.getFlowRecord();
        FlowNode flowNode = flowRecordVerifyService.getFlowNode();
        FlowWork flowWork = flowRecordVerifyService.getFlowWork();


        // 保存流程表单快照数据
        BindDataSnapshot snapshot = null;
        if (flowNode.isEditable()) {
            snapshot = new BindDataSnapshot(bindData);
            flowBindDataRepository.save(snapshot);
        } else {
            snapshot = flowBindDataRepository.getBindDataSnapshotById(flowRecord.getSnapshotId());
        }

        // 审批方向判断服务
        FlowDirectionService flowDirectionService = new FlowDirectionService(flowRecordVerifyService.getFlowNode(), flowRecordVerifyService.getFlowWork(), opinion);

        // 加载流程审批方向
        flowDirectionService.loadFlowSourceDirection();
        // 验证审批方向
        flowDirectionService.verifyFlowSourceDirection();

        // 根据当前方向提交流程
        FlowSourceDirection flowSourceDirection = flowDirectionService.getFlowSourceDirection();
        flowRecord.submitRecord(currentOperator, snapshot, opinion, flowSourceDirection);
        flowRecordRepository.update(flowRecord);

        // 与当前流程同级的流程记录
        List<FlowRecord> historyRecords;
        if (flowRecord.isStartRecord()) {
            historyRecords = new ArrayList<>();
        } else {
            historyRecords = flowRecordRepository.findFlowRecordByPreId(flowRecord.getPreId());
        }
        flowDirectionService.bindHistoryRecords(historyRecords);

        // 判断流程是否结束（会签时需要所有人都通过）
        if (flowNode.isSign()) {
            boolean next = flowDirectionService.hasCurrentFlowNodeIsDone();
            if (next) {
                List<FlowRecord> todoRecords = historyRecords.stream().filter(FlowRecord::isTodo).toList();
                return new FlowResult(flowWork, todoRecords);
            }
        }

        // 非会签下，当有人提交以后，将所有未提交的流程都自动提交，然后再执行下一节点
        if (flowNode.isUnSign()) {
            for (FlowRecord record : historyRecords) {
                if (record.isTodo() && record.getId() != flowRecord.getId()) {
                    record.autoPass(currentOperator, snapshot);
                    flowRecordRepository.update(flowRecord);
                }
            }
        }

        // 根据所有提交意见，重新加载审批方向
        flowSourceDirection = flowDirectionService.reloadFlowSourceDirection();

        // 获取流程的发起者
        IFlowOperator createOperator = flowRecord.getCreateOperator();

        // 构建流程创建器
        FlowNodeService flowNodeService = new FlowNodeService(
                flowOperatorRepository,
                flowRecordRepository,
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

        List<FlowRecord> records = flowNodeService.createRecord();

        // 判断流程是否完成
        if (flowNodeService.nextNodeIsOver()) {
            flowRecord.submitRecord(currentOperator, snapshot, opinion, flowSourceDirection);
            flowRecord.finish();
            flowRecordRepository.update(flowRecord);
            flowRecordRepository.finishFlowRecordByProcessId(flowRecord.getProcessId());

            EventPusher.push(new FlowApprovalEvent(FlowApprovalEvent.STATE_FINISH,
                            flowRecord,
                            currentOperator,
                            flowWork,
                            snapshot.toBindData()),
                    true);
            if(!records.isEmpty()) {
                return new FlowResult(flowWork, records.get(0));
            }
            return new FlowResult(flowWork, flowRecord);
        }

        // 保存流程记录
        flowRecordRepository.save(records);

        // 推送审批事件消息
        int eventState = flowSourceDirection == FlowSourceDirection.PASS ? FlowApprovalEvent.STATE_PASS : FlowApprovalEvent.STATE_REJECT;
        EventPusher.push(new FlowApprovalEvent(eventState,
                        flowRecord,
                        currentOperator,
                        flowWork,
                        snapshot.toBindData()),
                true);

        // 推送待办事件消息
        for (FlowRecord record : records) {
            IFlowOperator pushOperator = record.getCurrentOperator();
            EventPusher.push(new FlowApprovalEvent(FlowApprovalEvent.STATE_TODO,
                            record,
                            pushOperator,
                            flowWork,
                            snapshot.toBindData()),
                    true);
        }

        return new FlowResult(flowWork, records);
    }
}
