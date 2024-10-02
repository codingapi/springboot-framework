package com.codingapi.example.infrastructure.entity.flow;

import com.codingapi.springboot.flow.em.FlowStatus;
import com.codingapi.springboot.flow.em.NodeStatus;
import com.codingapi.springboot.flow.em.RecodeState;
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
    private long id;
    /**
     * 工作id
     */
    private long workId;
    /**
     * 流程id
     */
    private long processId;

    /**
     * 父节点id
     */
    private long parentId;

    /**
     * 节点
     */
    private long nodeId;
    /**
     * 流程标题
     */
    private String title;
    /**
     * 操作者
     */
    private long operatorUserId;
    /**
     * 节点状态 | 待办、已办、专办
     */
    private NodeStatus nodeStatus;
    /**
     * 创建时间
     */
    private long createTime;

    /**
     * 更新时间
     */
    private long updateTime;
    /**
     * 发起者id
     */
    private long createOperatorUserId;
    /**
     * 审批意见
     */
    @Lob
    private String opinion;
    /**
     * 流程状态 ｜ 进行中、已完成
     */
    private FlowStatus flowStatus;
    /**
     * 记录状态 | 正常、异常，当流程发生异常时，将会记录异常状态，异常状态的流程将无法继续审批
     */
    private RecodeState state;
    /**
     * 异常信息
     */
    @Lob
    private String errMessage;
    /**
     * 绑定数据的快照
     */
    private long bindDataSnapshotId;

}
