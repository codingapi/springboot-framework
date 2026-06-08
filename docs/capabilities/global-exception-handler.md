---
name: global-exception-handler
description: 全局异常处理器，统一捕获 Controller 层异常并转换为标准 Response 格式返回
status: 已实现
scope: 后端
source: 项目自有
import: com.codingapi.springboot:springboot-starter
symbols:
  - BasicHandlerExceptionResolverConfiguration
  - ServletExceptionHandler
  - LocaleMessageException
content_hash: e3029dc411b34af5ea488d45c2ac414e2ea7578b571e681e0d7dc938ae75f052
---

## 解决什么问题

Controller 层的未捕获异常需要统一处理，避免将堆栈信息暴露给前端。本能力通过 Spring `HandlerExceptionResolver` 机制实现全局异常拦截：

- **统一错误格式**：所有异常都转换为 `{success: false, errCode, errMessage}` 格式
- **国际化异常支持**：`LocaleMessageException` 携带错误码，支持国际化消息
- **兜底处理**：未识别的异常统一返回 `system.err` 错误码
- **日志记录**：异常信息自动记录到日志

## 如何使用

### 抛出业务异常

```java
// 使用 LocaleMessageException 抛出带错误码的异常
throw new LocaleMessageException("user.not.found", "用户不存在");
throw new LocaleMessageException("order.invalid", "订单状态无效");
```

### 自动处理

框架自动注册 `ServletExceptionHandler`，所有 Controller 层的异常都会被拦截并转换为标准响应：

- `LocaleMessageException` → `{success: false, errCode: "user.not.found", errMessage: "用户不存在"}`
- 其他 `Exception` → `{success: false, errCode: "system.err", errMessage: "异常消息"}`

### 自动配置

无需手动配置，`BasicHandlerExceptionResolverConfiguration` 在 Spring Web 环境下自动生效（`@ConditionalOnClass`）。

## 使用实例

```java
@RestController
public class UserController {

    @GetMapping("/users/{id}")
    public SingleResponse<User> get(@PathVariable Long id) {
        User user = userService.findById(id);
        if (user == null) {
            // 抛出国际化异常，自动转换为错误响应
            throw new LocaleMessageException("user.not.found", "用户不存在");
        }
        return SingleResponse.of(user);
    }
}

// 前端收到的响应：
// {"success": false, "errCode": "user.not.found", "errMessage": "用户不存在"}
```
