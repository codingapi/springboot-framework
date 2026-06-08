---
name: springboot-starter/ddd-layered-architecture
module: springboot-starter
description: DDD 分层架构规范，定义 interface/app/domain/infra 四层的依赖规则和职责划分
status: 已实现
scope: 后端
source: 框架:springboot-starter
import: "com.codingapi.springboot:springboot-starter"
framework_version: "3.4.54"
---

## 解决什么问题

不遵守 DDD 分层架构规范会导致以下问题：

1. **领域逻辑污染**：domain 层直接引用 infra 层实现类（如 JPA Repository、Redis Client），导致业务规则与基础设施耦合，无法独立演进和测试。
2. **依赖方向混乱**：若 domain 反向依赖 app 或 interface，修改一个 API 接口可能牵连整个领域模型重构，变更成本指数级增长。
3. **CQRS 读写混淆**：查询服务与命令服务混杂在同一模块中，复杂查询被迫走聚合根加载全量数据，性能瓶颈难以定位和优化。
4. **接口层职责膨胀**：Controller 中直接编排业务流程、拼装 DTO、处理事件，导致接口层成为"上帝类"，复用性和可维护性急剧下降。
5. **Repository 契约缺失**：domain 层直接使用具体持久化实现而非接口，切换存储方案（如从 MySQL 迁移到 MongoDB）需要改动所有调用方代码。

## 如何使用

### 四层架构与依赖方向

```
interface → app → domain ← infra
```

| 层级 | 模块命名约定 | 核心职责 | 允许依赖 |
|------|-------------|---------|---------|
| **interface**（接口层） | `*-interface` | Controller（REST API）、Handler（事件处理器）、Runner（启动任务） | app |
| **app**（应用层） | `*-app-query` / `*-app-cmd-*` | query：CQRS 查询服务；cmd：命令服务与领域编排 | domain |
| **domain**（领域层） | `*-domain-*` | Entity、ValueObject、DomainService、Repository 接口、DomainEvent | 无外部框架 |
| **infra**（基础设施层） | `*-infra-*` | Repository 实现、Gateway 实现、外部服务适配、持久化配置 | domain |

### 核心规则

1. **依赖方向严格单向**：`interface → app → domain ← infra`。禁止反向依赖和跨层依赖。
2. **domain 层纯业务**：domain 层只包含领域模型和业务逻辑，不依赖 Spring、JPA、Redis 等任何外部框架。
3. **Repository 接口在 domain，实现在 infra**：domain 层定义 Repository 接口，infra 层提供具体实现并通过 Spring Bean 注入。
4. **app 层按 CQRS 拆分**：查询服务（query）与命令服务（cmd）分模块，query 侧可直接读取视图/DTO，cmd 侧通过聚合根执行业务操作。
5. **interface 层仅做适配**：Controller 只做参数校验、权限检查和响应封装，业务编排委托给 app 层；Handler 只负责事件监听与转发。
6. **禁止 domain 引用 infra 实现类**：domain 层不得 import infra 包下的任何类，包括具体的 Repository 实现、DAO、Client 等。

### 包结构约定

```
{bounded-context}/
  {context}-interface/        # 接口层
    controller/
    handler/
    runner/
  {context}-app/              # 应用层
    {context}-app-query/      # CQRS Query 侧
    {context}-app-cmd-domain/ # CQRS Command 侧（领域编排）
  {context}-domain/           # 领域层
    model/                    # Entity, ValueObject
    repository/               # Repository 接口
    service/                  # DomainService
    event/                    # DomainEvent
  {context}-infra/            # 基础设施层
    jpa/                      # Repository 实现
    gateway/                  # 外部服务适配
```

## 使用实例

### ✅ 正确示例

**domain 层定义 Repository 接口：**

```java
// example-domain-user/src/.../repository/UserRepository.java
package com.example.domain.user.repository;

public interface UserRepository {
    User findById(Long id);
    void save(User user);
}
```

**infra 层提供 Repository 实现：**

```java
// example-infra-jpa/src/.../jpa/UserRepositoryImpl.java
package com.example.infra.jpa;

import com.example.domain.user.repository.UserRepository;
import org.springframework.stereotype.Repository;

@Repository
public class UserRepositoryImpl implements UserRepository {
    private final UserJpaDao userJpaDao;

    @Override
    public User findById(Long id) {
        return userJpaDao.findById(id).map(UserConverter::toDomain).orElse(null);
    }

    @Override
    public void save(User user) {
        userJpaDao.save(UserConverter.toEntity(user));
    }
}
```

**app 层按 CQRS 拆分：**

```java
// example-app-query: 查询服务，直接返回 DTO
@Service
public class UserQueryService {
    private final UserReadDao userReadDao; // 可直接读视图

    public SingleResponse<UserDTO> getUser(Long id) {
        return SingleResponse.of(userReadDao.findUserDTO(id));
    }
}

// example-app-cmd-domain: 命令服务，通过聚合根操作
@Service
public class UserCommandService {
    private final UserRepository userRepository; // domain 层接口

    @Transactional
    public Response createUser(CreateUserCmd cmd) {
        User user = User.create(cmd.getName(), cmd.getEmail());
        userRepository.save(user);
        return Response.success();
    }
}
```

**interface 层仅做适配：**

```java
@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserQueryService queryService;
    private final UserCommandService commandService;

    @GetMapping("/{id}")
    public SingleResponse<UserDTO> getUser(@PathVariable Long id) {
        return queryService.getUser(id); // 委托 app 层
    }

    @PostMapping
    public Response createUser(@Valid @RequestBody CreateUserCmd cmd) {
        return commandService.createUser(cmd); // 委托 app 层
    }
}
```

### ❌ 错误示例

**domain 层直接引用 infra 实现类：**

```java
// ❌ domain 层 import 了 infra 包的类
package com.example.domain.user.service;

import com.example.infra.jpa.UserJpaDao; // 违规！domain 不应依赖 infra

@Service
public class UserService {
    private final UserJpaDao userJpaDao; // 应使用 UserRepository 接口

    public User findUser(Long id) {
        return userJpaDao.findById(id).orElse(null);
    }
}
```

**依赖方向反转：**

```java
// ❌ domain 层反向依赖 app 层
package com.example.domain.user.model;

import com.example.app.cmd.domain.UserService; // 违规！domain 不应依赖 app

public class User {
    public void activate() {
        UserService userService = ...; // 领域对象不应调用应用服务
        userService.sendActivationEmail(this);
    }
}
```

**app 层未拆分 CQRS，查询走聚合根：**

```java
// ❌ 查询和命令混在一起，列表查询加载完整聚合根
@Service
public class UserService {
    private final UserRepository userRepository;

    // 列表查询不应加载完整 User 聚合根
    public List<User> listUsers(String keyword) {
        return userRepository.findAll().stream()
            .filter(u -> u.getName().contains(keyword))
            .collect(Collectors.toList()); // 内存过滤，性能灾难
    }
}
```

**Controller 中直接编排业务逻辑：**

```java
// ❌ Controller 承担了本应属于 app 层的职责
@PostMapping("/orders")
public Response createOrder(@RequestBody OrderCmd cmd) {
    User user = userRepository.findById(cmd.getUserId());     // 应在 app 层
    Product product = productRepository.findById(cmd.getProductId()); // 应在 app 层
    if (product.getStock() < cmd.getQuantity()) {             // 业务校验应在 domain
        return Response.error("库存不足");
    }
    Order order = new Order(user, product, cmd.getQuantity()); // 领域创建应在 app/cmd
    orderRepository.save(order);                               // 应在 app 层
    eventPusher.push(new OrderCreatedEvent(order));            // 事件推送应在 app 层
    return Response.success(order.getId());
}
```
