---
name: admin
description: 管理端前端开发技能。当AI需要开发或修改admin-ui(Vue 3 + Ant Design Vue)管理端页面、组件配置(UTable/UDrawer/UForm)、接口对接、权限标识、菜单路由时使用。
role: 管理端前端开发工程师
---

# 管理端前端开发技能

## 1. 适用场景

编写管理端(admin-ui)代码时，包括但不限于：
- **页面开发**：在 `admin-ui/src/views/{模块}/{功能}/index.vue` 创建新页面
- **组件使用**：UView、UTable、UForm、UDrawer、UModal 等核心组件的使用
- **接口对接**：HTTP请求（GET/POST/PUT/DELETE）、权限标识配置
- **菜单配置**：后台菜单管理与路由关联

## 2. 文档索引

| 文档 | 内容 |
|------|------|
| **[admin-quick-ref.md](./references/admin-quick-ref.md)** | CoreConsts常量 / HTTP请求 / 组件体系总览 / UTag·UButton func / 事件速查 / 跨视图引用 / 表格相关 / 编辑容器(Drawer/Modal) / 表单项组件 / URow布局 / UDrawerView·UModalView / 全局实例属性 / 常见问题 |
| **[admin-ui-dev-flow.md](./references/admin-ui-dev-flow.md)** | 开发流程与检查清单 / 页面规范 / 组件体系 / UButton·UTag |
| [admin-ui-intro.md](./references/admin-ui-intro.md) | 管理端项目介绍（技术栈/目录结构/路由等） |
| [page-template.vue](./references/page-template.vue) | 页面完整模板 |

## 3. 开发流程

```
1. 后端接口开发完成（Controller/Service/Mapper）
        ↓
2. 新建页面：admin-ui/src/views/{模块}/{功能}/index.vue
        ↓
3. 复制 page-template.vue，按需修改
   - 搜索项配置
   - 列定义
   - 表单项
   - 接口路径
   - 权限标识
   - 子视图
        ↓
4. 后台菜单管理中添加菜单，URL 填写 {模块}/{功能}
```

## 4. 核心规范

- **组件 name** — 全局唯一，建议 `{模块}{功能}`
- **权限标识** — `v-auth` 格式为 `{模块}:{功能}:{操作}`，与后台菜单配置保持一致
- **接口路径** — 所有 `url` 与后端 Controller 路径完全匹配
- **响应码判断** — 必须使用 `CoreConsts.SuccessCode`（禁止硬编码 `200`）
- **编辑容器** — 字段多（>6）用Drawer，字段少（≤6）用Modal
- **跨视图引用** — uid 使用 fullUid 格式（如 `UView:paramDrawer`）

## 5. 检查清单

- [ ] 组件 name 全局唯一
- [ ] 权限标识格式正确 {模块}:{功能}:{操作}
- [ ] 接口 url 与后端 Controller 一致
- [ ] 列定义覆盖所有展示字段，width 必填
- [ ] 表单项覆盖所有可编辑字段
- [ ] 校验规则与后端 @NotNull/@NotBlank 对应
- [ ] uid 在同一视图上下文内唯一
- [ ] 编辑容器选择正确（Drawer vs Modal）
- [ ] UViewTable/UEditableTable 设置 `:scroll="{x: 1200}"`
