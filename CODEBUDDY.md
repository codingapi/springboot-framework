# CODEBUDDY.md This file provides guidance to CodeBuddy when working with code in this repository.

本仓库的全部开发指南、常用命令、架构说明与开发规范统一维护在 [AGENTS.md](./AGENTS.md) 中，请先完整阅读并严格遵循该文件，不要重复维护两份内容。

其中与本仓库协作的关键约定（详见 AGENTS.md）：

- **回复语言**：所有沟通及文档必须使用中文。
- **当前分支为 8.2.x 版本线**：JDK 8 / Spring Boot 2.7.x / `javax.*` 命名空间（禁止 `jakarta.*`）；本分支不包含 example 与 frontend 工程。
- **每次修改代码后必须执行本地编译/测试验证**（`./mvnw clean test -P travis`，需使用 JDK 8）。
- **处理 GitHub issue 必须遵循** `docs/conventions/workflow/github-issue.md` 的 worktree + 特性分支流程，不合并版本分支。
- **编码前必读**：`docs/conventions/index.md`（项目规范，最高优先级）与 `docs/capabilities/index.md`（已有能力清单，必须复用、禁止重复实现）。
