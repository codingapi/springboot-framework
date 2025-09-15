package com.codingapi.springboot.flow.service.impl;

import com.codingapi.springboot.flow.bind.BindDataSnapshot;
import com.codingapi.springboot.flow.domain.FlowNode;
import com.codingapi.springboot.flow.domain.FlowWork;
import com.codingapi.springboot.flow.event.FlowApprovalEvent;
import com.codingapi.springboot.flow.record.FlowRecord;
import com.codingapi.springboot.flow.repository.FlowBindDataRepository;
import com.codingapi.springboot.flow.repository.FlowRecordRepository;
import com.codingapi.springboot.flow.service.FlowRecordVerifyService;
import com.codingapi.springboot.flow.service.FlowServiceRepositoryHolder;
import com.codingapi.springboot.flow.user.IFlowOperator;
import com.codingapi.springboot.framework.event.EventPusher;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
public class FlowStopService {


    private final IFlowOperator currentOperator;
    private final FlowRecordVerifyService flowRecordVerifyService;
    private final FlowRecordRepository flowRecordRepository;
    private final FlowBindDataRepository flowBindDataRepository ;

    private FlowRecord flowRecord;
    private FlowWork flowWork;
    private FlowNode flowNode;
    private BindDataSnapshot snapshot;



    public FlowStopService(long recordId,
                           IFlowOperator currentOperator,
                           FlowServiceRepositoryHolder flowServiceRepositoryHolder) {
        this.currentOperator = currentOperator;
        this.flowRecordRepository = flowServiceRepositoryHolder.getFlowRecordRepository();
        this.flowBindDataRepository = flowServiceRepositoryHolder.getFlowBindDataRepository();
        this.flowRecordVerifyService = new FlowRecordVerifyService(
                flowServiceRepositoryHolder.getFlowWorkRepository(),
                flowServiceRepositoryHolder.getFlowRecordRepository(),
                flowServiceRepositoryHolder.getFlowProcessRepository(),
                recordId,
                currentOperator);
    }


    // 加载流程
    private void loadFlow() {
        // 验证流程的提交状态
        flowRecordVerifyService.verifyFlowRecordSubmitState();
        // 验证当前操作者
        flowRecordVerifyService.verifyFlowRecordCurrentOperator();

        // 加载流程设计
        flowRecordVerifyService.loadFlowWork();
        // 加载流程节点
        flowRecordVerifyService.loadFlowNode();
        // 验证没有子流程
        flowRecordVerifyService.verifyChildrenRecordsIsEmpty();

        this.flowRecord = flowRecordVerifyService.getFlowRecord();
        this.flowNode = flowRecordVerifyService.getFlowNode();
        this.flowWork = flowRecordVerifyService.getFlowWork();
        this.snapshot = flowBindDataRepository.getBindDataSnapshotById(flowRecord.getSnapshotId());
    }



    /**
     * 提交流程
     **/
    public void stop() {
        // 加载流程信息
        this.loadFlow();

        // 停止流程
        flowRecord.stop();
        flowRecordRepository.update(flowRecord);

        List<FlowRecord> todoRecords = flowRecordRepository.findFlowRecordByProcessId(flowRecord.getProcessId());
        for (FlowRecord record : todoRecords) {
            if (record.isTodo()) {
                record.stop();
                flowRecordRepository.update(record);
            }
        }

        flowRecordRepository.finishFlowRecordByProcessId(flowRecord.getProcessId());

        EventPusher.push(new FlowApprovalEvent(FlowApprovalEvent.STATE_STOP,
                flowRecord,
                flowRecord.getCurrentOperator(),
                flowWork,
                snapshot.toBindData()
        ), true);

    }


}
