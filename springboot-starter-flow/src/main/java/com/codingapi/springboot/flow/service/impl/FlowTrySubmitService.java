package com.codingapi.springboot.flow.service.impl;

import com.codingapi.springboot.flow.bind.BindDataSnapshot;
import com.codingapi.springboot.flow.bind.IBindData;
import com.codingapi.springboot.flow.domain.FlowNode;
import com.codingapi.springboot.flow.domain.FlowWork;
import com.codingapi.springboot.flow.domain.Opinion;
import com.codingapi.springboot.flow.em.FlowSourceDirection;
import com.codingapi.springboot.flow.pojo.FlowSubmitResult;
import com.codingapi.springboot.flow.record.FlowBackup;
import com.codingapi.springboot.flow.record.FlowProcess;
import com.codingapi.springboot.flow.record.FlowRecord;
import com.codingapi.springboot.flow.repository.*;
import com.codingapi.springboot.flow.service.FlowDirectionService;
import com.codingapi.springboot.flow.service.FlowNodeService;
import com.codingapi.springboot.flow.service.FlowRecordVerifyService;
import com.codingapi.springboot.flow.user.IFlowOperator;
import lombok.AllArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Transactional
@AllArgsConstructor
public class FlowTrySubmitService {

    private final FlowRecordRepository flowRecordRepository;
    private final FlowBindDataRepository flowBindDataRepository;
    private final FlowOperatorRepository flowOperatorRepository;
    private final FlowProcessRepository flowProcessRepository;
    private final FlowWorkRepository flowWorkRepository;
    private final FlowBackupRepository flowBackupRepository;


    /**
     * 尝试提交流程 （流程过程中）
     *
     * @param recordId        流程记录id
     * @param currentOperator 当前操作者
     * @param bindData        绑定数据
     * @param opinion         审批意见
     */
    public FlowSubmitResult trySubmitFlow(long recordId, IFlowOperator currentOperator, IBindData bindData, Opinion opinion) {

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

        // 获取流程记录对象  copy 流程数据防止影响原有数据
        FlowRecord flowRecord = flowRecordVerifyService.getFlowRecord().copy();
        FlowNode flowNode = flowRecordVerifyService.getFlowNode();
        FlowWork flowWork = flowRecordVerifyService.getFlowWork();

        return trySubmitFlow(flowWork, flowNode, flowRecord, currentOperator, bindData, opinion);
    }


    /**
     * 预提交流程数据查询
     *
     * @param flowWork        流程设计
     * @param flowNode        流程节点
     * @param flowRecord      流程记录
     * @param currentOperator 当前操作者
     * @param bindData        绑定数据
     * @param opinion         审批意见
     * @return FlowSubmitResult
     */
    private FlowSubmitResult trySubmitFlow(FlowWork flowWork, FlowNode flowNode, FlowRecord flowRecord, IFlowOperator currentOperator, IBindData bindData, Opinion opinion) {

        // 保存流程表单快照数据
        BindDataSnapshot snapshot = null;
        if (flowNode.isEditable()) {
            snapshot = new BindDataSnapshot(bindData);
        } else {
            snapshot = flowBindDataRepository.getBindDataSnapshotById(flowRecord.getSnapshotId());
        }

        // 审批方向判断服务
        FlowDirectionService flowDirectionService = new FlowDirectionService(flowNode, flowWork, opinion);

        // 加载流程审批方向
        flowDirectionService.loadFlowSourceDirection();
        // 验证审批方向
        flowDirectionService.verifyFlowSourceDirection();

        // 根据当前方向提交流程
        FlowSourceDirection flowSourceDirection = flowDirectionService.getFlowSourceDirection();
        flowRecord.submitRecord(currentOperator, snapshot, opinion, flowSourceDirection);

        // 与当前流程同级的流程记录
        List<FlowRecord> historyRecords;
        if (flowRecord.isStartRecord()) {
            historyRecords = new ArrayList<>();
        } else {
            // copy 流程数据防止影响原有数据
            historyRecords = flowRecordRepository.findFlowRecordByPreId(flowRecord.getPreId()).stream().map(FlowRecord::copy).collect(Collectors.toList());
            // 更新当前流程记录, 由于try测试过程中没有对数据落库，所以这里需要手动更新
            for(FlowRecord record : historyRecords){
                if(record.getId() == flowRecord.getId()){
                    record.submitRecord(currentOperator, snapshot, opinion, flowSourceDirection);
                }
            }
        }
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
        flowDirectionService.reloadFlowSourceDirection();

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

        FlowNode nextNode = flowNodeService.getNextNode();

        List<? extends IFlowOperator> operators = flowNodeService.loadNextNodeOperators();
        return new FlowSubmitResult(flowWork, nextNode, operators);
    }


    /**
     * 尝试提交流程 （发起流程）
     *
     * @param workCode        流程编码
     * @param currentOperator 当前操作者
     * @param bindData        绑定数据
     * @param opinion         审批意见
     */
    public FlowSubmitResult trySubmitFlow(String workCode, IFlowOperator currentOperator, IBindData bindData, Opinion opinion) {
        // 检测流程是否存在
        FlowWork flowWork = flowWorkRepository.getFlowWorkByCode(workCode);
        if (flowWork == null) {
            throw new IllegalArgumentException("flow work not found");
        }
        flowWork.verify();
        flowWork.enableValidate();

        // 流程数据备份
        FlowBackup flowBackup = flowBackupRepository.getFlowBackupByWorkIdAndVersion(flowWork.getId(), flowWork.getUpdateTime());
        if (flowBackup == null) {
            flowBackup = flowBackupRepository.backup(flowWork);
        }

        // 保存流程
        FlowProcess flowProcess = new FlowProcess(flowBackup.getId(), currentOperator);

        // 保存绑定数据
        BindDataSnapshot snapshot = new BindDataSnapshot(bindData);

        // 创建流程id
        String processId = flowProcess.getProcessId();

        // 获取开始节点
        FlowNode start = flowWork.getStartNode();
        if (start == null) {
            throw new IllegalArgumentException("start node not found");
        }
        // 设置开始流程的上一个流程id
        long preId = 0;

        List<FlowRecord> historyRecords = new ArrayList<>();

        FlowNodeService flowNodeService = new FlowNodeService(flowOperatorRepository,
                flowRecordRepository,
                snapshot,
                opinion,
                currentOperator,
                currentOperator,
                historyRecords,
                flowWork,
                null,
                processId,
                preId);

        flowNodeService.setNextNode(start);

        FlowRecord startRecord = null;

        // 创建待办记录
        List<FlowRecord> records = flowNodeService.createRecord();
        if (records.isEmpty()) {
            throw new IllegalArgumentException("flow record not found");
        } else {
            for (FlowRecord record : records) {
                record.updateOpinion(opinion);
                startRecord = record;
            }
        }

        // 检测流程是否结束
        if (flowNodeService.nextNodeIsOver()) {
            for (FlowRecord record : records) {
                record.submitRecord(currentOperator, snapshot, opinion, FlowSourceDirection.PASS);
                record.finish();
                startRecord = record;
            }
        }
        return this.trySubmitFlow(flowWork, start, startRecord, currentOperator, bindData, opinion);
    }
}
