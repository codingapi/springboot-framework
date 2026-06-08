---
name: react/react
module: react
description: React UI 框架配合 Ant Design 组件库，用于构建前端用户界面
status: 已实现
scope: 前端
source: 框架:react
import: "react"
framework_version: 18.3.1
---

## 解决什么问题

在企业级中后台与移动端业务系统中，前端开发面临以下核心痛点：

- **UI 一致性差**：多页面、多团队并行开发时，缺乏统一的组件体系导致视觉风格与交互行为不一致，用户体验割裂。
- **重复造轮子**：表单、表格、弹窗、导航等通用界面元素在每个项目中被反复实现，研发效率低下且质量参差不齐。
- **状态管理复杂**：随着业务增长，组件间数据流变得难以追踪，传统命令式 DOM 操作模式维护成本急剧上升。
- **前后端对接低效**：缺少与后端统一响应结构（`Response` / `SingleResponse` / `MultiResponse`）和分页查询（`PageRequest` + `Filter`）相匹配的前端消费范式。

React 18 + Ant Design 5 的组合提供了声明式 UI 编程模型与开箱即用的高质量企业级组件库，使开发者能够以组合式、可预测的方式快速构建一致的用户界面，并与 springboot-framework 后端的统一响应和数据查询能力无缝衔接。

## 如何使用

### 依赖安装

```bash
# 在 admin-ui 或 mobile-ui 目录下
npm install react@^18.3.1 react-dom@^18.3.1 antd@^5.x
```

### 项目集成要点

1. **组件引入**：按需导入 Ant Design 组件，配合 Rsbuild 自动完成 Tree Shaking，无需额外配置 babel-plugin-import。
2. **主题定制**：通过 Ant Design 5 的 ConfigProvider + Design Token 体系统一管理品牌色、圆角、字号等设计变量，确保多模块视觉一致。
3. **与后端对接**：封装统一的 HTTP 请求层，将后端 `Response.errCode / errMessage` 映射为全局提示；将 `MultiResponse.data` + `PageRequest` 参数直接绑定到 ProTable / ProList 的 `request` 属性，实现分页筛选零胶水代码。
4. **状态管理**：轻量场景使用 React Context + useReducer；跨模块共享状态推荐使用 Zustand 或 valtio，避免过度引入 Redux。
5. **微前端集成**：admin-ui 与 mobile-ui 均通过 Module Federation 暴露/消费远程模块，React 作为共享依赖（shared singleton）确保运行时只存在一个实例。

### 关键 API

| API / 概念 | 说明 |
|------------|------|
| `useState` / `useEffect` / `useMemo` | React Hooks，声明式状态与副作用管理 |
| `<ConfigProvider>` | Ant Design 全局配置：主题 token、国际化、组件尺寸 |
| `<ProTable>` / `<ProForm>` | Ant Design Pro Components，内置搜索表单、分页、列定义，天然适配后端 PageRequest |
| `message` / `notification` | 全局反馈，对接后端 Response 错误码 |
| `useRequest` (ahooks) | 异步数据请求 Hook，支持缓存、重试、轮询 |

## 使用实例

### 基础列表页（对接后端 MultiResponse + PageRequest）

```tsx
import React from 'react';
import { ProTable } from '@ant-design/pro-components';
import type { ProColumns } from '@ant-design/pro-components';
import { message } from 'antd';
import { request } from '@/utils/request'; // 封装的统一请求

interface UserItem {
  id: string;
  name: string;
  email: string;
  status: number;
}

const columns: ProColumns<UserItem>[] = [
  { title: '姓名', dataIndex: 'name' },
  { title: '邮箱', dataIndex: 'email', copyable: true },
  {
    title: '状态',
    dataIndex: 'status',
    valueEnum: { 1: { text: '启用', status: 'Success' }, 0: { text: '禁用', status: 'Error' } },
  },
];

const UserList: React.FC = () => {
  return (
    <ProTable<UserItem>
      headerTitle="用户管理"
      columns={columns}
      rowKey="id"
      request={async (params) => {
        // params 自动携带 current, pageSize 及搜索条件
        const res = await request.get('/api/users', { params });
        if (!res.success) {
          message.error(res.errMessage);
          return { data: [], total: 0, success: false };
        }
        return { data: res.data, total: res.totalElements, success: true };
      }}
      search={{ labelWidth: 'auto' }}
      pagination={{ defaultPageSize: 20 }}
    />
  );
};

export default UserList;
```

### 表单提交（对接后端 SingleResponse）

```tsx
import React from 'react';
import { ModalForm, ProFormText, ProFormSelect } from '@ant-design/pro-components';
import { message } from 'antd';
import { request } from '@/utils/request';

interface CreateUserProps {
  open: boolean;
  onClose: () => void;
  onSuccess: () => void;
}

const CreateUserModal: React.FC<CreateUserProps> = ({ open, onClose, onSuccess }) => {
  return (
    <ModalForm
      title="新建用户"
      open={open}
      modalProps={{ onCancel: onClose }}
      onFinish={async (values) => {
        const res = await request.post('/api/users', values);
        if (res.success) {
          message.success('创建成功');
          onSuccess();
          return true;
        }
        message.error(res.errMessage || '创建失败');
        return false;
      }}
    >
      <ProFormText name="name" label="姓名" rules={[{ required: true }]} />
      <ProFormText name="email" label="邮箱" rules={[{ required: true, type: 'email' }]} />
      <ProFormSelect
        name="status"
        label="状态"
        options={[{ label: '启用', value: 1 }, { label: '禁用', value: 0 }]}
        initialValue={1}
      />
    </ModalForm>
  );
};

export default CreateUserModal;
```

### 主题定制与国际化

```tsx
import React from 'react';
import { ConfigProvider, zhCN } from 'antd';

const App: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  return (
    <ConfigProvider
      locale={zhCN}
      theme={{
        token: {
          colorPrimary: '#1677ff',
          borderRadius: 6,
          fontSize: 14,
        },
      }}
    >
      {children}
    </ConfigProvider>
  );
};

export default App;
```

以上示例展示了 React + Ant Design 在 springboot-framework 项目中的典型用法：列表页自动对接 `PageRequest` 分页与过滤、表单提交消费统一 `Response` 结构、以及通过 `ConfigProvider` 实现全局主题管控。结合 Module Federation 微前端架构，各业务模块可独立开发部署，同时共享统一的 UI 基础与设计规范。
