---
name: rest-template-context
description: 预配置的 RestTemplate 单例上下文，内置信任所有证书的 HttpClient，简化内部服务间 HTTP 调用
status: 已实现
scope: 后端
source: 项目自有
---

## 解决什么问题

在企业内部系统集成场景中，服务间通过 HTTPS 通信时经常遇到自签名证书或内部 CA 签发的证书不被信任的问题。每次创建 `RestTemplate` 都需要手动配置 SSL 信任策略，代码重复且容易出错。

`RestTemplateContext` 提供了一个全局单例的 `RestTemplate` 实例，内置了信任所有证书的 `HttpClient`（基于 Apache HttpComponents 5），适用于内部服务间调用、第三方接口对接等不需要严格证书校验的场景。

核心特点：
- **单例模式**：通过双重检查锁确保线程安全的懒初始化
- **信任所有证书**：底层使用 `TrustAnyHttpClientFactory` 创建的 HttpClient，跳过 SSL 证书验证
- **支持 HTTP/HTTPS**：同时注册了 HTTP 和 HTTPS 的连接工厂
- **允许循环重定向**：默认开启 `circularRedirectsAllowed`

## 如何使用

### 1. 获取 RestTemplate 实例

```java
RestTemplate restTemplate = RestTemplateContext.getInstance().getRestTemplate();
```

### 2. 发起 HTTP 请求

```java
RestTemplate restTemplate = RestTemplateContext.getInstance().getRestTemplate();

// GET 请求
String result = restTemplate.getForObject("https://internal-api.example.com/data", String.class);

// POST 请求
Map<String, Object> body = Map.of("key", "value");
ResponseEntity<String> response = restTemplate.postForEntity(
    "https://internal-api.example.com/submit", 
    body, 
    String.class
);

// 带 Header 的请求
HttpHeaders headers = new HttpHeaders();
headers.setContentType(MediaType.APPLICATION_JSON);
headers.setBearerAuth(token);
HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
ResponseEntity<String> resp = restTemplate.exchange(
    "https://api.example.com/resource", 
    HttpMethod.GET, 
    entity, 
    String.class
);
```

### 3. 自定义 ClientHttpRequestFactory

如需使用不同的请求工厂创建新的 RestTemplate：

```java
ClientHttpRequestFactory customFactory = new HttpComponentsClientHttpRequestFactory(customHttpClient);
RestTemplate customTemplate = RestTemplateContext.getInstance().restTemplate(customFactory);
```

## 使用实例

### 内部服务调用示例

```java
@Service
public class ExternalServiceClient {

    private final RestTemplate restTemplate;

    public ExternalServiceClient() {
        this.restTemplate = RestTemplateContext.getInstance().getRestTemplate();
    }

    public UserInfo getUserFromRemote(Long userId) {
        String url = "https://user-service.internal/api/users/" + userId;
        return restTemplate.getForObject(url, UserInfo.class);
    }

    public void notifyRemote(String message) {
        String url = "https://notification-service.internal/api/notify";
        Map<String, String> payload = Map.of("message", message);
        restTemplate.postForEntity(url, payload, Void.class);
    }
}
```

### 架构说明

```
RestTemplateContext (Singleton)
    └── RestTemplate
         └── HttpComponentsClientHttpRequestFactory
              └── HttpClient (Apache HC5)
                   ├── SSLConnectionSocketFactory (TLS, TrustAnyTrustManager, NoopHostnameVerifier)
                   └── PlainConnectionSocketFactory (HTTP)
```

`TrustAnyHttpClientFactory.createTrustAnyHttpClient()` 创建的 HttpClient 具有以下特性：
- SSL 上下文使用 `TrustAnyTrustManager`，对所有服务端证书返回信任
- 主机名验证使用 `NoopHostnameVerifier`，不校验主机名
- 连接管理器使用 `BasicHttpClientConnectionManager`
- 请求配置允许循环重定向

### ⚠️ 安全提示

此 RestTemplate **仅适用于受信任的内部网络环境**。由于跳过了 SSL 证书验证，在公网环境下使用存在中间人攻击风险。对于面向公网的外部 API 调用，应使用标准配置的 RestTemplate 并正确配置证书信任链。
