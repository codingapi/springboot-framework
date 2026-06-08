---
name: spring-data-jpa
description: ORM 映射、Repository 抽象、自动分页
status: 已实现
scope: 后端
source: 框架:Spring Data JPA
---

## 解决什么问题

Spring Data JPA 是项目中数据持久化的基础层，解决了以下问题：

- **ORM 映射**：将数据库表映射为 Java Entity，减少手写 SQL
- **Repository 抽象**：通过接口定义数据访问方法，由框架自动生成实现
- **自动分页**：内置 `Pageable` / `Page` 支持，简化分页查询逻辑
- **作为 FastRepository 的底层**：项目的 `springboot-starter-data-fast` 模块在此基础上扩展了动态过滤查询能力

## 如何使用

项目使用 `spring-boot-starter-data-jpa`（随 Spring Boot 3.3.5），在 DDD 架构中处于 infra 层：

1. **Entity 定义**：在 `example-infra-jpa` 模块中定义 JPA Entity（如 `UserEntity`）
2. **JPA Repository**：继承 `JpaRepository` 或 `FastRepository` 获得 CRUD + 动态查询能力
3. **Domain Repository 实现**：在 infra 层实现 domain 层定义的 Repository 接口，内部委托给 JPA Repository
4. **Convertor 转换**：使用 Convertor 类在 Entity 与 Domain Entity 之间转换，隔离持久化细节

## 使用实例

### JPA Repository 定义

```java
// example-infra-jpa 模块
public interface UserEntityRepository extends FastRepository<UserEntity, Long> {

    UserEntity getUserEntityByUsername(String username);

    UserEntity getUserEntityById(long id);
}
```

### Domain Repository 接口（domain 层）

```java
// example-domain-user 模块 —— 仅定义接口，不依赖 JPA
public interface UserRepository {
    User getUserByUsername(String username);
    User getUserById(long id);
    void save(User user);
    void delete(long id);
}
```

### Infra 层实现（桥接 Domain 与 JPA）

```java
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
