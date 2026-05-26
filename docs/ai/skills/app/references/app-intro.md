# 移动端项目介绍 (app)

## 基本信息

| 属性 | 说明 |
|------|------|
| **项目目录** | `/app` |
| **项目名称** | cool-unix |
| **版本** | 8.1.0 |
| **开发服务器端口** | 9900 |
| **包管理工具** | pnpm |
| **后端代理** | 通过 `config/proxy.ts` 配置，代理规则自动添加 `/{key}/` 前缀 |

---

## 技术栈

| 分类 | 技术 | 版本 |
|------|------|------|
| **跨端框架** | uni-app x | 最新版 |
| **开发语言** | UTS(跨平台强类型语言，类似 TypeScript) + Vue 3.x | - |
| **核心框架** | @cool-vue/unix (Cool Unix) | 1.1.0-beta.6 |
| **UI 组件库** | Cool UI(`uni_modules/cool-ui`，基于 uni-app x) | - |
| **样式方案** | TailwindCSS | 3.4.17 |
| **构建工具** | Vite + @dcloudio/vite-plugin-uni | 6.3.x |
| **国际化** | 内置多语言(zh-cn / en / ja / ko / es / fr / zh-tw) | - |
| **微信 SDK** | weixin-js-sdk | 1.6.x |
| **触摸模拟** | hammer-touchemulator(开发调试用) | 0.0.2 |

---

## 支持平台

- Android App
- iOS App
- H5(Web)
- 微信小程序(及其他小程序平台)

---

## 项目结构

```
app/
├── .cool/              # Cool Unix 核心框架模块(勿随意修改)
│   ├── types/          # 全局共享类型(UserInfo, UserCategory, Page<T> 等)
│   ├── animation/      # 动画
│   ├── ctx/            # 上下文
│   ├── hooks/          # 组合式函数(pager、refs、cache、long-press 等)
│   ├── icons/          # 图标
│   ├── locale/         # 国际化
│   ├── page/           # 页面基础
│   ├── router/         # 路由
│   ├── scroller/       # 滚动
│   ├── service/        # HTTP 请求封装
│   ├── store/          # 状态管理(user、dict)
│   ├── theme/          # 主题
│   ├── upload/         # 文件上传
│   └── utils/          # 工具函数
├── components/         # 公共页面组件
├── config/             # 应用配置
│   ├── index.ts        # 主配置入口
│   ├── dev.ts          # 开发环境配置
│   ├── prod.ts         # 生产环境配置
│   └── proxy.ts        # 代理配置
├── locales/            # 国际化语言包
├── pages/              # 业务页面（各功能下含 {模块}.ts）
│   ├── index/          # 首页
│   ├── user/           # 用户相关(登录、注册、个人资料等)
│   ├── set/            # 设置
│   └── template/       # 页面模板
├── plugins/            # 插件(emitter、locale、router、touch)
├── static/             # 静态资源
├── uni_modules/        # uni-app 模块
│   ├── cool-ui/        # Cool UI 组件库
│   ├── cool-canvas/    # Canvas 组件
│   ├── cool-open-web/  # 打开网页
│   ├── cool-share/     # 分享
│   ├── cool-svg/       # SVG 组件
│   ├── cool-unix/      # Cool Unix 模块
│   └── cool-vibrate/   # 震动
├── pages.json          # 页面路由注册(所有页面必须在此注册)
└── manifest.json       # 应用配置(AppID、平台权限等)
```

---

## 核心模块说明(.cool/)

### HTTP 请求(service/index.ts)
- **请求函数**:`request(options: RequestOptions): Promise<any>`
- **Token 机制**:自动从 store 读取 token，注入 `Authorization` 请求头
- **Token 刷新**:token 过期自动刷新，刷新期间请求进入队列等待
- **响应码处理**:
  - `code: 1000` → 成功，resolve data
  - `401` → 退出登录，跳转登录页
  - `502` → 服务异常
  - `404` → 接口不存在
- **语言头**:自动注入 `language` 请求头(国际化)

### 状态管理(store/)
- **user**:用户信息单例(`User` 类)
  - `user.token`:当前 token
  - `user.info`:用户信息(响应式 ref)
  - `user.setToken()`:设置并持久化 token(含 refreshToken)
  - `user.refreshToken()`:刷新 token(调用 `/app/user/login/refreshToken`)
  - `user.logout()`:退出登录并跳转登录页
  - `user.get()`:从服务端拉取用户信息(接口:`/app/user/info/person`)
  - `user.update()`:更新用户信息(接口:`/app/user/info/updatePerson`)
- **dict**:数据字典缓存

### 路由(router/)
- 使用 uni-app x 原生路由，配合 Cool Unix 的 `router` 封装
- `router.login()` 跳转登录页

### AppSecurityUtil

后端 `AppSecurityUtil` 为移动端用户登录安全工具类，提供功能：

| 方法 | 说明 |
|------|------|
| `login(SocialUser)` | 登录，自动加 `APP:` 前缀隔离管理端ID |
| `getLoginId()` | 获取当前登录用户ID（已去前缀） |
| `isLogin()` | 判断当前是否已登录 |
| `logout()` | 登出 |
| `getTokenValue()` | 获取当前Token值 |
| `getTokenTimeout()` | 获取Token过期时间 |
| `getTokenName()` | 获取Token名称（用于前端请求头） |

### Hooks(hooks/)
| Hook | 说明 |
|------|------|
| `pager.ts` | 分页数据加载逻辑 |
| `refs.ts` | 组件引用管理 |
| `cache.ts` | 缓存 Hook |
| `long-press.ts` | 长按手势 |
| `parent.ts` | 父组件访问 |
| `wx.ts` | 微信相关 Hook |

---

## 页面开发规范

- **文件后缀**:页面使用 `.uvue`，脚本使用 `.ts`
- **页面注册**:所有页面必须在 `pages.json` 中注册，否则无法访问
- **滚动容器**:可滚动页面需使用 `<scroll-view style="flex:1">` 等滚动容器
- **平台条件编译**:平台专用代码使用 `#ifdef APP / #ifdef H5` 等条件编译
- **UI 组件**:优先使用 Cool UI 组件(`uni_modules/cool-ui`)，样式使用 TailwindCSS
- **类型严格**:UTS 强类型，禁止隐式类型转换，所有参数需明确类型

## 模块文件规范

- **全局共享类型**放在 `app/.cool/types/` 下（如 `UserInfo`、`UserCategory`、`Page<T>`）
- **功能模块文件**合并为一个 `{模块}.ts`，放在对应页面目录下
  - 如 `app/pages/user/user.ts`（同时包含 `UserApi` 地址常量和类型定义）
  - `UserApi` 对象导出所有接口地址，页面中通过 `request({ url: UserApi.xxx, ... })` 调用
  - 类型定义直接导出，支持按需导入
- **导入路径**统一使用 `@/` 别名，禁止使用 `../` 相对路径
  - 导入类型：`import type { LoginForm } from "@/pages/user/user"`
  - 导入地址：`import { UserApi } from "@/pages/user/user"`

---

## 接口规范

- **移动端接口前缀**:`/m/`(对应后端 `com.iteaj.iboot.module.app.controller`)
- **用户信息接口**:`GET /m/user/info/person`
- **更新用户接口**:`POST /m/user/info/updatePerson`
- **Token 刷新接口**:`POST /m/user/login/refreshToken`

---

## 国际化

- 语言包位于 `locales/` 目录(zh-cn、en、ja、ko、es、fr、zh-tw)
- 默认语言通过 `config/index.ts` 的 `locale` 字段配置(当前:`zh-tw`)
- 翻译函数:从 `.cool/locale` 导入 `t(key)` 使用
