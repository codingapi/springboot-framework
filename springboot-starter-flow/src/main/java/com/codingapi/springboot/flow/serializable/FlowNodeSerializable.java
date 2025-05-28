package com.codingapi.springboot.flow.serializable;

import com.codingapi.springboot.flow.domain.FlowButton;
import com.codingapi.springboot.flow.domain.FlowNode;
import com.codingapi.springboot.flow.em.ApprovalType;
import com.codingapi.springboot.flow.em.NodeType;
import com.codingapi.springboot.flow.error.ErrTrigger;
import com.codingapi.springboot.flow.generator.TitleGenerator;
import com.codingapi.springboot.flow.matcher.OperatorMatcher;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

/**
 * 流程节点序列化
 */
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class FlowNodeSerializable implements Serializable {

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
     * 节点标题创建规则
     */
    private String titleGenerator;

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
    private ApprovalType approvalType;

    /**
     * 操作者匹配器
     */
    private String operatorMatcher;

    /**
     * 是否可编辑
     */
    private boolean editable;

    /**
     * 是否可合并审批
     */
    private boolean mergeable;

    /**
     * 创建时间
     */
    private long createTime;
    /**
     * 更新时间
     */
    private long updateTime;

    /**
     * 超时时间（毫秒）
     */
    private long timeout;

    /**
     * 异常触发器，当流程发生异常时异常通常是指找不到审批人，将会触发异常触发器，异常触发器可以是一个节点
     */
    private String errTrigger;

    /**
     * 流程节点按钮
     */
    private List<FlowButton> buttons;

    public FlowNode toFlowNode() {
        return new FlowNode(id, code, name, new TitleGenerator(titleGenerator), type, view, approvalType,
                new OperatorMatcher(operatorMatcher), editable,mergeable, createTime, updateTime, timeout, errTrigger == null ? null : new ErrTrigger(errTrigger),buttons);
    }
}
