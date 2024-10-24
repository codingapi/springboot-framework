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

/**
 * 流程记录服务（流程内部服务）
 */
class FlowRecordService {

    private final FlowOperatorRepository flowOperatorRepository;
    private final String processId;
    private final IFlowOperator createOperator;
    private IFlowOperator currentOperator;
    private final BindDataSnapshot snapshot;
    private final Opinion opinion;
    private final FlowWork flowWork;
    private final boolean pass;
    private final List<FlowRecord> historyRecords;


    /**
     * @param flowOperatorRepository 操作者仓库
     * @param processId              流程id
     * @param createOperator         流程发起者
     * @param currentOperator        当前操作者
     * @param snapshot               绑定数据快照
     * @param opinion                审批意见
     * @param flowWork               流程设计
     * @param pass                   流转方式 true是同意审批 false是退回
     * @param historyRecords         当前节点下的审批记录
     */
    public FlowRecordService(FlowOperatorRepository flowOperatorRepository,
                             String processId,
                             IFlowOperator createOperator,
                             IFlowOperator currentOperator,
                             BindDataSnapshot snapshot,
                             Opinion opinion,
                             FlowWork flowWork,
                             boolean pass,
                             List<FlowRecord> historyRecords) {
        this.flowOperatorRepository = flowOperatorRepository;
        this.processId = processId;
        this.createOperator = createOperator;
        this.currentOperator = currentOperator;
        this.snapshot = snapshot;
        this.opinion = opinion;
        this.flowWork = flowWork;
        this.pass = pass;
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
                FlowRecord record = currentNode.createRecord(workId, processId, preId, recordTitle, createOperator, operator, snapshot, opinion, pass);
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
    public FlowNode matcherNextNode(FlowNode currentNode) {
        List<FlowRelation> relations = flowWork.getRelations().stream()
                .filter(relation -> relation.isBack() == !pass && relation.sourceMatcher(currentNode.getCode()))
                .sorted((o1, o2) -> (o2.getOrder() - o1.getOrder()))
                .toList();
        if (relations.isEmpty()) {
            throw new IllegalArgumentException("relation not found");
        }
        FlowContent flowContent = new FlowContent(flowWork, currentNode, createOperator, currentOperator, snapshot.toBindData(), opinion, historyRecords);
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
                List<Long> operatorIds = ((OperatorResult) errorResult).getOperatorIds();
                List<? extends IFlowOperator> operators = flowOperatorRepository.findByIds(operatorIds);
                for (IFlowOperator operator : operators) {
                    FlowContent content = new FlowContent(flowWork, currentNode, createOperator, operator, snapshot.toBindData(), opinion, historyRecords);
                    String recordTitle = currentNode.generateTitle(content);
                    FlowRecord record = currentNode.createRecord(flowWork.getId(), processId, preId, recordTitle, createOperator, operator, snapshot, opinion, pass);
                    recordList.add(record);
                }
                return recordList;
            }
            // 匹配节点
            if (errorResult.isNode()) {
                String nodeCode = ((NodeResult) errorResult).getNode();
                FlowNode node = flowWork.getNodeByCode(nodeCode);
                List<FlowRecord> recordList = new ArrayList<>();
                FlowContent content = new FlowContent(flowWork, node, createOperator, currentOperator, snapshot.toBindData(), opinion, historyRecords);
                List<? extends IFlowOperator> matcherOperators = node.loadFlowNodeOperator(content, flowOperatorRepository);
                if (!matcherOperators.isEmpty()) {
                    for (IFlowOperator matcherOperator : matcherOperators) {
                        String recordTitle = node.generateTitle(content);
                        FlowRecord record = node.createRecord(flowWork.getId(), processId, preId, recordTitle, createOperator, matcherOperator, snapshot, opinion, pass);
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
     * 切换当前操作者
     *
     * @param flowOperator 操作者
     */
    public void changeCurrentOperator(IFlowOperator flowOperator) {
        this.currentOperator = flowOperator;
    }
}
