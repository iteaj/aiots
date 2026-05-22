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
- 类型文件路径：什么类型的文件应该在哪个目录下 (参考第 3 章代码结构规范)。
- Cool UI 组件选型：什么功能选择什么组件，组件包含哪些属性、事件、样式、方法、插槽等。

## 2. 开发流程规范 [重要]

### 2.1 移动端开发完整流程
详细开发流程请参考：[移动端开发流程](./references/mobile-dev-flow.md)

### 2.2 代码模板
- [类型声明模板](./references/TypeTemplat.type.ts)
- [接口声明模板](./references/ApiTemplate.api.ts)

### 2.3 App模块后端速查
- [App模块速查文档](../aiots-dev/references/app-module-quick-ref.md) - 移动端后端接口规范、Controller模板、认证流程
- [App模块接口清单](./references/app-module-reference.md) - App模块完整Controller清单
- [移动端项目介绍](./references/app-intro.md) - 技术栈/目录结构/核心模块说明

## 3. 代码结构规范

### 3.1 文件目录
```
app/
├── .cool/
│   ├── aiots/              # 类型声明和接口声明
│   │   ├── xxx.type.ts     # 类型声明文件
│   │   └── xxx.api.ts      # 接口声明文件
│   ├── service/            # 服务层
│   └── store/              # 状态管理
├── pages/                  # 页面文件
├── components/             # 自定义组件
└── uni_modules/            # uni-app 插件模块
    └── cool-ui/           # Cool UI 组件库
```

### 3.2 命名规范
- **类型声明文件**：`{功能名}.type.ts`
- **接口声明文件**：`{功能名}.api.ts`
- **页面文件**：小写 + 连字符，如 `device-list.uvue`
- **组件文件**：大驼峰命名，如 `DeviceCard.uvue`

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

### 6.1 类型声明检查
- [ ] 类型文件路径正确（`app/.cool/aiots/xxx.type.ts`）
- [ ] 字段与后端实体类保持一致
- [ ] 包含所有必要的类型（实体、请求参数、响应参数）
- [ ] 使用 TypeScript 严格类型检查

### 6.2 接口声明检查
- [ ] 接口文件路径正确（`app/.cool/aiots/xxx.api.ts`）
- [ ] 接口路径与后端一致
- [ ] 请求方法与后端一致（GET、POST 等）
- [ ] 参数类型定义正确
- [ ] 返回值类型定义正确

### 6.3 页面开发检查
- [ ] 页面文件使用 `.uvue` 后缀
- [ ] 页面已在 `pages.json` 中注册
- [ ] 使用了合适的滚动容器
- [ ] 优先使用 cool-ui 组件
- [ ] 正确调用接口并处理响应
- [ ] 添加了适当的错误处理

## 7. 常见问题

### 7.1 类型不匹配
确保前端类型声明与后端实体类字段完全一致，包括：
- 字段名称（使用驼峰命名）
- 字段类型（string、number、boolean 等）
- 可选字段使用 `?` 标记

### 7.2 接口调用失败
检查以下内容：
- 接口路径是否正确
- 请求方法是否匹配
- 参数名称和类型是否正确
- 是否需要认证 token

### 7.3 组件找不到
- 确认组件在 cool-ui 中存在
- 检查组件名称拼写
- 查看组件文档确认使用方式
- 考虑使用 uni-app x 内置组件替代
