---
name: user-context
description: 线程级用户身份上下文，通过 ThreadLocal 持有当前登录用户，支持跨层传递
status: 已实现
scope: 后端
source: 项目自有
last_commit: 719167a0
code_files:
  - springboot-starter/src/main/java/com/codingapi/springboot/framework/user/UserContext.java
  - springboot-starter/src/main/java/com/codingapi/springboot/framework/user/IUser.java
---

## 解决什么问题

在企业应用中，几乎所有业务操作都需要知道"当前操作用户是谁"。传统做法是在方法签名中逐层传递用户对象（Controller → Service → Repository），导致：

- **参数污染**：每个业务方法都需要额外接收用户参数，签名臃肿
- **跨层耦合**：中间层即使不使用用户信息也必须透传
- **框架集成困难**：事件处理器、异步任务、AOP 切面等非调用链场景无法方便地获取用户身份

本能力通过 `ThreadLocal` 在当前线程中绑定用户身份对象，使任意层级的代码都能直接获取当前登录用户，无需显式传参。配合安全模块（`springboot-starter-security`）的认证过滤器，在请求入口处自动设置、请求结束时自动清理。

## 如何使用

### 核心接口与类

| 类型 | 说明 |
|------|------|
| `IUser` | 用户身份标记接口，业务模块实现此接口定义自己的用户实体 |
| `UserContext` | 单例用户上下文管理器，基于 `ThreadLocal<IUser>` 存储当前线程的用户身份 |

### API

```java
// 获取全局单例
UserContext ctx = UserContext.getInstance();

// 设置当前线程的用户身份
ctx.setCurrent(IUser user);

// 获取当前线程的用户身份
IUser user = ctx.current();
```

### 实现 IUser

`IUser` 是一个纯标记接口（无方法声明），业务模块自行定义用户属性：

```java
public class LoginUser implements IUser {
    private String userId;
    private String username;
    private List<String> roles;
    // getter/setter...
}
```

### 与安全模块集成

在认证过滤器中设置用户上下文：

```java
// 认证通过后设置
UserContext.getInstance().setCurrent(loginUser);

// 请求结束后清理（防止线程池复用导致上下文泄漏）
UserContext.getInstance().setCurrent(null);
```

### 注意事项

- `UserContext` 基于 `ThreadLocal`，仅在同一个线程内有效
- 异步场景（`@Async`、线程池）需要手动传递或配合 `TaskDecorator` 复制上下文
- 务必在请求结束时清理（设为 `null`），避免线程池场景下的用户信息泄漏

## 使用实例

### 1. 在 Service 层获取当前用户

```java
@Service
public class OrderService {

    public Order createOrder(CreateOrderCmd cmd) {
        // 无需方法参数传入用户，直接从上下文获取
        IUser currentUser = UserContext.getInstance().current();
        LoginUser loginUser = (LoginUser) currentUser;

        Order order = new Order();
        order.setCreatorId(loginUser.getUserId());
        order.setCreatorName(loginUser.getUsername());
        // ...
        return orderRepository.save(order);
    }
}
```

### 2. 在事件 Handler 中获取操作用户

```java
@Handler
@Component
public class AuditLogHandler implements IHandler<DomainCreateEvent> {

    @Override
    public void handler(DomainCreateEvent event) {
        IUser user = UserContext.getInstance().current();
        // 记录审计日志：谁在什么时候创建了什么
        auditLogRepository.save(new AuditLog(
            user != null ? user.toString() : "system",
            event.getEntityClass().getSimpleName(),
            LocalDateTime.now()
        ));
    }
}
```

### 3. 在认证过滤器中设置与清理

```java
@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain)
            throws ServletException, IOException {
        try {
            // 解析 Token → 构建用户对象
            LoginUser loginUser = tokenGateway.parseAndBuildUser(request);
            if (loginUser != null) {
                UserContext.getInstance().setCurrent(loginUser);
            }
            chain.doFilter(request, response);
        } finally {
            // 请求结束必须清理，防止线程池复用导致上下文污染
            UserContext.getInstance().setCurrent(null);
        }
    }
}
```
