package com.codingapi.springboot.flow.domain;

import com.codingapi.springboot.flow.bind.BindDataSnapshot;
import com.codingapi.springboot.flow.content.FlowContent;
import com.codingapi.springboot.flow.em.*;
import com.codingapi.springboot.flow.error.ErrTrigger;
import com.codingapi.springboot.flow.error.ErrorResult;
import com.codingapi.springboot.flow.generator.TitleGenerator;
import com.codingapi.springboot.flow.matcher.OperatorMatcher;
import com.codingapi.springboot.flow.record.FlowRecord;
import com.codingapi.springboot.flow.repository.FlowOperatorRepository;
import com.codingapi.springboot.flow.serializable.FlowNodeSerializable;
import com.codingapi.springboot.flow.user.IFlowOperator;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 流程节点
 */
@Getter
@AllArgsConstructor
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
    private TitleGenerator titleGenerator;

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
    private OperatorMatcher operatorMatcher;

    /**
     * 是否可编辑
     */
    private boolean editable;

    /**
     * 创建时间
     */
    private long createTime;
    /**
     * 更新时间
     */
    private long updateTime;

    /**
     * 超时时间（毫秒）
     */
    private long timeout;

    /**
     * 异常触发器，当流程发生异常时异常通常是指找不到审批人，将会触发异常触发器，异常触发器可以是一个节点
     */
    @Setter
    private ErrTrigger errTrigger;


    public void verify(){
        if (this.titleGenerator == null) {
            throw new IllegalArgumentException("titleGenerator is null");
        }
        if (this.operatorMatcher == null) {
            throw new IllegalArgumentException("operatorMatcher is null");
        }
        if(timeout<0){
            throw new IllegalArgumentException("timeout is less than 0");
        }
        if(!StringUtils.hasLength(id)){
            throw new IllegalArgumentException("id is empty");
        }
        if(!StringUtils.hasLength(code)){
            throw new IllegalArgumentException("code is empty");
        }
    }


    /**
     * 从序列化对象中创建节点
     *
     * @return FlowNodeSerializable 序列号节点
     */
    public FlowNodeSerializable toSerializable() {
        return new FlowNodeSerializable(
                this.id,
                this.code,
                this.name,
                this.titleGenerator.getScript(),
                this.type,
                this.view,
                this.approvalType,
                this.operatorMatcher.getScript(),
                this.editable,
                this.createTime,
                this.updateTime,
                this.timeout,
                this.errTrigger == null ? null : this.errTrigger.getScript()
        );
    }


    public FlowNode(String id,
                    String name,
                    String code,
                    String view,
                    NodeType type,
                    ApprovalType approvalType,
                    TitleGenerator titleGenerator,
                    OperatorMatcher operatorMatcher,
                    long timeout,
                    ErrTrigger errTrigger,
                    boolean editable) {
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
        this.errTrigger = errTrigger;
        this.timeout = timeout;
        this.editable = editable;
    }


    /**
     * 加载节点的操作者
     *
     * @param flowContent 操作内容
     * @return 是否匹配
     */
    public List<? extends IFlowOperator> loadFlowNodeOperator(FlowContent flowContent, FlowOperatorRepository flowOperatorRepository) {
        return flowOperatorRepository.findByIds(this.operatorMatcher.matcher(flowContent));
    }


    /**
     * 创建流程记录
     *
     * @param workId          流程设计id
     * @param processId       流程id
     * @param preId           上一条流程记录id
     * @param title           流程标题
     * @param createOperator  流程操作者
     * @param currentOperator 当前操作者
     * @param snapshot        快照数据
     * @param opinion         审批意见
     * @param flowSourceDirection   流程方式
     * @return 流程记录
     */
    public FlowRecord createRecord(long workId,
                                   String processId,
                                   long preId,
                                   String title,
                                   IFlowOperator createOperator,
                                   IFlowOperator currentOperator,
                                   BindDataSnapshot snapshot,
                                   Opinion opinion,
                                   FlowSourceDirection flowSourceDirection) {

        // 当前操作者存在委托人时，才需要寻找委托人
        IFlowOperator flowOperator = currentOperator;
        while (flowOperator.entrustOperator() != null) {
            //寻找委托人
            flowOperator = flowOperator.entrustOperator();
        }

        FlowRecord record = new FlowRecord();
        record.setProcessId(processId);
        record.setNodeCode(this.code);
        record.setCreateTime(System.currentTimeMillis());
        record.setWorkId(workId);
        record.setFlowStatus(FlowStatus.RUNNING);
        record.setPostponedCount(0);
        record.setCreateOperatorId(createOperator.getUserId());
        record.setBindClass(snapshot.getClazzName());
        record.setOpinion(opinion);
        record.setCurrentOperatorId(flowOperator.getUserId());
        record.setPreId(preId);
        record.setTitle(title);
        record.setFlowSourceDirection(flowSourceDirection);
        record.setTimeoutTime(this.loadTimeoutTime());
        record.setFlowType(FlowType.TODO);
        record.setErrMessage(null);
        record.setSnapshotId(snapshot.getId());
        return record;
    }


    /**
     * 获取超时时间
     *
     * @return 超时时间
     */
    private long loadTimeoutTime() {
        if (this.timeout > 0) {
            return System.currentTimeMillis() + this.timeout;
        }
        return 0;
    }

    /**
     * 是否有任意操作者匹配
     */
    public boolean isAnyOperatorMatcher() {
        return operatorMatcher.isAny();
    }

    /**
     * 异常匹配
     *
     * @param flowContent 操作内容
     */
    public ErrorResult errMatcher(FlowContent flowContent) {
        if (errTrigger != null) {
            return errTrigger.trigger(flowContent);
        }
        return null;
    }

    /**
     * 是否有异常触发器
     *
     * @return 是否有异常触发器
     */
    public boolean hasErrTrigger() {
        return errTrigger != null;
    }

    /**
     * 生成标题
     *
     * @param flowContent 流程内容
     * @return 标题
     */
    public String generateTitle(FlowContent flowContent) {
        return titleGenerator.generate(flowContent);
    }


    /**
     * 是否会签节点
     */
    public boolean isSign() {
        return approvalType == ApprovalType.SIGN;
    }

    /**
     * 是否非会签节点
     */
    public boolean isUnSign() {
        return approvalType == ApprovalType.UN_SIGN;
    }

    /**
     * 是否结束节点
     */
    public boolean isOverNode() {
        return CODE_OVER.equals(this.code);
    }

    /**
     * 是否开始节点
     */
    public boolean isStartNode() {
        return CODE_START.equals(this.code);
    }
}
