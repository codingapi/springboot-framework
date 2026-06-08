# 能力（Capabilities）

> 🔄 此文件由 `rebuild_pkr_index.py` 自动生成，请勿手动编辑。

## ✅ 已实现

| 名称 | 描述 | 范围 | 来源 |
|------|------|------|------|
| [data-authorization](./data-authorization.md) | 数据权限 SQL 拦截器，通过 JDBC 代理链在 SQL 执行前透明注入权限条件，支持行级过滤与列级脱敏 | 后端 | 项目自有 |
| [domain-proxy](./domain-proxy.md) | 基于 CGLIB 的领域实体代理，自动拦截字段变更并推送 DomainChangeEvent 领域事件 | 后端 | 项目自有 |
| [dynamic-data-query](./dynamic-data-query.md) | 动态数据查询体系，基于 FastRepository + PageRequest/Filter/SearchRequest 自动构建 Example 或 ... | 后端 | 项目自有 |
| [event-system](./event-system.md) | 发布-订阅事件系统，支持同步/异步事件、Handler排序、循环检测与事务集成 | 后端 | 项目自有 |
| [global-exception-handler](./global-exception-handler.md) | 全局异常处理器，统一捕获 Controller 层异常并转换为标准 Response 格式返回 | 后端 | 项目自有 |
| [groovy-runtime](./groovy-runtime.md) | Apache Groovy 运行时 — 动态脚本编译、执行与类型系统桥接 | 后端 | 框架:Groovy |
| [groovy-script-engine](./groovy-script-engine.md) | Groovy 脚本运行时引擎，支持动态编译、LRU 缓存、类型映射、元数据扫描与临时脚本持久化 | 后端 | 项目自有 |
| [jjwt](./jjwt.md) | JJWT 库 — JWT Token 的创建、签名、解析与验证 | 后端 | 框架:JJWT |
| [jsqlparser](./jsqlparser.md) | JSqlParser SQL 解析库 — 解析和改写 SQL 语句，用于数据权限条件注入 | 后端 | 框架:JSqlParser |
| [jwt-auth-gateway](./jwt-auth-gateway.md) | JWT 认证网关，集成 Spring Security 与 JJWT，支持 Token 创建、解析、Redis 有状态/无状态双模式 | 后端 | 项目自有 |
| [kryo](./kryo.md) | Kryo 高性能序列化库 — 用于工作流数据快照的深拷贝与持久化 | 后端 | 框架:Kryo |
| [rest-client](./rest-client.md) | REST HTTP 客户端封装，支持自动重试、代理配置、请求/响应拦截器与信任所有证书模式 | 后端 | 项目自有 |
| [spring-data-jpa](./spring-data-jpa.md) | Spring Data JPA — ORM 映射、Repository 抽象、分页查询、Specification 动态查询 | 后端 | 框架:Spring Data JPA |
| [spring-framework](./spring-framework.md) | Spring Framework / Spring Boot 核心能力 — IoC 容器、自动配置、事件发布、AOP、Web MVC | 后端 | 框架:Spring Boot |
| [spring-security](./spring-security.md) | Spring Security 认证与授权框架 — 提供 Filter 链、CSRF 防护、密码编码、权限控制 | 后端 | 框架:Spring Security |
| [unified-response](./unified-response.md) | 统一响应封装体系（Response / SingleResponse / MultiResponse / MapResponse），标准化 API 返回格式 | 后端 | 项目自有 |
| [workflow-engine](./workflow-engine.md) | 轻量级工作流引擎，支持流程定义、节点流转、审批、退回、委托、会签、抄送、数据快照与事件通知 | 后端 | 项目自有 |

---

**统计**: 共 17 篇 — 已实现 17 / 计划中 0 / 已废弃 0
