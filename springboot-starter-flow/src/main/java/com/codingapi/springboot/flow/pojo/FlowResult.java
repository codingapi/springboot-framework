package com.codingapi.springboot.flow.pojo;

import com.codingapi.springboot.flow.domain.FlowWork;
import com.codingapi.springboot.flow.record.FlowRecord;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class FlowResult {

    private final List<FlowRecord> records;
    private final FlowWork flowWork;

    public FlowResult(FlowWork flowWork, List<FlowRecord> records) {
        this.flowWork = flowWork;
        this.records = records;
    }

    public FlowResult(FlowWork flowWork,FlowRecord flowRecord) {
        this.flowWork = flowWork;
        this.records = new ArrayList<>();
        this.records.add(flowRecord);
    }
}
