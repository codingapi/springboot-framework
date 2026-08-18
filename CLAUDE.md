# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## 项目概述

springboot-framework 是一个基于 Spring Boot 2.7.18（Java 8）的多模块企业级开发框架，由 CodingApi 团队维护。核心定位是为领域驱动设计（DDD）与事件风暴提供开箱即用的基础设施。当前开发版本为 `8.2.0-SNAPSHOT`，采用 Maven `${revision}` CI-Friendly 版本机制，正式发布时通过 `-Drevision=x.y.z` 指定正式版本号。版本号含义：8 = 最低 JDK 版本，2 = Spring Boot 大版本，第三位为补丁版本。

**回复语言**：请使用中文进行回答。

**版本线说明**：本仓库维护两条版本线——`17.3.x`（JDK 17 / Spring Boot 3.x）与 `8.2.x`（JDK 8 / Spring Boot 2.x，当前分支）。两条版本线功能基本一致，主要差异为 JDK 与 Spring Boot 代际：本分支使用 `javax.*` 命名空间（如 `javax.servlet`、`javax.persistence`），**禁止使用** `jakarta.*`。本分支不包含示例工程（example）与前端工程（frontend）。

## 常用命令

```bash
# 全量构建
./mvnw clean install

# 仅运行测试（CI 使用）
./mvnw clean test -P travis

# 运行单个模块的测试
./mvnw test -pl springboot-starter
./mvnw test -pl springboot-starter-security

# 运行单个测试类
./mvnw test -pl springboot-starter -Dtest=SomeTestClass

# 正式发布（通过 -Drevision 指定正式版本号，无需修改 pom）
./mvnw clean deploy -P ossrh -Drevision=8.2.0
```

## 模块架构

### Starter 模块（框架核心）

| 模块 | 职责 |
|------|------|
| `springboot-starter` | 核心基础：统一 Response DTO、分页 PageRequest/Filter、事件系统、国际化异常、事务管理 |
| `springboot-starter-security` | Spring Security + JWT 无状态认证 / Redis 有状态认证，加解密支持 |
| `springboot-starter-data-fast` | JPA 增强：FastRepository 支持动态过滤查询、HQL 构建、SearchRequest |
| `springboot-starter-data-authorization` | 数据权限：通过 JDBC Connection/Statement 代理拦截 SQL，透明注入权限条件 |
| `springboot-starter-script` | Groovy 脚本引擎：运行时编译、LRU 缓存、热更新，提供 REST API |

### 自动配置注册

各 starter 模块同时维护 `META-INF/spring.factories`（兼容旧版）和 `META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports`（Spring Boot 2.7+ 支持）。新增 starter 时需同时注册这两种文件。

### 模块间依赖关系

```
springboot-starter (核心，无外部依赖)
    ├── springboot-starter-security      (依赖 starter)
    ├── springboot-starter-data-fast     (依赖 starter)
    ├── springboot-starter-data-authorization (依赖 starter + JSqlParser)
    └── springboot-starter-script        (依赖 starter + Groovy)
```

注意：根 `pom.xml` 默认 `<modules>` 中仅包含 `springboot-starter-data-authorization`，完整 5 模块通过 `dev` / `travis` / `ossrh` profile 激活。

## 核心架构模式

### 事件系统（Event System）

框架自建的发布-订阅事件机制，是 DDD 领域事件的核心基础设施：

- **`IEvent`** — 事件标记接口，分为 `ISyncEvent`（同步）和 `IAsyncEvent`（异步）
- **`IHandler<T extends IEvent>`** — 事件处理器，支持 `order()` 排序和 `error()` 异常回调
- **`EventPusher.push(event, sync)`** — 事件推送入口
- **`DomainEvent`** — 领域实体事件基类，派生 `DomainCreateEvent`、`DomainChangeEvent`、`DomainDeleteEvent`
- **`DomainChangeInterceptor`** — 通过代理拦截实体字段变更，自动推送 `DomainChangeEvent`

标注 `@Handler`（或 `@Component`/`@Service`）的处理器会被注册为 Spring Bean；`SpringHandlerConfiguration` 收集容器中所有 `IHandler` Bean，在构造事件处理器时通过 `addHandlers` 统一注册到 `ApplicationHandlerUtils`。

### 统一响应封装

```java
Response          // 基础响应（success / errCode / errMessage）
SingleResponse<T> // 单对象响应
MultiResponse<T>  // 列表响应
MapResponse       // Map 响应
```

### 数据查询（PageRequest + FastRepository）

`PageRequest` 扩展了 Spring Data 的 `PageRequest`，增加了 `RequestFilter` 动态过滤条件：

```java
PageRequest request = PageRequest.of(0, 20);
request.addFilter("name", "张三");
request.addFilter("age", Relation.GREATER_THAN, 18);
Page<User> page = userRepository.findAll(request);
```

`FastRepository.findAll(PageRequest)` 全部为等值条件时构建 Example 查询；包含 LIKE/范围/IN/OR 等复杂条件时自动切换 `DynamicSQLBuilder` 构建的 HQL 查询。

### 数据权限（SQL 拦截）

`ConnectionProxy` → `PreparedStatementProxy` / `StatementProxy` 代理链，在 SQL 执行前通过 `SQLRunningContext.intercept(sql)` 注入权限条件，实现透明的行级数据过滤。使用 JSqlParser 解析和改写 SQL。列级脱敏通过 `ResultSetProxy` + `ColumnHandlerContext` 在结果集读取时完成。

## 框架配置项

在 `application.properties` 中可用的配置前缀：

```properties
# 安全模块
codingapi.security.jwt.enable=true               # 启用 JWT 认证（必须显式开启）
codingapi.security.redis.enable=true             # 启用 Redis 有状态认证（必须显式开启）
codingapi.security.ignore-urls=/open/**,/#/**    # 免认证 URL 列表

# 框架核心
codingapi.framework.handler-thread-pool-size=20  # 事件异步线程池大小

# 数据权限
codingapi.data-authorization.show-sql=false      # 打印拦截后的 SQL
```

## 关键依赖版本

见根 `pom.xml` 的 `<properties>` 区块。主要版本：Groovy 4.0.24、JSqlParser 5.0、Fastjson 2.0.53、JJWT 0.12.6、H2 1.4.200。

<!-- PKR-START -->
## PKR 知识查阅（编码前必须）

进入计划模式或实现功能前，**必须按以下优先级查阅**：

### ⚠️ 开发规范（最高优先级，必须严格遵守）

1. [docs/conventions/index.md](./docs/conventions/index.md) — 项目开发规范

**规范具有最高优先级。** 所有代码必须遵循已注册的 Convention，违反规范的代码视为缺陷。
编码前必须逐条检查相关规范，确保命名、结构、模式完全符合要求。

### 已有能力（必须复用，禁止重复实现）

2. [docs/capabilities/index.md](./docs/capabilities/index.md) — 已有可复用能力

已有能力必须复用，禁止重新实现。优先组合已有能力解决问题。

### 计划模式约束

计划方案中必须包含：
1. **遵循了哪些规范** — 列出遵守的 Convention（必须首先说明）
2. **复用了哪些已有能力** — 列出从 PKR 中找到并复用的 Capability
3. **是否有新增能力** — 如果本次开发产生了可复用的新能力，完成后通过 `/pkr-add` 注册

### 知识管理命令

| 命令 | 用途 |
|------|------|
| `/pkr-init` | 扫描项目，发现候选能力和规范（自动跳过已有文档） |
| `/pkr-sync` | 全量同步，对比代码变更 |
| `/pkr-update <module>/<name> [desc]` | 单项更新，可带描述指导更新 |
| `/pkr-add <module>/<name> <desc>` | 从代码/框架扫描注册 |
| `/pkr-add plan <module>/<name> <desc>` | 注册计划中的能力 |
| `/pkr-export <module> ...` | 导出模块文档供其他项目使用 |
<!-- PKR-END -->
