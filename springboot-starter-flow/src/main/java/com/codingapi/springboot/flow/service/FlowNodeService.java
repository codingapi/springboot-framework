package com.codingapi.springboot.flow.service;

import com.codingapi.springboot.flow.bind.BindDataSnapshot;
import com.codingapi.springboot.flow.bind.IBindData;
import com.codingapi.springboot.flow.domain.FlowNode;
import com.codingapi.springboot.flow.domain.FlowWork;
import com.codingapi.springboot.flow.domain.Opinion;
import com.codingapi.springboot.flow.em.FlowSourceDirection;
import com.codingapi.springboot.flow.record.FlowRecord;
import com.codingapi.springboot.flow.repository.FlowBindDataRepository;
import com.codingapi.springboot.flow.repository.FlowOperatorRepository;
import lombok.Getter;

import java.util.List;

class FlowNodeService {


    private final FlowOperatorRepository flowOperatorRepository;
    private final FlowBindDataRepository flowBindDataRepository;

    private final FlowRecordService2 flowRecordService2;

    private final FlowRecord flowRecord;
    private final FlowWork flowWork;
    private final FlowNode flowNode;
    private final IBindData bindData;
    private final Opinion opinion;

    // load Object
    @Getter
    private FlowSourceDirection flowSourceDirection;
    @Getter
    private List<FlowRecord> childrenRecords;
    @Getter
    private BindDataSnapshot snapshot;

    public FlowNodeService(FlowOperatorRepository flowOperatorRepository,
                           FlowBindDataRepository flowBindDataRepository,
                           FlowRecordService2 flowRecordService2,
                           IBindData bindData,
                           Opinion opinion) {

        this.flowOperatorRepository = flowOperatorRepository;
        this.flowBindDataRepository = flowBindDataRepository;

        this.flowRecordService2 = flowRecordService2;
        this.bindData = bindData;
        this.opinion = opinion;

        this.flowRecord = flowRecordService2.getFlowRecord();
        this.flowWork = flowRecordService2.getFlowWork();
        this.flowNode = flowRecordService2.getFlowNode();

    }


    public void loadFlowSourceDirection(){
        if(opinion.isSuccess()){
            flowSourceDirection = FlowSourceDirection.PASS;
        }
        if(opinion.isReject()){
            flowSourceDirection = FlowSourceDirection.REJECT;
        }
    }


    /**
     *  保存流程表单数据
     */
    public void updateSnapshot() {
        BindDataSnapshot snapshot = new BindDataSnapshot(flowRecord.getSnapshotId(), bindData);
        flowBindDataRepository.update(snapshot);
    }



    public void loadSnapshot(){
        // 保存绑定数据
        if (flowNode.isEditable()) {
            snapshot = new BindDataSnapshot(bindData);
            flowBindDataRepository.save(snapshot);
        } else {
            snapshot = flowBindDataRepository.getBindDataSnapshotById(flowRecord.getSnapshotId());
        }
    }


    /**
     *  更新流程记录
     */
    public void updateFlowRecord() {
        flowRecord.setOpinion(opinion);
        flowRecordService2.flowRecordRepository.update(flowRecord);
    }


    public void loadChildrenRecords(){
        childrenRecords = flowRecordService2.flowRecordRepository.findFlowRecordByPreId(flowRecord.getId());
    }


    public void verifyChildrenRecordsIsEmpty(){
        if (!childrenRecords.isEmpty()) {
            throw new IllegalArgumentException("flow node is done");
        }
    }


    public void verifyFlowSourceDirection() {
        if(flowSourceDirection==null){
            throw new IllegalArgumentException("flow source direction is null");
        }
        if(flowNode.isStartNode() && flowSourceDirection == FlowSourceDirection.REJECT){
            throw new IllegalArgumentException("flow node is start node");
        }
    }
}
