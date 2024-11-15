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
import com.codingapi.springboot.flow.record.FlowRecord;
import com.codingapi.springboot.flow.repository.FlowOperatorRepository;
import com.codingapi.springboot.flow.repository.FlowRecordRepository;
import com.codingapi.springboot.flow.user.IFlowOperator;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 *  流程记录构建服务
 */
class FlowRecordBuilderService {


    private final FlowOperatorRepository flowOperatorRepository;
    private final FlowRecordRepository flowRecordRepository;

    private final String processId;
    private final long preId;

    private final FlowWork flowWork;
    private final Opinion opinion;
    private final IFlowOperator currentOperator;
    private final BindDataSnapshot snapshot;
    private final List<FlowRecord> historyRecords;
    private final IFlowOperator createOperator;


    public FlowRecordBuilderService(FlowOperatorRepository flowOperatorRepository,
                                    FlowRecordRepository flowRecordRepository,
                                    BindDataSnapshot snapshot,
                                    Opinion opinion,
                                    IFlowOperator createOperator,
                                    IFlowOperator currentOperator,
                                    List<FlowRecord> historyRecords,
                                    FlowWork flowWork,
                                    String processId,
                                    long preId) {

        if(createOperator==null){
            throw new IllegalArgumentException("createOperator is null");
        }

        this.createOperator = createOperator;
        this.flowOperatorRepository = flowOperatorRepository;

        this.flowRecordRepository = flowRecordRepository;
        this.snapshot = snapshot;
        this.opinion = opinion;
        this.currentOperator = currentOperator;
        this.flowWork = flowWork;


        this.processId = processId;
        this.preId = preId;
        this.historyRecords = historyRecords;
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
                .collect(Collectors.toList());
        if (relations.isEmpty()) {
            throw new IllegalArgumentException("relation not found");
        }
        FlowSession flowSession = new FlowSession(flowWork, flowNode, createOperator, currentOperator, snapshot.toBindData(), opinion, historyRecords);
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
     * 异常匹配
     *
     * @param currentNode     当前节点
     * @param currentOperator 当前操作者
     * @return 流程记录
     */
    private List<FlowRecord> errMatcher(FlowNode currentNode, IFlowOperator currentOperator) {
        if (currentNode.hasErrTrigger()) {
            FlowSession flowSession = new FlowSession(flowWork, currentNode, createOperator, currentOperator, snapshot.toBindData(), opinion, historyRecords);
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
                    FlowSession content = new FlowSession(flowWork, currentNode, createOperator, operator, snapshot.toBindData(), opinion, historyRecords);
                    String recordTitle = currentNode.generateTitle(content);
                    FlowRecord record = currentNode.createRecord(flowWork.getId(),flowWork.getCode(), processId, preId, recordTitle, createOperator, operator, snapshot);
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
                FlowSession content = new FlowSession(flowWork, node, createOperator, currentOperator, snapshot.toBindData(), opinion, historyRecords);
                List<? extends IFlowOperator> matcherOperators = node.loadFlowNodeOperator(content, flowOperatorRepository);
                if (!matcherOperators.isEmpty()) {
                    for (IFlowOperator matcherOperator : matcherOperators) {
                        String recordTitle = node.generateTitle(content);
                        FlowRecord record = node.createRecord(flowWork.getId(),flowWork.getCode(), processId, preId, recordTitle, createOperator, matcherOperator, snapshot);
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
       return this.createRecord(currentNode,currentOperator,null);
    }

    /**
     * 创建流程记录
     *
     * @param currentNode       当前节点
     * @param currentOperator   当前审批人
     * @param opinion           审批意见
     * @return 流程记录
     */
    public List<FlowRecord> createRecord(FlowNode currentNode, IFlowOperator currentOperator,Opinion opinion) {
        FlowSession flowSession = new FlowSession(flowWork, currentNode, createOperator, currentOperator, snapshot.toBindData(), opinion, historyRecords);
        long workId = flowWork.getId();
        List<? extends IFlowOperator> operators = currentNode.loadFlowNodeOperator(flowSession, flowOperatorRepository);
        List<FlowRecord> recordList;
        if (operators.isEmpty()) {
            recordList= this.errMatcher(currentNode, currentOperator);
            if (recordList.isEmpty()) {
                throw new IllegalArgumentException("operator not match.");
            }
        } else {
            String recordTitle = currentNode.generateTitle(flowSession);
            recordList = new ArrayList<>();
            for (IFlowOperator operator : operators) {
                FlowRecord record = currentNode.createRecord(workId,flowWork.getCode(), processId, preId, recordTitle, createOperator, operator, snapshot);
                recordList.add(record);
            }
        }
        if(!recordList.isEmpty()){
            for (FlowRecord record:recordList){
                if(opinion!=null){
                    record.updateOpinion(opinion);
                }
            }
        }

        return recordList;
    }


    /**
     * 创建下一个节点
     */
    public List<FlowRecord> createNextRecord(FlowNode flowNode) {
        FlowNode nextNode = this.matcherNextNode(flowNode, false);
        return this.createRecord(nextNode, currentOperator);
    }

    /**
     * 创建自定义的下级别节点
     */
    public List<FlowRecord> createCustomBackRecord(FlowNode flowNode, long parentRecordId) {
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
        return this.createRecord(nextNode, flowOperator);
    }

    /**
     * 创建默认拒绝时的流程记录
     */
    public List<FlowRecord> createDefaultBackRecord(long parentRecordId) {
        IFlowOperator flowOperator;
        // 拒绝时，默认返回上一个节点
        FlowRecord preRecord = flowRecordRepository.getFlowRecordById(parentRecordId);
        // 去除所有的转办的记录
        while (preRecord.isTransfer()) {
            // 继续寻找上一个节点
            preRecord = flowRecordRepository.getFlowRecordById(preRecord.getPreId());
        }
        // 获取上一个节点的审批者，继续将审批者设置为当前审批者
        flowOperator = preRecord.getCurrentOperator();
        FlowNode nextNode = flowWork.getNodeByCode(preRecord.getNodeCode());
        if (nextNode == null) {
            throw new IllegalArgumentException("next node not found");
        }
        return this.createRecord(nextNode, flowOperator);
    }


}
