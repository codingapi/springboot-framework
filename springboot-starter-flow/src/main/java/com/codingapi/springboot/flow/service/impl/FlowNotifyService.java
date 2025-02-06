package com.codingapi.springboot.flow.service.impl;

import com.codingapi.springboot.flow.bind.BindDataSnapshot;
import com.codingapi.springboot.flow.domain.FlowWork;
import com.codingapi.springboot.flow.em.FlowType;
import com.codingapi.springboot.flow.event.FlowApprovalEvent;
import com.codingapi.springboot.flow.record.FlowRecord;
import com.codingapi.springboot.flow.repository.FlowBindDataRepository;
import com.codingapi.springboot.flow.repository.FlowProcessRepository;
import com.codingapi.springboot.flow.repository.FlowRecordRepository;
import com.codingapi.springboot.flow.repository.FlowWorkRepository;
import com.codingapi.springboot.flow.service.FlowServiceRepositoryHolder;
import com.codingapi.springboot.flow.user.IFlowOperator;
import com.codingapi.springboot.framework.event.EventPusher;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Transactional
public class FlowNotifyService {

    private final String processId;
    private final IFlowOperator currentOperator;
    private final FlowRecordRepository flowRecordRepository;
    private final FlowBindDataRepository flowBindDataRepository;
    private final FlowWorkRepository flowWorkRepository;
    private final FlowProcessRepository flowProcessRepository;


    public FlowNotifyService(String processId, IFlowOperator currentOperator, FlowServiceRepositoryHolder flowServiceRepositoryHolder) {
        this.processId = processId;
        this.currentOperator = currentOperator;
        this.flowRecordRepository = flowServiceRepositoryHolder.getFlowRecordRepository();
        this.flowBindDataRepository = flowServiceRepositoryHolder.getFlowBindDataRepository();
        this.flowWorkRepository = flowServiceRepositoryHolder.getFlowWorkRepository();
        this.flowProcessRepository = flowServiceRepositoryHolder.getFlowProcessRepository();
    }


    /**
     * 获取流程设计对象
     */
    public FlowWork loadFlowWork(FlowRecord flowRecord) {
        FlowWork flowWork = flowProcessRepository.getFlowWorkByProcessId(flowRecord.getProcessId());
        if (flowWork == null) {
            flowWork = flowWorkRepository.getFlowWorkByCode(flowRecord.getWorkCode());
        }
        if (flowWork == null) {
            throw new IllegalArgumentException("flow work not found");
        }
        flowWork.enableValidate();

        return flowWork;
    }

    /**
     * 流程通知
     */
    public void notifyFlow() {
        List<FlowRecord> flowRecords = flowRecordRepository.findFlowRecordByProcessId(processId);
        List<FlowRecord> waitingRecords = flowRecords.stream().filter(FlowRecord::isWaiting).collect(Collectors.toList());
        for (FlowRecord flowRecord : waitingRecords) {
            if (flowRecord.isOperator(currentOperator)) {
                flowRecord.setFlowType(FlowType.TODO);
                flowRecordRepository.update(flowRecord);

                BindDataSnapshot snapshot = flowBindDataRepository.getBindDataSnapshotById(flowRecord.getSnapshotId());

                FlowWork flowWork = this.loadFlowWork(flowRecord);

                EventPusher.push(new FlowApprovalEvent(FlowApprovalEvent.STATE_TODO,
                        flowRecord,
                        flowRecord.getCurrentOperator(),
                        flowWork,
                        snapshot.toBindData()
                ), true);
            }
        }
    }
}
