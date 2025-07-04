package com.codingapi.springboot.flow.service.impl;

import com.codingapi.springboot.flow.bind.BindDataSnapshot;
import com.codingapi.springboot.flow.bind.IBindData;
import com.codingapi.springboot.flow.domain.FlowWork;
import com.codingapi.springboot.flow.event.FlowApprovalEvent;
import com.codingapi.springboot.flow.record.FlowProcess;
import com.codingapi.springboot.flow.record.FlowRecord;
import com.codingapi.springboot.flow.repository.FlowBindDataRepository;
import com.codingapi.springboot.flow.repository.FlowProcessRepository;
import com.codingapi.springboot.flow.repository.FlowRecordRepository;
import com.codingapi.springboot.flow.repository.FlowWorkRepository;
import com.codingapi.springboot.flow.user.IFlowOperator;
import com.codingapi.springboot.framework.event.EventPusher;
import lombok.AllArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 流程作废服务（流程管理员操作）
 */
@Transactional
@AllArgsConstructor
public class FlowVoidedService {

    private final FlowWorkRepository flowWorkRepository;
    private final FlowRecordRepository flowRecordRepository;
    private final FlowProcessRepository flowProcessRepository;
    private final FlowBindDataRepository flowBindDataRepository;


    /**
     * 作废流程
     *
     * @param processId       流程processId
     * @param currentOperator 当前操作者
     */
    public void voided(String processId, IFlowOperator currentOperator) {
        if (!currentOperator.isFlowManager()) {
            throw new IllegalArgumentException("current operator not flow manager");
        }

        FlowProcess flowProcess = flowProcessRepository.getFlowProcessByProcessId(processId);
        if(flowProcess.isVoided()){
            throw new IllegalArgumentException("flow process already voided");
        }
        flowProcess.voided();
        flowProcessRepository.save(flowProcess);

        FlowRecord currentRecord = null;
        List<FlowRecord> flowRecords = flowRecordRepository.findFlowRecordByProcessId(processId);
        if (!flowRecords.isEmpty()) {
            for (FlowRecord flowRecord : flowRecords) {
                if (flowRecord.isFinish()) {
                    throw new IllegalArgumentException("flow record already finish");
                }
                flowRecord.delete();
                if(currentRecord==null || flowRecord.getId()> currentRecord.getId()){
                    currentRecord = flowRecord;
                }
            }
        }
        flowRecordRepository.save(flowRecords);

        FlowWork flowWork = null;

        IBindData bindData = null;
        if (currentRecord != null) {
            // 删除流程绑定数据
            BindDataSnapshot dataSnapshot = flowBindDataRepository.getBindDataSnapshotById(currentRecord.getSnapshotId());
            if (dataSnapshot != null) {
                bindData = dataSnapshot.toBindData();
            }
            flowWork = flowWorkRepository.getFlowWorkByCode(currentRecord.getWorkCode());
        }

        EventPusher.push(new FlowApprovalEvent(FlowApprovalEvent.STATE_VOIDED, currentRecord, currentOperator, flowWork, bindData), true);


    }
}
