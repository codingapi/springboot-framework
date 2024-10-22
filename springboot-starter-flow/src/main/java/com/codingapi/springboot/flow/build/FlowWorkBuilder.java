package com.codingapi.springboot.flow.build;

import com.codingapi.springboot.flow.domain.FlowNode;
import com.codingapi.springboot.flow.domain.FlowRelation;
import com.codingapi.springboot.flow.domain.FlowWork;
import com.codingapi.springboot.flow.em.ApprovalType;
import com.codingapi.springboot.flow.em.NodeType;
import com.codingapi.springboot.flow.generator.ITitleGenerator;
import com.codingapi.springboot.flow.matcher.IOperatorMatcher;
import com.codingapi.springboot.flow.trigger.IOutTrigger;
import com.codingapi.springboot.flow.user.IFlowOperator;
import com.codingapi.springboot.flow.utils.IDGenerator;

public class FlowWorkBuilder {

    private FlowWork work = null;

    private FlowWorkBuilder(FlowWork flowWork) {
        this.work = flowWork;
    }


    public static FlowWorkBuilder builder(IFlowOperator flowOperator) {
        return new FlowWorkBuilder(new FlowWork(flowOperator));
    }

    public FlowWorkBuilder description(String description) {
        this.work.setDescription(description);
        return this;
    }


    public FlowWorkBuilder title(String title) {
        this.work.setTitle(title);
        return this;
    }


    public Nodes nodes() {
        return new Nodes();
    }

    public Relations relations() {
        return new Relations();
    }

    public FlowWork build() {
        return work;
    }


    public class Nodes {

        public Nodes start(String name, String view, ITitleGenerator titleGenerator, IOperatorMatcher operatorMatcher) {
            work.addNode(new FlowNode(IDGenerator.generate(), name, FlowNode.CODE_START, view, NodeType.START, ApprovalType.NOT_SIGN, titleGenerator, operatorMatcher));
            return this;
        }

        public Nodes node(String name, String code, String view, ApprovalType approvalType, ITitleGenerator titleGenerator, IOperatorMatcher operatorMatcher) {
            work.addNode(new FlowNode(IDGenerator.generate(), name, code, view, NodeType.APPROVAL, approvalType, titleGenerator, operatorMatcher));
            return this;
        }

        public Nodes over(String name, String view, ITitleGenerator titleGenerator, IOperatorMatcher operatorMatcher) {
            work.addNode(new FlowNode(IDGenerator.generate(), name, FlowNode.CODE_OVER, view, NodeType.OVER, ApprovalType.NOT_SIGN, titleGenerator, operatorMatcher));
            return this;
        }

        public Relations relations() {
            return new Relations();
        }

        public FlowWork build() {
            return work;
        }
    }

    public class Relations {

        public Relations relation(String name, String source, String target, IOutTrigger outTrigger, boolean defaultOut) {
            FlowNode from = work.getNodeByCode(source);
            FlowNode to = work.getNodeByCode(target);
            FlowRelation relation = new FlowRelation(IDGenerator.generate(), name, from, to, outTrigger, defaultOut);
            work.addRelation(relation);
            return this;
        }

        public FlowWork build() {
            return work;
        }


    }
}
