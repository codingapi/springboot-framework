---
name: http-client-factory
description: 信任所有证书的 Apache HttpClient 5 工厂，用于内部 HTTPS 调用跳过证书验证
status: 已实现
scope: 后端
source: 项目自有
---

## 解决什么问题

在微服务架构或企业内部系统集成中，服务间通信常使用自签名证书或内部 CA 签发的证书。标准的 Apache HttpClient 会严格校验 SSL 证书链，导致请求失败并抛出 `SSLHandshakeException`。

`TrustAnyHttpClientFactory` 提供了一个静态工厂方法，创建跳过 SSL 证书验证的 Apache HttpClient 5 实例。它同时被 `RestTemplateContext` 作为底层 HttpClient 使用，也可以独立用于任何需要信任所有证书的场景。

核心设计：
- **内部 TrustManager**：`TrustAnyTrustManager` 实现了 `X509TrustManager`，`checkClientTrusted` 和 `checkServerTrusted` 均为空实现
- **NoopHostnameVerifier**：不校验服务器主机名与证书的匹配
- **双协议支持**：通过 `Registry<ConnectionSocketFactory>` 同时注册 HTTP（`PlainConnectionSocketFactory`）和 HTTPS（`SSLConnectionSocketFactory`）
- **循环重定向**：默认开启 `circularRedirectsAllowed`

## 如何使用

### 1. 创建信任所有证书的 HttpClient

```java
import com.codingapi.springboot.framework.utils.TrustAnyHttpClientFactory;
import org.apache.hc.client5.http.classic.HttpClient;

HttpClient httpClient = TrustAnyHttpClientFactory.createTrustAnyHttpClient();
```

### 2. 配合 RestTemplate 使用

```java
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

HttpComponentsClientHttpRequestFactory factory = 
    new HttpComponentsClientHttpRequestFactory(
        TrustAnyHttpClientFactory.createTrustAnyHttpClient()
    );
RestTemplate restTemplate = new RestTemplate(factory);
```

### 3. 直接使用 HttpClient 执行请求

```java
HttpClient httpClient = TrustAnyHttpClientFactory.createTrustAnyHttpClient();

// 使用 Apache HC5 API 执行请求
httpClient.execute(new HttpGet("https://internal-service.example.com/api/data"), response -> {
    int statusCode = response.getCode();
    String body = EntityUtils.toString(response.getEntity());
    // 处理响应...
    return body;
});
```

## 使用实例

### 自定义 HttpClient 配置

如需在信任所有证书的基础上添加额外配置（如超时、连接池等），可参考 `TrustAnyHttpClientFactory` 的实现模式自行扩展：

```java
@SneakyThrows
public static HttpClient createCustomTrustAnyHttpClient() {
    // 1. 创建信任所有证书的 SSL 上下文
    SSLContext sslContext = SSLContext.getInstance("TLS");
    X509TrustManager trustManager = new X509TrustManager() {
        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType) {}
        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType) {}
        @Override
        public X509Certificate[] getAcceptedIssuers() { return new X509Certificate[0]; }
    };
    sslContext.init(null, new TrustManager[]{trustManager}, null);

    // 2. 创建 SSL 连接工厂
    SSLConnectionSocketFactory sslFactory = 
        new SSLConnectionSocketFactory(sslContext, NoopHostnameVerifier.INSTANCE);

    // 3. 注册协议
    Registry<ConnectionSocketFactory> registry = RegistryBuilder.<ConnectionSocketFactory>create()
        .register("https", sslFactory)
        .register("http", new PlainConnectionSocketFactory())
        .build();

    // 4. 配置请求参数
    RequestConfig requestConfig = RequestConfig.custom()
        .setCircularRedirectsAllowed(true)
        .setResponseTimeout(Timeout.ofSeconds(30))
        .setConnectTimeout(Timeout.ofSeconds(10))
        .build();

    // 5. 构建 HttpClient
    BasicHttpClientConnectionManager connManager = 
        new BasicHttpClientConnectionManager(registry);

    return HttpClients.custom()
        .setDefaultRequestConfig(requestConfig)
        .setConnectionManager(connManager)
        .build();
}
```

### 内部类 TrustAnyTrustManager

`TrustAnyTrustManager` 是 `TrustAnyHttpClientFactory` 的内部私有类，实现了 `X509TrustManager` 接口：

```java
private static class TrustAnyTrustManager implements X509TrustManager {
    @Override
    public void checkClientTrusted(X509Certificate[] x509Certificates, String s) 
            throws CertificateException {
        // 空实现：信任所有客户端证书
    }

    @Override
    public void checkServerTrusted(X509Certificate[] x509Certificates, String s) 
            throws CertificateException {
        // 空实现：信任所有服务端证书
    }

    @Override
    public X509Certificate[] getAcceptedIssuers() {
        return new X509Certificate[0];
    }
}
```

### 与其他模块的关系

`TrustAnyHttpClientFactory` 被 `RestTemplateContext` 直接引用作为底层 HttpClient 的创建方式：

```java
// RestTemplateContext 构造函数
private RestTemplateContext() {
    HttpComponentsClientHttpRequestFactory requestFactory = 
        new HttpComponentsClientHttpRequestFactory(
            TrustAnyHttpClientFactory.createTrustAnyHttpClient()
        );
    this.restTemplate = restTemplate(requestFactory);
}
```

如果只需要底层的 HttpClient 而不需要 RestTemplate 封装，可以直接使用 `TrustAnyHttpClientFactory`。

### ⚠️ 安全提示

此工厂创建的 HttpClient **完全跳过了 SSL/TLS 证书验证**，包括：
- 不验证证书链是否由受信任的 CA 签发
- 不验证证书是否过期
- 不验证服务器主机名是否与证书中的 CN/SAN 匹配

**仅应在以下场景使用：**
- 内部微服务间的 HTTPS 通信（自签名证书）
- 开发/测试环境调试
- 对接已知可信但证书不规范的第三方系统

**不应在以下场景使用：**
- 面向公网的用户数据传输
- 涉及敏感数据的跨组织通信
- 任何需要保证传输安全性的生产环境外部调用
