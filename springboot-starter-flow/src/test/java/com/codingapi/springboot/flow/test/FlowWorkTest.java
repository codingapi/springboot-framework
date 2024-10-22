package com.codingapi.springboot.flow.test;

import com.codingapi.springboot.flow.builder.FlowNodeFactory;
import com.codingapi.springboot.flow.builder.FlowWorkBuilder;
import com.codingapi.springboot.flow.context.FlowRepositoryContext;
import com.codingapi.springboot.flow.domain.FlowNode;
import com.codingapi.springboot.flow.domain.FlowRecord;
import com.codingapi.springboot.flow.domain.FlowWork;
import com.codingapi.springboot.flow.domain.Opinion;
import com.codingapi.springboot.flow.em.FlowStatus;
import com.codingapi.springboot.flow.em.FlowType;
import com.codingapi.springboot.flow.em.NodeStatus;
import com.codingapi.springboot.flow.form.Leave;
import com.codingapi.springboot.flow.matcher.AnyOperatorMatcher;
import com.codingapi.springboot.flow.matcher.IOperatorMatcher;
import com.codingapi.springboot.flow.matcher.ScriptOperatorMatcher;
import com.codingapi.springboot.flow.matcher.SpecifyOperatorMatcher;
import com.codingapi.springboot.flow.repository.*;
import com.codingapi.springboot.flow.trigger.IOutTrigger;
import com.codingapi.springboot.flow.trigger.ScriptOutTrigger;
import com.codingapi.springboot.flow.user.User;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
public class FlowWorkTest {


    @Test
    void scriptFlow() {
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

        IOperatorMatcher anyOperatorMatcher = new ScriptOperatorMatcher(
                """
                return [operator.getId()];
                """);
        IOperatorMatcher departOperatorMatcher = new ScriptOperatorMatcher("""
                return [params[0]];
                """, depart.getId());

        IOperatorMatcher bossOperatorMatcher = new ScriptOperatorMatcher("""
                return [params[0]];
                """, boss.getId());

        IOutTrigger userOutTrigger = new ScriptOutTrigger(
                """
                var leave = record.getBindData();
                if (leave.getLeaveDays() >= 3) {
                    return record.getNextNodeByCode("boss");
                } else {
                    return record.getNextNodeByCode("depart");
                }
                """);

        IOutTrigger departOutTrigger = new ScriptOutTrigger(
                """
                return record.getNextNodeByCode("depart");
                """
        );

        IOutTrigger bossOutTrigger = new ScriptOutTrigger(
                """
                if(record.getOpinion().isPass()){
                    return record.getNextNodeByCode("over");
                }else{
                    return record.getPreNode();
                }
                """);

        FlowWork flowWork = FlowWorkBuilder
                .Builder(admin)
                .title("请假流程")
                .description("请假流程")
                .nodes()
                .node(FlowNodeFactory.Builder(admin).startNode("发起请假", anyOperatorMatcher))
                .node(FlowNodeFactory.Builder(admin).node("部门经理审批", "depart", FlowType.NOT_SIGN, departOperatorMatcher))
                .node(FlowNodeFactory.Builder(admin).node("总经理审批", "boss", FlowType.NOT_SIGN, bossOperatorMatcher))
                .node(FlowNodeFactory.Builder(admin).overNode("结束"))
                .relations()
                .relation("1","start", "depart", userOutTrigger, false)
                .relation("2","start", "boss", userOutTrigger,false)
                .relation("3","depart", "boss", departOutTrigger,false)
                .relation("4","boss", "over", bossOutTrigger,false)
                .build();


        // 创建请假数据
        Leave leave = new Leave(1, "desc", user, 1, "2020-01-01", "2020-01-05");
        log.info("leave days:{}", leave.getLeaveDays());
        // 发起请假流程
        flowWork.createNode(leave, user);

        List<FlowRecord> userRecords = flowRecordRepository.findAllFlowRecordByOperatorId(user.getId());
        // 用户的已办列表
        assertEquals(1, userRecords.size());

        FlowRecord userDoneRecord = userRecords.get(0);

        // 撤回流程
        userDoneRecord.recall();

        // 用户的待办列表
        userRecords = flowRecordRepository.findTodoFlowRecordByOperatorId(user.getId());
        assertEquals(1, userRecords.size());

        // 撤销后的流程
        userDoneRecord = userRecords.get(0);
        // 继续审批
        userDoneRecord.submit(Opinion.pass("同意"));


        // 老板的待办列表
        List<FlowRecord> bossRecords = flowRecordRepository.findTodoFlowRecordByOperatorId(boss.getId());
        assertEquals(1, bossRecords.size());

        FlowRecord bossRecord = bossRecords.get(0);

        assertEquals(bossRecord.getFlowStatus(), FlowStatus.RUNNING);
        assertEquals(bossRecord.getNodeStatus(), NodeStatus.TODO);


        assertEquals(userRecords.get(0).getNodeStatus(), NodeStatus.DONE);

        // 不批准请假
        bossRecord.submit(Opinion.reject("最多给你3天假期"));

        bossRecords = flowRecordRepository.findDoneFlowRecordByOperatorId(boss.getId());
        assertEquals(1, bossRecords.size());

        assertEquals(bossRecord.getNodeStatus(), NodeStatus.DONE);

        // 用户的待办列表
        userRecords = flowRecordRepository.findTodoFlowRecordByOperatorId(user.getId());
        assertEquals(1, userRecords.size());

        FlowRecord userTodoRecord = userRecords.get(0);

        // 用户重新提交
        Leave bindData = (Leave) userTodoRecord.getBindData();
        bindData.setEndDate("2020-01-04");
        userTodoRecord.submit(Opinion.pass("好的，领导，我只请3天假"), bindData);

        // 用户的待办列表
        userRecords = flowRecordRepository.findDoneFlowRecordByOperatorId(user.getId());
        assertEquals(2, userRecords.size());

        bossRecords = flowRecordRepository.findTodoFlowRecordByOperatorId(boss.getId());
        assertEquals(1, bossRecords.size());

        FlowRecord bossTodoRecord = bossRecords.get(0);
        bossTodoRecord.submit(Opinion.pass("好的,批准了"));

        bossRecords = flowRecordRepository.findDoneFlowRecordByOperatorId(boss.getId());
        assertEquals(3, bossRecords.size());

        FlowRecord bossDoneRecord = bossRecords.get(0);
        assertEquals(bossDoneRecord.getNodeStatus(), NodeStatus.DONE);
        assertEquals(bossDoneRecord.getFlowStatus(), FlowStatus.FINISH);

        // 本流程的所有记录
        assertEquals(5, flowRecordRepository.findAllFlowRecordByProcessId(bossRecord.getProcessId()).size());
    }


    @Test
    void interfaceFlow() {
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
                if(record.getOpinion().isPass()) {
                    return record.getNextNodeByCode("over");
                }
                return record.getPreNode();
            }
        };

        FlowWork flowWork = FlowWorkBuilder
                .Builder(admin)
                .title("请假流程")
                .description("请假流程")
                .nodes()
                .node(FlowNodeFactory.Builder(admin).startNode("发起请假", anyOperatorMatcher))
                .node(FlowNodeFactory.Builder(admin).node("部门经理审批", "depart", FlowType.NOT_SIGN, departOperatorMatcher))
                .node(FlowNodeFactory.Builder(admin).node("总经理审批", "boss", FlowType.NOT_SIGN, bossOperatorMatcher))
                .node(FlowNodeFactory.Builder(admin).overNode("结束"))
                .relations()
                .relation("1","start", "depart", userOutTrigger, false)
                .relation("2","start", "boss", userOutTrigger,false)
                .relation("3","depart", "boss", departOutTrigger,false)
                .relation("4","boss", "over", bossOutTrigger,false)
                .build();


        // 创建请假数据
        Leave leave = new Leave(1, "desc", user, 1, "2020-01-01", "2020-01-05");
        log.info("leave days:{}", leave.getLeaveDays());
        // 发起请假流程
        flowWork.createNode(leave, user);

        List<FlowRecord> userRecords = flowRecordRepository.findAllFlowRecordByOperatorId(user.getId());
        // 用户的已办列表
        assertEquals(1, userRecords.size());

        FlowRecord userDoneRecord = userRecords.get(0);

        // 撤回流程
        userDoneRecord.recall();

        // 用户的待办列表
        userRecords = flowRecordRepository.findTodoFlowRecordByOperatorId(user.getId());
        assertEquals(1, userRecords.size());

        // 撤销后的流程
        userDoneRecord = userRecords.get(0);
        // 继续审批
        userDoneRecord.submit(Opinion.pass("同意"));


        // 老板的待办列表
        List<FlowRecord> bossRecords = flowRecordRepository.findTodoFlowRecordByOperatorId(boss.getId());
        assertEquals(1, bossRecords.size());

        FlowRecord bossRecord = bossRecords.get(0);

        assertEquals(bossRecord.getFlowStatus(), FlowStatus.RUNNING);
        assertEquals(bossRecord.getNodeStatus(), NodeStatus.TODO);


        assertEquals(userRecords.get(0).getNodeStatus(), NodeStatus.DONE);

        // 不批准请假
        bossRecord.submit(Opinion.reject("最多给你3天假期"));

        bossRecords = flowRecordRepository.findDoneFlowRecordByOperatorId(boss.getId());
        assertEquals(1, bossRecords.size());

        assertEquals(bossRecord.getNodeStatus(), NodeStatus.DONE);

        // 用户的待办列表
        userRecords = flowRecordRepository.findTodoFlowRecordByOperatorId(user.getId());
        assertEquals(1, userRecords.size());

        FlowRecord userTodoRecord = userRecords.get(0);

        // 用户重新提交
        Leave bindData = (Leave) userTodoRecord.getBindData();
        bindData.setEndDate("2020-01-04");
        userTodoRecord.submit(Opinion.pass("好的，领导，我只请3天假"), bindData);

        // 用户的待办列表
        userRecords = flowRecordRepository.findDoneFlowRecordByOperatorId(user.getId());
        assertEquals(2, userRecords.size());

        bossRecords = flowRecordRepository.findTodoFlowRecordByOperatorId(boss.getId());
        assertEquals(1, bossRecords.size());

        FlowRecord bossTodoRecord = bossRecords.get(0);
        bossTodoRecord.submit(Opinion.pass("好的,批准了"));

        bossRecords = flowRecordRepository.findDoneFlowRecordByOperatorId(boss.getId());
        assertEquals(3, bossRecords.size());

        FlowRecord bossDoneRecord = bossRecords.get(0);
        assertEquals(bossDoneRecord.getNodeStatus(), NodeStatus.DONE);
        assertEquals(bossDoneRecord.getFlowStatus(), FlowStatus.FINISH);

        // 本流程的所有记录
        assertEquals(5, flowRecordRepository.findAllFlowRecordByProcessId(bossRecord.getProcessId()).size());
    }
}
