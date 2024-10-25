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

public class SignTest {


    private final UserRepository userRepository = new UserRepository();
    private final FlowWorkRepository flowWorkRepository = new FlowWorkRepositoryImpl();
    private final FlowRecordRepositoryImpl flowRecordRepository = new FlowRecordRepositoryImpl();
    private final FlowBindDataRepositoryImpl flowBindDataRepository = new FlowBindDataRepositoryImpl();
    private final LeaveRepository leaveRepository = new LeaveRepository();
    private final FlowBackupRepository flowBackupRepository = new FlowBackupRepositoryImpl();
    private final FlowProcessRepository flowProcessRepository = new FlowProcessRepositoryImpl(flowBackupRepository,userRepository);
    private final FlowService flowService = new FlowService(flowWorkRepository, flowRecordRepository, flowBindDataRepository, userRepository,flowProcessRepository,flowBackupRepository);

    /**
     * 多人非会签测试
     */
    @Test
    void unSignTest(){
        PageRequest pageRequest = PageRequest.of(0, 1000);

        User caocao = new User("曹操");
        userRepository.save(caocao);
        User lvBu = new User("吕布");
        userRepository.save(lvBu);
        User zhaoYun = new User("赵云");
        userRepository.save(zhaoYun);

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
                .node("部门领导审批", "dept", "default", ApprovalType.UN_SIGN,
                        OperatorMatcher.specifyOperatorMatcher(dept.getUserId(),caocao.getUserId(),lvBu.getUserId(),zhaoYun.getUserId()))
                .node("总经理审批", "manager", "default", ApprovalType.UN_SIGN, OperatorMatcher.specifyOperatorMatcher(boss.getUserId()))
                .node("结束节点", "over", "default", ApprovalType.UN_SIGN, OperatorMatcher.creatorOperatorMatcher())
                .relations()
                .relation("部门领导审批", "start", "dept")
                .relation("总经理审批", "dept", "manager")
                .relation("结束节点", "manager", "over")
                .build();

        flowWorkRepository.save(flowWork);

        long workId = flowWork.getId();

        Leave leave = new Leave("我要出去看看");
        leaveRepository.save(leave);

        // 创建流程
        flowService.startFlow(workId, user, leave, "发起流程");

        // 查看我的待办
        List<FlowRecord> userTodos = flowRecordRepository.findTodoByOperatorId(user.getUserId(), pageRequest).getContent();
        assertEquals(1, userTodos.size());

        // 提交流程
        FlowRecord userTodo = userTodos.get(0);
        flowService.submitFlow(userTodo.getId(), user, leave, Opinion.pass("同意"));

        // 查看部门经理的待办
        List<FlowRecord> deptTodos = flowRecordRepository.findTodoByOperatorId(zhaoYun.getUserId(), pageRequest).getContent();
        assertEquals(1, deptTodos.size());

        // 提交部门经理的审批
        FlowRecord deptTodo = deptTodos.get(0);
        flowService.submitFlow(deptTodo.getId(), zhaoYun, leave, Opinion.pass("同意"));

        // 查看总经理的待办
        List<FlowRecord> bossTodos = flowRecordRepository.findTodoByOperatorId(boss.getUserId(), pageRequest).getContent();
        assertEquals(1, bossTodos.size());

        // 提交总经理的审批
        FlowRecord bossTodo = bossTodos.get(0);
        flowService.submitFlow(bossTodo.getId(), boss, leave, Opinion.pass("同意"));

        // 查看所有流程
        List<FlowRecord> records = flowRecordRepository.findAll(pageRequest).getContent();;
        assertEquals(7, records.size());

        userTodos = flowRecordRepository.findTodoByOperatorId(user.getUserId(), pageRequest).getContent();
        assertEquals(1, userTodos.size());

        userTodo = userTodos.get(0);
        flowService.submitFlow(userTodo.getId(), user, leave, Opinion.pass("同意"));

        records = flowRecordRepository.findAll(pageRequest).getContent();
        assertEquals(7, records.size());
        // 查看所有流程是否都已经结束
        assertTrue(records.stream().allMatch(FlowRecord::isFinish));

        List<BindDataSnapshot> snapshots = flowBindDataRepository.findAll();
        assertEquals(5, snapshots.size());

    }



    /**
     * 多人会签测试
     */
    @Test
    void signTest(){
        PageRequest pageRequest = PageRequest.of(0, 1000);

        User caocao = new User("曹操");
        userRepository.save(caocao);
        User lvBu = new User("吕布");
        userRepository.save(lvBu);
        User zhaoYun = new User("赵云");
        userRepository.save(zhaoYun);

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
                .node("部门领导审批", "dept", "default", ApprovalType.SIGN,
                        OperatorMatcher.specifyOperatorMatcher(dept.getUserId(),caocao.getUserId(),lvBu.getUserId(),zhaoYun.getUserId()))
                .node("总经理审批", "manager", "default", ApprovalType.UN_SIGN, OperatorMatcher.specifyOperatorMatcher(boss.getUserId()))
                .node("结束节点", "over", "default", ApprovalType.UN_SIGN, OperatorMatcher.creatorOperatorMatcher())
                .relations()
                .relation("部门领导审批", "start", "dept")
                .relation("总经理审批", "dept", "manager")
                .relation("结束节点", "manager", "over")
                .build();

        flowWorkRepository.save(flowWork);

        long workId = flowWork.getId();

        Leave leave = new Leave("我要出去看看");
        leaveRepository.save(leave);

        // 创建流程
        flowService.startFlow(workId, user, leave, "发起流程");

        // 查看我的待办
        List<FlowRecord> userTodos = flowRecordRepository.findTodoByOperatorId(user.getUserId(), pageRequest).getContent();
        assertEquals(1, userTodos.size());

        // 提交流程
        FlowRecord userTodo = userTodos.get(0);
        flowService.submitFlow(userTodo.getId(), user, leave, Opinion.pass("用户同意"));

        // 查看部门经理的待办
        List<FlowRecord> deptTodos = flowRecordRepository.findTodoByOperatorId(dept.getUserId(), pageRequest).getContent();
        assertEquals(1, deptTodos.size());

        // 提交部门经理的审批
        FlowRecord deptTodo = deptTodos.get(0);
        flowService.submitFlow(deptTodo.getId(), dept, leave, Opinion.pass("刘备同意"));

        // 查看总经理的待办
        List<FlowRecord> bossTodos = flowRecordRepository.findTodoByOperatorId(boss.getUserId(), pageRequest).getContent();
        assertEquals(0, bossTodos.size());

        // 查看部门经理 吕布 的待办
        List<FlowRecord> lvbuTodos = flowRecordRepository.findTodoByOperatorId(lvBu.getUserId(), pageRequest).getContent();
        assertEquals(1, lvbuTodos.size());

        // 提交部门经理 吕布 的审批
        FlowRecord lvbuTodo = lvbuTodos.get(0);
        flowService.submitFlow(lvbuTodo.getId(), lvBu, leave, Opinion.pass("吕布同意"));


        // 查看部门经理 赵云 的待办
        List<FlowRecord> zhaoYunTodos = flowRecordRepository.findTodoByOperatorId(zhaoYun.getUserId(), pageRequest).getContent();
        assertEquals(1, zhaoYunTodos.size());

        // 提交部门经理 赵云 的审批
        FlowRecord zhaoYunTodo = zhaoYunTodos.get(0);
        flowService.submitFlow(zhaoYunTodo.getId(), zhaoYun, leave, Opinion.pass("赵云同意"));


        // 查看部门经理 曹操 的待办
        List<FlowRecord> caocaoTodos = flowRecordRepository.findTodoByOperatorId(caocao.getUserId(), pageRequest).getContent();
        assertEquals(1, caocaoTodos.size());

        // 提交部门经理 曹操 的审批
        FlowRecord caocaoTodo = caocaoTodos.get(0);
        flowService.submitFlow(caocaoTodo.getId(), caocao, leave, Opinion.pass("曹操同意"));

        bossTodos = flowRecordRepository.findTodoByOperatorId(boss.getUserId(), pageRequest).getContent();
        assertEquals(1, bossTodos.size());

        // 提交总经理的审批
        FlowRecord bossTodo = bossTodos.get(0);
        flowService.submitFlow(bossTodo.getId(), boss, leave, Opinion.pass("同意"));

        // 查看所有流程
        List<FlowRecord> records = flowRecordRepository.findAll(pageRequest).getContent();
        assertEquals(7, records.size());

        userTodos = flowRecordRepository.findTodoByOperatorId(user.getUserId(), pageRequest).getContent();
        assertEquals(1, userTodos.size());

        userTodo = userTodos.get(0);
        flowService.submitFlow(userTodo.getId(), user, leave, Opinion.pass("同意"));

        records = flowRecordRepository.findAll(pageRequest).getContent();
        assertEquals(7, records.size());
        // 查看所有流程是否都已经结束
        assertTrue(records.stream().allMatch(FlowRecord::isFinish));

        List<BindDataSnapshot> snapshots = flowBindDataRepository.findAll();
        assertEquals(8, snapshots.size());

    }




    /**
     * 多人会签 有人拒绝测试
     */
    @Test
    void signRejectTest(){

        PageRequest pageRequest = PageRequest.of(0, 1000);

        User caocao = new User("曹操");
        userRepository.save(caocao);
        User lvBu = new User("吕布");
        userRepository.save(lvBu);
        User zhaoYun = new User("赵云");
        userRepository.save(zhaoYun);

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
                .node("部门领导审批", "dept", "default", ApprovalType.SIGN,
                        OperatorMatcher.specifyOperatorMatcher(dept.getUserId(),caocao.getUserId(),lvBu.getUserId(),zhaoYun.getUserId()))
                .node("总经理审批", "manager", "default", ApprovalType.UN_SIGN, OperatorMatcher.specifyOperatorMatcher(boss.getUserId()))
                .node("结束节点", "over", "default", ApprovalType.UN_SIGN, OperatorMatcher.creatorOperatorMatcher())
                .relations()
                .relation("部门领导审批", "start", "dept")
                .relation("总经理审批", "dept", "manager")
                .relation("结束节点", "manager", "over")
                .build();

        flowWorkRepository.save(flowWork);

        long workId = flowWork.getId();

        Leave leave = new Leave("我要出去看看");
        leaveRepository.save(leave);

        // 创建流程
        flowService.startFlow(workId, user, leave, "发起流程");

        // 查看我的待办
        List<FlowRecord> userTodos = flowRecordRepository.findTodoByOperatorId(user.getUserId(), pageRequest).getContent();
        assertEquals(1, userTodos.size());

        // 提交流程
        FlowRecord userTodo = userTodos.get(0);
        flowService.submitFlow(userTodo.getId(), user, leave, Opinion.pass("用户同意"));

        // 查看部门经理的待办
        List<FlowRecord> deptTodos = flowRecordRepository.findTodoByOperatorId(dept.getUserId(), pageRequest).getContent();
        assertEquals(1, deptTodos.size());

        // 提交部门经理的审批
        FlowRecord deptTodo = deptTodos.get(0);
        flowService.submitFlow(deptTodo.getId(), dept, leave, Opinion.reject("刘备不同意"));

        // 查看总经理的待办
        List<FlowRecord> bossTodos = flowRecordRepository.findTodoByOperatorId(boss.getUserId(), pageRequest).getContent();
        assertEquals(0, bossTodos.size());

        // 查看部门经理 吕布 的待办
        List<FlowRecord> lvbuTodos = flowRecordRepository.findTodoByOperatorId(lvBu.getUserId(), pageRequest).getContent();
        assertEquals(1, lvbuTodos.size());

        // 提交部门经理 吕布 的审批
        FlowRecord lvbuTodo = lvbuTodos.get(0);
        flowService.submitFlow(lvbuTodo.getId(), lvBu, leave, Opinion.pass("吕布同意"));


        // 查看部门经理 赵云 的待办
        List<FlowRecord> zhaoYunTodos = flowRecordRepository.findTodoByOperatorId(zhaoYun.getUserId(), pageRequest).getContent();
        assertEquals(1, zhaoYunTodos.size());

        // 提交部门经理 赵云 的审批
        FlowRecord zhaoYunTodo = zhaoYunTodos.get(0);
        flowService.submitFlow(zhaoYunTodo.getId(), zhaoYun, leave, Opinion.pass("赵云同意"));


        // 查看部门经理 曹操 的待办
        List<FlowRecord> caocaoTodos = flowRecordRepository.findTodoByOperatorId(caocao.getUserId(), pageRequest).getContent();
        assertEquals(1, caocaoTodos.size());

        // 提交部门经理 曹操 的审批
        FlowRecord caocaoTodo = caocaoTodos.get(0);
        flowService.submitFlow(caocaoTodo.getId(), caocao, leave, Opinion.pass("曹操同意"));

        bossTodos = flowRecordRepository.findTodoByOperatorId(boss.getUserId(), pageRequest).getContent();
        assertEquals(0, bossTodos.size());

        userTodos = flowRecordRepository.findTodoByOperatorId(user.getUserId(), pageRequest).getContent();
        assertEquals(1, userTodos.size());

        userTodo = userTodos.get(0);
        flowService.submitFlow(userTodo.getId(), user, leave, Opinion.pass("用户同意"));


        deptTodos = flowRecordRepository.findTodoByOperatorId(dept.getUserId(), pageRequest).getContent();
        assertEquals(1, deptTodos.size());

        // 提交部门经理的审批
        deptTodo = deptTodos.get(0);
        flowService.submitFlow(deptTodo.getId(), dept, leave, Opinion.pass("刘备同意"));


        lvbuTodos = flowRecordRepository.findTodoByOperatorId(lvBu.getUserId(), pageRequest).getContent();
        assertEquals(1, lvbuTodos.size());

        // 提交部门经理 吕布 的审批
        lvbuTodo = lvbuTodos.get(0);
        flowService.submitFlow(lvbuTodo.getId(), lvBu, leave, Opinion.pass("吕布同意"));


        zhaoYunTodos = flowRecordRepository.findTodoByOperatorId(zhaoYun.getUserId(), pageRequest).getContent();
        assertEquals(1, zhaoYunTodos.size());

        // 提交部门经理 赵云 的审批
        zhaoYunTodo = zhaoYunTodos.get(0);
        flowService.submitFlow(zhaoYunTodo.getId(), zhaoYun, leave, Opinion.pass("赵云同意"));

        caocaoTodos = flowRecordRepository.findTodoByOperatorId(caocao.getUserId(), pageRequest).getContent();
        assertEquals(1, caocaoTodos.size());

        // 提交部门经理 曹操 的审批
        caocaoTodo = caocaoTodos.get(0);
        flowService.submitFlow(caocaoTodo.getId(), caocao, leave, Opinion.pass("曹操同意"));


        bossTodos = flowRecordRepository.findTodoByOperatorId(boss.getUserId(), pageRequest).getContent();
        assertEquals(1, bossTodos.size());
        // 提交总经理的审批
        FlowRecord bossTodo = bossTodos.get(0);
        flowService.submitFlow(bossTodo.getId(), boss, leave, Opinion.pass("同意"));

        // 查看所有流程
        List<FlowRecord> records = flowRecordRepository.findAll(pageRequest).getContent();;
        assertEquals(12, records.size());

        userTodos = flowRecordRepository.findTodoByOperatorId(user.getUserId(), pageRequest).getContent();
        assertEquals(1, userTodos.size());

        userTodo = userTodos.get(0);
        flowService.submitFlow(userTodo.getId(), user, leave, Opinion.pass("同意"));

        records = flowRecordRepository.findAll(pageRequest).getContent();
        assertEquals(12, records.size());
        // 查看所有流程是否都已经结束
        assertTrue(records.stream().allMatch(FlowRecord::isFinish));

        List<BindDataSnapshot> snapshots = flowBindDataRepository.findAll();
        assertEquals(13, snapshots.size());

    }
}
