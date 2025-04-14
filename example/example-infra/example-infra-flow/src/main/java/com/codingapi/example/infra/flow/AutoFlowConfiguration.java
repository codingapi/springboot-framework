package com.codingapi.example.infra.flow;

import com.codingapi.springboot.flow.repository.*;
import com.codingapi.springboot.flow.service.FlowService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AutoFlowConfiguration {

    @Bean
    public FlowService flowService(FlowWorkRepository flowWorkRepository,
                                   FlowRecordRepository flowRecordRepository,
                                   FlowBindDataRepository flowBindDataRepository,
                                   FlowOperatorRepository flowOperatorRepository,
                                   FlowProcessRepository flowProcessRepository,
                                   FlowBackupRepository flowBackupRepository) {
        return new FlowService(flowWorkRepository, flowRecordRepository, flowBindDataRepository, flowOperatorRepository, flowProcessRepository, flowBackupRepository);
    }
}
