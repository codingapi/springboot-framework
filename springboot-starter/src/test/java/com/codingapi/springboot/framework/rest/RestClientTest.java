package com.codingapi.springboot.framework.rest;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.codingapi.springboot.framework.rest.param.RestParam;
import com.codingapi.springboot.framework.rest.properties.HttpProxyProperties;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;

import java.net.Proxy;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
class RestClientTest {

    /**
     * 依赖外部网络与本地代理（127.0.0.1:7890）的集成用例：
     * 代理缺失、网络受限或外部接口限流时跳过，不作为 CI 强制断言。
     */
    @Test
    void okxTest() {
        String baseUrl = "https://www.okx.com/";
        HttpProxyProperties proxyProperties = new HttpProxyProperties();
        proxyProperties.setEnableProxy(true);
        proxyProperties.setProxyType(Proxy.Type.HTTP);
        proxyProperties.setProxyHost("127.0.0.1");
        proxyProperties.setProxyPort(7890);
        RestClient restClient = new RestClient(proxyProperties,baseUrl,5,"{}",null,null);
        String response;
        try {
            response = restClient.get("api/v5/market/candles", RestParam.create()
                    .add("instId","BTC-USDT")
                    .add("bar","1m")
                    .add("limit","300")
            );
        } catch (Exception e) {
            Assumptions.assumeTrue(false, "OKX 外部接口不可用（本地代理 127.0.0.1:7890 缺失或网络受限），跳过用例: " + e.getMessage());
            return;
        }
        log.info("response:{}",response);
        JSONObject jsonObject = JSONObject.parseObject(response);
        JSONArray data = jsonObject == null ? null : jsonObject.getJSONArray("data");
        if (data == null) {
            Assumptions.assumeTrue(false, "OKX 外部接口响应无 data 数据（网络受限或限流），跳过用例");
            return;
        }
        assertEquals(300, data.size());
    }
}
