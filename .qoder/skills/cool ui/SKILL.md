---
name: cool ui skill
description: 基于 uni-app x 的跨端(安卓/IOS/H5/小程序)UI组件库，提供Cool UI组件库使用指导、功能域组件映射、TailwindCSS 样式规范、多语言支持、PassThrough API 等开发能力，帮助 Agent 正确理解项目架构并高效完成开发任务。设计页面时所有的标签优先使用Cool Unix组件。
---

# Cool Unix 移动端开发技能

## 1. 技能概述

### 1.1 Skill 名称
Cool Unix UI组件库使用技能
### 1.2 目标

本技能用于指导 Agent 在 **Cool Unix** 项目中：

- 正确理解Cool Unix的架构与技术栈；
- 根据功能需求选择合适的 **Cool UI** 组件；
- 正确组合组件，编写页面和业务逻辑；
- 合理使用多语言、TailwindCSS、PassThrough 等能力；
- 避免编造不存在的组件、属性或事件。

### 1.3 适用场景

当用户在 Cool Unix 项目中提出以下需求时，应启用本 Skill：

- 新增 / 修改页面、组件等ui设计布局功能；
- 使用 Cool UI 实现交互、布局、表单、列表、弹窗等；
- 调整多语言、主题样式、Tailwind 配置；
- 改造现有页面结构或交互逻辑。

### 1.4 非适用场景 / 边界

- 与本项目无关的通用前端教学；
- 与后端服务实现细节无关的后端问题；
- 不主动创建新的 `.md` / `.txt` / README 等文档文件；
- 不对项目外部代码仓库进行推断或修改。

---

## 2. 项目与框架背景

### 2.1 项目定位

- Cool Unix 是基于 **uni-app x** 的跨端应用开发脚手架。
- 目标：**一次开发，全端运行**。
- 提供：UI 组件库（Cool UI）、Service 封装、多语言 AI 翻译、Tailwind 深度集成、PassThrough API 等能力。

### 2.2 技术栈

| 类别 | 技术 |
|------|------|
| 核心框架 | uni-app x + Vue 3 + TypeScript |
| 构建工具 | Vite（端口 9900） |
| UI 样式 | Tailwind CSS + Cool UI（70+ 组件） |
| 多语言 | `$t` / `t` 函数 + AI 翻译（`npx cool-i18n create`） |
| 样式穿透 | PassThrough API（`:pt` 属性） |

### 2.3 核心特性

| 特性 | 说明 |
|------|------|
| 跨端兼容 | H5、小程序（微信/支付宝/百度/头条）、App（Android/iOS/鸿蒙） |
| UI 组件库 | Cool UI，70+ 组件 |
| 多语言 | `$t` / `t` 函数 + AI 自动翻译 |
| Tailwind 深度兼容 | 支持 `dark:` 前缀、`!` 强制样式 |
| PassThrough API | 通过 `:pt` 定制组件内部 DOM |

---

## 3. 目录结构与关键文件

### 3.1 根目录关键文件

| 文件 | 说明 |
|------|------|
| `main.ts` | `createSSRApp(App)` 创建应用实例 |
| `App.uvue` | 应用生命周期，`useStore().user.get()` 获取用户信息 |
| `pages.json` | 页面、分包、TabBar 配置，新增页面必须同步维护 |
| `manifest.json` | 多端平台配置 |
| `vite.config.ts` | `cool()` 插件，端口 9900，代理配置 |
| `tailwind.config.ts` | 主题颜色，`darkMode: "class"` |

### 3.2 业务目录

| 目录 | 说明 |
|------|------|
| `pages/index/` | 主 Tab 页：home、template、my |
| `pages/demo/` | 组件演示（basic/form/layout/data/feedback/status/other） |
| `pages/set/` | 设置相关页面 |
| `pages/user/` | 用户中心页面 |
| `pages/template/` | 业务模板（商品、购物车、地址） |

### 3.3 模块目录

| 目录 | 说明 |
|------|------|
| `uni_modules/cool-ui/components/` | Cool UI 所有组件 |
| `uni_modules/cool-canvas` | 画布组件 |
| `uni_modules/cool-svg` | SVG 图标组件 |
| `uni_modules/cool-share` | 分享能力 |
| `uni_modules/cool-vibrate` | 震动能力 |

---

## 4. 开发规范与约束

### 4.1 样式规范

- 优先使用 Tailwind 类进行布局与样式控制；
- 支持 `dark:` 前缀实现暗色模式；
- 支持 `!` 强制覆盖，如 `dark:!bg-surface-900`；

### 4.2 多语言规范

- 所有用户可见文案使用 `t` / `$t`：
  - 模板：`{{ t('你好') }}`
  - 脚本：`import { $t, t } from "@/.cool"`
- 带变量：`$t("欢迎，{name}", { name: "xxx" })`
- 语言包：`/locales/*.json`
- 翻译命令：`npx cool-i18n create`

### 4.3 文件约束

- 不主动创建 `.md` / `.txt` / README 等文档文件；
- 优先编辑已有文件而非新建文件。

### 4.4 组件使用规范
- 设计页面时所有的标签优先使用Cool Unix组件。
---

## 5. 功能域 → 组件映射

### 5.1 表单与校验

**关键词**：表单、输入、校验、登录、注册、编辑、筛选、搜索

**组件**：`cl-form`、`cl-form-item`、`cl-input`、`cl-textarea`、`cl-select`、`cl-checkbox`、`cl-radio`、`cl-switch`、`cl-rate`、`cl-slider`、`cl-input-number`、`cl-input-otp`、`cl-select-date`、`cl-select-time`、`cl-calendar`、`cl-cascader`、`cl-upload`、`cl-button`

**详细说明**：`references/components/form.md`

### 5.2 列表与数据展示

**关键词**：列表、卡片、瀑布流、时间轴、分页、轮播、头像

**组件**：`cl-list`、`cl-list-item`、`cl-list-view`、`cl-waterfall`、`cl-timeline`、`cl-pagination`、`cl-avatar`、`cl-banner`、`cl-read-more`、`cl-rolling-number`、`cl-tree`

**详细说明**：`references/components/data.md`

### 5.3 布局与导航

**关键词**：页面结构、导航、Tab、底部导航、吸顶、悬浮

**组件**：`cl-page`、`cl-topbar`、`cl-tabs`、`cl-tabbar`、`cl-row`、`cl-col`、`cl-sticky`、`cl-float-view`、`cl-footer`、`cl-safe-area`、`cl-collapse`

**详细说明**：`references/components/layout.md`

### 5.4 状态与反馈

**关键词**：提示、弹窗、对话框、加载、进度、骨架、空状态、角标

**组件**：`cl-toast`、`cl-popup`、`cl-confirm`、`cl-action-sheet`、`cl-skeleton`、`cl-loading`、`cl-loadmore`、`cl-progress`、`cl-progress-circle`、`cl-badge`、`cl-noticebar`、`cl-countdown`、`cl-empty`、`cl-back-top`

**详细说明**：`references/components/feedback.md`

### 5.5 交互增强与工具

**关键词**：拖拽、签名、裁剪、水印、二维码、滑动验证、座位选择

**组件**：`cl-draggable`、`cl-sign`、`cl-cropper`、`cl-watermark`、`cl-qrcode`、`cl-slide-verify`、`cl-select-seat`、`cl-marquee`、`cl-filter-bar`、`cl-picker-view`、`cl-keyboard-*`、`cl-index-bar`

**详细说明**：`references/components/utilities.md`

### 5.6 基础组件

**关键词**：文本、按钮、图标、图片、标签

**组件**：`cl-text`、`cl-button`、`cl-icon`、`cl-image`、`cl-tag`

**详细说明**：`references/components/basic.md`

---

## 6. 高频组件速查

> 完整 API 请查看 `references/components/` 或源码 `uni_modules/cool-ui/components/<组件名>/`

### cl-button

| 属性 | 类型 | 默认值 | 说明 |
|------|------|--------|------|
| type | `primary \| success \| warn \| error \| info \| light \| dark` | `primary` | 按钮类型 |
| size | `small \| normal \| large` | `normal` | 按钮尺寸 |
| loading | `boolean` | `false` | 加载状态 |
| disabled | `boolean` | `false` | 禁用状态 |
| icon | `string` | `''` | 图标名称 |
| text | `boolean` | `false` | 文本按钮 |
| rounded | `boolean` | `false` | 圆角按钮 |
| border | `boolean` | `false` | 边框按钮 |
| fluid | `boolean` | `false` | 自适应宽度 |
| pt | `PassThrough` | `{}` | 样式穿透 |

**事件**：`@click` / `@tap`

**PassThrough**：`className`、`icon`、`label`、`loading`

### cl-input

| 属性 | 类型 | 默认值 | 说明 |
|------|------|--------|------|
| modelValue | `string` | `''` | 绑定值 |
| placeholder | `string` | `t('请输入')` | 占位提示 |
| type | `text \| password \| number \| digit \| idcard` | `text` | 输入类型 |
| maxlength | `number` | `140` | 最大长度 |
| disabled | `boolean` | `false` | 禁用 |
| readonly | `boolean` | `null` | 只读 |
| clearable | `boolean` | `false` | 可清除 |
| password | `boolean` | `false` | 密码模式 |
| border | `boolean` | `true` | 显示边框 |
| prefixIcon | `string` | `''` | 前缀图标 |
| suffixIcon | `string` | `''` | 后缀图标 |
| pt | `PassThrough` | `{}` | 样式穿透 |

**事件**：`@input`、`@change`、`@focus`、`@blur`、`@confirm`、`@clear`

**插槽**：`prepend`、`append`

**方法**：`focus()`、`clear()`

**PassThrough**：`className`、`inner`、`prefixIcon`、`suffixIcon`

### cl-form

| 属性 | 类型 | 默认值 | 说明 |
|------|------|--------|------|
| modelValue | `object` | `{}` | 表单数据模型 |
| rules | `Map<string, ClFormRule[]>` | `new Map()` | 校验规则 |
| labelPosition | `top \| left` | `top` | 标签位置 |
| labelWidth | `string` | `'70px'` | 标签宽度 |
| showAsterisk | `boolean` | `true` | 显示必填星号 |
| showMessage | `boolean` | `true` | 显示错误信息 |
| disabled | `boolean` | `false` | 禁用整个表单 |
| scrollToError | `boolean` | `true` | 滚动到第一个错误 |

**方法**：`validate(callback)`、`validateField(prop)`、`clearValidate()`

### cl-form-item

| 属性 | 类型 | 默认值 | 说明 |
|------|------|--------|------|
| prop | `string` | `''` | 字段名 |
| label | `string` | `''` | 标签文案 |
| rules | `ClFormRule[]` | `[]` | 校验规则 |

### cl-popup

| 属性 | 类型 | 默认值 | 说明 |
|------|------|--------|------|
| modelValue | `boolean` | `false` | 是否显示 |
| title | `string` | `null` | 标题 |
| direction | `top \| bottom \| left \| right \| center` | `bottom` | 弹出方向 |
| size | `string \| number` | `''` | 弹窗尺寸 |
| showHeader | `boolean` | `true` | 显示头部 |
| showClose | `boolean` | `true` | 显示关闭按钮 |
| showMask | `boolean` | `true` | 显示遮罩 |
| maskClosable | `boolean` | `true` | 点击遮罩关闭 |
| swipeClose | `boolean` | `true` | 滑动关闭 |

**事件**：`@open`、`@opened`、`@close`、`@closed`、`@maskClose`

**插槽**：`default`、`header`

**方法**：`open()`、`close()`

**PassThrough**：`className`、`inner`、`header`、`container`、`mask`、`draw`

### cl-list-view

| 属性 | 类型 | 默认值 | 说明 |
|------|------|--------|------|
| data | `ClListViewItem[]` | `[]` | 列表数据源 |
| itemHeight | `number` | `50` | 列表项高度 |
| headerHeight | `number` | `32` | 分组头部高度 |
| virtual | `boolean` | `true` | 启用虚拟列表 |
| refresherEnabled | `boolean` | `false` | 启用下拉刷新 |
| refresherThreshold | `number` | `50` | 下拉刷新阈值 |
| showBackTop | `boolean` | `true` | 显示回到顶部 |

**事件**：`@item-tap`、`@pull`、`@scroll`、`@scrolltolower`、`@scrolltoupper`

**插槽**：`item`、`header`、`top`、`bottom`、`refresher`、`index`

**方法**：`stopRefresh()`

---

## 7. PassThrough API

PassThrough 是 Cool UI 的核心特性，允许向组件内部 DOM 透传样式和属性。

### 7.1 基本用法

```html
<cl-button
  :pt="{
    className: '!rounded-2xl',
    icon: { size: 20, className: 'mr-2' },
    label: { color: 'red', className: 'font-bold' }
  }"
>
  提交
</cl-button>
```

### 7.2 常用 pt 结构

| 组件 | pt 支持的内部节点 |
|------|------------------|
| `cl-button` | `className`、`icon`、`label`、`loading` |
| `cl-input` | `className`、`inner`、`prefixIcon`、`suffixIcon` |
| `cl-popup` | `className`、`inner`、`header`、`container`、`mask`、`draw` |
| `cl-list-view` | `className`、`item`、`itemHover`、`list`、`scroller`、`refresher` |

---

## 8. Hooks

| Hook | 说明 |
|------|------|
| `useUi()` | UI 交互（showToast、showConfirm、showActionSheet） |
| `useForm()` | 获取 cl-form 上下文 |
| `useFormItem()` | 获取 cl-form-item 上下文 |
| `usePage()` | 页面上下文 |

### 8.1 useUi 示例

```ts
import { useUi } from "@/uni_modules/cool-ui";

const ui = useUi();

ui.showToast({ message: t("操作成功") });

ui.showConfirm({
  title: t("提示"),
  message: t("确定要删除吗？")
}).then(() => { /* 确认 */ });

ui.showActionSheet({
  options: [t("编辑"), t("删除")]
}).then((index) => { /* 选择 */ });
```

---

## 9. references 目录结构

```
references/
└── components/
    ├── basic.md       # 基础组件：cl-text、cl-button、cl-icon、cl-image、cl-tag
    ├── form.md        # 表单组件：cl-form、cl-input、cl-select 等
    ├── data.md        # 数据展示：cl-list、cl-list-view、cl-waterfall 等
    ├── layout.md      # 布局组件：cl-page、cl-topbar、cl-tabs 等
    ├── feedback.md    # 反馈组件：cl-toast、cl-popup、cl-confirm 等
    └── utilities.md   # 工具组件：cl-draggable、cl-sign、cl-qrcode 等
```

---

## 10. 使用规则

1. 需要某组件完整 API 时，优先查阅 `references/components/<分类>.md`；
2. 若文档不存在或信息不足，查看组件源码 `uni_modules/cool-ui/components/<组件名>/`；
3. 在 `props` 定义中获取属性，在 `emits` 中获取事件，搜索 `<slot` 获取插槽；
4. 不得凭空编造属性或事件名称。

---

## 11. 检查清单

- [ ] 新增页面是否已在 `pages.json` 中配置？
- [ ] TabBar 页路径是否与 `tabBar.list.pagePath` 一致？
- [ ] 文案是否使用 `t` / `$t`？
- [ ] 是否优先使用 Cool UI 组件？
- [ ] 样式是否使用 Tailwind 类？
- [ ] 组件属性/事件是否查阅过源码或 reference？
- [ ] 是否避免创建新的 `.md` / `.txt` 文档文件？
