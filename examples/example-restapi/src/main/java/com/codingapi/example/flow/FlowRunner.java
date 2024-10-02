package com.codingapi.example.flow;

import com.codingapi.example.domain.User;
import com.codingapi.example.repository.UserRepository;
import com.codingapi.springboot.flow.builder.FlowNodeFactory;
import com.codingapi.springboot.flow.builder.FlowWorkBuilder;
import com.codingapi.springboot.flow.domain.FlowWork;
import com.codingapi.springboot.flow.em.FlowType;
import com.codingapi.springboot.flow.matcher.IOperatorMatcher;
import com.codingapi.springboot.flow.matcher.ScriptOperatorMatcher;
import com.codingapi.springboot.flow.repository.FlowWorkRepository;
import com.codingapi.springboot.flow.trigger.IOutTrigger;
import com.codingapi.springboot.flow.trigger.ScriptOutTrigger;
import lombok.AllArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class FlowRunner implements ApplicationRunner {

    private final UserRepository userRepository;
    private final FlowWorkRepository flowWorkRepository;

    @Override
    public void run(ApplicationArguments args) throws Exception {
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
                .node(FlowNodeFactory.Builder(admin).startNode("发起请假", anyOperatorMatcher, userOutTrigger))
                .node(FlowNodeFactory.Builder(admin).node("部门经理审批", "depart", FlowType.NOT_SIGN, departOutTrigger, departOperatorMatcher))
                .node(FlowNodeFactory.Builder(admin).node("总经理审批", "boss", FlowType.NOT_SIGN, bossOutTrigger, bossOperatorMatcher))
                .node(FlowNodeFactory.Builder(admin).overNode("结束"))
                .relations()
                .relation("start", "depart", "boss", "over")
                .relation("start", "boss", "over")
                .build();

        flowWorkRepository.save(flowWork);
    }
}
