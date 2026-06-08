---
name: global-exception-handling
description: 全局异常处理与国际化规范
status: 已实现
scope: 后端
source: 项目自有
---

## 解决什么问题

全局异常处理机制解决了以下问题：

- **统一错误格式**：所有异常自动转换为标准 `Response` 格式（`success=false, errCode, errMessage`）
- **国际化支持**：业务异常通过 `LocaleMessageException` + `messages.properties` 实现多语言错误消息
- **占位符参数化**：错误消息支持 `{0}`, `{1}` 等占位符，动态填充上下文信息
- **Controller 无需 try-catch**：业务代码只需抛出异常，由框架统一拦截和转换

## 如何使用

### 核心组件

```java
// 1. ExceptionConfiguration —— 注册国际化消息源
@Configuration
public class ExceptionConfiguration {
    @Bean(initMethod = "init")
    public LocaleMessage exceptionLocaleMessage(MessageSource messageSource) {
        return new LocaleMessage(messageSource);
    }
}

// 2. BasicHandlerExceptionResolverConfiguration —— 全局异常拦截器
@Configuration
public class BasicHandlerExceptionResolverConfiguration {
    @Bean
    public HandlerExceptionResolver servletExceptionHandler() {
        return new ServletExceptionHandler();
    }
}

// 3. LocaleMessageException —— 业务异常类
public class LocaleMessageException extends RuntimeException {
    String errCode;
    String errMessage;

    // 从 messages.properties 读取消息
    LocaleMessageException(String errCode);
    // 带参数的国际化消息
    LocaleMessageException(String errCode, Object[] args);
    // 直接指定消息
    LocaleMessageException(String errCode, String errMessage);
    // 静态工厂方法（推荐）
    static LocaleMessageException of(String errCode, Object... args);
}
```

### 国际化消息配置

在 `src/main/resources/messages.properties` 中定义错误码对应的消息：

```properties
error1=操作失败，请重试
error2=用户不存在
error3=参数错误：{0}
system.err=系统内部错误
```

### 异常类型说明

| 异常类 | 用途 | 处理方式 |
|--------|------|---------|
| `LocaleMessageException` | 业务异常，支持国际化 | 提取 errCode + errMessage 返回 |
| `EventException` | 事件处理器执行异常聚合 | 包含多个子异常列表 |
| `EventLoopException` | 事件循环调用检测 | 立即中断事件链 |
| 其他 `RuntimeException` | 未预期异常 | 使用 `system.err` 作为默认错误码 |

## 使用实例

### ✅ 正确示例：使用国际化错误码

```java
@GetMapping("/error1")
public Response error1() {
    // 从 messages.properties 读取 error1 对应的消息
    throw new LocaleMessageException("error1");
}
```

### ✅ 正确示例：带参数的国际化异常

```java
@GetMapping("/error3")
public Response error3() {
    // messages.properties: error3=参数错误：{0}
    // 最终消息：参数错误：my is arg
    throw LocaleMessageException.of("error3", "my is arg");
}
```

### ✅ 正确示例：直接指定错误消息（无需国际化时）

```java
@GetMapping("/error2")
public Response error2() {
    throw new LocaleMessageException("error2", "error 2 message");
}
```

### ✅ 正确示例：在 Domain Service 中抛出业务异常

```java
@Service
@AllArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public User getUser(long id) {
        User user = userRepository.getUserById(id);
        if (user == null) {
            throw LocaleMessageException.of("user.not.found", id);
        }
        return user;
    }
}
```

### ❌ 错误示例：在 Controller 中手动捕获异常

```java
@PostMapping("/save")
public Response save(@RequestBody UserCmd cmd) {
    try {
        userRouter.createOrUpdate(cmd);
        return Response.buildSuccess();
    } catch (LocaleMessageException e) {
        // 禁止！全局异常处理器会自动处理
        return Response.buildFailure(e.getErrCode(), e.getErrMessage());
    }
}
```

### ❌ 错误示例：直接使用 RuntimeException 而非 LocaleMessageException

```java
public void validate(String name) {
    if (name == null) {
        // 禁止！缺少 errCode，无法被前端识别和处理
        throw new RuntimeException("名称不能为空");
    }
}
```

### ❌ 错误示例：硬编码中文错误消息而不使用国际化

```java
// 不推荐：消息写死在代码中，无法切换语言
throw new LocaleMessageException("error.hardcoded", "用户名已存在");

// 推荐：使用错误码 + messages.properties
throw new LocaleMessageException("user.name.duplicate");
```

### ❌ 错误示例：忽略 EventException 中的子异常信息

```java
try {
    EventPusher.push(event);
} catch (EventException e) {
    // 禁止！丢失了具体的子异常详情
    log.error("事件处理失败");

    // 正确做法：遍历子异常
    for (Exception sub : e.getError()) {
        log.error("子异常: {}", sub.getMessage(), sub);
    }
}
```
