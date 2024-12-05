package com.codingapi.springboot.flow.test;

import com.codingapi.springboot.flow.build.FlowWorkBuilder;
import com.codingapi.springboot.flow.domain.FlowWork;
import com.codingapi.springboot.flow.em.ApprovalType;
import com.codingapi.springboot.flow.matcher.OperatorMatcher;
import com.codingapi.springboot.flow.repository.UserRepository;
import com.codingapi.springboot.flow.serializable.FlowWorkSerializable;
import com.codingapi.springboot.flow.user.User;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BuildTest {

    private final UserRepository userRepository = new UserRepository();


    @Test
    void build() {
        User user = new User("张三");
        userRepository.save(user);

        FlowWork flowWork = FlowWorkBuilder.builder(user)
                .title("请假流程")
                .nodes()
                .node("开始节点", "start", "default", ApprovalType.UN_SIGN, OperatorMatcher.anyOperatorMatcher())
                .node("部门领导审批", "dept", "default", ApprovalType.UN_SIGN, OperatorMatcher.anyOperatorMatcher())
                .node("总经理审批", "manager", "default", ApprovalType.UN_SIGN, OperatorMatcher.anyOperatorMatcher())
                .node("抄送节点", "circulate", "default", ApprovalType.CIRCULATE, OperatorMatcher.anyOperatorMatcher())
                .node("结束节点", "over", "default", ApprovalType.UN_SIGN, OperatorMatcher.anyOperatorMatcher())

                .relations()
                .relation("部门领导审批", "start", "dept")
                .relation("总经理审批", "dept", "manager")
                .relation("抄送节点", "manager", "circulate")
                .relation("结束节点", "circulate", "over")
                .build();
        assertEquals("请假流程", flowWork.getTitle());
        assertEquals(5, flowWork.getNodes().size());
        assertEquals(4, flowWork.getRelations().size());


        byte[] bytes = flowWork.toSerializable().toSerializable();
        FlowWorkSerializable flowWorkSerializable = FlowWorkSerializable.fromSerializable(bytes);
        assertEquals("请假流程", flowWorkSerializable.getTitle());

        FlowWork serializableWork = flowWorkSerializable.toFlowWork(userRepository);
        assertEquals("请假流程", serializableWork.getTitle());

    }
}
