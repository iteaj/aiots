---
name: app
description: 移动端前端开发技能。当AI需要开发uni-app x移动端页面(.uvue)、编写UTS脚本、定义TypeScript类型、对接后端接口时使用。
role: App 端开发工程师
---

# App 开发 Skill

## 1. 适用场景
编写移动端端代码时，包括但不限于：
- 页面开发：根据页面草图，编写页面代码 (.uvue)。
- 业务逻辑：根据页面草图和具体功能实现编写业务逻辑 (.uvue 的 script setup 部分)。
- TypeScript 类型定义：根据后端接口声明定义请求参数、响应参数、分页参数等 ts 参数类型。
- 后端接口对接：了解请求方法、请求参数、响应参数等；
- 模块文件路径：URL 地址和类型定义合并为一个 {模块}.ts 放在页面目录下 (参考第 3 章代码结构规范)。
- Cool UI 组件选型：什么功能选择什么组件，组件包含哪些属性、事件、样式、方法、插槽等。

## 2. 开发流程规范 [重要]

### 2.1 移动端开发完整流程
详细开发流程请参考：[移动端开发流程](./references/mobile-dev-flow.md)

### 2.2 代码模板
- [模块模板](./references/ApiTemplate.api.ts)（`UserApi` 地址 + 类型定义合并为一个文件）

### 2.3 App模块后端速查
- [App模块速查文档](../aiots-dev/references/app-module-quick-ref.md) - 移动端后端接口规范、Controller模板、认证流程
- [App模块接口清单](./references/app-module-reference.md) - App模块完整Controller清单
- [移动端项目介绍](./references/app-intro.md) - 技术栈/目录结构/核心模块说明

## 3. 代码结构规范

### 3.1 文件目录
```
app/
├── .cool/
│   ├── types/                # 全局共享类型
│   ├── service/              # HTTP 请求封装
│   └── store/                # 状态管理
├── pages/                    # 业务页面（含 {模块}.ts）
│   ├── user/
│   │   ├── user.ts           # UserApi 地址 + 类型定义
│   │   ├── login.uvue
│   │   └── components/
│   └── ...
├── components/               # 公共组件
└── uni_modules/              # uni-app 插件模块
    └── cool-ui/              # Cool UI 组件库
```

### 3.2 命名规范
- **模块文件**：`{模块名}.ts`，如 `user.ts`（URL 地址 + 类型定义合并）
- **页面文件**：小写 + 连字符，如 `device-list.uvue`
- **组件文件**：小写 + 连字符，如 `device-card.uvue`
- **路径地址**：驼峰写法，如 `/app/device/command/invokeUpdate`

## 4. Cool UI 组件库特别说明
- UI 优先使用 cool-ui 组件库，在 cool-ui 组件库中没有的组件，再考虑 uni-app x 的内置组件。
- 组件的属性、事件、样式、方法、插槽等使用方式参考 Cool Unix 使用技能 (skills/cool/skill.md) 或查组件库 (uni_modules/cool-ui/components/组件名)。
- 要确保选用的组件在 cool-ui 组件库中是确实存在，不能无中生有。
- 在使用弹性布局时优先使用 cool-ui 组件库的 cl-row 和 cl-col 组件。

## 5. 后端接口开发（移动端专用）

当移动端后端没有对应接口时，需要为移动端开发专用接口：

### 5.1 Controller 位置
- **包路径**：`com.iteaj.iboot.module.app.controller`
- **示例**：`AppXxxController.java`

### 5.2 接口路径前缀
- 移动端接口统一使用 `/m` 前缀
- 示例：`/m/xxx/list`，`/m/xxx/page`

## 6. 列表分页开发规范

### 6.1 分页请求参数
- **请求参数名**：使用 `current`（第几页）、`size`（每页条数）
- **示例**：`{ current: pageCurrent.value, size: pageSize }`

### 6.2 分页响应参数
- **响应字段**：`records`（数据列表）、`pages`（总页数）
- **hasMore 计算**：`pageCurrent.value < totalPages`（用服务端返回的 `pages` 比较，避免最后一页整数倍时重复加载）
- **page 自增时机**：仅在 API 成功响应后且 `hasMore` 为 true 时自增

### 6.3 列表容器 — 优先使用 cl-list-view
分页列表统一使用 `cl-list-view` 组件替代手动点击"加载更多"：

```uvue
<cl-list-view
    ref="listViewRef"
    :data="listData"
    refresher-enabled
    @bottom="onLoadMore"
    @refresher-refresh="onRefresh">
    <template #top>
        <!-- 列表内容，v-for 渲染卡片 -->
    </template>
</cl-list-view>
```

- **:data**：传入虚拟数据 `reactive([{ value: null }])`，驱动列表滚动行为
- **refresher-enabled**：开启下拉刷新
- **@bottom**：触底自动触发加载更多（替代 onReachBottom 或手动按钮）
- **@refresher-refresh**：下拉刷新回调
- **\<template #top\>**：列表内容放在此插槽中

### 6.4 核心状态变量
```typescript
const pageCurrent = ref(1);
const pageSize = 10;
const hasMore = ref(true);
const loadingMore = ref(false);
const listData = reactive<any[]>([{ value: null }]);
const listViewRef = ref<any>(null);
```

### 6.5 onLoadMore 模板
```typescript
function onLoadMore(): void {
    if (loadingMore.value || !hasMore.value) return;  // 并发防护
    loadingMore.value = true;
    fetchData(true).finally(() => { loadingMore.value = false; });
}
```

### 6.6 onRefresh 模板
```typescript
function onRefresh(): void {
    pageCurrent.value = 1;
    hasMore.value = true;
    fetchData(false).finally(() => {
        listViewRef.value?.stopRefresh();
    });
}
```

### 6.7 参考实现
- `app/pages/index/device.uvue` — 设备列表（完整分页实现）
- `app/pages/device/detail.uvue` — 设备详情（cl-list-view 用法参考）

## 7. 常用工具函数

### 7.1 获取页面内容可用高度
计算当前页面内容区域可用高度时，使用系统提供的 `getContentHeight` 方法，而非 `calc(100vh - xx)`。因为小程序和 APP 需扣除状态栏高度。

```typescript
import { getContentHeight } from "@/.cool";

// 参数 deduct：需扣除的 topbar 和菜单栏高度
// 返回：windowHeight - statusBarHeight - deduct
const height = getContentHeight(90);
```

**位置**：`app/.cool/utils/rect.ts`

### 7.2 模块文件检查
- [ ] 文件路径正确（`app/pages/{模块}/{模块}.ts`）
- [ ] `UserApi` 对象包含所有接口地址
- [ ] 字段与后端实体类保持一致
- [ ] 包含所有必要的类型（实体、请求参数、响应参数）
- [ ] 使用 TypeScript 严格类型检查

### 7.3 页面开发检查
- [ ] 页面文件使用 `.uvue` 后缀
- [ ] 页面已在 `pages.json` 中注册
- [ ] 使用了合适的滚动容器
- [ ] 优先使用 cool-ui 组件
- [ ] 正确调用接口并处理响应
- [ ] 添加了适当的错误处理

## 8. 常见问题

### 8.1 类型不匹配
确保前端类型声明与后端实体类字段完全一致，包括：
- 字段名称（使用驼峰命名）
- 字段类型（string、number、boolean 等）
- 可选字段使用 `?` 标记

### 8.2 接口调用失败
检查以下内容：
- 接口路径是否正确
- 请求方法是否匹配
- 参数名称和类型是否正确
- 是否需要认证 token

### 8.3 组件找不到
- 确认组件在 cool-ui 中存在
- 检查组件名称拼写
- 查看组件文档确认使用方式
- 考虑使用 uni-app x 内置组件替代
