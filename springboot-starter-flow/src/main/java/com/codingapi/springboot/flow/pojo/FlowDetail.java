package com.codingapi.springboot.flow.pojo;

import com.codingapi.springboot.flow.bind.BindDataSnapshot;
import com.codingapi.springboot.flow.bind.IBindData;
import com.codingapi.springboot.flow.domain.FlowNode;
import com.codingapi.springboot.flow.domain.FlowWork;
import com.codingapi.springboot.flow.domain.Opinion;
import com.codingapi.springboot.flow.record.FlowMerge;
import com.codingapi.springboot.flow.record.FlowRecord;
import com.codingapi.springboot.flow.user.IFlowOperator;
import lombok.Getter;

import java.util.List;


/**
 * 流程详情的阻止对象
 */
@Getter
public class FlowDetail {

    /**
     * 当前流程
     */
    private final FlowRecord flowRecord;
    /**
     * 流程设计器
     */
    private final FlowWork flowWork;
    /**
     * 流程节点
     */
    private final FlowNode flowNode;
    /**
     * 历史流程
     */
    private final List<FlowRecord> historyRecords;
    /**
     * 绑定数据
     */
    private final IBindData bindData;
    /**
     * 全部的操作人
     */
    private final List<? extends IFlowOperator> operators;

    /**
     * 流程创建者
     */
    private final IFlowOperator flowCreator;

    /**
     * 流程创建时间
     */
    private final long flowCreateTime;

    /**
     * 流程的意见
     */
    private final List<FlowOpinion> opinions;

    /**
     * 是否可以办理
     */
    private final boolean canHandle;

    /**
     * 合并记录
     */
    private final List<FlowMerge> mergeRecords;


    public FlowDetail(FlowRecord flowRecord,
                      List<FlowMerge> mergeRecords,
                      BindDataSnapshot snapshot,
                      FlowWork flowWork,
                      List<FlowRecord> historyRecords,
                      List<? extends IFlowOperator> operators,
                      boolean canHandle) {
        this.operators = operators;
        this.flowRecord = flowRecord;
        this.mergeRecords = mergeRecords;
        this.flowWork = flowWork;
        this.bindData = snapshot.toBindData();
        this.historyRecords = historyRecords;
        this.opinions = historyRecords.stream().map(FlowOpinion::new).toList();
        this.flowCreator = flowRecord.getCreateOperator();
        this.flowCreateTime = flowRecord.getCreateTime();
        this.flowNode = flowWork.getNodeByCode(flowRecord.getNodeCode());
        this.canHandle = canHandle;
    }

    public FlowDetail(FlowWork flowWork,
                      FlowNode flowNode,
                      List<? extends IFlowOperator> operators,
                        boolean canHandle) {
        this.flowWork = flowWork;
        this.flowNode = flowNode;
        this.operators = operators;
        this.flowCreateTime = 0;
        this.flowRecord = null;
        this.mergeRecords = null;
        this.historyRecords = null;
        this.bindData = null;
        this.opinions = null;
        this.flowCreator = null;
        this.canHandle = canHandle;
    }

    @Getter
    public final class FlowOpinion {
        private final long recordId;
        private final Opinion opinion;
        private final String nodeCode;
        private final String nodeName;
        private final IFlowOperator operator;
        private final long createTime;

        public FlowOpinion(FlowRecord flowRecord) {
            this.nodeCode = flowRecord.getNodeCode();
            this.nodeName = flowWork.getNodeByCode(nodeCode).getName();
            this.recordId = flowRecord.getId();
            this.opinion = flowRecord.getOpinion();
            this.operator = flowRecord.getCurrentOperator();
            this.createTime = flowRecord.getUpdateTime();
        }
    }

}
