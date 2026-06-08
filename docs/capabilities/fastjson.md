---
name: fastjson
description: 高性能 JSON 序列化/反序列化库
status: 已实现
scope: 后端
source: 框架:Fastjson
---

## 解决什么问题

在框架中需要频繁进行 Java 对象与 JSON 字符串之间的转换，包括：
- Response 统一响应体的序列化
- Token 对象的 JSON 序列化/反序列化
- DTO 数据的 Map 转换
- Security Filter 中的请求/响应体 JSON 处理
- Flow 工作流模块的数据快照绑定

Fastjson 提供了比 Jackson 更简洁的 API（`JSONObject.toJSONString()` / `JSONObject.parseObject()`），适合框架内部轻量级序列化场景。

## 如何使用

### 依赖引入

```xml
<!-- pom.xml 中已声明版本 2.0.53 -->
<dependency>
    <groupId>com.alibaba.fastjson2</groupId>
    <artifactId>fastjson2</artifactId>
    <version>2.0.53</version>
</dependency>
```

### 核心接口：JsonSerializable

框架定义了 `JsonSerializable` 接口，所有需要 JSON 序列化的 DTO/实体类实现此接口即可获得 `toJson()` 方法：

```java
public interface JsonSerializable extends Serializable {
    default String toJson() {
        return JSONObject.toJSONString(this);
    }
}
```

### 核心接口：MapSerializable

提供对象到 Map 的转换能力：

```java
public interface MapSerializable {
    default Map<String, Object> toMap() {
        return JSON.parseObject(JSON.toJSONString(this));
    }
}
```

## 使用实例

### 1. Token 序列化（JWT 模式）

```java
// JwtTokenGateway.java - 创建 JWT Token
public Token create(String username, String iv, List<String> authorities, String extra) {
    Token token = new Token(username, iv, extra, authorities, validTime, restTime);
    // 将 Token 对象序列化为 JSON 作为 JWT subject
    String jwt = Jwts.builder().subject(token.toJson()).signWith(key).compact();
    token.setToken(jwt);
    return token;
}

// 解析 JWT Token
public Token parser(String sign) {
    Jws<Claims> jws = Jwts.parser().verifyWith(key).build().parseSignedClaims(sign);
    String subject = jws.getPayload().getSubject();
    // 从 JSON 反序列化为 Token 对象
    return JSONObject.parseObject(subject, Token.class);
}
```

### 2. Security Filter 中的 JSON 响应

```java
// MyAuthenticationFilter.java / MyLoginFilter.java 等
// 使用 JSONObject 构建错误响应
JSONObject json = new JSONObject();
json.put("errCode", "401");
json.put("errMessage", "认证失败");
response.getWriter().write(json.toJSONString());
```

### 3. Redis Token 存储

```java
// RedisTokenGateway.java - 使用 fastjson2 进行 Token 持久化
import com.alibaba.fastjson2.JSONObject;

// Token 序列化存入 Redis
String json = token.toJson();
redisTemplate.opsForValue().set(key, json);

// 从 Redis 读取并反序列化
String json = redisTemplate.opsForValue().get(key);
Token token = JSONObject.parseObject(json, Token.class);
```

### 4. Flow 数据快照绑定

```java
// BindDataSnapshot.java / FlowMapBindData.java
// 使用 JSONObject 处理流程数据快照
JSONObject data = new JSONObject();
data.put("key", value);
```

### 涉及模块

| 模块 | 使用场景 |
|------|----------|
| springboot-starter | `JsonSerializable`、`MapSerializable` 接口定义 |
| springboot-starter-security | Token 序列化、Filter JSON 响应、JWT/Redis 网关 |
| springboot-starter-flow | 数据快照绑定、Schema 解析 |
