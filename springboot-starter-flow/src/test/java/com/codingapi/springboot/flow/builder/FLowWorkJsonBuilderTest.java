package com.codingapi.springboot.flow.builder;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.junit.jupiter.api.Test;

public class FLowWorkJsonBuilderTest {

    @Test
    void test() {
        String json = "{\"nodes\":[{\"id\":\"ec719c39-2244-43cd-b991-753537246d8b\",\"type\":\"start-node\",\"x\":942,\"y\":207,\"properties\":{\"name\":\"开始节点\",\"code\":\"start\",\"view\":\"default\",\"outOperatorMatcher\":\"\",\"outTrigger\":\"\"}},{\"id\":\"92bcc226-ff11-471b-84f8-ca71d1e375ba\",\"type\":\"node-node\",\"x\":520,\"y\":358,\"properties\":{\"name\":\"流程节点\",\"code\":\"flow\",\"flowType\":\"SIGN\",\"view\":\"default\",\"outOperatorMatcher\":\"\",\"outTrigger\":\"\",\"errTrigger\":\"\",\"errOperatorMatcher\":\"\"}},{\"id\":\"cbdda294-7233-4a4c-a74a-52a6dce3ed2e\",\"type\":\"node-node\",\"x\":1296,\"y\":429,\"properties\":{\"name\":\"流程节点\",\"code\":\"flow\",\"flowType\":\"SIGN\",\"view\":\"default\",\"outOperatorMatcher\":\"\",\"outTrigger\":\"\",\"errTrigger\":\"\",\"errOperatorMatcher\":\"\"}},{\"id\":\"f5056801-4fa6-4e4b-9f7f-cfb46bb16487\",\"type\":\"over-node\",\"x\":1024,\"y\":621,\"properties\":{\"name\":\"结束节点\",\"code\":\"over\",\"view\":\"default\"}}],\"edges\":[]}";

        JSONObject jsonObject = JSONObject.parseObject(json);

        JSONArray jsonArray = jsonObject.getJSONArray("nodes");
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject node = jsonArray.getJSONObject(i);
            System.out.println(node);
        }

    }
}
