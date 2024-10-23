package com.codingapi.springboot.flow.record;

import com.codingapi.springboot.flow.bind.BindDataSnapshot;
import com.codingapi.springboot.flow.domain.Opinion;
import com.codingapi.springboot.flow.em.FlowStatus;
import com.codingapi.springboot.flow.em.RecodeType;
import com.codingapi.springboot.flow.user.IFlowOperator;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
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
    private String nodeCode;

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
     * 流转产生方式
     * 流程是退回产生的还是通过产生的
     */
    private boolean pass;

    /**
     * 创建时间
     */
    private long createTime;

    /**
     * 更新时间
     */
    private long updateTime;

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

    /**
     * 是否已读
     */
    private boolean read;

    /**
     * 已读时间
     */
    private long readTime;

    /**
     * 已读
     */
    public void read() {
        this.read = true;
        this.readTime = System.currentTimeMillis();
    }

    /**
     * 提交状态校验
     * 是否可以提交
     */
    public void submitStateVerify() {
        if (flowStatus == FlowStatus.FINISH) {
            throw new IllegalArgumentException("flow is finish");
        }
        if (recodeType == RecodeType.DONE) {
            throw new IllegalArgumentException("flow is done");
        }
    }

    /**
     * 提交流程
     *
     * @param flowOperator 操作者
     * @param snapshot     绑定数据
     * @param opinion      意见
     */
    public void done(IFlowOperator flowOperator, BindDataSnapshot snapshot, Opinion opinion) {
        if (flowOperator.getUserId() != this.currentOperatorId) {
            throw new IllegalArgumentException("current operator is not match");
        }
        this.read();
        this.recodeType = RecodeType.DONE;
        this.updateTime = System.currentTimeMillis();
        this.snapshotId = snapshot.getId();
        this.bindClass = snapshot.getClazzName();
        this.opinion = opinion;

    }

    /**
     * 自动提交流程
     *
     * @param flowOperator 操作者
     * @param snapshot     绑定数据
     */
    public void autoDone(IFlowOperator flowOperator, BindDataSnapshot snapshot) {
        this.read();
        this.currentOperatorId = flowOperator.getUserId();
        this.recodeType = RecodeType.DONE;
        this.updateTime = System.currentTimeMillis();
        this.snapshotId = snapshot.getId();
        this.bindClass = snapshot.getClazzName();
        this.opinion = Opinion.autoSuccess();
    }

    /**
     * 是否完成
     */
    public boolean isDone() {
        return this.recodeType == RecodeType.DONE;
    }

    /**
     * 是否是转交
     * @return 是否是转交
     */
    public boolean isTransfer(){
        return this.recodeType == RecodeType.TRANSFER;
    }

    /**
     * 审批通过
     */
    public boolean isPass() {
        return this.opinion.isSuccess() && isDone();
    }
}
