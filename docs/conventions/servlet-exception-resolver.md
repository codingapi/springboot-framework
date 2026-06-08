---
name: servlet-exception-resolver
description: 使用 HandlerExceptionResolver（而非 @ControllerAdvice）作为全局异常处理机制，返回统一 JSON 响应格式
status: 已实现
scope: 后端
source: 项目自有
last_commit: 9c0b4ac5
code_files:
  - springboot-starter/src/main/java/com/codingapi/springboot/framework/servlet/BasicHandlerExceptionResolverConfiguration.java
---

## 解决什么问题

Spring MVC 中常见的全局异常处理方式是通过 `@ControllerAdvice` + `@ExceptionHandler` 注解实现。这种方式存在以下局限：

1. **与 Controller 层强耦合**：`@ControllerAdvice` 本质上是一个增强型 Controller，其生命周期和优先级受 DispatcherServlet 的 HandlerAdapter 调度影响，在某些边界场景（如 Filter 链异常、非标准 Handler 类型）下可能无法捕获所有异常。
2. **多 Advice 优先级冲突**：当项目中引入多个第三方库各自提供 `@ControllerAdvice` 时，需要通过 `@Order` 手动协调优先级，容易产生遗漏或覆盖。
3. **响应格式不统一**：各 `@ExceptionHandler` 方法可能返回不同的响应结构，导致前端需要适配多种错误格式。

本框架采用 Servlet 层面的 `HandlerExceptionResolver` 接口作为全局异常处理入口，直接注册为 Spring Bean，由 DispatcherServlet 在异常解析阶段统一调用，确保所有 Controller 异常都能被拦截并输出统一的 `{success, errCode, errMessage}` JSON 格式。

## 如何使用

### 核心实现类

`BasicHandlerExceptionResolverConfiguration` 是框架内置的自动配置类，通过 `@Bean` 注册一个 `HandlerExceptionResolver` 实例（内部类 `ServletExceptionHandler`）。

### 异常处理规则

| 异常类型 | errCode | errMessage | 日志级别 |
|---------|---------|------------|---------|
| `LocaleMessageException` | 异常自带的 `errCode` | 国际化消息内容 | WARN |
| 其他异常 | `"system.err"` | `ex.getMessage()` | WARN（含堆栈） |

### 响应格式

所有异常均通过 `MappingJackson2JsonView` 输出以下 JSON 结构：

```json
{
  "success": false,
  "errCode": "错误码",
  "errMessage": "错误描述"
}
```

### 业务异常抛出方式

业务代码应使用 `LocaleMessageException` 抛出异常，支持国际化错误码：

```java
// 直接使用错误码（从 message.properties 读取消息）
throw new LocaleMessageException("user.not.found");

// 带参数的错误码
throw LocaleMessageException.of("field.invalid", fieldName);

// 自定义错误消息
throw new LocaleMessageException("custom.error", "自定义错误信息");
```

### 扩展须知

如需新增自定义异常处理逻辑，应修改 `ServletExceptionHandler.resolveException()` 方法中的判断分支，而不是新建额外的 `@ControllerAdvice` 类。这保证了异常处理入口的唯一性。

## 使用实例

```java
// ✅ 正确 — 业务代码抛出 LocaleMessageException，由框架统一处理
@GetMapping("/users/{id}")
public SingleResponse<User> getUser(@PathVariable Long id) {
    User user = userRepository.findById(id).orElse(null);
    if (user == null) {
        throw new LocaleMessageException("user.not.found");
    }
    return Response.of(user);
}
// 异常被 ServletExceptionHandler 捕获，返回：
// {"success": false, "errCode": "user.not.found", "errMessage": "用户不存在"}

// ✅ 正确 — 未预期异常也会被捕获
@PostMapping("/orders")
public SingleResponse<Order> createOrder(@RequestBody OrderRequest request) {
    // 某个 NPE 或 IllegalArgumentException
    return Response.of(orderService.create(request));
}
// 返回：{"success": false, "errCode": "system.err", "errMessage": "..."}

// ❌ 错误 — 不要使用 @ControllerAdvice 处理全局异常
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleException(Exception ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("code", 500);       // 格式与框架不一致！
        body.put("msg", ex.getMessage());
        return ResponseEntity.status(500).body(body);
    }
}

// ❌ 错误 — 不要在 Controller 中自行 try-catch 并构造响应
@GetMapping("/users/{id}")
public Map<String, Object> getUser(@PathVariable Long id) {
    try {
        User user = userService.findById(id);
        return Map.of("data", user);
    } catch (Exception e) {
        return Map.of("error", e.getMessage()); // 绕过了框架异常处理！
    }
}
```
