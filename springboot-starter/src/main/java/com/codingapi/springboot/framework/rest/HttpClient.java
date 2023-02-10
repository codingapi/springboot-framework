package com.codingapi.springboot.framework.rest;

import com.alibaba.fastjson.JSON;
import com.codingapi.springboot.framework.rest.properties.HttpProxyProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URI;
import java.util.Objects;

@Slf4j
public class HttpClient {

    public interface IHttpResponseHandler{
        String toResponse(HttpClient client,URI uri,ResponseEntity<String> response);
    }

    private final RestTemplate restTemplate;

    private final IHttpResponseHandler responseHandler;

    private static final IHttpResponseHandler defaultResponseHandler =  new IHttpResponseHandler() {

        public HttpHeaders copyHeaders(HttpHeaders headers){
            HttpHeaders httpHeaders = new HttpHeaders();
            for (String key:headers.keySet()){
                httpHeaders.set(key, String.join(";", Objects.requireNonNull(headers.get(key))));
            }
            return httpHeaders;
        }

        @Override
        public String toResponse(HttpClient client, URI uri, ResponseEntity<String> response) {
            if(response.getStatusCode().equals(HttpStatus.OK)){
                return response.getBody();
            }
            if(response.getStatusCode().equals(HttpStatus.FOUND)){
                HttpHeaders headers = response.getHeaders();
                String location = Objects.requireNonNull(headers.getLocation()).toString();
                String baseUrl = uri.getScheme() + "://" + uri.getHost()+":"+uri.getPort();
                String url = baseUrl+location;
                return client.get(url,copyHeaders(headers));
            }
            return response.getBody();
        }
    };

    public HttpClient() {
        this(null,defaultResponseHandler);
    }

    public HttpClient(IHttpResponseHandler responseHandler) {
        this(null,responseHandler);
    }

    public HttpClient(HttpProxyProperties properties) {
        this(properties,defaultResponseHandler);
    }

    public HttpClient(HttpProxyProperties properties,IHttpResponseHandler responseHandler) {
        this.responseHandler = responseHandler;
        this.restTemplate = RestTemplateContext.getInstance().getRestTemplate();
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(3000);
        if (properties != null) {
            if (properties.isEnableProxy()) {
                log.info("enable proxy {}//:{}:{}", properties.getProxyType(), properties.getProxyHost(), properties.getProxyPort());
                requestFactory.setProxy(new Proxy(properties.getProxyType(),
                        new InetSocketAddress(properties.getProxyHost(), properties.getProxyPort())));
            }
        }
        this.restTemplate.setRequestFactory(requestFactory);
    }

    public String post(String url, JSON jsonObject) {
        return post(url, new HttpHeaders(), jsonObject);
    }

    public String post(String url, HttpHeaders headers, JSON jsonObject) {
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> httpEntity = new HttpEntity<>(jsonObject.toString(), headers);
        ResponseEntity<String> httpResponse = restTemplate.exchange(url, HttpMethod.POST, httpEntity, String.class);
        return httpResponse.getBody();
    }

    public String post(String url, MultiValueMap<String, String> formData) {
        return post(url,new HttpHeaders(),formData);
    }

    public String post(String url, HttpHeaders headers, MultiValueMap<String, String> formData) {
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(formData, headers);
        UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromHttpUrl(url);
        URI uri = uriComponentsBuilder.build().toUri();
        ResponseEntity<String> httpResponse = restTemplate.exchange(uri, HttpMethod.POST, httpEntity, String.class);
        return responseHandler.toResponse(this,uri,httpResponse);
    }

    public String get(String url, HttpHeaders headers, MultiValueMap<String, String> uriVariables) {
        headers.setContentType(MediaType.APPLICATION_JSON);
        UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromHttpUrl(url);
        if (uriVariables != null) {
            uriComponentsBuilder = uriComponentsBuilder.queryParams(uriVariables);
        }
        URI uri = uriComponentsBuilder.build().toUri();
        HttpEntity<String> httpEntity = new HttpEntity<>(headers);
        ResponseEntity<String> httpResponse = restTemplate.exchange(uri, HttpMethod.GET, httpEntity, String.class);
        return responseHandler.toResponse(this,uri,httpResponse);
    }


    public String get(String url, MultiValueMap<String, String> uriVariables) {
        return get(url, new HttpHeaders(), uriVariables);
    }

    public String get(String url, HttpHeaders headers) {
        return get(url,headers,null);
    }

    public String get(String url) {
        return get(url, new HttpHeaders(), null);
    }

}
