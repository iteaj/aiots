# 反馈组件（Feedback）

本文档包含反馈组件的详细说明：`cl-toast`、`cl-popup`、`cl-confirm`、`cl-action-sheet`、`cl-loading`、`cl-skeleton`、`cl-loadmore`、`cl-progress`、`cl-progress-circle`、`cl-badge`、`cl-noticebar`、`cl-countdown`、`cl-empty`、`cl-back-top`。

---

## cl-toast

### 概述

轻提示组件，通过 `useUi().showToast()` 调用。

### 使用方式

```ts
import { useUi } from "@/uni_modules/cool-ui";

const ui = useUi();

// 基础用法
ui.showToast({
  message: t("操作成功")
});

// 不同类型
ui.showToast({
  message: t("操作失败"),
  type: "error"
});

// 加载提示
ui.showToast({
  message: t("加载中..."),
  type: "loading"
});
```

### ToastOptions 参数

| 属性名 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| message | `string` | - | 提示内容 |
| type | `string` | `'success'` | 类型：`success`、`error`、`loading`、`none` |
| duration | `number` | `2000` | 展示时长（毫秒） |
| position | `string` | `'center'` | 位置：`top`、`center`、`bottom` |
| icon | `string` | - | 图标名称 |

---

## cl-popup

### 概述

弹窗组件，支持多方向弹出、滑动关闭等。

### 属性

| 属性名 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| pt | `Object` | `{}` | 样式穿透 |
| modelValue | `boolean` | `false` | 是否显示 |
| title | `string` | `null` | 标题 |
| direction | `string` | `'bottom'` | 弹出方向：`top`、`bottom`、`left`、`right`、`center` |
| size | `string \| number` | `''` | 弹窗尺寸 |
| showHeader | `boolean` | `true` | 是否显示头部 |
| showClose | `boolean` | `true` | 是否显示关闭按钮 |
| showMask | `boolean` | `true` | 是否显示遮罩层 |
| maskClosable | `boolean` | `true` | 是否点击遮罩层关闭 |
| swipeClose | `boolean` | `true` | 是否开启拖拽关闭 |
| swipeCloseThreshold | `number` | `150` | 拖拽关闭的阈值 |
| pointerEvents | `string` | `'auto'` | 触摸事件响应方式 |
| keepAlive | `boolean` | `false` | 是否开启缓存 |
| enablePortal | `boolean` | `true` | 是否启用 portal |

### 事件

| 事件名 | 说明 |
|--------|------|
| update:modelValue | 显示状态改变 |
| open | 打开动画开始 |
| opened | 打开动画结束 |
| close | 关闭动画开始 |
| closed | 关闭动画结束 |
| maskClose | 点击遮罩层 |

### 插槽

| 插槽名 | 说明 |
|--------|------|
| default | 弹窗内容 |
| header | 头部内容 |

### 方法

| 方法名 | 说明 |
|--------|------|
| open | 打开弹窗 |
| close | 关闭弹窗 |

### PassThrough

| 节点 | 说明 |
|------|------|
| className | 根元素样式 |
| inner | 内部容器 |
| header | 头部区域 |
| container | 内容容器 |
| mask | 遮罩层 |
| draw | 拖动条 |

### 使用示例

```html
<script setup lang="ts">
const visible = ref(false);

function open() {
  visible.value = true;
}

function close() {
  visible.value = false;
}
</script>

<template>
  <cl-button @tap="open">{{ t('打开弹窗') }}</cl-button>
  
  <cl-popup v-model="visible" :title="t('弹窗标题')">
    <view class="p-4">
      <cl-text>{{ t('弹窗内容') }}</cl-text>
    </view>
  </cl-popup>
</template>
```

---

## cl-confirm

### 概述

确认框组件，通过 `useUi().showConfirm()` 调用。

### 使用方式

```ts
import { useUi } from "@/uni_modules/cool-ui";

const ui = useUi();

// 基础用法
ui.showConfirm({
  title: t("提示"),
  message: t("确定要删除吗？")
}).then(() => {
  // 确认操作
  console.log("已确认");
}).catch(() => {
  // 取消操作
  console.log("已取消");
});

// 自定义按钮
ui.showConfirm({
  title: t("提示"),
  message: t("确定要删除吗？"),
  confirmText: t("删除"),
  confirmColor: "error"
}).then(() => {
  // 删除操作
});
```

### ConfirmOptions 参数

| 属性名 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| title | `string` | `t('提示')` | 标题 |
| message | `string` | - | 内容 |
| confirmText | `string` | `t('确定')` | 确认按钮文本 |
| cancelText | `string` | `t('取消')` | 取消按钮文本 |
| confirmColor | `string` | `'primary'` | 确认按钮颜色 |
| showCancelButton | `boolean` | `true` | 是否显示取消按钮 |

---

## cl-action-sheet

### 概述

操作菜单组件，通过 `useUi().showActionSheet()` 调用。

### 使用方式

```ts
import { useUi } from "@/uni_modules/cool-ui";

const ui = useUi();

// 基础用法
ui.showActionSheet({
  options: [t("编辑"), t("删除"), t("取消")]
}).then((index) => {
  console.log("选择了第", index, "项");
  if (index === 0) {
    // 编辑
  } else if (index === 1) {
    // 删除
  }
});

// 带取消按钮
ui.showActionSheet({
  options: [t("拍照"), t("从相册选择")],
  cancelButtonText: t("取消")
});
```

### ActionSheetOptions 参数

| 属性名 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| options | `string[]` | `[]` | 选项列表 |
| title | `string` | - | 标题 |
| cancelButtonText | `string` | `t('取消')` | 取消按钮文本 |
| destructiveButtons | `number[]` | `[]` | 危险按钮索引列表 |

---

## cl-loading

### 概述

加载动画组件。

### 属性

| 属性名 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| pt | `Object` | `{}` | 样式穿透 |
| loading | `boolean` | `true` | 是否加载中 |
| size | `number` | `20` | 加载图标大小 |
| color | `string` | `'primary'` | 加载图标颜色 |
| text | `string` | `''` | 加载文案 |

### PassThrough

| 节点 | 说明 |
|------|------|
| className | 根元素样式 |

### 使用示例

```html
<cl-loading :loading="true"></cl-loading>

<cl-loading :loading="true" :text="t('加载中...')"></cl-loading>
```

---

## cl-skeleton

### 概述

骨架屏组件。

### 属性

| 属性名 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| pt | `Object` | `{}` | 样式穿透 |
| loading | `boolean` | `true` | 是否加载中 |
| rows | `number` | `3` | 行数 |
| avatar | `boolean` | `false` | 是否显示头像 |
| avatarSize | `number` | `40` | 头像大小 |
| avatarShape | `string` | `'round'` | 头像形状：`round`、`square` |
| title | `boolean` | `true` | 是否显示标题 |
| titleWidth | `string` | `'40%'` | 标题宽度 |
| rowWidth | `string \| string[]` | `'100%'` | 行宽度 |
| animated | `boolean` | `true` | 是否开启动画 |

### 插槽

| 插槽名 | 说明 |
|--------|------|
| default | 加载完成后的内容 |
| template | 自定义骨架模板 |

### 使用示例

```html
<cl-skeleton :loading="isLoading">
  <!-- 加载完成后的内容 -->
  <cl-list :data="list"></cl-list>
</cl-skeleton>

<!-- 自定义骨架模板 -->
<cl-skeleton :loading="isLoading">
  <template #template>
    <view class="flex">
      <cl-skeleton-item variant="avatar"></cl-skeleton-item>
      <view class="flex-1">
        <cl-skeleton-item variant="text"></cl-skeleton-item>
        <cl-skeleton-item variant="text" width="60%"></cl-skeleton-item>
      </view>
    </view>
  </template>
  
  <cl-list :data="list"></cl-list>
</cl-skeleton>
```

---

## cl-loadmore

### 概述

加载更多组件。

### 属性

| 属性名 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| pt | `Object` | `{}` | 样式穿透 |
| loading | `boolean` | `false` | 是否加载中 |
| finished | `boolean` | `false` | 是否加载完成 |
| loadingText | `string` | `t('加载中...')` | 加载中文案 |
| finishedText | `string` | `t('没有更多了')` | 加载完成文案 |
| errorText | `string` | `t('加载失败')` | 加载失败文案 |
| error | `boolean` | `false` | 是否加载失败 |

### 事件

| 事件名 | 说明 |
|--------|------|
| load | 触发加载更多 |
| retry | 重试加载 |

### 使用示例

```html
<cl-loadmore :loading="isLoading" :finished="isFinished" @load="onLoadMore"></cl-loadmore>
```

---

## cl-progress

### 概述

进度条组件。

### 属性

| 属性名 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| pt | `Object` | `{}` | 样式穿透 |
| modelValue | `number` | `0` | 当前进度（0-100） |
| strokeWidth | `number` | `8` | 进度条高度 |
| color | `string` | - | 进度条颜色 |
| trackColor | `string` | - | 轨道颜色 |
| showText | `boolean` | `true` | 是否显示进度文字 |
| striped | `boolean` | `false` | 是否显示条纹 |
| stripedFlow | `boolean` | `false` | 条纹是否流动 |

### 使用示例

```html
<cl-progress :model-value="50"></cl-progress>

<cl-progress :model-value="80" striped striped-flow></cl-progress>
```

---

## cl-progress-circle

### 概述

圆形进度条组件。

### 属性

| 属性名 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| pt | `Object` | `{}` | 样式穿透 |
| modelValue | `number` | `0` | 当前进度（0-100） |
| size | `number` | `100` | 圆形进度条大小 |
| strokeWidth | `number` | `8` | 进度条宽度 |
| color | `string` | - | 进度条颜色 |
| trackColor | `string` | - | 轨道颜色 |
| showText | `boolean` | `true` | 是否显示进度文字 |

### 插槽

| 插槽名 | 说明 |
|--------|------|
| default | 自定义内容 |

### 使用示例

```html
<cl-progress-circle :model-value="75" :size="120"></cl-progress-circle>

<!-- 自定义内容 -->
<cl-progress-circle :model-value="downloadProgress">
  <cl-text>{{ downloadProgress }}%</cl-text>
</cl-progress-circle>
```

---

## cl-badge

### 概述

角标组件。

### 属性

| 属性名 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| pt | `Object` | `{}` | 样式穿透 |
| value | `string \| number` | - | 角标内容 |
| max | `number` | `99` | 最大值（数值时生效） |
| dot | `boolean` | `false` | 是否显示为小圆点 |
| hidden | `boolean` | `false` | 是否隐藏 |
| type | `string` | `'error'` | 类型：`primary`、`success`、`warn`、`error`、`info` |
| offset | `[number, number]` | `[0, 0]` | 偏移量 |

### 插槽

| 插槽名 | 说明 |
|--------|------|
| default | 徽标内容 |

### 使用示例

```html
<cl-badge :value="5">
  <cl-icon name="message-line" :size="24"></cl-icon>
</cl-badge>

<cl-badge :value="100" :max="99">
  <cl-icon name="bell-line" :size="24"></cl-icon>
</cl-badge>

<cl-badge dot>
  <cl-icon name="notification-line" :size="24"></cl-icon>
</cl-badge>
```

---

## cl-noticebar

### 概述

通知栏组件。

### 属性

| 属性名 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| pt | `Object` | `{}` | 样式穿透 |
| text | `string` | `''` | 通知文本内容 |
| mode | `string` | `''` | 模式：`closeable`（可关闭）、`link`（可点击） |
| color | `string` | - | 文字颜色 |
| background | `string` | - | 背景色 |
| leftIcon | `string` | - | 左侧图标 |
| scrollable | `boolean` | `true` | 是否滚动 |
| speed | `number` | `50` | 滚动速度 |

### 事件

| 事件名 | 说明 |
|--------|------|
| close | 关闭事件 |
| click | 点击事件 |

### 插槽

| 插槽名 | 说明 |
|--------|------|
| default | 自定义内容 |
| left | 左侧内容 |
| right | 右侧内容 |

### 使用示例

```html
<cl-noticebar :text="t('这是一条通知消息')"></cl-noticebar>

<cl-noticebar :text="t('这是一条可关闭的通知')" mode="closeable" @close="onClose"></cl-noticebar>
```

---

## cl-countdown

### 概述

倒计时组件。

### 属性

| 属性名 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| pt | `Object` | `{}` | 样式穿透 |
| modelValue | `number` | `0` | 倒计时时间（毫秒） |
| format | `string` | `'HH:mm:ss'` | 时间格式 |
| autoStart | `boolean` | `true` | 是否自动开始 |
| millisecond | `boolean` | `false` | 是否开启毫秒渲染 |

### 事件

| 事件名 | 说明 |
|--------|------|
| finish | 倒计时结束 |
| change | 倒计时变化 |

### 方法

| 方法名 | 说明 |
|--------|------|
| start | 开始倒计时 |
| pause | 暂停倒计时 |
| reset | 重置倒计时 |

### 使用示例

```html
<script setup lang="ts">
const countdown = ref(60 * 1000); // 60秒

function onFinish() {
  ui.showToast({ message: t("倒计时结束") });
}
</script>

<template>
  <cl-countdown v-model="countdown" @finish="onFinish"></cl-countdown>
</template>
```

---

## cl-empty

### 概述

空状态组件。

### 属性

| 属性名 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| pt | `Object` | `{}` | 样式穿透 |
| image | `string` | - | 图片地址 |
| imageSize | `number` | `100` | 图片大小 |
| description | `string` | `t('暂无数据')` | 描述文案 |
| fixed | `boolean` | `true` | 是否固定在页面中央 |

### 插槽

| 插槽名 | 说明 |
|--------|------|
| default | 自定义底部内容 |
| image | 自定义图片 |
| description | 自定义描述 |

### 使用示例

```html
<cl-empty :description="t('暂无数据')"></cl-empty>

<cl-empty :description="t('暂无订单')">
  <cl-button>{{ t('去下单') }}</cl-button>
</cl-empty>
```

---

## cl-back-top

### 概述

回到顶部组件。

### 属性

| 属性名 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| pt | `Object` | `{}` | 样式穿透 |
| top | `number` | `0` | 当前滚动位置 |
| visibilityHeight | `number` | `400` | 显示阈值 |
| right | `number` | `20` | 右侧距离 |
| bottom | `number` | `100` | 底部距离 |
| icon | `string` | `'arrow-up-line'` | 图标 |
| iconSize | `number` | `24` | 图标大小 |
| iconColor | `string` | - | 图标颜色 |
| background | `string` | - | 背景 |

### 事件

| 事件名 | 说明 |
|--------|------|
| back-top | 点击回到顶部 |

### 使用示例

```html
<script setup lang="ts">
const scrollTop = ref(0);

function onScroll(e) {
  scrollTop.value = e.detail.scrollTop;
}
</script>

<template>
  <scroll-view @scroll="onScroll">
    <!-- 内容 -->
  </scroll-view>
  
  <cl-back-top :top="scrollTop"></cl-back-top>
</template>
```
