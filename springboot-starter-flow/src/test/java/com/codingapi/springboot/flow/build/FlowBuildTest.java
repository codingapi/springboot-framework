package com.codingapi.springboot.flow.build;

import com.codingapi.springboot.flow.content.FlowContent;
import com.codingapi.springboot.flow.domain.FlowNode;
import com.codingapi.springboot.flow.domain.FlowWork;
import com.codingapi.springboot.flow.em.ApprovalType;
import com.codingapi.springboot.flow.generator.ITitleGenerator;
import com.codingapi.springboot.flow.matcher.IOperatorMatcher;
import com.codingapi.springboot.flow.repository.UserRepository;
import com.codingapi.springboot.flow.trigger.IOutTrigger;
import com.codingapi.springboot.flow.user.User;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FlowBuildTest {

    private final UserRepository userRepository = new UserRepository();


    @Test
    void build() {
        User user = new User("张三");
        userRepository.save(user);

        ITitleGenerator titleGenerator = new ITitleGenerator() {
            @Override
            public String generate(FlowContent flowContent) {
                User user = (User) flowContent.getCurrentOperator();
                return String.format("%s发起了请假申请", user.getName());
            }
        };

        IOperatorMatcher operatorMatcher = new IOperatorMatcher() {

            @Override
            public List<Long> matcher(FlowContent flowContent) {
                return List.of(flowContent.getCurrentOperator().getUserId());
            }
        };

        IOutTrigger outTrigger = new IOutTrigger() {

            @Override
            public FlowNode trigger(FlowContent flowContent) {
                return null;
            }
        };

        FlowWork flowWork = FlowWorkBuilder.builder(user)
                .title("请假流程")
                .nodes()
                .node("开始节点", "start", "default", ApprovalType.NOT_SIGN, titleGenerator, operatorMatcher)
                .node("部门领导审批", "dept", "default", ApprovalType.NOT_SIGN, titleGenerator, operatorMatcher)
                .node("总经理审批", "manager", "default", ApprovalType.NOT_SIGN, titleGenerator, operatorMatcher)
                .node("结束节点", "over", "default", ApprovalType.NOT_SIGN, titleGenerator, operatorMatcher)
                .relations()
                .relation("部门领导审批", "start", "dept", outTrigger, false)
                .relation("总经理审批", "dept", "manager", outTrigger, false)
                .relation("结束节点", "manager", "over", outTrigger, false)
                .build();
        assertEquals("请假流程", flowWork.getTitle());
        assertEquals(4, flowWork.getNodes().size());
        assertEquals(3, flowWork.getRelations().size());
    }
}
