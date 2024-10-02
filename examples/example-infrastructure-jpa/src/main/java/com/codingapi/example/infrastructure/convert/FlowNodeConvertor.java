package com.codingapi.example.infrastructure.convert;

import com.codingapi.example.infrastructure.entity.flow.FlowNodeEntity;
import com.codingapi.example.infrastructure.utils.ConvertUtils;
import com.codingapi.springboot.fast.manager.EntityManagerContent;
import com.codingapi.springboot.flow.context.FlowRepositoryContext;
import com.codingapi.springboot.flow.creator.ScriptTitleCreator;
import com.codingapi.springboot.flow.domain.FlowNode;
import com.codingapi.springboot.flow.matcher.ScriptOperatorMatcher;
import com.codingapi.springboot.flow.trigger.ScriptErrTrigger;
import com.codingapi.springboot.flow.trigger.ScriptOutTrigger;

public class FlowNodeConvertor {

    public static FlowNode convert(FlowNodeEntity entity) {
        if (entity == null) {
            return null;
        }

        FlowNode flowNode = new FlowNode();
        flowNode.setId(entity.getId());
        flowNode.setCode(entity.getCode());
        flowNode.setParentCode(entity.getParentCode());
        flowNode.setFlowType(entity.getFlowType());
        flowNode.setCreateTime(entity.getCreateTime());
        flowNode.setName(entity.getName());
        flowNode.setView(entity.getView());
        flowNode.setType(entity.getType());
        flowNode.setUpdateTime(entity.getUpdateTime());
        flowNode.setNext(ConvertUtils.string2List(entity.getNext()));
        flowNode.setCreateUser(FlowRepositoryContext.getInstance().getOperatorById(entity.getCreateUserId()));
        flowNode.setErrOperatorMatcher(new ScriptOperatorMatcher(entity.getErrOperatorMatcherScript()));
        flowNode.setErrTrigger(new ScriptErrTrigger(entity.getErrTriggerScript()));
        flowNode.setOutTrigger(new ScriptOutTrigger(entity.getOutTriggerScript()));
        flowNode.setOutOperatorMatcher(new ScriptOperatorMatcher(entity.getOutOperatorMatcherScript()));
        flowNode.setTitleCreator(new ScriptTitleCreator(entity.getTitleCreatorScript()));
        flowNode.setFlowWork(FlowRepositoryContext.getInstance().getFlowWorkById(entity.getFlowWorkId()));

        EntityManagerContent.getInstance().detach(entity);

        return flowNode;
    }

    public static FlowNodeEntity convert(FlowNode flowNode) {
        if (flowNode == null) {
            return null;
        }

        FlowNodeEntity entity = new FlowNodeEntity();
        entity.setId(flowNode.getId());
        entity.setCode(flowNode.getCode());
        entity.setParentCode(flowNode.getParentCode());
        entity.setFlowType(flowNode.getFlowType());
        entity.setCreateTime(flowNode.getCreateTime());
        entity.setName(flowNode.getName());
        entity.setView(flowNode.getView());
        entity.setType(flowNode.getType());
        entity.setUpdateTime(flowNode.getUpdateTime());
        entity.setNext(ConvertUtils.list2String(flowNode.getNext()));
        entity.setCreateUserId(flowNode.getCreateUser().getId());
        entity.setErrOperatorMatcherScript(((ScriptOperatorMatcher) (flowNode.getErrOperatorMatcher())).getScript());
        entity.setErrTriggerScript(((ScriptErrTrigger) (flowNode.getErrTrigger())).getScript());
        entity.setOutOperatorMatcherScript(((ScriptOperatorMatcher) (flowNode.getOutOperatorMatcher())).getScript());
        entity.setOutTriggerScript(((ScriptOutTrigger) (flowNode.getOutTrigger())).getScript());
        entity.setTitleCreatorScript(((ScriptTitleCreator) (flowNode.getTitleCreator())).getScript());
        entity.setFlowWorkId(flowNode.getFlowWork().getId());


        return entity;
    }
}
