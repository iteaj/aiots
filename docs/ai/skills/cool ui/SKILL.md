---
name: cool ui
description: Cool UI组件库使用技能。凡涉及移动端页面(.uvue)、uni-app x界面、App前端UI、小程序UI开发时，必须优先调用此技能。提供70+组件：cl-page/cl-button/cl-input/cl-form/cl-list/cl-list-view/cl-popup/cl-dialog/cl-tabs/cl-tabbar/cl-topbar/cl-footer/cl-toast/cl-icon/cl-text/cl-image/cl-tag/cl-select/cl-checkbox/cl-radio/cl-switch/cl-upload/cl-skeleton/cl-loading/cl-empty/cl-badge/cl-sticky/cl-collapse/cl-row/cl-col等。涵盖：页面布局、表单输入、列表数据展示、弹窗对话框、导航Tab、底部操作栏、状态反馈(loading/toast/empty/skeleton)、TailwindCSS深色模式、PassThrough样式穿透、多语言$t。触发条件：用户要写/改移动端页面、设计app界面、实现表单/列表/弹窗、调整移动端样式布局、选用UI组件。与app技能配合：本技能管UI组件选型和页面布局，app技能管页面逻辑和数据对接。
---

# Cool Unix 移动端开发技能

## 组件总表

### 基础组件

| 组件名 | 用途 |
|--------|------|
| `cl-text` | 文本显示，支持主题色、大小、粗细、行数截断 |
| `cl-button` | 按钮，支持类型/尺寸/加载/禁用/图标/圆角/文字按钮 |
| `cl-icon` | 图标，基于 cool-svg 图标库 |
| `cl-image` | 图片展示，支持懒加载/占位图/圆角/预览模式 |
| `cl-tag` | 标签，用于标记/分类/状态展示 |

### 表单与输入组件

| 组件名 | 用途 |
|--------|------|
| `cl-form` | 表单容器，统一管理校验/布局/提交 |
| `cl-form-item` | 表单子项，绑定字段校验规则 |
| `cl-input` | 文本输入框，支持多种类型/前缀后缀图标 |
| `cl-textarea` | 多行文本输入 |
| `cl-select` | 下拉选择器 |
| `cl-select-trigger` | 选择器触发器，用于自定义选择弹出 |
| `cl-select-date` | 日期选择器 |
| `cl-select-time` | 时间选择器 |
| `cl-checkbox` | 多选框 |
| `cl-radio` | 单选框 |
| `cl-switch` | 开关切换 |
| `cl-slider` | 滑动选择器 |
| `cl-rate` | 星级评分 |
| `cl-input-number` | 数字步进器，支持增减按钮 |
| `cl-input-otp` | OTP 验证码输入 |
| `cl-calendar` | 日历面板 |
| `cl-calendar-select` | 日历选择器 |
| `cl-cascader` | 级联选择器 |
| `cl-upload` | 文件/图片上传 |
| `cl-picker-view` | 滚轮选择器 |
| `cl-filter-bar` | 筛选栏容器 |
| `cl-filter-item` | 筛选项 |

### 数据展示组件

| 组件名 | 用途 |
|--------|------|
| `cl-list` | 静态列表容器 |
| `cl-list-item` | 列表项 |
| `cl-list-view` | 高性能虚拟列表，支持下拉刷新/上拉加载 |
| `cl-waterfall` | 瀑布流布局 |
| `cl-timeline` | 时间轴容器 |
| `cl-timeline-item` | 时间轴项 |
| `cl-pagination` | 分页器 |
| `cl-avatar` | 头像，支持图片/文字/图标 |
| `cl-banner` | 横幅/轮播广告 |
| `cl-read-more` | 长文本展开/收起 |
| `cl-rolling-number` | 数字滚动动画 |
| `cl-tree` | 树形控件容器 |
| `cl-tree-item` | 树形节点 |
| `cl-marquee` | 跑马灯文字滚动 |

### 布局与导航组件

| 组件名 | 用途 |
|--------|------|
| `cl-page` | 页面容器，内置滚动/下拉刷新/背景色 |
| `cl-topbar` | 顶部导航栏，支持标题/返回/右侧操作 |
| `cl-tabs` | 标签页切换 |
| `cl-tabbar` | 底部导航栏，支持图标/文字/角标 |
| `cl-row` | 弹性布局行（基于 flex） |
| `cl-col` | 弹性布局列（基于 flex） |
| `cl-sticky` | 吸顶/吸底容器 |
| `cl-float-view` | 浮动悬浮按钮/视图 |
| `cl-footer` | 底部操作栏，常用于按钮/金额汇总 |
| `cl-safe-area` | 安全区域适配（刘海屏/底部指示条） |
| `cl-collapse` | 折叠面板 |

### 状态与反馈组件

| 组件名 | 用途 |
|--------|------|
| `cl-toast` | 轻提示，支持成功/失败/加载/文字 |
| `cl-popup` | 弹出层，支持上下左右中五个方向 |
| `cl-confirm` | 确认对话框 |
| `cl-action-sheet` | 底部操作菜单 |
| `cl-skeleton` | 骨架屏，用于内容加载占位 |
| `cl-loading` | 加载中指示器 |
| `cl-loadmore` | 上拉加载更多状态提示 |
| `cl-progress` | 进度条 |
| `cl-progress-circle` | 圆形进度环 |
| `cl-badge` | 角标/徽章，用于未读提示 |
| `cl-noticebar` | 公告栏/通知条 |
| `cl-countdown` | 倒计时 |
| `cl-empty` | 空状态占位图 |
| `cl-back-top` | 回到顶部按钮 |

### 交互增强与工具组件

| 组件名 | 用途 |
|--------|------|
| `cl-draggable` | 拖拽排序容器 |
| `cl-sign` | 手写签名板 |
| `cl-cropper` | 图片裁剪 |
| `cl-watermark` | 水印覆盖层 |
| `cl-qrcode` | 二维码生成/展示 |
| `cl-slide-verify` | 滑动验证码 |
| `cl-select-seat` | 座位选择器（影院/会议室等场景） |
| `cl-keyboard-car` | 车牌号键盘 |
| `cl-keyboard-number` | 数字键盘 |
| `cl-keyboard-password` | 密码键盘 |
| `cl-index-bar` | 索引导航栏（通讯录字母导航） |

> 各组件的属性、事件、方法、插槽等详细配置参见源码：`app/uni_modules/cool-ui/components/<组件名>/`

---

## 使用规则

1. 需要某组件完整 API 时，优先查阅 `references/components/<分类>.md`；
2. 若文档不存在或信息不足，查看组件源码 `app/uni_modules/cool-ui/components/<组件名>/`；
3. 在 `props` 定义中获取属性，在 `emits` 中获取事件，搜索 `<slot` 获取插槽；
4. 不得凭空编造属性或事件名称。

---

## 检查清单

- [ ] 新增页面是否已在 `pages.json` 中配置？
- [ ] TabBar 页路径是否与 `tabBar.list.pagePath` 一致？
- [ ] 文案是否使用 `t` / `$t`？
- [ ] 是否优先使用 Cool UI 组件？
- [ ] 样式是否使用 Tailwind 类？
- [ ] 组件属性/事件是否查阅过源码或 reference？
- [ ] 是否避免创建新的 `.md` / `.txt` 文档文件？
