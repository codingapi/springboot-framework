---
name: frontend-state-management
description: Redux Toolkit 状态管理规范
status: 已实现
scope: 前端
source: 项目自有
---

## 解决什么问题

前端应用中存在需要跨组件共享的状态（如菜单刷新、页面标题、用户会话信息等）。如果没有统一的状态管理方案，会导致：

1. **Prop Drilling**：状态需要通过多层组件逐级传递，增加耦合度
2. **状态散落**：不同开发者用不同方式管理全局状态（Context、localStorage、全局变量等），难以维护
3. **不可预测的更新**：缺少统一的更新机制，状态变更难以追踪和调试

本项目使用 **@reduxjs/toolkit + react-redux** 作为统一的状态管理方案，通过 Slice 模式组织状态逻辑。

## 如何使用

### 1. 创建 Slice 文件

每个独立的状态域创建一个 Slice 文件，放在 `src/store/` 目录下。Slice 文件需要包含：

- **State 接口定义**：明确状态的类型结构
- **Action 类型定义**：声明 reducer 函数的签名
- **createSlice 调用**：定义 name、initialState 和 reducers
- **导出 actions**：从 slice 中解构并导出具名 action

**admin-ui 示例 — `store/MenuSlice.ts`：**
```typescript
import {createSlice} from '@reduxjs/toolkit';

export interface MenuStore {
    version: number;
}

export type MenuStoreAction = {
    refresh: (state: MenuStore) => void;
}

export const menuSlice = createSlice<MenuStore, MenuStoreAction, "menu", {}>({
    name: 'menu',
    initialState: {
        version: 0,
    },
    reducers: {
        refresh: (state) => {
            state.version = Math.random();
        },
    },
});

export const {refresh} = menuSlice.actions;
```

**mobile-ui 示例 — `sotre/LayoutSlice.ts`：**
```typescript
import {configureStore, createSlice, PayloadAction} from '@reduxjs/toolkit';

export interface LayoutStore {
    title: string;
    backHome: boolean;
}

export type MenuStoreAction = {
    changeTitle: (state: LayoutStore, action: PayloadAction<string>) => void;
}

export const layoutSlice = createSlice<LayoutStore, MenuStoreAction, "layout", {}>({
    name: 'layout',
    initialState: {
        title: '首页',
        backHome: true
    },
    reducers: {
        changeTitle: (state, action) => {
            const title = action.payload;
            state.title = title;
            state.backHome = '首页' === title;
        }
    },
});

export const {changeTitle} = layoutSlice.actions;
```

### 2. 注册 Slice 到 Store

在 `store/Redux.tsx`（admin-ui）或 Slice 文件内（mobile-ui）使用 `configureStore` 组合所有 reducer：

**admin-ui — `store/Redux.tsx`：**
```typescript
import {configureStore} from '@reduxjs/toolkit';
import {counterSlice} from './CounterSlice';
import {menuSlice} from './MenuSlice';

const store = configureStore({
    reducer: {
        counter: counterSlice.reducer,
        menu: menuSlice.reducer,
    },
});

export type RootState = ReturnType<typeof store.getState>;
export default store;
```

**mobile-ui — 在 `LayoutSlice.ts` 中直接创建 store：**
```typescript
export const layoutStore = configureStore({
    reducer: {
        layout: layoutSlice.reducer
    },
});

export type LayoutState = ReturnType<typeof layoutStore.getState>;
```

### 3. 在应用入口挂载 Provider

**admin-ui — `index.tsx`：**
```tsx
import {Provider} from "react-redux";
import store from "@/store/Redux";

root.render(
    <React.StrictMode>
        <ThemeProvider theme={theme}>
            <ConfigProvider locale={zhCN} theme={theme}>
                <Provider store={store}>
                    <RoutesProvider/>
                </Provider>
            </ConfigProvider>
        </ThemeProvider>
    </React.StrictMode>
);
```

**mobile-ui — `layout/index.tsx`：**
```tsx
import {Provider} from "react-redux";
import {layoutStore} from "@/sotre/LayoutSlice";

const Layout = () => {
    return (
        <Provider store={layoutStore}>
            <$Layout/>
        </Provider>
    )
}
```

### 4. 在组件中使用 useSelector / useDispatch

```tsx
import {useSelector} from "react-redux";
import {RootState} from "@/store/Redux";

const Layout = () => {
    const menuVersion = useSelector((state: RootState) => state.menu.version);

    useEffect(() => {
        // menuVersion 变化时重新加载菜单
        MenuRouteManager.getInstance().refresh();
        actionRef.current?.reload();
    }, [menuVersion]);
};
```

## 使用实例

### ✅ 正确示例

**创建新的 Slice：**
```typescript
// store/UserSlice.ts
import {createSlice, PayloadAction} from '@reduxjs/toolkit';

export interface UserStore {
    username: string;
    isLoggedIn: boolean;
}

export type UserStoreAction = {
    login: (state: UserStore, action: PayloadAction<string>) => void;
    logout: (state: UserStore) => void;
}

export const userSlice = createSlice<UserStore, UserStoreAction, "user", {}>({
    name: 'user',
    initialState: {
        username: '',
        isLoggedIn: false,
    },
    reducers: {
        login: (state, action) => {
            state.username = action.payload;
            state.isLoggedIn = true;
        },
        logout: (state) => {
            state.username = '';
            state.isLoggedIn = false;
        },
    },
});

export const {login, logout} = userSlice.actions;
```

**在组件中 dispatch action：**
```tsx
import {useDispatch} from "react-redux";
import {refresh} from "@/store/MenuSlice";

const SomeComponent = () => {
    const dispatch = useDispatch();

    const handleSave = async () => {
        await saveData();
        dispatch(refresh()); // ✅ 触发菜单刷新
    };
};
```

**带 payload 的 action：**
```tsx
import {useDispatch} from "react-redux";
import {changeTitle} from "@/sotre/LayoutSlice";

const Page = () => {
    const dispatch = useDispatch();

    useEffect(() => {
        dispatch(changeTitle('请假申请')); // ✅ PayloadAction<string>
    }, []);
};
```

### ❌ 错误示例

**直接修改 state 而不通过 reducer：**
```typescript
// ❌ 永远不要直接修改 store 中的状态
import store from "@/store/Redux";
const state = store.getState();
state.menu.version = 999; // 违反 Redux 不可变原则
```

**在 Slice 外部定义 mutable 全局变量代替 Redux：**
```typescript
// ❌ 不要用全局变量代替状态管理
let globalMenuVersion = 0;
export const refreshMenu = () => { globalMenuVersion++; };
```

**Slice 文件中不导出 Action 类型：**
```typescript
// ❌ 缺少类型定义，后续维护困难
export const someSlice = createSlice({
    name: 'some',
    initialState: { value: 0 },
    reducers: {
        update: (state) => { state.value += 1; },
    },
});
// 没有 interface、没有 type alias、没有显式泛型参数
```

**忘记将新 Slice 注册到 store：**
```typescript
// ❌ 创建了 UserSlice 但忘记添加到 configureStore
const store = configureStore({
    reducer: {
        counter: counterSlice.reducer,
        menu: menuSlice.reducer,
        // 缺少 user: userSlice.reducer
    },
});
```

**在组件中硬编码 state 路径而不使用 RootState 类型：**
```tsx
// ❌ 没有类型安全，重构时不会报错
const version = useSelector((state: any) => state.menu.version);
```
