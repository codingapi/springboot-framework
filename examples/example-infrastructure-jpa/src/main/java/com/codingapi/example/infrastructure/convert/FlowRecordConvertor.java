package com.codingapi.example.infrastructure.convert;

import com.alibaba.fastjson.JSONObject;
import com.codingapi.example.infrastructure.entity.flow.FlowRecordEntity;
import com.codingapi.springboot.fast.manager.EntityManagerContent;
import com.codingapi.springboot.flow.context.FlowRepositoryContext;
import com.codingapi.springboot.flow.domain.FlowRecord;
import com.codingapi.springboot.flow.domain.Opinion;

public class FlowRecordConvertor {

    public static FlowRecord convert(FlowRecordEntity entity){
        if(entity==null){
            return null;
        }

        FlowRecord flowRecord = new FlowRecord();
        flowRecord.setId(entity.getId());
        flowRecord.setProcessId(entity.getProcessId());
        flowRecord.setCreateTime(entity.getCreateTime());
        flowRecord.setUpdateTime(entity.getUpdateTime());
        flowRecord.setFlowStatus(entity.getFlowStatus());
        flowRecord.setErrMessage(entity.getErrMessage());
        flowRecord.setNodeStatus(entity.getNodeStatus());
        flowRecord.setOpinion(JSONObject.parseObject(entity.getOpinion(), Opinion.class));
        flowRecord.setPreRecordId(entity.getPreRecordId());
        flowRecord.setPreNodeCode(entity.getPreNodeCode());
        flowRecord.setTitle(entity.getTitle());
        flowRecord.setState(entity.getState());
        flowRecord.setWork(FlowRepositoryContext.getInstance().getFlowWorkById(entity.getWorkId()));
        flowRecord.setNode(FlowRepositoryContext.getInstance().getFlowNodeById(entity.getNodeId()));
        flowRecord.setCreateOperatorUser(FlowRepositoryContext.getInstance().getOperatorById(entity.getCreateOperatorUserId()));
        flowRecord.setOperatorUser(FlowRepositoryContext.getInstance().getOperatorById(entity.getOperatorUserId()));
        flowRecord.setBindDataSnapshot(FlowRepositoryContext.getInstance().getBindDataSnapshotById(entity.getBindDataSnapshotId()));

        EntityManagerContent.getInstance().detach(entity);

        return flowRecord;
    }

    public static FlowRecordEntity convert(FlowRecord flowRecord){
        if(flowRecord==null){
            return null;
        }

        FlowRecordEntity entity = new FlowRecordEntity();
        entity.setId(flowRecord.getId());
        entity.setProcessId(flowRecord.getProcessId());
        entity.setCreateTime(flowRecord.getCreateTime());
        entity.setUpdateTime(flowRecord.getUpdateTime());
        entity.setFlowStatus(flowRecord.getFlowStatus());
        entity.setErrMessage(flowRecord.getErrMessage());
        entity.setNodeStatus(flowRecord.getNodeStatus());
        entity.setOpinion(JSONObject.toJSONString(flowRecord.getOpinion()));
        entity.setPreRecordId(flowRecord.getPreRecordId());
        entity.setPreNodeCode(flowRecord.getPreNodeCode());
        entity.setTitle(flowRecord.getTitle());
        entity.setState(flowRecord.getState());
        entity.setWorkId(flowRecord.getWork().getId());
        entity.setNodeId(flowRecord.getNode().getId());
        entity.setCreateOperatorUserId(flowRecord.getCreateOperatorUser().getId());
        entity.setOperatorUserId(flowRecord.getOperatorUser().getId());
        entity.setBindDataSnapshotId(flowRecord.getBindDataSnapshot().getId());

        return entity;
    }

}
