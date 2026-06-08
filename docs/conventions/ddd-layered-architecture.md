---
name: ddd-layered-architecture
description: DDD 分层架构与依赖规范
status: 已实现
scope: 后端
source: 项目自有
---

## 解决什么问题

项目采用领域驱动设计（DDD）分层架构，解决了以下问题：

- **关注点分离**：将接口、应用、领域、基础设施四层职责清晰划分
- **领域纯净**：domain 层不依赖任何外部框架和基础设施实现，只包含纯业务逻辑
- **可替换性**：infra 层的实现可以替换（如 JPA → MyBatis），不影响 domain 层
- **CQRS 分离**：查询侧（Query）和命令侧（Command）独立演进，避免读写耦合

## 如何使用

### 分层结构与依赖方向

```
interface 层（Controller + Handler + Runner）
      ↓ 依赖
app 层（Query Service / Cmd Router）
      ↓ 依赖
domain 层（Entity / Repository接口 / Domain Service / Event / Gateway接口）
      ↑ 实现（反向依赖）
infra 层（JPA Entity / Repository实现 / Convertor / Flow集成 / Security配置）
```

**核心规则**：依赖只能从上到下，domain 层绝不依赖 infra 层。infra 层通过实现 domain 层定义的接口来注入。

### 模块命名规范

| 层级 | 模块命名 | 示例 |
|------|---------|------|
| 接口层 | `example-interface` | Controller、Handler、Runner |
| 应用查询 | `example-app-query` | QueryService |
| 应用命令 | `example-app-cmd-domain` | Router / CmdService |
| 应用元数据 | `example-app-cmd-meta` | 元数据相关命令 |
| 领域 | `example-domain-{聚合名}` | Entity、Repository接口、Service |
| 基础设施 | `example-infra-{技术}` | JPA实现、Flow集成、Security配置 |

### 包结构规范

```
com.codingapi.example.
├── api.domain/          # interface 层 - 命令 Controller
├── api.query/           # interface 层 - 查询 Controller
├── handler/             # interface 层 - 事件 Handler
├── runner/              # interface 层 - 启动 Runner
├── app.query.service/   # app 层 - 查询服务
├── app.cmd.domain/      # app 层 - 命令路由
├── domain.{聚合}.entity/       # domain 层 - 实体
├── domain.{聚合}.repository/   # domain 层 - 仓库接口
├── domain.{聚合}.service/      # domain 层 - 领域服务
├── domain.{聚合}.event/        # domain 层 - 领域事件
├── domain.{聚合}.gateway/      # domain 层 - 外部网关接口
├── infra.db.entity/            # infra 层 - JPA Entity
├── infra.db.jpa/               # infra 层 - JPA Repository
├── infra.db.repository/        # infra 层 - Domain Repository 实现
└── infra.db.convert/           # infra 层 - Entity 转换器
```

## 使用实例

### ✅ 正确示例：Domain 层定义 Repository 接口

```java
// example-domain-user —— 纯 Java 接口，无任何框架依赖
package com.codingapi.example.domain.user.repository;

public interface UserRepository {
    User getUserByUsername(String username);
    User getUserById(long id);
    void save(User user);
    void delete(long id);
}
```

### ✅ 正确示例：Infra 层实现 Repository

```java
// example-infra-jpa —— 实现 domain 层接口，内部委托 JPA
@Repository
@AllArgsConstructor
public class UserRepositoryImpl implements UserRepository {

    private final UserEntityRepository userEntityRepository;

    @Override
    public User getUserByUsername(String username) {
        return UserConvertor.convert(
            userEntityRepository.getUserEntityByUsername(username),
            userEntityRepository
        );
    }

    @Override
    public void save(User user) {
        UserEntity entity = UserConvertor.convert(user);
        entity = userEntityRepository.save(entity);
        user.setId(entity.getId());
    }
}
```

### ✅ 正确示例：Interface 层按 CQRS 分离 Controller

```java
// 查询 Controller —— 调用 app-query 服务
@RestController
@RequestMapping("/api/query/user")
@AllArgsConstructor
public class UserQueryController {
    private final UserQueryService userQueryService;

    @GetMapping("/list")
    public MultiResponse<UserEntity> list(SearchRequest searchRequest) {
        return MultiResponse.of(userQueryService.list(searchRequest));
    }
}

// 命令 Controller —— 调用 app-cmd 路由
@RestController
@RequestMapping("/api/cmd/user")
@AllArgsConstructor
public class UserDomainCmdController {
    private final UserRouter userRouter;

    @PostMapping("/save")
    public Response save(@RequestBody UserCmd.UpdateRequest request) {
        userRouter.createOrUpdate(request);
        return Response.buildSuccess();
    }
}
```

### ❌ 错误示例：Domain 层直接依赖 JPA

```java
// domain 层不应引入 javax.persistence / jakarta.persistence
@Entity  // ← 禁止！domain 层不能使用 JPA 注解
@Table(name = "t_user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
}
```

### ❌ 错误示例：Domain 层依赖 Infra 层的具体类

```java
package com.codingapi.example.domain.user.service;

import com.codingapi.example.infra.db.jpa.UserEntityRepository;  // ← 禁止！

public class UserService {
    private final UserEntityRepository repository;  // 应使用 UserRepository 接口
}
```

### ❌ 错误示例：查询和命令混在同一个 Controller

```java
@RestController
@RequestMapping("/api/user")
public class UserController {
    // 查询方法
    @GetMapping("/list")
    public MultiResponse<User> list() { ... }

    // 命令方法 —— 不应与查询混在一起
    @PostMapping("/save")
    public Response save(@RequestBody UserCmd cmd) { ... }

    @DeleteMapping("/remove")
    public Response remove(@RequestBody IdRequest req) { ... }
}
```
