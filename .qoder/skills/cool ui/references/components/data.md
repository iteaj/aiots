# 数据展示组件（Data）

本文档包含数据展示组件的详细说明：`cl-list`、`cl-list-item`、`cl-list-view`、`cl-waterfall`、`cl-timeline`、`cl-timeline-item`、`cl-pagination`、`cl-avatar`、`cl-banner`、`cl-read-more`、`cl-rolling-number`、`cl-tree`。

---

## cl-list

### 概述

列表容器组件，配合 `cl-list-item` 使用。

### 属性

| 属性名 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| pt | `Object` | `{}` | 样式穿透 |
| border | `boolean` | `false` | 是否显示边框 |

### 插槽

| 插槽名 | 说明 |
|--------|------|
| default | 列表项内容 |

### 使用示例

```html
<cl-list>
  <cl-list-item>{{ t('列表项 1') }}</cl-list-item>
  <cl-list-item>{{ t('列表项 2') }}</cl-list-item>
</cl-list>
```

---

## cl-list-item

### 概述

列表项组件。

### 属性

| 属性名 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| pt | `Object` | `{}` | 样式穿透 |
| title | `string` | `''` | 标题 |
| label | `string` | `''` | 描述 |
| value | `string` | `''` | 右侧值 |
| icon | `string` | `''` | 图标 |
| arrow | `boolean` | `true` | 是否显示箭头 |
| disabled | `boolean` | `false` | 是否禁用 |

### 事件

| 事件名 | 说明 |
|--------|------|
| click | 点击事件 |

### 插槽

| 插槽名 | 说明 |
|--------|------|
| default | 自定义内容 |
| icon | 图标插槽 |
| title | 标题插槽 |
| label | 描述插槽 |
| value | 右侧值插槽 |
| arrow | 箭头插槽 |

### 使用示例

```html
<cl-list-item :title="t('设置')" icon="settings-line" arrow @click="onSettings"></cl-list-item>
```

---

## cl-list-view

### 概述

虚拟列表组件，支持下拉刷新、上拉加载、分组、索引栏等。

### 属性

| 属性名 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| pt | `Object` | `{}` | 样式穿透 |
| data | `ClListViewItem[]` | `[]` | 列表数据源 |
| itemHeight | `number` | `50` | 列表项高度 |
| headerHeight | `number` | `32` | 分组头部高度 |
| topHeight | `number` | `0` | 列表顶部预留空间高度 |
| bottomHeight | `number` | `0` | 列表底部预留空间高度 |
| bufferSize | `number` | `5` / `15` | 缓冲区大小（App: 5, 其他: 15） |
| virtual | `boolean` | `true` | 是否启用虚拟列表渲染 |
| scrollIntoView | `string` | `''` | 滚动到指定位置 |
| scrollWithAnimation | `boolean` | `false` | 是否启用滚动动画 |
| showScrollbar | `boolean` | `false` | 是否显示滚动条 |
| refresherEnabled | `boolean` | `false` | 是否启用下拉刷新 |
| refresherThreshold | `number` | `50` | 下拉刷新触发距离 |
| refresherBackground | `string` | `'transparent'` | 下拉刷新区域背景色 |
| refresherDefaultText | `string` | `t('下拉刷新')` | 下拉刷新默认文案 |
| refresherPullingText | `string` | `t('释放立即刷新')` | 释放刷新文案 |
| refresherRefreshingText | `string` | `t('加载中')` | 正在刷新文案 |
| showBackTop | `boolean` | `true` | 是否显示回到顶部按钮 |

### ClListViewItem

| 属性名 | 类型 | 说明 |
|--------|------|------|
| label | `string` | 显示文本 |
| value | `any` | 值 |
| index | `string` | 分组索引 |

### 事件

| 事件名 | 说明 |
|--------|------|
| item-tap | 列表项点击 |
| pull | 下拉刷新触发 |
| scrolltoupper | 滚动到顶部 |
| scrolltolower | 滚动到底部 |
| scroll | 滚动事件 |
| scrollend | 滚动结束 |
| refresher-pulling | 下拉中 |
| refresher-refresh | 刷新中 |
| refresher-restore | 恢复 |

### 插槽

| 插槽名 | 参数 | 说明 |
|--------|------|------|
| item | `{ item, data, value, index }` | 列表项内容 |
| header | `{ index }` | 分组头部 |
| top | - | 顶部内容 |
| bottom | - | 底部内容 |
| refresher | `{ status, text }` | 下拉刷新区域 |
| index | `{ index }` | 索引提示 |

### 方法

| 方法名 | 说明 |
|--------|------|
| stopRefresh | 停止下拉刷新 |

### PassThrough

| 节点 | 说明 |
|------|------|
| className | 根元素样式 |
| item | 列表项 |
| itemHover | 列表项点击态 |
| list | 列表容器 |
| scroller | 滚动容器 |
| refresher | 下拉刷新区域 |
| indexBar | 索引栏 |

### 使用示例

```html
<script setup lang="ts">
const list = ref([
  { label: "苹果", value: "apple", index: "A" },
  { label: "香蕉", value: "banana", index: "B" }
]);

function onRefresh() {
  // 请求数据
  list.value = [];
  // 停止刷新
  listRef.value?.stopRefresh();
}
</script>

<template>
  <cl-list-view
    ref="listRef"
    :data="list"
    :refresher-enabled="true"
    @pull="onRefresh"
  >
    <template #item="{ data }">
      <view class="p-4">
        <cl-text>{{ data.label }}</cl-text>
      </view>
    </template>
  </cl-list-view>
</template>
```

---

## cl-waterfall

### 概述

瀑布流布局组件。

### 属性

| 属性名 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| pt | `Object` | `{}` | 样式穿透 |
| column | `number` | `2` | 列数 |
| gutter | `number` | `10` | 列间距 |

### 插槽

| 插槽名 | 说明 |
|--------|------|
| default | 瀑布流内容 |

### 使用示例

```html
<cl-waterfall :column="2" :gutter="10">
  <view v-for="item in list" :key="item.id">
    <cl-image :src="item.image" mode="widthFix"></cl-image>
  </view>
</cl-waterfall>
```

---

## cl-timeline

### 概述

时间轴容器组件，配合 `cl-timeline-item` 使用。

### 属性

| 属性名 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| pt | `Object` | `{}` | 样式穿透 |

### 插槽

| 插槽名 | 说明 |
|--------|------|
| default | 时间轴项 |

### 使用示例

```html
<cl-timeline>
  <cl-timeline-item :title="t('创建项目')" :time="'2024-01-01'"></cl-timeline-item>
  <cl-timeline-item :title="t('开发中')" :time="'2024-01-15'"></cl-timeline-item>
</cl-timeline>
```

---

## cl-timeline-item

### 概述

时间轴项组件。

### 属性

| 属性名 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| pt | `Object` | `{}` | 样式穿透 |
| title | `string` | `''` | 标题 |
| content | `string` | `''` | 内容 |
| time | `string` | `''` | 时间 |
| icon | `string` | `''` | 图标 |
| color | `string` | `'primary'` | 颜色 |

### 插槽

| 插槽名 | 说明 |
|--------|------|
| default | 自定义内容 |
| icon | 图标插槽 |

---

## cl-pagination

### 概述

分页组件。

### 属性

| 属性名 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| pt | `Object` | `{}` | 样式穿透 |
| modelValue | `number` | `1` | 当前页码 |
| total | `number` | `0` | 总条数 |
| pageSize | `number` | `10` | 每页条数 |
| showTotal | `boolean` | `true` | 是否显示总数 |
| showPageSize | `boolean` | `true` | 是否显示每页条数选择器 |
| pageSizes | `number[]` | `[10, 20, 50, 100]` | 每页条数选项 |

### 事件

| 事件名 | 说明 |
|--------|------|
| update:modelValue | 页码改变 |
| change | 页码或每页条数改变 |

### 使用示例

```html
<cl-pagination v-model="page" :total="100" :page-size="10" @change="onPageChange"></cl-pagination>
```

---

## cl-avatar

### 概述

头像组件。

### 属性

| 属性名 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| pt | `Object` | `{}` | 样式穿透 |
| src | `string` | `''` | 图片地址 |
| size | `number` | `40` | 头像大小 |
| round | `boolean` | `true` | 是否圆形 |
| icon | `string` | `'user-line'` | 默认图标 |

### 插槽

| 插槽名 | 说明 |
|--------|------|
| default | 自定义内容 |

### 使用示例

```html
<cl-avatar src="/static/avatar.png" :size="60"></cl-avatar>
```

---

## cl-banner

### 概述

轮播图组件。

### 属性

| 属性名 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| pt | `Object` | `{}` | 样式穿透 |
| list | `ClBannerItem[]` | `[]` | 轮播数据 |
| autoplay | `boolean` | `true` | 是否自动播放 |
| interval | `number` | `3000` | 自动切换间隔 |
| duration | `number` | `500` | 滑动动画时长 |
| indicatorDots | `boolean` | `true` | 是否显示指示点 |
| indicatorColor | `string` | - | 指示点颜色 |
| indicatorActiveColor | `string` | - | 当前选中的指示点颜色 |
| circular | `boolean` | `true` | 是否循环切换 |

### ClBannerItem

| 属性名 | 类型 | 说明 |
|--------|------|------|
| src | `string` | 图片地址 |
| url | `string` | 跳转地址 |
| title | `string` | 标题 |

### 事件

| 事件名 | 说明 |
|--------|------|
| change | 滑块切换 |
| click | 点击轮播项 |

### 使用示例

```html
<cl-banner :list="banners" @click="onBannerClick"></cl-banner>
```

---

## cl-read-more

### 概述

展开收起组件。

### 属性

| 属性名 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| pt | `Object` | `{}` | 样式穿透 |
| modelValue | `boolean` | `false` | 是否展开 |
| maxLines | `number` | `3` | 最大显示行数 |
| expandText | `string` | `t('展开')` | 展开文案 |
| collapseText | `string` | `t('收起')` | 收起文案 |

### 事件

| 事件名 | 说明 |
|--------|------|
| update:modelValue | 展开/收起状态改变 |
| change | 展开/收起状态改变 |

### 插槽

| 插槽名 | 说明 |
|--------|------|
| default | 内容 |

### 使用示例

```html
<cl-read-more :max-lines="3">
  <cl-text>{{ longText }}</cl-text>
</cl-read-more>
```

---

## cl-rolling-number

### 概述

数字滚动组件。

### 属性

| 属性名 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| pt | `Object` | `{}` | 样式穿透 |
| modelValue | `number` | `0` | 绑定值 |
| duration | `number` | `1000` | 动画时长 |
| decimals | `number` | `0` | 小数位数 |
| separator | `string` | `','` | 千分位分隔符 |
| prefix | `string` | `''` | 前缀 |
| suffix | `string` | `''` | 后缀 |

### 使用示例

```html
<cl-rolling-number :value="12345" :duration="1000" prefix="¥"></cl-rolling-number>
```

---

## cl-tree

### 概述

树形结构组件。

### 属性

| 属性名 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| pt | `Object` | `{}` | 样式穿透 |
| data | `ClTreeNode[]` | `[]` | 树形数据 |
| defaultExpandAll | `boolean` | `false` | 是否默认展开所有节点 |
| expandIcon | `string` | - | 展开图标 |
| collapseIcon | `string` | - | 收起图标 |

### ClTreeNode

| 属性名 | 类型 | 说明 |
|--------|------|------|
| label | `string` | 节点标题 |
| value | `any` | 节点值 |
| children | `ClTreeNode[]` | 子节点 |
| disabled | `boolean` | 是否禁用 |

### 事件

| 事件名 | 说明 |
|--------|------|
| node-click | 节点点击 |
| node-expand | 节点展开 |
| node-collapse | 节点收起 |

### 插槽

| 插槽名 | 参数 | 说明 |
|--------|------|------|
| default | `{ node }` | 节点内容 |

### 使用示例

```html
<script setup lang="ts">
const treeData = [
  {
    label: "一级 1",
    value: "1",
    children: [
      { label: "二级 1-1", value: "1-1" },
      { label: "二级 1-2", value: "1-2" }
    ]
  }
];
</script>

<template>
  <cl-tree :data="treeData" @node-click="onNodeClick"></cl-tree>
</template>
```
