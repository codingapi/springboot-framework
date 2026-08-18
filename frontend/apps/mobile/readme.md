# apps/mobile (原 mobile-ui) with Antd For Micro Frontends

This is a Simple React App with RsBuild & Typescript, managed by the pnpm workspace in the `frontend` root.

## Features
1. Support RsBuild ModuleFederationPlugin for Micro Frontends
2. Support Dynamic zip component loading
3. Support Dynamic Routing & Dynamic Menu
4. Support Axios for API calls
5. Support Antd-Mobile UI Library
6. Support Redux for State Management
7. Support Mock Server for API Mocking
8. Support Access Components
9. Support Jest for Unit Testing
10. Support DockerCompose for Deployment

## Running

在 `frontend` 根目录执行（pnpm workspace）：

```shell
cd frontend
pnpm install

pnpm dev:mobile     # 开发模式（代理后端）
pnpm mock:mobile    # Mock 模式（前端 mock 数据）
```

## Build

```shell
cd frontend
pnpm build:mobile
```

## Test

```shell
cd frontend
pnpm test:mobile
```

## Deploy
```shell
cd scripts
sh package.sh
sh deploy.sh
```

