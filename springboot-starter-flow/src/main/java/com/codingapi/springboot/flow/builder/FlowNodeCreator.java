package com.codingapi.springboot.flow.builder;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.codingapi.springboot.flow.domain.FlowNode;
import com.codingapi.springboot.flow.em.FlowType;
import com.codingapi.springboot.flow.matcher.ScriptOperatorMatcher;
import com.codingapi.springboot.flow.operator.IFlowOperator;
import com.codingapi.springboot.flow.trigger.ScriptErrTrigger;
import com.codingapi.springboot.flow.trigger.ScriptOutTrigger;
import io.micrometer.common.util.StringUtils;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public class FlowNodeCreator {

    private final IFlowOperator operator;

    private FlowNodeCreator(IFlowOperator operator) {
        this.operator = operator;
    }

    public static FlowNodeCreator Builder(IFlowOperator createOperator) {
        return new FlowNodeCreator(createOperator);
    }

    public List<FlowNode> create(JSONObject jsonObject) {
        NodeBuilder nodeBuilder = new NodeBuilder(jsonObject);
        return nodeBuilder.getNodes();
    }


    private final class NodeBuilder {
        private final JSONObject jsonObject;

        @Getter
        private final List<FlowNode> nodes;

        public NodeBuilder(JSONObject jsonObject) {
            this.jsonObject = jsonObject;
            this.nodes = new ArrayList<>();

            this.reloadNodes();
            this.reloadRelations();
        }

        public FlowNode getNodeById(String id) {
            for (FlowNode node : nodes) {
                if (node.getId().equals(id)) {
                    return node;
                }
            }
            return null;
        }


        private void reloadRelations() {
            JSONArray edges = jsonObject.getJSONArray("edges");
            for (int i = 0; i < edges.size(); i++) {
                JSONObject edge = edges.getJSONObject(i);
                String source = edge.getString("sourceNodeId");
                String target = edge.getString("targetNodeId");
                FlowNode sourceNode = getNodeById(source);
                FlowNode targetNode = getNodeById(target);
                if (sourceNode != null && targetNode != null) {
                    sourceNode.addNextNode(targetNode);
                    targetNode.setParentCode(sourceNode.getCode());
                }
            }
        }


        private void reloadNodes() {
            JSONArray nodes = jsonObject.getJSONArray("nodes");
            for (int i = 0; i < nodes.size(); i++) {
                JSONObject node = nodes.getJSONObject(i);
                JSONObject properties = node.getJSONObject("properties");
                String id = node.getString("id");
                String name = properties.getString("name");
                String code = properties.getString("code");
                String view = properties.getString("view");
                String type = properties.getString("type");
                String errTrigger = properties.getString("errTrigger");

                String outOperatorMatcher = properties.getString("outOperatorMatcher");

                FlowNode flowNode = FlowNodeFactory.Builder(operator)
                        .node(id, name, code, view, FlowType.parser(type),
                                StringUtils.isBlank(outOperatorMatcher) ? null : new ScriptOperatorMatcher(outOperatorMatcher),
                                StringUtils.isBlank(errTrigger) ? null : new ScriptErrTrigger(errTrigger)
                        );
                this.nodes.add(flowNode);
            }

        }
    }


}
