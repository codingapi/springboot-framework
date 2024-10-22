package com.codingapi.springboot.flow.test;

import com.codingapi.springboot.flow.build.FlowWorkBuilder;
import com.codingapi.springboot.flow.content.FlowContent;
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

        TitleGenerator titleGenerator = TitleGenerator.defaultTitleGenerator();

        OperatorMatcher operatorMatcher = OperatorMatcher.anyOperatorMatcher();

        FlowWork flowWork = FlowWorkBuilder.builder(user)
                .title("请假流程")
                .nodes()
                .node("开始节点", "start", "default", ApprovalType.NOT_SIGN, titleGenerator, operatorMatcher)
                .node("部门领导审批", "dept", "default", ApprovalType.NOT_SIGN, titleGenerator, operatorMatcher)
                .node("总经理审批", "manager", "default", ApprovalType.NOT_SIGN, titleGenerator, operatorMatcher)
                .node("结束节点", "over", "default", ApprovalType.NOT_SIGN, titleGenerator, operatorMatcher)
                .relations()
                .relation("部门领导审批", "start", "dept", false)
                .relation("总经理审批", "dept", "manager", false)
                .relation("结束节点", "manager", "over", false)
                .build();
        String target = "dept";
        OutTrigger outTrigger = new OutTrigger("def run(content){return '" + target + "';}");
        OperatorMatcher specifyOperatorMatcher = OperatorMatcher.specifyOperatorMatcher(1);

        long now = System.currentTimeMillis();
        Leave leave = new Leave("我要请假");

        FlowContent flowContent = new FlowContent(flowWork, flowWork.getNodeByCode("start"), user, user, leave, Opinion.success("同意"));

        List<Long> ids = matcher.matcher(flowContent);
        assertTrue(ids.contains(user.getUserId()));

        String title = titleGenerator.generate(flowContent);
        assertEquals("张三-请假流程-开始节点", title);

        String next = outTrigger.trigger(flowContent);
        assertEquals(target, next);

        List<Long> userIds = specifyOperatorMatcher.matcher(flowContent);
        assertTrue(userIds.contains(1L));

        long time = System.currentTimeMillis() - now;
        System.out.println("time:" + time);
    }
}
