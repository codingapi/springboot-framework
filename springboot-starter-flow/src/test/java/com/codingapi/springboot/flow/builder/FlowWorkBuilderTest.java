package com.codingapi.springboot.flow.builder;

import com.codingapi.springboot.flow.context.FlowRepositoryContext;
import com.codingapi.springboot.flow.domain.FlowNode;
import com.codingapi.springboot.flow.domain.FlowRecord;
import com.codingapi.springboot.flow.domain.FlowWork;
import com.codingapi.springboot.flow.em.FlowType;
import com.codingapi.springboot.flow.form.Leave;
import com.codingapi.springboot.flow.matcher.AnyOperatorMatcher;
import com.codingapi.springboot.flow.matcher.IOperatorMatcher;
import com.codingapi.springboot.flow.matcher.SpecifyOperatorMatcher;
import com.codingapi.springboot.flow.repository.*;
import com.codingapi.springboot.flow.trigger.IOutTrigger;
import com.codingapi.springboot.flow.user.User;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FlowWorkBuilderTest {

    @Test
    void builder() {

        UserRepository userRepository = new UserRepository();
        FlowRecordRepository flowRecordRepository = new FlowRecordRepositoryImpl();
        FlowWorkRepository flowWorkRepository = new FlowWorkRepositoryImpl();
        FlowNodeRepository flowNodeRepository = new FlowNodeRepositoryImpl();
        BindDataSnapshotRepository bindDataSnapshotRepository = new BindDataSnapshotRepositoryImpl();

        FlowRepositoryContext.getInstance().bind(userRepository);
        FlowRepositoryContext.getInstance().bind(flowRecordRepository);
        FlowRepositoryContext.getInstance().bind(flowWorkRepository);
        FlowRepositoryContext.getInstance().bind(flowNodeRepository);
        FlowRepositoryContext.getInstance().bind(bindDataSnapshotRepository);

        User admin = new User("admin");
        userRepository.save(admin);

        User user = new User("user");
        userRepository.save(user);

        User depart = new User("depart");
        userRepository.save(depart);

        User boss = new User("boss");
        userRepository.save(boss);

        IOperatorMatcher anyOperatorMatcher = new AnyOperatorMatcher();
        IOperatorMatcher departOperatorMatcher = new SpecifyOperatorMatcher(depart.getId());
        IOperatorMatcher bossOperatorMatcher = new SpecifyOperatorMatcher(boss.getId());

        IOutTrigger userOutTrigger = new IOutTrigger() {
            @Override
            public FlowNode trigger(FlowRecord record) {
                Leave leave = (Leave) record.getBindData();
                if (leave.getLeaveDays() >= 3) {
                    return record.getNextNodeByCode("boss");
                } else {
                    return record.getNextNodeByCode("depart");
                }
            }
        };

        IOutTrigger departOutTrigger = new IOutTrigger() {
            @Override
            public FlowNode trigger(FlowRecord record) {
                return record.getNextNodeByCode("depart");
            }
        };

        IOutTrigger bossOutTrigger = new IOutTrigger() {
            @Override
            public FlowNode trigger(FlowRecord record) {
                return record.getNextNodeByCode("over");
            }
        };

        FlowWork flowWork = FlowWorkBuilder.Builder(admin)
                .title("请假流程")
                .description("请假流程")
                .nodes()
                .node(FlowNodeFactory.Builder(admin).startNode("发起请假",anyOperatorMatcher, userOutTrigger))
                .node(FlowNodeFactory.Builder(admin).node("部门经理审批", "depart", FlowType.NOT_SIGN, departOutTrigger, departOperatorMatcher))
                .node(FlowNodeFactory.Builder(admin).node("总经理审批", "boss", FlowType.NOT_SIGN, bossOutTrigger, bossOperatorMatcher))
                .node(FlowNodeFactory.Builder(admin).overNode("结束"))
                .relations()
                .relation("start", "depart", "boss", "over")
                .relation("start", "boss", "over")
                .build();
        assertNotNull(flowWork);
        assertTrue(flowWork.isEnable());
        assertFalse(flowWork.isLock());

    }


}
