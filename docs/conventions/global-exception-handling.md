---
name: global-exception-handling
description: 全局异常处理规范 — 业务异常必须使用 LocaleMessageException，禁止在 Controller 中 try-catch 吞没异常
status: 已实现
scope: 后端
source: 项目自有
symbols:
  - LocaleMessageException
  - BasicHandlerExceptionResolverConfiguration
  - ServletExceptionHandler
content_hash: e3029dc411b34af5ea488d45c2ac414e2ea7578b571e681e0d7dc938ae75f052
---

## 解决什么问题

不遵守此规范会导致：
- 异常堆栈信息直接暴露给前端，存在安全风险
- 不同模块的错误响应格式不一致
- 异常被 try-catch 吞没后，问题难以排查
- 无法支持国际化错误消息

## 如何使用

### 异常类型规范

| 异常类型 | 使用场景 |
|----------|----------|
| `LocaleMessageException` | 所有业务异常（参数校验失败、数据不存在、状态错误等） |
| `IllegalArgumentException` | 框架级参数校验（不推荐在业务层使用） |
| `RuntimeException` | 不可预期的系统异常 |

### 错误码命名规范

错误码采用 **英文点分隔** 的格式：`模块.实体.错误类型`

```java
// ✅ 正确的错误码
throw new LocaleMessageException("user.not.found", "用户不存在");
throw new LocaleMessageException("order.status.invalid", "订单状态无效");
throw new LocaleMessageException("auth.permission.denied", "权限不足");

// ❌ 错误的错误码
throw new LocaleMessageException("USER_NOT_FOUND", "用户不存在");  // 不应大写
throw new LocaleMessageException("userNotFound", "用户不存在");     // 不应驼峰
```

### 异常处理规则

1. **Controller 层**：不 try-catch，直接抛出 `LocaleMessageException`，由全局处理器捕获
2. **Service 层**：业务校验失败抛 `LocaleMessageException`，系统异常可包装后向上抛
3. **基础设施层**：捕获底层异常，转换为 `LocaleMessageException` 向上传递

## 使用实例

✅ **正确示例**：
```java
@Service
public class UserService {
    public User findById(Long id) {
        return userRepository.findById(id)
            .orElseThrow(() -> new LocaleMessageException("user.not.found", "用户不存在"));
    }

    public void delete(Long id) {
        User user = findById(id);
        if ("ADMIN".equals(user.getRole())) {
            throw new LocaleMessageException("user.admin.cannot.delete", "管理员账户不可删除");
        }
        userRepository.delete(user);
    }
}

@RestController
public class UserController {
    @DeleteMapping("/{id}")
    public Response delete(@PathVariable Long id) {
        userService.delete(id);  // 不 try-catch，异常由全局处理器捕获
        return Response.buildSuccess();
    }
}
```

❌ **错误示例**：
```java
// 在 Controller 中 try-catch 吞没异常
@GetMapping("/{id}")
public SingleResponse<User> get(@PathVariable Long id) {
    try {
        User user = userService.findById(id);
        return SingleResponse.of(user);
    } catch (Exception e) {
        log.error("error", e);
        return null;  // 返回 null，前端无法识别错误
    }
}

// 直接抛出原始异常
throw new RuntimeException("数据库连接失败");  // 应包装为 LocaleMessageException
```
