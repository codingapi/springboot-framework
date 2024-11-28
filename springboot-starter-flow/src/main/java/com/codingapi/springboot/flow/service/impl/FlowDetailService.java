package com.codingapi.springboot.flow.service.impl;

import com.codingapi.springboot.flow.bind.BindDataSnapshot;
import com.codingapi.springboot.flow.content.FlowSession;
import com.codingapi.springboot.flow.domain.FlowNode;
import com.codingapi.springboot.flow.domain.FlowWork;
import com.codingapi.springboot.flow.pojo.FlowDetail;
import com.codingapi.springboot.flow.record.FlowRecord;
import com.codingapi.springboot.flow.repository.*;
import com.codingapi.springboot.flow.service.FlowRecordVerifyService;
import com.codingapi.springboot.flow.user.IFlowOperator;
import lombok.AllArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Transactional
@AllArgsConstructor
public class FlowDetailService {

    private final FlowWorkRepository flowWorkRepository;
    private final FlowRecordRepository flowRecordRepository;
    private final FlowBindDataRepository flowBindDataRepository;
    private final FlowOperatorRepository flowOperatorRepository;
    private final FlowProcessRepository flowProcessRepository;


    /**
     * 流程详情
     * 如果传递了currentOperator为流程的审批者时，在查看详情的时候可以将流程记录标记为已读
     *
     * @param recordId        流程记录id
     * @param currentOperator 当前操作者
     */
    public FlowDetail detail(long recordId, IFlowOperator currentOperator) {

        FlowRecordVerifyService flowRecordVerifyService = new FlowRecordVerifyService(flowRecordRepository,
                flowProcessRepository,
                recordId, currentOperator);

        flowRecordVerifyService.loadFlowRecord();
        flowRecordVerifyService.setFlowRecordRead();
        flowRecordVerifyService.loadFlowWork();

        FlowRecord flowRecord = flowRecordVerifyService.getFlowRecord();
        FlowWork flowWork = flowRecordVerifyService.getFlowWork();


        BindDataSnapshot snapshot = flowBindDataRepository.getBindDataSnapshotById(flowRecord.getSnapshotId());
        List<FlowRecord> flowRecords =
                flowRecordRepository.findFlowRecordByProcessId(flowRecord.getProcessId()).
                        stream().
                        sorted((o1, o2) -> (int) (o2.getId() - o1.getId()))
                        .toList();

        List<IFlowOperator> operators = new ArrayList<>();
        // 获取所有的操作者
        for (FlowRecord record : flowRecords) {
            operators.add(record.getCreateOperator());
            operators.add(record.getCurrentOperator());
            if (record.getInterferedOperator() != null) {
                operators.add(record.getInterferedOperator());
            }
        }

        return new FlowDetail(flowRecord, snapshot, flowWork, flowRecords, operators, currentOperator != null && flowRecord.isTodo() && flowRecord.isOperator(currentOperator));
    }


    /**
     * 发起流程详情
     * 如果传递了currentOperator为流程的审批者时，在查看详情的时候可以将流程记录标记为已读
     *
     * @param workCode        流程记录id
     * @param currentOperator 当前操作者
     */
    public FlowDetail detail(String workCode, IFlowOperator currentOperator) {

        if (currentOperator == null) {
            throw new IllegalArgumentException("current operator is null");
        }

        FlowWork flowWork = flowWorkRepository.getFlowWorkByCode(workCode);
        if (flowWork == null) {
            throw new IllegalArgumentException("flow work not found");
        }
        flowWork.enableValidate();

        // 获取开始节点
        FlowNode flowNode = flowWork.getStartNode();

        FlowSession flowSession = new FlowSession(
                null,
                flowWork,
                flowNode,
                currentOperator,
                currentOperator,
                null,
                null,
                new ArrayList<>());

        List<? extends IFlowOperator> operators = flowNode.loadFlowNodeOperator(flowSession, flowOperatorRepository);

        List<Long> operatorIds = operators.stream().map(IFlowOperator::getUserId).toList();
        if (!operatorIds.contains(currentOperator.getUserId())) {
            throw new IllegalArgumentException("current operator is not flow operator");
        }

        return new FlowDetail(flowWork, flowNode, operators,true);
    }

}
