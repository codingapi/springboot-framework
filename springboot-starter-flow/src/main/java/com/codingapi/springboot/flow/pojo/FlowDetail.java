package com.codingapi.springboot.flow.pojo;

import com.codingapi.springboot.flow.bind.BindDataSnapshot;
import com.codingapi.springboot.flow.bind.IBindData;
import com.codingapi.springboot.flow.domain.FlowWork;
import com.codingapi.springboot.flow.record.FlowRecord;
import lombok.Getter;

import java.util.List;


@Getter
public class FlowDetail {

    private final FlowRecord flowRecord;
    private final FlowWork flowWork;
    private final List<FlowRecord> historyRecords;
    private final BindDataSnapshot snapshot;
    private final IBindData bindData;

    public FlowDetail(FlowRecord flowRecord,BindDataSnapshot snapshot, FlowWork flowWork, List<FlowRecord> historyRecords) {
        this.flowRecord = flowRecord;
        this.snapshot = snapshot;
        this.flowWork = flowWork;
        this.bindData = snapshot.toBindData();
        this.historyRecords = historyRecords;
    }
}
