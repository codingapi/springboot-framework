package com.codingapi.springboot.flow.test;

import com.codingapi.springboot.flow.bind.BindDataSnapshot;
import com.codingapi.springboot.flow.build.FlowWorkBuilder;
import com.codingapi.springboot.flow.domain.FlowWork;
import com.codingapi.springboot.flow.domain.Opinion;
import com.codingapi.springboot.flow.em.ApprovalType;
import com.codingapi.springboot.flow.flow.Leave;
import com.codingapi.springboot.flow.matcher.OperatorMatcher;
import com.codingapi.springboot.flow.record.FlowRecord;
import com.codingapi.springboot.flow.repository.*;
import com.codingapi.springboot.flow.service.FlowService;
import com.codingapi.springboot.flow.user.User;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class FlowBackTest {

    private final UserRepository userRepository = new UserRepository();
    private final FlowWorkRepository flowWorkRepository = new FlowWorkRepositoryImpl();
    private final FlowRecordRepositoryImpl flowRecordRepository = new FlowRecordRepositoryImpl();
    private final FlowBindDataRepositoryImpl flowBindDataRepository = new FlowBindDataRepositoryImpl();
    private final LeaveRepository leaveRepository = new LeaveRepository();
    private final FlowBackupRepository flowBackupRepository = new FlowBackupRepositoryImpl();
    private final FlowProcessRepository flowProcessRepository = new FlowProcessRepositoryImpl(flowBackupRepository, userRepository);
    private final FlowService flowService = new FlowService(flowWorkRepository, flowRecordRepository, flowBindDataRepository, userRepository, flowProcessRepository, flowBackupRepository);

    /**
     * 流程退回测试，结束的流程作废失败
     */
    @Test
    void back1() {
        PageRequest pageRequest = PageRequest.of(0, 1000);

        User user = new User("张飞");
        userRepository.save(user);

        User dept = new User("刘备");
        userRepository.save(dept);

        User boss = new User("诸葛亮",true);
        userRepository.save(boss);

        FlowWork flowWork = FlowWorkBuilder.builder(user)
                .title("请假流程")
                .skipIfSameApprover(true)
                .nodes()
                .node("开始节点", "start", "default", ApprovalType.UN_SIGN, OperatorMatcher.anyOperatorMatcher())
                .node("部门领导审批", "dept", "default", ApprovalType.UN_SIGN, OperatorMatcher.specifyOperatorMatcher(dept.getUserId()))
                .node("总经理审批", "manager", "default", ApprovalType.UN_SIGN, OperatorMatcher.specifyOperatorMatcher(dept.getUserId()))
                .node("结束节点", "over", "default", ApprovalType.UN_SIGN, OperatorMatcher.anyOperatorMatcher())
                .relations()
                .relation("部门领导审批", "start", "dept")
                .relation("总经理审批", "dept", "manager")
                .relation("结束节点", "manager", "over")
                .build();

        flowWorkRepository.save(flowWork);

        String workCode = flowWork.getCode();

        Leave leave = new Leave("我要出去看看");
        leaveRepository.save(leave);

        // 创建流程
        flowService.startFlow(workCode, user, leave, "发起流程");

        // 查看我的待办
        List<FlowRecord> userTodos = flowRecordRepository.findTodoByOperatorId(user.getUserId(), pageRequest).getContent();
        assertEquals(1, userTodos.size());

        // 提交流程
        FlowRecord userTodo = userTodos.get(0);
        assertEquals(0, userTodo.getTimeoutTime());

        flowService.submitFlow(userTodo.getId(), user, leave, Opinion.pass("同意"));

        // 查看刘备经理的待办
        List<FlowRecord> deptTodos = flowRecordRepository.findTodoByOperatorId(dept.getUserId(), pageRequest).getContent();
        assertEquals(1, deptTodos.size());

        // 提交委托dept部门经理的审批
        FlowRecord deptTodo = deptTodos.get(0);
        flowService.submitFlow(deptTodo.getId(), dept, leave, Opinion.pass("同意"));

        assertThrows(IllegalArgumentException.class,()->{
            // 退回流程
            flowService.back(deptTodo.getProcessId(),"start",boss);
        });

        // 查看所有流程
        List<FlowRecord> records = flowRecordRepository.findAll(pageRequest).getContent();
        assertEquals(3, records.size());

        userTodos = flowRecordRepository.findTodoByOperatorId(user.getUserId(), pageRequest).getContent();
        assertEquals(0, userTodos.size());

        records = flowRecordRepository.findAll(pageRequest).getContent();
        assertEquals(3, records.size());
        // 查看所有流程是否都已经结束
        assertTrue(records.stream().allMatch(FlowRecord::isFinish));

        List<BindDataSnapshot> snapshots = flowBindDataRepository.findAll();
        assertEquals(4, snapshots.size());

    }


    /**
     * 流程退回测试，在结束节点前退回流程
     */
    @Test
    void back2() {
        PageRequest pageRequest = PageRequest.of(0, 1000);

        User user = new User("张飞");
        userRepository.save(user);

        User dept = new User("刘备");
        userRepository.save(dept);

        User boss = new User("诸葛亮",true);
        userRepository.save(boss);

        FlowWork flowWork = FlowWorkBuilder.builder(user)
                .title("请假流程")
                .skipIfSameApprover(true)
                .nodes()
                .node("开始节点", "start", "default", ApprovalType.UN_SIGN, OperatorMatcher.anyOperatorMatcher())
                .node("部门领导审批", "dept", "default", ApprovalType.UN_SIGN, OperatorMatcher.specifyOperatorMatcher(dept.getUserId()))
                .node("总经理审批", "manager", "default", ApprovalType.UN_SIGN, OperatorMatcher.specifyOperatorMatcher(boss.getUserId()))
                .node("结束节点", "over", "default", ApprovalType.UN_SIGN, OperatorMatcher.anyOperatorMatcher())
                .relations()
                .relation("部门领导审批", "start", "dept")
                .relation("总经理审批", "dept", "manager")
                .relation("结束节点", "manager", "over")
                .build();

        flowWorkRepository.save(flowWork);

        String workCode = flowWork.getCode();

        Leave leave = new Leave("我要出去看看");
        leaveRepository.save(leave);

        // 创建流程
        flowService.startFlow(workCode, user, leave, "发起流程");

        // 查看我的待办
        List<FlowRecord> userTodos = flowRecordRepository.findTodoByOperatorId(user.getUserId(), pageRequest).getContent();
        assertEquals(1, userTodos.size());

        // 提交流程
        FlowRecord userTodo = userTodos.get(0);
        assertEquals(0, userTodo.getTimeoutTime());

        flowService.submitFlow(userTodo.getId(), user, leave, Opinion.pass("同意"));

        // 查看刘备经理的待办
        List<FlowRecord> deptTodos = flowRecordRepository.findTodoByOperatorId(dept.getUserId(), pageRequest).getContent();
        assertEquals(1, deptTodos.size());

        // 提交委托dept部门经理的审批
        FlowRecord deptTodo = deptTodos.get(0);
        // 流程作废
        flowService.back(deptTodo.getProcessId(),"start",boss);

        // 查看所有流程
        List<FlowRecord> records = flowRecordRepository.findAll(pageRequest).getContent();
        assertEquals(1, records.size());

        userTodos = flowRecordRepository.findTodoByOperatorId(user.getUserId(), pageRequest).getContent();
        assertEquals(1, userTodos.size());

        userTodo = userTodos.get(0);
        assertEquals(0, userTodo.getTimeoutTime());

        flowService.submitFlow(userTodo.getId(), user, leave, Opinion.pass("同意"));


        deptTodos = flowRecordRepository.findTodoByOperatorId(dept.getUserId(), pageRequest).getContent();
        assertEquals(1, deptTodos.size());

        // 提交委托dept部门经理的审批
        deptTodo = deptTodos.get(0);
        flowService.submitFlow(deptTodo.getId(), dept, leave, Opinion.pass("同意"));

        List<FlowRecord> bossTodos = flowRecordRepository.findTodoByOperatorId(boss.getUserId(), pageRequest).getContent();
        assertEquals(1, bossTodos.size());

        FlowRecord bossTodo = bossTodos.get(0);
        flowService.submitFlow(bossTodo.getId(), boss, leave, Opinion.pass("同意"));

        records = flowRecordRepository.findAll(pageRequest).getContent();
        assertEquals(3, records.size());
        // 查看所有流程是否都已经结束
        assertTrue(records.stream().allMatch(FlowRecord::isFinish));

        List<BindDataSnapshot> snapshots = flowBindDataRepository.findAll();
        assertEquals(5, snapshots.size());

    }
}
