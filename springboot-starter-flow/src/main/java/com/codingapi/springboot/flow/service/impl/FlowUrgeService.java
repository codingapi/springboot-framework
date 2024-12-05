package com.codingapi.springboot.flow.service.impl;

import com.codingapi.springboot.flow.domain.FlowWork;
import com.codingapi.springboot.flow.event.FlowApprovalEvent;
import com.codingapi.springboot.flow.record.FlowRecord;
import com.codingapi.springboot.flow.repository.FlowProcessRepository;
import com.codingapi.springboot.flow.repository.FlowRecordRepository;
import com.codingapi.springboot.flow.service.FlowRecordVerifyService;
import com.codingapi.springboot.flow.user.IFlowOperator;
import com.codingapi.springboot.framework.event.EventPusher;
import lombok.AllArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
@AllArgsConstructor
public class FlowUrgeService {


    private final FlowRecordRepository flowRecordRepository;
    private final FlowProcessRepository flowProcessRepository;

    /**
     * 催办流程
     *
     * @param recordId        流程记录id
     * @param currentOperator 当前操作者
     */
    public void urge(long recordId, IFlowOperator currentOperator) {
        FlowRecordVerifyService flowRecordVerifyService = new FlowRecordVerifyService(flowRecordRepository,
                flowProcessRepository,
                recordId, currentOperator);
        flowRecordVerifyService.loadFlowRecord();
        flowRecordVerifyService.loadFlowWork();
        flowRecordVerifyService.verifyFlowRecordIsDone();

        FlowRecord flowRecord = flowRecordVerifyService.getFlowRecord();
        FlowWork flowWork = flowRecordVerifyService.getFlowWork();

        List<FlowRecord> todoRecords = flowRecordRepository.findTodoFlowRecordByProcessId(flowRecord.getProcessId());

        // 推送催办消息
        for (FlowRecord record : todoRecords) {
            IFlowOperator pushOperator = record.getCurrentOperator();
            EventPusher.push(new FlowApprovalEvent(FlowApprovalEvent.STATE_URGE, record, pushOperator, flowWork, null), true);
        }

    }
}
