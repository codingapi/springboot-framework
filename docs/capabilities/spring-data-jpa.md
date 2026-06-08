---
name: spring-data-jpa
description: Spring Data JPA 数据访问能力，提供 ORM 映射、Repository 抽象、分页查询等基础设施，框架在此基础上扩展了 FastRepository 动态查询
status: 已实现
scope: 后端
source: 框架:spring-data-jpa
framework_version: Managed by Spring Boot BOM (3.3.5)
---

## 解决什么问题

Spring Data JPA 是框架数据持久层的基石，解决了以下问题：

- **ORM 映射**：通过 JPA 注解将领域实体映射到关系数据库表，框架中所有领域对象（如 `FlowWork`、`FlowRecord`、`FlowNode` 等）均基于 JPA Entity 定义
- **Repository 抽象**：提供 `JpaRepository` 接口，自动生成 CRUD 实现，消除样板代码
- **分页查询**：原生支持 `Pageable` / `Page` 分页抽象，框架在此基础上扩展了 `PageRequest` + `RequestFilter` 动态过滤能力

框架的 `springboot-starter-data-fast` 模块在 Spring Data JPA 之上构建了 `FastRepository`，支持基于 Filter 的动态 Example 查询和 HQL 构建，大幅简化复杂条件查询的开发。

## 如何使用

### 依赖引入

```xml
<dependency>
    <groupId>com.codingapi.springboot</groupId>
    <artifactId>springboot-starter-data-fast</artifactId>
</dependency>
```

该模块已传递依赖 `spring-boot-starter-data-jpa`，无需单独引入。

### Repository 定义

继承 `FastRepository` 而非原生的 `JpaRepository`，即可获得动态过滤查询能力：

```java
public interface UserRepository extends FastRepository<UserEntity, Long> {
    // 自动拥有 findAll(PageRequest)、pageRequest(PageRequest)、searchRequest(SearchRequest) 等方法
}
```

### 动态过滤查询

`PageRequest` 扩展了 Spring Data 的 `org.springframework.data.domain.PageRequest`，增加了 `RequestFilter`：

```java
PageRequest request = PageRequest.of(0, 20);
request.addFilter("name", "张三");
request.addFilter("age", Relation.GT, 18);
Page<UserEntity> page = userRepository.findAll(request);
```

当 Filter 存在时，`findAll` 自动构建 Example 查询；`pageRequest` 方法则构建 HQL 动态 SQL，支持更复杂的关联查询。

## 使用实例

### 基础 CRUD + 动态过滤

```java
@Service
@AllArgsConstructor
public class UserQueryService {

    private final UserRepository userRepository;

    // 简单分页（无过滤）
    public Page<UserEntity> listUsers(int page, int size) {
        return userRepository.findAll(PageRequest.of(page, size));
    }

    // 带动态过滤的分页查询
    public Page<UserEntity> searchUsers(String name, Integer minAge) {
        PageRequest request = PageRequest.of(0, 20);
        if (name != null) {
            request.addFilter("name", name);
        }
        if (minAge != null) {
            request.addFilter("age", Relation.GTE, minAge);
        }
        return userRepository.findAll(request);
    }

    // 使用 HQL 动态查询（支持关联字段、OR 条件等）
    public Page<UserEntity> advancedSearch(SearchRequest searchRequest) {
        return userRepository.searchRequest(searchRequest);
    }
}
```

### JPA Entity 定义

```java
@Entity
@Table(name = "t_user")
@Getter @Setter
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private Integer age;
    private String email;
}
```
