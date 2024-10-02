package com.codingapi.example.infrastructure.entity.flow;

import com.codingapi.springboot.flow.em.FlowType;
import com.codingapi.springboot.flow.em.NodeType;
import jakarta.persistence.*;
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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

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
    private long flowWorkId;

    /**
     * 节点标题创建规则
     */
    @Lob
    private String titleCreatorScript;
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
    @Lob
    private String outOperatorMatcherScript;
    /**
     * 出口触发器
     */
    @Lob
    private String outTriggerScript;

    /**
     * 下一个节点数组，系统将根据出口配置，选择下一个节点
     */
    private String next;

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
    private long createUserId;
    /**
     * 异常触发器，当流程发生异常时异常通常是指找不到审批人，将会触发异常触发器，异常触发器可以是一个节点
     */
    @Lob
    private String errTriggerScript;

    /**
     * 异常操作者匹配器
     */
    @Lob
    private String errOperatorMatcherScript;


}
