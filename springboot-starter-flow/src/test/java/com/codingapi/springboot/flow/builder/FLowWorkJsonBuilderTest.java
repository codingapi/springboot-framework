package com.codingapi.springboot.flow.builder;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.junit.jupiter.api.Test;

public class FLowWorkJsonBuilderTest {

    @Test
    void test() {
        String json = "{\"nodes\":[{\"id\":\"e3553275-63ed-4055-b2a9-ef4478c1003b\",\"type\":\"start-node\",\"x\":1092,\"y\":184,\"properties\":{\"name\":\"开始节点\",\"code\":\"start\",\"view\":\"default\",\"outOperatorMatcher\":\"\",\"outTrigger\":\"\"}},{\"id\":\"11b39140-6984-423e-afaa-066bfa882ccc\",\"type\":\"node-node\",\"x\":644,\"y\":503,\"properties\":{\"name\":\"流程节点\",\"code\":\"flow\",\"flowType\":\"SIGN\",\"view\":\"default\",\"outOperatorMatcher\":\"321\",\"outTrigger\":\"123\",\"errTrigger\":\"\",\"errOperatorMatcher\":\"\",\"type\":\"SIGN\"}},{\"id\":\"3692231f-a1f4-4764-8b54-3a0599f1e3e6\",\"type\":\"over-node\",\"x\":1147,\"y\":677,\"properties\":{\"name\":\"结束节点\",\"code\":\"over\",\"view\":\"default\"}},{\"id\":\"72554497-562c-4d62-ae49-231d6343e88e\",\"type\":\"node-node\",\"x\":1323,\"y\":471,\"properties\":{\"name\":\"流程节点\",\"code\":\"flow\",\"flowType\":\"SIGN\",\"view\":\"default\",\"outOperatorMatcher\":\"321\",\"outTrigger\":\"123\",\"errTrigger\":\"\",\"errOperatorMatcher\":\"\",\"type\":\"SIGN\"}}],\"edges\":[]}";

        JSONObject jsonObject = JSONObject.parseObject(json);

        JSONArray jsonArray = jsonObject.getJSONArray("nodes");
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject node = jsonArray.getJSONObject(i);
            System.out.println(node);
        }

    }
}
