package com.codingapi.springboot.framework.rest;

import com.alibaba.fastjson.JSON;
import com.codingapi.springboot.framework.rest.param.RestParamBuilder;
import com.codingapi.springboot.framework.rest.properties.HttpProxyProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.MultiValueMap;

@Slf4j
public class RestClient {

    private final HttpHeaders httpHeaders;

    private final HttpClient httpClient;

    private final int retryCount;

    private final String emptyResponse;

    private final String baseUrl;

    public RestClient(HttpProxyProperties httpProxyProperties,
                      String baseUrl,
                      int retryCount,
                      String emptyResponse,
                      HttpClient.IHttpRequestHandler requestHandler,
                      HttpClient.IHttpResponseHandler responseHandler) {
        this.baseUrl = baseUrl;
        this.retryCount = retryCount;
        this.httpClient = new HttpClient(httpProxyProperties,requestHandler,responseHandler);
        this.httpHeaders = new HttpHeaders();
        this.emptyResponse = emptyResponse;
        this.initHeaders();
    }

    public RestClient(String baseUrl) {
        this(null, baseUrl,5,"{}",null,null);
    }

    private void initHeaders() {
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
    }

    private String toUrl(String api) {
        return baseUrl + api;
    }

    public String get(String api, HttpHeaders headers, MultiValueMap<String, String> requestParams) {
        for (int i = 0; i < retryCount; i++) {
            try {
                return httpClient.get(toUrl(api), headers, requestParams);
            } catch (Exception e) {
                log.warn("api:{},error:{}", api, e.getMessage());
                sleep();
            }
        }
        return emptyResponse;
    }

    public String get(String api, HttpHeaders headers, RestParamBuilder paramBuilder) {
        return get(api, headers,paramBuilder!=null?paramBuilder.toFormRequest():null);
    }

    public String get(String api, RestParamBuilder paramBuilder) {
        return get(api, httpHeaders, paramBuilder);
    }

    public String get(String api) {
        return get(api, httpHeaders, (RestParamBuilder) null);
    }

    public String get(String api, HttpHeaders headers) {
        return get(api, headers, (RestParamBuilder) null);
    }

    private String _post(String api, HttpHeaders headers, JSON requestBody) {
        return httpClient.post(toUrl(api), headers, requestBody);
    }

    public String post(String api, JSON requestBody) {
        return post(api, httpHeaders, requestBody);
    }

    public String post(String api, RestParamBuilder paramBuilder) {
        return post(api, httpHeaders, paramBuilder.toJsonRequest());
    }

    public String post(String api, HttpHeaders headers, JSON requestBody) {
        for (int i = 0; i < retryCount; i++) {
            try {
                return _post(api, headers, requestBody);
            } catch (Exception e) {
                log.warn("api:{},error:{}", api, e.getMessage());
                sleep();
            }
        }
        return emptyResponse;
    }

    public String post(String api, HttpHeaders headers, RestParamBuilder paramBuilder) {
        return post(api, headers, paramBuilder.toJsonRequest());
    }


    private void sleep() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ex) {
            throw new RuntimeException(ex);
        }
    }

}
