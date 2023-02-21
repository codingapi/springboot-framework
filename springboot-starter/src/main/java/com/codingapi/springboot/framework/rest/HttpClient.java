package com.codingapi.springboot.framework.rest;

import com.alibaba.fastjson.JSON;
import com.codingapi.springboot.framework.rest.properties.HttpProxyProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.util.MultiValueMap;

@Slf4j
public class HttpClient {

    private final HttpRequest httpRequest;

    public HttpClient() {
        this.httpRequest = new HttpRequest();
    }

    public HttpClient(HttpRequest.IHttpRequestHandler requestHandler, HttpRequest.IHttpResponseHandler responseHandler) {
        this.httpRequest = new HttpRequest(requestHandler,responseHandler);
    }

    public HttpClient(HttpProxyProperties properties) {
        this.httpRequest = new HttpRequest(properties);
    }

    public HttpClient(HttpProxyProperties properties, HttpRequest.IHttpRequestHandler requestHandler, HttpRequest.IHttpResponseHandler responseHandler) {
        this.httpRequest = new HttpRequest(properties, requestHandler, responseHandler);
    }

    public String post(String url, HttpHeaders headers, JSON jsonObject) {
        return httpRequest.getPostRequest(url, headers, jsonObject).execute();
    }

    public String post(String url, HttpHeaders headers, MultiValueMap<String, String> formData) {
        return httpRequest.getPostRequest(url, headers, formData).execute();
    }

    public String get(String url, HttpHeaders headers, MultiValueMap<String, String> uriVariables) {
        return httpRequest.getGetRequest(url, headers, uriVariables).execute();
    }


}
