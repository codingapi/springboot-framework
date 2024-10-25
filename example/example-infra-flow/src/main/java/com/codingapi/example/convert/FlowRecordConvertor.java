package com.codingapi.example.convert;

import com.codingapi.example.entity.FlowRecordEntity;
import com.codingapi.example.repository.UserRepository;
import com.codingapi.springboot.flow.domain.Opinion;
import com.codingapi.springboot.flow.em.FlowStatus;
import com.codingapi.springboot.flow.em.RecodeType;
import com.codingapi.springboot.flow.record.FlowRecord;

public class FlowRecordConvertor {

    public static FlowRecordEntity convert(FlowRecord flowRecord, UserRepository userRepository) {
        if (flowRecord == null) {
            return null;
        }

        FlowRecordEntity entity = new FlowRecordEntity();
        entity.setId(flowRecord.getId());
        entity.setPreId(flowRecord.getPreId());
        entity.setWorkId(flowRecord.getWorkId());
        entity.setProcessId(flowRecord.getProcessId());
        entity.setNodeCode(flowRecord.getNodeCode());
        entity.setTitle(flowRecord.getTitle());
        entity.setCurrentOperatorId(flowRecord.getCurrentOperatorId());
        entity.setRecodeType(flowRecord.getRecodeType().name());
        entity.setPass(flowRecord.isPass());
        entity.setCreateTime(flowRecord.getCreateTime());
        entity.setUpdateTime(flowRecord.getUpdateTime());
        entity.setFinishTime(flowRecord.getFinishTime());
        entity.setTimeoutTime(flowRecord.getTimeoutTime());
        entity.setPostponedCount(flowRecord.getPostponedCount());
        entity.setCreateOperatorId(flowRecord.getCreateOperatorId());
        if (flowRecord.getOpinion() != null) {
            entity.setOpinionAdvice(flowRecord.getOpinion().getAdvice());
            entity.setOpinionType(flowRecord.getOpinion().getType());
            entity.setOpinionResult(flowRecord.getOpinion().getResult());
        }

        entity.setCurrentOperatorName(userRepository.getUserById(flowRecord.getCurrentOperatorId()).getName());
        entity.setCreateOperatorName(userRepository.getUserById(flowRecord.getCreateOperatorId()).getName());

        entity.setFlowStatus(flowRecord.getFlowStatus().name());
        entity.setErrMessage(flowRecord.getErrMessage());
        entity.setBindClass(flowRecord.getBindClass());
        entity.setSnapshotId(flowRecord.getSnapshotId());
        entity.setRead(flowRecord.isRead());
        entity.setInterfere(flowRecord.isInterfere());
        entity.setReadTime(flowRecord.getReadTime());
        return entity;
    }


    public static FlowRecord convert(FlowRecordEntity entity) {
        if (entity == null) {
            return null;
        }

        FlowRecord flowRecord = new FlowRecord();
        flowRecord.setId(entity.getId());
        flowRecord.setPreId(entity.getPreId());
        flowRecord.setWorkId(entity.getWorkId());
        flowRecord.setProcessId(entity.getProcessId());
        flowRecord.setNodeCode(entity.getNodeCode());
        flowRecord.setTitle(entity.getTitle());
        flowRecord.setCurrentOperatorId(entity.getCurrentOperatorId());
        flowRecord.setRecodeType(RecodeType.parser(entity.getRecodeType()));
        flowRecord.setPass(entity.getPass());
        flowRecord.setCreateTime(entity.getCreateTime());
        flowRecord.setUpdateTime(entity.getUpdateTime());
        flowRecord.setFinishTime(entity.getFinishTime());
        flowRecord.setTimeoutTime(entity.getTimeoutTime());
        flowRecord.setPostponedCount(entity.getPostponedCount());
        flowRecord.setCreateOperatorId(entity.getCreateOperatorId());
        if (entity.getOpinionResult() != null && entity.getOpinionType() != null) {
            flowRecord.setOpinion(new Opinion(entity.getOpinionAdvice(), entity.getOpinionResult(), entity.getOpinionType()));
        }
        flowRecord.setFlowStatus(FlowStatus.parser(entity.getFlowStatus()));
        flowRecord.setErrMessage(entity.getErrMessage());
        flowRecord.setBindClass(entity.getBindClass());
        flowRecord.setSnapshotId(entity.getSnapshotId());
        flowRecord.setRead(entity.getRead());
        flowRecord.setInterfere(entity.getInterfere());
        flowRecord.setReadTime(entity.getReadTime());
        return flowRecord;
    }
}
