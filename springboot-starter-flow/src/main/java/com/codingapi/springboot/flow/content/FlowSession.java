package com.codingapi.springboot.flow.content;

import com.codingapi.springboot.flow.bind.IBindData;
import com.codingapi.springboot.flow.domain.FlowNode;
import com.codingapi.springboot.flow.domain.FlowWork;
import com.codingapi.springboot.flow.domain.Opinion;
import com.codingapi.springboot.flow.em.FlowSourceDirection;
import com.codingapi.springboot.flow.error.NodeResult;
import com.codingapi.springboot.flow.error.OperatorResult;
import com.codingapi.springboot.flow.pojo.FlowResult;
import com.codingapi.springboot.flow.pojo.FlowSubmitResult;
import com.codingapi.springboot.flow.record.FlowRecord;
import com.codingapi.springboot.flow.result.MessageResult;
import com.codingapi.springboot.flow.service.FlowService;
import com.codingapi.springboot.flow.user.IFlowOperator;
import lombok.Getter;

import java.util.List;

/**
 * 流程groovy脚本回话对象
 */
@Getter
public class FlowSession {

    // 当前的流程记录
    private final FlowRecord flowRecord;
    // 当前的流程设计器
    private final FlowWork flowWork;
    // 当前的流程节点
    private final FlowNode flowNode;
    // 流程的创建者
    private final IFlowOperator createOperator;
    // 当前的操作者
    private final IFlowOperator currentOperator;
    // 流程绑定数据
    private final IBindData bindData;
    // 流程审批意见
    private final Opinion opinion;
    // 当前节点的审批记录
    private final List<FlowRecord> historyRecords;
    // bean提供者
    private final FlowSessionBeanProvider provider;

    public FlowSession(FlowRecord flowRecord,
                       FlowWork flowWork,
                       FlowNode flowNode,
                       IFlowOperator createOperator,
                       IFlowOperator currentOperator,
                       IBindData bindData,
                       Opinion opinion,
                       List<FlowRecord> historyRecords) {
        this.flowRecord = flowRecord;
        this.flowWork = flowWork;
        this.flowNode = flowNode;
        this.createOperator = createOperator;
        this.currentOperator = currentOperator;
        this.bindData = bindData;
        this.opinion = opinion;
        this.historyRecords = historyRecords;
        this.provider = FlowSessionBeanProvider.getInstance();
    }


    public Object getBean(String beanName) {
        return provider.getBean(beanName);
    }


    /**
     *  获取审批意见
     */
    public String getAdvice() {
        if (opinion != null) {
            return opinion.getAdvice();
        } else {
            return null;
        }
    }

    /**
     * 创建节点结果
     *
     * @param nodeCode 节点code
     * @return 节点结果
     */
    public NodeResult createNodeErrTrigger(String nodeCode) {
        return new NodeResult(nodeCode);
    }

    /**
     * 创建操作者结果
     *
     * @param operatorIds 操作者id
     * @return 操作者结果
     */
    public OperatorResult createOperatorErrTrigger(List<Long> operatorIds) {
        return new OperatorResult(operatorIds);
    }

    /**
     * 创建操作者结果
     *
     * @param operatorIds 操作者id
     * @return 操作者结果
     */
    public OperatorResult createOperatorErrTrigger(long... operatorIds) {
        return new OperatorResult(operatorIds);
    }

    /**
     * 创建流程提醒
     *
     * @param title 提醒标题
     * @return 提醒对象
     */
    public MessageResult createMessageResult(String title, String resultState) {
        return MessageResult.create(title, resultState);
    }


    /**
     * 创建流程提醒
     *
     * @param title 提醒标题
     * @return 提醒对象
     */
    public MessageResult createMessageResult(String title) {
        return MessageResult.create(title);
    }

    /**
     * 创建流程提醒
     *
     * @param title     提醒标题
     * @param closeable 是否可关闭流程
     * @return 提醒对象
     */
    public MessageResult createMessageResult(String title, String resultState, boolean closeable) {
        return MessageResult.create(title, resultState, closeable);
    }


    /**
     * 创建流程提醒
     *
     * @param title     提醒标题
     * @param items     提醒内容
     * @param closeable 是否可关闭流程
     * @return 提醒对象
     */
    public MessageResult createMessageResult(String title, String resultState, List<MessageResult.Message> items, boolean closeable) {
        return MessageResult.create(title, resultState, items, closeable);
    }

    /**
     * 提交流程
     */
    public MessageResult submitFlow() {
        if (flowRecord == null) {
            throw new IllegalArgumentException("flow record is null");
        }
        FlowService flowService = loadFlowService();
        FlowResult result = flowService.submitFlow(flowRecord.getId(), currentOperator, bindData, Opinion.pass(opinion.getAdvice()));
        return MessageResult.create(result);
    }

    /**
     * 驳回流程
     */
    public MessageResult rejectFlow() {
        if (flowRecord == null) {
            throw new IllegalArgumentException("flow record is null");
        }
        FlowService flowService = loadFlowService();
        FlowResult result = flowService.submitFlow(flowRecord.getId(), currentOperator, bindData, Opinion.reject(opinion.getAdvice()));
        return MessageResult.create(result);
    }

    /**
     * 是否为驳回状态
     * @return 是否为驳回状态
     */
    public boolean isRejectState(){
        List<FlowRecord> historyRecords = this.historyRecords;
        if(historyRecords!=null) {
            for (FlowRecord record : historyRecords) {
                if (record.getId() == this.flowRecord.getPreId()) {
                    return record.getFlowSourceDirection() == FlowSourceDirection.REJECT;
                }
            }
        }
        return false;
    }


    /**
     * 预提交流程
     */
    public MessageResult trySubmitFlow() {
        if (flowRecord == null) {
            throw new IllegalArgumentException("flow record is null");
        }
        FlowService flowService = loadFlowService();
        FlowSubmitResult result = flowService.trySubmitFlow(flowRecord.getId(), currentOperator, bindData, Opinion.pass(opinion.getAdvice()));
        return MessageResult.create(result);
    }

    /**
     * 保存流程
     */
    public void saveFlow() {
        if (flowRecord == null) {
            throw new IllegalArgumentException("flow record is null");
        }
        FlowService flowService = loadFlowService();
        flowService.save(flowRecord.getId(), currentOperator, bindData, opinion.getAdvice());
    }


    /**
     * 催办流程
     */
    public void urgeFlow() {
        if (flowRecord == null) {
            throw new IllegalArgumentException("flow record is null");
        }
        FlowService flowService = loadFlowService();
        flowService.urge(flowRecord.getId(), currentOperator);
    }

    /**
     * 撤回流程
     */
    public void recallFlow() {
        if (flowRecord == null) {
            throw new IllegalArgumentException("flow record is null");
        }
        FlowService flowService = loadFlowService();
        flowService.recall(flowRecord.getId(), currentOperator);
    }


    private FlowService loadFlowService() {
        return (FlowService) getBean("flowService");
    }
}
