package com.codingapi.example.infra.flow.query;

import com.codingapi.example.infra.flow.convert.FlowRecordConvertor;
import com.codingapi.example.infra.flow.entity.FlowRecordEntity;
import com.codingapi.example.infra.flow.jpa.FlowRecordEntityRepository;
import com.codingapi.springboot.flow.query.FlowRecordQuery;
import com.codingapi.springboot.flow.record.FlowRecord;
import com.codingapi.springboot.flow.repository.FlowOperatorRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

@Repository
@AllArgsConstructor
public class FlowRecordQueryImpl implements FlowRecordQuery {

    private final FlowRecordEntityRepository flowRecordEntityRepository;
    private final FlowOperatorRepository flowOperatorRepository;


    @Override
    public FlowRecord getFlowRecordById(long id) {
        return FlowRecordConvertor.convert(flowRecordEntityRepository.getFlowRecordEntityById(id),flowOperatorRepository);
    }

    @Override
    public Page<FlowRecord> findAll(PageRequest pageRequest) {
        Page<FlowRecordEntity> page = flowRecordEntityRepository.findAllFlowRecords(pageRequest);
        return page.map(item->FlowRecordConvertor.convert(item,flowOperatorRepository));
    }

    @Override
    public Page<FlowRecord> findTodoByOperatorId(long operatorId, PageRequest pageRequest) {
        Page<FlowRecordEntity> page = flowRecordEntityRepository.findTodoByOperatorId(operatorId,pageRequest);
        return page.map(item->FlowRecordConvertor.convert(item,flowOperatorRepository));
    }


    @Override
    public Page<FlowRecord> findTodoByOperatorId(long operatorId, String workCode, PageRequest pageRequest) {
        Page<FlowRecordEntity> page = flowRecordEntityRepository.findTodoByOperatorIdAndWorkCode(operatorId,workCode,pageRequest);
        return page.map(item->FlowRecordConvertor.convert(item,flowOperatorRepository));
    }

    @Override
    public Page<FlowRecord> findUnReadByOperatorId(long operatorId, PageRequest pageRequest) {
        Page<FlowRecordEntity> page = flowRecordEntityRepository.findUnReadByOperatorId(operatorId,pageRequest);
        return page.map(item->FlowRecordConvertor.convert(item,flowOperatorRepository));
    }

    @Override
    public Page<FlowRecord> findUnReadByOperatorId(long operatorId, String workCode, PageRequest pageRequest) {
        Page<FlowRecordEntity> page = flowRecordEntityRepository.findUnReadByOperatorIdAndWorkCode(operatorId,workCode,pageRequest);
        return page.map(item->FlowRecordConvertor.convert(item,flowOperatorRepository));
    }


    @Override
    public Page<FlowRecord> findDoneByOperatorId(long operatorId, PageRequest pageRequest) {
        Page<FlowRecordEntity> page = flowRecordEntityRepository.findDoneByOperatorId(operatorId,pageRequest);
        return page.map(item->FlowRecordConvertor.convert(item,flowOperatorRepository));
    }

    @Override
    public Page<FlowRecord> findDoneByOperatorId(long operatorId, String workCode, PageRequest pageRequest) {
        Page<FlowRecordEntity> page = flowRecordEntityRepository.findDoneByOperatorIdAndworkCode(operatorId,workCode,pageRequest);
        return page.map(item->FlowRecordConvertor.convert(item,flowOperatorRepository));
    }

    @Override
    public Page<FlowRecord> findInitiatedByOperatorId(long operatorId, PageRequest pageRequest) {
        Page<FlowRecordEntity> page = flowRecordEntityRepository.findInitiatedByOperatorId(operatorId,pageRequest);
        return page.map(item->FlowRecordConvertor.convert(item,flowOperatorRepository));
    }

    @Override
    public Page<FlowRecord> findInitiatedByOperatorId(long operatorId, String workCode, PageRequest pageRequest) {
        Page<FlowRecordEntity> page = flowRecordEntityRepository.findInitiatedByOperatorIdAndWorkCode(operatorId,workCode,pageRequest);
        return page.map(item->FlowRecordConvertor.convert(item,flowOperatorRepository));
    }

    @Override
    public Page<FlowRecord> findTimeoutTodoByOperatorId(long operatorId, PageRequest pageRequest) {
        Page<FlowRecordEntity> page = flowRecordEntityRepository.findTimeoutTodoByOperatorId(operatorId,System.currentTimeMillis(), pageRequest);
        return page.map(item->FlowRecordConvertor.convert(item,flowOperatorRepository));
    }


    @Override
    public Page<FlowRecord> findTimeoutTodoByOperatorId(long operatorId, String workCode, PageRequest pageRequest) {
        Page<FlowRecordEntity> page = flowRecordEntityRepository.findTimeoutTodoByOperatorIdAndWorkCode(operatorId,workCode,System.currentTimeMillis(), pageRequest);
        return page.map(item->FlowRecordConvertor.convert(item,flowOperatorRepository));
    }

    @Override
    public Page<FlowRecord> findPostponedTodoByOperatorId(long operatorId, PageRequest pageRequest) {
        Page<FlowRecordEntity> page = flowRecordEntityRepository.findPostponedTodoByOperatorId(operatorId,pageRequest);
        return page.map(item->FlowRecordConvertor.convert(item,flowOperatorRepository));
    }


    @Override
    public Page<FlowRecord> findPostponedTodoByOperatorId(long operatorId, String workCode, PageRequest pageRequest) {
        Page<FlowRecordEntity> page = flowRecordEntityRepository.findPostponedTodoByOperatorIdAndWorkCode(operatorId,workCode,pageRequest);
        return page.map(item->FlowRecordConvertor.convert(item,flowOperatorRepository));
    }
}
