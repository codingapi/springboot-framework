---
name: springboot-starter/page-request
module: springboot-starter
description: 动态分页查询请求，扩展 Spring PageRequest 支持 RequestFilter 动态过滤条件
status: 已实现
scope: 后端
source: 框架:springboot-starter
import: "com.codingapi.springboot:springboot-starter"
framework_version: "3.4.54"
---

## 解决什么问题

在企业级后台管理系统中，列表查询是最常见的业务场景之一。Spring Data 原生的 `PageRequest` 仅支持分页参数（页码、大小、排序），无法携带动态过滤条件。开发者通常需要为每个查询接口手动编写 Specification、QueryDSL 或自定义 HQL，导致大量重复的查询构建代码。

`PageRequest` 能力解决了以下痛点：

- **动态过滤**：在分页请求对象上直接附加任意字段、任意比较关系的过滤条件，无需为每种查询组合编写独立方法。
- **前端驱动查询**：通过 `SearchRequest` 自动解析 HTTP 请求参数（包括 Base64 编码的 `filter`、`sort`、`params` JSON），将前端传入的筛选/排序规则转换为类型安全的过滤条件，减少 Controller 层样板代码。
- **复杂条件组合**：支持 AND/OR 嵌套组合过滤，满足多条件联合查询需求。
- **与 FastRepository 无缝集成**：`FastRepository.findAll(PageRequest)` 和 `pageRequest(PageRequest)` 自动根据过滤条件构建 Example 或 HQL 查询，开发者只需关注业务逻辑。

## 如何使用

### 核心类说明

| 类 | 职责 |
|---|------|
| `PageRequest` | 继承 Spring `PageRequest`，增加 `RequestFilter` 和链式 `addFilter` API |
| `RequestFilter` | 过滤条件容器，管理 `Filter` 列表并提供按 key 读取过滤值的快捷方法 |
| `Filter` | 单个过滤条件，包含字段名（key）、比较关系（Relation）和值 |
| `Relation` | 枚举，定义了 14 种比较关系：EQUAL、NOT_EQUAL、LIKE、LEFT_LIKE、RIGHT_LIKE、BETWEEN、IN、NOT_IN、IS_NULL、IS_NOT_NULL、GREATER_THAN、LESS_THAN、GREATER_THAN_EQUAL、LESS_THAN_EQUAL |
| `SearchRequest` | 从当前 `HttpServletRequest` 自动解析分页、排序、过滤参数并生成 `PageRequest` |

### 创建 PageRequest

```java
// 基本分页（第 0 页，每页 20 条）
PageRequest request = PageRequest.of(0, 20);

// 带排序
PageRequest request = PageRequest.of(0, 20, Sort.by("createTime").descending());
```

### 添加过滤条件

```java
// 等值过滤（默认 EQUAL）
request.addFilter("name", "张三");

// 指定比较关系
request.addFilter("age", Relation.GREATER_THAN, 18);
request.addFilter("createTime", Relation.BETWEEN, startDate, endDate);
request.addFilter("status", Relation.IN, "ACTIVE", "PENDING");

// AND / OR 组合
request.andFilter(
    Filter.as("deptId", 1),
    Filter.as("roleId", 2)
);

request.orFilters(
    Filter.as("name", Relation.LIKE, "张"),
    Filter.as("email", Relation.LIKE, "zhang")
);
```

### 读取过滤值

```java
String name = request.getStringFilter("name");
int age = request.getIntFilter("age", 0);
boolean hasConditions = request.hasFilter();
```

### 配合 FastRepository 使用

```java
// findAll — 简单过滤走 Example 查询
Page<User> page = userRepository.findAll(request);

// pageRequest — 复杂过滤走 HQL 动态查询
Page<User> page = userRepository.pageRequest(request);

// searchRequest — 从 HTTP 请求自动解析
Page<User> page = userRepository.searchRequest(searchRequest);
```

### SearchRequest 自动解析

`SearchRequest` 从当前 HTTP 请求中提取以下参数：

| 参数 | 格式 | 说明 |
|------|------|------|
| `current` | int | 页码（从 0 开始） |
| `pageSize` | int | 每页大小 |
| `sort` | Base64(JSON) | 排序规则，如 `{"createTime":"descend"}` |
| `filter` | Base64(JSON) | 过滤条件，如 `{"status":["ACTIVE"]}` |
| `params` | Base64(JSON Array) | 指定字段的比较关系，如 `[{"key":"age","type":"GREATER_THAN"}]` |
| 其他参数 | string | 作为 EQUAL 过滤条件自动添加 |

## 使用实例

### 示例 1：Service 层手动构建动态查询

```java
@Service
public class UserQueryService {

    @Autowired
    private UserRepository userRepository;

    public Page<User> searchUsers(String keyword, Integer minAge, String status) {
        PageRequest request = PageRequest.of(0, 20);
        request.addSort(Sort.by("createTime").descending());

        if (keyword != null) {
            request.addFilter("name", Relation.LIKE, keyword);
        }
        if (minAge != null) {
            request.addFilter("age", Relation.GREATER_THAN_EQUAL, minAge);
        }
        if (status != null) {
            request.addFilter("status", status);
        }

        return userRepository.findAll(request);
    }
}
```

### 示例 2：Controller 层使用 SearchRequest 自动绑定

```java
@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping
    public MultiResponse<User> list(SearchRequest searchRequest) {
        // 自动从 HTTP 参数解析 current、pageSize、sort、filter
        Page<User> page = userRepository.searchRequest(searchRequest);
        return MultiResponse.of(page.getContent(), (int) page.getTotalElements());
    }
}
```

前端请求示例：
```
GET /api/users?current=0&pageSize=20
    &sort=eyJjcmVhdGVUaW1lIjoiZGVzY2VuZCJ9
    &filter=eyJzdGF0dXMiOlsiQUNUSVZFIiwiUEVORElORyJdfQ==
    &params=W3sia2V5IjoiYWdlIiwidHlwZSI6IkdSRUFURVJfVEhBTiJ9XQ==
    &deptId=1
```

其中 `filter` 解码后为 `{"status":["ACTIVE","PENDING"]}`，`params` 解码后为 `[{"key":"age","type":"GREATER_THAN"}]`，`deptId` 作为额外 EQUAL 条件自动加入。

### 示例 3：AND/OR 组合过滤

```java
PageRequest request = PageRequest.of(0, 20);

// (deptId=1 AND roleId=2) OR (name LIKE '%admin%')
request.orFilters(
    Filter.and(
        Filter.as("deptId", 1),
        Filter.as("roleId", 2)
    ),
    Filter.as("name", Relation.LIKE, "admin")
);

Page<User> page = userRepository.pageRequest(request);
```
