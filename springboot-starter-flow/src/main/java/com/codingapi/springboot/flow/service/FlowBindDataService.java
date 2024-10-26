package com.codingapi.springboot.flow.service;

import com.codingapi.springboot.flow.bind.BindDataSnapshot;
import com.codingapi.springboot.flow.bind.IBindData;
import com.codingapi.springboot.flow.domain.FlowNode;
import com.codingapi.springboot.flow.record.FlowRecord;
import com.codingapi.springboot.flow.repository.FlowBindDataRepository;

class FlowBindDataService {

    private final FlowBindDataRepository flowBindDataRepository;
    private final IBindData bindData;
    private final FlowNode flowNode;


    public FlowBindDataService(FlowBindDataRepository flowBindDataRepository, FlowNode flowNode, IBindData bindData) {
        this.flowBindDataRepository = flowBindDataRepository;
        this.bindData = bindData;
        this.flowNode = flowNode;
    }

    /**
     * 加载快照数据
     */
    public BindDataSnapshot loadOrCreateSnapshot(FlowRecord flowRecord) {
        // 保存绑定数据
        if (flowNode.isEditable()) {
            BindDataSnapshot snapshot = new BindDataSnapshot(bindData);
            flowBindDataRepository.save(snapshot);
            return snapshot;
        } else {
            return flowBindDataRepository.getBindDataSnapshotById(flowRecord.getSnapshotId());
        }
    }
}
