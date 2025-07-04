package com.codingapi.example.infra.flow.repository;

import com.codingapi.example.infra.flow.convert.FlowProcessConvertor;
import com.codingapi.example.infra.flow.jpa.FlowProcessEntityRepository;
import com.codingapi.springboot.flow.domain.FlowWork;
import com.codingapi.springboot.flow.record.FlowBackup;
import com.codingapi.springboot.flow.record.FlowProcess;
import com.codingapi.springboot.flow.repository.FlowBackupRepository;
import com.codingapi.springboot.flow.repository.FlowOperatorRepository;
import com.codingapi.springboot.flow.repository.FlowProcessRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@AllArgsConstructor
public class FlowProcessRepositoryImpl implements FlowProcessRepository {

    private final FlowProcessEntityRepository flowProcessEntityRepository;
    private final FlowBackupRepository flowBackupRepository;
    private final FlowOperatorRepository flowOperatorRepository;

    @Override
    public void save(FlowProcess flowProcess) {
      flowProcessEntityRepository.save( FlowProcessConvertor.convert(flowProcess));
    }

    @Override
    public FlowProcess getFlowProcessByProcessId(String processId) {
        return FlowProcessConvertor.convert(flowProcessEntityRepository.getFlowProcessEntityByProcessId(processId));
    }

    @Override
    public FlowWork getFlowWorkByProcessId(String processId) {
        FlowProcess flowProcess =  FlowProcessConvertor.convert(flowProcessEntityRepository.getFlowProcessEntityByProcessId(processId));
        if(flowProcess==null){
            return null;
        }
        if(flowProcess.isVoided()){
            return null;
        }

        FlowBackup flowBackup = flowBackupRepository.getFlowBackupById(flowProcess.getBackupId());
        if (flowBackup != null) {
            try {
                return flowBackup.resume(flowOperatorRepository);
            }catch (Exception e){
                return null;
            }
        }
        return null;
    }


    @Override
    public void deleteByProcessId(String processId) {
        flowProcessEntityRepository.deleteByProcessId(processId);
    }
}
