package com.codingapi.springboot.framework.rest;

import com.alibaba.fastjson.JSONObject;
import com.codingapi.springboot.framework.rest.param.RestParam;
import com.codingapi.springboot.framework.rest.properties.HttpProxyProperties;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;

import java.net.Proxy;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
class RestClientTest {

    @Test
    void okxTest() {
        String baseUrl = "https://www.okx.com/";
        HttpProxyProperties proxyProperties = new HttpProxyProperties();
        proxyProperties.setEnableProxy(true);
        proxyProperties.setProxyType(Proxy.Type.HTTP);
        proxyProperties.setProxyHost("127.0.0.1");
        proxyProperties.setProxyPort(7890);

        HttpHeaders headers = new HttpHeaders();
        headers.set("x-simulated-trading","1");
        headers.set("User-Agent", "Application");
        RestClient restClient = new RestClient(proxyProperties,baseUrl,5,"{}",null,null);
        String response = restClient.get(
                "api/v5/market/candles",
                headers,
                RestParam.create()
                    .add("instId","BTC-USDT")
                    .add("bar","1m")
                    .add("limit","300")
        );
        log.info("response:{}",response);
        JSONObject jsonObject = JSONObject.parseObject(response);
        assertEquals(jsonObject.getJSONArray("data").size(),300);
    }
}
