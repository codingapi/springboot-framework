package com.codingapi.springboot.flow.builder;

import com.codingapi.springboot.flow.creator.DefaultTitleCreator;
import com.codingapi.springboot.flow.domain.FlowNode;
import com.codingapi.springboot.flow.em.FlowType;
import com.codingapi.springboot.flow.em.NodeType;
import com.codingapi.springboot.flow.matcher.IOperatorMatcher;
import com.codingapi.springboot.flow.operator.IFlowOperator;
import com.codingapi.springboot.flow.trigger.IErrTrigger;
import com.codingapi.springboot.flow.utils.IDGenerator;
import lombok.AllArgsConstructor;

public class FlowNodeFactory {

    @AllArgsConstructor
    public static class Builder {
        private IFlowOperator createUser;

        public FlowNode startNode(String id,String name, IOperatorMatcher operatorMatcher) {
            return startNode(id, name, FlowNode.VIEW_DEFAULT, operatorMatcher);
        }

        public FlowNode startNode(String name, IOperatorMatcher operatorMatcher) {
            return startNode(IDGenerator.generator(), name, FlowNode.VIEW_DEFAULT, operatorMatcher);
        }


        public FlowNode startNode(String id,String name, String view, IOperatorMatcher operatorMatcher) {
            FlowNode flowNode = new FlowNode();
            flowNode.setId(id);
            flowNode.setName(name);
            flowNode.setType(NodeType.START);
            flowNode.setCode(FlowNode.CODE_START);
            flowNode.setView(view);
            flowNode.setTitleCreator(new DefaultTitleCreator());
            flowNode.setFlowType(FlowType.NOT_SIGN);
            flowNode.setCreateTime(System.currentTimeMillis());
            flowNode.setUpdateTime(System.currentTimeMillis());
            flowNode.setCreateUser(createUser);
            flowNode.setOperatorMatcher(operatorMatcher);
            return flowNode;
        }

        public FlowNode overNode(String name) {
            return overNode(IDGenerator.generator(), name);
        }

        public FlowNode overNode(String id,String name) {
            FlowNode flowNode = new FlowNode();
            flowNode.setId(id);
            flowNode.setName(name);
            flowNode.setType(NodeType.OVER);
            flowNode.setCode(FlowNode.CODE_OVER);
            flowNode.setView(FlowNode.VIEW_DEFAULT);
            flowNode.setTitleCreator(new DefaultTitleCreator());
            flowNode.setFlowType(FlowType.NOT_SIGN);
            flowNode.setCreateTime(System.currentTimeMillis());
            flowNode.setUpdateTime(System.currentTimeMillis());
            flowNode.setCreateUser(createUser);
            return flowNode;
        }

        public FlowNode node(String name,
                             String code,
                             FlowType flowType,
                             IOperatorMatcher operatorMatcher) {
            return node(IDGenerator.generator(),name, code, FlowNode.VIEW_DEFAULT, flowType,  operatorMatcher);
        }

        public FlowNode node(String name,
                             String code,
                             String view,
                             FlowType flowType,
                             IOperatorMatcher operatorMatcher) {
            return node(IDGenerator.generator(),name, code, view, flowType, operatorMatcher, null);
        }

        public FlowNode node(String id,
                             String name,
                             String code,
                             String view,
                             FlowType flowType,
                             IOperatorMatcher operatorMatcher) {
            return node(id,name, code, view, flowType, operatorMatcher, null);
        }


        public FlowNode node(String id,
                             String name,
                             String code,
                             String view,
                             FlowType flowType,
                             IOperatorMatcher operatorMatcher,
                             IErrTrigger errTrigger) {
            FlowNode flowNode = new FlowNode();
            flowNode.setId(id);
            flowNode.setName(name);
            flowNode.setType(NodeType.APPROVAL);
            flowNode.setCode(code);
            flowNode.setView(view);
            flowNode.setTitleCreator(new DefaultTitleCreator());
            flowNode.setFlowType(flowType);
            flowNode.setCreateTime(System.currentTimeMillis());
            flowNode.setUpdateTime(System.currentTimeMillis());
            flowNode.setCreateUser(createUser);

            flowNode.setOperatorMatcher(operatorMatcher);
            flowNode.setErrTrigger(errTrigger);
            return flowNode;
        }

    }

    public static Builder Builder(IFlowOperator createUser) {
        return new Builder(createUser);
    }


}
