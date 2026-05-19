# 基础组件（Basic）

本文档包含基础组件的详细说明：`cl-text`、`cl-button`、`cl-icon`、`cl-image`、`cl-tag`。

---

## cl-text

### 概述

文本组件，支持多种格式化类型（手机号脱敏、姓名脱敏、金额格式化、银行卡脱敏、邮箱脱敏）、省略号、可选择等能力。

### 属性

| 属性名 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| pt | `Object` | `{}` | 样式穿透 |
| value | `string \| number \| null` | `null` | 显示的值 |
| color | `string` | `''` | 文本颜色，支持预设值：`primary`、`success`、`warn`、`error`、`info`、`dark`、`light`、`disabled` |
| size | `number` | `14` | 字体大小 |
| type | `string` | `'default'` | 文本类型：`default`、`phone`（手机号脱敏）、`name`（姓名脱敏）、`amount`（金额）、`card`（银行卡脱敏）、`email`（邮箱脱敏） |
| mask | `boolean` | `false` | 是否开启脱敏 |
| currency | `string` | `'¥'` | 金额货币符号 |
| precision | `number` | `2` | 金额小数位数 |
| maskStart | `number` | `3` | 脱敏起始位置 |
| maskEnd | `number` | `4` | 脱敏结束位置 |
| maskChar | `string` | `'*'` | 脱敏替换字符 |
| ellipsis | `boolean` | `false` | 是否省略号 |
| lines | `number` | `1` | 最大行数，仅在 ellipsis 时生效 |
| selectable | `boolean` | `false` | 是否可选择 |
| space | `string` | `''` | 显示连续空格：`ensp`、`emsp`、`nbsp` |
| decode | `boolean` | `false` | 是否解码 |
| preWrap | `boolean` | `false` | 是否保留单词 |

### 插槽

| 插槽名 | 说明 |
|--------|------|
| default | 默认插槽，自定义内容 |

### 使用示例

```html
<!-- 基础用法 -->
<cl-text>{{ t('你好') }}</cl-text>

<!-- 设置颜色和大小 -->
<cl-text color="primary" :size="16">{{ t('主要文本') }}</cl-text>

<!-- 省略号 -->
<cl-text ellipsis :lines="2">{{ longText }}</cl-text>

<!-- 金额格式化 -->
<cl-text type="amount" :value="1234.56"></cl-text>

<!-- 手机号脱敏 -->
<cl-text type="phone" :value="13812345678" mask></cl-text>
```

---

## cl-button

### 概述

按钮组件，支持多种类型、尺寸、加载状态、图标、开放能力等。

### 属性

| 属性名 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| pt | `Object` | `{}` | 样式穿透 |
| type | `string` | `'primary'` | 按钮类型：`primary`、`success`、`warn`、`error`、`info`、`light`、`dark` |
| color | `string` | `''` | 字体、图标颜色 |
| icon | `string` | `''` | 图标名称 |
| text | `boolean` | `false` | 文本按钮 |
| rounded | `boolean` | `false` | 圆角按钮 |
| border | `boolean` | `false` | 边框按钮 |
| loading | `boolean` | `false` | 加载状态 |
| disabled | `boolean` | `false` | 禁用状态 |
| size | `string` | `'normal'` | 按钮尺寸：`small`、`normal`、`large` |
| fluid | `boolean` | `false` | 是否为 flex-1 布局 |
| hoverClass | `string` | `''` | 按钮点击态样式类 |
| hoverStopPropagation | `boolean` | `false` | 是否阻止点击态冒泡 |
| hoverStartTime | `number` | `20` | 按住后多久出现点击态 |
| hoverStayTime | `number` | `70` | 手指松开后点击态保留时间 |
| formType | `string` | `''` | 表单提交类型：`submit`、`reset` |
| openType | `string` | `''` | 开放能力类型（微信小程序） |

### 事件

| 事件名 | 说明 |
|--------|------|
| click | 点击事件 |
| tap | 点击事件 |
| getuserinfo | 获取用户信息 |
| contact | 客服消息 |
| getphonenumber | 获取手机号 |
| error | 错误事件 |
| opensetting | 打开设置 |
| launchapp | 打开 APP |
| chooseavatar | 选择头像 |
| chooseaddress | 选择收货地址 |
| chooseinvoicetitle | 选择发票抬头 |
| addgroupapp | 添加群应用 |
| subscribe | 订阅消息 |
| login | 登录 |
| getrealtimephonenumber | 获取实时手机号 |
| agreeprivacyauthorization | 同意隐私授权 |

### 插槽

| 插槽名 | 说明 |
|--------|------|
| default | 按钮文案 |
| content | 自定义内容（图标按钮时使用） |

### PassThrough

| 节点 | 说明 |
|------|------|
| className | 根元素样式 |
| icon | 图标（支持 ClIconProps） |
| label | 文本（支持 ClTextProps） |
| loading | 加载图标（支持 ClLoadingProps） |

### 使用示例

```html
<!-- 基础按钮 -->
<cl-button>{{ t('提交') }}</cl-button>

<!-- 不同类型 -->
<cl-button type="success">{{ t('成功') }}</cl-button>
<cl-button type="error">{{ t('错误') }}</cl-button>
<cl-button type="warn">{{ t('警告') }}</cl-button>

<!-- 不同尺寸 -->
<cl-button size="small">{{ t('小按钮') }}</cl-button>
<cl-button size="large">{{ t('大按钮') }}</cl-button>

<!-- 加载状态 -->
<cl-button :loading="true">{{ t('加载中') }}</cl-button>

<!-- 图标按钮 -->
<cl-button icon="search-line">{{ t('搜索') }}</cl-button>

<!-- 文本按钮 -->
<cl-button text>{{ t('文本按钮') }}</cl-button>

<!-- 圆角按钮 -->
<cl-button rounded>{{ t('圆角按钮') }}</cl-button>

<!-- 边框按钮 -->
<cl-button border>{{ t('边框按钮') }}</cl-button>

<!-- PassThrough 样式穿透 -->
<cl-button
  :pt="{
    className: '!rounded-2xl',
    icon: { size: 20, className: 'mr-2' },
    label: { color: 'red', className: 'font-bold' }
  }"
>
  {{ t('自定义样式') }}
</cl-button>
```

---

## cl-icon

### 概述

图标组件，基于 RemixIcon 字体图标。

### 属性

| 属性名 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| pt | `Object` | `{}` | 样式穿透 |
| name | `string` | `''` | 图标名称 |
| size | `number` | `16` | 图标大小 |
| height | `number` | `null` | 图标高度 |
| width | `number` | `null` | 图标宽度 |
| color | `string` | `''` | 图标颜色，支持预设值：`primary`、`success`、`warn`、`error`、`info`、`dark`、`light`、`disabled` |

### PassThrough

| 节点 | 说明 |
|------|------|
| className | 根元素样式 |

### 使用示例

```html
<!-- 基础用法 -->
<cl-icon name="search-line"></cl-icon>

<!-- 设置大小和颜色 -->
<cl-icon name="home-line" :size="24" color="primary"></cl-icon>

<!-- 在按钮中使用 -->
<cl-button icon="search-line">{{ t('搜索') }}</cl-button>
```

---

## cl-image

### 概述

图片组件，支持懒加载、预览、加载状态、错误状态等。

### 属性

| 属性名 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| pt | `Object` | `{}` | 样式穿透 |
| src | `string` | `''` | 图片源 |
| mode | `string` | `'aspectFill'` | 图片裁剪、缩放模式 |
| border | `boolean` | `false` | 是否显示边框 |
| preview | `boolean` | `false` | 是否预览 |
| previewList | `string[]` | `[]` | 预览图片列表 |
| height | `string \| number` | `60` | 图片高度 |
| width | `string \| number` | `60` | 图片宽度 |
| showLoading | `boolean` | `true` | 是否显示加载状态 |
| lazyLoad | `boolean` | `false` | 是否懒加载 |
| fadeShow | `boolean` | `false` | 图片显示动画效果 |
| webp | `boolean` | `false` | 是否解码 webp 格式 |
| showMenuByLongpress | `boolean` | `false` | 是否长按显示菜单 |

### 事件

| 事件名 | 说明 |
|--------|------|
| load | 图片加载成功 |
| error | 图片加载失败 |

### 插槽

| 插槽名 | 说明 |
|--------|------|
| default | 默认插槽 |
| error | 错误状态内容 |
| loading | 加载状态内容 |

### PassThrough

| 节点 | 说明 |
|------|------|
| className | 根元素样式 |
| inner | 图片元素 |
| error | 错误状态（支持 ClIconProps） |
| loading | 加载状态 |

### 使用示例

```html
<!-- 基础用法 -->
<cl-image src="/static/logo.png" :width="100" :height="100"></cl-image>

<!-- 懒加载 -->
<cl-image src="/static/logo.png" lazy-load></cl-image>

<!-- 预览图片 -->
<cl-image src="/static/logo.png" preview></cl-image>

<!-- 自定义错误状态 -->
<cl-image src="/static/error.png">
  <template #error>
    <cl-icon name="image-line" :size="40"></cl-icon>
  </template>
</cl-image>
```

---

## cl-tag

### 概述

标签组件，支持多种类型、图标、可关闭等。

### 属性

| 属性名 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| pt | `Object` | `{}` | 样式穿透 |
| type | `string` | `'primary'` | 类型：`primary`、`success`、`warn`、`error`、`info` |
| icon | `string` | `''` | 图标名称 |
| rounded | `boolean` | `false` | 圆角 |
| closable | `boolean` | `false` | 可关闭 |
| plain | `boolean` | `false` | 镂空 |

### 事件

| 事件名 | 说明 |
|--------|------|
| close | 关闭事件 |

### 插槽

| 插槽名 | 说明 |
|--------|------|
| default | 标签内容 |

### PassThrough

| 节点 | 说明 |
|------|------|
| className | 根元素样式 |
| text | 文本（支持 ClTextProps） |

### 使用示例

```html
<!-- 基础用法 -->
<cl-tag>{{ t('标签') }}</cl-tag>

<!-- 不同类型 -->
<cl-tag type="success">{{ t('成功') }}</cl-tag>
<cl-tag type="error">{{ t('错误') }}</cl-tag>
<cl-tag type="warn">{{ t('警告') }}</cl-tag>

<!-- 镂空 -->
<cl-tag plain>{{ t('镂空标签') }}</cl-tag>

<!-- 圆角 -->
<cl-tag rounded>{{ t('圆角标签') }}</cl-tag>

<!-- 可关闭 -->
<cl-tag closable @close="onClose">{{ t('可关闭') }}</cl-tag>

<!-- 带图标 -->
<cl-tag icon="star-line">{{ t('带图标') }}</cl-tag>
```
