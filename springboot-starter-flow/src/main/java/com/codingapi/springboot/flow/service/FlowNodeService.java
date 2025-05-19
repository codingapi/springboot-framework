package com.codingapi.springboot.flow.service;

import com.codingapi.springboot.flow.bind.BindDataSnapshot;
import com.codingapi.springboot.flow.content.FlowSession;
import com.codingapi.springboot.flow.domain.FlowNode;
import com.codingapi.springboot.flow.domain.FlowRelation;
import com.codingapi.springboot.flow.domain.FlowWork;
import com.codingapi.springboot.flow.domain.Opinion;
import com.codingapi.springboot.flow.error.ErrorResult;
import com.codingapi.springboot.flow.error.NodeResult;
import com.codingapi.springboot.flow.error.OperatorResult;
import com.codingapi.springboot.flow.event.FlowApprovalEvent;
import com.codingapi.springboot.flow.record.FlowRecord;
import com.codingapi.springboot.flow.repository.FlowOperatorRepository;
import com.codingapi.springboot.flow.repository.FlowRecordRepository;
import com.codingapi.springboot.flow.user.IFlowOperator;
import com.codingapi.springboot.framework.event.EventPusher;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * 流程节点服务
 */
public class FlowNodeService {

    @Getter
    private FlowNode nextNode;
    private IFlowOperator nextOperator;
    private IFlowOperator backOperator;

    private final FlowOperatorRepository flowOperatorRepository;
    private final FlowRecordRepository flowRecordRepository;

    private final String processId;
    private final long preId;

    private final FlowWork flowWork;
    private final FlowRecord flowRecord;
    private final Opinion opinion;
    private final IFlowOperator currentOperator;
    private final BindDataSnapshot snapshot;
    private final List<FlowRecord> historyRecords;
    private final IFlowOperator createOperator;


    public FlowNodeService(FlowOperatorRepository flowOperatorRepository,
                           FlowRecordRepository flowRecordRepository,
                           BindDataSnapshot snapshot,
                           Opinion opinion,
                           IFlowOperator createOperator,
                           IFlowOperator currentOperator,
                           List<FlowRecord> historyRecords,
                           FlowWork flowWork,
                           FlowRecord flowRecord,
                           String processId,
                           long preId) {

        this.flowOperatorRepository = flowOperatorRepository;
        this.flowRecordRepository = flowRecordRepository;
        this.snapshot = snapshot;
        this.opinion = opinion;
        this.createOperator = createOperator;
        this.currentOperator = currentOperator;
        this.historyRecords = historyRecords;
        this.flowWork = flowWork;
        this.flowRecord = flowRecord;
        this.processId = processId;
        this.preId = preId;
    }


    /**
     * 设置下一个节点
     */
    public void setNextNode(FlowNode nextNode) {
        this.nextNode = nextNode;
        this.nextOperator = currentOperator;
    }


    /**
     * 加载下一个节点
     */
    public void loadNextPassNode(FlowNode currentNode) {
        this.nextNode = matcherNextNode(currentNode, false);
        this.nextOperator = currentOperator;
    }

    /**
     * 跳过传阅节点流转
     */
    public void skipCirculate() {
        this.nextNode = matcherNextNode(nextNode, false);
        this.nextOperator = currentOperator;
    }

    /**
     * 加载默认回退节点
     */
    public void loadDefaultBackNode(FlowRecord currentRecord) {
        List<FlowRecord> historyRecords =
                flowRecordRepository.findFlowRecordByProcessId(currentRecord.getProcessId())
                        .stream()
                        .sorted((o1, o2) -> (int) (o2.getId() - o1.getId()))
                        .filter(record -> record.getId() < currentRecord.getId())
                        .toList();

        int index = 0;
        while (true) {
            if (index >= historyRecords.size()) {
                throw new IllegalArgumentException("back node not found");
            }
            FlowRecord record = historyRecords.get(index);
            if (record.isDone()) {
                // 是连续的回退节点时，则根据流程记录的状态来判断
                if(record.isReject()){
                    boolean startRemove = false;
                    for(FlowRecord historyRecord: historyRecords){
                        if(startRemove){
                            if(historyRecord.getNodeCode().equals(currentRecord.getNodeCode())){
                                continue;
                            }
                            this.nextNode = flowWork.getNodeByCode(historyRecord.getNodeCode());
                            this.nextOperator = historyRecord.getCurrentOperator();
                            this.backOperator = historyRecord.getCurrentOperator();
                            return;
                        }
                        if(historyRecord.getNodeCode().equals(currentRecord.getNodeCode())){
                            startRemove = true;
                        }
                    }
                }else {
                    this.nextNode = flowWork.getNodeByCode(record.getNodeCode());
                    this.nextOperator = record.getCurrentOperator();
                    this.backOperator = record.getCurrentOperator();
                    return;
                }
            }
            index++;
        }
    }


    /**
     * 加载自定义回退节点
     */
    public void loadCustomBackNode(FlowNode flowNode, long parentRecordId) {
        FlowNode nextNode = this.matcherNextNode(flowNode, true);
        if (nextNode == null) {
            throw new IllegalArgumentException("next node not found");
        }
        IFlowOperator flowOperator = currentOperator;
        if (nextNode.isAnyOperatorMatcher()) {
            // 如果是任意人员操作时则需要指定为当时审批人员为当前审批人员
            FlowRecord preFlowRecord = flowRecordRepository.getFlowRecordById(parentRecordId);
            while (preFlowRecord.isTransfer() || !preFlowRecord.getNodeCode().equals(nextNode.getCode())) {
                preFlowRecord = flowRecordRepository.getFlowRecordById(preFlowRecord.getPreId());
            }
            flowOperator = preFlowRecord.getCurrentOperator();
        }
        this.nextNode = nextNode;
        this.nextOperator = flowOperator;
        this.backOperator = flowOperator;
    }


    /**
     * 获取下一个节点
     *
     * @return 下一个节点
     */
    private FlowNode matcherNextNode(FlowNode flowNode, boolean back) {
        List<FlowRelation> relations = flowWork.getRelations().stream()
                .filter(relation -> relation.sourceMatcher(flowNode.getCode()))
                .filter(relation -> relation.isBack() == back)
                .sorted((o1, o2) -> (o2.getOrder() - o1.getOrder()))
                .toList();
        if (relations.isEmpty()) {
            throw new IllegalArgumentException("relation not found");
        }
        FlowSession flowSession = new FlowSession(flowRecord, flowWork, flowNode, createOperator, currentOperator, snapshot.toBindData(), opinion, historyRecords);
        List<FlowNode> flowNodes = new ArrayList<>();
        for (FlowRelation flowRelation : relations) {
            FlowNode node = flowRelation.trigger(flowSession);
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
     * 创建流程记录
     *
     * @return 流程记录
     */
    public List<FlowRecord> createRecord() {
        // 创建下一节点的流程记录
        List<FlowRecord> records = this.createNextRecord();

        // 检测流程是否为抄送节点
        while (this.nextNodeIsCirculate()) {
            for (FlowRecord record : records) {
                record.circulate();
            }
            flowRecordRepository.save(records);

            for (FlowRecord record : records) {
                IFlowOperator pushOperator = record.getCurrentOperator();

                EventPusher.push(new FlowApprovalEvent(FlowApprovalEvent.STATE_CIRCULATE,
                                record,
                                pushOperator,
                                flowWork,
                                snapshot.toBindData()),
                        true);
            }

            this.skipCirculate();

            records = this.createNextRecord();
        }
        return records;
    }


    /**
     * 加载下一节点的操作者
     *
     * @return 操作者
     */
    public List<? extends IFlowOperator> loadNextNodeOperators() {
        FlowSession flowSession = new FlowSession(flowRecord, flowWork, nextNode, createOperator, nextOperator, snapshot.toBindData(), opinion, historyRecords);
        List<? extends IFlowOperator> operators = nextNode.loadFlowNodeOperator(flowSession, flowOperatorRepository);
        if (operators.isEmpty()) {
            if (nextNode.hasErrTrigger()) {
                ErrorResult errorResult = nextNode.errMatcher(flowSession);
                if (errorResult == null) {
                    throw new IllegalArgumentException("errMatcher match error.");
                }
                if (errorResult.isOperator()) {
                    List<Long> operatorIds = ((OperatorResult) errorResult).getOperatorIds();
                    operators = flowOperatorRepository.findByIds(operatorIds);
                }
            }
        }
        return operators;
    }


    private List<FlowRecord> createNextRecord() {
        FlowSession flowSession = new FlowSession(flowRecord, flowWork,
                nextNode,
                createOperator,
                nextOperator,
                snapshot.toBindData(),
                opinion,
                historyRecords);

        long workId = flowWork.getId();
        List<? extends IFlowOperator> operators = null;
        if (this.backOperator == null) {
            operators = nextNode.loadFlowNodeOperator(flowSession, flowOperatorRepository);
        } else {
            operators = List.of(this.backOperator);
        }
        List<Long> customOperatorIds = opinion.getOperatorIds();
        if (customOperatorIds != null && !customOperatorIds.isEmpty()) {
            operators = operators.stream()
                    .filter(operator -> customOperatorIds.contains(operator.getUserId())).toList();
        }
        List<FlowRecord> recordList;
        if (operators.isEmpty()) {
            recordList = this.errMatcher(nextNode, nextOperator);
            if (recordList.isEmpty()) {
                throw new IllegalArgumentException("operator not match.");
            }
        } else {
            String recordTitle = nextNode.generateTitle(flowSession);
            recordList = new ArrayList<>();
            for (IFlowOperator operator : operators) {
                FlowRecord record = nextNode.createRecord(workId, flowWork.getCode(), processId, preId, recordTitle, createOperator, operator, snapshot, opinion.isWaiting());
                recordList.add(record);
            }
        }
        return recordList;
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
            FlowSession flowSession = new FlowSession(flowRecord, flowWork, currentNode, createOperator, currentOperator, snapshot.toBindData(), opinion, historyRecords);
            ErrorResult errorResult = currentNode.errMatcher(flowSession);
            if (errorResult == null) {
                throw new IllegalArgumentException("errMatcher match error.");
            }

            // 匹配操作者
            if (errorResult.isOperator()) {
                List<FlowRecord> recordList = new ArrayList<>();
                List<Long> operatorIds = ((OperatorResult) errorResult).getOperatorIds();
                List<? extends IFlowOperator> operators = flowOperatorRepository.findByIds(operatorIds);
                for (IFlowOperator operator : operators) {
                    FlowSession content = new FlowSession(flowRecord, flowWork, currentNode, createOperator, operator, snapshot.toBindData(), opinion, historyRecords);
                    String recordTitle = currentNode.generateTitle(content);
                    FlowRecord record = currentNode.createRecord(flowWork.getId(), flowWork.getCode(), processId, preId, recordTitle, createOperator, operator, snapshot, opinion.isWaiting());
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
                FlowSession content = new FlowSession(flowRecord, flowWork, node, createOperator, currentOperator, snapshot.toBindData(), opinion, historyRecords);
                List<? extends IFlowOperator> matcherOperators = node.loadFlowNodeOperator(content, flowOperatorRepository);
                if (!matcherOperators.isEmpty()) {
                    for (IFlowOperator matcherOperator : matcherOperators) {
                        String recordTitle = node.generateTitle(content);
                        FlowRecord record = node.createRecord(flowWork.getId(), flowWork.getCode(), processId, preId, recordTitle, createOperator, matcherOperator, snapshot, opinion.isWaiting());
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
     * 下一节点的类型
     */
    public boolean nextNodeIsCirculate() {
        return nextNode.isCirculate();
    }


    /**
     * 下一节点是否结束节点
     */
    public boolean nextNodeIsOver() {
        return nextNode.isOverNode();
    }
}
