---
name: springboot-starter/exception-handling-convention
module: springboot-starter
description: 全局异常处理规范，定义异常抛出和捕获的标准模式
status: 已实现
scope: 后端
source: 项目自有
import: "com.codingapi.springboot:springboot-starter"
content_hash: 669e54861ccb844ae926652bdd6b30a9a1b8bbc9f9a55ce145d19c283d499596
---

## 解决什么问题

不遵守此规范会导致以下问题：

1. **错误响应格式不一致**：不同开发者在 Controller 中自行 try-catch 并返回自定义格式的错误信息，导致前端无法统一解析 `errCode` / `errMessage` 字段，增加联调和维护成本。
2. **缺失国际化支持**：直接抛出 `RuntimeException` 或硬编码中文消息，无法根据请求 Locale 切换语言，多语言场景下用户体验差且需要大量改造。
3. **异常被吞没**：Controller 层使用 try-catch 捕获异常后仅打印日志或返回 null，调用方无法感知业务失败原因，排查线上问题时缺少关键上下文。
4. **错误码体系混乱**：各模块自行定义错误码常量或直接拼接字符串，缺乏统一的 key → message 映射机制，同一个业务含义可能出现多个不同的 errCode。
5. **全局拦截失效**：绕过框架的 `HandlerExceptionResolver` 机制手动处理异常，导致日志记录、监控埋点等横切关注点被跳过。

## 如何使用

### 规则 1：业务异常统一使用 LocaleMessageException

所有业务校验失败、参数非法、权限不足等场景，必须抛出 `LocaleMessageException`，禁止直接使用 `RuntimeException`、`IllegalArgumentException` 等原生异常。

```java
// 构造方式一：errCode + 默认消息（推荐用于简单场景）
throw new LocaleMessageException("user.not.found", "用户不存在");

// 构造方式二：仅 errCode，消息从 message.properties 自动解析
throw new LocaleMessageException("user.not.found");

// 构造方式三：带占位符参数，对应 properties 中 user.duplicate=用户名 {0} 已存在
throw LocaleMessageException.of("user.duplicate", username);
```

### 规则 2：异常消息采用 message key + 默认消息格式

- 第一个参数为 **errCode**（即 i18n message key），用于前端匹配和国际化查找。
- 第二个参数为 **默认消息**（defaultMessage），当 message.properties 中未配置该 key 时作为兜底展示。
- errCode 命名采用 **点分隔小写** 格式，如 `order.status.invalid`、`auth.token.expired`。

### 规则 3：依赖 ExceptionConfiguration 全局拦截

框架通过 `ExceptionConfiguration` 注册 `LocaleMessage` Bean，并由 `BasicHandlerExceptionResolverConfiguration` 中的 `ServletExceptionHandler` 统一拦截所有 Controller 层异常：

- `LocaleMessageException` → 提取 `errCode` 和 `errMessage`，返回标准 Response 格式。
- 其他未知异常 → 使用默认 errCode `system.err`，返回异常原始消息。

**无需在任何 Controller 中添加额外的 @ExceptionHandler。**

### 规则 4：异常响应统一返回 Response 格式

全局拦截器返回的 JSON 结构如下：

```json
{
  "success": false,
  "errCode": "user.not.found",
  "errMessage": "用户不存在"
}
```

前端只需判断 `success === false` 即可进入统一错误处理流程。

### 规则 5：禁止在 Controller 中使用 try-catch 吞掉异常

Controller 方法应保持简洁，让异常自然向上抛出由全局处理器接管。如需对特定异常做差异化处理（如参数校验），应在 Service 层完成转换后再抛出 `LocaleMessageException`。

### 规则 6：禁止直接抛出 RuntimeException

`RuntimeException` 没有 `errCode` 字段，全局拦截器只能将其归入 `system.err`，丢失了业务语义。所有可预见的业务异常都必须使用 `LocaleMessageException`。

## 使用实例

### ✅ 正确示例

```java
@Service
public class UserService {

    public User getUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new LocaleMessageException("user.not.found", "用户不存在"));
    }

    public void createUser(String username) {
        if (userRepository.existsByUsername(username)) {
            // 使用静态工厂方法 + 占位符参数
            throw LocaleMessageException.of("user.duplicate", username);
        }
        // ...
    }
}

@RestController
@RequestMapping("/users")
public class UserController {

    @GetMapping("/{id}")
    public SingleResponse<User> getUser(@PathVariable Long id) {
        // 不需要 try-catch，异常由全局处理器接管
        return SingleResponse.of(userService.getUser(id));
    }
}
```

对应的 `messages_zh_CN.properties`：

```properties
user.not.found=用户不存在
user.duplicate=用户名 {0} 已存在
```

### ❌ 错误示例

```java
// 错误 1：直接抛出 RuntimeException，缺少 errCode
public User getUser(Long id) {
    return userRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("用户不存在"));
}

// 错误 2：Controller 中 try-catch 吞掉异常
@GetMapping("/{id}")
public SingleResponse<User> getUser(@PathVariable Long id) {
    try {
        return SingleResponse.of(userService.getUser(id));
    } catch (Exception e) {
        log.error("查询失败", e);
        return null; // 前端收到 null，无法区分成功与失败
    }
}

// 错误 3：Controller 中自行构建错误响应，绕过全局拦截
@GetMapping("/{id}")
public ResponseEntity<?> getUser(@PathVariable Long id) {
    try {
        return ResponseEntity.ok(SingleResponse.of(userService.getUser(id)));
    } catch (Exception e) {
        Map<String, Object> error = new HashMap<>();
        error.put("code", 500);
        error.put("msg", e.getMessage());
        return ResponseEntity.status(500).body(error);
    }
}

// 错误 4：硬编码中文消息，不支持国际化
throw new LocaleMessageException("err001", "这个用户找不到啊");
```
