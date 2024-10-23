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

public class FlowTest {

    private final UserRepository userRepository = new UserRepository();
    private final FlowWorkRepository flowWorkRepository = new FlowWorkRepositoryImpl();
    private final FlowRecordRepository flowRecordRepository = new FlowRecordRepositoryImpl();
    private final FlowBindDataRepository flowBindDataRepository = new FlowBindDataRepositoryImpl();
    private final LeaveRepository leaveRepository = new LeaveRepository();
    private final FlowService flowService = new FlowService(flowWorkRepository, flowRecordRepository, flowBindDataRepository);


    @Test
    void test() {
        User user = new User("张三");
        userRepository.save(user);

        FlowWork flowWork = FlowWorkBuilder.builder(user)
                .title("请假流程")
                .nodes()
                .node("开始节点", "start", "default", ApprovalType.NOT_SIGN, OperatorMatcher.anyOperatorMatcher())
                .node("部门领导审批", "dept", "default", ApprovalType.NOT_SIGN, OperatorMatcher.anyOperatorMatcher())
                .node("总经理审批", "manager", "default", ApprovalType.NOT_SIGN, OperatorMatcher.anyOperatorMatcher())
                .node("结束节点", "over", "default", ApprovalType.NOT_SIGN, OperatorMatcher.anyOperatorMatcher())
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

    }
}
