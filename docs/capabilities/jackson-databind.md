---
name: jackson-databind
description: JSON 数据绑定库，Spring Boot 默认 JSON 处理器
status: 已实现
scope: 后端
source: 框架:Jackson Databind
---

## 解决什么问题

作为 Spring Boot 默认的 JSON 处理库，Jackson Databind 在框架中承担：
- Controller 层请求体（`@RequestBody`）的自动反序列化
- Controller 层响应体的自动序列化
- 自定义枚举序列化（将 `IEnum` 接口实现类序列化为 code 值而非名称）
- 与 Spring MVC 的消息转换器（`MappingJackson2HttpMessageConverter`）集成

虽然框架内部使用 Fastjson 做轻量级序列化，但 Jackson 仍然是 Spring MVC 层面的默认 JSON 处理器，负责所有 REST API 的请求/响应转换。

## 如何使用

### 依赖引入

```xml
<!-- 通过 spring-boot-starter-web 间接引入 -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
```

### 核心用法

#### 自定义枚举序列化器

框架定义了 `EnumSerializer`，将实现 `IEnum` 接口的枚举序列化为数字 code 值：

```java
// EnumSerializer.java
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

public class EnumSerializer extends JsonSerializer<IEnum> {

    @Override
    public void serialize(IEnum iEnum, JsonGenerator jsonGenerator,
                          SerializerProvider serializerProvider) throws IOException {
        // 将枚举序列化为 code 数字，而非枚举名称字符串
        jsonGenerator.writeNumber(iEnum.getCode());
    }
}
```

## 使用实例

### 1. IEnum 接口定义

```java
// IEnum.java - 枚举编码接口
public interface IEnum {
    int getCode();
}

// 使用示例
public enum ApprovalType implements IEnum {
    APPROVE(1),
    REJECT(2),
    WITHDRAW(3);

    private final int code;
    ApprovalType(int code) { this.code = code; }

    @Override
    public int getCode() { return code; }
}
```

### 2. 配置 Jackson 使用自定义序列化器

```java
// 在 Jackson 配置中注册 EnumSerializer
@Configuration
public class JacksonConfiguration {

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addSerializer(IEnum.class, new EnumSerializer());
        mapper.registerModule(module);
        return mapper;
    }
}
```

### 3. Controller 中的自动序列化

```java
// 当 Controller 返回包含 IEnum 字段的对象时，
// Jackson 自动使用 EnumSerializer 将枚举序列化为数字
@GetMapping("/flow/nodes")
public MultiResponse<FlowNodeDTO> getNodes() {
    // FlowNodeDTO.approvalType 字段如果实现了 IEnum，
    // 会被序列化为 {"approvalType": 1} 而非 {"approvalType": "APPROVE"}
    return Response.buildSuccess(nodeService.getNodes());
}
```

### 4. 与 Fastjson 的分工

| 场景 | 使用的库 | 说明 |
|------|----------|------|
| Spring MVC 请求/响应 | Jackson | `@RequestBody` / 返回值自动序列化 |
| 框架内部 DTO 序列化 | Fastjson | `JsonSerializable.toJson()` |
| Token JWT payload | Fastjson | `JSONObject.toJSONString(token)` |
| 枚举 code 序列化 | Jackson | `EnumSerializer` 自定义序列化器 |
| Security Filter JSON 响应 | Fastjson | 手动构建 JSONObject |

### 涉及模块

| 模块 | 使用场景 |
|------|----------|
| springboot-starter | `EnumSerializer` 自定义枚举序列化 |
| 所有 Web 模块 | Spring MVC 默认 JSON 消息转换 |
