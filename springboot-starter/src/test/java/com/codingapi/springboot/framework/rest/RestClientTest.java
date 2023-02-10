package com.codingapi.springboot.framework.rest;

import com.alibaba.fastjson.JSONObject;
import com.codingapi.springboot.framework.rest.param.RestParamBuilder;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
class RestClientTest {

    @Test
    void baikeTest() {
        String baseUrl = "http://baike.baidu.com/";
        RestClient restClient = new RestClient(baseUrl);
        String response = restClient.get("/api/openapi/BaikeLemmaCardApi", RestParamBuilder.create()
                .add("scope","103").add("format","json")
                .add("appid","379020").add("bk_key","关键字")
        );
        log.info("response:{}",response);
        JSONObject jsonObject = JSONObject.parseObject(response);
        log.info("desc:{}",jsonObject.getString("desc"));
        assertEquals(jsonObject.getString("key"),"关键字");
        assertTrue(response.contains("id"));
    }
}