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
import com.codingapi.springboot.flow.repository.FlowBackupRepository;
import com.codingapi.springboot.flow.repository.FlowOperatorRepository;
import com.codingapi.springboot.flow.repository.FlowRecordRepository;
import com.codingapi.springboot.flow.repository.FlowWorkRepository;
import com.codingapi.springboot.flow.service.FlowNodeService;
import com.codingapi.springboot.flow.service.FlowServiceRepositoryHolder;
import com.codingapi.springboot.flow.user.IFlowOperator;
import com.codingapi.springboot.framework.event.EventPusher;
import lombok.Getter;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Transactional
public class FlowStartService {

    private final String workCode;
    private final IFlowOperator operator;
    private final IBindData bindData;
    private final Opinion opinion;
    private final FlowServiceRepositoryHolder flowServiceRepositoryHolder;


    @Getter
    private FlowWork flowWork;
    private FlowNode flowNode;
    private FlowBackup flowBackup;
    private FlowProcess flowProcess;
    private BindDataSnapshot snapshot;
    private FlowNodeService flowNodeService;

    public FlowStartService(String workCode,
                            IFlowOperator operator,
                            IBindData bindData,
                            String advice,
                            FlowServiceRepositoryHolder flowServiceRepositoryHolder) {
        this.workCode = workCode;
        this.operator = operator;
        this.bindData = bindData;
        this.opinion = Opinion.pass(advice);
        this.flowServiceRepositoryHolder = flowServiceRepositoryHolder;
    }

    private void loadFlowWork() {
        // 检测流程是否存在
        FlowWorkRepository flowWorkRepository = flowServiceRepositoryHolder.getFlowWorkRepository();
        this.flowWork = flowWorkRepository.getFlowWorkByCode(workCode);
        if (flowWork == null) {
            throw new IllegalArgumentException("flow work not found");
        }
        flowWork.verify();
        flowWork.enableValidate();
    }

    private void loadFlowBackup() {
        FlowBackupRepository flowBackupRepository = flowServiceRepositoryHolder.getFlowBackupRepository();
        this.flowBackup = flowBackupRepository.getFlowBackupByWorkIdAndVersion(flowWork.getId(), flowWork.getUpdateTime());
        if (flowBackup == null) {
            flowBackup = flowBackupRepository.backup(flowWork);
        }
    }

    private void saveFlowProcess() {
        this.flowProcess = new FlowProcess(flowBackup.getId(), operator);
        flowServiceRepositoryHolder.getFlowProcessRepository().save(flowProcess);
    }

    private void saveBindDataSnapshot() {
        snapshot = new BindDataSnapshot(bindData);
        flowServiceRepositoryHolder.getFlowBindDataRepository().save(snapshot);
    }

    private void buildFlowNodeService() {

        // 获取开始节点
        FlowNode start = flowWork.getStartNode();
        if (start == null) {
            throw new IllegalArgumentException("start node not found");
        }

        this.flowNode = start;
        // 设置开始流程的上一个流程id
        long preId = 0;

        // 创建流程id
        String processId = flowProcess.getProcessId();

        List<FlowRecord> historyRecords = new ArrayList<>();

        FlowOperatorRepository flowOperatorRepository = flowServiceRepositoryHolder.getFlowOperatorRepository();
        FlowRecordRepository flowRecordRepository = flowServiceRepositoryHolder.getFlowRecordRepository();

        flowNodeService = new FlowNodeService(flowOperatorRepository,
                flowRecordRepository,
                snapshot,
                opinion,
                operator,
                operator,
                historyRecords,
                flowWork,
                null,
                processId,
                preId);

        flowNodeService.setNextNode(start);
    }


    private void pushEvent(int flowApprovalEventState, FlowRecord flowRecord) {
        EventPusher.push(new FlowApprovalEvent(flowApprovalEventState,
                        flowRecord,
                        flowRecord.getCurrentOperator(),
                        flowWork,
                        snapshot.toBindData()),
                true);
    }


    private void saveFlowRecords(List<FlowRecord> flowRecords) {
        FlowRecordRepository flowRecordRepository = flowServiceRepositoryHolder.getFlowRecordRepository();
        flowRecordRepository.save(flowRecords);
    }


    /**
     * 发起流程 （不自动提交到下一节点）
     */
    public FlowResult startFlow() {
        // 检测流程是否存在
        this.loadFlowWork();

        // 流程数据备份
        this.loadFlowBackup();

        // 保存流程
        this.saveFlowProcess();

        // 保存绑定数据
        this.saveBindDataSnapshot();

        // 构建流程节点服务
        this.buildFlowNodeService();

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

            this.saveFlowRecords(records);

            // 推送事件
            for (FlowRecord record : records) {
                this.pushEvent(FlowApprovalEvent.STATE_CREATE, record);
                this.pushEvent(FlowApprovalEvent.STATE_FINISH, record);
            }
            return new FlowResult(flowWork, records);
        }

        // 保存流程记录
        this.saveFlowRecords(records);

        // 推送事件消息
        for (FlowRecord record : records) {
            this.pushEvent(FlowApprovalEvent.STATE_CREATE, record);
            this.pushEvent(FlowApprovalEvent.STATE_TODO, record);
            this.pushEvent(FlowApprovalEvent.STATE_SAVE, record);
        }
        // 当前的审批记录
        return new FlowResult(flowWork, records);
    }


    public FlowRecord tryStartFlow() {
        // 检测流程是否存在
        this.loadFlowWork();
        // 流程数据备份
        this.loadFlowBackup();

        // 保存绑定数据
        snapshot = new BindDataSnapshot(bindData);
        // 保存流程
        flowProcess = new FlowProcess(flowBackup.getId(), operator);

        // 构建流程节点服务
        this.buildFlowNodeService();

        FlowRecord startRecord = null;

        // 创建待办记录
        List<FlowRecord> records = flowNodeService.createRecord();
        if (records.isEmpty()) {
            throw new IllegalArgumentException("flow record not found");
        } else {
            for (FlowRecord record : records) {
                record.updateOpinion(opinion);
                startRecord = record;
            }
        }

        // 检测流程是否结束
        if (flowNodeService.nextNodeIsOver()) {
            for (FlowRecord record : records) {
                record.submitRecord(operator, snapshot, opinion, FlowSourceDirection.PASS);
                record.finish();
                startRecord = record;
            }
        }
        return startRecord;
    }
}
