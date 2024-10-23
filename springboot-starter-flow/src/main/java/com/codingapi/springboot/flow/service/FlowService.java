package com.codingapi.springboot.flow.service;

import com.codingapi.springboot.flow.bind.BindDataSnapshot;
import com.codingapi.springboot.flow.bind.IBindData;
import com.codingapi.springboot.flow.domain.FlowNode;
import com.codingapi.springboot.flow.domain.FlowWork;
import com.codingapi.springboot.flow.domain.Opinion;
import com.codingapi.springboot.flow.record.FlowRecord;
import com.codingapi.springboot.flow.repository.FlowBindDataRepository;
import com.codingapi.springboot.flow.repository.FlowOperatorRepository;
import com.codingapi.springboot.flow.repository.FlowRecordRepository;
import com.codingapi.springboot.flow.repository.FlowWorkRepository;
import com.codingapi.springboot.flow.user.IFlowOperator;
import lombok.AllArgsConstructor;

import java.util.List;

@AllArgsConstructor
public class FlowService {

    private final FlowWorkRepository flowWorkRepository;
    private final FlowRecordRepository flowRecordRepository;
    private final FlowBindDataRepository flowBindDataRepository;
    private final FlowOperatorRepository flowOperatorRepository;

    /**
     * 发起流程
     *
     * @param workId   流程id
     * @param operator 操作者
     * @param bindData 绑定数据
     * @param opinion  意见
     */
    public void startFlow(long workId, IFlowOperator operator, IBindData bindData, String opinion) {
        // 检测流程是否存在
        FlowWork flowWork = flowWorkRepository.getFlowWorkById(workId);
        if (flowWork == null) {
            throw new IllegalArgumentException("flow work not found");
        }
        flowWork.verifyState();
        // 保存绑定数据
        BindDataSnapshot snapshot = new BindDataSnapshot(bindData);
        flowBindDataRepository.save(snapshot);
        // 创建待办记录
        List<FlowRecord> records = flowWork.startFlow(flowOperatorRepository, operator, snapshot, Opinion.success(opinion));
        flowRecordRepository.save(records);

        // 提交流程
        if (!records.isEmpty()) {
            for (FlowRecord record : records) {
                this.submitFlow(record.getId(), operator, bindData, Opinion.success(opinion));
            }
        }
    }


    /**
     * 提交流程
     *
     * @param recordId        流程记录id
     * @param currentOperator 当前操作者
     * @param bindData        绑定数据
     * @param opinion         审批意见
     */
    public void submitFlow(long recordId, IFlowOperator currentOperator, IBindData bindData, Opinion opinion) {
        // 检测流程记录
        FlowRecord flowRecord = flowRecordRepository.getFlowRecordById(recordId);
        if (flowRecord == null) {
            throw new IllegalArgumentException("flow record not found");
        }
        flowRecord.submitStateVerify();


        // 检测流程
        FlowWork flowWork = flowWorkRepository.getFlowWorkById(flowRecord.getWorkId());
        if (flowWork == null) {
            throw new IllegalArgumentException("flow work not found");
        }
        flowWork.verifyState();

        // 检测流程节点
        FlowNode flowNode = flowWork.getNodeByCode(flowRecord.getNodeCode());
        if (flowNode == null) {
            throw new IllegalArgumentException("flow node not found");
        }

        // 是否存在子流程
        List<FlowRecord> childrenRecords = flowRecordRepository.findFlowRecordByPreId(recordId);
        if (flowNode.isUnSign()) {
            // 如果是非会签，则不能存在后续的子流程
            if (!childrenRecords.isEmpty()) {
                throw new IllegalArgumentException("flow node is done");
            }
        }

        // 获取创建者
        IFlowOperator createOperator = flowOperatorRepository.getFlowOperatorById(flowRecord.getCreateOperatorId());

        // 保存绑定数据
        BindDataSnapshot snapshot = new BindDataSnapshot(bindData);
        flowBindDataRepository.save(snapshot);

        // 提交流程
        flowRecord.done(currentOperator, snapshot, opinion);
        flowRecordRepository.update(flowRecord);

        // 会签处理流程
        if (flowNode.isSign()) {
            // 会签流程需要所有人都提交再执行下一节点
            List<FlowRecord> currentFlowRecords =  flowRecordRepository.findFlowRecordByPreId(flowRecord.getPreId());
            // 会签下所有人尚未提交时，不执行下一节点
            boolean allDone = currentFlowRecords.stream().allMatch(FlowRecord::isDone);
            if (!allDone) {
                return;
            }
        }
        // 非会签处理流程
        if(flowNode.isUnSign()){
            List<FlowRecord> currentFlowRecords =  flowRecordRepository.findFlowRecordByPreId(flowRecord.getPreId());
            // 非会签下将所有人都自动提交，然后再执行下一节点
            for(FlowRecord record : currentFlowRecords){
               if(record.getId() != recordId){
                   record.autoDone(currentOperator, snapshot);
                   flowRecordRepository.update(flowRecord);
               }
            }
        }

        // 进入下一节点
        String processId = flowRecord.getProcessId();
        long preId = flowRecord.getId();

        FlowNode nextNode = flowWork.getNextNode(flowNode, createOperator, currentOperator, snapshot, opinion);
        if (nextNode == null) {
            throw new IllegalArgumentException("next node not found");
        }
        List<FlowRecord> records = flowWork.createRecord(flowOperatorRepository, preId, processId, nextNode, createOperator, currentOperator, snapshot, opinion);
        flowRecordRepository.save(records);
    }

}
