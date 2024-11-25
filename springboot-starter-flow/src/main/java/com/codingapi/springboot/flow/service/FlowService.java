package com.codingapi.springboot.flow.service;

import com.codingapi.springboot.flow.bind.BindDataSnapshot;
import com.codingapi.springboot.flow.bind.IBindData;
import com.codingapi.springboot.flow.content.FlowSession;
import com.codingapi.springboot.flow.domain.FlowNode;
import com.codingapi.springboot.flow.domain.FlowWork;
import com.codingapi.springboot.flow.domain.Opinion;
import com.codingapi.springboot.flow.em.FlowSourceDirection;
import com.codingapi.springboot.flow.event.FlowApprovalEvent;
import com.codingapi.springboot.flow.pojo.FlowDetail;
import com.codingapi.springboot.flow.pojo.FlowNodeResult;
import com.codingapi.springboot.flow.pojo.FlowResult;
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
        FlowRecordVerifyService flowRecordVerifyService = new FlowRecordVerifyService(flowRecordRepository,
                flowProcessRepository,
                recordId, currentOperator);

        flowRecordVerifyService.loadFlowRecord();
        flowRecordVerifyService.verifyFlowRecordSubmitState();
        flowRecordVerifyService.verifyFlowRecordCurrentOperator();
        flowRecordVerifyService.loadFlowWork();
        flowRecordVerifyService.verifyFlowRecordNotFinish();
        flowRecordVerifyService.verifyFlowRecordNotDone();

        FlowRecord flowRecord = flowRecordVerifyService.getFlowRecord();
        FlowWork flowWork = flowRecordVerifyService.getFlowWork();

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
        FlowRecordVerifyService flowRecordVerifyService = new FlowRecordVerifyService(flowRecordRepository,
                flowProcessRepository,
                recordId, currentOperator);
        flowRecordVerifyService.loadFlowRecord();
        flowRecordVerifyService.loadFlowWork();
        flowRecordVerifyService.verifyFlowRecordIsDone();

        FlowRecord flowRecord = flowRecordVerifyService.getFlowRecord();
        FlowWork flowWork = flowRecordVerifyService.getFlowWork();

        List<FlowRecord> todoRecords = flowRecordRepository.findTodoFlowRecordByProcessId(flowRecord.getProcessId());

        // 推送催办消息
        for (FlowRecord record : todoRecords) {
            IFlowOperator pushOperator = record.getCurrentOperator();
            EventPusher.push(new FlowApprovalEvent(FlowApprovalEvent.STATE_URGE, record, pushOperator, flowWork, null), true);
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

        FlowRecordVerifyService flowRecordVerifyService = new FlowRecordVerifyService(flowRecordRepository,
                flowProcessRepository,
                recordId, currentOperator);

        flowRecordVerifyService.loadFlowRecord();
        flowRecordVerifyService.setFlowRecordRead();
        flowRecordVerifyService.loadFlowWork();

        FlowRecord flowRecord = flowRecordVerifyService.getFlowRecord();
        FlowWork flowWork = flowRecordVerifyService.getFlowWork();


        BindDataSnapshot snapshot = flowBindDataRepository.getBindDataSnapshotById(flowRecord.getSnapshotId());
        List<FlowRecord> flowRecords =
                flowRecordRepository.findFlowRecordByProcessId(flowRecord.getProcessId()).
                        stream().
                        sorted((o1, o2) -> (int) (o2.getId() - o1.getId()))
                        .toList();

        List<IFlowOperator> operators = new ArrayList<>();
        // 获取所有的操作者
        for (FlowRecord record : flowRecords) {
            operators.add(record.getCreateOperator());
            operators.add(record.getCurrentOperator());
            if (record.getInterferedOperator() != null) {
                operators.add(record.getInterferedOperator());
            }
        }

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
    public FlowResult interfere(long recordId, IFlowOperator currentOperator, IBindData bindData, Opinion opinion) {
        if (!currentOperator.isFlowManager()) {
            throw new IllegalArgumentException("current operator is not flow manager");
        }
        return this.submitFlow(recordId, currentOperator, bindData, opinion);
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

        FlowRecordVerifyService flowRecordVerifyService = new FlowRecordVerifyService(flowRecordRepository,
                flowProcessRepository,
                recordId, currentOperator);

        flowRecordVerifyService.loadFlowRecord();
        flowRecordVerifyService.verifyFlowRecordSubmitState();
        flowRecordVerifyService.verifyFlowRecordCurrentOperator();
        flowRecordVerifyService.verifyTargetOperatorIsNotCurrentOperator(targetOperator);

        flowRecordVerifyService.loadFlowWork();
        flowRecordVerifyService.loadFlowNode();

        flowRecordVerifyService.verifyFlowRecordIsTodo();

        FlowRecord flowRecord = flowRecordVerifyService.getFlowRecord();
        FlowWork flowWork = flowRecordVerifyService.getFlowWork();
        FlowNode flowNode = flowRecordVerifyService.getFlowNode();


        // 保存绑定数据
        BindDataSnapshot snapshot = new BindDataSnapshot(bindData);
        flowBindDataRepository.save(snapshot);

        // 构建审批意见
        Opinion opinion = Opinion.transfer(advice);

        // 设置自己的流程状态为转办已完成
        flowRecord.transfer(currentOperator, snapshot, opinion);
        flowRecordRepository.update(flowRecord);

        // 获取创建者
        IFlowOperator createOperator = flowRecord.getCreateOperator();

        // 与当前流程同级的流程记录
        List<FlowRecord> historyRecords;
        if (flowRecord.isStartRecord()) {
            historyRecords = new ArrayList<>();
        } else {
            historyRecords = flowRecordRepository.findFlowRecordByPreId(flowRecord.getPreId());
        }

        // 创建新的待办标题
        FlowSession content = new FlowSession(flowWork, flowNode, createOperator, targetOperator, snapshot.toBindData(), opinion, historyRecords);
        String generateTitle = flowNode.generateTitle(content);

        // 创建转办记录
        FlowRecord transferRecord = flowRecord.copy();
        transferRecord.transferToTodo(generateTitle, targetOperator);
        flowRecordRepository.save(List.of(transferRecord));

        // 推送转办消息
        EventPusher.push(new FlowApprovalEvent(FlowApprovalEvent.STATE_TRANSFER, flowRecord, currentOperator, flowWork, snapshot.toBindData()), true);

        // 推送待办消息
        EventPusher.push(new FlowApprovalEvent(FlowApprovalEvent.STATE_TODO, transferRecord, targetOperator, flowWork, snapshot.toBindData()), true);
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
        FlowRecordVerifyService flowRecordVerifyService = new FlowRecordVerifyService(flowRecordRepository,
                flowProcessRepository,
                recordId, currentOperator);
        flowRecordVerifyService.loadFlowRecord();
        flowRecordVerifyService.verifyFlowRecordSubmitState();
        flowRecordVerifyService.verifyFlowRecordCurrentOperator();
        flowRecordVerifyService.loadFlowWork();
        flowRecordVerifyService.loadFlowNode();
        flowRecordVerifyService.verifyFlowNodeEditableState(false);

        Opinion opinion = Opinion.save(advice);
        FlowRecord flowRecord = flowRecordVerifyService.getFlowRecord();
        BindDataSnapshot snapshot = new BindDataSnapshot(flowRecord.getSnapshotId(), bindData);
        flowBindDataRepository.update(snapshot);

        flowRecord.setOpinion(opinion);
        flowRecordVerifyService.flowRecordRepository.update(flowRecord);

    }


    /**
     * 发起流程 （不自动提交到下一节点）
     *
     * @param workCode 流程编码
     * @param operator 操作者
     * @param bindData 绑定数据
     * @param advice   审批意见
     */
    public FlowResult startFlow(String workCode, IFlowOperator operator, IBindData bindData, String advice) {
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

        List<FlowRecord> historyRecords = new ArrayList<>();

        FlowNodeService flowNodeService = new FlowNodeService(flowOperatorRepository,
                flowRecordRepository,
                snapshot,
                opinion,
                operator,
                operator,
                historyRecords,
                flowWork,
                processId,
                preId);

        flowNodeService.setNextNode(start);

        // 创建待办记录
        List<FlowRecord> records = flowNodeService.createRecord();
        if (records.isEmpty()) {
            throw new IllegalArgumentException("flow record not found");
        } else {
            for (FlowRecord record : records) {
                record.updateOpinion(opinion);
            }
        }

        // 检测流程是否结束
        if (flowNodeService.nextNodeIsOver()) {
            for (FlowRecord record : records) {
                record.submitRecord(operator, snapshot, opinion, FlowSourceDirection.PASS);
                record.finish();
            }

            flowRecordRepository.save(records);

            // 推送事件
            for (FlowRecord record : records) {
                EventPusher.push(new FlowApprovalEvent(FlowApprovalEvent.STATE_CREATE, record, operator, flowWork, snapshot.toBindData()), true);

                EventPusher.push(new FlowApprovalEvent(FlowApprovalEvent.STATE_FINISH,
                                record,
                                operator,
                                flowWork,
                                snapshot.toBindData()),
                        true);
            }
            return new FlowResult(flowWork, records);
        }

        // 保存流程记录
        flowRecordRepository.save(records);

        // 推送事件消息
        for (FlowRecord record : records) {
            EventPusher.push(new FlowApprovalEvent(FlowApprovalEvent.STATE_CREATE, record, operator, flowWork, snapshot.toBindData()), true);
            EventPusher.push(new FlowApprovalEvent(FlowApprovalEvent.STATE_TODO, record, operator, flowWork, snapshot.toBindData()), true);
        }
        // 当前的审批记录
        return new FlowResult(flowWork, records);
    }



    /**
     * 尝试提交流程
     *
     * @param recordId        流程记录id
     * @param currentOperator 当前操作者
     * @param bindData        绑定数据
     * @param opinion         审批意见
     */
    public FlowNodeResult trySubmitFlow(long recordId, IFlowOperator currentOperator, IBindData bindData, Opinion opinion) {

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


        // 保存流程表单快照数据
        BindDataSnapshot snapshot = null;
        if (flowNode.isEditable()) {
            snapshot = new BindDataSnapshot(bindData);
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

        // 与当前流程同级的流程记录
        List<FlowRecord> historyRecords;
        if (flowRecord.isStartRecord()) {
            historyRecords = new ArrayList<>();
        } else {
            // copy 流程数据防止影响原有数据
            historyRecords = flowRecordRepository.findFlowRecordByPreId(flowRecord.getPreId()).stream().map(FlowRecord::copy).toList();
        }
        flowDirectionService.bindHistoryRecords(historyRecords);

        // 判断流程是否结束（会签时需要所有人都通过）
        if (flowNode.isSign()) {
            boolean next = flowDirectionService.hasCurrentFlowNodeIsDone();
            if (next) {
                List<FlowRecord> todoRecords = historyRecords.stream().filter(FlowRecord::isTodo).toList();
                return new FlowNodeResult(flowWork,flowNode, todoRecords.stream().map(FlowRecord::getCurrentOperator).toList());
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

        List<? extends IFlowOperator> operators =  flowNodeService.loadNextNodeOperators();
        return new FlowNodeResult(flowWork, flowNode,operators);
    }


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


    /**
     * 撤回流程
     *
     * @param recordId        流程记录id
     * @param currentOperator 当前操作者
     */
    public void recall(long recordId, IFlowOperator currentOperator) {
        FlowRecordVerifyService flowRecordVerifyService = new FlowRecordVerifyService(flowRecordRepository,
                flowProcessRepository,
                recordId, currentOperator);

        flowRecordVerifyService.loadFlowRecord();
        flowRecordVerifyService.verifyFlowRecordCurrentOperator();
        flowRecordVerifyService.loadFlowWork();
        flowRecordVerifyService.loadFlowNode();
        flowRecordVerifyService.verifyFlowRecordNotFinish();
        flowRecordVerifyService.verifyFlowRecordNotTodo();

        FlowRecord flowRecord = flowRecordVerifyService.getFlowRecord();
        FlowWork flowWork = flowRecordVerifyService.getFlowWork();

        // 下一流程的流程记录
        List<FlowRecord> childrenRecords = flowRecordRepository.findFlowRecordByPreId(recordId);
        // 下一流程均为办理且未读

        // 如果是在开始节点撤销，则直接删除
        if (flowRecord.isStartRecord() && flowRecord.isTodo()) {
            if (!childrenRecords.isEmpty()) {
                throw new IllegalArgumentException("flow record not recall");
            }
            flowRecordRepository.delete(List.of(flowRecord));
        } else {
            // 如果是在中间节点撤销，则需要判断是否所有的子流程都是未读状态
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
        }

        EventPusher.push(new FlowApprovalEvent(FlowApprovalEvent.STATE_RECALL, flowRecord, currentOperator, flowWork, null), true);
    }

}
