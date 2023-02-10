package com.codingapi.springboot.framework.rest;

import com.alibaba.fastjson.JSONObject;
import com.codingapi.springboot.framework.rest.param.RestParamBuilder;
import com.codingapi.springboot.framework.rest.properties.HttpProxyProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;

@Slf4j
public class RestClient {

    private final HttpClient httpClient;

    private final static int RETRY_COUNT = 5;

    private final static String EMPTY = "{}";

    private final String baseUrl;

    public RestClient(HttpProxyProperties httpProxyProperties, String baseUrl) {
        this.baseUrl = baseUrl;
        this.httpClient = new HttpClient(httpProxyProperties);
    }

    public RestClient(String baseUrl) {
       this(null,baseUrl);
    }

    private String toUrl(String api) {
        return baseUrl + api;
    }
    private String _get(String api, RestParamBuilder paramBuilder) {
        return _get(api,new HttpHeaders(),paramBuilder);
    }

    private String _get(String api,HttpHeaders headers, RestParamBuilder paramBuilder) {
        return httpClient.get(toUrl(api), headers,paramBuilder!=null?paramBuilder.toFormRequest():null);
    }

    public String get(String api,HttpHeaders headers, RestParamBuilder paramBuilder) {
        for(int i=0; i< RETRY_COUNT; i++){
            try {
                return _get(api,headers, paramBuilder);
            }catch (Exception e){
                log.warn("api:{},error:{}",api,e.getMessage());
                sleep();
            }
        }
        return EMPTY;
    }
    public String get(String api, RestParamBuilder paramBuilder) {
        return get(api,new HttpHeaders(),paramBuilder);
    }

    public String get(String api) {
        return get(api,new HttpHeaders(),null);
    }

    public String get(String api,HttpHeaders headers) {
        return get(api,headers,null);
    }

    private String _post(String api, JSONObject requestBody) {
        return _post(api,new HttpHeaders(),requestBody);
    }

    private String _post(String api, HttpHeaders headers, JSONObject requestBody) {
        return httpClient.post(toUrl(api),headers, requestBody);
    }

    public String post(String api, JSONObject requestBody) {
        return post(api,new HttpHeaders(),requestBody);
    }

    public String post(String api, RestParamBuilder paramBuilder) {
        return post(api,new HttpHeaders(),paramBuilder.toJsonRequest());
    }

    public String post(String api,HttpHeaders headers, JSONObject requestBody) {
        for (int i=0;i< RETRY_COUNT;i++){
            try {
                return _post(api, headers, requestBody);
            }catch (Exception e){
                log.warn("api:{},error:{}",api,e.getMessage());
                sleep();
            }
        }
        return EMPTY;
    }

    public String post(String api,HttpHeaders headers, RestParamBuilder paramBuilder) {
       return post(api, headers, paramBuilder.toJsonRequest());
    }


    private void sleep(){
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ex) {
            throw new RuntimeException(ex);
        }
    }

}
