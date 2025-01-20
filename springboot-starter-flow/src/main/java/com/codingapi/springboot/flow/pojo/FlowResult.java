package com.codingapi.springboot.flow.pojo;

import com.codingapi.springboot.flow.domain.FlowWork;
import com.codingapi.springboot.flow.record.FlowRecord;
import com.codingapi.springboot.flow.user.IFlowOperator;
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

    /**
     * 匹配操作者的记录
     * @param operator 操作者
     * @return 记录
     */
    public List<FlowRecord> matchRecordByOperator(IFlowOperator operator){
        return records.stream().filter(record -> record.isOperator(operator)).toList();
    }

    public boolean isOver() {
        return records.stream().allMatch(FlowRecord::isOverNode);
    }
}
