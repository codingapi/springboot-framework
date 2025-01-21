package com.codingapi.example.jpa;

import com.codingapi.example.entity.FlowRecordEntity;
import com.codingapi.springboot.fast.jpa.repository.FastRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface FlowRecordEntityRepository extends FastRepository<FlowRecordEntity, Long> {


    FlowRecordEntity getFlowRecordEntityById(long id);

    void deleteByProcessId(String processId);


    List<FlowRecordEntity> findFlowRecordEntityByPreId(long preId);

    List<FlowRecordEntity> findFlowRecordEntityByProcessId(String processId);

    @Query(value = "select r from FlowRecordEntity  r where r.flowType = 'TODO' and r.flowStatus = 'RUNNING' and r.processId = ?1")
    List<FlowRecordEntity> findTodoFlowRecordByProcessId(String processId);

    @Query(value = "select r from FlowRecordEntity  r where r.currentOperatorId = ?1 and r.flowType = 'TODO' and r.flowStatus = 'RUNNING' order by r.id desc")
    Page<FlowRecordEntity> findTodoByOperatorId(long operatorId, PageRequest pageRequest);

    @Query(value = "select r from FlowRecordEntity  r where r.currentOperatorId = ?1  and r.workCode = ?2 and r.flowType = 'TODO' and r.flowStatus = 'RUNNING' order by r.id desc")
    Page<FlowRecordEntity> findTodoByOperatorIdAndWorkCode(long operatorId, String workCode, PageRequest pageRequest);

    @Query(value = "select r from FlowRecordEntity  r where r.currentOperatorId = ?1 and r.read = false and r.flowStatus = 'RUNNING' order by r.id desc")
    Page<FlowRecordEntity> findUnReadByOperatorId(long operatorId, PageRequest pageRequest);

    @Query(value = "select r from FlowRecordEntity  r where r.currentOperatorId = ?1  and r.workCode = ?2 and r.read = false and r.flowStatus = 'RUNNING' order by r.id desc")
    Page<FlowRecordEntity> findUnReadByOperatorIdAndWorkCode(long operatorId, String workCode, PageRequest pageRequest);

    @Query(value = "select d from FlowRecordEntity  d where d.id in (select max(r.id) from FlowRecordEntity  r where r.currentOperatorId = ?1 and r.flowType = 'DONE' group by r.processId ) order by d.id desc")
    Page<FlowRecordEntity> findDoneByOperatorId(long operatorId, PageRequest pageRequest);

    @Query(value = "select d from FlowRecordEntity  d where d.id in (select max(r.id) from FlowRecordEntity  r where r.currentOperatorId = ?1 and r.workCode = ?2 and r.flowType = 'DONE' group by r.processId)  order by d.id desc")
    Page<FlowRecordEntity> findDoneByOperatorIdAndworkCode(long operatorId, String workCode, PageRequest pageRequest);

    @Query(value = "select d from FlowRecordEntity  d where d.id in (select max(r.id) from FlowRecordEntity  r where r.currentOperatorId = ?1 and r.preId = 0 and r.nodeCode = 'start'  group by r.processId) order by d.id desc")
    Page<FlowRecordEntity> findInitiatedByOperatorId(long operatorId, PageRequest pageRequest);

    @Query(value = "select d from FlowRecordEntity  d where d.id in (select max(r.id) from FlowRecordEntity  r where r.currentOperatorId = ?1  and r.workCode = ?2 and r.preId = 0 and r.nodeCode = 'start'  group by r.processId) order by d.id desc")
    Page<FlowRecordEntity> findInitiatedByOperatorIdAndWorkCode(long operatorId, String workCode, PageRequest pageRequest);

    @Query(value = "select r from FlowRecordEntity  r where r.currentOperatorId = ?1 and r.flowType = 'TODO' and r.flowStatus = 'RUNNING' and r.timeoutTime >0 and r.timeoutTime < ?2 order by r.id desc")
    Page<FlowRecordEntity> findTimeoutTodoByOperatorId(long operatorId, long currentTime, PageRequest pageRequest);

    @Query(value = "select r from FlowRecordEntity  r where r.currentOperatorId = ?1 and r.workCode = ?2 and r.flowType = 'TODO' and r.flowStatus = 'RUNNING' and r.timeoutTime >0 and r.timeoutTime < ?3 order by r.id desc")
    Page<FlowRecordEntity> findTimeoutTodoByOperatorIdAndWorkCode(long operatorId, String workCode, long currentTime, PageRequest pageRequest);

    @Query(value = "select r from FlowRecordEntity  r where r.currentOperatorId = ?1 and r.flowType = 'TODO' and r.flowStatus = 'RUNNING' and r.postponedCount > 0 order by r.id desc")
    Page<FlowRecordEntity> findPostponedTodoByOperatorId(long operatorId, PageRequest pageRequest);

    @Query(value = "select r from FlowRecordEntity  r where r.currentOperatorId = ?1 and r.workCode =?2 and r.flowType = 'TODO' and r.flowStatus = 'RUNNING' and r.postponedCount > 0 order by r.id desc")
    Page<FlowRecordEntity> findPostponedTodoByOperatorIdAndWorkCode(long operatorId, String workCode, PageRequest pageRequest);


    @Query(value = "select d from FlowRecordEntity  d where d.id in (select max(r.id) from FlowRecordEntity  r group by r.processId ) order by d.id desc")
    Page<FlowRecordEntity> findAllFlowRecords(PageRequest pageRequest);

}
