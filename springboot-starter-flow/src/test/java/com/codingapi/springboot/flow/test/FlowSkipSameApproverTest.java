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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FlowSkipSameApproverTest {

    private final UserRepository userRepository = new UserRepository();
    private final FlowWorkRepository flowWorkRepository = new FlowWorkRepositoryImpl();
    private final FlowRecordRepositoryImpl flowRecordRepository = new FlowRecordRepositoryImpl();
    private final FlowBindDataRepositoryImpl flowBindDataRepository = new FlowBindDataRepositoryImpl();
    private final LeaveRepository leaveRepository = new LeaveRepository();
    private final FlowBackupRepository flowBackupRepository = new FlowBackupRepositoryImpl();
    private final FlowProcessRepository flowProcessRepository = new FlowProcessRepositoryImpl(flowBackupRepository, userRepository);
    private final FlowService flowService = new FlowService(flowWorkRepository, flowRecordRepository, flowBindDataRepository, userRepository, flowProcessRepository, flowBackupRepository);


    /**
     * 设置跳过审批人且会签时测试
     */
    @Test
    void test() {
        PageRequest pageRequest = PageRequest.of(0, 1000);

        User user = new User("张飞");
        userRepository.save(user);

        User dept = new User("刘备");
        userRepository.save(dept);

        User boss = new User("诸葛亮");
        userRepository.save(boss);

        FlowWork flowWork = FlowWorkBuilder.builder(user)
                .title("请假流程")
                .skipIfSameApprover(true)
                .nodes()
                .node("开始节点", "start", "default", ApprovalType.UN_SIGN, OperatorMatcher.anyOperatorMatcher())
                .node("部门负责人审批", "dept", "default", ApprovalType.SIGN, OperatorMatcher.specifyOperatorMatcher(user.getUserId(), boss.getUserId()))
                .node("分管领导审批", "office", "default", ApprovalType.UN_SIGN, OperatorMatcher.specifyOperatorMatcher(dept.getUserId()))
                .node("总经理审批", "manager", "default", ApprovalType.UN_SIGN, OperatorMatcher.specifyOperatorMatcher(boss.getUserId()))
                .node("结束节点", "over", "default", ApprovalType.UN_SIGN, OperatorMatcher.anyOperatorMatcher())
                .relations()
                .relation("部门负责人审批", "start", "dept")
                .relation("分管领导审批", "dept", "office")
                .relation("总经理审批", "office", "manager")
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
        flowService.submitFlow(userTodo.getId(), user, leave, Opinion.pass("同意"));

        // 查看部门经理的待办
        List<FlowRecord> deptTodos = flowRecordRepository.findTodoByOperatorId(dept.getUserId(), pageRequest).getContent();
        assertEquals(0, deptTodos.size());

        // 查看部门经理的待办
        List<FlowRecord> boosTodos = flowRecordRepository.findTodoByOperatorId(boss.getUserId(), pageRequest).getContent();
        assertEquals(1, boosTodos.size());

        FlowRecord boosTodo = boosTodos.get(0);
        flowService.submitFlow(boosTodo.getId(), boss, leave, Opinion.pass("同意"));

        deptTodos = flowRecordRepository.findTodoByOperatorId(dept.getUserId(), pageRequest).getContent();
        assertEquals(1, deptTodos.size());

        FlowRecord deptTodo = deptTodos.get(0);
        flowService.submitFlow(deptTodo.getId(), dept, leave, Opinion.pass("同意"));

        boosTodos = flowRecordRepository.findTodoByOperatorId(boss.getUserId(), pageRequest).getContent();
        assertEquals(1, boosTodos.size());

        boosTodo = boosTodos.get(0);
        flowService.submitFlow(boosTodo.getId(), boss, leave, Opinion.pass("同意"));

        // 查看所有流程
        List<FlowRecord> records = flowRecordRepository.findAll(pageRequest).getContent();
        assertEquals(5, records.size());

        userTodos = flowRecordRepository.findTodoByOperatorId(user.getUserId(), pageRequest).getContent();
        assertEquals(0, userTodos.size());

        records = flowRecordRepository.findAll(pageRequest).getContent();
        assertEquals(5, records.size());
        // 查看所有流程是否都已经结束
        assertTrue(records.stream().allMatch(FlowRecord::isFinish));

        List<BindDataSnapshot> snapshots = flowBindDataRepository.findAll();
        assertEquals(6, snapshots.size());


    }
}
