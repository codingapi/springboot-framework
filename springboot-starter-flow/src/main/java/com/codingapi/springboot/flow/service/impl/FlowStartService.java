package com.codingapi.springboot.flow.service.impl;

import com.codingapi.springboot.flow.bind.BindDataSnapshot;
import com.codingapi.springboot.flow.bind.IBindData;
import com.codingapi.springboot.flow.domain.FlowNode;
import com.codingapi.springboot.flow.domain.FlowWork;
import com.codingapi.springboot.flow.domain.Opinion;
import com.codingapi.springboot.flow.em.FlowSourceDirection;
import com.codingapi.springboot.flow.event.FlowApprovalEvent;
import com.codingapi.springboot.flow.pojo.FlowResult;
import com.codingapi.springboot.flow.record.FlowBackup;
import com.codingapi.springboot.flow.record.FlowProcess;
import com.codingapi.springboot.flow.record.FlowRecord;
import com.codingapi.springboot.flow.repository.*;
import com.codingapi.springboot.flow.service.FlowNodeService;
import com.codingapi.springboot.flow.user.IFlowOperator;
import com.codingapi.springboot.framework.event.EventPusher;
import lombok.AllArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Transactional
@AllArgsConstructor
public class FlowStartService {

    private final FlowWorkRepository flowWorkRepository;
    private final FlowRecordRepository flowRecordRepository;
    private final FlowBindDataRepository flowBindDataRepository;
    private final FlowOperatorRepository flowOperatorRepository;
    private final FlowProcessRepository flowProcessRepository;
    private final FlowBackupRepository flowBackupRepository;


    /**
     * 发起流程 （不自动提交到下一节点）
     *
     * @param workCode 流程编码
     * @param operator 操作者
     * @param bindData 绑定数据
     * @param advice   审批意见
     */
    public FlowResult startFlow(String workCode, IFlowOperator operator, IBindData bindData, String advice) {
        // 检测流程是否存在
        FlowWork flowWork = flowWorkRepository.getFlowWorkByCode(workCode);
        if (flowWork == null) {
            throw new IllegalArgumentException("flow work not found");
        }
        flowWork.verify();
        flowWork.enableValidate();

        // 流程数据备份
        FlowBackup flowBackup = flowBackupRepository.getFlowBackupByWorkIdAndVersion(flowWork.getId(), flowWork.getUpdateTime());
        if (flowBackup == null) {
            flowBackup = flowBackupRepository.backup(flowWork);
        }

        // 保存流程
        FlowProcess flowProcess = new FlowProcess(flowBackup.getId(), operator);
        flowProcessRepository.save(flowProcess);

        // 保存绑定数据
        BindDataSnapshot snapshot = new BindDataSnapshot(bindData);
        flowBindDataRepository.save(snapshot);

        // 创建流程id
        String processId = flowProcess.getProcessId();

        // 构建审批意见
        Opinion opinion = Opinion.pass(advice);

        // 获取开始节点
        FlowNode start = flowWork.getStartNode();
        if (start == null) {
            throw new IllegalArgumentException("start node not found");
        }
        // 设置开始流程的上一个流程id
        long preId = 0;

        List<FlowRecord> historyRecords = new ArrayList<>();

        FlowNodeService flowNodeService = new FlowNodeService(flowOperatorRepository,
                flowRecordRepository,
                snapshot,
                opinion,
                operator,
                operator,
                historyRecords,
                flowWork,
                processId,
                preId);

        flowNodeService.setNextNode(start);

        // 创建待办记录
        List<FlowRecord> records = flowNodeService.createRecord();
        if (records.isEmpty()) {
            throw new IllegalArgumentException("flow record not found");
        } else {
            for (FlowRecord record : records) {
                record.updateOpinion(opinion);
            }
        }

        // 检测流程是否结束
        if (flowNodeService.nextNodeIsOver()) {
            for (FlowRecord record : records) {
                record.submitRecord(operator, snapshot, opinion, FlowSourceDirection.PASS);
                record.finish();
            }

            flowRecordRepository.save(records);

            // 推送事件
            for (FlowRecord record : records) {
                EventPusher.push(new FlowApprovalEvent(FlowApprovalEvent.STATE_CREATE, record, operator, flowWork, snapshot.toBindData()), true);

                EventPusher.push(new FlowApprovalEvent(FlowApprovalEvent.STATE_FINISH,
                                record,
                                operator,
                                flowWork,
                                snapshot.toBindData()),
                        true);
            }
            return new FlowResult(flowWork, records);
        }

        // 保存流程记录
        flowRecordRepository.save(records);

        // 推送事件消息
        for (FlowRecord record : records) {
            EventPusher.push(new FlowApprovalEvent(FlowApprovalEvent.STATE_CREATE, record, operator, flowWork, snapshot.toBindData()), true);
            EventPusher.push(new FlowApprovalEvent(FlowApprovalEvent.STATE_TODO, record, operator, flowWork, snapshot.toBindData()), true);
        }
        // 当前的审批记录
        return new FlowResult(flowWork, records);
    }

}
