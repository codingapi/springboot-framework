package com.codingapi.example.jpa;

import com.codingapi.example.entity.FlowRecordEntity;
import com.codingapi.springboot.fast.jpa.repository.FastRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.List;

public interface FlowRecordEntityRepository extends FastRepository<FlowRecordEntity,Long> {


    FlowRecordEntity getFlowRecordEntityById(long id);


    List<FlowRecordEntity> findFlowRecordEntityByPreId(long preId);

    List<FlowRecordEntity> findFlowRecordEntityByProcessId(String processId);


    @Query(value = "select r from FlowRecordEntity  r where r.currentOperatorId = ?1 and r.recodeType = 'TODO' and r.flowStatus = 'RUNNING'")
    Page<FlowRecordEntity> findTodoByOperatorId(long operatorId,PageRequest pageRequest);

    @Query(value = "select r from FlowRecordEntity  r where r.currentOperatorId = ?1 and r.recodeType = 'DONE'")
    Page<FlowRecordEntity> findDoneByOperatorId(long operatorId, PageRequest pageRequest);

    @Query(value = "select r from FlowRecordEntity  r where r.currentOperatorId = ?1 and r.preId = 0 and r.nodeCode = 'start'")
    Page<FlowRecordEntity> findInitiatedByOperatorId(long operatorId, PageRequest pageRequest);

    @Query(value = "select r from FlowRecordEntity  r where r.currentOperatorId = ?1 and r.recodeType = 'TODO' and r.flowStatus = 'RUNNING' and r.timeoutTime >0 and r.timeoutTime < ?2")
    Page<FlowRecordEntity> findTimeoutTodoByOperatorId(long operatorId,long currentTime, PageRequest pageRequest);

    @Query(value = "select r from FlowRecordEntity  r where r.currentOperatorId = ?1 and r.recodeType = 'TODO' and r.flowStatus = 'RUNNING' and r.postponedCount > 0")
    Page<FlowRecordEntity> findPostponedTodoByOperatorId(long operatorId, PageRequest pageRequest);

    @Query(value = "select r from FlowRecordEntity  r where r.currentOperatorId = ?1 and r.recodeType = 'TODO' and r.flowStatus = 'RUNNING'")
    List<FlowRecordEntity> findTodoFlowRecordByProcessId(String processId);
}
