---
name: commons-io
description: IO 工具类库，简化文件/流操作
status: 已实现
scope: 后端
source: 框架:Apache Commons IO
---

## 解决什么问题

在 Spring Security Filter 链中，需要从 HTTP 请求/响应中读取和写入数据流：
- 从 `HttpServletRequest` 中读取请求体内容
- 向 `HttpServletResponse` 中写入 JSON 响应
- 测试中读取文件资源

Apache Commons IO 的 `IOUtils` 提供了简洁的流读写 API，避免了手动处理流的样板代码。

## 如何使用

### 依赖引入

```xml
<!-- pom.xml 中已声明版本 2.17.0 -->
<dependency>
    <groupId>commons-io</groupId>
    <artifactId>commons-io</artifactId>
    <version>2.17.0</version>
</dependency>
```

### 核心用法

主要使用 `org.apache.commons.io.IOUtils` 进行流的读写操作。

## 使用实例

### 1. Security Filter 中读取请求体

```java
// MyLoginFilter.java - 读取登录请求体
import org.apache.commons.io.IOUtils;

@Override
public Authentication attemptAuthentication(HttpServletRequest request,
                                            HttpServletResponse response) {
    // 从请求输入流中读取 JSON 数据
    String body = IOUtils.toString(request.getInputStream(), StandardCharsets.UTF_8);
    JSONObject json = JSONObject.parseObject(body);
    String username = json.getString("username");
    String password = json.getString("password");
    // ... 执行认证逻辑
}
```

### 2. Security Filter 中写入响应

```java
// MyAccessDeniedHandler.java / MyUnAuthenticationEntryPoint.java
import org.apache.commons.io.IOUtils;

@Override
public void handle(HttpServletRequest request, HttpServletResponse response,
                   AccessDeniedException accessDeniedException) throws IOException {
    response.setContentType("application/json;charset=UTF-8");
    JSONObject json = new JSONObject();
    json.put("errCode", "403");
    json.put("errMessage", "访问被拒绝");
    // 使用 IOUtils 将 JSON 字符串写入响应输出流
    IOUtils.write(json.toJSONString(), response.getOutputStream(), StandardCharsets.UTF_8);
}
```

### 3. MyAuthenticationFilter 中的 Token 读取

```java
// MyAuthenticationFilter.java - 从请求头或请求体中读取 Token
import org.apache.commons.io.IOUtils;

// 读取请求内容进行 Token 验证
String content = IOUtils.toString(request.getInputStream(), StandardCharsets.UTF_8);
```

### 4. 测试中的文件读取

```java
// SearchRequestTest.java - 测试中读取测试资源文件
import org.apache.commons.io.FileUtils;

String content = FileUtils.readFileToString(
    new File("src/test/resources/test-data.json"),
    StandardCharsets.UTF_8
);
```

### 涉及模块

| 模块 | 使用场景 |
|------|----------|
| springboot-starter-security | Filter 链中请求体读取、响应体写入（IOUtils） |
| springboot-starter | 测试中文件操作（FileUtils） |
