package com.codingapi.springboot.flow.domain;

import com.codingapi.springboot.flow.user.IFlowOperator;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * 流程设计
 */
@Getter
public class FlowWork {

    /**
     * 流程的设计id
     */
    @Setter
    private long id;
    /**
     * 流程标题
     */
    @Setter
    private String title;
    /**
     * 流程描述
     */
    @Setter
    private String description;
    /**
     * 流程创建者
     */
    private IFlowOperator<?> createUser;
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
     * 流程的关系
     */
    private List<FlowRelation> relations;

    /**
     * 界面设计脚本
     */
    private String schema;


    public FlowWork(IFlowOperator<?> createUser) {
        this.createUser = createUser;
        this.createTime = System.currentTimeMillis();
        this.updateTime = System.currentTimeMillis();
        this.enable = true;
        this.lock = false;
        this.nodes = new ArrayList<>();
        this.relations = new ArrayList<>();
    }


    /**
     * schema解析流程设计
     *
     * @param schema schema
     */
    public void schema(String schema) {
        this.schema = schema;
        //todo 解析schema
    }

    /**
     * 添加节点
     *
     * @param node 节点
     */
    public void addNode(FlowNode node) {
        List<String> codes = nodes.stream().map(FlowNode::getCode).toList();
        if (codes.contains(node.getCode())) {
            throw new RuntimeException("node code is exist");
        }
        nodes.add(node);
    }

    public void addRelation(FlowRelation relation) {
        relations.add(relation);
    }


    public FlowNode getNodeByCode(String code) {
        for (FlowNode node : nodes) {
            if (node.getCode().equals(code)) {
                return node;
            }
        }
        return null;
    }

}
