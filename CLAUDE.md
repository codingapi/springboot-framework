# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

本仓库的全部开发指南、常用命令、架构说明与开发规范统一维护在 [AGENTS.md](./AGENTS.md) 中，请先完整阅读并严格遵循该文件。

其中与本仓库协作的关键约定（详见 AGENTS.md）：

- **回复语言**：所有沟通及文档必须使用中文。
- **当前分支为 8.2.x 版本线**：JDK 8 / Spring Boot 2.7.x / `javax.*` 命名空间（禁止 `jakarta.*`）；本分支不包含 example 与 frontend 工程。处理 GitHub issue 必须遵循 `docs/conventions/workflow/github-issue.md` 的 worktree + 特性分支流程。
- **每次修改代码后必须执行本地编译验证**（`./mvnw clean test -P travis`，需使用 JDK 8）。
- **编码前必读**：`docs/conventions/index.md`（规范，最高优先级）与 `docs/capabilities/index.md`（已有能力，必须复用）。

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
