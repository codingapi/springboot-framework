package com.codingapi.springboot.framework.rest;

import com.alibaba.fastjson.JSONObject;
import com.codingapi.springboot.framework.rest.param.ApiGetParamBuilder;
import com.codingapi.springboot.framework.rest.param.ApiPostParamBuilder;
import com.codingapi.springboot.framework.rest.properties.RestApiProperties;
import lombok.extern.slf4j.Slf4j;

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
        return httpClient.get(api, paramBuilder!=null?paramBuilder.build():null);
    }

    public String get(String api, ApiGetParamBuilder paramBuilder) {
        for(int i=0; i< RETRY_COUNT; i++){
            try {
                return _get(api, paramBuilder);
            }catch (Exception e){
                log.warn("api:{},error:{}",api,e.getMessage());
                sleep();
            }
        }
        return EMPTY;
    }

    public String get(String api) {
        return get(api,null);
    }

    private String _post(String api, ApiPostParamBuilder paramBuilder) {
        return _post(api, paramBuilder.build());
    }


    private String _post(String api, JSONObject requestBody) {
        return httpClient.post(api, requestBody);
    }


    public String post(String api, JSONObject requestBody) {
        for (int i=0;i< RETRY_COUNT;i++){
            try {
                return _post(api, requestBody);
            }catch (Exception e){
                log.warn("api:{},error:{}",api,e.getMessage());
                sleep();
            }
        }
        return EMPTY;
    }


    public String post(String api, ApiPostParamBuilder paramBuilder) {
        for (int i=0;i< RETRY_COUNT;i++){
            try {
                return _post(api, paramBuilder);
            }catch (Exception e){
                log.warn("api:{},error:{}",api,e.getMessage());
                sleep();
            }
        }
        return EMPTY;
    }


    private void sleep(){
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ex) {
            throw new RuntimeException(ex);
        }
    }

}
