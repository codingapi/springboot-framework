package com.codingapi.example.infra.flow.convert;

import com.codingapi.example.infra.flow.entity.FlowRecordEntity;
import com.codingapi.springboot.flow.domain.Opinion;
import com.codingapi.springboot.flow.em.FlowSourceDirection;
import com.codingapi.springboot.flow.em.FlowStatus;
import com.codingapi.springboot.flow.em.FlowType;
import com.codingapi.springboot.flow.record.FlowRecord;
import com.codingapi.springboot.flow.repository.FlowOperatorRepository;

public class FlowRecordConvertor {

    public static FlowRecordEntity convert(FlowRecord flowRecord) {
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
        entity.setCurrentOperatorId(flowRecord.getCurrentOperator().getUserId());
        entity.setFlowType(flowRecord.getFlowType().name());
        if (flowRecord.getFlowSourceDirection() != null) {
            entity.setFlowSourceDirection(flowRecord.getFlowSourceDirection().name());
        }
        entity.setCreateTime(flowRecord.getCreateTime());
        entity.setUpdateTime(flowRecord.getUpdateTime());
        entity.setFinishTime(flowRecord.getFinishTime());
        entity.setTimeoutTime(flowRecord.getTimeoutTime());
        entity.setPostponedCount(flowRecord.getPostponedCount());
        entity.setCreateOperatorId(flowRecord.getCreateOperator().getUserId());
        if (flowRecord.getOpinion() != null) {
            entity.setOpinionAdvice(flowRecord.getOpinion().getAdvice());
            entity.setOpinionType(flowRecord.getOpinion().getType());
            entity.setOpinionResult(flowRecord.getOpinion().getResult());
        }


        entity.setCurrentOperatorName(flowRecord.getCurrentOperator().getName());
        entity.setCreateOperatorName(flowRecord.getCreateOperator().getName());
        entity.setWorkCode(flowRecord.getWorkCode());

        entity.setFlowStatus(flowRecord.getFlowStatus().name());
        entity.setErrMessage(flowRecord.getErrMessage());
        entity.setBindClass(flowRecord.getBindClass());
        entity.setSnapshotId(flowRecord.getSnapshotId());
        entity.setRead(flowRecord.isRead());
        entity.setInterfere(flowRecord.isInterfere());

        if (flowRecord.getInterferedOperator()!=null) {
            entity.setInterferedOperatorId(flowRecord.getInterferedOperator().getUserId());
            entity.setInterferedOperatorName(flowRecord.getInterferedOperator().getName());
        }
        entity.setReadTime(flowRecord.getReadTime());
        return entity;
    }


    public static FlowRecord convert(FlowRecordEntity entity, FlowOperatorRepository flowUserRepository) {
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
        flowRecord.setCurrentOperator(flowUserRepository.getFlowOperatorById(entity.getCurrentOperatorId()));
        flowRecord.setFlowType(FlowType.parser(entity.getFlowType()));
        flowRecord.setFlowSourceDirection(FlowSourceDirection.parser(entity.getFlowSourceDirection()));
        flowRecord.setCreateTime(entity.getCreateTime());
        flowRecord.setUpdateTime(entity.getUpdateTime());
        flowRecord.setFinishTime(entity.getFinishTime());
        flowRecord.setTimeoutTime(entity.getTimeoutTime());
        flowRecord.setPostponedCount(entity.getPostponedCount());
        flowRecord.setCreateOperator(flowUserRepository.getFlowOperatorById(entity.getCreateOperatorId()));
        if (entity.getOpinionResult() != null && entity.getOpinionType() != null) {
            flowRecord.setOpinion(new Opinion(entity.getOpinionAdvice(), entity.getOpinionResult(), entity.getOpinionType()));
        }
        flowRecord.setFlowStatus(FlowStatus.parser(entity.getFlowStatus()));
        flowRecord.setErrMessage(entity.getErrMessage());
        flowRecord.setBindClass(entity.getBindClass());
        flowRecord.setSnapshotId(entity.getSnapshotId());
        flowRecord.setRead(entity.getRead());
        flowRecord.setInterfere(entity.getInterfere());
        if(entity.getInterferedOperatorId()!=null) {
            flowRecord.setInterferedOperator(flowUserRepository.getFlowOperatorById(entity.getInterferedOperatorId()));
        }
        flowRecord.setWorkCode(entity.getWorkCode());
        flowRecord.setReadTime(entity.getReadTime());
        return flowRecord;
    }
}
