---
name: frontend-css-variables
description: 前端 CSS 变量定义与使用规范
status: 已实现
scope: 前端
source: 项目自有
---

## 解决什么问题

在前端项目中，主题颜色、字体大小、背景色等设计令牌（Design Tokens）需要在多处复用，且需要与 Ant Design / Ant Design Mobile 组件库的主题配置保持同步。如果直接在样式文件中硬编码颜色值或尺寸，会导致：

1. **主题不一致**：修改主色时需要逐个文件查找替换，容易遗漏
2. **JS/CSS 不同步**：Ant Design 主题 token 和 CSS 样式使用不同的颜色来源
3. **多端适配困难**：admin-ui 和 mobile-ui 有不同的设计令牌，但缺乏统一的管理方式

本规范通过 CSS Custom Properties（CSS 变量）+ SCSS 变量桥接 + `CSSUtils.getRootVariable()` 运行时读取三层机制，实现设计令牌的单一来源管理。

## 如何使用

### 1. 在 `index.scss` 的 `:root` 中定义 CSS 变量

所有 CSS 变量的**实际值**必须在 `src/styles/index.scss` 的 `:root` 选择器中定义。这是唯一的设计令牌来源。

**admin-ui/src/styles/index.scss** 定义的变量：
```scss
:root {
  --primary-color: #094edc;
  --body-background-color: #fdfdfd;

  --content-font-size-large: 24px;
  --content-font-size-middle: 16px;
  --content-font-size-small: 12px;

  --content-font-size: var(--content-font-size-middle);
}
```

**mobile-ui/src/styles/index.scss** 定义的变量：
```scss
:root {
  --primary-color: #0f58ea;
  --body-background-color: #e6e7ea;

  --content-font-size-large: 24px;
  --content-font-size-middle: 16px;
  --content-font-size-small: 12px;

  --content-font-size: var(--content-font-size-middle);
}

// 移动端额外：将主色注入 antd-mobile 主题
:root:root {
  --adm-color-primary: var(--primary-color);
}
```

### 2. 在 `variable.scss` 中创建 SCSS 变量桥接

为了让 SCSS 文件也能使用这些变量，在 `src/styles/variable.scss` 中用 SCSS 变量包装 CSS 变量，并提供 fallback 值：

```scss
// 主题颜色
$theme-primary-color: var(--primary-color, #4a79d8);
// 背景颜色
$body-background-color: var(--body-background-color, #fdfdfd);
// 标题字体大小
$title-font-size: var(--content-font-size-middle, 16px);
// 内容字体大小
$content-font-size: var(--content-font-size, 12px);
```

> mobile-ui 的 `variable.scss` 还定义了移动端专属变量（如 `$page-header-height`、`$page-footer-height`），这些不需要 CSS 变量形式，可直接使用固定值。

### 3. 在 SCSS 文件中通过 `@use` 引入变量

```scss
@use "@/styles/variable" as *;

.page-header {
  background-color: white;
  height: $page-header-height !important;
  box-shadow: inset 0 0 0 1px $body-background-color;
}
```

### 4. 在 TypeScript/React 中通过 `CSSUtils.getRootVariable()` 读取

当需要在 JS/TS 代码中获取 CSS 变量值时（如配置 Ant Design 主题），使用 `@codingapi/ui-framework` 提供的 `CSSUtils`：

```typescript
import {CSSUtils, ThemeConfig, ThemeProvider} from "@codingapi/ui-framework";

const theme = {
    token: {
        colorPrimary: CSSUtils.getRootVariable('--primary-color'),
        contentFontSize: CSSUtils.getRootVariable('--content-font-size'),
    }
} as ThemeConfig;
```

然后将 `theme` 同时传给 `<ThemeProvider>` 和 Ant Design 的 `<ConfigProvider>`，确保框架组件和 UI 库使用同一套主题。

## 使用实例

### ✅ 正确示例

**在 SCSS 中使用变量：**
```scss
@use "@/styles/variable" as *;

.my-component {
  color: $theme-primary-color;
  font-size: $content-font-size;
  background-color: $body-background-color;
}
```

**在 TSX 中注入 Ant Design 主题：**
```tsx
import {CSSUtils, ThemeConfig, ThemeProvider} from "@codingapi/ui-framework";
import {ConfigProvider} from "antd";

const theme = {
    token: {
        colorPrimary: CSSUtils.getRootVariable('--primary-color'),
        contentFontSize: CSSUtils.getRootVariable('--content-font-size'),
    }
} as ThemeConfig;

<ThemeProvider theme={theme}>
    <ConfigProvider theme={theme}>
        <App />
    </ConfigProvider>
</ThemeProvider>
```

**在 `:root` 中添加新的 CSS 变量：**
```scss
// index.scss
:root {
  --primary-color: #094edc;
  --body-background-color: #fdfdfd;
  // ✅ 新增变量放在 :root 中
  --sidebar-width: 240px;
}
```

```scss
// variable.scss
// ✅ 对应添加 SCSS 桥接变量
$sidebar-width: var(--sidebar-width, 240px);
```

### ❌ 错误示例

**直接硬编码颜色值：**
```scss
// ❌ 不要直接使用十六进制颜色
.my-component {
  color: #094edc;
  background-color: #fdfdfd;
}
```

**在 SCSS 中直接使用 CSS 变量而不经过 variable.scss 桥接：**
```scss
// ❌ 缺少 fallback 值，且没有统一管理
.my-component {
  color: var(--primary-color);
}
```

**在 JS 中手动读取 CSS 变量而不用 CSSUtils：**
```typescript
// ❌ 不要手动操作 DOM API
const color = getComputedStyle(document.documentElement)
    .getPropertyValue('--primary-color');
```

**在组件内联样式中硬编码主题色：**
```tsx
// ❌ 内联样式无法响应主题变更
<div style={{ color: '#094edc' }}>标题</div>
```

**mobile-ui 忘记注入 antd-mobile 主题变量：**
```scss
// ❌ mobile-ui 的 index.scss 中缺少以下配置会导致 antd-mobile 组件颜色不一致
// 必须包含：
:root:root {
  --adm-color-primary: var(--primary-color);
}
```
