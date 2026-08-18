# apps/pc (原 admin-ui) with Antd For Micro Frontends

This is a simple React App with RsBuild & Typescript, managed by the pnpm workspace in the `frontend` root.

## Features
1. Support RsBuild ModuleFederationPlugin for Micro Frontends
2. Support Dynamic zip component loading
3. Support Dynamic Routing & Dynamic Menu
4. Support Axios for API calls
5. Support Antd & Pro-Components UI Library
6. Support Redux for State Management
7. Support Mock Server for API Mocking
8. Support Monaco Editor for Code Editor
9. Support Access ControlPanel for Menu & Page Components
10. Support Jest for Unit Testing
11. Support DockerCompose for Deployment

## Running

在 `frontend` 根目录执行（pnpm workspace）：

```shell
cd frontend
pnpm install

pnpm dev:pc     # 开发模式（代理后端）
pnpm mock:pc    # Mock 模式（前端 mock 数据）
```

## Build

```shell
cd frontend
pnpm build:pc
```

## Test

```shell
cd frontend
pnpm test:pc
```

## Deploy
```shell
cd scripts
sh package.sh
sh deploy.sh
```

