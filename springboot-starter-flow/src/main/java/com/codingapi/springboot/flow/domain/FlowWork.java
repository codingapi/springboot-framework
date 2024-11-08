package com.codingapi.springboot.flow.domain;

import com.codingapi.springboot.flow.build.SchemaReader;
import com.codingapi.springboot.flow.serializable.FlowWorkSerializable;
import com.codingapi.springboot.flow.user.IFlowOperator;
import com.codingapi.springboot.framework.utils.RandomGenerator;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 流程设计
 */
@Getter
@AllArgsConstructor
public class FlowWork {

    /**
     * 流程的设计id
     */
    @Setter
    private long id;

    /**
     * 流程编码 （唯一值）
     */
    @Setter
    private String code;

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
    private IFlowOperator createUser;
    /**
     * 创建时间
     */
    private long createTime;
    /**
     * 更新时间
     * 也是流程的版本号
     */
    private long updateTime;
    /**
     * 是否启用
     */
    private boolean enable;

    /**
     * 最大延期次数
     */
    @Setter
    private int postponedMax;

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

    /**
     * 构造函数
     *
     * @param createUser 创建者
     */
    public FlowWork(IFlowOperator createUser) {
        this.createUser = createUser;
        this.createTime = System.currentTimeMillis();
        this.updateTime = System.currentTimeMillis();
        this.nodes = new ArrayList<>();
        this.relations = new ArrayList<>();
        this.enable = false;
        this.postponedMax = 1;
        this.code = RandomGenerator.randomString(8);
    }


    /**
     * 流程设计复制
     * @return FlowWork 流程设计
     */
    public FlowWork copy(){
        if(!StringUtils.hasLength(schema)){
            throw new IllegalArgumentException("schema is empty");
        }
        String schema = this.getSchema();
        for(FlowNode flowNode:this.getNodes()){
            String newId = RandomGenerator.generateUUID();
            schema = schema.replaceAll(flowNode.getId(),newId);
        }

        for(FlowRelation relation:this.getRelations()){
            String newId = RandomGenerator.generateUUID();
            schema = schema.replaceAll(relation.getId(),newId);
        }

        FlowWork flowWork = new FlowWork(this.createUser);
        flowWork.setDescription(this.getDescription());
        flowWork.setTitle(this.getTitle());
        flowWork.setCode(RandomGenerator.randomString(8));
        flowWork.setPostponedMax(this.getPostponedMax());
        flowWork.schema(schema);
        return flowWork;
    }


    public FlowWork(String code,String title, String description, int postponedMax, IFlowOperator createUser) {
        this.title = title;
        this.code = code;
        this.description = description;
        this.postponedMax = postponedMax;
        this.createUser = createUser;
        this.createTime = System.currentTimeMillis();
        this.updateTime = System.currentTimeMillis();
        this.nodes = new ArrayList<>();
        this.relations = new ArrayList<>();
        this.enable = false;
    }


    public void verify() {
        if (this.nodes == null || this.nodes.isEmpty()) {
            throw new IllegalArgumentException("nodes is empty");
        }
        if (this.relations == null || this.relations.isEmpty()) {
            throw new IllegalArgumentException("relations is empty");
        }
        if (!StringUtils.hasLength(title)) {
            throw new IllegalArgumentException("title is empty");
        }
        if (!StringUtils.hasLength(code)) {
            throw new IllegalArgumentException("code is empty");
        }

        this.verifyNodes();
        this.verifyRelations();
        this.checkRelation();
    }


    private void checkRelation() {
        FlowNode startNode = getNodeByCode(FlowNode.CODE_START);
        if (startNode == null) {
            throw new IllegalArgumentException("start node is not exist");
        }
        FlowNode overNode = getNodeByCode(FlowNode.CODE_OVER);
        if (overNode == null) {
            throw new IllegalArgumentException("over node is not exist");
        }

        List<String> sourceCodes = new ArrayList<>();
        List<String> targetCodes = new ArrayList<>();
        for (FlowRelation relation : relations) {
            sourceCodes.add(relation.getSource().getCode());
            targetCodes.add(relation.getTarget().getCode());
        }

        if (!sourceCodes.contains(FlowNode.CODE_START)) {
            throw new IllegalArgumentException("start node relation is not exist");
        }

        if (!targetCodes.contains(FlowNode.CODE_OVER)) {
            throw new IllegalArgumentException("over node relation is not exist");
        }

    }


    private void verifyNodes() {
        List<String> nodeCodes = new ArrayList<>();

        for (FlowNode node : nodes) {
            node.verify();
            if (nodeCodes.contains(node.getCode())) {
                throw new IllegalArgumentException("node code is exist");
            }
            nodeCodes.add(node.getCode());
        }
    }


    private void verifyRelations() {
        for (FlowRelation relation : relations) {
            relation.verify();

            relation.verifyNodes(nodes);
        }
    }


    /**
     * 序列化
     *
     * @return FlowSerializable 流程序列化对象
     */
    public FlowWorkSerializable toSerializable() {
        return new FlowWorkSerializable(
                id,
                code,
                title,
                description,
                createUser.getUserId(),
                createTime,
                updateTime,
                enable,
                postponedMax,
                nodes.stream().map(FlowNode::toSerializable).collect(Collectors.toCollection(ArrayList::new)),
                relations.stream().map(FlowRelation::toSerializable).collect(Collectors.toCollection(ArrayList::new)));
    }


    /**
     * schema解析流程设计
     *
     * @param schema schema
     */
    public void schema(String schema) {
        SchemaReader schemaReader = new SchemaReader(schema);
        this.relations = schemaReader.getFlowRelations();
        this.nodes = schemaReader.getFlowNodes();
        this.schema = schema;
        this.verify();
        this.updateTime = System.currentTimeMillis();
    }

    /**
     * 添加节点
     *
     * @param node 节点
     */
    public void addNode(FlowNode node) {
        List<String> codes = nodes.stream().map(FlowNode::getCode).toList();
        if (codes.contains(node.getCode())) {
            throw new IllegalArgumentException("node code is exist");
        }
        nodes.add(node);
        this.updateTime = System.currentTimeMillis();
    }

    /**
     * 添加关系
     *
     * @param relation 关系
     */
    public void addRelation(FlowRelation relation) {
        relations.add(relation);
        this.updateTime = System.currentTimeMillis();
    }


    /**
     * 获取节点
     *
     * @param code 节点编码
     * @return 节点
     */
    public FlowNode getNodeByCode(String code) {
        for (FlowNode node : nodes) {
            if (node.getCode().equals(code)) {
                return node;
            }
        }
        return null;
    }


    /**
     * 获取开始节点
     *
     * @return 开始节点
     */
    public FlowNode getStartNode() {
        return getNodeByCode(FlowNode.CODE_START);
    }


    /**
     * 是否存在退回关系
     */
    public boolean hasBackRelation() {
        return relations.stream().anyMatch(FlowRelation::isBack);
    }


    /**
     * 启用检测
     */
    public void enableValidate() {
        if (!this.isEnable()) {
            throw new IllegalArgumentException("flow work is disable");
        }
    }


    /**
     * 启用
     */
    public void enable() {
        this.verify();
        this.enable = true;
    }

    /**
     * 禁用
     */
    public void disbale() {
        this.enable = false;
    }
}
