package com.codingapi.springboot.flow.service.impl;

import com.codingapi.springboot.flow.bind.BindDataSnapshot;
import com.codingapi.springboot.flow.bind.IBindData;
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

    private final FlowWorkRepository flowWorkRepository;
    private final FlowRecordRepository flowRecordRepository;
    private final FlowProcessRepository flowProcessRepository;
    private final FlowBindDataRepository flowBindDataRepository;

    /**
     * 撤回流程
     *
     * @param recordId        流程记录id
     * @param currentOperator 当前操作者
     */
    public void recall(long recordId, IFlowOperator currentOperator) {
        FlowRecordVerifyService flowRecordVerifyService = new FlowRecordVerifyService(
                flowWorkRepository,
                flowRecordRepository,
                flowProcessRepository,
                recordId,
                currentOperator);

        flowRecordVerifyService.verifyFlowRecordCurrentOperator();
        flowRecordVerifyService.loadFlowWork();
        flowRecordVerifyService.loadFlowNode();
        flowRecordVerifyService.verifyFlowRecordNotFinish();
        flowRecordVerifyService.verifyFlowRecordNotTodo();

        FlowRecord flowRecord = flowRecordVerifyService.getFlowRecord();
        FlowWork flowWork = flowRecordVerifyService.getFlowWork();
        BindDataSnapshot bindDataSnapshot = flowBindDataRepository.getBindDataSnapshotById(flowRecord.getSnapshotId());

        if(flowRecordVerifyService.isCreateOperator()){
            List<FlowRecord> records = flowRecordRepository.findFlowRecordByProcessId(flowRecord.getProcessId());
            for(FlowRecord record:records){
                if(!record.isStartRecord()) {
                    record.delete();
                    flowRecordRepository.update(record);
                }else {
                    record.recall();
                    flowRecord = record;
                    flowRecordRepository.update(record);
                }
            }
        }else {
            // 下一流程的流程记录
            List<FlowRecord> childrenRecords = flowRecordRepository.findFlowRecordByPreId(recordId);

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

            for (FlowRecord childrenRecord : childrenRecords) {
                childrenRecord.delete();
            }
            flowRecordRepository.save(childrenRecords);
        }

        IBindData bindData = bindDataSnapshot.toBindData();
        EventPusher.push(new FlowApprovalEvent(FlowApprovalEvent.STATE_RECALL, flowRecord, currentOperator, flowWork, bindData), true);
    }
}
