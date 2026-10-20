# 工具组件（Utilities）

本文档包含工具组件的详细说明：`cl-draggable`、`cl-sign`、`cl-cropper`、`cl-watermark`、`cl-qrcode`、`cl-slide-verify`、`cl-select-seat`、`cl-marquee`、`cl-filter-bar`、`cl-picker-view`、`cl-keyboard-number`、`cl-keyboard-password`、`cl-keyboard-car`、`cl-index-bar`。

---

## cl-draggable

### 概述

拖拽排序组件，支持列表项拖拽排序。

### 属性

| 属性名 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| pt | `Object` | `{}` | 样式穿透 |
| modelValue | `any[]` | `[]` | 列表数据 |
| itemKey | `string` | `'id'` | 唯一标识字段 |
| disabled | `boolean` | `false` | 是否禁用拖拽 |
| animation | `number` | `200` | 动画时长 |
| handle | `string` | `''` | 拖拽句柄选择器 |

### 事件

| 事件名 | 说明 |
|--------|------|
| update:modelValue | 数据更新 |
| start | 开始拖拽 |
| end | 结束拖拽 |
| move | 拖拽移动 |

### 插槽

| 插槽名 | 参数 | 说明 |
|--------|------|------|
| default | `{ item, index }` | 列表项内容 |

### 使用示例

```html
<script setup lang="ts">
const list = ref([
  { id: 1, name: t("项目1") },
  { id: 2, name: t("项目2") },
  { id: 3, name: t("项目3") }
]);
</script>

<template>
  <cl-draggable v-model="list" item-key="id">
    <template #default="{ item, index }">
      <view class="p-4 bg-white mb-2">
        <cl-text>{{ item.name }}</cl-text>
      </view>
    </template>
  </cl-draggable>
</template>
```

---

## cl-sign

### 概述

签名板组件，支持手写签名。

### 属性

| 属性名 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| pt | `Object` | `{}` | 样式穿透 |
| width | `number` | `300` | 画布宽度 |
| height | `number` | `200` | 画布高度 |
| lineWidth | `number` | `3` | 线条宽度 |
| lineColor | `string` | `'#000'` | 线条颜色 |
| backgroundColor | `string` | `'#fff'` | 背景色 |
| disabled | `boolean` | `false` | 是否禁用 |

### 方法

| 方法名 | 参数 | 说明 |
|--------|------|------|
| clear | - | 清空签名 |
| save | `(format?: string) => string` | 保存签名，返回图片路径 |
| isEmpty | - | 判断是否为空 |

### 使用示例

```html
<script setup lang="ts">
const signRef = ref();

function onClear() {
  signRef.value?.clear();
}

function onSave() {
  const path = signRef.value?.save('png');
  console.log('签名图片:', path);
}
</script>

<template>
  <cl-sign ref="signRef" :width="350" :height="200"></cl-sign>
  
  <cl-button @tap="onClear">{{ t('清空') }}</cl-button>
  <cl-button @tap="onSave">{{ t('保存') }}</cl-button>
</template>
```

---

## cl-cropper

### 概述

图片裁剪组件。

### 属性

| 属性名 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| pt | `Object` | `{}` | 样式穿透 |
| src | `string` | `''` | 图片地址 |
| aspectRatio | `number` | - | 裁剪比例（宽/高） |
| outputWidth | `number` | - | 输出图片宽度 |
| outputHeight | `number` | - | 输出图片高度 |
| outputQuality | `number` | `1` | 输出图片质量（0-1） |

### 事件

| 事件名 | 说明 |
|--------|------|
| confirm | 确认裁剪 |
| cancel | 取消裁剪 |

### 方法

| 方法名 | 说明 |
|--------|------|
| getCroppedImage | 获取裁剪后的图片 |

### 使用示例

```html
<script setup lang="ts">
const imageSrc = ref('');

function onConfirm(path: string) {
  console.log('裁剪后的图片:', path);
}
</script>

<template>
  <cl-cropper :src="imageSrc" @confirm="onConfirm"></cl-cropper>
</template>
```

---

## cl-watermark

### 概述

水印组件。

### 属性

| 属性名 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| pt | `Object` | `{}` | 样式穿透 |
| content | `string` | `''` | 水印内容 |
| fontSize | `number` | `14` | 字体大小 |
| fontColor | `string` | `'#000'` | 字体颜色 |
| opacity | `number` | `0.15` | 透明度 |
| rotate | `number` | `-20` | 旋转角度 |
| gapX | `number` | `100` | 水平间距 |
| gapY | `number` | `100` | 垂直间距 |

### 插槽

| 插槽名 | 说明 |
|--------|------|
| default | 水印覆盖的内容 |

### 使用示例

```html
<cl-watermark :content="t('机密文件')">
  <view class="p-4">
    <cl-text>{{ t('这是需要添加水印的内容') }}</cl-text>
  </view>
</cl-watermark>
```

---

## cl-qrcode

### 概述

二维码生成组件。

### 属性

| 属性名 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| pt | `Object` | `{}` | 样式穿透 |
| value | `string` | `''` | 二维码内容 |
| size | `number` | `200` | 二维码大小 |
| color | `string` | `'#000'` | 前景色 |
| backgroundColor | `string` | `'#fff'` | 背景色 |
| level | `string` | `'M'` | 容错级别：`L`、`M`、`Q`、`H` |

### 事件

| 事件名 | 说明 |
|--------|------|
| ready | 二维码生成完成 |
| error | 生成失败 |

### 使用示例

```html
<cl-qrcode :value="'https://example.com'" :size="200"></cl-qrcode>
```

---

## cl-slide-verify

### 概述

滑动验证组件。

### 属性

| 属性名 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| pt | `Object` | `{}` | 样式穿透 |
| width | `number` | `300` | 宽度 |
| height | `number` | `40` | 高度 |
| sliderSize | `number` | `40` | 滑块大小 |
| accuracy | `number` | `5` | 精度 |
| background | `string` | - | 背景色 |
| sliderBackground | `string` | - | 滑块背景色 |

### 事件

| 事件名 | 说明 |
|--------|------|
| success | 验证成功 |
| fail | 验证失败 |
| refresh | 刷新验证 |

### 使用示例

```html
<cl-slide-verify @success="onSuccess" @fail="onFail"></cl-slide-verify>
```

---

## cl-select-seat

### 概述

座位选择组件，常用于电影票、演出票选座。

### 属性

| 属性名 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| pt | `Object` | `{}` | 样式穿透 |
| rows | `number` | `10` | 行数 |
| cols | `number` | `10` | 列数 |
| maxSelect | `number` | `4` | 最大选择数量 |
| selected | `string[]` | `[]` | 已选座位（格式：`row-col`） |
| occupied | `string[]` | `[]` | 已占座位 |

### 事件

| 事件名 | 说明 |
|--------|------|
| update:selected | 选择更新 |
| select | 选择座位 |
| unselect | 取消选择 |
| overflow | 超出最大选择数量 |

### 使用示例

```html
<script setup lang="ts">
const selectedSeats = ref<string[]>([]);
</script>

<template>
  <cl-select-seat v-model:selected="selectedSeats" :rows="8" :cols="12"></cl-select-seat>
</template>
```

---

## cl-marquee

### 概述

跑马灯组件。

### 属性

| 属性名 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| pt | `Object` | `{}` | 样式穿透 |
| direction | `string` | `'left'` | 滚动方向：`left`、`right`、`up`、`down` |
| speed | `number` | `50` | 滚动速度 |
| pauseOnHover | `boolean` | `true` | 鼠标悬停时暂停 |

### 插槽

| 插槽名 | 说明 |
|--------|------|
| default | 滚动内容 |

### 使用示例

```html
<cl-marquee direction="left" :speed="50">
  <cl-text>{{ t('这是一条滚动消息') }}</cl-text>
</cl-marquee>
```

---

## cl-filter-bar

### 概述

筛选栏组件，配合 `cl-filter-item` 使用。

### 属性

| 属性名 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| pt | `Object` | `{}` | 样式穿透 |
| modelValue | `Object` | `{}` | 筛选条件 |
| layout | `string` | `'horizontal'` | 布局方式：`horizontal`、`vertical` |

### 事件

| 事件名 | 说明 |
|--------|------|
| update:modelValue | 筛选条件更新 |
| change | 筛选条件改变 |
| reset | 重置筛选 |

### 插槽

| 插槽名 | 说明 |
|--------|------|
| default | 筛选项内容 |

### 使用示例

```html
<script setup lang="ts">
const filters = ref({
  category: '',
  price: ''
});
</script>

<template>
  <cl-filter-bar v-model="filters">
    <cl-filter-item prop="category" :label="t('分类')" :options="categories"></cl-filter-item>
    <cl-filter-item prop="price" :label="t('价格')" :options="prices"></cl-filter-item>
  </cl-filter-bar>
</template>
```

---

## cl-filter-item

### 概述

筛选项组件。

### 属性

| 属性名 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| pt | `Object` | `{}` | 样式穿透 |
| prop | `string` | `''` | 字段名 |
| label | `string` | `''` | 标签 |
| options | `ClFilterOption[]` | `[]` | 选项列表 |
| multiple | `boolean` | `false` | 是否多选 |

### ClFilterOption

| 属性名 | 类型 | 说明 |
|--------|------|------|
| label | `string` | 选项标签 |
| value | `any` | 选项值 |

### 事件

| 事件名 | 说明 |
|--------|------|
| change | 选择改变 |

---

## cl-picker-view

### 概述

滚动选择器组件。

### 属性

| 属性名 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| pt | `Object` | `{}` | 样式穿透 |
| value | `number[]` | `[]` | 当前选中索引 |
| columns | `ClPickerColumn[]` | `[]` | 列数据 |
| itemHeight | `number` | `44` | 选项高度 |
| visibleItemCount | `number` | `5` | 可见选项数量 |

### ClPickerColumn

| 属性名 | 类型 | 说明 |
|--------|------|------|
| values | `string[]` | 选项值列表 |
| defaultIndex | `number` | 默认选中索引 |

### 事件

| 事件名 | 说明 |
|--------|------|
| change | 选择改变 |
| change-index | 索引改变 |

### 使用示例

```html
<script setup lang="ts">
const columns = [
  { values: [t('周一'), t('周二'), t('周三')] },
  { values: [t('上午'), t('下午'), t('晚上')] }
];

const indexes = ref([0, 0]);
</script>

<template>
  <cl-picker-view v-model="indexes" :columns="columns"></cl-picker-view>
</template>
```

---

## cl-keyboard-number

### 概述

数字键盘组件。

### 属性

| 属性名 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| pt | `Object` | `{}` | 样式穿透 |
| modelValue | `string` | `''` | 输入值 |
| maxlength | `number` | - | 最大长度 |
| disabled | `boolean` | `false` | 是否禁用 |

### 事件

| 事件名 | 说明 |
|--------|------|
| update:modelValue | 值更新 |
| input | 输入事件 |
| delete | 删除事件 |
| confirm | 确认事件 |

### 使用示例

```html
<cl-keyboard-number v-model="value" @confirm="onConfirm"></cl-keyboard-number>
```

---

## cl-keyboard-password

### 概述

密码键盘组件。

### 属性

| 属性名 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| pt | `Object` | `{}` | 样式穿透 |
| modelValue | `string` | `''` | 输入值 |
| length | `number` | `6` | 密码长度 |
| disabled | `boolean` | `false` | 是否禁用 |

### 事件

| 事件名 | 说明 |
|--------|------|
| update:modelValue | 值更新 |
| complete | 输入完成 |
| confirm | 确认事件 |

### 使用示例

```html
<cl-keyboard-password v-model="password" :length="6" @complete="onComplete"></cl-keyboard-password>
```

---

## cl-keyboard-car

### 概述

车牌键盘组件。

### 属性

| 属性名 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| pt | `Object` | `{}` | 样式穿透 |
| modelValue | `string` | `''` | 输入值 |
| maxlength | `number` | `8` | 最大长度 |
| disabled | `boolean` | `false` | 是否禁用 |

### 事件

| 事件名 | 说明 |
|--------|------|
| update:modelValue | 值更新 |
| confirm | 确认事件 |

### 使用示例

```html
<cl-keyboard-car v-model="plateNumber" @confirm="onConfirm"></cl-keyboard-car>
```

---

## cl-index-bar

### 概述

索引栏组件，用于列表快速定位。

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
<script setup lang="ts">
const indexList = ['A', 'B', 'C', 'D', 'E'];
const activeIndex = ref(0);

function onIndexChange(index: number) {
  // 滚动到对应位置
}
</script>

<template>
  <cl-index-bar v-model="activeIndex" :list="indexList" @change="onIndexChange"></cl-index-bar>
</template>
```

---

## 配合模块

以下模块需额外安装使用：

### cool-canvas

画布组件模块，提供更强大的画布能力。

### cool-vibrate

震动反馈模块，提供触觉反馈。

```ts
import { useVibrate } from "@/uni_modules/cool-vibrate";

const vibrate = useVibrate();

// 轻震动
vibrate.light();

// 重震动
vibrate.heavy();
```

### cool-svg

SVG 图标模块，支持自定义 SVG 图标。

### cool-share

分享功能模块，支持多平台分享。
