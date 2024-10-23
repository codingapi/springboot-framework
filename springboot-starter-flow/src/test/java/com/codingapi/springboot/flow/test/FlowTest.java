package com.codingapi.springboot.flow.test;

import com.codingapi.springboot.flow.build.FlowWorkBuilder;
import com.codingapi.springboot.flow.domain.FlowWork;
import com.codingapi.springboot.flow.em.ApprovalType;
import com.codingapi.springboot.flow.flow.Leave;
import com.codingapi.springboot.flow.matcher.OperatorMatcher;
import com.codingapi.springboot.flow.record.FlowRecord;
import com.codingapi.springboot.flow.repository.*;
import com.codingapi.springboot.flow.service.FlowService;
import com.codingapi.springboot.flow.user.User;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FlowTest {

    private final UserRepository userRepository = new UserRepository();
    private final FlowWorkRepository flowWorkRepository = new FlowWorkRepositoryImpl();
    private final FlowRecordRepository flowRecordRepository = new FlowRecordRepositoryImpl();
    private final FlowBindDataRepository flowBindDataRepository = new FlowBindDataRepositoryImpl();
    private final LeaveRepository leaveRepository = new LeaveRepository();
    private final FlowService flowService = new FlowService(flowWorkRepository, flowRecordRepository, flowBindDataRepository,userRepository);


    @Test
    void test() {
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
                .relation("部门领导审批", "start", "dept", false)
                .relation("总经理审批", "dept", "manager", false)
                .relation("结束节点", "manager", "over", false)
                .build();

        flowWorkRepository.save(flowWork);

        long workId = flowWork.getId();

        Leave leave = new Leave("我要出去看看");
        leaveRepository.save(leave);

        flowService.startFlow(workId, user, leave, "发起流程");

        List<FlowRecord> records = flowRecordRepository.findAll();
        System.out.println(records);
        assertEquals(2, records.size());

    }
}
