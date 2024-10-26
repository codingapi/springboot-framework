package com.codingapi.example.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
public class FlowRecordEntity {

    /**
     * 流程记录id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 上一个流程记录id
     */
    private Long preId;

    /**
     * 工作id
     */
    private Long workId;
    /**
     * 流程id
     */
    private String processId;

    /**
     * 节点
     */
    private String nodeCode;

    /**
     * 流程标题
     */
    private String title;
    /**
     * 当前操作者
     */
    private Long currentOperatorId;

    /**
     * 当前操作者的名称
     */
    private String currentOperatorName;

    /**
     * 节点类型 | 待办、已办、转办
     */
    private String flowType;

    /**
     * 流转产生方式 ｜ 同意、拒绝、转办
     */
    private String flowSourceDirection;

    /**
     * 创建时间
     */
    private Long createTime;

    /**
     * 更新时间
     */
    private Long updateTime;

    /**
     * 完成时间
     */
    private Long finishTime;

    /**
     * 超时到期时间
     */
    private Long timeoutTime;

    /**
     * 延期次数
     */
    private Integer postponedCount;

    /**
     * 发起者id
     */
    private Long createOperatorId;

    /**
     * 发起者名称
     */
    private String createOperatorName;
    /**
     * 审批意见
     */
    private String opinionAdvice;
    private Integer opinionResult;
    private Integer opinionType;

    /**
     * 流程状态 ｜ 进行中、已完成
     */
    private String flowStatus;
    /**
     * 异常信息
     */
    private String errMessage;

    /**
     * 绑定数据的类
     */
    private String bindClass;

    /**
     * 绑定数据的快照
     */
    private Long snapshotId;

    /**
     * 是否已读
     */
    @Column(name = "m_read")
    private Boolean read;

    /**
     * 是否干预
     */
    private Boolean interfere;

    /**
     * 被干预的用户
     */
    private Long interferedOperatorId;

    /**
     * 被干预的用户
     */
    private String interferedOperatorName;

    /**
     * 已读时间
     */
    private Long readTime;
}
