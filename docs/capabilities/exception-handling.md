---
name: exception-handling
description: 国际化异常体系与事件异常处理，支持多语言错误消息、占位符参数和事件循环检测
status: 已实现
scope: 后端
source: 项目自有
---

## 解决什么问题

企业级应用的异常处理面临以下挑战：

1. **错误消息硬编码**：异常信息写死在代码中，无法根据用户语言环境切换
2. **错误码管理混乱**：不同模块使用不同的错误码格式，缺乏统一管理
3. **占位符支持缺失**：类似"用户 {userId} 不存在"的消息需要手动拼接字符串
4. **事件异常丢失**：多个事件处理器并发执行时，部分异常可能被吞掉
5. **循环调用难排查**：事件之间形成循环依赖时，缺少清晰的堆栈信息

框架提供三层异常基础设施：
- `LocaleMessageException` — 基于 Spring MessageSource 的国际化业务异常
- `EventException` — 聚合多个事件处理器异常的容器异常
- `EventLoopException` — 事件循环调用的专用检测异常

配合 `ExceptionConfiguration` 自动装配和 `MessageContext` 全局上下文，实现开箱即用的国际化异常体系。

## 如何使用

### 1. 配置消息资源文件

在 `src/main/resources/` 下创建 messages 属性文件：

```properties
# messages.properties (默认/中文)
USER_NOT_FOUND=用户不存在
USER_DUPLICATE=用户名 {0} 已存在
ORDER_AMOUNT_INVALID=订单金额 {0} 超出限制，最大允许 {1}
ACCOUNT_INSUFFICIENT=账户余额不足，当前余额 {0}，需要 {1}

# messages_en.properties (英文)
USER_NOT_FOUND=User not found
USER_DUPLICATE=Username {0} already exists
ORDER_AMOUNT_INVALID=Order amount {0} exceeds limit, maximum allowed {1}
ACCOUNT_INSUFFICIENT=Insufficient balance, current {0}, required {1}
```

### 2. 抛出 LocaleMessageException

```java
// 简单错误码
throw new LocaleMessageException("USER_NOT_FOUND");

// 带占位符参数
throw LocaleMessageException.of("USER_DUPLICATE", username);
throw LocaleMessageException.of("ORDER_AMOUNT_INVALID", amount, maxAmount);

// 带原始异常
throw new LocaleMessageException("SYSTEM_ERROR", cause);

// 直接指定消息（不走国际化）
throw new LocaleMessageException("CUSTOM_ERROR", "自定义错误消息");
```

### 3. 获取错误码和消息

```java
try {
    userService.create(cmd);
} catch (LocaleMessageException e) {
    String errCode = e.getErrCode();     // "USER_DUPLICATE"
    String errMsg = e.getErrMessage();   // "用户名 admin 已存在"
}
```

### 4. 处理 EventException

当多个事件处理器中有多个抛出异常时，框架会将它们收集到 `EventException` 中：

```java
try {
    EventPusher.push(new OrderCreatedEvent(orderId));
} catch (EventException e) {
    List<Exception> errors = e.getError();
    log.error("Event processing failed with {} errors", errors.size());
    for (Exception error : errors) {
        log.error("  - {}", error.getMessage());
    }
}
```

### 5. 处理 EventLoopException

当事件形成循环调用时自动抛出：

```java
try {
    EventPusher.push(new EventA());
} catch (EventLoopException e) {
    List<Class<?>> stack = e.getStack();
    log.error("Event loop detected! Stack: {}", stack);
    // 输出: event loop error current event class:class EventA, 
    //       history event stack:[class EventA, class EventB]
}
```

## 使用实例

### 完整的 Service 层异常处理

```java
@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public User createUser(CreateUserCommand cmd) {
        // 1. 唯一性校验 - 带占位符的国际化异常
        if (userRepository.existsByName(cmd.getName())) {
            throw LocaleMessageException.of("USER_DUPLICATE", cmd.getName());
        }

        // 2. 业务规则校验
        if (cmd.getAge() != null && cmd.getAge() < 0) {
            throw LocaleMessageException.of("AGE_INVALID", cmd.getAge());
        }

        // 3. 创建用户
        User user = new User();
        user.setName(cmd.getName());
        user.setAge(cmd.getAge());
        
        try {
            userRepository.save(user);
        } catch (DataAccessException e) {
            // 包装底层异常为业务异常
            throw new LocaleMessageException("USER_CREATE_FAILED", e);
        }

        return user;
    }

    public User findById(Long id) {
        return userRepository.findById(id)
            .orElseThrow(() -> new LocaleMessageException("USER_NOT_FOUND"));
    }

    public void transferPoints(Long fromUserId, Long toUserId, int points) {
        User fromUser = findById(fromUserId);
        User toUser = findById(toUserId);

        if (fromUser.getPoints() < points) {
            throw LocaleMessageException.of("POINTS_INSUFFICIENT", 
                fromUser.getPoints(), points);
        }

        fromUser.setPoints(fromUser.getPoints() - points);
        toUser.setPoints(toUser.getPoints() + points);

        userRepository.save(fromUser);
        userRepository.save(toUser);
    }
}
```

### Controller 层的全局异常处理配合

框架的 `LocaleMessageException` 通常由全局异常处理器捕获并转为标准响应：

```java
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理国际化业务异常
     */
    @ExceptionHandler(LocaleMessageException.class)
    public Response handleLocaleMessageException(LocaleMessageException e) {
        return Response.buildFailure(e.getErrCode(), e.getErrMessage());
    }

    /**
     * 处理事件聚合异常
     */
    @ExceptionHandler(EventException.class)
    public Response handleEventException(EventException e) {
        log.error("Event processing errors", e);
        return Response.buildFailure("EVENT_PROCESSING_ERROR", 
            "事件处理异常，共 " + e.getError().size() + " 个错误");
    }

    /**
     * 处理事件循环异常
     */
    @ExceptionHandler(EventLoopException.class)
    public Response handleEventLoopException(EventLoopException e) {
        log.error("Event loop detected", e);
        return Response.buildFailure("EVENT_LOOP_ERROR", 
            "检测到事件循环调用");
    }
}
```

### messages.properties 完整示例

```properties
# ===== 用户模块 =====
USER_NOT_FOUND=用户不存在
USER_DUPLICATE=用户名 {0} 已存在
USER_DISABLED=用户 {0} 已被禁用
USER_CREATE_FAILED=用户创建失败
AGE_INVALID=年龄 {0} 无效，必须大于等于 0

# ===== 订单模块 =====
ORDER_NOT_FOUND=订单 {0} 不存在
ORDER_STATUS_INVALID=订单状态不允许此操作，当前状态: {0}
ORDER_AMOUNT_INVALID=订单金额 {0} 超出限制，最大允许 {1}
ORDER_ALREADY_PAID=订单 {0} 已支付，请勿重复操作

# ===== 账户模块 =====
ACCOUNT_NOT_FOUND=账户 {0} 不存在
ACCOUNT_INSUFFICIENT=账户余额不足，当前余额 {0}，需要 {1}
ACCOUNT_FROZEN=账户 {0} 已冻结

# ===== 系统模块 =====
SYSTEM_ERROR=系统内部错误
PARAM_MISSING=缺少必要参数: {0}
PARAM_INVALID=参数 {0} 格式不正确
UNAUTHORIZED=未授权访问
FORBIDDEN=无权执行此操作
```

### 异常类层次结构

```
RuntimeException
  ├── LocaleMessageException          — 国际化业务异常
  │     └── getErrCode()              — 错误码
  │     └── getErrMessage()           — 国际化后的错误消息
  │
  ├── EventException                  — 事件处理器聚合异常
  │     └── getError(): List<Exception> — 所有处理器抛出的异常列表
  │
  └── EventLoopException              — 事件循环调用异常
        └── getStack(): List<Class<?>>  — 事件调用栈（按触发顺序）
```

### 关键设计要点

1. **errCode 是稳定的标识符**：前端应根据 errCode 而非 errMessage 做逻辑判断，因为 errMessage 会随语言变化
2. **占位符从 {0} 开始**：遵循 Java MessageFormat 规范
3. **LocaleMessage 自动初始化**：通过 `ExceptionConfiguration` 的 `@Bean(initMethod = "init")` 自动注册到 `MessageContext`
4. **EventException 保留原始异常链**：通过 `addSuppressed()` 将所有子异常附加到主异常上，确保日志可追溯
5. **EventLoopException 包含完整调用栈**：stack 列表按事件触发顺序排列，便于定位循环点
6. **线程安全**：`MessageContext` 使用单例模式 + synchronized 保证初始化安全；`LocaleContextHolder` 保证多语言请求隔离
