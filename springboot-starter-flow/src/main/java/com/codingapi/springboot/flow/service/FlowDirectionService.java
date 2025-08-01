package com.codingapi.springboot.flow.service;

import com.codingapi.springboot.flow.domain.FlowNode;
import com.codingapi.springboot.flow.domain.FlowWork;
import com.codingapi.springboot.flow.domain.Opinion;
import com.codingapi.springboot.flow.em.FlowSourceDirection;
import com.codingapi.springboot.flow.record.FlowRecord;
import lombok.Getter;

import java.util.List;

/**
 * 流程方向服务
 */
public class FlowDirectionService {

    private final FlowWork flowWork;
    private final FlowNode flowNode;
    private final Opinion opinion;

    @Getter
    private FlowSourceDirection flowSourceDirection;


    private List<FlowRecord> historyRecords;

    public FlowDirectionService(FlowNode flowNode, FlowWork flowWork, Opinion opinion) {
        this.flowNode = flowNode;
        this.opinion = opinion;
        this.flowWork = flowWork;
    }


    public void bindHistoryRecords(List<FlowRecord> historyRecords) {
        this.historyRecords = historyRecords;
    }


    /**
     * 解析当前的审批方向
     */
    public void loadFlowSourceDirection() {
        if (opinion.isSuccess() || opinion.isWaiting()) {
            flowSourceDirection = FlowSourceDirection.PASS;
        }
        if (opinion.isReject()) {
            flowSourceDirection = FlowSourceDirection.REJECT;
        }
    }


    /**
     * 重新加载审批方向
     * 根据会签结果判断是否需要重新设置审批方向
     */
    public FlowSourceDirection reloadFlowSourceDirection() {
        if (flowNode.isSign()) {
            boolean allPass = historyRecords.stream().filter(item -> !item.isTransfer())
                    .allMatch(item-> item.isPass() || item.getOpinion().isWaiting());
            if (!allPass) {
                flowSourceDirection = FlowSourceDirection.REJECT;
            }
        }
        return flowSourceDirection;
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
     * 判断当前流程节点是否已经完成，是否可以继续流转
     */
    public boolean hasCurrentFlowNodeIsDone() {
        // 会签下所有人尚未提交时，不执行下一节点
        return historyRecords.stream().filter(item -> !item.isTransfer()).allMatch(FlowRecord::isDone);
    }

    /**
     * 判断当前流程是否为通过流程
     */
    public boolean isPassRecord() {
        return flowSourceDirection == FlowSourceDirection.PASS;
    }

}
