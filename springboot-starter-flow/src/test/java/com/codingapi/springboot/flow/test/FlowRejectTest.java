package com.codingapi.springboot.flow.test;

import com.codingapi.springboot.flow.bind.BindDataSnapshot;
import com.codingapi.springboot.flow.build.FlowWorkBuilder;
import com.codingapi.springboot.flow.domain.FlowWork;
import com.codingapi.springboot.flow.domain.Opinion;
import com.codingapi.springboot.flow.em.ApprovalType;
import com.codingapi.springboot.flow.flow.Leave;
import com.codingapi.springboot.flow.matcher.OperatorMatcher;
import com.codingapi.springboot.flow.pojo.FlowResult;
import com.codingapi.springboot.flow.record.FlowRecord;
import com.codingapi.springboot.flow.repository.*;
import com.codingapi.springboot.flow.service.FlowService;
import com.codingapi.springboot.flow.trigger.OutTrigger;
import com.codingapi.springboot.flow.user.User;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FlowRejectTest {

    private final UserRepository userRepository = new UserRepository();
    private final FlowWorkRepository flowWorkRepository = new FlowWorkRepositoryImpl();
    private final FlowRecordRepositoryImpl flowRecordRepository = new FlowRecordRepositoryImpl();
    private final FlowBindDataRepositoryImpl flowBindDataRepository = new FlowBindDataRepositoryImpl();
    private final LeaveRepository leaveRepository = new LeaveRepository();
    private final FlowBackupRepository flowBackupRepository = new FlowBackupRepositoryImpl();
    private final FlowProcessRepository flowProcessRepository = new FlowProcessRepositoryImpl(flowBackupRepository, userRepository);
    private final FlowService flowService = new FlowService(flowWorkRepository, flowRecordRepository, flowBindDataRepository, userRepository, flowProcessRepository, flowBackupRepository);

    /**
     * 驳回测试
     */
    @Test
    void reject1() {
        PageRequest pageRequest = PageRequest.of(0, 1000);

        User lorne = new User("lorne");
        userRepository.save(lorne);

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
                .node("部门领导审批", "dept", "default", ApprovalType.SIGN, OperatorMatcher.specifyOperatorMatcher(dept.getUserId(),lorne.getUserId()))
                .node("总经理审批", "manager", "default", ApprovalType.SIGN, OperatorMatcher.specifyOperatorMatcher(boss.getUserId()))
                .node("结束节点", "over", "default", ApprovalType.UN_SIGN, OperatorMatcher.anyOperatorMatcher())
                .relations()
                .relation("部门领导审批", "start", "dept")
                .relation("总经理审批", "dept", "manager")
                .relation("总经理审批", "dept", "start",new OutTrigger(" def run(content) {\n" +
                        "                            return false;\n" +
                        "                        }"),1,true)
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

        flowService.submitFlow(userTodo.getId(), user, leave, Opinion.pass("同意").specify(lorne.getUserId()));

        // 查看刘备经理的待办
        List<FlowRecord> deptTodos = flowRecordRepository.findTodoByOperatorId(dept.getUserId(), pageRequest).getContent();
        assertEquals(0, deptTodos.size());

        List<FlowRecord> lorneTodos = flowRecordRepository.findTodoByOperatorId(lorne.getUserId(), pageRequest).getContent();
        assertEquals(1, lorneTodos.size());

        // 提交委托lorne部门经理的审批
        FlowRecord lorneTodo = lorneTodos.get(0);
        flowService.submitFlow(lorneTodo.getId(), lorne, leave, Opinion.pass("同意"));

        // 查看总经理的待办
        List<FlowRecord> bossTodos = flowRecordRepository.findTodoByOperatorId(boss.getUserId(), pageRequest).getContent();
        assertEquals(1, bossTodos.size());

        // 提交总经理的审批
        FlowRecord bossTodo = bossTodos.get(0);
        FlowResult flowResult = flowService.submitFlow(bossTodo.getId(), boss, leave, Opinion.reject("不同意"));
        System.out.println(flowResult.getRecords());

        deptTodos = flowRecordRepository.findTodoByOperatorId(dept.getUserId(), pageRequest).getContent();
        assertEquals(0, deptTodos.size());

        lorneTodos = flowRecordRepository.findTodoByOperatorId(lorne.getUserId(), pageRequest).getContent();
        assertEquals(1, lorneTodos.size());

        lorneTodo = lorneTodos.get(0);
        flowService.submitFlow(lorneTodo.getId(), lorne, leave, Opinion.pass("同意"));

        bossTodos = flowRecordRepository.findTodoByOperatorId(boss.getUserId(), pageRequest).getContent();
        assertEquals(1, bossTodos.size());

        bossTodo = bossTodos.get(0);
        flowService.submitFlow(bossTodo.getId(), boss, leave, Opinion.pass("同意"));

        // 查看所有流程
        List<FlowRecord> records = flowRecordRepository.findAll(pageRequest).getContent();
        assertEquals(5, records.size());

        records = flowRecordRepository.findAll(pageRequest).getContent();
        assertEquals(5, records.size());
        // 查看所有流程是否都已经结束
        assertTrue(records.stream().allMatch(FlowRecord::isFinish));

        List<BindDataSnapshot> snapshots = flowBindDataRepository.findAll();
        assertEquals(6, snapshots.size());

    }



    /**
     * 驳回测试
     */
    @Test
    void reject2() {
        PageRequest pageRequest = PageRequest.of(0, 1000);

        User lorne = new User("lorne");
        userRepository.save(lorne);

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
                .node("部门领导审批", "dept", "default", ApprovalType.SIGN, OperatorMatcher.specifyOperatorMatcher(dept.getUserId(),lorne.getUserId()))
                .node("总经理审批", "manager", "default", ApprovalType.SIGN, OperatorMatcher.specifyOperatorMatcher(boss.getUserId()))
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

        flowService.submitFlow(userTodo.getId(), user, leave, Opinion.pass("同意").specify(lorne.getUserId()));

        // 查看刘备经理的待办
        List<FlowRecord> deptTodos = flowRecordRepository.findTodoByOperatorId(dept.getUserId(), pageRequest).getContent();
        assertEquals(0, deptTodos.size());

        List<FlowRecord> lorneTodos = flowRecordRepository.findTodoByOperatorId(lorne.getUserId(), pageRequest).getContent();
        assertEquals(1, lorneTodos.size());

        // 提交委托lorne部门经理的审批
        FlowRecord lorneTodo = lorneTodos.get(0);
        flowService.submitFlow(lorneTodo.getId(), lorne, leave, Opinion.pass("同意"));

        // 查看总经理的待办
        List<FlowRecord> bossTodos = flowRecordRepository.findTodoByOperatorId(boss.getUserId(), pageRequest).getContent();
        assertEquals(1, bossTodos.size());

        // 提交总经理的审批
        FlowRecord bossTodo = bossTodos.get(0);
        FlowResult flowResult = flowService.submitFlow(bossTodo.getId(), boss, leave, Opinion.reject("不同意"));
        System.out.println(flowResult.getRecords());

        deptTodos = flowRecordRepository.findTodoByOperatorId(dept.getUserId(), pageRequest).getContent();
        assertEquals(0, deptTodos.size());

        lorneTodos = flowRecordRepository.findTodoByOperatorId(lorne.getUserId(), pageRequest).getContent();
        assertEquals(1, lorneTodos.size());

        lorneTodo = lorneTodos.get(0);
        flowService.submitFlow(lorneTodo.getId(), lorne, leave, Opinion.pass("同意"));

        bossTodos = flowRecordRepository.findTodoByOperatorId(boss.getUserId(), pageRequest).getContent();
        assertEquals(1, bossTodos.size());

        bossTodo = bossTodos.get(0);
        flowService.submitFlow(bossTodo.getId(), boss, leave, Opinion.pass("同意"));

        // 查看所有流程
        List<FlowRecord> records = flowRecordRepository.findAll(pageRequest).getContent();
        assertEquals(5, records.size());

        records = flowRecordRepository.findAll(pageRequest).getContent();
        assertEquals(5, records.size());
        // 查看所有流程是否都已经结束
        assertTrue(records.stream().allMatch(FlowRecord::isFinish));

        List<BindDataSnapshot> snapshots = flowBindDataRepository.findAll();
        assertEquals(6, snapshots.size());

    }



    /**
     * 驳回测试3
     */
    @Test
    void reject3() {
        PageRequest pageRequest = PageRequest.of(0, 1000);

        User lorne = new User("lorne");
        userRepository.save(lorne);

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
                .node("部门领导审批", "dept", "default", ApprovalType.SIGN, OperatorMatcher.specifyOperatorMatcher(dept.getUserId()))
                .node("总经理审批", "manager", "default", ApprovalType.SIGN, OperatorMatcher.specifyOperatorMatcher(boss.getUserId()))
                .node("结束节点", "over", "default", ApprovalType.UN_SIGN, OperatorMatcher.anyOperatorMatcher())
                .relations()
                .relation("部门领导审批", "start", "dept")
                .relation("总经理审批", "dept", "manager")
                .relation("总经理审批", "manager", "start",new OutTrigger("" +
                        "def run(content) {\n" +
                        "                            if(content.getBindData().getDays() > 3) {\n" +
                        "                                return true;\n" +
                        "                            }\n" +
                        "                            return false;\n" +
                        "                        }"),1,true)
                .relation("结束节点", "manager", "over")
                .build();

        flowWorkRepository.save(flowWork);

        String workCode = flowWork.getCode();

        Leave leave = new Leave("我要出去看看");
        leave.setDays(5);
        leaveRepository.save(leave);

        // 创建流程
        flowService.startFlow(workCode, user, leave, "发起流程");

        // 查看我的待办
        List<FlowRecord> userTodos = flowRecordRepository.findTodoByOperatorId(user.getUserId(), pageRequest).getContent();
        assertEquals(1, userTodos.size());

        // 提交流程
        FlowRecord userTodo = userTodos.get(0);
        assertEquals(0, userTodo.getTimeoutTime());

        flowService.submitFlow(userTodo.getId(), user, leave, Opinion.pass("同意").specify(dept.getUserId()));

        // 查看刘备经理的待办
        List<FlowRecord> deptTodos = flowRecordRepository.findTodoByOperatorId(dept.getUserId(), pageRequest).getContent();
        assertEquals(1, deptTodos.size());

        // 部门经理的提交审批
        FlowRecord deptTodo = deptTodos.get(0);
        flowService.submitFlow(deptTodo.getId(), dept, leave, Opinion.pass("同意"));

        // 查看总经理的待办
        List<FlowRecord> bossTodos = flowRecordRepository.findTodoByOperatorId(boss.getUserId(), pageRequest).getContent();
        assertEquals(1, bossTodos.size());

        // 提交总经理的审批
        FlowRecord bossTodo = bossTodos.get(0);
        flowService.submitFlow(bossTodo.getId(), boss, leave, Opinion.reject("不同意"));

        deptTodos = flowRecordRepository.findTodoByOperatorId(dept.getUserId(), pageRequest).getContent();
        assertEquals(0, deptTodos.size());

        userTodos = flowRecordRepository.findTodoByOperatorId(user.getUserId(), pageRequest).getContent();
        assertEquals(1, bossTodos.size());

        userTodo = userTodos.get(0);
        leave.setDays(3);
        flowService.submitFlow(userTodo.getId(), user, leave, Opinion.pass("同意"));

        // 查看所有流程
        List<FlowRecord> records = flowRecordRepository.findAll(pageRequest).getContent();
        assertEquals(5, records.size());

        deptTodos = flowRecordRepository.findTodoByOperatorId(dept.getUserId(), pageRequest).getContent();
        assertEquals(1, deptTodos.size());

        // 部门经理的提交审批
        deptTodo = deptTodos.get(0);
        flowService.submitFlow(deptTodo.getId(), dept, leave, Opinion.pass("同意"));

        bossTodos = flowRecordRepository.findTodoByOperatorId(boss.getUserId(), pageRequest).getContent();
        assertEquals(1, bossTodos.size());

        // 提交总经理的审批
        bossTodo = bossTodos.get(0);
        flowService.submitFlow(bossTodo.getId(), boss, leave, Opinion.pass("同意"));

        records = flowRecordRepository.findAll(pageRequest).getContent();
        assertEquals(6, records.size());
        // 查看所有流程是否都已经结束
        assertTrue(records.stream().allMatch(FlowRecord::isFinish));

        List<BindDataSnapshot> snapshots = flowBindDataRepository.findAll();
        assertEquals(7, snapshots.size());

    }



    /**
     *  多条件流程测试撤回
     */
    @Test
    void reject4(){
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
                .relation("总经理审批", "dept", "over",new OutTrigger("def run(content){content.getBindData().getDays()<=5}"),1,false)
                .relation("总经理审批", "dept", "manager",new OutTrigger("def run(content){content.getBindData().getDays()>5}"),2,false)
                .relation("结束节点", "manager", "start",new OutTrigger("def run(content){return true}"),1,true)
                .relation("结束节点", "manager", "over")
                .build();

        flowWorkRepository.save(flowWork);

        String workCode = flowWork.getCode();

        Leave leave = new Leave("我要出去看看",6);
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
        assertEquals(1, deptTodos.size());

        // 提交部门经理的审批
        FlowRecord deptTodo = deptTodos.get(0);
        flowService.submitFlow(deptTodo.getId(), dept, leave, Opinion.pass("同意"));


        // 查看老板的待办
        List<FlowRecord> bossTodos = flowRecordRepository.findTodoByOperatorId(boss.getUserId(), pageRequest).getContent();
        assertEquals(1, bossTodos.size());

        // 提交老板的审批
        FlowRecord bossTodo = bossTodos.get(0);
        flowService.submitFlow(bossTodo.getId(), boss, leave, Opinion.reject("不同意，最多让你请假3天"));

        // 查看所有流程
        List<FlowRecord> records = flowRecordRepository.findAll(pageRequest).getContent();
        assertEquals(4, records.size());

        // 用户修改确认
        userTodos = flowRecordRepository.findTodoByOperatorId(user.getUserId(), pageRequest).getContent();
        assertEquals(1, userTodos.size());

        // 用户调整为3天
        leave.setDays(3);
        // 提交流程
        userTodo = userTodos.get(0);
        flowService.submitFlow(userTodo.getId(), user, leave, Opinion.pass("同意"));

        deptTodos = flowRecordRepository.findTodoByOperatorId(dept.getUserId(), pageRequest).getContent();
        assertEquals(1, deptTodos.size());

        // 提交部门经理的审批
        deptTodo = deptTodos.get(0);
        flowService.submitFlow(deptTodo.getId(), dept, leave, Opinion.pass("同意"));

        records = flowRecordRepository.findAll(pageRequest).getContent();
        assertEquals(5, records.size());

        // 查看所有流程是否都已经结束
        assertTrue(records.stream().allMatch(FlowRecord::isFinish));

        List<BindDataSnapshot> snapshots = flowBindDataRepository.findAll();
        assertEquals(6, snapshots.size());
    }

}
