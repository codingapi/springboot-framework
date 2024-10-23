package com.codingapi.springboot.flow.build;

import com.codingapi.springboot.flow.domain.FlowNode;
import com.codingapi.springboot.flow.domain.FlowRelation;
import com.codingapi.springboot.flow.domain.FlowWork;
import com.codingapi.springboot.flow.em.ApprovalType;
import com.codingapi.springboot.flow.em.NodeType;
import com.codingapi.springboot.flow.generator.TitleGenerator;
import com.codingapi.springboot.flow.matcher.OperatorMatcher;
import com.codingapi.springboot.flow.trigger.OutTrigger;
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
        work.verify();
        return work;
    }


    public class Nodes {

        public Nodes start(String name, String view, TitleGenerator titleGenerator, OperatorMatcher operatorMatcher) {
            work.addNode(new FlowNode(IDGenerator.generate(), name, FlowNode.CODE_START, view, NodeType.START, ApprovalType.UN_SIGN, titleGenerator, operatorMatcher, 0, null));
            return this;
        }

        public Nodes node(String name, String code, String view, ApprovalType approvalType, OperatorMatcher operatorMatcher) {
            return node(name, code, view, approvalType, TitleGenerator.defaultTitleGenerator(), operatorMatcher);
        }

        public Nodes node(String name, String code, String view, ApprovalType approvalType, TitleGenerator titleGenerator, OperatorMatcher operatorMatcher) {
            work.addNode(new FlowNode(IDGenerator.generate(), name, code, view, NodeType.APPROVAL, approvalType, titleGenerator, operatorMatcher, 0, null));
            return this;
        }

        public Nodes over(String name, String view, TitleGenerator titleGenerator, OperatorMatcher operatorMatcher) {
            work.addNode(new FlowNode(IDGenerator.generate(), name, FlowNode.CODE_OVER, view, NodeType.OVER, ApprovalType.UN_SIGN, titleGenerator, operatorMatcher, 0, null));
            return this;
        }

        public Relations relations() {
            return new Relations();
        }

        public FlowWork build() {
            work.verify();
            return work;
        }
    }

    public class Relations {

        public Relations relation(String name, String source, String target) {
            FlowNode from = work.getNodeByCode(source);
            FlowNode to = work.getNodeByCode(target);
            OutTrigger outTrigger = OutTrigger.defaultOutTrigger();
            FlowRelation relation = new FlowRelation(IDGenerator.generate(), name, from, to, outTrigger, false);
            work.addRelation(relation);
            return this;
        }


        public Relations relation(String name, String source, String target, OutTrigger outTrigger, boolean back) {
            FlowNode from = work.getNodeByCode(source);
            FlowNode to = work.getNodeByCode(target);
            FlowRelation relation = new FlowRelation(IDGenerator.generate(), name, from, to, outTrigger,  back);
            work.addRelation(relation);
            return this;
        }

        public FlowWork build() {
            work.verify();
            return work;
        }


    }
}
