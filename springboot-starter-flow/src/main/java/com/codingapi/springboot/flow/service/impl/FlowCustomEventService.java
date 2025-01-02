package com.codingapi.springboot.flow.service.impl;

import com.codingapi.springboot.flow.bind.IBindData;
import com.codingapi.springboot.flow.domain.FlowButton;
import com.codingapi.springboot.flow.domain.FlowNode;
import com.codingapi.springboot.flow.domain.FlowWork;
import com.codingapi.springboot.flow.domain.Opinion;
import com.codingapi.springboot.flow.record.FlowRecord;
import com.codingapi.springboot.flow.repository.FlowProcessRepository;
import com.codingapi.springboot.flow.repository.FlowRecordRepository;
import com.codingapi.springboot.flow.repository.FlowWorkRepository;
import com.codingapi.springboot.flow.result.MessageResult;
import com.codingapi.springboot.flow.service.FlowRecordVerifyService;
import com.codingapi.springboot.flow.user.IFlowOperator;
import lombok.AllArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Transactional
@AllArgsConstructor
public class FlowCustomEventService {

    private final FlowWorkRepository flowWorkRepository;
    private final FlowRecordRepository flowRecordRepository;
    private final FlowProcessRepository flowProcessRepository;

    /**
     * 自定义事件
     *
     * @param recordId        流程记录id
     * @param currentOperator 当前操作者
     * @param buttonId        按钮id
     * @param bindData        绑定数据
     * @param opinion         审批意见
     */
    public MessageResult customFlowEvent(long recordId, IFlowOperator currentOperator, String buttonId, IBindData bindData, Opinion opinion) {
        FlowRecordVerifyService flowRecordVerifyService = new FlowRecordVerifyService(flowWorkRepository,flowRecordRepository, flowProcessRepository, recordId, currentOperator);

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

        // 获取流程记录对象
        FlowRecord flowRecord = flowRecordVerifyService.getFlowRecord();
        FlowNode flowNode = flowRecordVerifyService.getFlowNode();
        FlowWork flowWork = flowRecordVerifyService.getFlowWork();

        // 与当前流程同级的流程记录
        List<FlowRecord> historyRecords;
        if (flowRecord.isStartRecord()) {
            historyRecords = new ArrayList<>();
        } else {
            historyRecords = flowRecordRepository.findFlowRecordByPreId(flowRecord.getPreId());
        }

        // 获取流程的发起者
        IFlowOperator createOperator = flowRecord.getCreateOperator();
        FlowButton flowButton = flowNode.getButton(buttonId);
        if (flowButton == null) {
            throw new IllegalArgumentException("flow button not found");
        }
        if (!flowButton.hasGroovy()) {
            throw new IllegalArgumentException("flow button not groovy");
        }
        return flowButton.run(flowRecord, flowNode, flowWork, createOperator, currentOperator, bindData, opinion, historyRecords);
    }
}
