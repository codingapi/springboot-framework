package com.codingapi.springboot.flow.service.impl;

import com.codingapi.springboot.flow.bind.BindDataSnapshot;
import com.codingapi.springboot.flow.bind.IBindData;
import com.codingapi.springboot.flow.domain.FlowNode;
import com.codingapi.springboot.flow.domain.FlowWork;
import com.codingapi.springboot.flow.domain.Opinion;
import com.codingapi.springboot.flow.pojo.FlowStepResult;
import com.codingapi.springboot.flow.record.FlowRecord;
import com.codingapi.springboot.flow.repository.FlowOperatorRepository;
import com.codingapi.springboot.flow.repository.FlowRecordRepository;
import com.codingapi.springboot.flow.service.FlowNodeService;
import com.codingapi.springboot.flow.service.FlowServiceRepositoryHolder;
import com.codingapi.springboot.flow.user.IFlowOperator;

import java.util.ArrayList;
import java.util.List;

public class FlowStepService {
    private final FlowWork flowWork;

    private final IFlowOperator currentOperator;
    private final IBindData bindData;
    private final FlowServiceRepositoryHolder flowServiceRepositoryHolder;

    private FlowNodeService flowNodeService;
    private FlowNode flowNode;

    public FlowStepService(String workCode, IFlowOperator currentOperator, IBindData bindData, FlowServiceRepositoryHolder flowServiceRepositoryHolder) {
        this.currentOperator = currentOperator;
        this.bindData = bindData;
        this.flowServiceRepositoryHolder = flowServiceRepositoryHolder;
        this.flowWork = flowServiceRepositoryHolder.getFlowWorkRepository().getFlowWorkByCode(workCode);
    }


    public FlowStepResult getFlowStep() {
        FlowStepResult flowStepResult = new FlowStepResult();
        // 获取开始节点
        FlowNode start = flowWork.getStartNode();
        if (start == null) {
            throw new IllegalArgumentException("start node not found");
        }

        this.flowNode = start;
        // 设置开始流程的上一个流程id
        long preId = 0;

        // 创建流程id
        String processId = "flow_" + System.currentTimeMillis();

        List<FlowRecord> historyRecords = new ArrayList<>();

        FlowOperatorRepository flowOperatorRepository = flowServiceRepositoryHolder.getFlowOperatorRepository();
        FlowRecordRepository flowRecordRepository = flowServiceRepositoryHolder.getFlowRecordRepository();

        BindDataSnapshot snapshot = new BindDataSnapshot(bindData);
        flowNodeService = new FlowNodeService(flowOperatorRepository,
                flowRecordRepository,
                snapshot,
                Opinion.pass("同意"),
                currentOperator,
                currentOperator,
                historyRecords,
                flowWork,
                null,
                processId,
                preId);

        flowNodeService.setNextNode(start);

        this.flowNode = start;
        flowStepResult.addFlowNode(this.flowNode);

        do {
            flowNodeService.loadNextPassNode(this.flowNode);
            this.flowNode = flowNodeService.getNextNode();

            flowStepResult.addFlowNode(this.flowNode);
        } while (!flowNode.isOverNode());

        return flowStepResult;
    }
}
