---
name: httpclient5
description: HTTP 客户端库，支持 HTTPS 信任所有证书的请求
status: 已实现
scope: 后端
source: 框架:Apache HttpClient 5
---

## 解决什么问题

框架需要发起外部 HTTP/HTTPS 请求的场景：
- 对接第三方服务时需要跳过 SSL 证书验证（开发/测试环境）
- 通过 `RestTemplate` 发送 HTTP 请求时底层使用高性能连接管理
- 支持代理配置的企业内网环境

Apache HttpClient 5 提供了成熟的 HTTP 客户端实现，项目中主要用于创建信任所有证书的 `HttpClient` 实例，作为 Spring `RestTemplate` 的底层传输层。

## 如何使用

### 依赖引入

```xml
<!-- pom.xml 中已声明 -->
<dependency>
    <groupId>org.apache.httpcomponents.client5</groupId>
    <artifactId>httpclient5</artifactId>
</dependency>
```

### 核心类

- `TrustAnyHttpClientFactory` — 创建信任所有证书的 HttpClient 工厂
- `RestTemplateContext` — 基于 HttpClient 5 的 RestTemplate 单例上下文
- `HttpClient` — 框架封装的 HTTP 客户端（GET/POST）
- `HttpRequest` — 底层请求执行器（支持代理、重定向）

## 使用实例

### 1. 创建信任所有证书的 HttpClient

```java
// TrustAnyHttpClientFactory.java
import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.ssl.NoopHostnameVerifier;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactory;

public class TrustAnyHttpClientFactory {

    public static HttpClient createTrustAnyHttpClient() {
        // 创建信任所有证书的 SSLContext
        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, new TrustManager[]{new TrustAnyTrustManager()}, null);

        // 使用 NoopHostnameVerifier 跳过主机名验证
        SSLConnectionSocketFactory sslSocketFactory =
            new SSLConnectionSocketFactory(sslContext, NoopHostnameVerifier.INSTANCE);

        // 注册 HTTP 和 HTTPS 协议
        Registry<ConnectionSocketFactory> registry = RegistryBuilder
            .<ConnectionSocketFactory>create()
            .register("https", sslSocketFactory)
            .register("http", new PlainConnectionSocketFactory())
            .build();

        BasicHttpClientConnectionManager connectionManager =
            new BasicHttpClientConnectionManager(registry);

        return HttpClients.custom()
            .setDefaultRequestConfig(RequestConfig.custom()
                .setCircularRedirectsAllowed(true).build())
            .setConnectionManager(connectionManager)
            .build();
    }
}
```

### 2. RestTemplate 集成

```java
// RestTemplateContext.java - 使用 HttpClient 5 作为底层传输
public class RestTemplateContext {
    private static RestTemplateContext instance;
    private final RestTemplate restTemplate;

    private RestTemplateContext() {
        // 使用 Apache HttpClient 5 创建请求工厂
        HttpComponentsClientHttpRequestFactory requestFactory =
            new HttpComponentsClientHttpRequestFactory(
                TrustAnyHttpClientFactory.createTrustAnyHttpClient()
            );
        this.restTemplate = new RestTemplate(requestFactory);
    }

    public static RestTemplateContext getInstance() { /* 双重检查锁单例 */ }
}
```

### 3. 框架封装的 HttpClient

```java
// HttpClient.java - 简洁的 HTTP 调用 API
HttpClient client = new HttpClient();

// POST JSON 请求
String result = client.post(url, headers, jsonObject);

// POST 表单请求
String result = client.post(url, headers, formData);

// GET 请求
String result = client.get(url, headers, uriVariables);
```

### 涉及模块

| 模块 | 使用场景 |
|------|----------|
| springboot-starter | `TrustAnyHttpClientFactory`、`RestTemplateContext`、`HttpClient`、`HttpRequest` |
