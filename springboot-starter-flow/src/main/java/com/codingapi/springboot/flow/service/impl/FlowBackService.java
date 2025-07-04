package com.codingapi.springboot.flow.service.impl;

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

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 流程退回服务（流程管理员操作）
 */
@Transactional
@AllArgsConstructor
public class FlowBackService {

    private final FlowWorkRepository flowWorkRepository;
    private final FlowRecordRepository flowRecordRepository;
    private final FlowProcessRepository flowProcessRepository;
    private final FlowBindDataRepository flowBindDataRepository;


    /**
     * 退回流程
     *
     * @param processId       流程processId
     * @param backNodeCode    退回节点编码
     * @param currentOperator 当前操作者
     */
    public void back(String processId, String backNodeCode, IFlowOperator currentOperator) {
        if (!currentOperator.isFlowManager()) {
            throw new IllegalArgumentException("current operator not flow manager");
        }

        FlowProcess flowProcess = flowProcessRepository.getFlowProcessByProcessId(processId);
        if (flowProcess.isVoided()) {
            throw new IllegalArgumentException("flow process already voided");
        }

        List<FlowRecord> historyRecords = flowRecordRepository
                .findFlowRecordByProcessId(processId)
                .stream()
                .sorted(Comparator.comparingLong(FlowRecord::getId))
                .collect(Collectors.toList());

        for (FlowRecord flowRecord : historyRecords) {
            if (flowRecord.isFinish()) {
                throw new IllegalArgumentException("flow record already finish");
            }
        }

        if (historyRecords.isEmpty()) {
            throw new IllegalArgumentException("flow record not found");
        }

        List<String> historyNodeCodes = historyRecords.stream()
                .map(FlowRecord::getNodeCode)
                .distinct()
                .collect(Collectors.toList());

        if (!historyNodeCodes.contains(backNodeCode)) {
            throw new IllegalArgumentException("flow node code not found");
        }

        FlowRecord beginRecord = historyRecords.get(0);
        for (FlowRecord flowRecord : historyRecords) {
            if(flowRecord.getNodeCode().equals(backNodeCode)){
                beginRecord = flowRecord;
            }
        }

        for (FlowRecord flowRecord : historyRecords) {
            if(flowRecord.getId()> beginRecord.getId() ){
                flowRecord.delete();
            }else {
                if(flowRecord.getNodeCode().equals(beginRecord.getNodeCode())){
                    flowRecord.recall();
                }
            }
        }

        flowRecordRepository.save(historyRecords);

        FlowWork flowWork = flowWorkRepository.getFlowWorkByCode(beginRecord.getWorkCode());
        IBindData bindData = flowBindDataRepository.getBindDataSnapshotById(beginRecord.getSnapshotId()).toBindData();

        EventPusher.push(new FlowApprovalEvent(FlowApprovalEvent.STATE_BACK, beginRecord, currentOperator, flowWork, bindData), true);

    }
}
