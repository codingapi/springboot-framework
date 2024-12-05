package com.codingapi.springboot.flow.service.impl;

import com.codingapi.springboot.flow.domain.FlowWork;
import com.codingapi.springboot.flow.record.FlowRecord;
import com.codingapi.springboot.flow.repository.FlowProcessRepository;
import com.codingapi.springboot.flow.repository.FlowRecordRepository;
import com.codingapi.springboot.flow.service.FlowRecordVerifyService;
import com.codingapi.springboot.flow.user.IFlowOperator;
import lombok.AllArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@AllArgsConstructor
public class FlowPostponedService {

    private final FlowRecordRepository flowRecordRepository;
    private final FlowProcessRepository flowProcessRepository;

    /**
     * 延期待办
     *
     * @param recordId        流程记录id
     * @param currentOperator 当前操作者
     * @param time            延期时间
     */
    public void postponed(long recordId, IFlowOperator currentOperator, long time) {
        FlowRecordVerifyService flowRecordVerifyService = new FlowRecordVerifyService(flowRecordRepository,
                flowProcessRepository,
                recordId, currentOperator);

        flowRecordVerifyService.loadFlowRecord();
        flowRecordVerifyService.verifyFlowRecordSubmitState();
        flowRecordVerifyService.verifyFlowRecordCurrentOperator();
        flowRecordVerifyService.loadFlowWork();
        flowRecordVerifyService.verifyFlowRecordNotFinish();
        flowRecordVerifyService.verifyFlowRecordNotDone();

        FlowRecord flowRecord = flowRecordVerifyService.getFlowRecord();
        FlowWork flowWork = flowRecordVerifyService.getFlowWork();

        flowRecord.postponedTime(flowWork.getPostponedMax(), time);
        flowRecordRepository.update(flowRecord);
    }
}
