package com.codingapi.springboot.flow.service;

import com.codingapi.springboot.flow.bind.BindDataSnapshot;
import com.codingapi.springboot.flow.bind.IBindData;
import com.codingapi.springboot.flow.content.FlowContent;
import com.codingapi.springboot.flow.domain.FlowNode;
import com.codingapi.springboot.flow.domain.FlowWork;
import com.codingapi.springboot.flow.domain.Opinion;
import com.codingapi.springboot.flow.em.FlowSourceDirection;
import com.codingapi.springboot.flow.event.FlowApprovalEvent;
import com.codingapi.springboot.flow.pojo.FlowDetail;
import com.codingapi.springboot.flow.record.FlowBackup;
import com.codingapi.springboot.flow.record.FlowProcess;
import com.codingapi.springboot.flow.record.FlowRecord;
import com.codingapi.springboot.flow.repository.*;
import com.codingapi.springboot.flow.user.IFlowOperator;
import com.codingapi.springboot.framework.event.EventPusher;
import lombok.AllArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;


/**
 * 流程服务
 */
@Transactional
@AllArgsConstructor
public class FlowService {

    private final FlowWorkRepository flowWorkRepository;
    private final FlowRecordRepository flowRecordRepository;
    private final FlowBindDataRepository flowBindDataRepository;
    private final FlowOperatorRepository flowOperatorRepository;
    private final FlowProcessRepository flowProcessRepository;
    private final FlowBackupRepository flowBackupRepository;


    /**
     * 流程详情
     *
     * @param recordId 流程记录id
     * @return 流程详情
     */
    public FlowDetail detail(long recordId) {
        return detail(recordId, null);
    }

    /**
     * 延期待办
     *
     * @param recordId        流程记录id
     * @param currentOperator 当前操作者
     * @param time            延期时间
     */
    public void postponed(long recordId, IFlowOperator currentOperator, long time) {
        FlowRecordService flowRecordService = new FlowRecordService(flowRecordRepository,
                flowProcessRepository,
                recordId, currentOperator);

        flowRecordService.loadFlowRecord();
        flowRecordService.verifyFlowRecordSubmitState();
        flowRecordService.verifyFlowRecordCurrentOperator();
        flowRecordService.loadFlowWork();
        flowRecordService.verifyFlowRecordNotFinish();
        flowRecordService.verifyFlowRecordNotDone();

        FlowRecord flowRecord = flowRecordService.getFlowRecord();
        FlowWork flowWork = flowRecordService.getFlowWork();

        flowRecord.postponedTime(flowWork.getPostponedMax(), time);
        flowRecordRepository.update(flowRecord);
    }

    /**
     * 催办流程
     *
     * @param recordId        流程记录id
     * @param currentOperator 当前操作者
     */
    public void urge(long recordId, IFlowOperator currentOperator) {
        FlowRecordService flowRecordService = new FlowRecordService(flowRecordRepository,
                flowProcessRepository,
                recordId, currentOperator);
        flowRecordService.loadFlowRecord();
        flowRecordService.loadFlowWork();
        flowRecordService.verifyFlowRecordIsDone();

        FlowRecord flowRecord = flowRecordService.getFlowRecord();
        FlowWork flowWork = flowRecordService.getFlowWork();

        List<FlowRecord> todoRecords = flowRecordRepository.findTodoFlowRecordByProcessId(flowRecord.getProcessId());

        // 推送催办消息
        for (FlowRecord record : todoRecords) {
            IFlowOperator pushOperator = flowOperatorRepository.getFlowOperatorById(record.getCurrentOperatorId());
            EventPusher.push(new FlowApprovalEvent(FlowApprovalEvent.STATE_URGE, record, pushOperator, flowWork));
        }

    }

    /**
     * 流程详情
     * 如果传递了currentOperator为流程的审批者时，在查看详情的时候可以将流程记录标记为已读
     *
     * @param recordId        流程记录id
     * @param currentOperator 当前操作者
     */
    public FlowDetail detail(long recordId, IFlowOperator currentOperator) {

        FlowRecordService flowRecordService = new FlowRecordService(flowRecordRepository,
                flowProcessRepository,
                recordId, currentOperator);

        flowRecordService.loadFlowRecord();
        flowRecordService.setFlowRecordRead();
        flowRecordService.loadFlowWork();

        FlowRecord flowRecord = flowRecordService.getFlowRecord();
        FlowWork flowWork = flowRecordService.getFlowWork();


        BindDataSnapshot snapshot = flowBindDataRepository.getBindDataSnapshotById(flowRecord.getSnapshotId());
        List<FlowRecord> flowRecords = flowRecordRepository.findFlowRecordByProcessId(flowRecord.getProcessId());

        // 获取所有的操作者
        List<Long> operatorIds = new ArrayList<>();
        for (FlowRecord record : flowRecords) {
            if (!operatorIds.contains(record.getCurrentOperatorId())) {
                operatorIds.add(record.getCurrentOperatorId());
            }
            if (!operatorIds.contains(record.getCreateOperatorId())) {
                operatorIds.add(record.getCreateOperatorId());
            }
        }

        List<? extends IFlowOperator> operators = flowOperatorRepository.findByIds(operatorIds);
        return new FlowDetail(flowRecord, snapshot, flowWork, flowRecords, operators);
    }


    /**
     * 干预流程
     *
     * @param recordId        流程记录id
     * @param currentOperator 当前操作者
     * @param bindData        绑定数据
     * @param opinion         审批意见
     */
    public void interfere(long recordId, IFlowOperator currentOperator, IBindData bindData, Opinion opinion) {
        if (!currentOperator.isFlowManager()) {
            throw new IllegalArgumentException("current operator is not flow manager");
        }
        this.submitFlow(recordId, currentOperator, bindData, opinion);
    }


    /**
     * 转办流程
     *
     * @param recordId        流程记录id
     * @param currentOperator 当前操作者
     * @param targetOperator  转办操作者
     * @param bindData        绑定数据
     * @param advice          转办意见
     */
    public void transfer(long recordId, IFlowOperator currentOperator, IFlowOperator targetOperator, IBindData bindData, String advice) {

        FlowRecordService flowRecordService = new FlowRecordService(flowRecordRepository,
                flowProcessRepository,
                recordId, currentOperator);

        flowRecordService.loadFlowRecord();
        flowRecordService.verifyFlowRecordSubmitState();
        flowRecordService.verifyFlowRecordCurrentOperator();
        flowRecordService.verifyTargetOperatorIsNotCurrentOperator(targetOperator);

        flowRecordService.loadFlowWork();
        flowRecordService.loadFlowNode();

        flowRecordService.verifyFlowRecordIsTodo();

        FlowRecord flowRecord = flowRecordService.getFlowRecord();
        FlowWork flowWork = flowRecordService.getFlowWork();
        FlowNode flowNode = flowRecordService.getFlowNode();


        // 保存绑定数据
        BindDataSnapshot snapshot = new BindDataSnapshot(bindData);
        flowBindDataRepository.save(snapshot);

        // 构建审批意见
        Opinion opinion = Opinion.transfer(advice);

        // 设置自己的流程状态为转办已完成
        flowRecord.transfer(currentOperator, snapshot, opinion);
        flowRecordRepository.update(flowRecord);

        // 获取创建者
        IFlowOperator createOperator = flowOperatorRepository.getFlowOperatorById(flowRecord.getCreateOperatorId());

        // 与当前流程同级的流程记录
        List<FlowRecord> historyRecords = flowRecordRepository.findFlowRecordByPreId(flowRecord.getPreId());


        // 创建新的待办标题
        FlowContent content = new FlowContent(flowWork, flowNode, createOperator, targetOperator, snapshot.toBindData(), opinion, historyRecords);
        String generateTitle = flowNode.generateTitle(content);

        // 创建转办记录
        FlowRecord transferRecord = flowRecord.copy();
        transferRecord.transferToTodo(generateTitle, targetOperator);
        flowRecordRepository.save(List.of(transferRecord));

        // 推送转办消息
        EventPusher.push(new FlowApprovalEvent(FlowApprovalEvent.STATE_TRANSFER, flowRecord, currentOperator, flowWork));

        // 推送待办消息
        EventPusher.push(new FlowApprovalEvent(FlowApprovalEvent.STATE_TODO, transferRecord, targetOperator, flowWork));
    }


    /**
     * 保存流程
     *
     * @param recordId        流程记录id
     * @param currentOperator 当前操作者
     * @param bindData        绑定数据
     * @param advice          审批意见
     */
    public void save(long recordId, IFlowOperator currentOperator, IBindData bindData, String advice) {
        FlowRecordService flowRecordService = new FlowRecordService(flowRecordRepository,
                flowProcessRepository,
                recordId, currentOperator);
        flowRecordService.loadFlowRecord();
        flowRecordService.verifyFlowRecordSubmitState();
        flowRecordService.verifyFlowRecordCurrentOperator();
        flowRecordService.loadFlowWork();
        flowRecordService.loadFlowNode();
        flowRecordService.verifyFlowNodeEditableState(false);

        Opinion opinion = Opinion.save(advice);
        FlowRecord flowRecord = flowRecordService.getFlowRecord();
        BindDataSnapshot snapshot = new BindDataSnapshot(flowRecord.getSnapshotId(), bindData);
        flowBindDataRepository.update(snapshot);

        flowRecord.setOpinion(opinion);
        flowRecordService.flowRecordRepository.update(flowRecord);

    }


    /**
     * 发起流程 （不自动提交到下一节点）
     *
     * @param workId   流程id
     * @param operator 操作者
     * @param bindData 绑定数据
     * @param advice   审批意见
     */
    public void startFlow(long workId, IFlowOperator operator, IBindData bindData, String advice) {
        // 检测流程是否存在
        FlowWork flowWork = flowWorkRepository.getFlowWorkById(workId);
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
        FlowProcess flowProcess = new FlowProcess(flowBackup.getId(), operator);
        flowProcessRepository.save(flowProcess);

        // 保存绑定数据
        BindDataSnapshot snapshot = new BindDataSnapshot(bindData);
        flowBindDataRepository.save(snapshot);

        // 创建流程id
        String processId = flowProcess.getProcessId();

        // 构建审批意见
        Opinion opinion = Opinion.pass(advice);

        // 获取开始节点
        FlowNode start = flowWork.getStartNode();
        if (start == null) {
            throw new IllegalArgumentException("start node not found");
        }
        // 设置开始流程的上一个流程id
        long preId = 0;

        FlowRecordBuilderService flowRecordBuilderService = new FlowRecordBuilderService(
                flowOperatorRepository,
                flowRecordRepository,
                snapshot,
                opinion,
                operator,
                operator,
                new ArrayList<>(),
                flowWork,
                processId,
                preId
        );

        // 创建待办记录
        List<FlowRecord> records = flowRecordBuilderService.createRecord(start, operator);
        if (records.isEmpty()) {
            throw new IllegalArgumentException("flow record not found");
        }

        // 保存流程记录
        flowRecordRepository.save(records);

        // 推送事件消息
        for (FlowRecord record : records) {
            EventPusher.push(new FlowApprovalEvent(FlowApprovalEvent.STATE_CREATE, record, operator, flowWork));
            EventPusher.push(new FlowApprovalEvent(FlowApprovalEvent.STATE_TODO, record, operator, flowWork));
        }

    }

    /**
     * 提交流程
     *
     * @param recordId        流程记录id
     * @param currentOperator 当前操作者
     * @param bindData        绑定数据
     * @param opinion         审批意见
     */
    public void submitFlow(long recordId, IFlowOperator currentOperator, IBindData bindData, Opinion opinion) {

        FlowRecordService flowRecordService = new FlowRecordService(flowRecordRepository, flowProcessRepository, recordId, currentOperator);

        // 加载流程
        flowRecordService.loadFlowRecord();
        // 验证流程的提交状态
        flowRecordService.verifyFlowRecordSubmitState();
        // 验证当前操作者
        flowRecordService.verifyFlowRecordCurrentOperator();
        // 加载流程设计
        flowRecordService.loadFlowWork();
        // 加载流程节点
        flowRecordService.loadFlowNode();
        // 验证没有子流程
        flowRecordService.verifyChildrenRecordsIsEmpty();

        // 获取流程记录对象
        FlowRecord flowRecord = flowRecordService.getFlowRecord();
        FlowNode flowNode = flowRecordService.getFlowNode();
        FlowWork flowWork = flowRecordService.getFlowWork();


        // 保存流程表单快照数据
        BindDataSnapshot snapshot = null;
        if (flowNode.isEditable()) {
            snapshot = new BindDataSnapshot(bindData);
            flowBindDataRepository.save(snapshot);
        } else {
            snapshot = flowBindDataRepository.getBindDataSnapshotById(flowRecord.getSnapshotId());
        }

        // 审批方向判断服务
        FlowDirectionService flowDirectionService = new FlowDirectionService(flowRecordService.getFlowNode(), flowRecordService.getFlowWork(), opinion);

        // 加载流程审批方向
        flowDirectionService.loadFlowSourceDirection();
        // 验证审批方向
        flowDirectionService.verifyFlowSourceDirection();

        // 根据当前方向提交流程
        FlowSourceDirection flowSourceDirection = flowDirectionService.getFlowSourceDirection();
        flowRecord.submitRecord(currentOperator, snapshot, opinion, flowSourceDirection);
        flowRecordRepository.update(flowRecord);

        // 获取与当前流程同级的流程记录
        List<FlowRecord> historyRecords = flowRecordRepository.findFlowRecordByPreId(flowRecord.getPreId());
        flowDirectionService.bindHistoryRecords(historyRecords);

        // 判断流程是否结束（会签时需要所有人都通过）
        if (flowNode.isSign()) {
            boolean next = flowDirectionService.hasCurrentFlowNodeIsDone();
            if (next) {
                return;
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


        // 判断流程是否完成
        if (flowDirectionService.hasCurrentFlowIsFinish()) {
            flowRecord.finish();
            flowRecord.submitRecord(currentOperator, snapshot, opinion, flowSourceDirection);
            flowRecordRepository.update(flowRecord);
            flowRecordRepository.finishFlowRecordByProcessId(flowRecord.getProcessId());

            EventPusher.push(new FlowApprovalEvent(FlowApprovalEvent.STATE_FINISH, flowRecord, currentOperator, flowWork));
            return;
        }

        // 获取流程的发起者
        IFlowOperator createOperator = flowOperatorRepository.getFlowOperatorById(flowRecord.getCreateOperatorId());

        // 构建流程创建器
        FlowRecordBuilderService flowRecordBuilderService = new FlowRecordBuilderService(
                flowOperatorRepository,
                flowRecordRepository,
                snapshot,
                opinion,
                createOperator,
                currentOperator,
                historyRecords,
                flowWork,
                flowRecord.getProcessId(),
                flowRecord.getId()
        );

        // 创建下一节点的流程记录
        List<FlowRecord> records;
        // 审批通过并进入下一节点
        if (flowDirectionService.isPassBackRecord()) {
            records = flowRecordBuilderService.createNextRecord(flowNode);
            // 审批拒绝返回上一节点
        } else if (flowDirectionService.isDefaultBackRecord()) {
            records = flowRecordBuilderService.createDefaultBackRecord(flowRecord.getPreId());
        } else {
            // 审批拒绝，并且自定了返回节点
            records = flowRecordBuilderService.createCustomBackRecord(flowNode, flowRecord.getPreId());
        }

        // 保存流程记录
        flowRecordRepository.save(records);

        // 推送审批事件消息
        int eventState = flowSourceDirection == FlowSourceDirection.PASS ? FlowApprovalEvent.STATE_PASS : FlowApprovalEvent.STATE_REJECT;
        EventPusher.push(new FlowApprovalEvent(eventState, flowRecord, currentOperator, flowWork));

        // 推送待办事件消息
        for (FlowRecord record : records) {
            IFlowOperator pushOperator = flowOperatorRepository.getFlowOperatorById(record.getCurrentOperatorId());
            EventPusher.push(new FlowApprovalEvent(FlowApprovalEvent.STATE_TODO, record, pushOperator, flowWork));
        }
    }


    /**
     * 撤回流程
     *
     * @param recordId        流程记录id
     * @param currentOperator 当前操作者
     */
    public void recall(long recordId, IFlowOperator currentOperator) {
        FlowRecordService flowRecordService = new FlowRecordService(flowRecordRepository,
                flowProcessRepository,
                recordId, currentOperator);

        flowRecordService.loadFlowRecord();
        flowRecordService.verifyFlowRecordCurrentOperator();
        flowRecordService.loadFlowWork();
        flowRecordService.loadFlowNode();
        flowRecordService.verifyFlowRecordNotFinish();
        flowRecordService.verifyFlowRecordNotTodo();

        FlowRecord flowRecord = flowRecordService.getFlowRecord();
        FlowWork flowWork = flowRecordService.getFlowWork();

        // 下一流程的流程记录
        List<FlowRecord> childrenRecords = flowRecordRepository.findFlowRecordByPreId(recordId);
        // 下一流程均为办理且未读

        if (childrenRecords.isEmpty()) {
            throw new IllegalArgumentException("flow record not submit");
        }

        boolean allUnDone = childrenRecords.stream().allMatch(item -> item.isUnRead() && item.isTodo());
        if (!allUnDone) {
            throw new IllegalArgumentException("flow record not recall");
        }
        flowRecord.recall();
        flowRecordRepository.update(flowRecord);

        flowRecordRepository.delete(childrenRecords);
        EventPusher.push(new FlowApprovalEvent(FlowApprovalEvent.STATE_RECALL, flowRecord, currentOperator, flowWork));
    }

}
