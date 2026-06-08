---
name: redux/toolkit
module: redux
description: Redux Toolkit 状态管理库，提供简化的 Redux 状态管理模式
status: 已实现
scope: 前端
source: 框架:redux
import: "@reduxjs/toolkit"
framework_version: 2.2.7
---

## 解决什么问题

原生 Redux 存在样板代码过多、配置繁琐、不可变数据更新复杂等痛点。Redux Toolkit (RTK) 作为官方推荐的状态管理方案，解决了以下核心问题：

- **减少样板代码**：通过 `createSlice` 自动生成 action creators 和 reducer，无需手写 switch-case 和常量定义
- **简化不可变更新**：内置 Immer.js，允许以"可变"风格编写 reducer，自动转换为安全的不可变更新
- **标准化异步逻辑**：`createAsyncThunk` 统一处理请求生命周期（pending / fulfilled / rejected），避免手动管理 loading/error 状态
- **开箱即用的 Store 配置**：`configureStore` 默认集成 thunk 中间件、序列化检查、开发工具，零配置即可使用
- **TypeScript 友好**：完整类型推导，减少手写类型注解的负担
- **性能优化内置**：提供 `createSelector`（Reselect）用于派生数据的记忆化计算，避免不必要的重渲染

适用于需要全局状态共享、复杂业务状态流转、多组件协同的前端应用，如 admin-ui 的管理后台、mobile-ui 的业务流程等场景。

## 如何使用

### 安装与引入

```bash
npm install @reduxjs/toolkit react-redux
```

### 核心 API

#### configureStore — 创建 Store

```typescript
import { configureStore } from '@reduxjs/toolkit'

const store = configureStore({
  reducer: {
    user: userReducer,
    order: orderReducer,
  },
  // devTools、thunk、serializableCheck 均默认启用
})

export type RootState = ReturnType<typeof store.getState>
export type AppDispatch = typeof store.dispatch
```

#### createSlice — 定义状态切片

```typescript
import { createSlice, PayloadAction } from '@reduxjs/toolkit'

interface UserState {
  name: string
  token: string | null
}

const initialState: UserState = { name: '', token: null }

const userSlice = createSlice({
  name: 'user',
  initialState,
  reducers: {
    setUser(state, action: PayloadAction<{ name: string; token: string }>) {
      // 直接"修改"state，Immer 自动处理不可变更新
      state.name = action.payload.name
      state.token = action.payload.token
    },
    logout(state) {
      state.name = ''
      state.token = null
    },
  },
})

export const { setUser, logout } = userSlice.actions
export default userSlice.reducer
```

#### createAsyncThunk — 异步操作

```typescript
import { createAsyncThunk } from '@reduxjs/toolkit'

export const fetchUser = createAsyncThunk(
  'user/fetchUser',
  async (userId: string, thunkAPI) => {
    const response = await fetch(`/api/users/${userId}`)
    return response.json()
  }
)

// 在 slice 的 extraReducers 中处理三种状态
extraReducers: (builder) => {
  builder
    .addCase(fetchUser.pending, (state) => { state.loading = true })
    .addCase(fetchUser.fulfilled, (state, action) => {
      state.loading = false
      state.data = action.payload
    })
    .addCase(fetchUser.rejected, (state, action) => {
      state.loading = false
      state.error = action.error.message ?? 'Unknown error'
    })
}
```

#### createSelector — 派生数据记忆化

```typescript
import { createSelector } from '@reduxjs/toolkit'

const selectOrders = (state: RootState) => state.order.list
const selectFilter = (state: RootState) => state.order.filter

export const selectFilteredOrders = createSelector(
  [selectOrders, selectFilter],
  (orders, filter) => orders.filter(o => o.status === filter)
)
```

### React 绑定

```typescript
import { useSelector, useDispatch } from 'react-redux'
import type { RootState, AppDispatch } from './store'

// 带类型的 hooks
const useAppSelector = useSelector.withTypes<RootState>()
const useAppDispatch = useDispatch.withTypes<AppDispatch>()
```

## 使用实例

### 完整的用户模块示例

```typescript
// features/user/userSlice.ts
import { createSlice, createAsyncThunk, PayloadAction } from '@reduxjs/toolkit'
import { userService } from '../../services/userService'

export interface UserInfo {
  id: string
  username: string
  role: string
}

interface UserState {
  current: UserInfo | null
  list: UserInfo[]
  loading: boolean
  error: string | null
}

const initialState: UserState = {
  current: null,
  list: [],
  loading: false,
  error: null,
}

// 异步：获取当前用户
export const loadCurrentUser = createAsyncThunk<UserInfo>(
  'user/loadCurrent',
  async () => {
    return await userService.getCurrentUser()
  }
)

// 异步：获取用户列表
export const loadUserList = createAsyncThunk<UserInfo[], { keyword?: string }>(
  'user/loadList',
  async ({ keyword } = {}) => {
    return await userService.list({ keyword })
  }
)

const userSlice = createSlice({
  name: 'user',
  initialState,
  reducers: {
    clearError(state) {
      state.error = null
    },
    resetUser(state) {
      Object.assign(state, initialState)
    },
  },
  extraReducers: (builder) => {
    builder
      // loadCurrentUser
      .addCase(loadCurrentUser.pending, (state) => {
        state.loading = true
        state.error = null
      })
      .addCase(loadCurrentUser.fulfilled, (state, action) => {
        state.loading = false
        state.current = action.payload
      })
      .addCase(loadCurrentUser.rejected, (state, action) => {
        state.loading = false
        state.error = action.error.message ?? '加载失败'
      })
      // loadUserList
      .addCase(loadUserList.pending, (state) => {
        state.loading = true
      })
      .addCase(loadUserList.fulfilled, (state, action) => {
        state.loading = false
        state.list = action.payload
      })
      .addCase(loadUserList.rejected, (state, action) => {
        state.loading = false
        state.error = action.error.message ?? '列表加载失败'
      })
  },
})

export const { clearError, resetUser } = userSlice.actions
export default userSlice.reducer
```

```tsx
// features/user/UserProfile.tsx
import { useEffect } from 'react'
import { useAppSelector, useAppDispatch } from '../../app/hooks'
import { loadCurrentUser } from './userSlice'

export function UserProfile() {
  const dispatch = useAppDispatch()
  const { current, loading, error } = useAppSelector((state) => state.user)

  useEffect(() => {
    dispatch(loadCurrentUser())
  }, [dispatch])

  if (loading) return <div>加载中...</div>
  if (error) return <div>错误: {error}</div>
  if (!current) return <div>未登录</div>

  return (
    <div>
      <h2>{current.username}</h2>
      <p>角色: {current.role}</p>
    </div>
  )
}
```

### RTK Query — 声明式数据获取

```typescript
// app/api.ts
import { createApi, fetchBaseQuery } from '@reduxjs/toolkit/query/react'

export const api = createApi({
  reducerPath: 'api',
  baseQuery: fetchBaseQuery({ baseUrl: '/api', credentials: 'include' }),
  tagTypes: ['User'],
  endpoints: (builder) => ({
    getUsers: builder.query<UserInfo[], { keyword?: string }>({
      query: ({ keyword }) => `/users?keyword=${keyword ?? ''}`,
      providesTags: ['User'],
    }),
    updateUser: builder.mutation<UserInfo, Partial<UserInfo>>({
      query: (body) => ({ url: `/users/${body.id}`, method: 'PUT', body }),
      invalidatesTags: ['User'],
    }),
  }),
})

export const { useGetUsersQuery, useUpdateUserMutation } = api
```

```tsx
// 组件中使用 RTK Query hooks
function UserList() {
  const { data: users, isLoading, error } = useGetUsersQuery({ keyword: 'admin' })
  const [updateUser] = useUpdateUserMutation()

  if (isLoading) return <Spin />
  if (error) return <Alert message="加载失败" type="error" />

  return (
    <Table
      dataSource={users}
      columns={[
        { title: '用户名', dataIndex: 'username' },
        { title: '角色', dataIndex: 'role' },
        {
          title: '操作',
          render: (_, record) => (
            <Button onClick={() => updateUser({ ...record, role: 'editor' })}>
              设为编辑者
            </Button>
          ),
        },
      ]}
    />
  )
}
```

此模式在 admin-ui 和 mobile-ui 中广泛使用，配合 Module Federation 微前端架构，各子应用可独立管理自身状态切片，同时通过共享 Store 实现跨应用状态同步。
