# 规范（Conventions）

> 🔄 此文件由 `rebuild_pkr_index.py` 自动生成，请勿手动编辑。

## ✅ 已实现

| 名称 | 模块 | 描述 | 范围 | 来源 |
|------|------|------|------|------|
| [java/domain-modeling-convention](./java/domain-modeling-convention.md) | java | 领域建模规范：同一性判断、聚合间深复制、可配置单一出口与只读集合 | 后端 | 项目自有 |
| [java/naming-convention](./java/naming-convention.md) | java | 方法命名规范：按返回数量选择 get/find，必须存在的场景用 require | 后端 | 项目自有 |
| [java/static-method-convention](./java/static-method-convention.md) | java | 静态方法规范：领域判断回归实例方法，明确允许的 static 场景 | 后端 | 项目自有 |
| [java/test-design-convention](./java/test-design-convention.md) | java | 测试设计规范：模型不为测试让路，区分模型能力与测试脚手架 | 后端 | 项目自有 |
| [springboot-starter/ddd-layered-architecture](./springboot-starter/ddd-layered-architecture.md) | springboot-starter | DDD 分层架构规范，定义 interface/app/domain/infra 四层的依赖规则和职责划分 | 后端 | 项目自有 |
| [springboot-starter/event-driven-convention](./springboot-starter/event-driven-convention.md) | springboot-starter | 事件驱动开发规范，定义 IEvent/IHandler 的使用约定和事件处理模式 | 后端 | 项目自有 |
| [springboot-starter/exception-handling-convention](./springboot-starter/exception-handling-convention.md) | springboot-starter | 全局异常处理规范，定义异常抛出和捕获的标准模式 | 后端 | 项目自有 |
| [springboot-starter/response-convention](./springboot-starter/response-convention.md) | springboot-starter | 统一响应格式规范，定义 Controller 层返回值的标准格式 | 后端 | 项目自有 |
| [springboot-starter-data-fast/dynamic-query-convention](./springboot-starter-data-fast/dynamic-query-convention.md) | springboot-starter-data-fast | 动态查询规范，定义 PageRequest + RequestFilter + FastRepository 的使用模式 | 后端 | 项目自有 |
| [workflow/github-issue](./workflow/github-issue.md) | workflow | GitHub Issue 标准处理流程 — 定位 → 回复 → worktree 修复 → 推送特性分支 → 推送后回复（8.x/17.x 双线） | 全栈 | 项目自有 |

---

**统计**: 共 10 篇 — 已实现 10 / 计划中 0 / 已废弃 0
