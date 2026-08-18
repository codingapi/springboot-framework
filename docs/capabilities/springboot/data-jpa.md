---
name: springboot/data-jpa
module: springboot
description: Spring Data JPA，提供 ORM 映射、Repository 抽象和分页支持
status: 已实现
scope: 后端
source: 框架:springboot
import: "org.springframework.boot:spring-boot-starter-data-jpa"
framework_version: 3.3.5
---

## 解决什么问题

在企业级 Java 应用中，数据持久化是核心基础设施需求。直接使用 JDBC 或原生 JPA API 存在以下痛点：

- **样板代码冗余**：每个实体都需要手动编写 CRUD 操作、SQL 拼接、结果集映射，重复劳动量大
- **ORM 配置复杂**：原生 Hibernate/JPA 的 XML 或注解配置繁琐，事务管理和 EntityManager 生命周期需要手工控制
- **分页与排序缺乏统一抽象**：不同数据库的分页语法差异大，业务层需要自行处理 offset/limit 逻辑
- **查询方法命名约定缺失**：简单的条件查询也需要手写 JPQL/SQL，无法通过方法名自动推导

Spring Data JPA 通过 Repository 抽象层和自动实现机制，将上述问题封装为声明式接口，开发者只需定义接口和方法签名即可获得完整的持久化能力，大幅降低数据访问层的开发和维护成本。

## 如何使用

### 1. 添加依赖

在 `pom.xml` 中引入 Spring Boot Data JPA Starter：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>
```

### 2. 配置数据源

在 `application.properties` 中配置数据库连接和 JPA 行为：

```properties
spring.datasource.url=jdbc:h2:file:./data/demo
spring.datasource.driver-class-name=org.h2.Driver
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```

### 3. 定义实体类

使用 JPA 注解将 POJO 映射到数据库表：

```java
@Entity
@Table(name = "t_user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 64)
    private String username;

    private String email;

    // getters & setters
}
```

### 4. 定义 Repository 接口

继承 `JpaRepository` 即可获得标准 CRUD、分页、排序等能力；通过方法命名约定可自动生成查询：

```java
public interface UserRepository extends JpaRepository<User, Long> {

    List<User> findByUsername(String username);

    Page<User> findByEmailContaining(String keyword, Pageable pageable);

    Optional<User> findByUsernameAndEmail(String username, String email);
}
```

### 5. 核心接口说明

| 接口 | 说明 |
|------|------|
| `CrudRepository<T, ID>` | 基础 CRUD 操作（save、findById、delete 等） |
| `PagingAndSortingRepository<T, ID>` | 继承 CrudRepository，增加分页和排序支持 |
| `JpaRepository<T, ID>` | 继承上述两者，增加批量操作、flush、Example 查询等 JPA 特有能力 |
| `Specification<T>` | 用于构建动态查询条件的函数式接口 |
| `Pageable` / `PageRequest` | 分页参数抽象（页码、每页大小、排序） |
| `Page<T>` | 分页结果封装（内容列表、总记录数、总页数） |

### 6. 事务管理

Spring Data JPA 默认集成 Spring 声明式事务。Service 层方法添加 `@Transactional` 即可自动管理事务边界：

```java
@Service
public class UserService {

    @Transactional
    public User createUser(User user) {
        return userRepository.save(user);
    }
}
```

## 使用实例

### 基本 CRUD 操作

```java
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public User create(String username, String email) {
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        return userRepository.save(user);
    }

    public Optional<User> getById(Long id) {
        return userRepository.findById(id);
    }

    public void delete(Long id) {
        userRepository.deleteById(id);
    }
}
```

### 分页与排序查询

```java
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;

    @GetMapping
    public Page<User> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String keyword) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());

        if (keyword != null && !keyword.isBlank()) {
            return userRepository.findByEmailContaining(keyword, pageable);
        }
        return userRepository.findAll(pageable);
    }
}
```

### Specification 动态查询

适用于多条件组合筛选场景，避免为每种组合定义单独的 Repository 方法：

```java
public class UserSpecifications {

    public static Specification<User> hasUsername(String username) {
        return (root, query, cb) ->
                username == null ? null : cb.equal(root.get("username"), username);
    }

    public static Specification<User> emailContains(String email) {
        return (root, query, cb) ->
                email == null ? null : cb.like(root.get("email"), "%" + email + "%");
    }
}

// 在 Service 中组合使用
@Service
@RequiredArgsConstructor
public class UserSearchService {

    private final UserRepository userRepository;

    public Page<User> search(String username, String email, Pageable pageable) {
        Specification<User> spec = Specification
                .where(UserSpecifications.hasUsername(username))
                .and(UserSpecifications.emailContains(email));
        return userRepository.findAll(spec, pageable);
    }
}
```

### Example 查询

基于实体对象字段值自动匹配，适合简单等值条件查询：

```java
User probe = new User();
probe.setUsername("zhangsan");

ExampleMatcher matcher = ExampleMatcher.matching()
        .withIgnoreCase()
        .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING);

List<User> results = userRepository.findAll(Example.of(probe, matcher));
```
