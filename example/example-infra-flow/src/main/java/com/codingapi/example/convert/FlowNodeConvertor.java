package com.codingapi.example.convert;

import com.alibaba.fastjson.JSON;
import com.codingapi.example.entity.FlowNodeEntity;
import com.codingapi.springboot.flow.domain.FlowNode;
import com.codingapi.springboot.flow.em.ApprovalType;
import com.codingapi.springboot.flow.em.NodeType;
import com.codingapi.springboot.flow.error.ErrTrigger;
import com.codingapi.springboot.flow.generator.TitleGenerator;
import com.codingapi.springboot.flow.matcher.OperatorMatcher;
import org.springframework.util.StringUtils;

public class FlowNodeConvertor {

    public static FlowNodeEntity convert(FlowNode flowNode, long workId){
        if(flowNode==null){
            return null;
        }
        FlowNodeEntity entity = new FlowNodeEntity();
        entity.setId(flowNode.getId());
        entity.setCode(flowNode.getCode());
        entity.setName(flowNode.getName());
        entity.setEditable(flowNode.isEditable());
        entity.setCreateTime(flowNode.getCreateTime());
        entity.setType(flowNode.getType().name());
        entity.setTimeout(flowNode.getTimeout());
        entity.setView(flowNode.getView());
        entity.setErrTrigger(flowNode.getErrTrigger()!=null?flowNode.getErrTrigger().getScript():null);
        entity.setApprovalType(flowNode.getApprovalType().name());
        entity.setTitleGenerator(flowNode.getTitleGenerator().getScript());
        entity.setOperatorMatcher(flowNode.getOperatorMatcher().getScript());
        entity.setUpdateTime(flowNode.getUpdateTime());
        entity.setWorkId(workId);
        if(flowNode.getButtons()!=null) {
            String json = JSON.toJSONString(flowNode.getButtons());
            entity.setButtons(json);
        }
        return entity;
    }

    public static FlowNode convert(FlowNodeEntity entity){
        if(entity==null){
            return null;
        }
        return new FlowNode(
                entity.getId(),
                entity.getCode(),
                entity.getName(),
                new TitleGenerator(entity.getTitleGenerator()),
                NodeType.parser(entity.getType()),
                entity.getView(),
                ApprovalType.parser(entity.getApprovalType()),
                new OperatorMatcher(entity.getOperatorMatcher()),
                entity.getEditable(),
                entity.getCreateTime(),
                entity.getUpdateTime(),
                entity.getTimeout(),
                StringUtils.hasLength(entity.getErrTrigger())?new ErrTrigger(entity.getErrTrigger()):null,
                entity.toFlowButtons());
    }

}
