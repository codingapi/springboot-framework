# 规范（Conventions）

> 🔄 此文件由 `rebuild_pkr_index.py` 自动生成，请勿手动编辑。

## ✅ 已实现

| 名称 | 描述 | 范围 | 来源 |
|------|------|------|------|
| [autoconfiguration-pattern](./autoconfiguration-pattern.md) | 每个 starter 模块必须同时注册 spring.factories（旧版兼容）和 AutoConfiguration.imports（Spring ... | 后端 | 项目自有 |
| [context-register-pairing](./context-register-pairing.md) | 每个 Context 单例必须配套 Register 类和 Configuration 类，形成三件套：Context（持有状态）+ Register（注... | 后端 | 项目自有 |
| [context-singleton](./context-singleton.md) | 全局状态通过单例 Context 类持有，由 Register 类在启动时注入依赖 | 后端 | 项目自有 |
| [locale-exception-handling](./locale-exception-handling.md) | 所有业务异常必须使用 LocaleMessageException，通过 errCode 实现国际化，由全局 HandlerExceptionResolv... | 后端 | 项目自有 |
| [meta-table-annotation](./meta-table-annotation.md) | 通过 @MetaTable/@MetaColumn/@MetaRelation 注解声明表的元数据描述，驱动动态实体生成和元数据查询 | 后端 | 项目自有 |
| [servlet-exception-resolver](./servlet-exception-resolver.md) | 使用 HandlerExceptionResolver（而非 @ControllerAdvice）作为全局异常处理机制，返回统一 JSON 响应格式 | 后端 | 项目自有 |
| [slf4j-logging](./slf4j-logging.md) | 所有 Java 类必须使用 Lombok @Slf4j 注解进行日志输出，禁止直接使用 LoggerFactory.getLogger() | 全栈 | 项目自有 |
| [unified-response](./unified-response.md) | 所有 API 返回值必须使用统一响应封装（Response → SingleResponse / MultiResponse / MapResponse）... | 后端 | 项目自有 |

---

**统计**: 共 8 篇 — 已实现 8 / 计划中 0 / 已废弃 0
