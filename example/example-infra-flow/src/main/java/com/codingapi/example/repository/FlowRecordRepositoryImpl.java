package com.codingapi.example.repository;

import com.codingapi.example.convert.FlowRecordConvertor;
import com.codingapi.example.jpa.FlowRecordEntityRepository;
import com.codingapi.springboot.flow.em.FlowStatus;
import com.codingapi.springboot.flow.record.FlowRecord;
import com.codingapi.springboot.flow.repository.FlowRecordRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@AllArgsConstructor
public class FlowRecordRepositoryImpl implements FlowRecordRepository {

    private final FlowRecordEntityRepository flowRecordEntityRepository;

    private final UserRepository userRepository;

    @Override
    public void save(List<FlowRecord> records) {
        flowRecordEntityRepository.saveAll(records.stream().map(item->FlowRecordConvertor.convert(item,userRepository)).toList());
    }

    @Override
    public void update(FlowRecord flowRecord) {
        flowRecordEntityRepository.save(FlowRecordConvertor.convert(flowRecord,userRepository));
    }

    @Override
    public FlowRecord getFlowRecordById(long id) {
        return FlowRecordConvertor.convert(flowRecordEntityRepository.getFlowRecordEntityById(id));
    }

    @Override
    public List<FlowRecord> findFlowRecordByPreId(long preId) {
        return flowRecordEntityRepository.findFlowRecordEntityByPreId(preId).stream().map(FlowRecordConvertor::convert).toList();
    }

    @Override
    public List<FlowRecord> findFlowRecordByProcessId(String processId) {
        return flowRecordEntityRepository.findFlowRecordEntityByProcessId(processId).stream().map(FlowRecordConvertor::convert).toList();
    }

    @Override
    public List<FlowRecord> findTodoFlowRecordByProcessId(String processId) {
        return flowRecordEntityRepository.findTodoFlowRecordByProcessId(processId)
                .stream().map(FlowRecordConvertor::convert).toList();
    }

    @Override
    public void finishFlowRecordByProcessId(String processId) {
        flowRecordEntityRepository.findFlowRecordEntityByProcessId(processId)
                .forEach(flowRecordEntity -> {
                    flowRecordEntity.setFlowStatus(FlowStatus.FINISH.name());
                    flowRecordEntity.setFinishTime(System.currentTimeMillis());
                    flowRecordEntityRepository.save(flowRecordEntity);
                });
    }

    @Override
    public void delete(List<FlowRecord> childrenRecords) {
        flowRecordEntityRepository.deleteAll(childrenRecords.stream().map(item->FlowRecordConvertor.convert(item,userRepository)).toList());
    }
}
