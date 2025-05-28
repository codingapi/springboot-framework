package com.codingapi.springboot.flow.build;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.codingapi.springboot.flow.domain.FlowButton;
import com.codingapi.springboot.flow.domain.FlowNode;
import com.codingapi.springboot.flow.domain.FlowRelation;
import com.codingapi.springboot.flow.em.ApprovalType;
import com.codingapi.springboot.flow.em.NodeType;
import com.codingapi.springboot.flow.error.ErrTrigger;
import com.codingapi.springboot.flow.generator.TitleGenerator;
import com.codingapi.springboot.flow.matcher.OperatorMatcher;
import com.codingapi.springboot.flow.trigger.OutTrigger;
import lombok.Getter;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 流程设计schema读取器
 */
public class SchemaReader {

    private final JSONObject data;

    @Getter
    private final List<FlowNode> flowNodes;
    @Getter
    private final List<FlowRelation> flowRelations;

    public SchemaReader(String schema) {
        this.data = JSONObject.parseObject(schema);
        this.flowNodes = new ArrayList<>();
        this.flowRelations = new ArrayList<>();
        this.loadNodes();
        this.loadEdges();
    }


    private void loadNodes() {
        JSONArray nodes = data.getJSONArray("nodes");
        for (int i = 0; i < nodes.size(); i++) {
            JSONObject node = nodes.getJSONObject(i);
            JSONObject properties = node.getJSONObject("properties");
            String code = properties.getString("code");
            String operatorMatcher = properties.getString("operatorMatcher");
            String titleGenerator = properties.getString("titleGenerator");
            String name = properties.getString("name");
            boolean editable = properties.getBoolean("editable");
            boolean mergeable = properties.getBoolean("mergeable");
            String view = properties.getString("view");
            String type = properties.getString("type");
            String approvalType = properties.getString("approvalType");
            int timeout = properties.getIntValue("timeout");
            String errTrigger = properties.getString("errTrigger");
            String id = properties.getString("id");
            List<FlowButton> buttons = null;
            if(properties.containsKey("buttons")){
                buttons = properties.getJSONArray("buttons").toJavaList(FlowButton.class);
            }
            FlowNode flowNode = new FlowNode(id, name, code, view, NodeType.parser(type), ApprovalType.parser(approvalType), new TitleGenerator(titleGenerator),
                    new OperatorMatcher(operatorMatcher), timeout, StringUtils.hasLength(errTrigger) ? new ErrTrigger(errTrigger) : null, editable,mergeable, buttons);
            flowNodes.add(flowNode);
        }
    }

    private FlowNode getFlowNodeById(String id) {
        for (FlowNode flowNode : flowNodes) {
            if (flowNode.getId().equals(id)) {
                return flowNode;
            }
        }
        return null;
    }

    private void loadEdges() {
        JSONArray edges = data.getJSONArray("edges");
        for (int i = 0; i < edges.size(); i++) {
            JSONObject edge = edges.getJSONObject(i);
            String id = edge.getString("id");
            String sourceNodeId = edge.getString("sourceNodeId");
            String targetNodeId = edge.getString("targetNodeId");

            JSONObject properties = edge.getJSONObject("properties");
            String name = properties.containsKey("name") ? properties.getString("name") : null;
            String outTrigger = properties.containsKey("outTrigger") ? properties.getString("outTrigger") : OutTrigger.defaultOutTrigger().getScript();
            boolean back = properties.containsKey("back") ? properties.getBoolean("back") : false;
            int order = properties.containsKey("order") ? properties.getIntValue("order") : 1;

            FlowNode source = getFlowNodeById(sourceNodeId);
            FlowNode target = getFlowNodeById(targetNodeId);

            FlowRelation relation = new FlowRelation(id, name, source, target, new OutTrigger(outTrigger), order, back);
            flowRelations.add(relation);
        }
    }
}
