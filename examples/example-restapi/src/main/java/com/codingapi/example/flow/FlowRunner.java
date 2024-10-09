package com.codingapi.example.flow;

import com.alibaba.fastjson.JSONObject;
import com.codingapi.example.domain.Leave;
import com.codingapi.example.domain.User;
import com.codingapi.example.gateway.PasswordEncoder;
import com.codingapi.example.repository.UserRepository;
import com.codingapi.springboot.flow.builder.FlowNodeCreator;
import com.codingapi.springboot.flow.builder.FlowWorkBuilder;
import com.codingapi.springboot.flow.domain.FlowNode;
import com.codingapi.springboot.flow.domain.FlowRecord;
import com.codingapi.springboot.flow.domain.FlowWork;
import com.codingapi.springboot.flow.domain.Opinion;
import com.codingapi.springboot.flow.em.FlowStatus;
import com.codingapi.springboot.flow.em.NodeStatus;
import com.codingapi.springboot.flow.repository.FlowRecordRepository;
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

        String schema = "{\"nodes\":[{\"id\":\"6fd8585a-2d74-4d82-a6bc-ff6b52a542fe\",\"type\":\"start-node\",\"x\":934,\"y\":183,\"properties\":{\"name\":\"开始节点\",\"code\":\"start\",\"type\":\"NO_SIGN\",\"view\":\"default\",\"outOperatorMatcher\":\" return [operator.getId()];\",\"outTrigger\":\"var leave = record.getBindData();\\nif (leave.getLeaveDays() >= 3) {\\n    return record.getNextNodeByCode(\\\"boss\\\");\\n} else {\\n    return record.getNextNodeByCode(\\\"depart\\\");\\n}\"}},{\"id\":\"45d491c6-e8cf-4658-b5fd-3daf70ec0bd5\",\"type\":\"node-node\",\"x\":668,\"y\":364,\"properties\":{\"name\":\"部门经理审批\",\"code\":\"depart\",\"type\":\"NO_SIGN\",\"view\":\"default\",\"outOperatorMatcher\":\"return [3];\",\"outTrigger\":\" return record.getNextNodeByCode(\\\"depart\\\");\",\"errTrigger\":\"\",\"errOperatorMatcher\":\"\"}},{\"id\":\"c429698f-f17a-472a-81f0-7b62e540463b\",\"type\":\"over-node\",\"x\":986,\"y\":669,\"properties\":{\"name\":\"结束节点\",\"code\":\"over\",\"type\":\"NO_SIGN\",\"view\":\"default\"}},{\"id\":\"30910885-3326-4156-9e29-3299e2c49190\",\"type\":\"node-node\",\"x\":965,\"y\":445,\"properties\":{\"name\":\"总经理审批\",\"code\":\"boss\",\"type\":\"NO_SIGN\",\"view\":\"default\",\"outOperatorMatcher\":\"return [4];\",\"outTrigger\":\"if(record.getOpinion().isPass()){\\n    return record.getNextNodeByCode(\\\"over\\\");\\n}else{\\n    return record.getPreNode();\\n}\",\"errTrigger\":\"\",\"errOperatorMatcher\":\"\"}}],\"edges\":[{\"id\":\"ce303095-00b4-42da-b9e4-19fcfd096c91\",\"type\":\"bezier\",\"properties\":{},\"sourceNodeId\":\"6fd8585a-2d74-4d82-a6bc-ff6b52a542fe\",\"targetNodeId\":\"45d491c6-e8cf-4658-b5fd-3daf70ec0bd5\",\"startPoint\":{\"x\":934,\"y\":205.5},\"endPoint\":{\"x\":668,\"y\":341.5},\"pointsList\":[{\"x\":934,\"y\":205.5},{\"x\":934,\"y\":305.5},{\"x\":668,\"y\":241.5},{\"x\":668,\"y\":341.5}]},{\"id\":\"d85b3419-a6ba-439d-a364-7d563877d786\",\"type\":\"bezier\",\"properties\":{},\"sourceNodeId\":\"6fd8585a-2d74-4d82-a6bc-ff6b52a542fe\",\"targetNodeId\":\"30910885-3326-4156-9e29-3299e2c49190\",\"startPoint\":{\"x\":934,\"y\":205.5},\"endPoint\":{\"x\":965,\"y\":422.5},\"pointsList\":[{\"x\":934,\"y\":205.5},{\"x\":934,\"y\":305.5},{\"x\":965,\"y\":322.5},{\"x\":965,\"y\":422.5}]},{\"id\":\"a2b3e4d8-924f-4187-a1f7-581421d05c8e\",\"type\":\"bezier\",\"properties\":{},\"sourceNodeId\":\"30910885-3326-4156-9e29-3299e2c49190\",\"targetNodeId\":\"c429698f-f17a-472a-81f0-7b62e540463b\",\"startPoint\":{\"x\":965,\"y\":467.5},\"endPoint\":{\"x\":986,\"y\":646.5},\"pointsList\":[{\"x\":965,\"y\":467.5},{\"x\":965,\"y\":567.5},{\"x\":986,\"y\":546.5},{\"x\":986,\"y\":646.5}]},{\"id\":\"1ae8f9a8-8146-4826-a27e-b721e875bc0e\",\"type\":\"bezier\",\"properties\":{},\"sourceNodeId\":\"45d491c6-e8cf-4658-b5fd-3daf70ec0bd5\",\"targetNodeId\":\"30910885-3326-4156-9e29-3299e2c49190\",\"startPoint\":{\"x\":768,\"y\":364},\"endPoint\":{\"x\":865,\"y\":445},\"pointsList\":[{\"x\":768,\"y\":364},{\"x\":868,\"y\":364},{\"x\":765,\"y\":445},{\"x\":865,\"y\":445}]}]}";
        System.out.println(schema);
        List<FlowNode> nodes  = FlowNodeCreator.Builder(admin).create(JSONObject.parseObject(schema));
        FlowWork flowWork = FlowWorkBuilder.Builder(admin)
                .title("请假流程")
                .description("请假流程")
                .schema(schema)
                .nodes(nodes)
                .build();

        // 创建请假数据
        Leave leave = new Leave(1,
                "desc", user, 1, "2020-01-01", "2020-01-05");
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
        System.out.println(bindData.getLeaveDays());
        userTodoRecord.submit(Opinion.pass("好的，领导，我只请3天假"), bindData);

        // 用户的已待列表
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
            throw new RuntimeException("value:"+value+" ,targetValue:"+targetValue);
        }
    }
}
