package com.codingapi.springboot.flow.domain;

import com.codingapi.springboot.flow.context.OperatorMatcher;
import com.codingapi.springboot.flow.creator.ITitleCreator;
import com.codingapi.springboot.flow.data.IBindData;
import com.codingapi.springboot.flow.em.*;
import com.codingapi.springboot.flow.matcher.IOperatorMatcher;
import com.codingapi.springboot.flow.operator.IFlowOperator;
import com.codingapi.springboot.flow.trigger.IErrTrigger;
import com.codingapi.springboot.flow.trigger.IOutTrigger;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

/**
 * 流程节点，约定流程的节点执行逻辑
 * 流程的节点类型：会签、非会签，会签代表所有审批人都需要审批通过，非会签代表只需要一个审批人审批通过即可
 * 流程的出口配置，约定流程的出口配置
 * 流程的操作者配置，约定流程的操作者配置
 */
@Setter
@Getter
@ToString(exclude =
        {"flowWork", "outOperatorMatcher", "outTrigger", "errTrigger","errOperatorMatcher", "titleCreator"})
public class FlowNode {

    public static final String CODE_START = "start";
    public static final String CODE_OVER = "over";

    public static final String VIEW_DEFAULT = "default";

    /**
     * 节点id
     */
    private String id;

    /**
     * 节点编码
     */
    private String code;

    /**
     * 节点名称
     */
    private String name;

    /**
     * 流程设计
     */
    @JsonIgnore
    private FlowWork flowWork;

    /**
     * 节点标题创建规则
     */
    @JsonIgnore
    private ITitleCreator titleCreator;
    /**
     * 节点类型 | 分为发起、审批、结束
     */
    private NodeType type;
    /**
     * 节点视图
     */
    private String view;
    /**
     * 流程审批类型 | 分为会签、非会签
     */
    private FlowType flowType;
    /**
     * 操作者匹配器
     */
    @JsonIgnore
    private IOperatorMatcher outOperatorMatcher;
    /**
     * 出口触发器
     */
    @JsonIgnore
    private IOutTrigger outTrigger;

    /**
     * 下一个节点数组，系统将根据出口配置，选择下一个节点
     */
    @JsonIgnore
    private List<String> next;


    /**
     * 父节点编码
     */
    private String parentCode;


    /**
     * 创建时间
     */
    private long createTime;
    /**
     * 更新时间
     */
    private long updateTime;
    /**
     * 设计者
     */
    @JsonIgnore
    private IFlowOperator createUser;
    /**
     * 异常触发器，当流程发生异常时异常通常是指找不到审批人，将会触发异常触发器，异常触发器可以是一个节点
     */
    @JsonIgnore
    private IErrTrigger errTrigger;

    /**
     * 异常操作者匹配器
     */
    @JsonIgnore
    private IOperatorMatcher errOperatorMatcher;


    /**
     * 添加下一个节点
     *
     * @param flowNode 下一个节点
     */
    public void addNextNode(FlowNode flowNode) {
        if (next == null) {
            this.next = new ArrayList<>();
        }
        if (!next.contains(flowNode.getCode())) {
            this.next.add(flowNode.getCode());
        }
    }

    /**
     * 获取上一节点
     * @return 上一节点
     */
    @JsonIgnore
    public FlowNode getPreNode() {
        if (this.getParentCode() == null) {
            return flowWork.getFlowNode(FlowNode.CODE_START);
        }
        return flowWork.getFlowNode(this.getParentCode());
    }

    /**
     * 匹配操作者
     *
     * @param operator 操作者
     */
    public void verifyOperator(IFlowOperator operator) {
        List<? extends IFlowOperator> operators = OperatorMatcher.matcher(outOperatorMatcher, operator);
        List<Long> operatorIds = operators.stream().map(IFlowOperator::getId).toList();
        if (!operatorIds.contains(operator.getId())) {
            throw new RuntimeException("operator not match.");
        }
    }

    /**
     * 创建流程记录
     */
    public FlowRecord createRecord(long processId,
                                   long preRecordId,
                                   String preNodeCode,
                                   IBindData bindData,
                                   IFlowOperator operatorUser,
                                   IFlowOperator createOperatorUser) {
        return createRecord(null,preRecordId,preNodeCode, processId, bindData, operatorUser, createOperatorUser);
    }

    /**
     * 创建流程记录
     */
    public FlowRecord createRecord(Opinion opinion,
                                   long preRecordId,
                                   String preNodeCode,
                                   long processId,
                                   IBindData bindData,
                                   IFlowOperator operatorUser,
                                   IFlowOperator createOperatorUser) {
        FlowRecord record = new FlowRecord();
        record.bindData(bindData);
        record.setProcessId(processId);
        record.setNode(this);
        record.setPreRecordId(preRecordId);
        record.setPreNodeCode(preNodeCode);
        record.setWork(flowWork);
        record.setOpinion(opinion);
        record.setOperatorUser(operatorUser);
        record.setCreateTime(System.currentTimeMillis());
        record.setCreateOperatorUser(createOperatorUser);
        record.setNodeStatus(NodeStatus.TODO);
        record.setFlowStatus(FlowStatus.RUNNING);
        record.setState(RecodeState.NORMAL);
        record.setTitle(createTitle(record));

        return record;
    }


    /**
     * 创建流程标题
     *
     * @param record 流程记录
     * @return 标题
     */
    public String createTitle(FlowRecord record) {
        return this.titleCreator.createTitle(record);
    }

    /**
     * 触发下一个节点
     * @param record 流程记录
     * @return 下一个节点
     */
    public FlowNode triggerNextNode(FlowRecord record) {
        if (outTrigger != null) {
            return outTrigger.trigger(record);
        }
        return null;
    }

    /**
     * 触发异常节点
     * @param record 流程记录
     * @return 异常节点
     */
    public FlowNode triggerErrorNode(FlowRecord record) {
        if (errTrigger != null) {
            return errTrigger.trigger(record);
        }
        return null;
    }

    public boolean isOver() {
        return CODE_OVER.equals(this.code);
    }

    public boolean isStart() {
        return CODE_START.equals(this.code);
    }

    /**
     * 匹配出口操作者
     *
     * @param record 流程记录
     * @return 操作者
     */
    public List<? extends IFlowOperator> matchOutOperators(FlowRecord record) {
        return OperatorMatcher.matcher(outOperatorMatcher, record);
    }

    /**
     * 匹配出口操作者
     *
     * @param record 流程记录
     * @return 操作者
     */
    public List<? extends IFlowOperator> matchErrorOperators(FlowRecord record) {
        return OperatorMatcher.matcher(errOperatorMatcher, record);
    }

    /**
     * 是否为指定节点
     * @param code 节点编码
     * @return 是否为指定节点
     */
    public boolean isCode(String code) {
        return this.code.equals(code);
    }
}
