package com.codingapi.springboot.flow.service;

import com.codingapi.springboot.flow.bind.BindDataSnapshot;
import com.codingapi.springboot.flow.content.FlowContent;
import com.codingapi.springboot.flow.domain.FlowNode;
import com.codingapi.springboot.flow.domain.FlowRelation;
import com.codingapi.springboot.flow.domain.FlowWork;
import com.codingapi.springboot.flow.domain.Opinion;
import com.codingapi.springboot.flow.error.ErrorResult;
import com.codingapi.springboot.flow.error.NodeResult;
import com.codingapi.springboot.flow.error.OperatorResult;
import com.codingapi.springboot.flow.record.FlowRecord;
import com.codingapi.springboot.flow.repository.FlowOperatorRepository;
import com.codingapi.springboot.flow.user.IFlowOperator;

import java.util.ArrayList;
import java.util.List;

public class FlowRecordService {

    private final FlowOperatorRepository flowOperatorRepository;
    private final String processId;
    private final IFlowOperator createOperator;
    private final IFlowOperator currentOperator;
    private final BindDataSnapshot snapshot;
    private final Opinion opinion;
    private final FlowWork flowWork;
    private final List<FlowRecord> historyRecords;


    public FlowRecordService(FlowOperatorRepository flowOperatorRepository,
                             String processId,
                             IFlowOperator createOperator,
                             IFlowOperator currentOperator,
                             BindDataSnapshot snapshot,
                             Opinion opinion,
                             FlowWork flowWork,
                             List<FlowRecord> historyRecords) {
        this.flowOperatorRepository = flowOperatorRepository;
        this.processId = processId;
        this.createOperator = createOperator;
        this.currentOperator = currentOperator;
        this.snapshot = snapshot;
        this.opinion = opinion;
        this.flowWork = flowWork;
        this.historyRecords = historyRecords;
    }


    /**
     * 创建流程记录
     *
     * @param preId       上一条流程记录id
     * @param currentNode 当前节点
     * @return 流程记录
     */
    public List<FlowRecord> createRecord(long preId, FlowNode currentNode) {
        FlowContent flowContent = new FlowContent(flowWork, currentNode, createOperator, currentOperator, snapshot.toBindData(), opinion, historyRecords);
        long workId = flowWork.getId();
        List<? extends IFlowOperator> operators = currentNode.loadFlowNodeOperator(flowContent, flowOperatorRepository);
        if (operators.isEmpty()) {
            List<FlowRecord> errorRecordList = this.errMatcher(currentNode, preId, currentOperator);
            if (errorRecordList.isEmpty()) {
                throw new IllegalArgumentException("operator not match.");
            }
            return errorRecordList;
        } else {
            String recordTitle = currentNode.generateTitle(flowContent);
            List<FlowRecord> recordList = new ArrayList<>();
            for (IFlowOperator operator : operators) {
                FlowRecord record = currentNode.createRecord(workId, processId, preId, recordTitle, createOperator, operator, snapshot, opinion);
                recordList.add(record);
            }
            return recordList;
        }
    }


    /**
     * 获取下一个节点
     *
     * @param currentNode 当前节点
     * @return 下一个节点
     */
    public FlowNode getNextNode(FlowNode currentNode) {
        List<FlowRelation> relations = flowWork.getRelations().stream().filter(relation -> relation.matchNode(currentNode.getCode())).toList();
        if (relations.isEmpty()) {
            throw new IllegalArgumentException("relation not found");
        }
        FlowContent flowContent = new FlowContent(flowWork, currentNode, createOperator, currentOperator, snapshot.toBindData(), opinion, historyRecords);
        List<FlowNode> flowNodes = new ArrayList<>();
        for (FlowRelation flowRelation : relations) {
            FlowNode node = flowRelation.trigger(flowContent);
            if (node != null) {
                flowNodes.add(node);
                if (flowRelation.isDefaultOut()) {
                    return node;
                }
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
     * @param preId           上一条流程记录id
     * @param currentOperator 当前操作者
     * @return 流程记录
     */
    private List<FlowRecord> errMatcher(FlowNode currentNode, long preId, IFlowOperator currentOperator) {
        if (currentNode.hasErrTrigger()) {
            FlowContent flowContent = new FlowContent(flowWork, currentNode, createOperator, currentOperator, snapshot.toBindData(), opinion, historyRecords);
            ErrorResult errorResult = currentNode.errMatcher(flowContent);
            if (errorResult == null) {
                throw new IllegalArgumentException("errMatcher match error.");
            }

            // 匹配操作者
            if (errorResult.isOperator()) {
                List<FlowRecord> recordList = new ArrayList<>();
                List<IFlowOperator> operators = ((OperatorResult) errorResult).getOperators();
                for (IFlowOperator operator : operators) {
                    List<FlowRecord> records = triggerNextNode(preId, currentNode, operator);
                    if (!records.isEmpty()) {
                        recordList.addAll(records);
                    }
                }
                return recordList;
            }
            // 匹配节点
            if (errorResult.isNode()) {
                String nodeCode = ((NodeResult) errorResult).getNode();
                FlowNode node = flowWork.getNodeByCode(nodeCode);
                return triggerNextNode(preId, node, currentOperator);
            }
            throw new IllegalArgumentException("errMatcher not match.");
        }
        throw new IllegalArgumentException("operator not match.");
    }


    /**
     * 触发下一个节点
     * @param preId 上一条流程记录id
     * @param currentNode 当前节点
     * @param currentOperator 当前操作者
     * @return 流程记录
     */
    private List<FlowRecord> triggerNextNode(long preId, FlowNode currentNode, IFlowOperator currentOperator) {
        List<FlowRecord> records = new ArrayList<>();
        FlowContent content = new FlowContent(flowWork, currentNode, createOperator, currentOperator, snapshot.toBindData(), opinion, historyRecords);
        List<? extends IFlowOperator> matcherOperators = currentNode.loadFlowNodeOperator(content, flowOperatorRepository);
        if (!matcherOperators.isEmpty()) {
            for (IFlowOperator matcherOperator : matcherOperators) {
                String recordTitle = currentNode.generateTitle(content);
                FlowRecord record = currentNode.createRecord(flowWork.getId(), processId, preId, recordTitle, createOperator, matcherOperator, snapshot, opinion);
                records.add(record);
            }
        }
        return records;
    }

}
