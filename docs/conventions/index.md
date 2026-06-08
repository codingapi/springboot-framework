# 规范（Conventions）

> 🔄 此文件由 `rebuild_pkr_index.py` 自动生成，请勿手动编辑。

## ✅ 已实现

| 名称 | 描述 | 范围 | 来源 |
|------|------|------|------|
| [ddd-layered-architecture](./ddd-layered-architecture.md) | DDD 分层架构规范 — 遵循 interface → app → domain ← infra 四层依赖规则，domain 层定义接口，infra 层提供实现 | 后端 | 项目自有 |
| [event-handler-standard](./event-handler-standard.md) | 事件处理规范 — Handler 必须实现 IHandler 接口并注册为 Spring Bean，事件必须实现 IEvent 接口 | 后端 | 项目自有 |
| [global-exception-handling](./global-exception-handling.md) | 全局异常处理规范 — 业务异常必须使用 LocaleMessageException，禁止在 Controller 中 try-catch 吞没异常 | 后端 | 项目自有 |
| [unified-response-format](./unified-response-format.md) | 统一响应格式规范 — 所有 API 必须使用 Response/SingleResponse/MultiResponse 标准封装返回 | 后端 | 项目自有 |

---

**统计**: 共 4 篇 — 已实现 4 / 计划中 0 / 已废弃 0
