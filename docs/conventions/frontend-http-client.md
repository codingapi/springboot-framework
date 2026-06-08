---
name: frontend-http-client
description: 前端 HTTP 请求封装规范
status: 已实现
scope: 前端
source: 项目自有
---

## 解决什么问题

前端应用需要与后端 REST API 交互，如果直接使用 axios 或 fetch，会面临以下问题：

1. **错误处理分散**：每个请求都要手动 try/catch 并显示错误提示，代码重复且容易遗漏
2. **反馈方式不一致**：不同页面使用不同的消息提示组件（message、notification、Toast），用户体验不统一
3. **API 调用无结构**：请求散落在组件中，没有按业务领域组织，难以维护和复用
4. **超时和认证缺失**：缺少统一的超时配置和 Token 注入机制

本项目通过 `@codingapi/ui-framework` 提供的 `HttpClient` 类进行统一封装，结合按领域组织的 API 模块文件，实现标准化的 HTTP 请求管理。

## 如何使用

### 1. 创建 HttpClient 实例

在 `src/api/index.ts` 中创建全局单例的 `httpClient`，配置超时时间和消息反馈回调：

**admin-ui — `src/api/index.ts`：**
```typescript
import {message} from "antd";
import {HttpClient} from "@codingapi/ui-framework";

export const httpClient = new HttpClient(10000, {
    success: (msg: string) => {
        message.success(msg).then();
    },
    error: (msg: string) => {
        message.error(msg).then();
    }
})
```

**mobile-ui — `src/api/index.ts`：**
```typescript
import {Toast} from "antd-mobile";
import {HttpClient} from "@codingapi/ui-framework";

export const httpClient = new HttpClient(10000, {
    success: (msg: string) => {
        Toast.show({
            content: msg,
        })
    },
    error: (msg: string) => {
        Toast.show({
            icon: 'fail',
            content: msg,
        })
    }
})
```

> `HttpClient` 构造函数的第一个参数是超时时间（毫秒），第二个参数是消息回调对象，包含 `success` 和 `error` 两个方法。

### 2. 按业务领域创建 API 模块文件

在 `src/api/` 目录下按后端领域划分文件，每个文件导出该领域的 API 函数：

```
src/api/
├── index.ts       # HttpClient 实例
├── account.ts     # 登录、验证码
├── user.ts        # 用户管理
├── flow.ts        # 工作流
├── leave.ts       # 请假
├── oss.ts         # 文件上传
└── jar.ts         # 部署管理
```

### 3. API 函数命名和签名约定

- **查询列表**：使用 `list` 作为函数名，接受 `params`、`sort`、`filter`、`match` 四个参数，调用 `httpClient.page()`
- **查询详情**：使用描述性名称如 `detail`、`users`，调用 `httpClient.get()`
- **命令操作**：使用动词如 `save`、`remove`、`startFlow`，调用 `httpClient.post()`
- 所有 API 函数都应该是 `async function`，返回 `httpClient` 方法的 Promise

### 4. HttpClient 提供的方法

| 方法 | 用途 | 示例 |
|------|------|------|
| `get(url, params?)` | GET 请求 | `httpClient.get('/api/query/user/list', {current:1, pageSize:999999})` |
| `post(url, body)` | POST 请求 | `httpClient.post('/api/cmd/user/save', body)` |
| `page(url, params, sort, filter, match)` | 分页查询 | `httpClient.page('/api/query/flowWork/list', params, sort, filter, match)` |

## 使用实例

### ✅ 正确示例

**标准 API 模块文件：**
```typescript
// src/api/user.ts
import {httpClient} from "@/api/index";

export async function list(
    params: any,
    sort: any,
    filter: any,
    match: { key: string, type: string }[]
) {
    return httpClient.page('/api/query/user/list', params, sort, filter, match);
}

export async function save(body: any) {
    return httpClient.post('/api/cmd/user/save', body);
}

export async function remove(id: any) {
    return httpClient.post('/api/cmd/user/remove', {id});
}
```

**在组件中调用 API：**
```tsx
import {list, save} from "@/api/user";

const UserPage = () => {
    const loadData = async () => {
        const result = await list(
            {current: 1, pageSize: 20},
            {},
            {},
            []
        );
        // result 已经过 HttpClient 统一处理
    };

    const handleSave = async (values: any) => {
        await save(values); // ✅ 成功/失败消息由 HttpClient 自动展示
    };
};
```

**登录 API 示例：**
```typescript
// src/api/account.ts
import {httpClient} from "@/api/index";

export async function login(body: Account.LoginRequest) {
    return httpClient.post('/user/login', body);
}

export async function captcha() {
    return httpClient.get('/open/captcha');
}
```

### ❌ 错误示例

**直接在组件中使用 axios/fetch：**
```tsx
// ❌ 不要绕过 HttpClient 直接发请求
import axios from "axios";

const loadData = async () => {
    const res = await axios.get('/api/query/user/list');
    // 缺少统一错误处理、超时配置、Token 注入
};
```

**在组件中内联写请求逻辑：**
```tsx
// ❌ 请求逻辑不应写在组件内部
const UserPage = () => {
    const handleSave = async () => {
        const res = await httpClient.post('/api/cmd/user/save', formValues);
        if (res.success) {
            message.success('保存成功');
        } else {
            message.error(res.errMessage);
        }
    };
};
```

**API 文件不按领域组织：**
```typescript
// ❌ 不要把所有 API 放在一个大文件中
// api/all.ts
export async function userList() { ... }
export async function userSave() { ... }
export async function flowList() { ... }
export async function flowSave() { ... }
export async function leaveStart() { ... }
// 几百个函数混在一起，无法维护
```

**自行封装新的 HTTP 工具类：**
```typescript
// ❌ 不要重复造轮子
class MyHttpClient {
    async get(url: string) {
        const res = await fetch(url);
        return res.json();
    }
}
// 已有 @codingapi/ui-framework 的 HttpClient，不需要自建
```

**忽略消息回调的配置：**
```typescript
// ❌ 不传消息回调会导致用户看不到操作反馈
export const httpClient = new HttpClient(10000);
// 应该始终提供 success 和 error 回调
```
