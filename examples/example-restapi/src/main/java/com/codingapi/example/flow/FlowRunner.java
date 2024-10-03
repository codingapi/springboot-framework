package com.codingapi.example.flow;

import com.codingapi.example.domain.Leave;
import com.codingapi.example.domain.User;
import com.codingapi.example.gateway.PasswordEncoder;
import com.codingapi.example.repository.UserRepository;
import com.codingapi.springboot.flow.builder.FlowNodeFactory;
import com.codingapi.springboot.flow.builder.FlowWorkBuilder;
import com.codingapi.springboot.flow.domain.FlowRecord;
import com.codingapi.springboot.flow.domain.FlowWork;
import com.codingapi.springboot.flow.domain.Opinion;
import com.codingapi.springboot.flow.em.FlowStatus;
import com.codingapi.springboot.flow.em.FlowType;
import com.codingapi.springboot.flow.em.NodeStatus;
import com.codingapi.springboot.flow.matcher.IOperatorMatcher;
import com.codingapi.springboot.flow.matcher.ScriptOperatorMatcher;
import com.codingapi.springboot.flow.repository.FlowRecordRepository;
import com.codingapi.springboot.flow.trigger.IOutTrigger;
import com.codingapi.springboot.flow.trigger.ScriptOutTrigger;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@AllArgsConstructor
public class FlowRunner implements ApplicationRunner {

    private final UserRepository userRepository;
    private final FlowRecordRepository flowRecordRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        User admin = new User("admin",passwordEncoder);
        userRepository.save(admin);

        User user = new User("user",passwordEncoder);
        userRepository.save(user);

        User depart = new User("depart",passwordEncoder);
        userRepository.save(depart);

        User boss = new User("boss",passwordEncoder);
        userRepository.save(boss);

        IOperatorMatcher anyOperatorMatcher = new ScriptOperatorMatcher(
                """
                return [operator.getId()];
                """);
        IOperatorMatcher departOperatorMatcher = new ScriptOperatorMatcher("""
                return [{departId}];
                """.replaceAll("\\{departId}",String.valueOf(depart.getId())));

        IOperatorMatcher bossOperatorMatcher = new ScriptOperatorMatcher("""
                return [{bossId}];
                """.replaceAll("\\{bossId}",String.valueOf(boss.getId())));

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
                .node(FlowNodeFactory.Builder(admin).startNode("发起请假", anyOperatorMatcher, userOutTrigger))
                .node(FlowNodeFactory.Builder(admin).node("部门经理审批", "depart", FlowType.NOT_SIGN, departOutTrigger, departOperatorMatcher))
                .node(FlowNodeFactory.Builder(admin).node("总经理审批", "boss", FlowType.NOT_SIGN, bossOutTrigger, bossOperatorMatcher))
                .node(FlowNodeFactory.Builder(admin).overNode("结束"))
                .relations()
                .relation("start", "depart", "boss", "over")
                .relation("start", "boss", "over")
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

    private void assertEquals(Object value, Object targetValue) {
        if(value!=targetValue){
            throw new RuntimeException("value:"+value+" targetValue:"+targetValue);
        }
    }
}
