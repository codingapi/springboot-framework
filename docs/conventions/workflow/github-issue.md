---
name: workflow/github-issue
module: workflow
description: GitHub Issue 标准处理流程 — 定位 → 回复 → worktree 修复 → 推送特性分支 → 推送后回复（8.x/17.x 双线）
status: 已实现
scope: 全栈
source: 项目自有
---

## 解决什么问题

不遵守此流程会导致：

- 问题分析结论只留在本地，issue 参与者无法同步进展
- 直接在主干工作区改代码，污染工作区、难以回滚和并行处理多个 issue
- 只修了一条版本线就声称修复完成，另一条线（8.x 或 17.x）漏修
- 修复完成后 issue 长期悬空，无法追溯「哪个提交解决了哪个 issue」
- 分支、worktree、合并目标不统一，发布时遗漏改动

## 适用范围

`codingapi/springboot-framework` 仓库的**所有 GitHub issue**。本仓库同时维护两条版本线，issue 可能落在任意一条上：

| 版本线 | 技术栈 | 模块 | 发布路径 |
|--------|--------|------|----------|
| 17.x | JDK 17 / Spring Boot 3.x，`jakarta.*` | 5 个 starter + `example/` + `frontend/`（PC 端、移动端） | `17.3.x` → `main` |
| 8.x | JDK 8 / Spring Boot 2.7.x，`javax.*` | 5 个 starter，无 `example/`、无 `frontend/` | `8.2.x` 独立发布 |

两条版本线功能基本一致，但 **API 命名空间不同（`jakarta` vs `javax`）、JDK 与 Spring Boot 代际不同**，代码不能直接照搬。

## 分支模型

| 分支 | 用途 |
|------|------|
| `main` | 默认分支，17.x 系列的发布分支；`17.3.x` 通过 PR 合入后打 `v17.3.x` 标签 |
| `17.3.x` | 17.x 版本开发分支，17.x issue 的**基线**与 PR 目标 |
| `8.2.x` | 8.x 版本维护分支，8.x issue 的**基线**与 PR 目标，独立打 `v8.2.x` 标签发布 |
| `2.x` | 历史版本分支，仅在 issue 明确指向 2.x 时使用 |

> 本仓库没有 `dev` 分支，也没有「统一开发分支」的概念：**每条版本线各自维护、各自交付**。仓库中残留的 `dev` 分支为历史遗留，已废弃，不得作为基线或交付目标。

## 核心原则

1. **分析可以不开 worktree，改代码必须开**：定位阶段只读代码即可；一旦确定要修改，必须新建 git worktree + 特性分支。
2. **先定版本线，再动手**：定位阶段必须确认 issue 属于 8.x、17.x 还是两者，并按对应版本分支创建 worktree。
3. **先回复，再动手**：定位到问题后，先在对应 issue 回复根因、影响版本与处理方案。
4. **版本分支只作基线，不参与交付**：worktree 基于 `17.3.x` 或 `8.2.x` 创建；修复提交、推送与 issue 关联全部发生在该 issue 的特性分支上。完成后**不合并版本分支**，是否合并由人工决定，助手只在任务完成时提示分支与 PR 链接。
5. **双版本 issue 必须双线验证**：同时影响两条线时，两条线各切一个特性分支、各自测试、各自推送，不得只修一条线就宣布完成。
6. **提交即关联 issue**：提交信息使用 `Closes #<id>` 关联；是否已真正关闭取决于是否进入默认分支 `main`，未自动关闭时由维护者确认后手动关闭。
7. **推送后再回复**：特性分支推送到 GitHub 之后，才在 issue 回复修复内容、提交与验证结果；**未推送前不得发布完成回复**。

## 标准步骤

### 1. 读取 issue

```bash
gh issue view <id> --repo codingapi/springboot-framework \
  --json number,title,state,labels,body,comments,author,createdAt
```

### 2. 定位问题（可不建 worktree）

- 使用代码搜索、LSP、文档（`docs/conventions`、`docs/capabilities`）定位根因；
- **判定版本线**：issue 的复现环境、依赖坐标（`17.3.x` / `8.2.x`）、报错栈中的 `jakarta.*` 或 `javax.*` 都是判断依据；无法判断时按 17.x 处理，并在定位回复中请提问者确认是否需要同步 8.x；
- 明确影响模块（哪个 starter / `example` / `frontend` 的 PC 端或移动端）；
- 判断是否属于已有能力缺陷，优先复用既有能力（统一响应 `Response`、动态查询 `PageRequest` + `FastRepository`、事件 `EventPusher`、数据权限 SQL 拦截等），而不是新增一套实现。

### 3. 回复 issue（定位结论）

将以下内容整理为中文评论：

- 问题原因（引用具体类/文件/方法）；
- **影响版本线**（8.x / 17.x / 两者）及判断依据；
- 处理方案（改动点、行为变化、兼容性；双版本时说明两条线的差异点）；
- 交付方式（基于对应版本分支的特性分支名，完成后由维护者合并）。

```bash
gh issue comment <id> --repo codingapi/springboot-framework --body-file /tmp/issue-<id>-analysis.md
```

### 4. 创建 worktree 与特性分支

`.worktrees/` 已被 `.gitignore` 忽略。按版本线二选一；双版本则两条都建：

```bash
# 17.x
git worktree add .worktrees/issue-<id>-17x -b feature/issue-<id>-<slug>-17x 17.3.x

# 8.x
git worktree add .worktrees/issue-<id>-8x -b feature/issue-<id>-<slug>-8x 8.2.x
```

- 分支命名统一带版本线后缀：`feature/issue-<issue编号>-<简短英文描述>-<17x|8x>`，避免两条线同名冲突；
- 17.x 的前端改动在同一 worktree 的 `frontend/` 目录内进行，首次使用需安装依赖（worktree 内没有 `node_modules`）：

```bash
cd frontend
pnpm install --frozen-lockfile --prefer-offline
```

> worktree 仅以对应版本分支为**基线**；创建之后的所有提交、推送与 issue 关联都只发生在该特性分支上，版本分支不参与交付。

### 5. 修复与验证（TDD）

- 先补测试（红灯），再实现（绿灯），最后重构；
- 后端测试位于各模块 `src/test/java`，使用 JUnit 5，类名以 `Test` 结尾；
- 编译/测试必须使用该版本线对应的 JDK：17.x 用 JDK 17，8.x 用 JDK 8；
- 后端验证：`./mvnw test -pl <module> -Dtest=XxxTest`；改动公共模块或被依赖模块后执行 `./mvnw clean install`；提交前执行 `./mvnw clean test -P travis`（与 CI 一致）；
- 前端验证（仅 17.x，在 `frontend/` 下）：`pnpm test:pc` / `pnpm test:mobile`、`pnpm build:pc` / `pnpm build:mobile`；
- 8.x 分支没有 `example/` 与 `frontend/`，验证只覆盖后端模块，新增代码使用 `javax.*`；
- **修改代码后必须本地编译/测试通过**，失败项如实报告，不得声称已验证；双版本 issue 必须在两条线各自的 worktree 中分别验证。

### 6. 推送特性分支到 GitHub（任务到此结束）

```bash
git push -u origin feature/issue-<id>-<slug>-17x   # 17.x
git push -u origin feature/issue-<id>-<slug>-8x    # 8.x
```

- 只推送该 issue 的特性分支，`17.3.x` / `8.2.x` 保持不动，是否合并由人工决定；
- 禁止推送到 `17.3.x` / `8.2.x` / `main` / `dev`，禁止在本地合并；
- 任务完成提示中给出分支地址与 PR 链接（PR 目标分支：17.x → `17.3.x`，8.x → `8.2.x`，**不指向 `main`**），不代为合并；
- 双版本 issue 需要两个 PR，分别对应两条版本线；
- 推送后可清理本地 worktree（`git worktree remove <path>`），分支保留。

### 7. 提交信息与 issue 关闭

提交信息格式：

```
fix: <一句话描述> (<版本线>/<关联模块>)

<可选的改动说明>

Closes #<id>
```

- `Closes #<id>` 只在提交进入**默认分支**（`main`）时由 GitHub 自动关闭，即只有 17.x 经 `17.3.x → main` 合并时才可能自动关闭；
- 8.x 的特性分支合入 `8.2.x` 不会关闭 issue；仅修 8.x 的 issue 需在确认修复已发布后由维护者手动关闭；
- 双版本同时修复时，17.x 提交写 `Closes #<id>`，8.x 提交写 `Ref #<id>`，避免关闭语义重复；
- 如需提前关闭，必须由维护者确认后执行 `gh issue close <id>`，不得擅自关闭。

### 8. 推送后回复 issue

**前置条件：特性分支已推送到 GitHub**。未推送前只可说明「修复已提交到本地特性分支，待推送」，不得发布完成结论。

回复内容包含：

- 根因与最终方案（与步骤 3 的差异）；
- 变更文件/能力（新增公共 API、行为变化），双版本时按版本线分别列出；
- 验证结果（各版本线的测试用例数、构建结果、使用的 JDK）；
- 推送分支与提交号、对应 PR/评审状态；
- 升级/兼容提示（如有行为变更）；
- 未覆盖的版本线（若有），以及是否需要后续跟进。

## 禁止事项

- 不得向 `17.3.x` / `8.2.x` / `main` / `dev` 推送，不得在本地合并（是否合并由人工决定）；
- 不得把 8.x 的修复直接 cherry-pick 到 17.x（或反向）而不做 `javax` ↔ `jakarta`、JDK / Spring Boot 代际适配；
- 不得只修一条版本线就宣布双版本 issue 已修复；
- 特性分支推送是标准步骤，但推送前必须确认远程分支名，避免误推；
- 不得在主工作区（非 worktree）直接修改业务代码；
- 不得跳过测试与编译验证；
- 不得在特性分支推送前发布完成回复（定位回复不受此限）；
- 不得在代码未进入默认分支前擅自关闭 issue；
- 前端 workspace 内跨 package 引用必须使用包名（`@springboot-framework/*`），不得使用相对路径。

## 检查清单

- [ ] issue 已读取并确认现状
- [ ] 已确认影响版本线（8.x / 17.x / 两者）
- [ ] 定位结论（含影响版本）已在 issue 回复
- [ ] worktree + 特性分支已创建（基于 `17.3.x` / `8.2.x`）
- [ ] 测试先行且全量通过，构建通过（使用对应 JDK）
- [ ] 双版本 issue 已在两条线分别验证
- [ ] 特性分支已推送到 GitHub（版本分支未改动）
- [ ] 提交信息包含 `Closes #<id>`（双版本时 8.x 用 `Ref #<id>`）
- [ ] 推送后已在 issue 回复修复与验证结果
