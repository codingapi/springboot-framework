package com.codingapi.springboot.flow.builder;

import com.codingapi.springboot.flow.context.FlowRepositoryContext;
import com.codingapi.springboot.flow.domain.FlowNode;
import com.codingapi.springboot.flow.domain.FlowRelation;
import com.codingapi.springboot.flow.domain.FlowWork;
import com.codingapi.springboot.flow.operator.IFlowOperator;
import com.codingapi.springboot.flow.trigger.IOutTrigger;

import java.util.ArrayList;
import java.util.List;

public class FlowWorkBuilder {

    private final FlowWork flowWork = new FlowWork();

    private FlowWorkBuilder(IFlowOperator createOperator) {
        flowWork.setEnable(true);
        flowWork.setLock(false);
        flowWork.setCreateUser(createOperator);
        flowWork.setCreateTime(System.currentTimeMillis());
    }

    public static FlowWorkBuilder Builder(IFlowOperator createOperator) {
        return new FlowWorkBuilder(createOperator);
    }

    public FlowWorkBuilder title(String title) {
        flowWork.setTitle(title);
        return this;
    }

    public FlowWorkBuilder description(String description) {
        flowWork.setDescription(description);
        return this;
    }

    public FlowNodeBuilder nodes() {
        return new FlowNodeBuilder();
    }

    public Relations nodes(List<FlowNode> nodes) {
        nodes.forEach(flowNode -> flowNode.setFlowWork(flowWork));
        return new Relations(nodes);
    }

    public FlowWorkBuilder schema(String schema) {
        flowWork.setSchema(schema);
        return this;
    }

    public FlowWorkBuilder enable(boolean enable) {
        flowWork.setEnable(enable);
        return this;
    }

    public FlowWorkBuilder lock(boolean lock) {
        flowWork.setLock(lock);
        return this;
    }


    public class Relations {
        private final List<FlowNode> list;
        private final List<FlowRelation> relations;

        private Relations(List<FlowNode> list) {
            this.list = list;
            this.relations = new ArrayList<>();
        }

        public Relations relation(String id, String source, String target, IOutTrigger outTrigger, boolean defaultOut) {
            FlowNode sourceNode = getFlowNodeByCode(source);
            if (sourceNode == null) {
                throw new RuntimeException(source + " not found node");
            }
            FlowNode targetNode = getFlowNodeByCode(target);
            if (targetNode == null) {
                throw new RuntimeException(target + " not found node");
            }
            FlowRelation flowRelation = new FlowRelation(id, sourceNode, targetNode, outTrigger, flowWork.getCreateUser(), defaultOut);
            relations.add(flowRelation);
            return this;
        }

        public FlowWork build() {
            FlowNode flowNode = getFlowNodeByCode(FlowNode.CODE_START);
            if (flowNode == null) {
                throw new RuntimeException("start node not found");
            }
            FlowRepositoryContext.getInstance().save(flowWork);
            list.forEach(FlowRepositoryContext.getInstance()::save);
            flowWork.setNodes(list);
            flowWork.setRelations(relations);
            FlowRepositoryContext.getInstance().save(flowWork);
            return flowWork;
        }

        private FlowNode getFlowNodeByCode(String code) {
            for (FlowNode flowNode : list) {
                if (flowNode.getCode().equals(code)) {
                    return flowNode;
                }
            }
            return null;
        }
    }


    public class FlowNodeBuilder {

        private final List<FlowNode> list = new ArrayList<>();

        private FlowNodeBuilder() {
        }

        public FlowNodeBuilder node(FlowNode flowNode) {
            flowNode.setFlowWork(flowWork);
            list.add(flowNode);
            return this;
        }

        public Relations relations() {
            return new Relations(list);
        }


    }


}
