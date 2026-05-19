# 导航组件（Navigation）

本文档包含导航组件的详细说明：`cl-topbar`、`cl-tabs`、`cl-tabbar`、`cl-index-bar`。

---

## cl-topbar

### 概述

顶部导航栏组件，支持自定义返回按钮、首页按钮、左右插槽等。

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
<!-- 基础用法 -->
<cl-topbar :title="t('页面标题')"></cl-topbar>

<!-- 隐藏返回按钮 -->
<cl-topbar :title="t('页面标题')" :back="false"></cl-topbar>

<!-- 显示首页按钮 -->
<cl-topbar :title="t('页面标题')" home></cl-topbar>

<!-- 自定义右侧 -->
<cl-topbar :title="t('页面标题')">
  <template #right>
    <cl-icon name="more-line" @tap="onMore"></cl-icon>
  </template>
</cl-topbar>

<!-- 自定义左侧文案 -->
<cl-topbar :title="t('页面标题')" left-text="关闭" @left="onClose"></cl-topbar>

<!-- 监听返回事件 -->
<cl-topbar :title="t('页面标题')" @back="onBack"></cl-topbar>
```

---

## cl-tabs

### 概述

Tab 标签页组件，支持滑动切换、下划线指示器、粘性定位等。

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
| sticky | `boolean` | `false` | 是否粘性定位 |
| offsetTop | `number` | `0` | 粘性定位偏移值 |

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
<script setup lang="ts">
const activeTab = ref(0);
const tabs = [t('全部'), t('待付款'), t('已完成')];
</script>

<template>
  <!-- 基础用法 -->
  <cl-tabs v-model="activeTab" :labels="tabs">
    <view v-if="activeTab === 0" class="p-4">
      <cl-text>{{ t('全部内容') }}</cl-text>
    </view>
    <view v-if="activeTab === 1" class="p-4">
      <cl-text>{{ t('待付款内容') }}</cl-text>
    </view>
    <view v-if="activeTab === 2" class="p-4">
      <cl-text>{{ t('已完成内容') }}</cl-text>
    </view>
  </cl-tabs>

  <!-- 粘性定位 -->
  <cl-tabs v-model="activeTab" :labels="tabs" sticky :offset-top="44">
    <!-- 内容 -->
  </cl-tabs>

  <!-- 隐藏下划线 -->
  <cl-tabs v-model="activeTab" :labels="tabs" :show-line="false">
    <!-- 内容 -->
  </cl-tabs>

  <!-- 自定义下划线颜色 -->
  <cl-tabs v-model="activeTab" :labels="tabs" line-color="#ff0000">
    <!-- 内容 -->
  </cl-tabs>
</template>
```

---

## cl-tabbar

### 概述

底部导航栏组件，支持图标、角标、自定义项等。

### 属性

| 属性名 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| pt | `Object` | `{}` | 样式穿透 |
| modelValue | `number` | `0` | 当前激活的标签索引 |
| list | `ClTabbarItem[]` | `[]` | 导航项列表 |
| fixed | `boolean` | `true` | 是否固定在底部 |
| safeAreaInsetBottom | `boolean` | `true` | 是否开启底部安全区域 |
| border | `boolean` | `true` | 是否显示上边框 |
| background | `string` | `'white'` | 背景色 |
| activeColor | `string` | - | 激活颜色 |
| inactiveColor | `string` | - | 未激活颜色 |

### ClTabbarItem

| 属性名 | 类型 | 说明 |
|--------|------|------|
| label | `string` | 导航名称 |
| icon | `string` | 图标名称 |
| activeIcon | `string` | 激活图标名称 |
| badge | `string \| number` | 角标内容 |
| dot | `boolean` | 是否显示小圆点 |
| disabled | `boolean` | 是否禁用 |

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
  { label: t("分类"), icon: "apps-line", activeIcon: "apps-fill" },
  { label: t("购物车"), icon: "shopping-cart-line", activeIcon: "shopping-cart-fill", badge: 3 },
  { label: t("我的"), icon: "user-line", activeIcon: "user-fill" }
];

function onChange(index: number) {
  // 切换页面
}
</script>

<template>
  <!-- 基础用法 -->
  <cl-tabbar v-model="active" :list="tabbarList" @change="onChange"></cl-tabbar>

  <!-- 自定义导航项 -->
  <cl-tabbar v-model="active" :list="tabbarList">
    <template #item="{ item, index, active }">
      <view class="flex flex-col items-center">
        <cl-icon :name="active ? item.activeIcon : item.icon"></cl-icon>
        <cl-text :size="12" :color="active ? 'primary' : 'info'">{{ item.label }}</cl-text>
      </view>
    </template>
  </cl-tabbar>

  <!-- 不固定在底部 -->
  <cl-tabbar v-model="active" :list="tabbarList" :fixed="false"></cl-tabbar>
</template>
```

---

## cl-index-bar

### 概述

索引栏组件，用于列表快速定位（如通讯录字母索引）。

### 属性

| 属性名 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| pt | `Object` | `{}` | 样式穿透 |
| modelValue | `number` | `0` | 当前激活索引 |
| list | `string[]` | `[]` | 索引列表 |
| showPopover | `boolean` | `true` | 是否显示索引提示气泡 |

### 事件

| 事件名 | 说明 |
|--------|------|
| update:modelValue | 索引改变 |
| change | 索引改变 |

### 使用示例

```html
<script setup lang="ts">
const indexList = ['A', 'B', 'C', 'D', 'E', 'F', 'G'];
const activeIndex = ref(0);

function onIndexChange(index: number) {
  // 滚动到对应位置
  const letter = indexList[index];
  // scrollToIndex(letter);
}
</script>

<template>
  <view class="relative">
    <!-- 列表内容 -->
    <scroll-view>
      <!-- ... -->
    </scroll-view>
    
    <!-- 索引栏 -->
    <cl-index-bar 
      v-model="activeIndex" 
      :list="indexList" 
      @change="onIndexChange"
    ></cl-index-bar>
  </view>
</template>
```

### 配合 cl-list-view 使用

```html
<script setup lang="ts">
const contacts = ref([
  { label: 'Alice', value: 1, index: 'A' },
  { label: 'Bob', value: 2, index: 'B' },
  { label: 'Charlie', value: 3, index: 'C' },
  // ...
]);
</script>

<template>
  <cl-list-view :data="contacts">
    <template #item="{ data }">
      <view class="p-4">
        <cl-text>{{ data.label }}</cl-text>
      </view>
    </template>
  </cl-list-view>
</template>
```

> cl-list-view 内置了索引栏支持，当数据项包含 `index` 字段时会自动显示索引栏。
