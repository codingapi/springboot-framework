package com.codingapi.example.convert;

import com.codingapi.example.entity.FlowRelationEntity;
import com.codingapi.springboot.flow.domain.FlowNode;
import com.codingapi.springboot.flow.domain.FlowRelation;
import com.codingapi.springboot.flow.trigger.OutTrigger;

import java.util.List;

public class FlowRelationConvertor {


    public static FlowRelation convert(FlowRelationEntity entity, List<FlowNode> flowNodes) {

        FlowNode source = null;
        FlowNode target = null;

        if (entity == null) {
            return null;
        }

        for (FlowNode flowNode : flowNodes) {
            if (flowNode.getId().equals(entity.getSourceId())) {
                source = flowNode;
            }
            if (flowNode.getId().equals(entity.getTargetId())) {
                target = flowNode;
            }
        }

        return new FlowRelation(entity.getId(), entity.getName(), source, target, entity.getOrder(), entity.getBack(), new OutTrigger(entity.getOutTrigger()), entity.getCreateTime(), entity.getUpdateTime());
    }


    public static FlowRelationEntity convert(FlowRelation flowRelation, long workId) {
        if (flowRelation == null) {
            return null;
        }
        FlowRelationEntity entity = new FlowRelationEntity();
        entity.setId(flowRelation.getId());
        entity.setWorkId(workId);
        entity.setName(flowRelation.getName());
        entity.setSourceId(flowRelation.getSource().getId());
        entity.setTargetId(flowRelation.getTarget().getId());
        entity.setOrder(flowRelation.getOrder());
        entity.setBack(flowRelation.isBack());
        entity.setOutTrigger(flowRelation.getOutTrigger().getScript());
        entity.setCreateTime(flowRelation.getCreateTime());
        entity.setUpdateTime(flowRelation.getUpdateTime());
        return entity;
    }
}
