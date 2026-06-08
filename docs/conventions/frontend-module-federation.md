---
name: frontend-module-federation
description: Module Federation 微前端集成规范
status: 已实现
scope: 前端
source: 项目自有
---

## 解决什么问题

本项目包含多个前端应用（admin-ui、mobile-ui），它们需要共享基础依赖（React、ReactDOM）并具备独立部署、运行时集成的能力。传统的 monorepo 或 iframe 方案存在以下问题：

1. **依赖重复打包**：每个应用都打包一份 React，用户加载多个应用时浪费带宽
2. **版本不一致**：不同应用使用不同版本的 React 会导致 hooks 报错和运行时异常
3. **无法运行时组合**：构建时就确定了所有依赖，无法在运行时动态加载其他应用的模块
4. **部署耦合**：修改一个子应用需要重新构建和部署整个主应用

本项目使用 **@module-federation/enhanced + @module-federation/rsbuild-plugin** 配合 Rsbuild 构建工具，实现基于 Module Federation 的微前端架构。

## 如何使用

### 1. 安装依赖

确保 `package.json` 中包含以下依赖：

```json
{
    "dependencies": {
        "@module-federation/enhanced": "^0.17.0"
    },
    "devDependencies": {
        "@module-federation/rsbuild-plugin": "^0.17.0",
        "@rsbuild/core": "1.4.7",
        "@rsbuild/plugin-react": "1.3.4",
        "@rsbuild/plugin-sass": "..."
    }
}
```

### 2. 配置 Rsbuild + Module Federation

在 `rsbuild.config.ts` 中通过 `pluginModuleFederation` 插件配置联邦模块：

**admin-ui/rsbuild.config.ts：**
```typescript
import * as path from 'path';
import {defineConfig} from '@rsbuild/core';
import {pluginReact} from '@rsbuild/plugin-react';
import {pluginSass} from '@rsbuild/plugin-sass';
import {pluginModuleFederation} from '@module-federation/rsbuild-plugin';

export default defineConfig({
    plugins: [
        pluginReact(),
        pluginSass(),
        pluginModuleFederation({
            name: "AdminUI",
            shared: {
                react: {
                    requiredVersion: '^18.3.1',
                    singleton: true,
                    strictVersion: false,
                },
                'react-dom': {
                    requiredVersion: '^18.3.1',
                    singleton: true,
                    strictVersion: false,
                },
            }
        }, {
            ssr: false,
            ssrDir: path.resolve(__dirname, 'ssr'),
            environment: 'development',
        }),
    ],
    server: {
        port: 8000,
    },
    source: {
        entry: {
            index: './src/entry.tsx',
        },
    },
    resolve: {
        alias: {
            '@': path.resolve(__dirname, 'src'),
            react: path.resolve(__dirname, 'node_modules/react'),
            'react-dom': path.resolve(__dirname, 'node_modules/react-dom'),
        }
    },
});
```

**mobile-ui/rsbuild.config.ts：**
```typescript
import {pluginModuleFederation} from '@module-federation/rsbuild-plugin';

export default defineConfig({
    plugins: [
        pluginReact(),
        pluginSass(),
        pluginModuleFederation({
            name: "MobileUI",       // ✅ 每个应用必须有唯一的 name
            shared: {
                react: {
                    requiredVersion: '^18.3.1',
                    singleton: true,
                    strictVersion: false,
                },
                'react-dom': {
                    requiredVersion: '^18.3.1',
                    singleton: true,
                    strictVersion: false,
                },
            }
        }, {
            ssr: false,
            ssrDir: path.resolve(__dirname, 'ssr'),
            environment: 'development',
        }),
    ],
    // ...其余配置相同
});
```

### 3. 关键配置说明

| 配置项 | 说明 |
|--------|------|
| `name` | 联邦模块的唯一标识名，如 `"AdminUI"`、`"MobileUI"` |
| `shared.react.singleton: true` | 确保整个运行时只加载一个 React 实例，避免多实例导致 hooks 报错 |
| `shared.react.strictVersion: false` | 允许在一定范围内兼容不同小版本，避免因严格版本匹配导致加载失败 |
| `shared.react.requiredVersion` | 声明所需的 React 版本范围 |
| `ssr: false` | 当前项目不使用 SSR，设为 false |
| `source.entry.index` | 入口文件为 `./src/entry.tsx`，而非直接指向 `index.tsx` |

### 4. 入口文件异步化

Module Federation 要求入口文件使用异步导入，确保共享依赖在应用初始化前完成协商：

```typescript
// src/entry.tsx
import("./index");
```

### 5. 开发环境代理配置

通过 `rsbuild.config.dev.ts` 扩展基础配置，添加后端 API 代理：

```typescript
import {defineConfig} from '@rsbuild/core';
import commonConfig from './rsbuild.config';

export default defineConfig({
    ...commonConfig,
    server: {
        port: 8000,
        proxy: {
            '/api': 'http://127.0.0.1:8090',
            '/open': 'http://127.0.0.1:8090',
            '/user': 'http://127.0.0.1:8090',
        },
    },
})
```

### 6. Mock 模式配置

通过 `rsbuild.config.mock.ts` 提供无后端的开发模式：

```typescript
import {defineConfig} from '@rsbuild/core';
import commonConfig from './rsbuild.config';
import {usersHandler} from "./mocks/user";
import {productsHandler} from "./mocks/product";

export default defineConfig({
    ...commonConfig,
    server: {
        port: 8000,
    },
    dev: {
        setupMiddlewares: (middlewares, devServer) => {
            if (!devServer) {
                throw new Error('webpack-dev-server is not defined');
            }
            console.log('mock server is running');
            middlewares.unshift(usersHandler, productsHandler);
        }
    }
})
```

## 使用实例

### ✅ 正确示例

**配置 shared 依赖时使用 singleton：**
```typescript
pluginModuleFederation({
    name: "AdminUI",
    shared: {
        react: {
            requiredVersion: '^18.3.1',
            singleton: true,      // ✅ 保证单例
            strictVersion: false,  // ✅ 允许版本弹性
        },
        'react-dom': {
            requiredVersion: '^18.3.1',
            singleton: true,
            strictVersion: false,
        },
    }
})
```

**入口文件使用异步导入：**
```typescript
// ✅ entry.tsx - 异步加载确保共享依赖先协商
import("./index");
```

**新增一个联邦应用时保持配置一致：**
```typescript
// new-app/rsbuild.config.ts
pluginModuleFederation({
    name: "NewApp",  // ✅ 唯一名称
    shared: {
        react: {
            requiredVersion: '^18.3.1',  // ✅ 与其他应用保持一致
            singleton: true,
            strictVersion: false,
        },
        'react-dom': {
            requiredVersion: '^18.3.1',
            singleton: true,
            strictVersion: false,
        },
    }
})
```

### ❌ 错误示例

**忘记设置 singleton: true：**
```typescript
// ❌ 缺少 singleton 可能导致多个 React 实例
shared: {
    react: {
        requiredVersion: '^18.3.1',
        // singleton: true  ← 遗漏！
        strictVersion: false,
    },
}
```

**入口文件使用同步导入：**
```typescript
// ❌ entry.tsx 不能直接写业务代码
import React from 'react';
import ReactDOM from 'react-dom/client';
// Module Federation 需要先完成共享依赖协商
// 必须使用 import("./index") 的异步形式
```

**不同应用使用不同的 React 版本范围：**
```typescript
// ❌ AdminUI 使用 ^18.3.1
shared: { react: { requiredVersion: '^18.3.1' } }

// ❌ MobileUI 使用 ^17.0.0 — 版本不兼容！
shared: { react: { requiredVersion: '^17.0.0' } }
```

**将 rsbuild.config.ts 与 rsbuild.config.dev.ts 混为一谈：**
```typescript
// ❌ 不要在基础配置中写死代理地址
export default defineConfig({
    server: {
        proxy: {
            '/api': 'http://127.0.0.1:8090', // 这应该在 dev 配置中
        },
    },
})
```

**Module Federation 名称重复：**
```typescript
// ❌ 两个应用使用了相同的 name
// admin-ui/rsbuild.config.ts
pluginModuleFederation({ name: "App" })

// mobile-ui/rsbuild.config.ts
pluginModuleFederation({ name: "App" })  // 冲突！应改为 "MobileUI"
```
