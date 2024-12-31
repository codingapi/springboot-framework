package com.codingapi.springboot.flow.service.impl;

import com.codingapi.springboot.flow.domain.FlowWork;
import com.codingapi.springboot.flow.event.FlowApprovalEvent;
import com.codingapi.springboot.flow.record.FlowRecord;
import com.codingapi.springboot.flow.repository.*;
import com.codingapi.springboot.flow.service.FlowRecordVerifyService;
import com.codingapi.springboot.flow.user.IFlowOperator;
import com.codingapi.springboot.framework.event.EventPusher;
import lombok.AllArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
@AllArgsConstructor
public class FlowRecallService {

    private final FlowRecordRepository flowRecordRepository;
    private final FlowProcessRepository flowProcessRepository;

    /**
     * 撤回流程
     *
     * @param recordId        流程记录id
     * @param currentOperator 当前操作者
     */
    public void recall(long recordId, IFlowOperator currentOperator) {
        FlowRecordVerifyService flowRecordVerifyService = new FlowRecordVerifyService(flowRecordRepository,
                flowProcessRepository,
                recordId, currentOperator);

        flowRecordVerifyService.verifyFlowRecordCurrentOperator();
        flowRecordVerifyService.loadFlowWork();
        flowRecordVerifyService.loadFlowNode();
        flowRecordVerifyService.verifyFlowRecordNotFinish();
        flowRecordVerifyService.verifyFlowRecordNotTodo();

        FlowRecord flowRecord = flowRecordVerifyService.getFlowRecord();
        FlowWork flowWork = flowRecordVerifyService.getFlowWork();

        // 下一流程的流程记录
        List<FlowRecord> childrenRecords = flowRecordRepository.findFlowRecordByPreId(recordId);
        // 下一流程均为办理且未读

        // 如果是在开始节点撤销，则直接删除
        if (flowRecord.isStartRecord() && flowRecord.isTodo()) {
            if (!childrenRecords.isEmpty()) {
                throw new IllegalArgumentException("flow record not recall");
            }
            flowRecordRepository.delete(List.of(flowRecord));
        } else {
            // 如果是在中间节点撤销，则需要判断是否所有的子流程都是未读状态
            if (childrenRecords.isEmpty()) {
                throw new IllegalArgumentException("flow record not submit");
            }

            boolean allUnDone = childrenRecords.stream().allMatch(item -> item.isUnRead() && item.isTodo());
            if (!allUnDone) {
                throw new IllegalArgumentException("flow record not recall");
            }
            flowRecord.recall();
            flowRecordRepository.update(flowRecord);

            flowRecordRepository.delete(childrenRecords);
        }

        EventPusher.push(new FlowApprovalEvent(FlowApprovalEvent.STATE_RECALL, flowRecord, currentOperator, flowWork, null), true);
    }
}
