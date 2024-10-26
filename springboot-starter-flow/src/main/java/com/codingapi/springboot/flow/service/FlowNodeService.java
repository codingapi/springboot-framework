package com.codingapi.springboot.flow.service;

import com.codingapi.springboot.flow.bind.BindDataSnapshot;
import com.codingapi.springboot.flow.bind.IBindData;
import com.codingapi.springboot.flow.content.FlowContent;
import com.codingapi.springboot.flow.domain.FlowNode;
import com.codingapi.springboot.flow.domain.FlowRelation;
import com.codingapi.springboot.flow.domain.FlowWork;
import com.codingapi.springboot.flow.domain.Opinion;
import com.codingapi.springboot.flow.em.FlowSourceDirection;
import com.codingapi.springboot.flow.error.ErrorResult;
import com.codingapi.springboot.flow.error.NodeResult;
import com.codingapi.springboot.flow.error.OperatorResult;
import com.codingapi.springboot.flow.record.FlowRecord;
import com.codingapi.springboot.flow.repository.FlowBindDataRepository;
import com.codingapi.springboot.flow.repository.FlowOperatorRepository;
import com.codingapi.springboot.flow.user.IFlowOperator;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

class FlowNodeService {


    private final FlowOperatorRepository flowOperatorRepository;
    private final FlowBindDataRepository flowBindDataRepository;

    private final FlowRecordService2 flowRecordService2;
    private final String processId;
    private final long preId;

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
    private IFlowOperator createOperator;

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
        this.preId = flowRecord.getId();
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
     * 完成所有流程
     */
    public void finishFlow() {
        flowRecord.finish();
        flowRecord.submitRecord(currentOperator, snapshot, opinion, flowSourceDirection);
        flowRecordService2.flowRecordRepository.update(flowRecord);
        flowRecordService2.flowRecordRepository.finishFlowRecordByProcessId(processId);
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
     * 加载子节点的审批记录
     * 即加载后续节点的审批记录
     */
    public void loadChildrenRecords() {
        childrenRecords = flowRecordService2.flowRecordRepository.findFlowRecordByPreId(flowRecord.getId());
    }


    /**
     * 校验是否后续没有审批记录
     */
    public void verifyChildrenRecordsIsEmpty() {
        if (!childrenRecords.isEmpty()) {
            throw new IllegalArgumentException("flow node is done");
        }
    }

    /**
     * 校验流程的审批方向
     */
    public void verifyFlowSourceDirection() {
        if (flowSourceDirection == null) {
            throw new IllegalArgumentException("flow source direction is null");
        }
        if (flowNode.isStartNode() && flowSourceDirection == FlowSourceDirection.REJECT) {
            throw new IllegalArgumentException("flow node is start node");
        }
    }


    /**
     * 加载流程发起者
     */
    public void loadCreateOperator() {
        createOperator = flowOperatorRepository.getFlowOperatorById(flowRecord.getCreateOperatorId());
    }


    /**
     * 获取下一个节点
     *
     * @return 下一个节点
     */
    public FlowNode matcherNextNode(boolean back) {
        List<FlowRelation> relations = flowWork.getRelations().stream()
                .filter(relation -> relation.sourceMatcher(flowNode.getCode()))
                .filter(relation -> relation.isBack() == back)
                .sorted((o1, o2) -> (o2.getOrder() - o1.getOrder()))
                .toList();
        if (relations.isEmpty()) {
            throw new IllegalArgumentException("relation not found");
        }
        FlowContent flowContent = new FlowContent(flowWork, flowNode, createOperator, currentOperator, snapshot.toBindData(), opinion, historyRecords);
        List<FlowNode> flowNodes = new ArrayList<>();
        for (FlowRelation flowRelation : relations) {
            FlowNode node = flowRelation.trigger(flowContent);
            if (node != null) {
                flowNodes.add(node);
            }
        }
        if (flowNodes.isEmpty()) {
            throw new IllegalArgumentException("next node not found");
        }
        return flowNodes.get(0);
    }

    /**
     * 异常匹配
     *
     * @param currentNode     当前节点
     * @param currentOperator 当前操作者
     * @return 流程记录
     */
    private List<FlowRecord> errMatcher(FlowNode currentNode, IFlowOperator currentOperator) {
        if (currentNode.hasErrTrigger()) {
            FlowContent flowContent = new FlowContent(flowWork, currentNode, createOperator, currentOperator, snapshot.toBindData(), opinion, historyRecords);
            ErrorResult errorResult = currentNode.errMatcher(flowContent);
            if (errorResult == null) {
                throw new IllegalArgumentException("errMatcher match error.");
            }

            // 匹配操作者
            if (errorResult.isOperator()) {
                List<FlowRecord> recordList = new ArrayList<>();
                List<Long> operatorIds = ((OperatorResult) errorResult).getOperatorIds();
                List<? extends IFlowOperator> operators = flowOperatorRepository.findByIds(operatorIds);
                for (IFlowOperator operator : operators) {
                    FlowContent content = new FlowContent(flowWork, currentNode, createOperator, operator, snapshot.toBindData(), opinion, historyRecords);
                    String recordTitle = currentNode.generateTitle(content);
                    FlowRecord record = currentNode.createRecord(flowWork.getId(), processId, preId, recordTitle, createOperator, operator, snapshot);
                    recordList.add(record);
                }
                return recordList;
            }
            // 匹配节点
            if (errorResult.isNode()) {
                String nodeCode = ((NodeResult) errorResult).getNode();
                FlowNode node = flowWork.getNodeByCode(nodeCode);
                if (node == null) {
                    throw new IllegalArgumentException("node not found.");
                }
                List<FlowRecord> recordList = new ArrayList<>();
                FlowContent content = new FlowContent(flowWork, node, createOperator, currentOperator, snapshot.toBindData(), opinion, historyRecords);
                List<? extends IFlowOperator> matcherOperators = node.loadFlowNodeOperator(content, flowOperatorRepository);
                if (!matcherOperators.isEmpty()) {
                    for (IFlowOperator matcherOperator : matcherOperators) {
                        String recordTitle = node.generateTitle(content);
                        FlowRecord record = node.createRecord(flowWork.getId(), processId, preId, recordTitle, createOperator, matcherOperator, snapshot);
                        recordList.add(record);
                    }
                }
                return recordList;
            }
            throw new IllegalArgumentException("errMatcher not match.");
        }
        throw new IllegalArgumentException("operator not match.");
    }


    /**
     * 创建流程记录
     *
     * @param currentNode 当前节点
     * @return 流程记录
     */
    public List<FlowRecord> createRecord(FlowNode currentNode, IFlowOperator currentOperator) {
        FlowContent flowContent = new FlowContent(flowWork, currentNode, createOperator, currentOperator, snapshot.toBindData(), opinion, historyRecords);
        long workId = flowWork.getId();
        List<? extends IFlowOperator> operators = currentNode.loadFlowNodeOperator(flowContent, flowOperatorRepository);
        if (operators.isEmpty()) {
            List<FlowRecord> errorRecordList = this.errMatcher(currentNode, currentOperator);
            if (errorRecordList.isEmpty()) {
                throw new IllegalArgumentException("operator not match.");
            }
            return errorRecordList;
        } else {
            String recordTitle = currentNode.generateTitle(flowContent);
            List<FlowRecord> recordList = new ArrayList<>();
            for (IFlowOperator operator : operators) {
                FlowRecord record = currentNode.createRecord(workId, processId, preId, recordTitle, createOperator, operator, snapshot);
                recordList.add(record);
            }
            return recordList;
        }
    }


    private List<FlowRecord> createPassRecord() {
        if (flowSourceDirection == FlowSourceDirection.PASS) {
            FlowNode nextNode = this.matcherNextNode(false);
            return this.createRecord(nextNode, currentOperator);
        }
        return null;
    }

    private List<FlowRecord> createCustomBackRecord() {
        if (flowSourceDirection == FlowSourceDirection.REJECT) {
            // 设置了退回流程
            if (flowWork.hasBackRelation()) {
                FlowNode nextNode = this.matcherNextNode(true);
                if (nextNode == null) {
                    throw new IllegalArgumentException("next node not found");
                }
                IFlowOperator flowOperator = currentOperator;
                if (nextNode.isAnyOperatorMatcher()) {
                    // 如果是任意人员操作时则需要指定为当时审批人员为当前审批人员
                    FlowRecord preFlowRecord = flowRecordService2.flowRecordRepository.getFlowRecordById(flowRecord.getPreId());
                    while (preFlowRecord.isTransfer() || !preFlowRecord.getNodeCode().equals(nextNode.getCode())) {
                        preFlowRecord = flowRecordService2.flowRecordRepository.getFlowRecordById(preFlowRecord.getPreId());
                    }
                    flowOperator = flowOperatorRepository.getFlowOperatorById(preFlowRecord.getCurrentOperatorId());
                }
                return this.createRecord(nextNode, flowOperator);
            }
        }
        return null;
    }

    private List<FlowRecord> createDefaultBackRecord() {
        if (flowSourceDirection == FlowSourceDirection.REJECT) {
            if (!flowWork.hasBackRelation()) {
                IFlowOperator flowOperator;
                // 拒绝时，默认返回上一个节点
                FlowRecord preRecord = flowRecordService2.flowRecordRepository.getFlowRecordById(flowRecord.getPreId());
                // 去除所有的转办的记录
                while (preRecord.isTransfer()) {
                    // 继续寻找上一个节点
                    preRecord = flowRecordService2.flowRecordRepository.getFlowRecordById(preRecord.getPreId());
                }
                // 获取上一个节点的审批者，继续将审批者设置为当前审批者
                flowOperator = flowOperatorRepository.getFlowOperatorById(preRecord.getCurrentOperatorId());
                FlowNode nextNode = flowWork.getNodeByCode(preRecord.getNodeCode());
                if (nextNode == null) {
                    throw new IllegalArgumentException("next node not found");
                }
                return this.createRecord(nextNode, flowOperator);
            }
        }
        return null;
    }

    private boolean isDefaultBackRecord() {
        return flowSourceDirection == FlowSourceDirection.REJECT && !flowWork.hasBackRelation();
    }

    private boolean isPassBackRecord() {
        return flowSourceDirection == FlowSourceDirection.PASS;
    }

    private boolean isCustomBackRecord() {
        return flowSourceDirection == FlowSourceDirection.REJECT && flowWork.hasBackRelation();
    }


    public List<FlowRecord> createNextRecord() {
        this.loadCreateOperator();
        List<FlowRecord> records = null;
        if (isDefaultBackRecord()) {
            records = this.createDefaultBackRecord();
        }
        if (isCustomBackRecord()) {
            records = this.createCustomBackRecord();
        }
        if (isPassBackRecord()) {
            records = this.createPassRecord();
        }
        return records;
    }


}
