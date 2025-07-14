package com.codingapi.example.infra.flow.jpa;

import com.codingapi.example.infra.flow.entity.FlowRecordEntity;
import com.codingapi.springboot.fast.jpa.repository.FastRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface FlowRecordEntityRepository extends FastRepository<FlowRecordEntity, Long> {

    @Query(value = "select r from FlowRecordEntity  r where r.flowType!='DELETE' and r.id = ?1")
    FlowRecordEntity getFlowRecordEntityById(long id);

    void deleteByProcessId(String processId);

    @Query(value = "select r from FlowRecordEntity  r where r.flowType!='DELETE' and r.preId = ?1")
    List<FlowRecordEntity> findFlowRecordEntityByPreId(long preId);

    @Query(value = "select r from FlowRecordEntity  r where r.flowType!='DELETE' and r.processId = ?1")
    List<FlowRecordEntity> findFlowRecordEntityByProcessId(String processId);

    @Query(value = "select r from FlowRecordEntity  r where r.flowType!='DELETE' and r.flowType = 'TODO' and r.flowStatus = 'RUNNING' and r.processId = ?1")
    List<FlowRecordEntity> findTodoFlowRecordByProcessId(String processId);

    @Query(value = "select r from FlowRecordEntity r" +
            " LEFT JOIN (select min(m.id) as id from FlowRecordEntity m where m.currentOperatorId = ?1 and m.flowType = 'TODO' and m.flowStatus = 'RUNNING' and m.mergeable = true ) debup " +
            "on r.id = debup.id" +
            " where r.flowType!='DELETE' and r.currentOperatorId = ?1 and r.flowType = 'TODO' and r.flowStatus = 'RUNNING'" +
            " and (r.mergeable !=true or debup.id is NOT null ) order by r.id desc")
    Page<FlowRecordEntity> findTodoByOperatorId(long operatorId, PageRequest pageRequest);

    @Query(value = "select r from FlowRecordEntity  r where r.flowType!='DELETE'and r.currentOperatorId = ?1 and r.workCode = ?2 and r.nodeCode = ?3" +
            " and r.mergeable = true and r.flowType = 'TODO' and r.flowStatus = 'RUNNING' order by r.id desc")
    List<FlowRecordEntity> findMergeFlowRecordById(long currentOperatorId,String workCode, String nodeCode);

    @Query(value = "select r from FlowRecordEntity  r where r.flowType!='DELETE'and r.currentOperatorId = ?1  and r.workCode = ?2 and r.flowType = 'TODO' and r.flowStatus = 'RUNNING' order by r.id desc")
    Page<FlowRecordEntity> findTodoByOperatorIdAndWorkCode(long operatorId, String workCode, PageRequest pageRequest);

    @Query(value = "select r from FlowRecordEntity  r where r.flowType!='DELETE'and r.currentOperatorId = ?1 and r.read = false and r.flowStatus = 'RUNNING' order by r.id desc")
    Page<FlowRecordEntity> findUnReadByOperatorId(long operatorId, PageRequest pageRequest);

    @Query(value = "select r from FlowRecordEntity  r where r.flowType!='DELETE'and r.currentOperatorId = ?1  and r.workCode = ?2 and r.read = false and r.flowStatus = 'RUNNING' order by r.id desc")
    Page<FlowRecordEntity> findUnReadByOperatorIdAndWorkCode(long operatorId, String workCode, PageRequest pageRequest);

    @Query(value = "select d from FlowRecordEntity  d where d.flowType!='DELETE'and d.id in (select max(r.id) from FlowRecordEntity  r where r.currentOperatorId = ?1 and r.flowType = 'DONE' group by r.processId ) order by d.id desc")
    Page<FlowRecordEntity> findDoneByOperatorId(long operatorId, PageRequest pageRequest);

    @Query(value = "select d from FlowRecordEntity  d where d.flowType!='DELETE'and d.id in (select max(r.id) from FlowRecordEntity  r where r.currentOperatorId = ?1 and r.workCode = ?2 and r.flowType = 'DONE' group by r.processId)  order by d.id desc")
    Page<FlowRecordEntity> findDoneByOperatorIdAndworkCode(long operatorId, String workCode, PageRequest pageRequest);

    @Query(value = "select d from FlowRecordEntity  d where d.flowType!='DELETE'and d.id in (select max(r.id) from FlowRecordEntity  r where r.currentOperatorId = ?1 and r.preId = 0 and r.nodeCode = 'start'  group by r.processId) order by d.id desc")
    Page<FlowRecordEntity> findInitiatedByOperatorId(long operatorId, PageRequest pageRequest);

    @Query(value = "select d from FlowRecordEntity  d where d.flowType!='DELETE' and  d.id in (select max(r.id) from FlowRecordEntity  r where r.currentOperatorId = ?1  group by r.processId) order by d.id desc")
    Page<FlowRecordEntity> findAllByOperatorId(long operatorId, PageRequest pageRequest);

    @Query(value = "select d from FlowRecordEntity  d where d.flowType!='DELETE' and  d.id in (select max(r.id) from FlowRecordEntity  r where r.currentOperatorId = ?1  and r.workCode = ?2 and r.preId = 0 and r.nodeCode = 'start'  group by r.processId) order by d.id desc")
    Page<FlowRecordEntity> findInitiatedByOperatorIdAndWorkCode(long operatorId, String workCode, PageRequest pageRequest);

    @Query(value = "select r from FlowRecordEntity  r where r.flowType!='DELETE' and  r.currentOperatorId = ?1 and r.flowType = 'TODO' and r.flowStatus = 'RUNNING' and r.timeoutTime >0 and r.timeoutTime < ?2 order by r.id desc")
    Page<FlowRecordEntity> findTimeoutTodoByOperatorId(long operatorId, long currentTime, PageRequest pageRequest);

    @Query(value = "select r from FlowRecordEntity  r where r.flowType!='DELETE' and  r.currentOperatorId = ?1 and r.workCode = ?2 and r.flowType = 'TODO' and r.flowStatus = 'RUNNING' and r.timeoutTime >0 and r.timeoutTime < ?3 order by r.id desc")
    Page<FlowRecordEntity> findTimeoutTodoByOperatorIdAndWorkCode(long operatorId, String workCode, long currentTime, PageRequest pageRequest);

    @Query(value = "select r from FlowRecordEntity  r where r.flowType!='DELETE' and r.currentOperatorId = ?1 and r.flowType = 'TODO' and r.flowStatus = 'RUNNING' and r.postponedCount > 0 order by r.id desc")
    Page<FlowRecordEntity> findPostponedTodoByOperatorId(long operatorId, PageRequest pageRequest);

    @Query(value = "select r from FlowRecordEntity  r where r.flowType!='DELETE' and  r.currentOperatorId = ?1 and r.workCode =?2 and r.flowType = 'TODO' and r.flowStatus = 'RUNNING' and r.postponedCount > 0 order by r.id desc")
    Page<FlowRecordEntity> findPostponedTodoByOperatorIdAndWorkCode(long operatorId, String workCode, PageRequest pageRequest);

    @Query(value = "select d from FlowRecordEntity  d where d.flowType!='DELETE' and  d.id in (select max(r.id) from FlowRecordEntity  r group by r.processId ) order by d.id desc")
    Page<FlowRecordEntity> findAllFlowRecords(PageRequest pageRequest);

}
