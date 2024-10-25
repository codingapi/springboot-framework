package com.codingapi.springboot.flow.pojo;

import com.codingapi.springboot.flow.bind.BindDataSnapshot;
import com.codingapi.springboot.flow.bind.IBindData;
import com.codingapi.springboot.flow.domain.FlowNode;
import com.codingapi.springboot.flow.domain.FlowWork;
import com.codingapi.springboot.flow.record.FlowRecord;
import com.codingapi.springboot.flow.user.IFlowOperator;
import lombok.Getter;

import java.util.List;


@Getter
public class FlowDetail {

    private final FlowRecord flowRecord;
    private final FlowWork flowWork;
    private final FlowNode flowNode;
    private final List<FlowRecord> historyRecords;
    private final BindDataSnapshot snapshot;
    private final IBindData bindData;
    private final List<? extends IFlowOperator> operators;

    public FlowDetail(FlowRecord flowRecord,
                      BindDataSnapshot snapshot,
                      FlowWork flowWork,
                      List<FlowRecord> historyRecords,
                        List<? extends IFlowOperator> operators) {
        this.operators = operators;
        this.flowRecord = flowRecord;
        this.snapshot = snapshot;
        this.flowWork = flowWork;
        this.bindData = snapshot.toBindData();
        this.historyRecords = historyRecords;
        this.flowNode = flowWork.getNodeByCode(flowRecord.getNodeCode());
    }
}
