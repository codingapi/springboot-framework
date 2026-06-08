---
name: ddd-layered-architecture
description: DDD 分层架构规范 — 遵循 interface → app → domain ← infra 四层依赖规则，domain 层定义接口，infra 层提供实现
status: 已实现
scope: 后端
source: 项目自有
symbols:
  - IDomain
content_hash: dc7a9d76224be09498f0278ec05c56ea34b0c5e2dbb80850ccb826fabe721da6
---

## 解决什么问题

不遵守此规范会导致：
- 业务逻辑散落在 Controller 或基础设施层，难以测试和复用
- 层间循环依赖，修改一处牵动全局
- 领域模型贫血（只有 getter/setter，没有业务行为）
- 基础设施变更（如换数据库、换消息队列）影响业务代码

## 如何使用

### 四层架构

```
example/
  example-interface/    ← 接口层：接收外部请求
  example-app/          ← 应用层：编排业务流程
  example-domain/       ← 领域层：核心业务逻辑
  example-infra/        ← 基础设施层：技术实现
```

### 依赖规则

```
interface → app → domain ← infra
```

- **interface → app**：接口层调用应用层服务
- **app → domain**：应用层调用领域层服务和实体
- **infra → domain**：基础设施层实现领域层定义的接口
- **domain 不依赖任何层**：领域层是核心，独立存在

### 各层职责

| 层 | 职责 | 包含 |
|----|------|------|
| **interface** | 接收 HTTP 请求，参数转换，响应封装 | Controller、Handler（事件处理）、Runner |
| **app** | 编排业务流程，调用领域服务，管理事务 | 命令服务（cmd）、查询服务（query） |
| **domain** | 核心业务逻辑，定义 Repository 接口和 Gateway 接口 | Entity、Repository（接口）、Service、Event |
| **infra** | 技术实现：数据库、缓存、消息队列、外部 API | Repository（实现）、Entity（JPA）、Gateway（实现） |

### CQRS 模式

应用层按读写分离：
- **cmd（命令侧）**：写操作，调用领域服务
- **query（查询侧）**：读操作，直接查询数据库

## 使用实例

✅ **正确示例**：

```java
// domain 层 — 定义接口
public interface UserRepository {
    Optional<User> findById(Long id);
    User save(User user);
}

// infra 层 — 提供实现
@Repository
public class UserRepositoryImpl implements UserRepository {
    @Override
    public Optional<User> findById(Long id) {
        return jpaRepository.findById(id).map(UserConvertor::toDomain);
    }
}

// app 层 — 编排
@Service
public class UserCommandService {
    @Autowired
    private UserRepository userRepository;  // 依赖 domain 层接口

    @Transactional
    public void registerUser(RegisterCmd cmd) {
        User user = new User(cmd.getUsername(), cmd.getEmail());
        userRepository.save(user);
        EventPusher.push(new UserRegisteredEvent(user.getId()));
    }
}

// interface 层 — 接收请求
@RestController
@RequestMapping("/api/users")
public class UserController {
    @Autowired
    private UserCommandService commandService;  // 依赖 app 层

    @PostMapping("/register")
    public Response register(@RequestBody RegisterCmd cmd) {
        commandService.registerUser(cmd);
        return Response.buildSuccess();
    }
}
```

❌ **错误示例**：

```java
// domain 层直接依赖 infra 实现（违反依赖方向）
public class User {
    @Autowired
    private UserRepositoryImpl repository;  // ❌ 不应依赖实现类
}

// Controller 直接调用 Repository（跳过 app 层）
@RestController
public class UserController {
    @Autowired
    private UserRepository userRepository;  // ❌ 应通过 app 层
}

// 业务逻辑在 Controller 中（应在 domain 层）
@PostMapping
public Response create(@RequestBody UserDTO dto) {
    if (dto.getAge() < 18) { /* ❌ 业务逻辑不应在 Controller */ }
    // ...
}
```
