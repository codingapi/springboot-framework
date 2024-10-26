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
        FlowRecordService2 flowRecordService2 = new FlowRecordService2(flowRecordRepository,
                flowProcessRepository,
                recordId,currentOperator);

        flowRecordService2.loadFlowRecord();
        flowRecordService2.verifyFlowRecordSubmitState();
        flowRecordService2.verifyFlowRecordCurrentOperator();
        flowRecordService2.loadFlowWork();
        flowRecordService2.verifyFlowRecordNotFinish();
        flowRecordService2.verifyFlowRecordNotDone();

        FlowRecord flowRecord = flowRecordService2.getFlowRecord();
        FlowWork flowWork = flowRecordService2.getFlowWork();

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
        FlowRecordService2 flowRecordService2 = new FlowRecordService2(flowRecordRepository,
                flowProcessRepository,
                recordId,currentOperator);
        flowRecordService2.loadFlowRecord();
        flowRecordService2.loadFlowWork();
        flowRecordService2.verifyFlowRecordIsDone();

        FlowRecord flowRecord = flowRecordService2.getFlowRecord();
        FlowWork flowWork = flowRecordService2.getFlowWork();

        List<FlowRecord> todoRecords = flowRecordRepository.findTodoFlowRecordByProcessId(flowRecord.getProcessId());

        // 推送催办消息
        for (FlowRecord record : todoRecords) {
            IFlowOperator pushOperator = flowOperatorRepository.getFlowOperatorById(record.getCurrentOperatorId());
            EventPusher.push(new FlowApprovalEvent(FlowApprovalEvent.STATE_URGE, record, pushOperator,flowWork));
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

        FlowRecordService2 flowRecordService2 = new FlowRecordService2(flowRecordRepository,
                flowProcessRepository,
                recordId,currentOperator);

        flowRecordService2.loadFlowRecord();
        flowRecordService2.flagReadFlowRecord();
        flowRecordService2.loadFlowWork();

        FlowRecord flowRecord = flowRecordService2.getFlowRecord();
        FlowWork flowWork = flowRecordService2.getFlowWork();


        BindDataSnapshot snapshot = flowBindDataRepository.getBindDataSnapshotById(flowRecord.getSnapshotId());
        List<FlowRecord> flowRecords = flowRecordRepository.findFlowRecordByProcessId(flowRecord.getProcessId());

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

        FlowRecordService2 flowRecordService2 = new FlowRecordService2(flowRecordRepository,
                flowProcessRepository,
                recordId,currentOperator);

        flowRecordService2.loadFlowRecord();
        flowRecordService2.verifyFlowRecordSubmitState();
        flowRecordService2.verifyFlowRecordCurrentOperator();
        flowRecordService2.verifyTargetOperatorIsNotCurrentOperator(targetOperator);


        Opinion opinion = Opinion.transfer(advice);


        flowRecordService2.loadFlowWork();
        flowRecordService2.loadFlowNode();;
        flowRecordService2.verifyFlowRecordIsTodo();

        FlowRecord flowRecord = flowRecordService2.getFlowRecord();
        FlowWork flowWork = flowRecordService2.getFlowWork();
        FlowNode flowNode = flowRecordService2.getFlowNode();

        // 下一流程的流程记录
        List<FlowRecord> childrenRecords = flowRecordRepository.findFlowRecordByPreId(recordId);
        // 下一流程均为办理且未读
        if (!childrenRecords.isEmpty()) {
            throw new IllegalArgumentException("flow record has submit");
        }
        // 保存绑定数据
        BindDataSnapshot snapshot = new BindDataSnapshot(bindData);
        flowBindDataRepository.save(snapshot);

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
        EventPusher.push(new FlowApprovalEvent(FlowApprovalEvent.STATE_TRANSFER, flowRecord, currentOperator,flowWork));

        // 推送待办消息
        EventPusher.push(new FlowApprovalEvent(FlowApprovalEvent.STATE_TODO, transferRecord, targetOperator,flowWork));
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
        FlowRecordService2 flowRecordService2 = new FlowRecordService2(flowRecordRepository,
                flowProcessRepository,
                recordId,currentOperator);
        flowRecordService2.loadFlowRecord();
        flowRecordService2.verifyFlowRecordSubmitState();
        flowRecordService2.verifyFlowRecordCurrentOperator();
        flowRecordService2.loadFlowWork();
        flowRecordService2.loadFlowNode();
        flowRecordService2.verifyFlowNodeEditableState(false);

        FlowNodeService flowNodeService = new FlowNodeService(flowOperatorRepository,flowBindDataRepository,flowRecordService2,bindData,Opinion.save(advice));
        flowNodeService.updateSnapshot();
        flowNodeService.updateFlowRecord();

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

        String processId = flowProcess.getProcessId();

        Opinion opinion = Opinion.pass(advice);

        FlowRecordService createRecordService = new FlowRecordService(flowOperatorRepository, processId, operator, operator, snapshot, opinion, flowWork, FlowSourceDirection.PASS, new ArrayList<>());
        // 获取开始节点
        FlowNode start = flowWork.getStartNode();
        if (start == null) {
            throw new IllegalArgumentException("start node not found");
        }
        long preId = 0;

        // 创建待办记录
        List<FlowRecord> records = createRecordService.createRecord(preId, start);
        if (records.isEmpty()) {
            throw new IllegalArgumentException("flow record not found");
        }

        flowRecordRepository.save(records);

        // 推送消息
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

        FlowRecordService2 flowRecordService2 = new FlowRecordService2(flowRecordRepository, flowProcessRepository,
                recordId,currentOperator);

        flowRecordService2.loadFlowRecord();
        flowRecordService2.verifyFlowRecordSubmitState();
        flowRecordService2.verifyFlowRecordCurrentOperator();
        flowRecordService2.loadFlowWork();
        flowRecordService2.loadFlowNode();

        FlowRecord flowRecord = flowRecordService2.getFlowRecord();
        FlowNode flowNode = flowRecordService2.getFlowNode();
        FlowWork flowWork = flowRecordService2.getFlowWork();

        FlowNodeService flowNodeService = new FlowNodeService(flowOperatorRepository,flowBindDataRepository,flowRecordService2,bindData,opinion);

        flowNodeService.loadFlowSourceDirection();
        flowNodeService.verifyFlowSourceDirection();


        flowNodeService.loadChildrenRecords();
        flowNodeService.verifyChildrenRecordsIsEmpty();

        // 获取创建者
        IFlowOperator createOperator = flowOperatorRepository.getFlowOperatorById(flowRecord.getCreateOperatorId());

        flowNodeService.loadSnapshot();

        BindDataSnapshot snapshot = flowNodeService.getSnapshot();
        FlowSourceDirection flowSourceDirection = flowNodeService.getFlowSourceDirection();

        // 提交流程
        flowRecord.submitRecord(currentOperator, snapshot, opinion, flowSourceDirection);
        flowRecordRepository.update(flowRecord);

        // 与当前流程同级的流程记录
        List<FlowRecord> historyRecords = flowRecordRepository.findFlowRecordByPreId(flowRecord.getPreId());


        // 会签处理流程
        if (flowNode.isSign()) {
            // 会签下所有人尚未提交时，不执行下一节点
            boolean allDone = historyRecords.stream().filter(item -> !item.isTransfer()).allMatch(FlowRecord::isDone);
            if (!allDone) {
                // 流程尚未审批结束直接退出
                return;
            }
            // 会签下所有人都同意，再执行下一节点
            boolean allPass = historyRecords.stream().filter(item -> !item.isTransfer()).allMatch(FlowRecord::isPass);
            if (!allPass) {
                flowSourceDirection = FlowSourceDirection.REJECT;
            }

        }
        // 非会签处理流程
        if (flowNode.isUnSign()) {
            // 非会签下，默认其他将所有人未提交的流程，都自动提交然后再执行下一节点
            for (FlowRecord record : historyRecords) {
                if (record.isTodo() && record.getId() != recordId) {
                    record.unSignAutoDone(currentOperator, snapshot);
                    flowRecordRepository.update(flowRecord);
                }
            }
        }

        String processId = flowRecord.getProcessId();

        // 流程结束的情况
        if (flowSourceDirection== FlowSourceDirection.PASS && flowNode.isOverNode()) {
            // 结束简单时自动审批
            flowRecord.finish();
            // 提交流程
            flowRecord.submitRecord(currentOperator, snapshot, opinion, flowSourceDirection);
            flowRecordRepository.update(flowRecord);

            flowRecordRepository.finishFlowRecordByProcessId(processId);
            EventPusher.push(new FlowApprovalEvent(FlowApprovalEvent.STATE_FINISH, flowRecord, currentOperator,flowWork));
            return;
        }

        this.createNextRecord(flowWork,flowSourceDirection,flowNode,processId,createOperator,currentOperator,snapshot,opinion,flowRecord,historyRecords);

    }


    /**
     * 创建下一个流程的记录
     * @param flowWork              流程
     * @param flowSourceDirection   是否通过
     * @param flowNode              流程节点
     * @param processId             流程id
     * @param createOperator        流程创建者
     * @param currentOperator       当前操作者
     * @param snapshot              绑定数据
     * @param opinion               上级审批意见
     * @param flowRecord            流程记录
     * @param historyRecords        历史记录
     */
    private void createNextRecord(FlowWork flowWork, FlowSourceDirection flowSourceDirection, FlowNode flowNode, String processId, IFlowOperator createOperator, IFlowOperator currentOperator, BindDataSnapshot snapshot, Opinion opinion, FlowRecord flowRecord, List<FlowRecord> historyRecords){
        long preId = flowRecord.getId();
        // 拥有退出条件 或审批通过时，匹配下一节点
        if (flowWork.hasBackRelation() || flowSourceDirection== FlowSourceDirection.PASS) {

            FlowRecordService flowRecordService = new FlowRecordService(flowOperatorRepository, processId, createOperator, currentOperator, snapshot, opinion, flowWork, flowSourceDirection, historyRecords);
            FlowNode nextNode = flowRecordService.matcherNextNode(flowNode);
            if (nextNode == null) {
                throw new IllegalArgumentException("next node not found");
            }

            IFlowOperator flowOperator = currentOperator;
            // 退回流程 并且  也设置了退回节点
            if (flowSourceDirection== FlowSourceDirection.REJECT && flowWork.hasBackRelation()) {
                if (nextNode.isAnyOperatorMatcher()) {
                    // 如果是任意人员操作时则需要指定为当时审批人员为当前审批人员
                    FlowRecord preFlowRecord = flowRecordRepository.getFlowRecordById(flowRecord.getPreId());
                    while (preFlowRecord.isTransfer() || !preFlowRecord.getNodeCode().equals(nextNode.getCode())) {
                        preFlowRecord = flowRecordRepository.getFlowRecordById(preFlowRecord.getPreId());
                    }
                    flowOperator = flowOperatorRepository.getFlowOperatorById(preFlowRecord.getCurrentOperatorId());
                }
            }
            flowRecordService.changeCurrentOperator(flowOperator);

            this.createNexRecordList(flowRecordService,preId,nextNode,flowWork);

        } else {
            IFlowOperator flowOperator;
            // 拒绝时，默认返回上一个节点
            FlowRecord preRecord = flowRecordRepository.getFlowRecordById(flowRecord.getPreId());
            // 去除所有的转办的记录
            while (preRecord.isTransfer()) {
                // 继续寻找上一个节点
                preRecord = flowRecordRepository.getFlowRecordById(preRecord.getPreId());
            }

            // 获取上一个节点的审批者，继续将审批者设置为当前审批者
            flowOperator = flowOperatorRepository.getFlowOperatorById(preRecord.getCurrentOperatorId());

            FlowRecordService flowRecordService = new FlowRecordService(flowOperatorRepository, processId, createOperator, flowOperator, snapshot, opinion, flowWork, flowSourceDirection, historyRecords);
            FlowNode nextNode = flowWork.getNodeByCode(preRecord.getNodeCode());
            if (nextNode == null) {
                throw new IllegalArgumentException("next node not found");
            }
            this.createNexRecordList(flowRecordService,preId,nextNode,flowWork);
        }

        int eventState = flowSourceDirection== FlowSourceDirection.PASS ? FlowApprovalEvent.STATE_PASS : FlowApprovalEvent.STATE_REJECT;
        EventPusher.push(new FlowApprovalEvent(eventState, flowRecord, currentOperator,flowWork));

    }

    private void createNexRecordList(FlowRecordService flowRecordService,long preId,FlowNode nextNode,FlowWork flowWork){
        List<FlowRecord> records = flowRecordService.createRecord(preId, nextNode);
        flowRecordRepository.save(records);

        for (FlowRecord record : records) {
            IFlowOperator pushOperator = flowOperatorRepository.getFlowOperatorById(record.getCurrentOperatorId());
            EventPusher.push(new FlowApprovalEvent(FlowApprovalEvent.STATE_TODO, record, pushOperator,flowWork));
        }
    }


    /**
     * 撤回流程
     *
     * @param recordId        流程记录id
     * @param currentOperator 当前操作者
     */
    public void recall(long recordId, IFlowOperator currentOperator) {
        FlowRecordService2 flowRecordService2 = new FlowRecordService2(flowRecordRepository,
                flowProcessRepository,
                recordId,currentOperator);

        flowRecordService2.loadFlowRecord();
        flowRecordService2.verifyFlowRecordCurrentOperator();
        flowRecordService2.loadFlowWork();
        flowRecordService2.loadFlowNode();
        flowRecordService2.verifyFlowRecordNotFinish();
        flowRecordService2.verifyFlowRecordNotTodo();

        FlowRecord flowRecord = flowRecordService2.getFlowRecord();
        FlowWork flowWork = flowRecordService2.getFlowWork();



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
        EventPusher.push(new FlowApprovalEvent(FlowApprovalEvent.STATE_RECALL, flowRecord, currentOperator,flowWork));
    }

}
