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
import com.codingapi.springboot.framework.utils.RandomGenerator;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class FlowStepService {
    private final FlowWork flowWork;
    private final long recordId;
    private final List<FlowRecord> flowRecords;

    private final IFlowOperator currentOperator;
    private final IBindData bindData;
    private final FlowServiceRepositoryHolder flowServiceRepositoryHolder;

    private FlowNodeService flowNodeService;
    private FlowNode flowNode;
    private FlowRecord currentFlowRecord;

    public FlowStepService(long recordId,String workCode, IFlowOperator currentOperator, IBindData bindData, FlowServiceRepositoryHolder flowServiceRepositoryHolder) {
        this.recordId = recordId;
        this.currentOperator = currentOperator;
        this.bindData = bindData;
        this.flowServiceRepositoryHolder = flowServiceRepositoryHolder;
        if(this.recordId>0) {
            this.currentFlowRecord = flowServiceRepositoryHolder.getFlowRecordRepository().getFlowRecordById(recordId);
            this.flowRecords = flowServiceRepositoryHolder.getFlowRecordRepository().findFlowRecordByProcessId(currentFlowRecord.getProcessId()).stream().sorted(Comparator.comparingLong(FlowRecord::getId)).collect(Collectors.toList());
            this.flowWork = flowServiceRepositoryHolder.getFlowWorkRepository().getFlowWorkByCode(currentFlowRecord.getWorkCode());
        }else {
            this.currentFlowRecord = null;
            this.flowRecords = new ArrayList<>();
            this.flowWork = flowServiceRepositoryHolder.getFlowWorkRepository().getFlowWorkByCode(workCode);
        }
    }


    public FlowStepResult getFlowStep() {
        FlowStepResult flowStepResult = new FlowStepResult();

        // 设置开始流程的上一个流程id
        long preId = 0;
        if(currentFlowRecord==null) {
            // 获取开始节点
            FlowNode start = flowWork.getStartNode();
            if (start == null) {
                throw new IllegalArgumentException("start node not found");
            }
            preId = 0;
            this.flowNode = start;
        }else {
            for(FlowRecord flowRecord : flowRecords) {
                FlowNode flowNode = this.flowWork.getNodeByCode(flowRecord.getNodeCode());
                List<IFlowOperator> operators = new ArrayList<>();
                if(flowRecord.getCurrentOperator()!=null) {
                    operators.add(flowRecord.getCurrentOperator());
                }
                boolean isDone =flowRecord.isDone() || flowRecord.getOpinion().isCirculate();
                flowStepResult.addFlowNode(flowNode,isDone, operators);
            }
            FlowRecord lastRecord = this.flowRecords.get(this.flowRecords.size()-1);
            this.flowNode =  this.flowWork.getNodeByCode(lastRecord.getNodeCode());
            preId = lastRecord.getId();
        }

        // 创建流程id
        String processId = "flow_" + RandomGenerator.generateUUID();

        FlowOperatorRepository flowOperatorRepository = flowServiceRepositoryHolder.getFlowOperatorRepository();
        FlowRecordRepository flowRecordRepository = flowServiceRepositoryHolder.getFlowRecordRepository();
        List<FlowRecord> historyRecords = new ArrayList<>();
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

        flowNodeService.setNextNode(this.flowNode);

        if(currentFlowRecord==null) {
            flowStepResult.addFlowNode(this.flowNode, false, this.flowNodeService.loadNextNodeOperators());
        }

        do {
            flowNodeService.loadNextPassNode(this.flowNode);
            this.flowNode = flowNodeService.getNextNode();

            boolean isFinish = currentFlowRecord != null && currentFlowRecord.isFinish();

            flowStepResult.addFlowNode(this.flowNode,isFinish, this.flowNodeService.loadNextNodeOperators());
        } while (!flowNode.isOverNode());

        return flowStepResult;
    }
}
