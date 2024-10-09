package com.codingapi.springboot.flow.builder;

import com.codingapi.springboot.flow.context.FlowRepositoryContext;
import com.codingapi.springboot.flow.domain.FlowNode;
import com.codingapi.springboot.flow.domain.FlowWork;
import com.codingapi.springboot.flow.operator.IFlowOperator;

import java.util.ArrayList;
import java.util.Arrays;
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


    public class Relations{
        private final List<FlowNode> list;

        private Relations(List<FlowNode> list) {
            this.list = list;
        }

        public Relations relation(String... codes) {
            relationNodes(codes);
            return this;
        }

        public FlowWork build() {
            FlowNode flowNode = getFlowNodeByCode(FlowNode.CODE_START);
            if(flowNode==null){
                throw new RuntimeException("start node not found");
            }
            FlowRepositoryContext.getInstance().save(flowWork);
            list.forEach(FlowRepositoryContext.getInstance()::save);
            flowWork.setNodes(list);
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

        private void relationNodes(String[] codes) {
            int length = codes.length;
            if (length >= 2) {
                String first = codes[0];
                FlowNode firstNode = getFlowNodeByCode(first);
                if (firstNode == null) {
                    throw new RuntimeException(first+" not found node");
                }
                String next = codes[1];
                FlowNode nexNode = getFlowNodeByCode(next);
                if (nexNode == null) {
                    throw new RuntimeException(next+" not found node");
                }
                nexNode.setParentCode(first);
                firstNode.addNextNode(nexNode);
                relationNodes(Arrays.copyOfRange(codes, 1, length));
            }
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
