# springboot-framework frontend | 前端 monorepo

springboot-framework 前端工程，采用 [pnpm workspace](https://pnpm.io/zh/workspaces) 统一管理 PC 端与移动端两个应用，公共依赖代码抽取在 `packages/shared` 中。整体结构参考 [flow-frontend](https://github.com/codingapi/flow-frontend)：`pnpm-workspace.yaml` 声明 `packages/**` 与 `apps/**` 两个工作区，根目录 scripts 采用「动作:目标」命名并通过 `pnpm -F` 转发到对应 workspace 包，workspace 内部依赖使用 `workspace:*` 引用。

## 目录结构 | Structure

```
frontend/
├── pnpm-workspace.yaml     # workspace 声明（packages/** + apps/**）
├── package.json            # 根 scripts（dev/mock/build/test × pc/mobile）
├── apps/
│   ├── pc/                 # @springboot-framework/pc，PC 端管理后台（原 admin-ui）
│   └── mobile/             # @springboot-framework/mobile，移动端应用（原 mobile-ui）
└── packages/
    └── shared/             # @springboot-framework/shared，pc 与 mobile 的公共代码
```

- `apps/pc`（原 admin-ui）：React 18 + Ant Design 5 + Ant Design Pro Components + Rsbuild
- `apps/mobile`（原 mobile-ui）：React 18 + Ant Design Mobile 5 + Rsbuild
- `packages/shared`（@springboot-framework/shared）：两个应用的公共工具与 Mock 数据，通过 `workspace:*` 引用
- 两个应用均使用 Module Federation 做微前端集成

## 环境要求 | Prerequisites

- Node.js >= 20
- pnpm >= 10（仓库已通过 `packageManager` 字段锁定 pnpm@10.32.1）

## 快速开始 | Getting Started

```bash
cd frontend
pnpm install
```

## 根指令 | Root Scripts

所有指令在 `frontend` 根目录执行，命名规则为「动作:目标」：

| 指令 | 说明 |
|------|------|
| `pnpm dev:pc` | 启动 PC 端开发服务（代理后端） |
| `pnpm dev:mobile` | 启动移动端开发服务（代理后端） |
| `pnpm mock:pc` | 启动 PC 端 Mock 模式（前端 mock 数据） |
| `pnpm mock:mobile` | 启动移动端 Mock 模式（前端 mock 数据） |
| `pnpm build:pc` | PC 端生产构建 |
| `pnpm build:mobile` | 移动端生产构建 |
| `pnpm build` | 依次构建 PC 端与移动端 |
| `pnpm test:pc` | 运行 PC 端 Jest 测试 |
| `pnpm test:mobile` | 运行移动端 Jest 测试 |
| `pnpm test` | 依次运行两端测试 |

也可以直接进入某个应用目录执行该包自身的脚本，例如：

```bash
pnpm -F @springboot-framework/pc dev
pnpm -F @springboot-framework/mobile build
```

## 相关文档 | References

- [apps/pc README](./apps/pc/README.md)
- [apps/mobile readme](./apps/mobile/readme.md)
- 结构参考：[codingapi/flow-frontend](https://github.com/codingapi/flow-frontend)
