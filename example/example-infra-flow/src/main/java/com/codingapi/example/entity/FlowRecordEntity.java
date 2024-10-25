package com.codingapi.example.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
     * 节点状态 | 待办、已办
     */
    private String recodeType;

    /**
     * 流转产生方式
     * 流程是退回产生的还是通过产生的
     */
    private Boolean pass;

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
     * 审批意见
     */
    private String opinionAdvice;
    private Boolean opinionSuccess;
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
    private Boolean read;

    /**
     * 是否干预
     */
    private Boolean interfere;

    /**
     * 已读时间
     */
    private Long readTime;
}