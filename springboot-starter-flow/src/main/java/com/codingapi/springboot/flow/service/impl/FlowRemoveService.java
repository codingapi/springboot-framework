package com.codingapi.springboot.flow.service.impl;

import com.codingapi.springboot.flow.bind.BindDataSnapshot;
import com.codingapi.springboot.flow.bind.IBindData;
import com.codingapi.springboot.flow.domain.FlowNode;
import com.codingapi.springboot.flow.domain.FlowWork;
import com.codingapi.springboot.flow.event.FlowApprovalEvent;
import com.codingapi.springboot.flow.record.FlowRecord;
import com.codingapi.springboot.flow.repository.FlowBindDataRepository;
import com.codingapi.springboot.flow.repository.FlowProcessRepository;
import com.codingapi.springboot.flow.repository.FlowRecordRepository;
import com.codingapi.springboot.flow.repository.FlowWorkRepository;
import com.codingapi.springboot.flow.service.FlowRecordVerifyService;
import com.codingapi.springboot.flow.user.IFlowOperator;
import com.codingapi.springboot.framework.event.EventPusher;
import lombok.AllArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@AllArgsConstructor
public class FlowRemoveService {

    private final FlowWorkRepository flowWorkRepository;
    private final FlowRecordRepository flowRecordRepository;
    private final FlowProcessRepository flowProcessRepository;
    private final FlowBindDataRepository flowBindDataRepository;

    /**
     * 删除流程
     *
     * @param recordId        流程记录id
     * @param currentOperator 当前操作者
     */
    public void remove(long recordId, IFlowOperator currentOperator) {
        FlowRecordVerifyService flowRecordVerifyService = new FlowRecordVerifyService(
                flowWorkRepository,
                flowRecordRepository,
                flowProcessRepository,
                recordId, currentOperator);


        flowRecordVerifyService.verifyFlowRecordCurrentOperator();
        flowRecordVerifyService.loadFlowWork();
        flowRecordVerifyService.loadFlowNode();
        flowRecordVerifyService.verifyFlowRecordNotFinish();
        flowRecordVerifyService.verifyFlowRecordIsTodo();
        FlowWork flowWork = flowRecordVerifyService.getFlowWork();
        FlowNode flowNode =  flowRecordVerifyService.getFlowNode();
        FlowRecord flowRecord = flowRecordVerifyService.getFlowRecord();

        if(!flowNode.isStartNode()){
            throw new IllegalArgumentException("flow record not remove");
        }
        BindDataSnapshot bindDataSnapshot = flowBindDataRepository.getBindDataSnapshotById(flowRecord.getSnapshotId());
        IBindData bindData =  bindDataSnapshot.toBindData();

        flowProcessRepository.deleteByProcessId(flowRecord.getProcessId());
        flowRecordRepository.deleteByProcessId(flowRecord.getProcessId());

        EventPusher.push(new FlowApprovalEvent(FlowApprovalEvent.STATE_DELETE, flowRecord, currentOperator, flowWork, bindData), true);
    }
}
