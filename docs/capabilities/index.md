# 能力（Capabilities）

> 🔄 此文件由 `rebuild_pkr_index.py` 自动生成，请勿手动编辑。

## ✅ 已实现

| 名称 | 描述 | 范围 | 来源 |
|------|------|------|------|
| [domain-change-interceptor](./domain-change-interceptor.md) | 通过 CGLIB 代理拦截领域实体字段的 setter 方法，自动检测变更并发布 DomainChangeEvent | 后端 | 项目自有 |
| [event-system](./event-system.md) | 自建的领域事件发布-订阅系统，支持同步/异步事件、事务后提交、Handler自动注册排序 | 后端 | 项目自有 |
| [fast-repository](./fast-repository.md) | JPA Repository 增强，支持动态过滤查询（RequestFilter + Relation 操作符）、HQL 自动构建、排序 | 后端 | 项目自有 |
| [groovy-script-engine](./groovy-script-engine.md) | Groovy 动态脚本引擎，支持运行时编译执行、LRU 缓存、热更新和 REST API | 后端 | 项目自有 |
| [jackson](./jackson.md) | Jackson JSON 序列化/反序列化能力，由 Spring Boot Web 自动配置，框架中用于 REST API 响应序列化、请求参数绑定等场景 | 后端 | 框架:jackson |
| [jdbc-proxy-chain](./jdbc-proxy-chain.md) | 完整的 JDBC 装饰器代理链，透明拦截 SQL 执行以实现数据权限控制 | 后端 | 项目自有 |
| [locale-exception](./locale-exception.md) | 支持 i18n 的业务异常体系，errCode + errMessage，通过 MessageSource 实现国际化 | 后端 | 项目自有 |
| [spring-data-jpa](./spring-data-jpa.md) | Spring Data JPA 数据访问能力，提供 ORM 映射、Repository 抽象、分页查询等基础设施，框架在此基础上扩展了 FastRepos... | 后端 | 框架:spring-data-jpa |
| [spring-data-redis](./spring-data-redis.md) | Spring Data Redis 数据存储能力，框架中主要用于 Token/Session 的有状态存储，支持服务端主动失效和多端登录管理 | 后端 | 框架:spring-data-redis |
| [spring-framework](./spring-framework.md) | Spring Framework / Spring Boot 核心基础能力，提供 IoC 容器、事件发布、AOP、Web MVC、事务管理等基础设施 | 后端 | 框架:spring-framework |
| [spring-security](./spring-security.md) | Spring Security 安全认证与授权能力，框架在此基础上封装了 JWT 无状态认证和 Redis 有状态认证两种模式 | 后端 | 框架:spring-security |
| [sql-interceptor](./sql-interceptor.md) | 基于 JSqlParser 的 SQL 解析改写拦截器，在 SQL 执行前透明注入数据权限条件 | 后端 | 项目自有 |
| [token-gateway](./token-gateway.md) | JWT/Redis 双模式 Token 认证网关，统一创建、解析和管理认证令牌 | 后端 | 项目自有 |
| [transaction-manager-context](./transaction-manager-context.md) | 单例编程式事务上下文，支持 REQUIRES_NEW 语义，在非 Spring 管理上下文中也能使用事务 | 后端 | 项目自有 |
| [trigger-system](./trigger-system.md) | 触发器框架，支持定时/条件触发，提供统一的触发上下文管理 | 后端 | 项目自有 |
| [workflow-engine](./workflow-engine.md) | 工作流引擎，支持流程定义、节点流转、审批、委托、会签、数据快照和事件通知 | 后端 | 项目自有 |

## 🗓️ 计划中

| 名称 | 描述 | 范围 | 来源 |
|------|------|------|------|
| [flow-postpone-event](./flow-postpone-event.md) | 延期流程操作的事件通知 — 当前 FlowPostponedService 未发送 FlowApprovalEvent | 后端 | 计划 |

---

**统计**: 共 17 篇 — 已实现 16 / 计划中 1 / 已废弃 0
