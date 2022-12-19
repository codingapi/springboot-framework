package com.codingapi.springboot.framework.rest;


import com.codingapi.springboot.framework.utils.TrustAnyHttpClientFactory;
import lombok.Getter;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

class RestTemplateContext {

    private static RestTemplateContext instance;
    @Getter
    private final RestTemplate restTemplate;

    private RestTemplateContext(){
        this.restTemplate = restTemplate(new HttpComponentsClientHttpRequestFactory(
                TrustAnyHttpClientFactory.createTrustAnyHttpClient()));
    }

    public static RestTemplateContext getInstance() {
        if (instance == null) {
            synchronized (RestTemplateContext.class) {
                if (instance == null) {
                    try {
                        instance = new RestTemplateContext();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
        return instance;
    }

    RestTemplate restTemplate(ClientHttpRequestFactory factory) {
        return new RestTemplate(factory);
    }

}
