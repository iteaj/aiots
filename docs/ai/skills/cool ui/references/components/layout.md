# 布局组件（Layout）

本文档包含布局组件的详细说明：`cl-page`、`cl-topbar`、`cl-tabs`、`cl-tabbar`、`cl-row`、`cl-col`、`cl-sticky`、`cl-float-view`、`cl-footer`、`cl-safe-area`、`cl-collapse`、`cl-index-bar`。

---

## cl-page

### 概述

页面容器组件，提供统一的页面布局结构，包含顶部栏、内容区域、底部区域。

### 属性

| 属性名 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| pt | `Object` | `{}` | 样式穿透 |
| title | `string` | `''` | 页面标题 |
| loading | `boolean` | `false` | 是否显示加载状态 |
| empty | `boolean` | `false` | 是否显示空状态 |
| back | `boolean` | `true` | 是否显示返回按钮 |
| home | `boolean` | `true` | 是否显示首页按钮 |
| navbar | `boolean` | `true` | 是否显示导航栏 |

### 插槽

| 插槽名 | 说明 |
|--------|------|
| default | 页面内容 |
| header | 顶部区域 |
| footer | 底部区域 |
| navbar | 导航栏内容 |
| back-icon | 返回图标 |
| home-icon | 首页图标 |

### PassThrough

| 节点 | 说明 |
|------|------|
| className | 根元素样式 |
| header | 顶部区域 |
| container | 内容区域 |
| footer | 底部区域 |
| navbar | 导航栏 |

### 使用示例

```html
<cl-page :title="t('页面标题')">
  <view class="p-4">
    <cl-text>{{ t('页面内容') }}</cl-text>
  </view>
  
  <template #footer>
    <cl-button fluid>{{ t('提交') }}</cl-button>
  </template>
</cl-page>
```

---

## cl-topbar

### 概述

顶部导航栏组件。

### 属性

| 属性名 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| pt | `Object` | `{}` | 样式穿透 |
| title | `string` | `''` | 标题 |
| back | `boolean` | `true` | 是否显示返回按钮 |
| home | `boolean` | `false` | 是否显示首页按钮 |
| leftIcon | `string` | `'arrow-left-s-line'` | 左侧图标 |
| leftText | `string` | `''` | 左侧文案 |
| rightIcon | `string` | `''` | 右侧图标 |
| rightText | `string` | `''` | 右侧文案 |
| border | `boolean` | `true` | 是否显示边框 |
| background | `string` | `'transparent'` | 背景色 |

### 事件

| 事件名 | 说明 |
|--------|------|
| back | 返回按钮点击 |
| home | 首页按钮点击 |
| left | 左侧按钮点击 |
| right | 右侧按钮点击 |

### 插槽

| 插槽名 | 说明 |
|--------|------|
| default | 标题区域 |
| left | 左侧区域 |
| right | 右侧区域 |

### PassThrough

| 节点 | 说明 |
|------|------|
| className | 根元素样式 |
| left | 左侧区域 |
| title | 标题区域 |
| right | 右侧区域 |

### 使用示例

```html
<cl-topbar :title="t('页面标题')" @back="onBack">
  <template #right>
    <cl-icon name="more-line" @tap="onMore"></cl-icon>
  </template>
</cl-topbar>
```

---

## cl-tabs

### 概述

Tab 标签页组件。

### 属性

| 属性名 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| pt | `Object` | `{}` | 样式穿透 |
| modelValue | `number \| string` | `0` | 当前激活的标签索引或名称 |
| labels | `string[]` | `[]` | 标签列表 |
| scrollable | `boolean` | `true` | 是否可滚动 |
| stretch | `boolean` | `false` | 是否拉伸填满 |
| showLine | `boolean` | `true` | 是否显示下划线 |
| lineColor | `string` | - | 下划线颜色 |
| lineWidth | `string \| number` | - | 下划线宽度 |
| lineHeight | `string \| number` | - | 下划线高度 |

### 事件

| 事件名 | 说明 |
|--------|------|
| update:modelValue | 切换标签 |
| change | 切换标签 |

### 插槽

| 插槽名 | 说明 |
|--------|------|
| default | Tab 内容 |

### PassThrough

| 节点 | 说明 |
|------|------|
| className | 根元素样式 |
| header | 头部区域 |
| item | 标签项 |
| line | 下划线 |
| content | 内容区域 |

### 使用示例

```html
<cl-tabs v-model="activeTab" :labels="[t('全部'), t('待付款'), t('已完成')]">
  <cl-list v-if="activeTab === 0" :data="allOrders"></cl-list>
  <cl-list v-if="activeTab === 1" :data="pendingOrders"></cl-list>
  <cl-list v-if="activeTab === 2" :data="completedOrders"></cl-list>
</cl-tabs>
```

---

## cl-tabbar

### 概述

底部导航栏组件。

### 属性

| 属性名 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| pt | `Object` | `{}` | 样式穿透 |
| modelValue | `number` | `0` | 当前激活的标签索引 |
| list | `ClTabbarItem[]` | `[]` | 导航项列表 |
| fixed | `boolean` | `true` | 是否固定在底部 |
| safeAreaInsetBottom | `boolean` | `true` | 是否开启底部安全区域 |

### ClTabbarItem

| 属性名 | 类型 | 说明 |
|--------|------|------|
| label | `string` | 导航名称 |
| icon | `string` | 图标名称 |
| activeIcon | `string` | 激活图标名称 |
| badge | `string \| number` | 角标内容 |

### 事件

| 事件名 | 说明 |
|--------|------|
| update:modelValue | 切换导航 |
| change | 切换导航 |

### 插槽

| 插槽名 | 参数 | 说明 |
|--------|------|------|
| item | `{ item, index, active }` | 自定义导航项 |

### PassThrough

| 节点 | 说明 |
|------|------|
| className | 根元素样式 |
| item | 导航项 |
| icon | 图标 |
| label | 文本 |

### 使用示例

```html
<script setup lang="ts">
const active = ref(0);
const tabbarList = [
  { label: t("首页"), icon: "home-line", activeIcon: "home-fill" },
  { label: t("我的"), icon: "user-line", activeIcon: "user-fill" }
];
</script>

<template>
  <cl-tabbar v-model="active" :list="tabbarList"></cl-tabbar>
</template>
```

---

## cl-row

### 概述

行组件，配合 `cl-col` 使用实现栅格布局。

### 属性

| 属性名 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| pt | `Object` | `{}` | 样式穿透 |
| gutter | `number \| number[]` | `0` | 列间距 |
| justify | `string` | `'start'` | 水平排列方式 |
| align | `string` | `'center'` | 垂直排列方式 |
| wrap | `boolean` | `true` | 是否换行 |

### 插槽

| 插槽名 | 说明 |
|--------|------|
| default | 列内容 |

### 使用示例

```html
<cl-row :gutter="20">
  <cl-col :span="12">{{ t('左侧') }}</cl-col>
  <cl-col :span="12">{{ t('右侧') }}</cl-col>
</cl-row>
```

---

## cl-col

### 概述

列组件，配合 `cl-row` 使用。

### 属性

| 属性名 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| pt | `Object` | `{}` | 样式穿透 |
| span | `number` | `24` | 占位格数 |
| offset | `number` | `0` | 偏移格数 |

### 插槽

| 插槽名 | 说明 |
|--------|------|
| default | 列内容 |

---

## cl-sticky

### 概述

吸顶容器组件。

### 属性

| 属性名 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| pt | `Object` | `{}` | 样式穿透 |
| offsetTop | `number` | `0` | 吸顶距离 |
| zIndex | `number` | `999` | 层级 |

### 事件

| 事件名 | 说明 |
|--------|------|
| change | 吸顶状态改变 |

### 插槽

| 插槽名 | 说明 |
|--------|------|
| default | 吸顶内容 |

### 使用示例

```html
<cl-sticky :offset-top="44">
  <cl-tabs v-model="activeTab" :labels="tabs"></cl-tabs>
</cl-sticky>
```

---

## cl-float-view

### 概述

悬浮视图组件，可自由拖动。

### 属性

| 属性名 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| pt | `Object` | `{}` | 样式穿透 |
| x | `number` | `0` | X 坐标 |
| y | `number` | `0` | Y 坐标 |
| gap | `number` | `10` | 边缘间距 |
|吸附 | `boolean` | `true` | 是否吸附边缘 |
|吸附阈值 | `number` | `50` | 吸附判定阈值 |

### 事件

| 事件名 | 说明 |
|--------|------|
| update:x | X 坐标改变 |
| update:y | Y 坐标改变 |
| dragstart | 开始拖动 |
| dragend | 结束拖动 |

### 插槽

| 插槽名 | 说明 |
|--------|------|
| default | 悬浮内容 |

### 使用示例

```html
<cl-float-view>
  <cl-button rounded icon="service-line"></cl-button>
</cl-float-view>
```

---

## cl-footer

### 概述

底部视图组件，常用于底部操作栏。

### 属性

| 属性名 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| pt | `Object` | `{}` | 样式穿透 |
| fixed | `boolean` | `false` | 是否固定在底部 |
| safeAreaInsetBottom | `boolean` | `true` | 是否开启底部安全区域 |
| border | `boolean` | `false` | 是否显示上边框 |
| background | `string` | `'white'` | 背景色 |

### 插槽

| 插槽名 | 说明 |
|--------|------|
| default | 底部内容 |

### 使用示例

```html
<cl-footer fixed>
  <cl-button fluid>{{ t('立即购买') }}</cl-button>
</cl-footer>
```

---

## cl-safe-area

### 概述

安全区域适配组件。

### 属性

| 属性名 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| pt | `Object` | `{}` | 样式穿透 |
| position | `string` | `'bottom'` | 安全区域位置：`top`、`bottom` |
| background | `string` | `'transparent'` | 背景色 |

### 使用示例

```html
<!-- 底部安全区域 -->
<cl-safe-area position="bottom"></cl-safe-area>

<!-- 顶部安全区域 -->
<cl-safe-area position="top"></cl-safe-area>
```

---

## cl-collapse

### 概述

折叠面板组件。

### 属性

| 属性名 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| pt | `Object` | `{}` | 样式穿透 |
| modelValue | `string[]` | `[]` | 展开的面板列表 |
| accordion | `boolean` | `false` | 是否手风琴模式 |

### 事件

| 事件名 | 说明 |
|--------|------|
| update:modelValue | 展开状态改变 |
| change | 展开状态改变 |

### 插槽

| 插槽名 | 说明 |
|--------|------|
| default | 折叠面板项 |

### 使用示例

```html
<cl-collapse v-model="activeNames" accordion>
  <cl-collapse-item :title="t('标题1')" name="1">
    {{ t('内容1') }}
  </cl-collapse-item>
  <cl-collapse-item :title="t('标题2')" name="2">
    {{ t('内容2') }}
  </cl-collapse-item>
</cl-collapse>
```

---

## cl-index-bar

### 概述

索引栏组件，用于字母快速定位。

### 属性

| 属性名 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| pt | `Object` | `{}` | 样式穿透 |
| modelValue | `number` | `0` | 当前激活索引 |
| list | `string[]` | `[]` | 索引列表 |

### 事件

| 事件名 | 说明 |
|--------|------|
| update:modelValue | 索引改变 |
| change | 索引改变 |

### 使用示例

```html
<cl-index-bar v-model="activeIndex" :list="['A', 'B', 'C']" @change="onIndexChange"></cl-index-bar>
```
