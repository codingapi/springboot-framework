package com.codingapi.example.infrastructure.convert;

import com.codingapi.example.infrastructure.entity.flow.FlowWorkEntity;
import com.codingapi.example.infrastructure.utils.ConvertUtils;
import com.codingapi.springboot.fast.manager.EntityManagerContent;
import com.codingapi.springboot.flow.context.FlowRepositoryContext;
import com.codingapi.springboot.flow.domain.FlowNode;
import com.codingapi.springboot.flow.domain.FlowWork;

import java.util.stream.Collectors;

public class FlowWorkConvertor {

    public static final ThreadLocal<FlowWork> current = new ThreadLocal<>();

    public static FlowWork convert(FlowWorkEntity entity){
        if(entity==null){
            return null;
        }

        FlowWork flowWork = new FlowWork();
        flowWork.setId(entity.getId());
        flowWork.setCreateTime(entity.getCreateTime());
        flowWork.setUpdateTime(entity.getUpdateTime());
        flowWork.setEnable(entity.isEnable());
        flowWork.setLock(entity.isLock());
        flowWork.setDescription(entity.getDescription());
        flowWork.setTitle(entity.getTitle());
        flowWork.setSchema(entity.getSchema());

        current.set(flowWork);

        flowWork.setNodes(
                ConvertUtils.string2List(entity.getNodeIds())
                        .stream()
                        .map(FlowRepositoryContext.getInstance()::getFlowNodeById)
                        .toList()
        );

        EntityManagerContent.getInstance().detach(entity);
        return flowWork;
    }

    public static FlowWorkEntity convert(FlowWork flowWork){
        if(flowWork==null){
            return null;
        }

        FlowWorkEntity entity = new FlowWorkEntity();
        entity.setId(flowWork.getId());
        entity.setCreateTime(flowWork.getCreateTime());
        entity.setUpdateTime(flowWork.getUpdateTime());
        entity.setEnable(flowWork.isEnable());
        entity.setLock(flowWork.isLock());
        entity.setDescription(flowWork.getDescription());
        entity.setTitle(flowWork.getTitle());
        entity.setSchema(flowWork.getSchema());
        if(flowWork.getNodes()!=null) {
            entity.setNodeIds(
                    flowWork.getNodes().stream()
                            .map(FlowNode::getId)
                            .map(String::valueOf)
                            .collect(Collectors.joining(","))
            );
        }

        return entity;
    }
}
