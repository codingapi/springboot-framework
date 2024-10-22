package com.codingapi.springboot.flow.record;

import com.codingapi.springboot.flow.domain.Opinion;
import com.codingapi.springboot.flow.em.FlowStatus;
import com.codingapi.springboot.flow.em.RecodeType;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class FlowRecord {

    /**
     * 流程记录id
     */
    private long id;

    /**
     * 上一个流程记录id
     */
    private long preId;

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
    private long currentOperatorId;
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
    private long createOperatorId;
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
     * 绑定数据的类
     */
    private String bindClass;

    /**
     * 绑定数据的快照
     */
    private long snapshotId;
}
