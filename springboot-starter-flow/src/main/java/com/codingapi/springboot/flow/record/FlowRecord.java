package com.codingapi.springboot.flow.record;

import com.codingapi.springboot.flow.bind.BindDataSnapshot;
import com.codingapi.springboot.flow.domain.Opinion;
import com.codingapi.springboot.flow.em.FlowStatus;
import com.codingapi.springboot.flow.em.RecodeType;
import lombok.Getter;

@Getter
public class FlowRecord<ID> {

    /**
     * 流程记录id
     */
    private long id;
    /**
     * 工作id
     */
    private long workId;
    /**
     * 流程id
     */
    private String processId;

    /**
     * 节点
     */
    private String nodeId;
    /**
     * 流程标题
     */
    private String title;
    /**
     * 当前操作者
     */
    private ID currentOperatorId;
    /**
     * 节点状态 | 待办、已办
     */
    private RecodeType recodeType;
    /**
     * 创建时间
     */
    private long createTime;

    /**
     * 超时到期时间
     */
    private long timeoutTime;

    /**
     * 发起者id
     */
    private ID createOperatorId;
    /**
     * 审批意见
     */
    private Opinion opinion;
    /**
     * 流程状态 ｜ 进行中、已完成
     */
    private FlowStatus flowStatus;
    /**
     * 异常信息
     */
    private String errMessage;
    /**
     * 绑定数据的快照
     */
    private BindDataSnapshot snapshotId;
}
