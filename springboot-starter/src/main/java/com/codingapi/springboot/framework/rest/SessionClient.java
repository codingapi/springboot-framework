package com.codingapi.springboot.framework.rest;

import com.codingapi.springboot.framework.rest.param.RestParamBuilder;
import com.codingapi.springboot.framework.rest.properties.HttpProxyProperties;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.net.URI;
import java.util.Objects;

public class SessionClient {

    private final HttpClient httpClient;

    private final HttpHeaders httpHeaders;

    public SessionClient(HttpProxyProperties properties) {
        HttpClient.IHttpResponseHandler responseHandler = new HttpClient.IHttpResponseHandler() {

            public HttpHeaders copyHeaders(HttpHeaders headers) {
                for (String key : headers.keySet()) {
                    httpHeaders.set(key, String.join(";", Objects.requireNonNull(headers.get(key))));
                }
                return httpHeaders;
            }

            @Override
            public String toResponse(HttpClient client, URI uri, ResponseEntity<String> response) {
                if (response.getStatusCode().equals(HttpStatus.OK)) {
                    return response.getBody();
                }

                if(response.getStatusCode().equals(HttpStatus.NOT_FOUND)){
                    return response.getBody();
                }

                if (response.getStatusCode().equals(HttpStatus.FOUND)) {
                    HttpHeaders headers = response.getHeaders();
                    String location = Objects.requireNonNull(headers.getLocation()).toString();
                    String baseUrl = uri.getScheme() + "://" + uri.getHost() + ":" + uri.getPort();
                    String url = baseUrl + location;
                    return client.get(url, copyHeaders(headers),null);
                }
                return response.getBody();
            }
        };
        this.httpClient = new HttpClient(properties, responseHandler);
        this.httpHeaders = new HttpHeaders();
    }

    public SessionClient(){
        this(null);
    }

    public SessionClient addHeader(String key, String value){
        this.httpHeaders.add(key, value);
        return this;
    }

    public String post(String url, RestParamBuilder restParam){
        return httpClient.post(url,httpHeaders,restParam.toFormRequest());
    }

    public String get(String url){
        return httpClient.get(url,httpHeaders,null);
    }

}
