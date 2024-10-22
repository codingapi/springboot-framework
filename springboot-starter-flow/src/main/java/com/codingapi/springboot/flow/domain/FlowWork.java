package com.codingapi.springboot.flow.domain;

import com.codingapi.springboot.flow.bind.BindDataSnapshot;
import com.codingapi.springboot.flow.content.FlowContent;
import com.codingapi.springboot.flow.error.ErrorResult;
import com.codingapi.springboot.flow.error.NodeResult;
import com.codingapi.springboot.flow.error.OperatorResult;
import com.codingapi.springboot.flow.record.FlowRecord;
import com.codingapi.springboot.flow.user.IFlowOperator;
import com.codingapi.springboot.flow.utils.IDGenerator;
import lombok.Getter;
import lombok.Setter;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
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
        this.enable = true;
        this.lock = false;
        this.nodes = new ArrayList<>();
        this.relations = new ArrayList<>();
    }

    public void verify() {
        if (this.nodes == null || this.nodes.isEmpty()) {
            throw new IllegalArgumentException("nodes is empty");
        }
        if (this.relations == null || this.relations.isEmpty()) {
            throw new IllegalArgumentException("relations is empty");
        }
        if(!StringUtils.hasLength(title)){
            throw new IllegalArgumentException("title is empty");
        }
    }


    /**
     * schema解析流程设计
     *
     * @param schema schema
     */
    public void schema(String schema) {
        this.schema = schema;
        this.updateTime = System.currentTimeMillis();
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
    private FlowNode getStartNode() {
        return getNodeByCode(FlowNode.CODE_START);
    }

    /**
     * 生成流程id
     *
     * @return 流程id
     */
    public String generateProcessId() {
        return IDGenerator.generate();
    }


    /**
     * 启动流程
     *
     * @param operator 操作者
     * @param snapshot 绑定数据
     * @param opinion  意见
     * @return 流程记录
     */
    public List<FlowRecord> startFlow(IFlowOperator operator, BindDataSnapshot snapshot, Opinion opinion) {
        FlowNode start = this.getStartNode();
        if (start == null) {
            throw new IllegalArgumentException("start node not found");
        }

        long workId = this.getId();
        String processId = generateProcessId();
        long preId = 0;
        FlowContent content = new FlowContent(this, start, operator, operator, snapshot.toBindData(), opinion);
        String recordTitle = start.generateTitle(content);
        if (start.matcher(content)) {
            return Collections.singletonList(start.createRecord(workId, processId, preId, recordTitle, operator, operator, snapshot, opinion));
        }
        List<FlowRecord> errorRecordList = this.errMatcher(start, processId, preId, operator, operator, snapshot, opinion);
        if (errorRecordList.isEmpty()) {
            throw new IllegalArgumentException("operator not match.");
        }
        return errorRecordList;
    }


    /**
     * 异常匹配
     *
     * @param currentNode     当前节点
     * @param processId       流程id
     * @param preId           上一条流程记录id
     * @param createOperator  创建操作者
     * @param currentOperator 当前操作者
     * @param snapshot        绑定数据
     * @param opinion         意见
     * @return 流程记录
     */
    private List<FlowRecord> errMatcher(FlowNode currentNode, String processId, long preId, IFlowOperator createOperator, IFlowOperator currentOperator, BindDataSnapshot snapshot, Opinion opinion) {
        if (currentNode.hasErrTrigger()) {
            FlowContent flowContent = new FlowContent(this, currentNode, createOperator, currentOperator, snapshot.toBindData(), opinion);
            ErrorResult errorResult = currentNode.errMatcher(flowContent);
            if (errorResult == null) {
                throw new IllegalArgumentException("errMatcher match error.");
            }

            // 匹配操作者
            if (errorResult.isOperator()) {
                List<FlowRecord> records = new ArrayList<>();
                List<IFlowOperator> operators = ((OperatorResult) errorResult).getOperators();
                for (IFlowOperator operator : operators) {
                    FlowContent content = new FlowContent(this, currentNode, createOperator, operator, snapshot.toBindData(), opinion);
                    if (currentNode.matcher(content)) {
                        String recordTitle = currentNode.generateTitle(content);
                        FlowRecord record = currentNode.createRecord(this.getId(), processId, preId, recordTitle, createOperator, operator, snapshot, opinion);
                        records.add(record);
                    }
                }
                return records;
            }
            // 匹配节点
            if (errorResult.isNode()) {
                String nodeCode = ((NodeResult) errorResult).getNode();
                FlowNode node = getNodeByCode(nodeCode);
                FlowContent content = new FlowContent(this, node, createOperator, currentOperator, snapshot.toBindData(), opinion);
                if (node.matcher(content)) {
                    String recordTitle = node.generateTitle(content);
                    return Collections.singletonList(node.createRecord(this.getId(), processId, preId, recordTitle, createOperator, currentOperator, snapshot, opinion));
                }
            }
            throw new IllegalArgumentException("errMatcher not match.");
        }
        throw new IllegalArgumentException("operator not match.");
    }


}
