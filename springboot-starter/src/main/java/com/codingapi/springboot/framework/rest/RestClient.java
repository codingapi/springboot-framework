package com.codingapi.springboot.framework.rest;

import com.alibaba.fastjson.JSONObject;
import com.codingapi.springboot.framework.rest.param.ApiGetParamBuilder;
import com.codingapi.springboot.framework.rest.param.ApiPostParamBuilder;
import com.codingapi.springboot.framework.rest.properties.RestApiProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;

@Slf4j
public class RestClient {

    private final HttpClient httpClient;

    private final static int RETRY_COUNT = 5;

    private final static String EMPTY = "{}";

    public RestClient(RestApiProperties restApiProperties, String baseUrl) {
        this.httpClient = new HttpClient(restApiProperties,baseUrl);
    }

    public RestClient(String baseUrl) {
        this.httpClient = new HttpClient(null,baseUrl);
    }

    private String _get(String api, ApiGetParamBuilder paramBuilder) {
        return _get(api,new HttpHeaders(),paramBuilder);
    }

    private String _get(String api,HttpHeaders headers, ApiGetParamBuilder paramBuilder) {
        return httpClient.get(api, headers,paramBuilder!=null?paramBuilder.build():null);
    }

    public String get(String api,HttpHeaders headers, ApiGetParamBuilder paramBuilder) {
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
    public String get(String api, ApiGetParamBuilder paramBuilder) {
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
        return httpClient.post(api,headers, requestBody);
    }

    public String post(String api, JSONObject requestBody) {
        return post(api,new HttpHeaders(),requestBody);
    }

    public String post(String api, ApiPostParamBuilder paramBuilder) {
        return post(api,new HttpHeaders(),paramBuilder.build());
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

    public String post(String api,HttpHeaders headers, ApiPostParamBuilder paramBuilder) {
       return post(api, headers, paramBuilder.build());
    }


    private void sleep(){
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ex) {
            throw new RuntimeException(ex);
        }
    }

}
