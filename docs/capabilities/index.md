# 能力（Capabilities）

> 🔄 此文件由 `rebuild_pkr_index.py` 自动生成，请勿手动编辑。

## ✅ 已实现

| 名称 | 模块 | 描述 | 范围 | 来源 |
|------|------|------|------|------|
| [springboot/spring-boot](./springboot/spring-boot.md) | springboot | Spring Boot 2.7 基础框架，提供 IoC 容器、AOP、事件发布、配置自动装配等核心能力 | 后端 | 框架:springboot |
| [springboot-starter/domain-event](./springboot-starter/domain-event.md) | springboot-starter | 领域事件总线（同步/异步/事务后），提供事件栈追踪、循环事件检测与全局 Handler 调度 | 后端 | 项目自有 |
| [springboot-starter/domain-proxy](./springboot-starter/domain-proxy.md) | springboot-starter | 基于 CGLIB 的实体代理工厂，在创建时发布 DomainCreateEvent，在 setter 触发时对比字段差异并发布 DomainChangeE... | 后端 | 项目自有 |
| [springboot-starter-data-authorization/jdbc-proxy](./springboot-starter-data-authorization/jdbc-proxy.md) | springboot-starter-data-authorization | 自定义 JDBC 驱动代理（Connection/Statement/PreparedStatement/CallableStatement/Result... | 后端 | 项目自有 |
| [springboot-starter-data-authorization/sql-interceptor](./springboot-starter-data-authorization/sql-interceptor.md) | springboot-starter-data-authorization | 基于 JSqlParser 的 SQL 拦截器，对 SELECT 语句递归改写以注入行级数据权限条件 | 后端 | 项目自有 |
| [springboot-starter-data-fast/jpa-repository](./springboot-starter-data-fast/jpa-repository.md) | springboot-starter-data-fast | 增强版 JPA Repository 接口体系，支持动态 SQL、原生查询、Map 返回、分页、排序与高级搜索 | 后端 | 项目自有 |
| [springboot-starter-flow/schema-reader](./springboot-starter-flow/schema-reader.md) | springboot-starter-flow | 流程设计图（Schema）解析器，将 JSON 描述的节点/边转换为 FlowNode/FlowRelation 内存模型 | 后端 | 项目自有 |
| [springboot-starter-script/groovy-script-runtime](./springboot-starter-script/groovy-script-runtime.md) | springboot-starter-script | 基于 Groovy 的动态脚本运行时，支持函数调用与脚本执行两类入口、LRU 缓存、临时脚本落盘与启动恢复 | 后端 | 项目自有 |
| [springboot-starter-security/token-gateway](./springboot-starter-security/token-gateway.md) | springboot-starter-security | 无状态 JWT 与有状态 Redis 双模 Token 网关，统一 Token 生命周期、过期/续期与权限注入 | 后端 | 项目自有 |

---

**统计**: 共 9 篇 — 已实现 9 / 计划中 0 / 已废弃 0
