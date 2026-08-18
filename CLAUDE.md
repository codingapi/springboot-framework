# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## 项目概览

`com.codingapi.springboot:springboot-parent`（当前开发版本 `8.2.0-SNAPSHOT`）是一套基于 Spring Boot 2.7.18 + JDK 8 的**领域驱动设计（DDD）落地框架**，围绕"事件风暴 + 数据权限 + 动态脚本"三大支柱提供可插拔的 starter 模块。框架代码本身即是规范，业务方按需引入 starter 即可获得对应能力，无需重复造轮子。

版本采用 CI-Friendly 机制：pom 版本声明为 `${revision}`，开发期锁定 `8.2.0-SNAPSHOT`（版本号含义：8 = JDK 8，2 = Spring Boot 2.x 大版本，第三位为补丁版本），正式发布时通过 `-Drevision=x.y.z` 指定正式版本号，无需修改 pom。

详细能力文档见 `docs/capabilities/index.md`（共 8 篇），开发规范见 `docs/conventions/index.md`。

## 模块拓扑与依赖

多模块项目，根 `pom.xml` 中只激活 `springboot-starter-data-authorization`；完整 5 模块通过 `dev` / `travis` / `ossrh` profile 激活。模块依赖**严格单向**，不得反向依赖：

```
springboot-starter          (基础 DDD 框架：领域事件、代理、异常、加密、HTTP 代理)
        │
        ├─→ springboot-starter-script          (Groovy 脚本引擎，依赖 starter)
        │       │
        │       └─→ springboot-starter-data-fast   (JPA 增强 + 脚本映射，依赖 starter+script)
        │
        └─→ springboot-starter-security        (JWT/Redis 双模 Token 网关，依赖 starter)
        │
        └─→ springboot-starter-data-authorization (行+列数据权限，**仅在根 pom 直接声明**)
```

⚠️ `springboot-starter-data-authorization` 不依赖 starter，自带 jsqlparser 解析 SQL，改写后通过 `AuthorizationJdbcDriver` 在结果集层做列权限脱敏。

## 关键架构决策

| 决策 | 体现位置 | 说明 |
|------|---------|------|
| **领域事件总线** | `framework/event/` | 自研 `EventPusher` + `DomainEvent`，支持同步/异步/事务后（`@TransactionalEventListener AFTER_COMMIT`），通过 `EventTraceContext` 检测循环事件 |
| **CGLIB 实体代理** | `framework/domain/proxy/` | `DomainProxyFactory.create()` 在 setter 调用前后对比字段差异，自动发布 `DomainChangeEvent`，无需在业务 setter 中写事件代码 |
| **脚本热加载** | `script/GroovyScript*` | 编译缓存用 SHA-256 做 key；`TempGroovyScript` 临时脚本由 `GroovyScriptEngineRunner` 在启动时从 DB 恢复、应用关闭时落盘 |
| **双模 Token** | `security/gateway/TokenGateway` | 同一接口有 `JwtTokenGateway`（无状态）和 `RedisTokenGateway`（可踢人）两种实现，通过 `codingapi.security.token.type` 切换 |
| **行+列双层权限** | `authorization/` | `DefaultSQLInterceptor` 在 SQL 执行前用 `JSqlParser` 递归改写 WHERE；`ResultSetProxy` 在结果集读取时通过 `ColumnHandlerContext` 脱敏 |
| **泛型签名一致性** | `framework/utils/RandomGenerator` 等 | JDK 8 编译时必须显式声明 `LinkedHashMap<String,Script>` 等类型，依赖 IDE 推断会丢失泛型信息（见 git log 中 `GroovyScriptRuntime` 修复记录） |
| **javax.persistence 适配** | 所有 `*Repository` 接口 | 项目基于 Spring Boot 2.7.18，对应 JPA 2.x；**禁止使用** `jakarta.persistence.*`（那是 Spring Boot 3.x / Jakarta EE 9+） |

## 常用命令

```bash
# 构建
./mvnw clean install -DskipTests          # 跳过测试
./mvnw clean test                         # 全量测试（默认 dev profile）

# 单模块测试
./mvnw -pl springboot-starter -am test
./mvnw -pl springboot-starter-script -Dtest=GroovyScriptRuntimeContextTest test

# 部署 profile
./mvnw clean test -P travis               # 启用 jacoco + clover

# 正式发布（通过 -Drevision 指定正式版本号，无需修改 pom）
./mvnw clean deploy -P ossrh -Drevision=8.2.0   # 推送到 Sonatype（需 GPG）
```

测试基于 H2 内存库（`com.h2database:h2` scope=test），无需外部 DB（H2 关闭时的告警是正常的）。

## 文档与知识库

- `README.md` — 模块列表、maven 依赖、版本说明
- `docs/capabilities/` — 8 篇核心能力文档（DDD 事件、领域代理、Groovy 脚本、JDBC 代理、SQL 拦截器、JPA 仓储、Token 网关、Spring Boot 基础）
- `docs/conventions/` — 项目开发规范（待补全）
- Wiki：https://github.com/codingapi/springboot-framework/wiki

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
