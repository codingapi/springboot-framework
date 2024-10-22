package com.codingapi.springboot.flow.service;

import com.codingapi.springboot.flow.bind.BindDataSnapshot;
import com.codingapi.springboot.flow.bind.IBindData;
import com.codingapi.springboot.flow.domain.FlowWork;
import com.codingapi.springboot.flow.domain.Opinion;
import com.codingapi.springboot.flow.record.FlowRecord;
import com.codingapi.springboot.flow.repository.FlowBindDataRepository;
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

    /**
     * 发起流程
     *
     * @param workId   流程id
     * @param operator 操作者
     * @param bindData 绑定数据
     * @param opinion  意见
     */
    public void startFlow(long workId, IFlowOperator operator, IBindData bindData, String opinion) {
        FlowWork flowWork = flowWorkRepository.getFlowWorkById(workId);
        if (flowWork == null) {
            throw new IllegalArgumentException("flow work not found");
        }
        BindDataSnapshot snapshot = flowBindDataRepository.save(bindData);
        List<FlowRecord> records = flowWork.startFlow(operator, snapshot, Opinion.success(opinion));
        flowRecordRepository.save(records);
    }

}
