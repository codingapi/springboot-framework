---
name: springboot-starter/rest-client
module: springboot-starter
description: REST 客户端封装，提供 RestTemplate 上下文管理和 HTTPS 信任所有证书支持
status: 已实现
scope: 后端
source: 框架:springboot-starter
import: "com.codingapi.springboot:springboot-starter"
framework_version: "3.4.54"
---

## 解决什么问题

在企业内部系统集成和第三方 API 对接场景中，经常需要调用外部 HTTPS 服务。这些服务的证书可能由私有 CA 签发、已过期或主机名不匹配，导致标准 HTTP 客户端抛出 SSL 握手异常。同时，项目中多处使用 `RestTemplate` 时，如果每次都手动创建实例并配置底层 HttpClient，会导致代码重复且难以统一维护。

本能力通过两个核心类解决上述问题：

- **TrustAnyHttpClientFactory** — 创建信任所有证书的 Apache HttpClient 5 实例，跳过服务端证书校验和主机名验证，适用于开发调试及内网可信环境下的 HTTPS 调用。
- **RestTemplateContext** — 以单例模式持有预配置好的 `RestTemplate`（底层使用 TrustAnyHttpClientFactory），为全应用提供统一的 REST 客户端访问入口，避免重复创建和配置。

## 如何使用

### 依赖引入

在 Maven 项目中添加 springboot-starter 依赖即可：

```xml
<dependency>
    <groupId>com.codingapi.springboot</groupId>
    <artifactId>springboot-starter</artifactId>
</dependency>
```

### 获取 RestTemplate 实例

通过 `RestTemplateContext` 的单例方法获取已配置好 HTTPS 信任策略的 `RestTemplate`：

```java
RestTemplate restTemplate = RestTemplateContext.getInstance().getRestTemplate();
```

该实例底层使用 Apache HttpComponents 5 作为传输层，并已启用以下特性：

- TLS 协议下信任所有服务端证书（`TrustAnyTrustManager`）
- 禁用主机名验证（`NoopHostnameVerifier`）
- 允许循环重定向

### 自定义 ClientHttpRequestFactory

如果需要替换默认的请求工厂，可以使用 `restTemplate(ClientHttpRequestFactory)` 方法创建新的 `RestTemplate` 实例：

```java
ClientHttpRequestFactory customFactory = new HttpComponentsClientHttpRequestFactory(customHttpClient);
RestTemplate customTemplate = RestTemplateContext.getInstance().restTemplate(customFactory);
```

### 直接使用 TrustAnyHttpClientFactory

若仅需创建信任所有证书的 HttpClient 而不使用 RestTemplateContext 封装，可直接调用静态工厂方法：

```java
HttpClient httpClient = TrustAnyHttpClientFactory.createTrustAnyHttpClient();
```

返回的 `HttpClient` 基于 Apache HttpClient 5，可用于任何需要绕过 SSL 校验的场景。

## 使用实例

### 示例 1：调用外部 HTTPS 接口

```java
@Service
public class ExternalApiService {

    private final RestTemplate restTemplate;

    public ExternalApiService() {
        this.restTemplate = RestTemplateContext.getInstance().getRestTemplate();
    }

    public String fetchRemoteData(String url) {
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        return response.getBody();
    }

    public <T> T postJson(String url, Object request, Class<T> responseType) {
        return restTemplate.postForObject(url, request, responseType);
    }
}
```

### 示例 2：独立使用 TrustAnyHttpClientFactory

```java
// 在不依赖 RestTemplateContext 的场景下，单独创建信任所有证书的 HttpClient
HttpClient httpClient = TrustAnyHttpClientFactory.createTrustAnyHttpClient();

// 配合 Spring 的 HttpComponentsClientHttpRequestFactory 构建自定义 RestTemplate
HttpComponentsClientHttpRequestFactory factory =
        new HttpComponentsClientHttpRequestFactory(httpClient);
factory.setConnectTimeout(Duration.ofSeconds(5));
factory.setReadTimeout(Duration.ofSeconds(30));

RestTemplate restTemplate = new RestTemplate(factory);
String result = restTemplate.getForObject("https://internal-api.example.com/data", String.class);
```

> ⚠️ **安全提示**：信任所有证书仅适用于开发调试和内网可信环境。在生产环境中应正确配置受信任的 CA 证书链，避免中间人攻击风险。
