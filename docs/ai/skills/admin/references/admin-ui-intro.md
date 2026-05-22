# 管理端项目介绍 (admin-ui)

## 基本信息

| 属性 | 说明 |
|------|------|
| **项目目录** | `admin-ui/` |
| **项目名称** | ivzone |
| **版本** | 3.0.0 |
| **开发服务器端口** | 默认 Vite 端口（5173） |
| **后端代理** | `/api/*` → `http://localhost:8085`（移除 `/api` 前缀） |

---

## 技术栈

| 分类 | 技术 | 版本 |
|------|------|------|
| **核心框架** | Vue | 3.3.x |
| **构建工具** | Vite + @vitejs/plugin-vue + @vitejs/plugin-vue-jsx | 4.5.x |
| **UI 组件库** | Ant Design Vue | 4.0.x |
| **状态管理** | Vuex | 4.1.x |
| **路由** | Vue Router（Hash 模式） | 4.2.x |
| **HTTP 请求** | Axios | 1.4.x |
| **图表** | ECharts | 5.3.x |
| **可视化组态** | @meta2d/core (le5le) | 1.0.x |
| **富文本编辑器** | WangEditor | 5.1.x |
| **Markdown 编辑器** | md-editor-v3 | 4.3.x |
| **代码编辑器** | vue3-ace-editor | 2.2.x |
| **地图** | 高德地图 @amap/amap-jsapi-loader | 1.0.x |
| **日期处理** | dayjs | 1.11.x |
| **工具库** | lodash / lodash-es | 4.17.x |
| **图片裁剪** | vue-cropper | 1.1.x |
| **全屏** | screenfull | 6.0.x |
| **序列化** | qs | 6.11.x |

---

## 项目结构

```
admin-ui/src/
├── api/            # 接口定义
├── components/     # 公共组件（全局注册）
├── env/            # 环境配置
├── plugin/         # 系统插件（le5le、通用插件）
├── router/         # 路由配置（Hash 模式 + 动态菜单路由）
├── store/          # Vuex 状态管理
│   └── module/
│       ├── core.js     # 系统核心状态（菜单、任务栏、权限等）
│       └── view.js     # 视图状态
├── theme/          # 主题样式
├── utils/          # 工具函数
└── views/          # 业务视图
```

---

## 公共组件（全局注册）

所有组件在 `src/components/index.js` 中统一注册，开发时可直接使用组件标签名。

| 组件名 | 文件 | 说明 |
|--------|------|------|
| `UTable` | `table/BasicTable.jsx` | 基础表格组件 |
| `UEditableTable` | `table/EditableTable.jsx` | 可编辑表格 |
| `USearch` | `search/BasicSearch.vue` | 搜索栏组件 |
| `UFormModal` | `modal/FormModal.jsx` | 表单弹窗 |
| `UBasicModal` | `modal/BasicModal.jsx` | 基础弹窗 |
| `UFormDrawer` | `drawer/FormDrawer.jsx` | 表单抽屉 |
| `UBasicDrawer` | `drawer/BasicDrawer.jsx` | 基础抽屉 |
| `UForm` | `form/basic/Form.jsx` | 表单组件 |
| `UCron` | `cron/UCron.vue` | Cron 表达式编辑器 |
| `UCropper` | `cropper/ImageCropper.vue` | 图片裁剪 |
| `UDrawerLogger` | `logger/DrawerLogger.vue` | 抽屉日志 |
| `URunningLogger` | `logger/RunningLogger.vue` | 运行日志 |
| 表单项组件 | `form/basic/formItem.jsx` | 各种表单控件（输入框、下拉、日期等） |
| 视图组件 | `components/view/` | Context、LinkView、View 等视图基础组件 |
| 图标组件 | `components/icon/` | 图标选择器 |
| 地图组件 | `components/amap/` | 高德地图 |

---

## 路由机制

- **模式**：Hash 路由（`createWebHashHistory`）
- **静态路由**：登录页、404页、主布局、设备详情、大屏等
- **动态菜单路由**：登录后从后端获取菜单数据，调用 `resolverMenuToRoutes()` 动态注册路由
- **页面路径规则**：`views/{模块}/{功能}/index.vue`（支持 2~3 级路径）
- **任务栏**：通过路由 meta 控制是否加入任务栏（`meta.taskBar`）、是否可关闭（`meta.closable`）、是否缓存（`meta.keepAlive`）

---

## HTTP 请求

- **工具**：Axios，封装在 `utils/request.js`
- **请求方式**：导出 `GET`、`POST`、`PUT`、`PATCH`、`DELETE` 方法
- **Token 处理**：从 `localStorage` 读取 token，自动注入请求头（key: `AccessToken`）
- **响应码处理**：
  - `200`（业务成功）→ resolve
  - `401`（未授权）→ 跳转登录页
  - 其他 → reject 并提示错误信息
- **全局属性**：`this.$http`（axios 实例）、`this.$env`、`this.$msg`、`this.$notify`、`this.$apiUrl`

---

## 状态管理（Vuex）

- **命名空间**：`sys`（核心系统状态）、`view`（视图状态）
- **核心状态（sys/core.js）**：
  - 菜单树、URL-菜单映射
  - 任务栏任务列表（多标签页）
  - 权限列表
  - 系统初始化状态（`init`）
  - KeepAlive 组件缓存名称映射

---

## 业务视图模块

| 目录 | 模块 | 说明 |
|------|------|------|
| `views/ai/` | AI 管理 | 模型、模型参数、训练任务、训练指标、测试任务 |
| `views/core/` | 系统核心 | 用户、角色、菜单、组织、岗位、字典、日志、配置、消息等 |
| `views/iot/` | 物联网 | 设备、产品、协议、驱动、网关、采集、告警、脚本等（28个子模块） |
| `views/gbs/` | GB28181 视频 | 视频设备、通道、实时/历史视频、告警 |
| `views/screen/` | 可视化大屏 | SCADA 组态、数据源 |
| `views/gen/` | 代码生成 | 在线代码生成 |
| `views/quartz/` | 定时任务 | Quartz 任务调度 |
| `views/iotview/` | IoT 视图 | 物联网视图展示 |
| `views/oauth2/` | OAuth2 | 授权管理 |
| `views/open/` | 开放接口 | 开放 API 管理 |
| `views/login/` | 登录 | 登录页面 |
| `views/error/` | 错误页 | 404 等错误页 |
| `views/main/` | 主布局 | 顶栏、侧边栏、任务栏、用户中心等 |

---

## 开发规范

- **视图文件命名**：`views/{模块}/{功能}/index.vue`，组件必须设置 `name` 属性（用于 KeepAlive）
- **JSX 支持**：项目支持 Vue JSX 语法，部分组件使用 `.jsx` 编写
- **路由别名**：`@` → `src/`，`@msn` → `src/views/`
- **权限指令**：`v-auth="'权限标识'"` 控制按钮级权限
- **构建输出**：插件文件输出到 `assets/plugin/` 目录
