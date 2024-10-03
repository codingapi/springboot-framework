package com.codingapi.springboot.flow.domain;

import com.codingapi.springboot.flow.context.FlowRepositoryContext;
import com.codingapi.springboot.flow.data.IBindData;
import com.codingapi.springboot.flow.operator.IFlowOperator;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * 流程的设计，约定流程的节点配置与配置
 */
@Slf4j
@Setter
@Getter
public class FlowWork {

    /**
     * 流程的设计id
     */
    private long id;
    /**
     * 流程标题
     */
    private String title;
    /**
     * 流程描述
     */
    private String description;
    /**
     * 流程创建者
     */
    private IFlowOperator createUser;
    /**
     * 创建时间
     */
    private long createTime;
    /**
     * 更新时间
     */
    private long updateTime;
    /**
     * 是否启用
     */
    private boolean enable;
    /**
     * 是否锁定
     * 锁定流程将无法发起新的流程，当前存在的流程不受影响
     */
    private boolean lock;
    /**
     * 流程的节点(发起节点)
     */
    private List<FlowNode> nodes;
    /**
     * 界面设计脚本
     */
    private String schema;

    public FlowNode startNode() {
        return getFlowNode(FlowNode.CODE_START);
    }

    /**
     * 获取节点
     *
     * @param code 节点编码
     * @return 节点
     */
    public FlowNode getFlowNode(String code) {
        return nodes.stream().filter(node -> node.getCode().equals(code)).findFirst().orElse(null);
    }

    /**
     * 创建流程节点
     *
     * @param bindData     绑定数据
     * @param operatorUser 操作者
     */
    public void createNode(IBindData bindData, IFlowOperator operatorUser) {
        FlowNode startNode = startNode();
        startNode.verifyOperator(operatorUser);
        long processId = System.currentTimeMillis();
        FlowRecord record = startNode.createRecord(processId, 0, bindData, operatorUser, operatorUser);
        record.submit(null, bindData);
    }

    /**
     * 删除流程
     */
    public void delete() {
        this.getNodes().forEach(FlowRepositoryContext.getInstance()::delete);
        FlowRepositoryContext.getInstance().delete(this);
    }
}
