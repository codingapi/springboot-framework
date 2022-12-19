package com.codingapi.springboot.framework.rest;

import com.alibaba.fastjson.JSON;
import com.codingapi.springboot.framework.rest.properties.RestApiProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URI;

@Slf4j
class HttpClient {

    private final RestTemplate restTemplate;

    private final String baseUrl;

    public HttpClient(RestApiProperties properties, String baseUrl) {
        this.baseUrl = baseUrl;
        this.restTemplate = RestTemplateContext.getInstance().getRestTemplate();
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(3000);
        if(properties!=null) {
            if (properties.isEnableProxy()) {
                log.info("enable proxy {}//:{}:{}", properties.getProxyType(), properties.getProxyHost(), properties.getProxyPort());
                requestFactory.setProxy(new Proxy(properties.getProxyType(),
                        new InetSocketAddress(properties.getProxyHost(), properties.getProxyPort())));
            }
        }
        this.restTemplate.setRequestFactory(requestFactory);
    }


    private String buildUrl(String api) {
        return baseUrl+api;
    }

    public String post(String api, JSON jsonObject) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        String url = buildUrl(api);
        HttpEntity<String> httpEntity = new HttpEntity<>(jsonObject.toString(), headers);
        ResponseEntity<String> httpResponse = restTemplate.exchange(url, HttpMethod.POST, httpEntity, String.class);
        return httpResponse.getBody();
    }


    public String get(String api, MultiValueMap<String, String> uriVariables) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        String url = buildUrl(api);
        UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromHttpUrl(url);
        if(uriVariables!=null){
            uriComponentsBuilder = uriComponentsBuilder.queryParams(uriVariables);
        }
        URI uri = uriComponentsBuilder.build().toUri();
        HttpEntity<String> httpEntity = new HttpEntity<>(headers);
        ResponseEntity<String> httpResponse = restTemplate.exchange(uri, HttpMethod.GET, httpEntity, String.class);
        return httpResponse.getBody();
    }
}
