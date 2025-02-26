package com.codingapi.springboot.flow.test;

import com.codingapi.springboot.flow.build.FlowWorkBuilder;
import com.codingapi.springboot.flow.domain.FlowNode;
import com.codingapi.springboot.flow.domain.FlowWork;
import com.codingapi.springboot.flow.domain.Opinion;
import com.codingapi.springboot.flow.em.ApprovalType;
import com.codingapi.springboot.flow.flow.Leave;
import com.codingapi.springboot.flow.matcher.OperatorMatcher;
import com.codingapi.springboot.flow.pojo.FlowDetail;
import com.codingapi.springboot.flow.record.FlowRecord;
import com.codingapi.springboot.flow.repository.*;
import com.codingapi.springboot.flow.service.FlowService;
import com.codingapi.springboot.flow.user.User;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class FlowTest2 {

    private final UserRepository userRepository = new UserRepository();
    private final FlowWorkRepository flowWorkRepository = new FlowWorkRepositoryImpl();
    private final FlowRecordRepositoryImpl flowRecordRepository = new FlowRecordRepositoryImpl();
    private final FlowBindDataRepositoryImpl flowBindDataRepository = new FlowBindDataRepositoryImpl();
    private final LeaveRepository leaveRepository = new LeaveRepository();
    private final FlowBackupRepository flowBackupRepository = new FlowBackupRepositoryImpl();
    private final FlowProcessRepository flowProcessRepository = new FlowProcessRepositoryImpl(flowBackupRepository,userRepository);
    private final FlowService flowService = new FlowService(flowWorkRepository, flowRecordRepository, flowBindDataRepository, userRepository,flowProcessRepository,flowBackupRepository);

    /**
     * 转办测试
     */
    @Test
    void flowTest() {
        PageRequest pageRequest = PageRequest.of(0, 1000);
        User lorne = new User("lorne");
        userRepository.save(lorne);

        User boss = new User("boss");
        userRepository.save(boss);

        FlowWork flowWork = FlowWorkBuilder.builder(lorne)
                .title("请假流程")
                .nodes()
                .node("开始节点", "start", "default", ApprovalType.UN_SIGN, OperatorMatcher.anyOperatorMatcher())
                .node("老板审批", "boss", "default", ApprovalType.SIGN, OperatorMatcher.anyOperatorMatcher())
                .node("结束节点", "over", "default", ApprovalType.UN_SIGN, OperatorMatcher.anyOperatorMatcher())
                .relations()
                .relation("老板审批", "start", "boss")
                .relation("结束节点", "boss", "over")
                .build();

        flowWorkRepository.save(flowWork);

        String workCode = flowWork.getCode();

        Leave leave = new Leave("我想要出去看看");
        leaveRepository.save(leave);

        // 创建流程
        flowService.startFlow(workCode, lorne, leave, "发起流程");
        // 查看我的待办
        List<FlowRecord> userTodos = flowRecordRepository.findTodoByOperatorId(lorne.getUserId(), pageRequest).getContent();
        assertEquals(1, userTodos.size());

        FlowRecord userTodo = userTodos.get(0);
        flowService.submitFlow(userTodo.getId(), lorne, leave, Opinion.pass("自己提交"));

        // 部门领导审批
        List<FlowRecord> deptTodos = flowRecordRepository.findTodoByOperatorId(lorne.getUserId(), pageRequest).getContent();
        assertEquals(1, deptTodos.size());

        FlowRecord deptTodo = deptTodos.get(0);
        assertNull(deptTodo.getOpinion());
        flowService.transfer(deptTodo.getId(), lorne,boss, leave, "转交给领导审批通过");

        // 查看boss的待办
        List<FlowRecord> bossTodos = flowRecordRepository.findTodoByOperatorId(boss.getUserId(), pageRequest).getContent();
        assertEquals(1, bossTodos.size());

        FlowRecord bossTodo = bossTodos.get(0);
        flowService.submitFlow(bossTodo.getId(), boss, leave, Opinion.reject("领导审批不通过"));

        userTodos = flowRecordRepository.findTodoByOperatorId(lorne.getUserId(), pageRequest).getContent();
        assertEquals(1, userTodos.size());

        userTodo = userTodos.get(0);
        assertEquals(FlowNode.CODE_START, userTodo.getNodeCode());

        flowService.submitFlow(userTodo.getId(), lorne, leave, Opinion.pass("自己再次提交"));

        deptTodos = flowRecordRepository.findTodoByOperatorId(lorne.getUserId(), pageRequest).getContent();
        assertEquals(1, deptTodos.size());

        deptTodo = deptTodos.get(0);
        flowService.submitFlow(deptTodo.getId(), lorne, leave, Opinion.pass("转交给领导审批通过"));

        bossTodos = flowRecordRepository.findTodoByOperatorId(lorne.getUserId(), pageRequest).getContent();
        assertEquals(0, bossTodos.size());

        List<FlowRecord> records = flowRecordRepository.findAll(pageRequest).getContent();
        assertEquals(5, records.size());

        // 查看所有流程是否都已经结束
        assertTrue(records.stream().allMatch(FlowRecord::isFinish));

    }



    /**
     * 流程等待测试
     */
    @Test
    void flowWaitingTest() {
        PageRequest pageRequest = PageRequest.of(0, 1000);
        User lorne = new User("lorne");
        userRepository.save(lorne);

        User boss = new User("boss");
        userRepository.save(boss);

        FlowWork flowWork = FlowWorkBuilder.builder(lorne)
                .title("请假流程")
                .nodes()
                .node("开始节点", "start", "default", ApprovalType.UN_SIGN, OperatorMatcher.anyOperatorMatcher())
                .node("老板审批", "boss", "default", ApprovalType.UN_SIGN, OperatorMatcher.specifyOperatorMatcher(boss.getUserId()))
                .node("结束节点", "over", "default", ApprovalType.UN_SIGN, OperatorMatcher.anyOperatorMatcher())
                .relations()
                .relation("老板审批", "start", "boss")
                .relation("结束节点", "boss", "over")
                .build();

        flowWorkRepository.save(flowWork);

        String workCode = flowWork.getCode();

        Leave leave = new Leave("我想要出去看看");
        leaveRepository.save(leave);

        // 创建流程
        flowService.startFlow(workCode, lorne, leave, "发起流程");

        // 查看我的待办
        List<FlowRecord> userTodos = flowRecordRepository.findTodoByOperatorId(lorne.getUserId(), pageRequest).getContent();
        assertEquals(1, userTodos.size());

        String processId = userTodos.get(0).getProcessId();

        FlowRecord userTodo = userTodos.get(0);
        flowService.submitFlow(userTodo.getId(), lorne, leave, Opinion.waiting("自己先提交"));

        // 查看boss的待办
        List<FlowRecord> bossTodos = flowRecordRepository.findTodoByOperatorId(boss.getUserId(), pageRequest).getContent();
        assertEquals(0, bossTodos.size());

        // 通知流程
        flowService.notifyFlow(processId,boss);

        // 查看boss的待办
        bossTodos = flowRecordRepository.findTodoByOperatorId(boss.getUserId(), pageRequest).getContent();
        assertEquals(1, bossTodos.size());

        FlowRecord bossTodo = bossTodos.get(0);
        flowService.submitFlow(bossTodo.getId(), boss, leave, Opinion.pass("领导审批通过"));

        bossTodos = flowRecordRepository.findTodoByOperatorId(lorne.getUserId(), pageRequest).getContent();
        assertEquals(0, bossTodos.size());

        List<FlowRecord> records = flowRecordRepository.findAll(pageRequest).getContent();
        assertEquals(2, records.size());

        // 查看所有流程是否都已经结束
        assertTrue(records.stream().allMatch(FlowRecord::isFinish));

    }


    /**
     * 部门拒绝再提交测试
     */
    @Test
    void rejectTest() {
        PageRequest pageRequest = PageRequest.of(0, 1000);

        User user = new User("张飞");
        userRepository.save(user);

        User dept = new User("刘备");
        userRepository.save(dept);

        User boss = new User("诸葛亮");
        userRepository.save(boss);

        FlowWork flowWork = FlowWorkBuilder.builder(user)
                .title("请假流程")
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

        // 查看流程详情
        FlowDetail flowDetail = flowService.detail(userTodo.getId());
        assertEquals("我要出去看看", ((Leave) flowDetail.getBindData()).getTitle());
        assertTrue(flowDetail.getFlowRecord().isUnRead());


        flowService.submitFlow(userTodo.getId(), user, leave, Opinion.pass("同意"));

        // 查看部门经理的待办
        List<FlowRecord> deptTodos = flowRecordRepository.findTodoByOperatorId(dept.getUserId(), pageRequest).getContent();
        assertEquals(1, deptTodos.size());

        // 提交部门经理的审批
        FlowRecord deptTodo = deptTodos.get(0);
        flowService.submitFlow(deptTodo.getId(), dept, leave, Opinion.pass("同意"));

        // 查看老板审批
        List<FlowRecord> bossTodos = flowRecordRepository.findTodoByOperatorId(boss.getUserId(), pageRequest).getContent();
        assertEquals(1, bossTodos.size());

        // 老板审批不通过
        FlowRecord bossTodo = bossTodos.get(0);
        flowService.submitFlow(bossTodo.getId(), boss, leave, Opinion.reject("不同意"));

        // 部门经理查看到流程
        deptTodos = flowRecordRepository.findTodoByOperatorId(dept.getUserId(), pageRequest).getContent();
        assertEquals(1, deptTodos.size());

        // 提交部门经理的审批
        deptTodo = deptTodos.get(0);
        flowService.submitFlow(deptTodo.getId(), dept, leave, Opinion.reject("不同意"));

        // 查看用户的待办
        userTodos = flowRecordRepository.findTodoByOperatorId(user.getUserId(), pageRequest).getContent();
        assertEquals(1, userTodos.size());

        // 用户再次提交
        userTodo = userTodos.get(0);
        flowService.submitFlow(userTodo.getId(), user, leave, Opinion.pass("同意"));

        // 部门经理查看到流程
        deptTodos = flowRecordRepository.findTodoByOperatorId(dept.getUserId(), pageRequest).getContent();
        assertEquals(1, deptTodos.size());

        // 提交部门经理的审批
        deptTodo = deptTodos.get(0);
        flowService.submitFlow(deptTodo.getId(), dept, leave, Opinion.pass("同意"));

        // 查看老板审批
        bossTodos = flowRecordRepository.findTodoByOperatorId(boss.getUserId(), pageRequest).getContent();
        assertEquals(1, bossTodos.size());

        // 老板审批通过
        bossTodo = bossTodos.get(0);
        flowService.submitFlow(bossTodo.getId(), boss, leave, Opinion.pass("同意"));


        List<FlowRecord> records = flowRecordRepository.findAll(pageRequest).getContent();
        assertEquals(7, records.size());

        // 查看所有流程是否都已经结束
        assertTrue(records.stream().allMatch(FlowRecord::isFinish));

    }
}
