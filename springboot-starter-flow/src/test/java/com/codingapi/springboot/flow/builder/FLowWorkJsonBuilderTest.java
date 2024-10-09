package com.codingapi.springboot.flow.builder;

import com.alibaba.fastjson.JSONObject;
import com.codingapi.springboot.flow.domain.FlowWork;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FLowWorkJsonBuilderTest {

    @Test
    void test() {
        String json = "{\"nodes\":[{\"id\":\"ed0db7f9-6b71-4d94-8145-183339b2d409\",\"type\":\"start-node\",\"x\":831,\"y\":159,\"properties\":{\"name\":\"开始节点\",\"code\":\"start\",\"type\":\"NO_SIGN\",\"view\":\"default\",\"outOperatorMatcher\":\"\",\"outTrigger\":\"\"}},{\"id\":\"1da7e0aa-dea0-45d0-a8f8-63682c41f348\",\"type\":\"node-node\",\"x\":459,\"y\":269,\"properties\":{\"name\":\"流程节点\",\"code\":\"flow\",\"type\":\"NO_SIGN\",\"view\":\"default\",\"outOperatorMatcher\":\"\",\"outTrigger\":\"\",\"errTrigger\":\"\",\"errOperatorMatcher\":\"\"}},{\"id\":\"d69ae42a-1f5b-45d5-8a7d-a4232fefa82c\",\"type\":\"over-node\",\"x\":657,\"y\":420,\"properties\":{\"name\":\"结束节点\",\"code\":\"over\",\"type\":\"NO_SIGN\",\"view\":\"default\"}}],\"edges\":[{\"id\":\"32069ec0-826a-4a95-bf98-e94a68206e19\",\"type\":\"bezier\",\"properties\":{},\"sourceNodeId\":\"ed0db7f9-6b71-4d94-8145-183339b2d409\",\"targetNodeId\":\"1da7e0aa-dea0-45d0-a8f8-63682c41f348\",\"startPoint\":{\"x\":831,\"y\":181.5},\"endPoint\":{\"x\":459,\"y\":291.5},\"pointsList\":[{\"x\":831,\"y\":181.5},{\"x\":831,\"y\":281.5},{\"x\":459,\"y\":391.5},{\"x\":459,\"y\":291.5}]},{\"id\":\"d7b874e1-013b-4918-a431-bad75e29cb2b\",\"type\":\"bezier\",\"properties\":{},\"sourceNodeId\":\"1da7e0aa-dea0-45d0-a8f8-63682c41f348\",\"targetNodeId\":\"d69ae42a-1f5b-45d5-8a7d-a4232fefa82c\",\"startPoint\":{\"x\":459,\"y\":246.5},\"endPoint\":{\"x\":657,\"y\":397.5},\"pointsList\":[{\"x\":459,\"y\":246.5},{\"x\":459,\"y\":146.5},{\"x\":657,\"y\":297.5},{\"x\":657,\"y\":397.5}]}]}";

        JSONObject jsonObject = JSONObject.parseObject(json);

        FlowWork flowWork =  FlowWorkJsonBuilder.Builder(null).build(jsonObject);

        assertEquals(3, flowWork.getNodes().size());

    }
}
