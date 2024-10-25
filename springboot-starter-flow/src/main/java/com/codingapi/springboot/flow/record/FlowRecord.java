package com.codingapi.springboot.flow.record;

import com.codingapi.springboot.flow.bind.BindDataSnapshot;
import com.codingapi.springboot.flow.domain.FlowNode;
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
     * 完成时间
     */
    private long finishTime;

    /**
     * 超时到期时间
     */
    private long timeoutTime;

    /**
     * 延期次数
     */
    private int postponedCount;

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
     * 是否干预
     */
    private boolean interfere;

    /**
     * 已读时间
     */
    private long readTime;

    /**
     * 延期时间
     *
     * @param postponedMax 最大延期次数
     * @param time         延期时间(毫秒)
     */
    public void postponedTime(int postponedMax, long time) {
        if (this.postponedCount >= postponedMax) {
            throw new IllegalArgumentException("postponed count is max");
        }
        this.read();
        if (this.timeoutTime == 0) {
            this.timeoutTime = System.currentTimeMillis();
        }
        this.timeoutTime += time;
        this.postponedCount++;
        this.updateTime = System.currentTimeMillis();
    }

    /**
     * 是否是发起节点
     */
    public boolean isInitiated() {
        return preId == 0 && this.nodeCode.equals(FlowNode.CODE_START);
    }

    /**
     * 已读
     */
    public void read() {
        this.read = true;
        this.readTime = System.currentTimeMillis();
    }

    /**
     * 是否未读
     */
    public boolean isUnRead() {
        return !this.read;
    }


    /**
     * 更新时间
     */
    public void update(Opinion opinion) {
        this.opinion = opinion;
        this.updateTime = System.currentTimeMillis();
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
     * @param pass         是否通过
     */
    public void submitRecord(IFlowOperator flowOperator, BindDataSnapshot snapshot, Opinion opinion, boolean pass) {
        if (!flowOperator.isFlowManager()) {
            if (flowOperator.getUserId() != this.currentOperatorId) {
                throw new IllegalArgumentException("current operator is not match");
            }
        } else {
            this.currentOperatorId = flowOperator.getUserId();
            this.interfere = true;
        }
        this.read();
        this.pass = pass;
        this.recodeType = RecodeType.DONE;
        this.updateTime = System.currentTimeMillis();
        this.snapshotId = snapshot.getId();
        this.bindClass = snapshot.getClazzName();
        this.opinion = opinion;
    }

    /**
     * 转交流程
     */
    public void transfer(IFlowOperator flowOperator, BindDataSnapshot snapshot, Opinion opinion) {
        if (flowOperator.getUserId() != this.currentOperatorId) {
            throw new IllegalArgumentException("current operator is not match");
        }
        this.read();
        this.pass = true;
        this.recodeType = RecodeType.TRANSFER;
        this.updateTime = System.currentTimeMillis();
        this.snapshotId = snapshot.getId();
        this.bindClass = snapshot.getClazzName();
        this.opinion = opinion;
    }

    /**
     * 自动提交流程 (非会签时自通审批)
     *
     * @param flowOperator 操作者
     * @param snapshot     绑定数据
     */
    public void unSignAutoDone(IFlowOperator flowOperator, BindDataSnapshot snapshot) {
        this.read();
        this.pass = true;
        this.currentOperatorId = flowOperator.getUserId();
        this.recodeType = RecodeType.DONE;
        this.updateTime = System.currentTimeMillis();
        this.snapshotId = snapshot.getId();
        this.bindClass = snapshot.getClazzName();
        this.opinion = Opinion.unSignAutoSuccess();
    }


    /**
     * 完成流程
     */
    public void finish() {
        this.flowStatus = FlowStatus.FINISH;
        this.finishTime = System.currentTimeMillis();
    }


    /**
     * 是否已审批
     */
    public boolean isDone() {
        return this.recodeType == RecodeType.DONE;
    }

    /**
     * 是否完成
     */
    public boolean isFinish() {
        return this.flowStatus == FlowStatus.FINISH;
    }

    /**
     * 是否是待办
     */
    public boolean isTodo() {
        return this.recodeType == RecodeType.TODO && this.flowStatus == FlowStatus.RUNNING;
    }

    /**
     * 是否是转交
     *
     * @return 是否是转交
     */
    public boolean isTransfer() {
        return this.recodeType == RecodeType.TRANSFER;
    }

    /**
     * 审批通过
     */
    public boolean isPass() {
        return this.opinion != null && this.opinion.isSuccess() && isDone();
    }

    public void matcherOperator(IFlowOperator currentOperator) {
        if (currentOperator.getUserId() != this.currentOperatorId) {
            throw new IllegalArgumentException("current operator is not match");
        }
    }

    /**
     * 撤回流程
     */
    public void recall() {
        this.recodeType = RecodeType.TODO;
        this.updateTime = System.currentTimeMillis();
    }


    public FlowRecord copy() {
        FlowRecord record = new FlowRecord();
        record.setId(this.id);
        record.setPostponedCount(this.postponedCount);
        record.setPreId(this.preId);
        record.setWorkId(this.workId);
        record.setProcessId(this.processId);
        record.setNodeCode(this.nodeCode);
        record.setTitle(this.title);
        record.setCurrentOperatorId(this.currentOperatorId);
        record.setRecodeType(this.recodeType);
        record.setPass(this.pass);
        record.setCreateTime(this.createTime);
        record.setUpdateTime(this.updateTime);
        record.setFinishTime(this.finishTime);
        record.setTimeoutTime(this.timeoutTime);
        record.setCreateOperatorId(this.createOperatorId);
        record.setOpinion(this.opinion);
        record.setFlowStatus(this.flowStatus);
        record.setErrMessage(this.errMessage);
        record.setBindClass(this.bindClass);
        record.setSnapshotId(this.snapshotId);
        record.setRead(this.read);
        record.setInterfere(this.interfere);
        record.setReadTime(this.readTime);
        return record;
    }

    /**
     * 转待办
     *
     * @param title    标题
     * @param operator 操作者
     */
    public void toTodo(String title, IFlowOperator operator) {
        this.id = 0;
        this.recodeType = RecodeType.TODO;
        this.flowStatus = FlowStatus.RUNNING;
        this.postponedCount = 0;
        this.updateTime = 0;
        this.readTime = 0;
        this.read = false;
        this.title = title;
        this.opinion = null;
        this.pass = false;
        this.currentOperatorId = operator.getUserId();
    }

    /**
     * 是否超时
     */
    public boolean isTimeout() {
        if (this.timeoutTime == 0) {
            return false;
        }
        return System.currentTimeMillis() > this.timeoutTime;
    }

    /**
     * 是否延期
     */
    public boolean isPostponed() {
        return this.postponedCount > 0;
    }
}
