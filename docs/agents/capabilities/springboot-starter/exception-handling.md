---
name: springboot-starter/exception-handling
module: springboot-starter
description: 全局异常处理，统一拦截 Controller 层异常并返回标准 Response
status: 已实现
scope: 后端
source: 框架:springboot-starter
import: "com.codingapi.springboot:springboot-starter"
framework_version: "3.4.54"
---

## 解决什么问题

在 Spring MVC 项目中，Controller 层抛出的异常如果不做统一处理，会导致以下问题：

- **响应格式不一致**：不同接口返回的错误结构各异（有的返回 HTML 错误页、有的返回纯文本、有的返回自定义 JSON），前端难以统一解析。
- **错误码缺失**：原生异常只有 message，没有业务错误码（errCode），前端无法根据错误码做多语言提示或差异化逻辑。
- **国际化困难**：错误信息硬编码在代码中，无法根据用户 Locale 动态切换语言。
- **敏感信息泄露**：未捕获的异常可能将堆栈信息直接暴露给客户端。

`springboot-starter` 的异常处理能力通过 `HandlerExceptionResolver` 机制，在 Servlet 层面统一拦截所有 Controller 异常，将其转换为标准的 `{success, errCode, errMessage}` JSON 响应，同时结合 `LocaleMessageException` 和 `MessageSource` 实现错误信息的国际化管理。

## 如何使用

### 自动生效

引入 `springboot-starter` 依赖后，异常处理自动配置即可生效，无需额外注解或配置：

```xml
<dependency>
    <groupId>com.codingapi.springboot</groupId>
    <artifactId>springboot-starter</artifactId>
</dependency>
```

框架通过以下两个配置类自动注册：

- **`ExceptionConfiguration`**：注册 `LocaleMessage` Bean，绑定 Spring `MessageSource`，在初始化时将自身注入 `MessageContext` 单例，为 `LocaleMessageException` 提供国际化消息解析能力。
- **`BasicHandlerExceptionResolverConfiguration`**：当 classpath 中存在 `HandlerExceptionResolver`（即 Spring MVC 环境）时，注册 `ServletExceptionHandler` 作为全局异常解析器。

### 异常类型与响应映射

| 异常类型 | errCode | errMessage | 说明 |
|---------|---------|------------|------|
| `LocaleMessageException` | 异常中指定的 errCode | 国际化解析后的消息 | 业务异常，推荐使用 |
| 其他 `Exception` | `system.err` | `ex.getMessage()` | 未预期的系统异常 |

### 抛出业务异常

在 Service 或 Controller 中抛出 `LocaleMessageException`，支持三种构造方式：

```java
// 1. 直接使用错误码（从 messages.properties 中解析消息）
throw new LocaleMessageException("user.not.found");

// 2. 错误码 + 占位符参数
throw LocaleMessageException.of("order.amount.exceed", maxAmount);

// 3. 错误码 + 自定义消息（不走国际化）
throw new LocaleMessageException("custom.error", "自定义错误描述");
```

### 配置国际化消息

在 `src/main/resources/messages.properties`（及对应的多语言文件）中定义错误消息：

```properties
# messages.properties
user.not.found=用户不存在
order.amount.exceed=订单金额超出限制，最大金额为 {0}

# messages_en.properties
user.not.found=User not found
order.amount.exceed=Order amount exceeds limit, maximum is {0}
```

## 使用实例

### 完整示例：Controller 中抛出业务异常

```java
@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/{id}")
    public SingleResponse<User> getUser(@PathVariable Long id) {
        User user = userService.findById(id);
        if (user == null) {
            // 抛出国际化业务异常
            throw new LocaleMessageException("user.not.found");
        }
        return SingleResponse.of(user);
    }

    @PostMapping
    public Response createUser(@RequestBody CreateUserRequest request) {
        if (request.getAmount().compareTo(MAX_AMOUNT) > 0) {
            // 带占位符参数的异常
            throw LocaleMessageException.of("order.amount.exceed", MAX_AMOUNT);
        }
        userService.create(request);
        return Response.buildSuccess();
    }
}
```

当请求触发异常时，框架自动返回统一格式的 JSON 响应：

```json
{
    "success": false,
    "errCode": "user.not.found",
    "errMessage": "用户不存在"
}
```

若当前请求的 Locale 为英文，则自动返回：

```json
{
    "success": false,
    "errCode": "user.not.found",
    "errMessage": "User not found"
}
```

对于未预期的系统异常（如 NullPointerException），返回：

```json
{
    "success": false,
    "errCode": "system.err",
    "errMessage": "Cannot invoke method on null object"
}
```
