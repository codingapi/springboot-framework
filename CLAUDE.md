# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## 项目概述

springboot-framework 是一个基于 Spring Boot 3.3.5（Java 17）的多模块企业级开发框架，由 CodingApi 团队维护。核心定位是为领域驱动设计（DDD）与事件风暴提供开箱即用的基础设施，当前版本为 `3.4.54`。

**回复语言**：请使用中文进行回答。

## 常用命令

```bash
# 全量构建
./mvnw clean install

# 仅运行测试（CI 使用）
./mvnw clean test -P travis

# 运行单个模块的测试
./mvnw test -pl springboot-starter
./mvnw test -pl springboot-starter-flow
./mvnw test -pl springboot-starter-security

# 运行单个测试类
./mvnw test -pl springboot-starter -Dtest=SomeTestClass

# 运行 example 示例应用（端口 8090，H2 文件数据库）
./mvnw spring-boot:run -pl example/example-server
```

### 前端（admin-ui / mobile-ui）

```bash
cd admin-ui     # 或 mobile-ui
npm install
npm run dev     # 开发模式（代理后端）
npm run mock    # Mock 模式（前端 mock 数据）
npm run build   # 生产构建
npm test        # Jest 测试
```

- admin-ui 基于 React 18 + Ant Design 5 + Ant Design Pro Components + Rsbuild
- mobile-ui 基于 React 18 + Ant Design Mobile 5 + Rsbuild
- 两者都使用 Module Federation 做微前端集成

## 模块架构

### Starter 模块（框架核心）

| 模块 | 职责 |
|------|------|
| `springboot-starter` | 核心基础：统一 Response DTO、分页 PageRequest/Filter、事件系统、国际化异常、事务管理 |
| `springboot-starter-security` | Spring Security + JWT 无状态认证 / Redis 有状态认证，加解密支持 |
| `springboot-starter-data-fast` | JPA 增强：FastRepository 支持动态过滤查询、HQL 构建、SearchRequest |
| `springboot-starter-data-authorization` | 数据权限：通过 JDBC Connection/Statement 代理拦截 SQL，透明注入权限条件 |
| `springboot-starter-flow` | 工作流引擎：流程定义、节点流转、审批、委托、会签、数据快照、事件通知 |
| `springboot-starter-script` | Groovy 脚本引擎：运行时编译、LRU 缓存、热更新，提供 REST API |

### 自动配置注册

各 starter 模块同时维护 `META-INF/spring.factories`（兼容旧版）和 `META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports`（Spring Boot 3.x 标准）。新增 starter 时需同时注册这两种文件。

### 模块间依赖关系

```
springboot-starter (核心，无外部依赖)
    ├── springboot-starter-security      (依赖 starter)
    ├── springboot-starter-data-fast     (依赖 starter)
    ├── springboot-starter-data-authorization (依赖 starter + JSqlParser)
    ├── springboot-starter-flow          (依赖 starter)
    └── springboot-starter-script        (依赖 starter + Groovy)
```

## 核心架构模式

### 事件系统（Event System）

框架自建的发布-订阅事件机制，是 DDD 领域事件的核心基础设施：

- **`IEvent`** — 事件标记接口，分为 `ISyncEvent`（同步）和 `IAsyncEvent`（异步）
- **`IHandler<T extends IEvent>`** — 事件处理器，支持 `order()` 排序和 `error()` 异常回调
- **`EventPusher.push(event, sync)`** — 事件推送入口
- **`DomainEvent`** — 领域实体事件基类，派生 `DomainCreateEvent`、`DomainChangeEvent`、`DomainDeleteEvent`
- **`DomainChangeInterceptor`** — 通过代理拦截实体字段变更，自动推送 `DomainChangeEvent`

Handler 通过 Spring Bean 自动注册，由 `HandlerBeanDefinitionRegistrar` 扫描所有 `IHandler` 实现并注册到 `ApplicationHandlerUtils`。

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
request.addFilter("age", Relation.GT, 18);
Page<User> page = userRepository.findAll(request);
```

`FastRepository.findAll(PageRequest)` 自动根据 Filter 构建 Example 或 HQL 查询。

### 数据权限（SQL 拦截）

`ConnectionProxy` → `PreparedStatementProxy` / `StatementProxy` 代理链，在 SQL 执行前通过 `SQLRunningContext.intercept(sql)` 注入权限条件，实现透明的行级数据过滤。使用 JSqlParser 解析和改写 SQL。

### 工作流引擎

核心流程：`FlowWork`（流程定义）→ `FlowNode`（节点）→ `FlowRecord`（审批记录）→ `FlowBackup`（版本快照）。发起流程通过 `FlowStartService.startFlow()` 驱动，审批通过 `FlowNodeService` 逐节点流转，每个状态变更推送 `FlowApprovalEvent`。

## DDD 示例项目结构（example 模块）

```
example/
  example-server          — Spring Boot 启动入口
  example-interface       — 接口层：Controller（API）、Handler（事件处理）、Runner
  example-app             — 应用层
    example-app-query     — 查询服务（CQRS Query 侧）
    example-app-cmd-domain — 命令服务（CQRS Command 侧，领域编排）
    example-app-cmd-meta   — 元数据命令服务
  example-domain          — 领域层
    example-domain-leave  — 请假领域（Entity、Repository、Service）
    example-domain-user   — 用户领域（Entity、Repository、Service、Event、Gateway）
  example-infra           — 基础设施层
    example-infra-jpa     — JPA 持久化实现
    example-infra-flow    — 工作流集成
    example-infra-security — 安全配置
```

遵循 DDD 分层依赖规则：`interface → app → domain ← infra`。domain 层定义 Repository 接口，infra 层提供实现。

## 框架配置项

在 `application.properties` 中可用的配置前缀：

```properties
# 安全模块
codingapi.security.jwt.enable=true               # 启用 JWT 认证
codingapi.security.redis.enable=true             # 启用 Redis 有状态认证
codingapi.security.ignore-urls=/open/**,/#/**    # 免认证 URL 列表

# 框架核心
codingapi.framework.handler-thread-pool-size=20  # 事件异步线程池大小
```

## 关键依赖版本

见根 `pom.xml` 的 `<properties>` 区块。主要版本：Groovy 4.0.24、JSqlParser 5.0、Fastjson 2.0.53、JJWT 0.12.6、H2 2.3.232、Kryo 5.6.2。


<!-- PKR-START -->
## PKR 知识查阅（编码前必须）

进入计划模式或实现功能前，必须查阅：
1. [docs/capabilities/index.md](./docs/capabilities/index.md) — 已有可复用能力
2. [docs/conventions/index.md](./docs/conventions/index.md) — 开发规范

已有能力必须复用，禁止重新实现。编码必须遵循已注册的规范。

### 计划模式约束

计划方案中必须包含：
1. **复用了哪些已有能力** — 列出从 PKR 中找到并复用的 Capability
2. **遵循了哪些规范** — 列出遵守的 Convention
3. **是否有新增能力** — 如果本次开发产生了可复用的新能力，完成后通过 `/pkr-add` 注册

### 知识管理命令

| 命令 | 用途 |
|------|------|
| `/pkr-init` | 首次扫描项目，发现候选能力和规范 |
| `/pkr-sync` | 全量同步，对比代码变更 |
| `/pkr-update <name> [desc]` | 单项更新，可带描述指导更新 |
| `/pkr-add [name] <desc>` | 从代码/框架扫描注册（名称可省略） |
| `/pkr-add plan [name] <desc>` | 注册计划中的能力（名称可省略） |
<!-- PKR-END -->
