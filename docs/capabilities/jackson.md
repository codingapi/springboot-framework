---
name: jackson
description: Jackson JSON 序列化/反序列化能力，由 Spring Boot Web 自动配置，框架中用于 REST API 响应序列化、请求参数绑定等场景
status: 已实现
scope: 后端
source: 框架:jackson
framework_version: Managed by Spring Boot BOM (3.3.5)
---

## 解决什么问题

Jackson 是 Spring Boot Web 默认的 JSON 处理库，在框架中承担以下职责：

- **REST API 响应序列化**：所有 Controller 返回的 `Response` / `SingleResponse` / `MultiResponse` / `MapResponse` 对象均通过 Jackson 自动序列化为 JSON 输出
- **请求参数反序列化**：`@RequestBody` 注解的请求体自动由 Jackson 反序列化为 Java 对象，包括 `PageRequest` 中的过滤条件
- **枚举序列化**：框架提供了自定义 `EnumSerializer`，统一枚举值的 JSON 输出格式
- **日期/时间格式化**：通过 Spring Boot 的 `spring.jackson.*` 配置项统一日期格式
- **字段命名策略**：支持驼峰/下划线等命名转换，前后端字段名对齐

> **注意**：框架内部部分场景（如 Token 序列化、事件数据传输）使用了 Fastjson（`com.alibaba.fastjson.JSONObject`）作为补充，通过 `JsonSerializable` 接口封装。Jackson 主要负责 HTTP 层的 JSON 处理。

## 如何使用

### 依赖引入

Jackson 由 `spring-boot-starter-web` 自动引入，无需单独声明：

```xml
<!-- springboot-starter-data-fast 和 springboot-starter-security 均已传递依赖 -->
<dependency>
    <groupId>com.codingapi.springboot</groupId>
    <artifactId>springboot-starter</artifactId>
</dependency>
```

### 全局配置

在 `application.properties` 中统一配置 Jackson 行为：

```properties
# 日期格式
spring.jackson.date-format=yyyy-MM-dd HH:mm:ss
spring.jackson.time-zone=GMT+8

# 序列化选项
spring.jackson.default-property-inclusion=non_null
spring.jackson.serialization.write-dates-as-timestamps=false

# 反序列化选项
spring.jackson.deserialization.fail-on-unknown-properties=false
```

### 自定义序列化器

框架已提供 `EnumSerializer`，可按需注册更多自定义序列化器：

```java
@Configuration
public class JacksonConfig {

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addSerializer(MyEnum.class, new EnumSerializer());
        mapper.registerModule(module);
        return mapper;
    }
}
```

## 使用实例

### REST API 响应（自动序列化）

```java
@RestController
@RequestMapping("/api/users")
@AllArgsConstructor
public class UserController {

    private final UserQueryService userQueryService;

    // Jackson 自动将 SingleResponse<UserDTO> 序列化为 JSON
    @GetMapping("/{id}")
    public SingleResponse<UserDTO> getUser(@PathVariable Long id) {
        return SingleResponse.of(userQueryService.findById(id));
    }

    // 分页响应包含 total、content 等字段
    @GetMapping
    public MultiResponse<UserDTO> list(PageRequest request) {
        Page<UserDTO> page = userQueryService.findAll(request);
        return MultiResponse.of(page.getContent(), page.getTotalElements());
    }
}
```

输出示例：

```json
{
  "success": true,
  "errCode": null,
  "errMessage": null,
  "data": [
    { "id": 1, "name": "张三", "age": 28 }
  ],
  "total": 1
}
```

### 请求体反序列化

```java
@PostMapping
public Response createUser(@RequestBody CreateUserCmd cmd) {
    // Jackson 自动将 JSON 请求体反序列化为 CreateUserCmd
    userService.create(cmd);
    return Response.buildSuccess();
}
```
