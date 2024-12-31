package com.codingapi.springboot.flow.service.impl;

import com.codingapi.springboot.flow.bind.BindDataSnapshot;
import com.codingapi.springboot.flow.bind.IBindData;
import com.codingapi.springboot.flow.domain.Opinion;
import com.codingapi.springboot.flow.record.FlowRecord;
import com.codingapi.springboot.flow.repository.*;
import com.codingapi.springboot.flow.service.FlowRecordVerifyService;
import com.codingapi.springboot.flow.user.IFlowOperator;
import lombok.AllArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@AllArgsConstructor
public class FlowSaveService {

    private final FlowRecordRepository flowRecordRepository;
    private final FlowBindDataRepository flowBindDataRepository;
    private final FlowProcessRepository flowProcessRepository;

    /**
     * 保存流程
     *
     * @param recordId        流程记录id
     * @param currentOperator 当前操作者
     * @param bindData        绑定数据
     * @param advice          审批意见
     */
    public void save(long recordId, IFlowOperator currentOperator, IBindData bindData, String advice) {
        FlowRecordVerifyService flowRecordVerifyService = new FlowRecordVerifyService(flowRecordRepository,
                flowProcessRepository,
                recordId, currentOperator);
        flowRecordVerifyService.verifyFlowRecordSubmitState();
        flowRecordVerifyService.verifyFlowRecordCurrentOperator();
        flowRecordVerifyService.loadFlowWork();
        flowRecordVerifyService.loadFlowNode();
        flowRecordVerifyService.verifyFlowNodeEditableState(false);

        Opinion opinion = Opinion.save(advice);
        FlowRecord flowRecord = flowRecordVerifyService.getFlowRecord();
        BindDataSnapshot snapshot = new BindDataSnapshot(flowRecord.getSnapshotId(), bindData);
        flowBindDataRepository.update(snapshot);

        flowRecord.setOpinion(opinion);
        flowRecordRepository.update(flowRecord);
    }

}
