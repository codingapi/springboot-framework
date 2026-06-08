---
name: locale-exception-handling
description: 所有业务异常必须使用 LocaleMessageException，通过 errCode 实现国际化，由全局 HandlerExceptionResolver 统一处理
status: 已实现
scope: 后端
source: 项目自有
last_commit: 0c4299a1
code_files:
  - springboot-starter/src/main/java/com/codingapi/springboot/framework/exception/LocaleMessageException.java
  - springboot-starter/src/main/java/com/codingapi/springboot/framework/servlet/BasicHandlerExceptionResolverConfiguration.java
  - springboot-starter/src/main/java/com/codingapi/springboot/framework/exception/ExceptionConfiguration.java
---

## 解决什么问题

如果业务代码中随意抛出 `RuntimeException`、`IllegalArgumentException` 等原生异常，会导致以下问题：

1. **错误信息无法国际化**：硬编码的中文字符串无法根据用户语言环境切换
2. **缺少结构化错误码**：前端无法通过 `errCode` 精确判断错误类型并做差异化处理
3. **异常响应格式不统一**：不同异常类型被 Spring 默认处理器转为不同的 HTTP 响应结构
4. **敏感信息泄露**：未捕获的异常堆栈可能暴露内部实现细节给客户端

框架通过 `LocaleMessageException` + `BasicHandlerExceptionResolverConfiguration` 实现统一的国际化异常处理链：
- `LocaleMessageException` 携带 `errCode`，可选携带占位符参数
- `LocaleMessage` 从 Spring `MessageSource`（`messages.properties`）读取国际化消息
- `ServletExceptionHandler` 将所有异常统一转为 `{success, errCode, errMessage}` JSON 响应

## 如何使用

### 1. 定义国际化消息

在 `src/main/resources/messages.properties` 和 `messages_{locale}.properties` 中配置错误码对应的消息：

```properties
# messages.properties (默认/英文)
user.not.found=User not found
order.amount.invalid=Order amount must be greater than {0}

# messages_zh_CN.properties (中文)
user.not.found=用户不存在
order.amount.invalid=订单金额必须大于 {0}
```

### 2. 抛出 LocaleMessageException

| 构造方式 | 说明 |
|----------|------|
| `new LocaleMessageException("err.code")` | 仅指定错误码，自动从 MessageSource 查找消息 |
| `new LocaleMessageException("err.code", "fallback message")` | 指定错误码和回退消息（不走国际化） |
| `LocaleMessageException.of("err.code", arg1, arg2)` | 带占位符参数的国际化消息（推荐） |
| `new LocaleMessageException("err.code", new Object[]{arg1}, cause)` | 带参数和原始异常 |

### 3. 规则

1. **所有业务异常必须使用 `LocaleMessageException`**，禁止直接抛出 `RuntimeException` 或其他原生异常
2. 错误码采用点分隔的小写命名（如 `user.not.found`、`order.amount.invalid`）
3. 需要动态内容时使用占位符 `{0}`、`{1}`，通过 `LocaleMessageException.of()` 传入参数
4. Controller 方法无需 try-catch，由全局 `ServletExceptionHandler` 统一处理并返回标准错误响应

## 使用实例

### ✅ 正确示例

```java
// 1. 在 Service 层抛出国际化异常
@Service
public class UserService {

    public UserEntity getById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new LocaleMessageException("user.not.found"));
    }

    public void createOrder(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw LocaleMessageException.of("order.amount.invalid", amount);
        }
    }
}

// 2. Controller 无需手动处理异常，返回类型仍为统一响应
@RestController
@RequestMapping("/api/users")
public class UserController {

    @GetMapping("/{id}")
    public SingleResponse<UserEntity> getById(@PathVariable Long id) {
        return SingleResponse.of(userService.getById(id));
    }
}

// 3. 当 user.not.found 触发时，框架自动返回：
// {"success": false, "errCode": "user.not.found", "errMessage": "用户不存在"}
```

### ❌ 错误示例

```java
// 错误：直接抛出 RuntimeException，无错误码、不支持国际化
public UserEntity getById(Long id) {
    return userRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("用户不存在"));
}

// 错误：使用 IllegalArgumentException，前端无法识别错误类型
public void createOrder(BigDecimal amount) {
    if (amount.compareTo(BigDecimal.ZERO) <= 0) {
        throw new IllegalArgumentException("金额无效");
    }
}

// 错误：在 Controller 中手动 try-catch 构建错误响应
@GetMapping("/{id}")
public Response getById(@PathVariable Long id) {
    try {
        UserEntity user = userService.getById(id);
        return SingleResponse.of(user);
    } catch (Exception e) {
        return Response.buildFailure("error", e.getMessage());
    }
}

// 错误：自定义异常类但未继承 LocaleMessageException
public class BusinessException extends RuntimeException {
    // 不会被全局异常处理器识别为业务异常
}
```
