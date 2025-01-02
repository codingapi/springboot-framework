package com.codingapi.springboot.flow.service;

import com.codingapi.springboot.flow.repository.*;
import lombok.Getter;

@Getter
public class FlowServiceRepositoryHolder {

    private final FlowWorkRepository flowWorkRepository;
    private final FlowRecordRepository flowRecordRepository;
    private final FlowBindDataRepository flowBindDataRepository;
    private final FlowOperatorRepository flowOperatorRepository;
    private final FlowProcessRepository flowProcessRepository;
    private final FlowBackupRepository flowBackupRepository;

    public FlowServiceRepositoryHolder(FlowWorkRepository flowWorkRepository,
                                       FlowRecordRepository flowRecordRepository,
                                       FlowBindDataRepository flowBindDataRepository,
                                       FlowOperatorRepository flowOperatorRepository,
                                       FlowProcessRepository flowProcessRepository,
                                       FlowBackupRepository flowBackupRepository){
        this.flowWorkRepository = flowWorkRepository;
        this.flowRecordRepository = flowRecordRepository;
        this.flowBindDataRepository = flowBindDataRepository;
        this.flowOperatorRepository = flowOperatorRepository;
        this.flowProcessRepository = flowProcessRepository;
        this.flowBackupRepository = flowBackupRepository;
    }
}
