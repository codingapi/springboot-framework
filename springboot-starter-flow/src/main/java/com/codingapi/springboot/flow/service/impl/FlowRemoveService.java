package com.codingapi.springboot.flow.service.impl;

import com.codingapi.springboot.flow.domain.FlowNode;
import com.codingapi.springboot.flow.record.FlowRecord;
import com.codingapi.springboot.flow.repository.FlowProcessRepository;
import com.codingapi.springboot.flow.repository.FlowRecordRepository;
import com.codingapi.springboot.flow.repository.FlowWorkRepository;
import com.codingapi.springboot.flow.service.FlowRecordVerifyService;
import com.codingapi.springboot.flow.user.IFlowOperator;
import lombok.AllArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@AllArgsConstructor
public class FlowRemoveService {

    private final FlowWorkRepository flowWorkRepository;
    private final FlowRecordRepository flowRecordRepository;
    private final FlowProcessRepository flowProcessRepository;

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
        FlowNode flowNode =  flowRecordVerifyService.getFlowNode();
        FlowRecord flowRecord = flowRecordVerifyService.getFlowRecord();

        if(!flowNode.isStartNode()){
            throw new IllegalArgumentException("flow record not remove");
        }

        flowProcessRepository.deleteByProcessId(flowRecord.getProcessId());

        flowRecordRepository.deleteByProcessId(flowRecord.getProcessId());
    }
}
