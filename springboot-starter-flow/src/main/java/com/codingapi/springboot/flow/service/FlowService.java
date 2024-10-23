package com.codingapi.springboot.flow.service;

import com.codingapi.springboot.flow.bind.BindDataSnapshot;
import com.codingapi.springboot.flow.bind.IBindData;
import com.codingapi.springboot.flow.domain.FlowNode;
import com.codingapi.springboot.flow.domain.FlowWork;
import com.codingapi.springboot.flow.domain.Opinion;
import com.codingapi.springboot.flow.event.FlowApprovalEvent;
import com.codingapi.springboot.flow.pojo.FlowDetail;
import com.codingapi.springboot.flow.record.FlowProcess;
import com.codingapi.springboot.flow.record.FlowRecord;
import com.codingapi.springboot.flow.repository.*;
import com.codingapi.springboot.flow.user.IFlowOperator;
import com.codingapi.springboot.framework.event.EventPusher;
import lombok.AllArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
public class FlowService {

    private final FlowWorkRepository flowWorkRepository;
    private final FlowRecordRepository flowRecordRepository;
    private final FlowBindDataRepository flowBindDataRepository;
    private final FlowOperatorRepository flowOperatorRepository;
    private final FlowProcessRepository flowProcessRepository;

    /**
     * 发起流程
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
        flowWork.enableValidate();
        flowWork.verify();

        // 保存流程
        FlowProcess flowProcess = flowWork.generateProcess(operator);
        flowProcessRepository.save(flowProcess);

        // 保存绑定数据
        BindDataSnapshot snapshot = new BindDataSnapshot(bindData);
        flowBindDataRepository.save(snapshot);

        String processId = flowProcess.getId();

        Opinion opinion = Opinion.pass(advice);

        FlowRecordService flowRecordService = new FlowRecordService(flowOperatorRepository, processId, operator, operator, snapshot, opinion, flowWork, opinion.isSuccess(), new ArrayList<>());
        // 获取开始节点
        FlowNode start = flowWork.getStartNode();
        if (start == null) {
            throw new IllegalArgumentException("start node not found");
        }
        long preId = 0;

        // 创建待办记录
        List<FlowRecord> records = flowRecordService.createRecord(preId, start);
        if (records.isEmpty()) {
            throw new IllegalArgumentException("flow record not found");
        }
        flowRecordRepository.save(records);
        EventPusher.push(new FlowApprovalEvent(FlowApprovalEvent.STATE_CREATE));
    }


    /**
     * 流程详情
     *
     * @param recordId        流程记录id
     * @param currentOperator 当前操作者
     */
    public FlowDetail detail(long recordId, IFlowOperator currentOperator) {
        // 检测流程记录
        FlowRecord flowRecord = flowRecordRepository.getFlowRecordById(recordId);
        if (flowRecord == null) {
            throw new IllegalArgumentException("flow record not found");
        }
        flowRecord.submitStateVerify();
        flowRecord.matcherOperator(currentOperator);

        if (!flowRecord.isRead()) {
            flowRecord.read();
            flowRecordRepository.update(flowRecord);
        }

        // 检测流程
        FlowWork flowWork = flowProcessRepository.getFlowWorkByProcessId(flowRecord.getProcessId());
        if (flowWork == null) {
            throw new IllegalArgumentException("flow work not found");
        }
        flowWork.enableValidate();

        BindDataSnapshot snapshot = flowBindDataRepository.getBindDataSnapshotById(flowRecord.getSnapshotId());
        List<FlowRecord> flowRecords = flowRecordRepository.findFlowRecordByProcessId(flowRecord.getProcessId());
        return new FlowDetail(flowRecord, snapshot, flowWork, flowRecords);
    }


    /**
     * 保存流程
     *
     * @param recordId        流程记录id
     * @param currentOperator 当前操作者
     * @param bindData        绑定数据
     */
    public void save(long recordId, IFlowOperator currentOperator, IBindData bindData) {
        // 检测流程记录
        FlowRecord flowRecord = flowRecordRepository.getFlowRecordById(recordId);
        if (flowRecord == null) {
            throw new IllegalArgumentException("flow record not found");
        }
        flowRecord.submitStateVerify();
        flowRecord.matcherOperator(currentOperator);

        // 保存绑定数据
        BindDataSnapshot snapshot = new BindDataSnapshot(flowRecord.getSnapshotId(), bindData);
        flowBindDataRepository.update(snapshot);

        flowRecord.update();
        flowRecordRepository.update(flowRecord);
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
        // 检测流程记录
        FlowRecord flowRecord = flowRecordRepository.getFlowRecordById(recordId);
        if (flowRecord == null) {
            throw new IllegalArgumentException("flow record not found");
        }
        flowRecord.submitStateVerify();
        flowRecord.matcherOperator(currentOperator);

        // 检测流程
        FlowWork flowWork = flowProcessRepository.getFlowWorkByProcessId(flowRecord.getProcessId());
        if (flowWork == null) {
            throw new IllegalArgumentException("flow work not found");
        }
        flowWork.enableValidate();

        // 检测流程节点
        FlowNode flowNode = flowWork.getNodeByCode(flowRecord.getNodeCode());
        if (flowNode == null) {
            throw new IllegalArgumentException("flow node not found");
        }

        // 下一流程的流程记录
        List<FlowRecord> childrenRecords = flowRecordRepository.findFlowRecordByPreId(recordId);
        // 不能存在后续的子流程
        if (!childrenRecords.isEmpty()) {
            throw new IllegalArgumentException("flow node is done");
        }

        // 获取创建者
        IFlowOperator createOperator = flowOperatorRepository.getFlowOperatorById(flowRecord.getCreateOperatorId());

        // 保存绑定数据
        BindDataSnapshot snapshot = new BindDataSnapshot(bindData);
        flowBindDataRepository.save(snapshot);

        // 根据审批意见判断流程是否进入下一节点
        boolean flowNextStep = opinion.isSuccess();

        // 当前节点的办理记录
        List<FlowRecord> historyRecords = new ArrayList<>();

        // 与当前流程同级的流程记录
        List<FlowRecord> currentFlowRecords = flowRecordRepository.findFlowRecordByPreId(flowRecord.getPreId());

        // 会签处理流程
        if (flowNode.isSign()) {
            // 会签下所有人尚未提交时，不执行下一节点
            boolean allDone = currentFlowRecords.stream().allMatch(FlowRecord::isDone);
            if (!allDone) {
                // 流程尚未审批结束直接退出
                return;
            }
            // 会签下所有人都同意，再执行下一节点
            boolean allPass = currentFlowRecords.stream().allMatch(FlowRecord::isPass);
            if (!allPass) {
                flowNextStep = false;

            }

            historyRecords.addAll(currentFlowRecords);
        }
        // 非会签处理流程
        if (flowNode.isUnSign()) {
            // 非会签下，默认将所有人为提交的流程，都自动提交然后再执行下一节点
            for (FlowRecord record : currentFlowRecords) {
                if (record.getId() != recordId) {
                    record.unSignAutoDone(currentOperator, snapshot);
                    flowRecordRepository.update(flowRecord);
                    historyRecords.add(record);
                }
            }
        }

        String processId = flowRecord.getProcessId();

        // 流程结束的情况
        if (flowNextStep && flowNode.isOverNode()) {
            // 结束简单时自动审批
            flowRecord.finish();
            // 提交流程
            flowRecord.submitRecord(currentOperator, snapshot, opinion, flowNextStep);
            flowRecordRepository.update(flowRecord);

            flowRecordRepository.finishFlowRecordByProcessId(processId);
            EventPusher.push(new FlowApprovalEvent(FlowApprovalEvent.STATE_FINISH));
            return;
        }
        // 提交流程
        flowRecord.submitRecord(currentOperator, snapshot, opinion, flowNextStep);
        flowRecordRepository.update(flowRecord);

        // 拥有退出条件 或审批通过时，匹配下一节点
        if (flowWork.hasBackRelation() || flowNextStep) {

            // 退回流程 并且  也设置了退回节点
            if (!flowNextStep && flowWork.hasBackRelation()) {
                if (flowNode.isAnyOperatorMatcher()) {
                    // 如果是任意人员操作时则需要指定为当时审批人员为当前审批人员
                    FlowRecord preFlowRecord = flowRecordRepository.getFlowRecordById(flowRecord.getPreId());
                    while (!preFlowRecord.isTransfer() && preFlowRecord.getNodeCode().equals(flowNode.getCode())) {
                        preFlowRecord = flowRecordRepository.getFlowRecordById(flowRecord.getPreId());
                    }
                    currentOperator = flowOperatorRepository.getFlowOperatorById(preFlowRecord.getCurrentOperatorId());
                }
            }

            FlowRecordService flowRecordService = new FlowRecordService(flowOperatorRepository, processId, createOperator, currentOperator, snapshot, opinion, flowWork, flowNextStep, historyRecords);
            FlowNode nextNode = flowRecordService.matcherNextNode(flowNode);
            if (nextNode == null) {
                throw new IllegalArgumentException("next node not found");
            }

            List<FlowRecord> records = flowRecordService.createRecord(flowRecord.getId(), nextNode);
            flowRecordRepository.save(records);

        } else {
            // 拒绝时，默认返回上一个节点
            FlowRecord preRecord = flowRecordRepository.getFlowRecordById(flowRecord.getPreId());
            // 去除所有的转办的记录
            while (preRecord.isTransfer()) {
                // 继续寻找上一个节点
                preRecord = flowRecordRepository.getFlowRecordById(preRecord.getPreId());
            }

            // 获取上一个节点的审批者，继续将审批者设置为当前审批者
            currentOperator = flowOperatorRepository.getFlowOperatorById(preRecord.getCurrentOperatorId());

            FlowRecordService flowRecordService = new FlowRecordService(flowOperatorRepository, processId, createOperator, currentOperator, snapshot, opinion, flowWork, flowNextStep, historyRecords);
            FlowNode nextNode = flowWork.getNodeByCode(preRecord.getNodeCode());
            if (nextNode == null) {
                throw new IllegalArgumentException("next node not found");
            }
            List<FlowRecord> records = flowRecordService.createRecord(preRecord.getId(), nextNode);
            flowRecordRepository.save(records);

        }

        int eventState = flowNextStep ? FlowApprovalEvent.STATE_PASS : FlowApprovalEvent.STATE_REJECT;
        EventPusher.push(new FlowApprovalEvent(eventState));

    }


    /**
     * 撤回流程
     *
     * @param recordId        流程记录id
     * @param currentOperator 当前操作者
     */
    public void recall(long recordId, IFlowOperator currentOperator) {
        // 检测流程记录
        FlowRecord flowRecord = flowRecordRepository.getFlowRecordById(recordId);
        if (flowRecord == null) {
            throw new IllegalArgumentException("flow record not found");
        }
        flowRecord.matcherOperator(currentOperator);

        // 检测流程
        FlowWork flowWork = flowProcessRepository.getFlowWorkByProcessId(flowRecord.getProcessId());
        if (flowWork == null) {
            throw new IllegalArgumentException("flow work not found");
        }
        flowWork.enableValidate();

        // 检测流程节点
        FlowNode flowNode = flowWork.getNodeByCode(flowRecord.getNodeCode());
        if (flowNode == null) {
            throw new IllegalArgumentException("flow node not found");
        }

        if (flowRecord.isTodo()) {
            throw new IllegalArgumentException("flow record is todo");
        }

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
        EventPusher.push(new FlowApprovalEvent(FlowApprovalEvent.STATE_RECALL));
    }

}
