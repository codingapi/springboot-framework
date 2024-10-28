package com.codingapi.springboot.flow.content;

import com.codingapi.springboot.flow.bind.IBindData;
import com.codingapi.springboot.flow.domain.FlowNode;
import com.codingapi.springboot.flow.domain.FlowWork;
import com.codingapi.springboot.flow.domain.Opinion;
import com.codingapi.springboot.flow.error.NodeResult;
import com.codingapi.springboot.flow.error.OperatorResult;
import com.codingapi.springboot.flow.record.FlowRecord;
import com.codingapi.springboot.flow.user.IFlowOperator;
import lombok.Getter;

import java.util.List;

/**
 * 流程groovy脚本回话对象
 */
@Getter
public class FlowSession {

    private final FlowWork flowWork;
    private final FlowNode flowNode;
    private final IFlowOperator createOperator;
    private final IFlowOperator currentOperator;
    private final IBindData bindData;
    private final Opinion opinion;
    private final List<FlowRecord> historyRecords;
    private final FlowSessionBeanProvider provider;

    public FlowSession(FlowWork flowWork, FlowNode flowNode, IFlowOperator createOperator, IFlowOperator currentOperator, IBindData bindData, Opinion opinion, List<FlowRecord> historyRecords) {
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


}
