package com.codingapi.springboot.framework.utils;

import org.junit.jupiter.api.Test;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class TrustAnyHttpClientFactoryTest {

    @Test
    void createTrustAnyHttpClient() {
        ClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory(TrustAnyHttpClientFactory.createTrustAnyHttpClient());
        RestTemplate restTemplate = new RestTemplate(factory);
        String response = restTemplate.getForObject("https://www.baidu.com",String.class);
        assertNotNull(response);
    }
}