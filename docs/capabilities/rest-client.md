---
name: rest-client
description: REST HTTP 客户端封装，支持自动重试、代理配置、请求/响应拦截器与信任所有证书模式
status: 已实现
scope: 后端
source: 项目自有
import: com.codingapi.springboot:springboot-starter
symbols:
  - RestClient
  - HttpClient
  - HttpRequest
  - Request
  - RestTemplateContext
  - SessionClient
  - IRestParam
  - RestParam
  - HttpProxyProperties
  - TrustAnyHttpClientFactory
content_hash: 7c8d0b2743ca064f6b334c28b0fc2565f210b4898903f3cf030cf8613ff95854
---

## 解决什么问题

微服务之间或调用第三方 API 时，需要封装 HTTP 请求。Spring RestTemplate 原生使用不够便捷。本能力提供了更友好的 REST 客户端：

- **自动重试**：`RestClient` 内置重试机制，失败后自动重试（默认5次）
- **请求拦截器**：支持请求前/响应后的钩子处理（如自动添加 Header、日志记录）
- **代理支持**：通过 `HttpProxyProperties` 配置 HTTP 代理
- **信任所有证书**：`TrustAnyHttpClientFactory` 支持 HTTPS 免证书验证（开发环境）
- **参数构建器**：`RestParam` 链式构建请求参数

## 如何使用

### RestClient（带重试）

```java
// 简单使用
RestClient client = new RestClient("https://api.example.com");
String result = client.get("/users/1");
String result = client.post("/users", jsonObject);

// 带自定义 Header
HttpHeaders headers = new HttpHeaders();
headers.set("Authorization", "Bearer " + token);
String result = client.get("/protected", headers);
```

### HttpClient（无重试）

```java
HttpClient client = new HttpClient();
String result = client.post("https://api.example.com/data", headers, jsonObject);
String result = client.get("https://api.example.com/data", headers, params);
```

### RestTemplateContext（单例）

```java
// 全局共享的 RestTemplate（信任所有证书）
RestTemplate restTemplate = RestTemplateContext.getInstance().getRestTemplate();
```

### 请求/响应拦截器

```java
RestClient client = new RestClient(
    proxyProperties,
    "https://api.example.com",
    5,      // 重试次数
    "{}",   // 失败时的默认响应
    request -> { /* 请求前处理 */ },
    response -> { /* 响应后处理 */ }
);
```

## 使用实例

```java
// 调用外部 API
RestClient client = new RestClient("https://api.weather.com");
RestParam params = new RestParam()
    .add("city", "北京")
    .add("unit", "celsius");
String weather = client.get("/forecast", params);
JSONObject data = JSON.parseObject(weather);
```
