---
name: rest-client
description: 出站 HTTP 客户端框架，提供 HttpClient（底层 HTTP 封装）、RestClient（REST 高级客户端）、SessionClient（Cookie/Session 感知）、HttpRequest（请求构建器 + 代理支持）
status: 已实现
scope: 后端
source: 项目自有
last_commit: dc5e04ef
code_files:
  - springboot-starter/src/main/java/com/codingapi/springboot/framework/rest/HttpClient.java
  - springboot-starter/src/main/java/com/codingapi/springboot/framework/rest/RestClient.java
  - springboot-starter/src/main/java/com/codingapi/springboot/framework/rest/SessionClient.java
  - springboot-starter/src/main/java/com/codingapi/springboot/framework/rest/HttpRequest.java
  - springboot-starter/src/main/java/com/codingapi/springboot/framework/rest/Request.java
  - springboot-starter/src/main/java/com/codingapi/springboot/framework/rest/RestTemplateContext.java
  - springboot-starter/src/main/java/com/codingapi/springboot/framework/rest/param/IRestParam.java
  - springboot-starter/src/main/java/com/codingapi/springboot/framework/rest/param/RestParam.java
  - springboot-starter/src/main/java/com/codingapi/springboot/framework/rest/properties/HttpProxyProperties.java
---

## 解决什么问题

在微服务架构中，服务间调用、第三方 API 对接、爬虫数据采集等场景需要频繁发起出站 HTTP 请求。直接使用 Spring `RestTemplate` 存在以下痛点：

- **缺少重试机制**：网络抖动时直接抛异常，需要手动包装重试逻辑
- **Session/Cookie 管理繁琐**：模拟登录等有状态交互需手动维护 Cookie
- **代理配置分散**：HTTP 代理设置需要在每处创建 RestTemplate 时重复配置
- **参数构建不便**：GET 查询参数和 POST 表单/JSON 的参数组装代码冗长

本能力提供三层 HTTP 客户端抽象：

- **HttpClient**：轻量级底层封装，适合单次无状态调用
- **RestClient**：REST 高级客户端，内置 baseUrl 拼接、自动重试、默认 JSON Content-Type
- **SessionClient**：Cookie/Session 感知客户端，自动跟踪 Set-Cookie 响应头并回传，支持 302 重定向跟随

所有客户端共享 `RestTemplateContext` 单例（基于 Apache HttpComponents，信任所有证书），统一支持 HTTP 代理配置。

## 如何使用

### 核心类层次

```
RestTemplateContext (单例, Apache HttpComponents)
└── HttpRequest (请求构建器, 代理支持, 拦截器钩子)
    ├── HttpClient     (基础 GET/POST)
    ├── RestClient     (baseUrl + 重试 + REST 风格)
    └── SessionClient  (Cookie 跟踪 + 重定向)
```

### HttpRequest — 请求构建器

底层请求引擎，封装 `RestTemplate.exchange()` 调用，提供两个扩展点：

```java
// 请求拦截器：可修改 URL、Header、Body
interface IHttpRequestHandler {
    String handler(HttpRequest client, String uri, HttpMethod method,
                   HttpHeaders headers, HttpEntity<?> httpEntity);
}

// 响应拦截器：可处理状态码、重定向、错误
interface IHttpResponseHandler {
    String handler(HttpRequest client, String uri, ResponseEntity<String> response);
}
```

默认行为：请求拦截器透传 URL；响应拦截器处理 200/404 返回 body，302 自动跟随重定向。连接超时固定为 3000ms。

### HttpClient — 基础客户端

```java
// 无代理
HttpClient client = new HttpClient();

// 带代理
HttpProxyProperties proxy = new HttpProxyProperties();
proxy.setEnableProxy(true);
proxy.setProxyHost("proxy.example.com");
proxy.setProxyPort(8080);
proxy.setProxyType(Proxy.Type.HTTP);
HttpClient client = new HttpClient(proxy);

// 自定义请求/响应处理器
HttpClient client = new HttpClient(proxy, requestHandler, responseHandler);
```

提供三个便捷方法：
- `post(url, headers, JSON)` — POST JSON 请求
- `post(url, headers, MultiValueMap)` — POST 表单请求
- `get(url, headers, uriVariables)` — GET 请求

### RestClient — REST 高级客户端

```java
// 最简构造：指定 baseUrl，默认重试 5 次，空响应 "{}"
RestClient rest = new RestClient("https://api.example.com");

// 完整构造
RestClient rest = new RestClient(proxyProperties, baseUrl, retryCount,
                                  emptyResponse, requestHandler, responseHandler);
```

特性：
- **baseUrl 自动拼接**：`rest.get("/users")` → `https://api.example.com/users`
- **自动重试**：请求失败时按 `retryCount` 重试，每次间隔 1 秒
- **默认 JSON**：初始化时自动设置 `Content-Type: application/json`
- **优雅降级**：重试耗尽后返回 `emptyResponse` 而非抛异常

### SessionClient — Session 感知客户端

```java
SessionClient session = new SessionClient();  // 无代理
SessionClient session = new SessionClient(proxyProperties);  // 带代理

// 链式添加 Header
session.addHeader("Authorization", "Bearer xxx");

// 表单提交（自动设置 APPLICATION_FORM_URLENCODED）
session.postForm("https://example.com/login", restParam);

// JSON 提交
session.postJson("https://example.com/api/data", restParam);

// GET 请求
session.getJson("https://example.com/api/users");
session.getHtml("https://example.com/page");
```

内部通过自定义 `IHttpResponseHandler` 自动将响应中的 `Set-Cookie` 转为后续请求的 `Cookie` 头，并在遇到 302 时自动跟随重定向。

### RestParam — 统一参数构建器

```java
// 手动构建
RestParam param = RestParam.create()
    .add("username", "admin")
    .add("password", "secret");

// 从对象解析
RestParam param = RestParam.parser(myObject);

// 转换为不同格式
JSONObject json = param.toJsonRequest();           // POST JSON
MultiValueMap<String, Object> form = param.toFormRequest();  // POST 表单
MultiValueMap<String, String> query = param.toGetRequest();  // GET 参数
```

实现 `IRestParam` 接口的 POJO 可直接调用 `toParameters()` 转为 `RestParam`。

### HttpProxyProperties — 代理配置

```java
@Setter @Getter
public class HttpProxyProperties {
    private boolean enableProxy;
    private String proxyHost;
    private int proxyPort;
    private Proxy.Type proxyType;
}
```

## 使用实例

### 1. 调用第三方 REST API（带重试）

```java
RestClient api = new RestClient("https://api.third-party.com");

// GET 请求
String users = api.get("/v1/users");

// POST JSON 请求
RestParam param = RestParam.create()
    .add("name", "张三")
    .add("age", 25);
String result = api.post("/v1/users", param);

// 带自定义 Header
HttpHeaders headers = new HttpHeaders();
headers.set("X-API-Key", "my-key");
String data = api.get("/v1/data", headers);
```

### 2. 模拟登录并保持会话

```java
SessionClient session = new SessionClient();

// 登录（表单提交，自动保存 Cookie）
RestParam loginParam = RestParam.create()
    .add("username", "admin")
    .add("password", "password123");
session.postForm("https://app.example.com/login", loginParam);

// 后续请求自动携带 Cookie
String dashboard = session.getHtml("https://app.example.com/dashboard");
String userInfo = session.getJson("https://app.example.com/api/user/info");
```

### 3. 通过代理访问外部服务

```java
HttpProxyProperties proxy = new HttpProxyProperties();
proxy.setEnableProxy(true);
proxy.setProxyHost("proxy.corp.com");
proxy.setProxyPort(3128);
proxy.setProxyType(Proxy.Type.HTTP);

RestClient client = new RestClient(proxy, "https://external-api.com",
                                    3, "{}", null, null);
String response = client.get("/data");
```

### 4. 自定义请求/响应拦截

```java
// 请求签名拦截器
HttpRequest.IHttpRequestHandler signer = (client, uri, method, headers, entity) -> {
    String timestamp = String.valueOf(System.currentTimeMillis());
    headers.set("X-Timestamp", timestamp);
    headers.set("X-Signature", HmacSHA256.sign(uri + timestamp, secretKey));
    return uri;
};

HttpClient httpClient = new HttpClient(signer, null);
String result = httpClient.get("https://secure-api.com/data", headers, null);
```
