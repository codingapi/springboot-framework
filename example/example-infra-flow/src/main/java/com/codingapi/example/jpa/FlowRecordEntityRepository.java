package com.codingapi.example.jpa;

import com.codingapi.example.entity.FlowRecordEntity;
import com.codingapi.springboot.fast.jpa.repository.FastRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface FlowRecordEntityRepository extends FastRepository<FlowRecordEntity,Long> {


    FlowRecordEntity getFlowRecordEntityById(long id);


    List<FlowRecordEntity> findFlowRecordEntityByPreId(long preId);

    List<FlowRecordEntity> findFlowRecordEntityByProcessId(String processId);


    @Query(value = "select r from FlowRecordEntity  r where r.currentOperatorId = ?1 and r.flowType = 'TODO' and r.flowStatus = 'RUNNING' order by r.createTime desc")
    Page<FlowRecordEntity> findTodoByOperatorId(long operatorId,PageRequest pageRequest);

    @Query(value = "select r from FlowRecordEntity  r where r.currentOperatorId = ?1 and r.flowType = 'DONE' order by r.createTime desc")
    Page<FlowRecordEntity> findDoneByOperatorId(long operatorId, PageRequest pageRequest);

    @Query(value = "select r from FlowRecordEntity  r where r.currentOperatorId = ?1 and r.preId = 0 and r.nodeCode = 'start' order by r.createTime desc")
    Page<FlowRecordEntity> findInitiatedByOperatorId(long operatorId, PageRequest pageRequest);

    @Query(value = "select r from FlowRecordEntity  r where r.currentOperatorId = ?1 and r.flowType = 'TODO' and r.flowStatus = 'RUNNING' and r.timeoutTime >0 and r.timeoutTime < ?2 order by r.createTime desc")
    Page<FlowRecordEntity> findTimeoutTodoByOperatorId(long operatorId,long currentTime, PageRequest pageRequest);

    @Query(value = "select r from FlowRecordEntity  r where r.currentOperatorId = ?1 and r.flowType = 'TODO' and r.flowStatus = 'RUNNING' and r.postponedCount > 0 order by r.createTime desc")
    Page<FlowRecordEntity> findPostponedTodoByOperatorId(long operatorId, PageRequest pageRequest);

    @Query(value = "select r from FlowRecordEntity  r where r.currentOperatorId = ?1 and r.flowType = 'TODO' and r.flowStatus = 'RUNNING' order by r.createTime desc")
    List<FlowRecordEntity> findTodoFlowRecordByProcessId(String processId);
}
