package com.codingapi.example.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
public class FlowNodeEntity {

    /**
     * 节点id
     */
    @Id
    private String id;


    private Long workId;

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
    @Lob
    private String titleGenerator;

    /**
     * 节点类型 | 分为发起、审批、结束
     */
    private String type;

    /**
     * 节点视图
     */
    private String view;

    /**
     * 流程审批类型 | 分为会签、非会签
     */
    private String approvalType;

    /**
     * 操作者匹配器
     */
    @Lob
    private String operatorMatcher;

    /**
     * 是否可编辑
     */
    private Boolean editable;

    /**
     * 创建时间
     */
    private Long createTime;
    /**
     * 更新时间
     */
    private Long updateTime;

    /**
     * 超时时间（毫秒）
     */
    private Long timeout;

    /**
     * 异常触发器，当流程发生异常时异常通常是指找不到审批人，将会触发异常触发器，异常触发器可以是一个节点
     */
    @Lob
    private String errTrigger;
}
