package com.codingapi.springboot.flow.test;

import com.codingapi.springboot.flow.build.FlowWorkBuilder;
import com.codingapi.springboot.flow.content.FlowSession;
import com.codingapi.springboot.flow.domain.FlowWork;
import com.codingapi.springboot.flow.domain.Opinion;
import com.codingapi.springboot.flow.em.ApprovalType;
import com.codingapi.springboot.flow.flow.Leave;
import com.codingapi.springboot.flow.generator.TitleGenerator;
import com.codingapi.springboot.flow.matcher.OperatorMatcher;
import com.codingapi.springboot.flow.repository.UserRepository;
import com.codingapi.springboot.flow.trigger.OutTrigger;
import com.codingapi.springboot.flow.user.User;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ScriptTest {

    private final UserRepository userRepository = new UserRepository();

    @Test
    void test() {
        User user = new User("张三");
        userRepository.save(user);

        OperatorMatcher matcher = OperatorMatcher.anyOperatorMatcher();

        OperatorMatcher operatorMatcher = OperatorMatcher.anyOperatorMatcher();

        TitleGenerator titleGenerator = TitleGenerator.defaultTitleGenerator();

        FlowWork flowWork = FlowWorkBuilder.builder(user)
                .title("请假流程")
                .nodes()
                .node("开始节点", "start", "default", ApprovalType.UN_SIGN, operatorMatcher)
                .node("部门领导审批", "dept", "default", ApprovalType.UN_SIGN, operatorMatcher)
                .node("总经理审批", "manager", "default", ApprovalType.UN_SIGN, operatorMatcher)
                .node("结束节点", "over", "default", ApprovalType.UN_SIGN, operatorMatcher)
                .relations()
                .relation("部门领导审批", "start", "dept")
                .relation("总经理审批", "dept", "manager")
                .relation("结束节点", "manager", "over")
                .build();
        OutTrigger outTrigger =OutTrigger.defaultOutTrigger();
        OperatorMatcher specifyOperatorMatcher = OperatorMatcher.specifyOperatorMatcher(1);

        long now = System.currentTimeMillis();
        Leave leave = new Leave("我要请假");

        FlowSession flowSession = new FlowSession(null,flowWork, flowWork.getNodeByCode("start"), user, user, leave, Opinion.pass("同意"),new ArrayList<>());

        List<Long> ids = matcher.matcher(flowSession);
        assertTrue(ids.contains(user.getUserId()));

        String title = titleGenerator.generate(flowSession);
        assertEquals("张三-请假流程-开始节点", title);

        boolean next = outTrigger.trigger(flowSession);
        assertTrue(next);

        List<Long> userIds = specifyOperatorMatcher.matcher(flowSession);
        assertTrue(userIds.contains(1L));

        long time = System.currentTimeMillis() - now;
        System.out.println("time:" + time);
    }
}
