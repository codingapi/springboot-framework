package com.codingapi.springboot.flow.build;

import com.codingapi.springboot.flow.domain.FlowButton;
import com.codingapi.springboot.flow.domain.FlowNode;
import com.codingapi.springboot.flow.domain.FlowRelation;
import com.codingapi.springboot.flow.domain.FlowWork;
import com.codingapi.springboot.flow.em.ApprovalType;
import com.codingapi.springboot.flow.em.NodeType;
import com.codingapi.springboot.flow.error.ErrTrigger;
import com.codingapi.springboot.flow.generator.TitleGenerator;
import com.codingapi.springboot.flow.matcher.OperatorMatcher;
import com.codingapi.springboot.flow.trigger.OutTrigger;
import com.codingapi.springboot.flow.user.IFlowOperator;
import com.codingapi.springboot.framework.utils.RandomGenerator;

import java.util.List;

/**
 * 流程工作构建器
 */
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

    public FlowWorkBuilder postponedMax(int postponedMax) {
        this.work.setPostponedMax(postponedMax);
        return this;
    }

    public FlowWorkBuilder skipIfSameApprover(boolean skipIfSameApprover) {
        this.work.setSkipIfSameApprover(skipIfSameApprover);
        return this;
    }

    public FlowWorkBuilder title(String title) {
        this.work.setTitle(title);
        return this;
    }

    public FlowWorkBuilder schema(String schema) {
        this.work.schema(schema);
        return this;
    }


    public Nodes nodes() {
        return new Nodes();
    }

    public Relations relations() {
        return new Relations();
    }

    public FlowWork build() {
        work.enable();
        return work;
    }


    public class Nodes {

        public Nodes node(String id, String name, String code, String view, ApprovalType approvalType, OperatorMatcher operatorMatcher, long timeout, TitleGenerator titleGenerator, ErrTrigger errTrigger, boolean editable, boolean mergeable, List<FlowButton> buttons) {
            FlowNode node = new FlowNode(id, name, code, view, NodeType.parser(code), approvalType, titleGenerator, operatorMatcher, timeout, errTrigger, editable,mergeable, buttons);
            work.addNode(node);
            return this;
        }

        public Nodes node(String name, String code, String view, ApprovalType approvalType, OperatorMatcher operatorMatcher, long timeout, boolean editable) {
            return node(RandomGenerator.generateUUID(), name, code, view, approvalType, operatorMatcher, timeout, TitleGenerator.defaultTitleGenerator(), null, editable,false, null);
        }

        public Nodes node(String name, String code, String view, ApprovalType approvalType, OperatorMatcher operatorMatcher, long timeout, boolean editable,boolean mergeable) {
            return node(RandomGenerator.generateUUID(), name, code, view, approvalType, operatorMatcher, timeout, TitleGenerator.defaultTitleGenerator(), null, editable,mergeable, null);
        }

        public Nodes node(String name, String code, String view, ApprovalType approvalType, OperatorMatcher operatorMatcher, long timeout, boolean editable,boolean mergeable, List<FlowButton> buttons) {
            return node(RandomGenerator.generateUUID(), name, code, view, approvalType, operatorMatcher, timeout, TitleGenerator.defaultTitleGenerator(), null, editable,mergeable, buttons);
        }


        public Nodes node(String name, String code, String view, ApprovalType approvalType, OperatorMatcher operatorMatcher, boolean editable,boolean mergeable) {
            return node(RandomGenerator.generateUUID(), name, code, view, approvalType, operatorMatcher, 0, TitleGenerator.defaultTitleGenerator(), null, editable,mergeable, null);
        }

        public Nodes node(String name, String code, String view, ApprovalType approvalType, OperatorMatcher operatorMatcher, boolean editable,boolean mergeable, List<FlowButton> buttons) {
            return node(RandomGenerator.generateUUID(), name, code, view, approvalType, operatorMatcher, 0, TitleGenerator.defaultTitleGenerator(), null, editable,mergeable, buttons);
        }

        public Nodes node(String name, String code, String view, ApprovalType approvalType, OperatorMatcher operatorMatcher, List<FlowButton> buttons) {
            return node(name, code, view, approvalType, operatorMatcher, true,false, buttons);
        }

        public Nodes node(String name, String code, String view, ApprovalType approvalType, OperatorMatcher operatorMatcher) {
            return node(name, code, view, approvalType, operatorMatcher, true,false, null);
        }

        public Nodes node(String name, String code, String view, ApprovalType approvalType, OperatorMatcher operatorMatcher, ErrTrigger errTrigger, boolean editable,boolean mergeable, List<FlowButton> buttons) {
            return node(RandomGenerator.generateUUID(), name, code, view, approvalType, operatorMatcher, 0, TitleGenerator.defaultTitleGenerator(), errTrigger, editable,mergeable, buttons);
        }

        public Nodes node(String name, String code, String view, ApprovalType approvalType, OperatorMatcher operatorMatcher, ErrTrigger errTrigger, boolean editable,boolean mergeable) {
            return node(RandomGenerator.generateUUID(), name, code, view, approvalType, operatorMatcher, 0, TitleGenerator.defaultTitleGenerator(), errTrigger, editable,mergeable, null);
        }


        public Relations relations() {
            return new Relations();
        }

        public FlowWork build() {
            work.enable();
            return work;
        }


    }

    public class Relations {

        public Relations relation(String name, String source, String target) {
            return relation(name, source, target, OutTrigger.defaultOutTrigger(), 1, false);
        }

        public Relations relation(String name, String source, String target, OutTrigger outTrigger, int order, boolean back) {
            FlowNode from = work.getNodeByCode(source);
            FlowNode to = work.getNodeByCode(target);
            FlowRelation relation = new FlowRelation(RandomGenerator.generateUUID(), name, from, to, outTrigger, order, back);
            work.addRelation(relation);
            return this;
        }

        public FlowWork build() {
            work.enable();
            return work;
        }


    }
}
