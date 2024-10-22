package com.codingapi.springboot.flow.domain;

import com.codingapi.springboot.flow.em.ApprovalType;
import com.codingapi.springboot.flow.em.NodeType;
import com.codingapi.springboot.flow.generator.ITitleGenerator;
import com.codingapi.springboot.flow.matcher.IOperatorMatcher;
import com.codingapi.springboot.flow.trigger.IErrTrigger;
import lombok.Getter;
import lombok.Setter;

/**
 * 流程节点
 */
@Getter
public class FlowNode {

    public static final String CODE_START = "start";
    public static final String CODE_OVER = "over";

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
    private ITitleGenerator titleGenerator;

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
    private IOperatorMatcher operatorMatcher;

    /**
     * 创建时间
     */
    private long createTime;
    /**
     * 更新时间
     */
    private long updateTime;

    /**
     * 异常触发器，当流程发生异常时异常通常是指找不到审批人，将会触发异常触发器，异常触发器可以是一个节点
     */
    @Setter
    private IErrTrigger errTrigger;


    public FlowNode(String id, String name,
                    String code, String view,
                    NodeType type,ApprovalType approvalType,
                    ITitleGenerator titleGenerator,
                    IOperatorMatcher operatorMatcher) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.titleGenerator = titleGenerator;
        this.type = type;
        this.view = view;
        this.approvalType = approvalType;
        this.operatorMatcher = operatorMatcher;
        this.createTime = System.currentTimeMillis();
        this.updateTime = System.currentTimeMillis();
    }


}
