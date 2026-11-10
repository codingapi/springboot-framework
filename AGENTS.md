# AGENTS.md

This file provides guidance to AI coding agents when working with code in this repository.

## 项目概述

springboot-framework 是一个基于 Spring Boot 3.3.5（Java 17）的多模块企业级开发框架，由 CodingApi 团队维护。核心定位是为领域驱动设计（DDD）与事件风暴提供开箱即用的基础设施。当前开发版本为 `17.3.0-SNAPSHOT`，采用 Maven `${revision}` CI-Friendly 版本机制，正式发布时通过 `-Drevision=x.y.z` 指定正式版本号。版本号含义：17 = JDK 版本，3 = Spring Boot 大版本，第三位为补丁版本。

**回复语言**：请使用中文进行回答。

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

# 运行 example 示例应用（端口 8090，H2 文件数据库）
./mvnw spring-boot:run -pl example/example-server

# 正式发布（通过 -Drevision 指定正式版本号，无需修改 pom）
./mvnw clean deploy -P ossrh -Drevision=17.3.0
```

### 前端（frontend monorepo）

```bash
cd frontend
pnpm install
pnpm dev:pc        # PC 端开发模式（代理后端）
pnpm dev:mobile    # 移动端开发模式（代理后端）
pnpm mock:pc       # PC 端 Mock 模式（前端 mock 数据）
pnpm mock:mobile   # 移动端 Mock 模式（前端 mock 数据）
pnpm build:pc      # PC 端生产构建
pnpm build:mobile  # 移动端生产构建
pnpm test:pc       # PC 端 Jest 测试
pnpm test:mobile   # 移动端 Jest 测试
```

- apps/pc（原 admin-ui）基于 React 18 + Ant Design 5 + Ant Design Pro Components + Rsbuild
- apps/mobile（原 mobile-ui）基于 React 18 + Ant Design Mobile 5 + Rsbuild
- 两者由 pnpm workspace 统一管理，公共代码位于 packages/shared（@springboot-framework/shared）；均使用 Module Federation 做微前端集成

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

各 starter 模块同时维护 `META-INF/spring.factories`（兼容旧版）和 `META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports`（Spring Boot 3.x 标准）。新增 starter 时需同时注册这两种文件。

### 模块间依赖关系

```
springboot-starter (核心，无外部依赖)
    ├── springboot-starter-security      (依赖 starter)
    ├── springboot-starter-data-fast     (依赖 starter)
    ├── springboot-starter-data-authorization (依赖 starter + JSqlParser)
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
request.addFilter("age", Relation.GREATER_THAN, 18);
Page<User> page = userRepository.findAll(request);
```

`FastRepository.findAll(PageRequest)` 自动根据 Filter 构建 Example 或 HQL 查询。

### 数据权限（SQL 拦截）

`ConnectionProxy` → `PreparedStatementProxy` / `StatementProxy` 代理链，在 SQL 执行前通过 `SQLRunningContext.intercept(sql)` 注入权限条件，实现透明的行级数据过滤。使用 JSqlParser 解析和改写 SQL。

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
    example-domain-user   — 用户领域（Entity、Repository、Service、Event、Gateway）
  example-infra           — 基础设施层
    example-infra-jpa     — JPA 持久化实现
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

见根 `pom.xml` 的 `<properties>` 区块。主要版本：Groovy 4.0.24、JSqlParser 5.0、Fastjson 2.0.53、JJWT 0.12.6、H2 2.3.232。

## 开发规范

### GitHub Issue 标准处理流程

本仓库同时维护 **8.x（JDK 8 / Spring Boot 2.7.x / `javax.*`）与 17.x（JDK 17 / Spring Boot 3.x / `jakarta.*`）两条版本线**，各自独立交付，没有 `dev` 分支。

处理任何来自 GitHub 的 issue 时，必须遵循 [docs/conventions/workflow/github-issue.md](./docs/conventions/workflow/github-issue.md)：

1. **定位阶段**可只读代码分析，不必创建 worktree；
2. **先确认影响版本线**（8.x / 17.x / 两者），定位完成后先回复 issue，说明根因、影响版本与处理方案；
3. **实际改代码必须使用 git worktree + 特性分支**，基线按版本线选择：17.x 用 `17.3.x`，8.x 用 `8.2.x`；不得在主工作区直接修改；
4. 修复需测试先行，并使用对应版本线的 JDK 完成本地编译/测试验证；双版本 issue 必须两条线分别验证；
5. 完成后**只推送该 issue 的特性分支到 GitHub**：提交内容、推送与 issue 关联均以该特性分支为准，**不合并版本分支**；是否合并由人工决定，任务完成时仅提示分支与 PR 链接（PR 目标为 `17.3.x` 或 `8.2.x`）；
6. 提交信息使用 `Closes #<issue编号>` 关联 issue；仅当进入默认分支 `main` 时自动关闭，未自动关闭时需用户确认后手动关闭（8.x 合并到 `8.2.x` 不会自动关闭）；
7. **特性分支推送到 GitHub 后再回复 issue**，说明修复内容、验证结果与推送分支/提交；未推送前不得发布完成回复（定位回复不受此限）。

### 基本规范

- **与用户沟通及编写文档时，所有内容必须使用中文表述**
- **在每次修改代码以后，要执行本地化的编译验证确保代码没有错误**
- Java 代码遵循 Spring Boot 规范
- 17.x 使用 `jakarta.*` 与 JDK 17，8.x 使用 `javax.*` 与 JDK 8，两条线的修复不得直接 cherry-pick，必须做命名空间与代际适配
- 编码规范见 `docs/conventions`：方法命名 [java/naming-convention](./docs/conventions/java/naming-convention.md)、测试设计 [java/test-design-convention](./docs/conventions/java/test-design-convention.md)、领域建模 [java/domain-modeling-convention](./docs/conventions/java/domain-modeling-convention.md)、静态方法 [java/static-method-convention](./docs/conventions/java/static-method-convention.md)

### 测试规范

本项目后端采用 JUnit 5 进行单元测试。两条版本线测试命令一致，但需切换到对应 JDK（17.x 用 JDK 17，8.x 用 JDK 8）。

**测试文件位置：**

- 测试文件放在各模块 `src/test/java/` 目录下，包路径与源码保持一致
- 测试文件命名：`*Test.java`

**测试编写流程：**

1. **Red（红灯）**：先编写测试用例，明确预期行为
2. **Green（绿灯）**：编写最小代码使测试通过
3. **Refactor（重构）**：优化代码结构，运行全部测试确认不回归

**测试用例设计要求：**

- 每个测试用例需明确：测试目标、前置条件、执行步骤、期望断言
- 优先测试核心业务逻辑（DTO、事件系统、动态查询、SQL 拦截、脚本引擎等）
- 测试应该有确定性结果，避免依赖外部状态

**测试命令：**

```bash
# 运行所有测试（与 CI 一致）
./mvnw clean test -P travis

# 运行特定模块的测试
./mvnw test -pl springboot-starter

# 运行特定的测试类
./mvnw test -pl springboot-starter -Dtest=ResponseTest

# 前端测试（在 frontend/ 目录下执行）
pnpm test:pc
pnpm test:mobile
```

**示例（基于 springboot-starter 的响应封装测试，17.x 版本线）：**

```java
package com.codingapi.springboot.framework.dto;

import com.codingapi.springboot.framework.dto.response.SingleResponse;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SingleResponseTest {

    @Test
    void should_wrap_single_data() {
        // given
        String data = "hello";

        // when
        SingleResponse<String> response = SingleResponse.of(data);

        // then
        assertTrue(response.isSuccess());
        assertEquals(data, response.getData());
    }
}
```

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
