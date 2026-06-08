# 能力（Capabilities）

> 🔄 此文件由 `rebuild_pkr_index.py` 自动生成，请勿手动编辑。

## ✅ 已实现

| 名称 | 描述 | 范围 | 来源 |
|------|------|------|------|
| [apache-groovy](./apache-groovy.md) | 动态语言运行时，支持脚本编译与执行 | 后端 | 框架:Apache Groovy |
| [bouncycastle](./bouncycastle.md) | 密码学算法库，提供 AES/RSA 等加密算法支持 | 后端 | 框架:BouncyCastle |
| [byte-buddy](./byte-buddy.md) | 字节码操作库，用于运行时动态生成 JPA 实体类 | 后端 | 框架:Byte Buddy |
| [command-executor](./command-executor.md) | CQRS 命令执行器接口，定义标准化的命令-响应契约，支持带参和无参两种执行模式 | 后端 | 项目自有 |
| [commons-crypto](./commons-crypto.md) | Apache 加密工具封装，提供 AES 等加密操作 | 后端 | 框架:Apache Commons Crypto |
| [commons-io](./commons-io.md) | IO 工具类库，简化文件/流操作 | 后端 | 框架:Apache Commons IO |
| [commons-text](./commons-text.md) | 文本处理工具库，提供字符串命名转换等功能 | 后端 | 框架:Apache Commons Text |
| [data-authorization](./data-authorization.md) | 基于 JDBC 代理链的透明数据权限过滤，支持行级和列级权限控制 | 后端 | 项目自有 |
| [domain-event](./domain-event.md) | 领域实体事件自动追踪机制，支持创建、变更、删除事件的自动推送与字段级变更检测 | 后端 | 项目自有 |
| [event-system](./event-system.md) | 基于发布-订阅模式的事件系统，支持同步/异步事件处理、事件排序和异常隔离 | 后端 | 项目自有 |
| [exception-handling](./exception-handling.md) | 国际化异常体系与事件异常处理，支持多语言错误消息、占位符参数和事件循环检测 | 后端 | 项目自有 |
| [fast-repository](./fast-repository.md) | JPA Repository 增强，支持基于 PageRequest 的动态过滤查询、HQL 构建和 SearchRequest 自动解析 | 后端 | 项目自有 |
| [fastjson](./fastjson.md) | 高性能 JSON 序列化/反序列化库 | 后端 | 框架:Fastjson |
| [groovy-script-engine](./groovy-script-engine.md) | Groovy 脚本运行时编译、缓存与热更新引擎，提供 REST API 管理脚本生命周期 | 后端 | 项目自有 |
| [http-client-factory](./http-client-factory.md) | 信任所有证书的 Apache HttpClient 5 工厂，用于内部 HTTPS 调用跳过证书验证 | 后端 | 项目自有 |
| [httpclient5](./httpclient5.md) | HTTP 客户端库，支持 HTTPS 信任所有证书的请求 | 后端 | 框架:Apache HttpClient 5 |
| [jackson-databind](./jackson-databind.md) | JSON 数据绑定库，Spring Boot 默认 JSON 处理器 | 后端 | 框架:Jackson Databind |
| [jjwt](./jjwt.md) | JWT 令牌生成与解析库 | 后端 | 框架:JJWT |
| [jsqlparser](./jsqlparser.md) | SQL 解析与改写库，用于数据权限 SQL 注入 | 后端 | 框架:JSqlParser |
| [kryo](./kryo.md) | 高性能 Java 序列化框架，用于数据快照 | 后端 | 框架:Kryo |
| [page-request-filter](./page-request-filter.md) | 动态查询过滤请求封装，支持多种比较关系、组合条件、HTTP 参数自动解析 | 后端 | 项目自有 |
| [rest-template-context](./rest-template-context.md) | 预配置的 RestTemplate 单例上下文，内置信任所有证书的 HttpClient，简化内部服务间 HTTP 调用 | 后端 | 项目自有 |
| [security-auth](./security-auth.md) | 基于 Spring Security 的 JWT/Redis 双模式认证框架，支持 Token 自动续期与登录扩展 | 后端 | 项目自有 |
| [spring-data-jpa](./spring-data-jpa.md) | ORM 映射、Repository 抽象、自动分页 | 后端 | 框架:Spring Data JPA |
| [spring-data-redis](./spring-data-redis.md) | Redis 客户端抽象，支持缓存和有状态认证 Token 存储 | 后端 | 框架:Spring Data Redis |
| [spring-ioc-container](./spring-ioc-container.md) | Spring IoC 容器提供依赖注入、Bean 生命周期管理、AOP 支持 | 后端 | 框架:Spring Framework |
| [spring-security](./spring-security.md) | 认证授权框架，提供 Filter Chain、CSRF 防护 | 后端 | 框架:Spring Security |
| [spring-web-mvc](./spring-web-mvc.md) | Web MVC 框架，提供 RESTful API 路由、请求参数绑定、响应序列化 | 后端 | 框架:Spring Web MVC |
| [unified-response](./unified-response.md) | 统一 API 响应封装，提供 Response、SingleResponse、MultiResponse、MapResponse 四种标准响应格式 | 后端 | 项目自有 |
| [workflow-engine](./workflow-engine.md) | 内置工作流引擎，支持流程定义、节点流转、审批、退回、会签、抄送、委托与数据快照 | 后端 | 项目自有 |

---

**统计**: 共 30 篇 — 已实现 30 / 计划中 0 / 已废弃 0
