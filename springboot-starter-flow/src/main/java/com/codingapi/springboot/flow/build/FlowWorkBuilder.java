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


        public Nodes node(String name, String code, String view, ApprovalType approvalType, OperatorMatcher operatorMatcher, boolean editable) {
            FlowNode node = new FlowNode(IDGenerator.generate(), name, code, view, NodeType.parser(code), approvalType, TitleGenerator.defaultTitleGenerator(), operatorMatcher, 0, null, editable);
            work.addNode(node);
            return this;
        }

        public Nodes node(String name, String code, String view, ApprovalType approvalType, OperatorMatcher operatorMatcher) {
            return node(name, code, view, approvalType, operatorMatcher, true);
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
            FlowRelation relation = new FlowRelation(IDGenerator.generate(), name, from, to, outTrigger, back);
            work.addRelation(relation);
            return this;
        }

        public FlowWork build() {
            work.verify();
            return work;
        }


    }
}
