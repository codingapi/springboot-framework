---
name: spring-data-jpa
description: Spring Data JPA — ORM 映射、Repository 抽象、分页查询、Specification 动态查询
status: 已实现
scope: 后端
source: 框架:Spring Data JPA
import: org.springframework.boot:spring-boot-starter-data-jpa
framework_version: (由 Spring Boot 3.3.5 管理)
---

## 解决什么问题

Spring Data JPA 是项目持久层的标准基础：

- **ORM 映射**：JPA 注解定义实体与表的映射关系
- **Repository 抽象**：`JpaRepository` 提供标准 CRUD 操作
- **分页查询**：`Pageable` / `Page<T>` 标准化分页接口
- **Specification**：`JpaSpecificationExecutor` 支持动态条件查询
- **命名查询**：通过方法名自动推导 SQL（`findByUsername`）

## 如何使用

### 基础 Repository

```java
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    List<User> findByStatus(String status);
}
```

### 与本框架集成

本框架的 `FastRepository` 扩展了 Spring Data JPA：

```java
// FastRepository 继承 JpaRepository + JpaSpecificationExecutor
public interface UserRepository extends FastRepository<User, Long> {
    // 继承 findAll(PageRequest) — 自动构建 Example/HQL
}
```

### 分页查询

```java
Page<User> page = userRepository.findAll(
    org.springframework.data.domain.PageRequest.of(0, 20, Sort.by("id"))
);
```

## 使用实例

```java
@Entity
@Table(name = "sys_user")
public class User {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String username;
    private String email;
    private String status;
}

// 标准 CRUD
userRepository.save(user);
userRepository.findById(id);
userRepository.delete(user);
```
