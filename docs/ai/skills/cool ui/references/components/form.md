# 表单组件（Form）

本文档包含表单组件的详细说明：`cl-form`、`cl-form-item`、`cl-input`、`cl-textarea`、`cl-select`、`cl-checkbox`、`cl-radio`、`cl-switch`、`cl-rate`、`cl-slider`、`cl-input-number`、`cl-input-otp`、`cl-select-date`、`cl-select-time`、`cl-calendar`、`cl-cascader`、`cl-upload`。

---

## cl-form

### 概述

表单容器组件，负责整体布局与校验，与 `cl-form-item` 配合完成表单功能。

### 属性

| 属性名 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| pt | `Object` | `{}` | 样式穿透 |
| modelValue | `Object` | `{}` | 表单数据模型 |
| rules | `Map<string, ClFormRule[]>` | `new Map()` | 校验规则 |
| labelPosition | `string` | `'top'` | 标签位置：`top`、`left` |
| labelWidth | `string` | `'70px'` | 标签宽度 |
| showAsterisk | `boolean` | `true` | 是否显示必填星号 |
| showMessage | `boolean` | `true` | 是否显示错误信息 |
| disabled | `boolean` | `false` | 是否禁用整个表单 |
| scrollToError | `boolean` | `true` | 滚动到第一个错误位置 |

### ClFormRule 校验规则

| 属性名 | 类型 | 说明 |
|--------|------|------|
| required | `boolean` | 是否必填 |
| message | `string` | 错误提示信息 |
| min | `number` | 最小值/最小长度 |
| max | `number` | 最大值/最大长度 |
| pattern | `RegExp` | 正则验证 |
| validator | `(value: any) => boolean \| string` | 自定义验证函数 |

### 方法

| 方法名 | 参数 | 说明 |
|--------|------|------|
| validate | `(callback: (valid: boolean, errors: ClFormValidateError[]) => void)` | 验证整个表单 |
| validateField | `(prop: string)` | 验证单个字段 |
| clearValidate | - | 清除所有验证 |
| getValue | `(prop: string)` | 获取字段值 |
| setError | `(prop: string, error: string)` | 设置字段错误信息 |
| getError | `(prop: string)` | 获取字段错误信息 |

### 插槽

| 插槽名 | 说明 |
|--------|------|
| default | 表单项内容 |

### 使用示例

```html
<script setup lang="ts">
import { ref } from "vue";

const form = ref({
  username: "",
  password: ""
});

const rules = new Map([
  ["username", [{ required: true, message: t("请输入用户名") }]],
  ["password", [{ required: true, message: t("请输入密码") }, { min: 6, message: t("密码至少6位") }]]
]);

function onSubmit() {
  formRef.value?.validate((valid, errors) => {
    if (valid) {
      // 提交表单
    }
  });
}
</script>

<template>
  <cl-form ref="formRef" v-model="form" :rules="rules">
    <cl-form-item prop="username" :label="t('用户名')">
      <cl-input v-model="form.username" :placeholder="t('请输入用户名')"></cl-input>
    </cl-form-item>
    
    <cl-form-item prop="password" :label="t('密码')">
      <cl-input v-model="form.password" type="password" :placeholder="t('请输入密码')"></cl-input>
    </cl-form-item>
    
    <cl-button @tap="onSubmit">{{ t('提交') }}</cl-button>
  </cl-form>
</template>
```

---

## cl-form-item

### 概述

表单项容器，配合 `cl-form` 完成字段级校验与错误提示。

### 属性

| 属性名 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| pt | `Object` | `{}` | 样式穿透 |
| prop | `string` | `''` | 字段名 |
| label | `string` | `''` | 标签文案 |
| rules | `ClFormRule[]` | `[]` | 校验规则 |

### 插槽

| 插槽名 | 说明 |
|--------|------|
| default | 表单控件 |

### 使用示例

```html
<cl-form-item prop="username" :label="t('用户名')" :rules="[{ required: true, message: t('必填') }]">
  <cl-input v-model="form.username"></cl-input>
</cl-form-item>
```

---

## cl-input

### 概述

单行文本输入组件，支持多种输入类型、密码模式、清除按钮、图标等。

### 属性

| 属性名 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| pt | `Object` | `{}` | 样式穿透 |
| modelValue | `string` | `''` | 绑定值 |
| type | `string` | `'text'` | 输入类型：`text`、`password`、`number`、`digit`、`idcard` |
| placeholder | `string` | `t('请输入')` | 占位提示 |
| prefixIcon | `string` | `''` | 前缀图标 |
| suffixIcon | `string` | `''` | 后缀图标 |
| password | `boolean` | `false` | 是否密码框 |
| autofocus | `boolean` | `false` | 是否自动聚焦 |
| disabled | `boolean` | `false` | 是否禁用 |
| readonly | `boolean` | `null` | 是否只读 |
| border | `boolean` | `true` | 是否显示边框 |
| clearable | `boolean` | `false` | 是否可清除 |
| maxlength | `number` | `140` | 最大输入长度 |
| cursorSpacing | `number` | `5` | 光标与键盘的距离 |
| confirmHold | `boolean` | `false` | 点击键盘确认按钮时是否保持键盘不收起 |
| confirmType | `string` | `'done'` | 设置键盘右下角按钮的文字：`done`、`go`、`next`、`search`、`send` |
| adjustPosition | `boolean` | `true` | 键盘弹起时，是否自动上推页面 |
| holdKeyboard | `boolean` | `false` | 是否保持键盘不收起 |
| precision | `number` | `0` | 保留精度 |

### 事件

| 事件名 | 说明 |
|--------|------|
| update:modelValue | 更新绑定值 |
| input | 输入事件 |
| change | 值改变事件 |
| focus | 获取焦点 |
| blur | 失去焦点 |
| confirm | 点击确认按钮 |
| clear | 清除事件 |
| keyboardheightchange | 键盘高度变化 |

### 插槽

| 插槽名 | 说明 |
|--------|------|
| prepend | 前置内容 |
| append | 后置内容 |

### 方法

| 方法名 | 说明 |
|--------|------|
| focus | 聚焦 |
| clear | 清除 |

### PassThrough

| 节点 | 说明 |
|------|------|
| className | 根元素样式 |
| inner | 输入框元素 |
| prefixIcon | 前缀图标（支持 ClIconProps） |
| suffixIcon | 后缀图标（支持 ClIconProps） |

### 使用示例

```html
<!-- 基础用法 -->
<cl-input v-model="value" :placeholder="t('请输入')"></cl-input>

<!-- 密码输入 -->
<cl-input v-model="password" type="password" :placeholder="t('请输入密码')"></cl-input>

<!-- 可清除 -->
<cl-input v-model="value" clearable :placeholder="t('可清除')"></cl-input>

<!-- 带图标 -->
<cl-input v-model="value" prefix-icon="search-line" :placeholder="t('搜索')"></cl-input>

<!-- 数字输入 -->
<cl-input v-model="number" type="number" :placeholder="t('请输入数字')"></cl-input>
```

---

## cl-select

### 概述

选择器组件，支持单列、多列、联动选择。

### 属性

| 属性名 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| pt | `Object` | `{}` | 样式穿透 |
| modelValue | `any \| any[]` | `null` | 选择器的值 |
| title | `string` | `t('请选择')` | 选择器标题 |
| placeholder | `string` | `t('请选择')` | 选择器占位符 |
| options | `ClSelectOption[]` | `[]` | 选项数据 |
| showTrigger | `boolean` | `true` | 是否显示选择器触发器 |
| disabled | `boolean` | `false` | 是否禁用选择器 |
| columnCount | `number` | `1` | 列数 |
| splitor | `string` | `' - '` | 分隔符 |
| confirmText | `string` | `t('确定')` | 确认按钮文本 |
| showConfirm | `boolean` | `true` | 是否显示确认按钮 |
| cancelText | `string` | `t('取消')` | 取消按钮文本 |
| showCancel | `boolean` | `true` | 是否显示取消按钮 |

### ClSelectOption

| 属性名 | 类型 | 说明 |
|--------|------|------|
| label | `string` | 选项标签 |
| value | `any` | 选项值 |
| children | `ClSelectOption[]` | 子选项（联动时使用） |

### 事件

| 事件名 | 说明 |
|--------|------|
| update:modelValue | 更新绑定值 |
| change | 选择完成 |
| changing | 滚动选择中 |

### 插槽

| 插槽名 | 说明 |
|--------|------|
| prepend | 弹窗前置内容 |
| append | 弹窗后置内容 |

### 方法

| 方法名 | 说明 |
|--------|------|
| open | 打开选择器 |
| close | 关闭选择器 |

### PassThrough

| 节点 | 说明 |
|------|------|
| trigger | 触发器（支持 ClSelectTriggerPassThrough） |
| popup | 弹窗（支持 ClPopupPassThrough） |

### 使用示例

```html
<script setup lang="ts">
const city = ref(null);
const cities = [
  { label: "北京", value: "beijing" },
  { label: "上海", value: "shanghai" },
  { label: "广州", value: "guangzhou" }
];
</script>

<template>
  <!-- 单列选择 -->
  <cl-select v-model="city" :options="cities" :placeholder="t('请选择城市')"></cl-select>
</template>
```

---

## cl-checkbox

### 概述

多选框组件，支持数组值和布尔值两种模式。

### 属性

| 属性名 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| pt | `Object` | `{}` | 样式穿透 |
| modelValue | `any[] \| boolean` | `[]` | 绑定值 |
| label | `string` | `''` | 标签文本 |
| value | `any` | - | 选项值（数组模式时使用） |
| disabled | `boolean` | `false` | 是否禁用 |
| activeIcon | `string` | `'checkbox-line'` | 选中时的图标 |
| inactiveIcon | `string` | `'checkbox-blank-line'` | 未选中时的图标 |
| showIcon | `boolean` | `true` | 是否显示图标 |

### 事件

| 事件名 | 说明 |
|--------|------|
| update:modelValue | 更新绑定值 |
| change | 值改变事件 |

### 插槽

| 插槽名 | 说明 |
|--------|------|
| default | 标签内容 |

### PassThrough

| 节点 | 说明 |
|------|------|
| className | 根元素样式 |
| icon | 图标（支持 ClIconProps） |
| label | 标签（支持 ClTextProps） |

### 使用示例

```html
<!-- 布尔模式 -->
<cl-checkbox v-model="checked">{{ t('同意协议') }}</cl-checkbox>

<!-- 数组模式 -->
<cl-checkbox v-model="selected" value="apple">{{ t('苹果') }}</cl-checkbox>
<cl-checkbox v-model="selected" value="banana">{{ t('香蕉') }}</cl-checkbox>
```

---

## cl-radio

### 概述

单选框组件，需配合 `cl-radio-group` 使用。

### 属性

| 属性名 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| pt | `Object` | `{}` | 样式穿透 |
| modelValue | `any` | - | 绑定值 |
| label | `string` | `''` | 标签文本 |
| value | `any` | - | 选项值 |
| disabled | `boolean` | `false` | 是否禁用 |
| activeIcon | `string` | `'radio-button-line'` | 选中时的图标 |
| inactiveIcon | `string` | `'radio-button-blank-line'` | 未选中时的图标 |

### 事件

| 事件名 | 说明 |
|--------|------|
| update:modelValue | 更新绑定值 |
| change | 值改变事件 |

### 插槽

| 插槽名 | 说明 |
|--------|------|
| default | 标签内容 |

### 使用示例

```html
<cl-radio-group v-model="gender">
  <cl-radio value="male">{{ t('男') }}</cl-radio>
  <cl-radio value="female">{{ t('女') }}</cl-radio>
</cl-radio-group>
```

---

## cl-switch

### 概述

开关组件。

### 属性

| 属性名 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| pt | `Object` | `{}` | 样式穿透 |
| modelValue | `boolean` | `false` | 绑定值 |
| disabled | `boolean` | `false` | 是否禁用 |
| loading | `boolean` | `false` | 加载状态 |
| size | `string` | `'normal'` | 尺寸：`small`、`normal`、`large` |
| activeColor | `string` | - | 打开时的颜色 |
| inactiveColor | `string` | - | 关闭时的颜色 |

### 事件

| 事件名 | 说明 |
|--------|------|
| update:modelValue | 更新绑定值 |
| change | 值改变事件 |

### 使用示例

```html
<cl-switch v-model="enabled"></cl-switch>
```

---

## cl-rate

### 概述

评分组件。

### 属性

| 属性名 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| pt | `Object` | `{}` | 样式穿透 |
| modelValue | `number` | `0` | 绑定值 |
| max | `number` | `5` | 最大分值 |
| size | `number` | `20` | 图标大小 |
| allowHalf | `boolean` | `false` | 是否允许半选 |
| disabled | `boolean` | `false` | 是否禁用 |
| readonly | `boolean` | `false` | 是否只读 |
| activeColor | `string` | - | 选中颜色 |
| inactiveColor | `string` | - | 未选中颜色 |
| icon | `string` | `'star-fill'` | 图标名称 |

### 事件

| 事件名 | 说明 |
|--------|------|
| update:modelValue | 更新绑定值 |
| change | 值改变事件 |

### 使用示例

```html
<cl-rate v-model="score"></cl-rate>
```

---

## cl-slider

### 概述

滑块组件。

### 属性

| 属性名 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| pt | `Object` | `{}` | 样式穿透 |
| modelValue | `number` | `0` | 绑定值 |
| min | `number` | `0` | 最小值 |
| max | `number` | `100` | 最大值 |
| step | `number` | `1` | 步长 |
| disabled | `boolean` | `false` | 是否禁用 |
| showValue | `boolean` | `false` | 是否显示当前值 |
| activeColor | `string` | - | 选中颜色 |
| inactiveColor | `string` | - | 未选中颜色 |

### 事件

| 事件名 | 说明 |
|--------|------|
| update:modelValue | 更新绑定值 |
| change | 值改变事件 |
| changing | 拖动中事件 |

### 使用示例

```html
<cl-slider v-model="volume" :min="0" :max="100"></cl-slider>
```

---

## cl-input-number

### 概述

计数器组件。

### 属性

| 属性名 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| pt | `Object` | `{}` | 样式穿透 |
| modelValue | `number` | `0` | 绑定值 |
| min | `number` | `0` | 最小值 |
| max | `number` | `999` | 最大值 |
| step | `number` | `1` | 步长 |
| disabled | `boolean` | `false` | 是否禁用 |

### 事件

| 事件名 | 说明 |
|--------|------|
| update:modelValue | 更新绑定值 |
| change | 值改变事件 |

### 使用示例

```html
<cl-input-number v-model="count" :min="1" :max="99"></cl-input-number>
```

---

## cl-input-otp

### 概述

验证码输入组件。

### 属性

| 属性名 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| pt | `Object` | `{}` | 样式穿透 |
| modelValue | `string` | `''` | 绑定值 |
| length | `number` | `6` | 输入框数量 |
| type | `string` | `'number'` | 输入类型：`number`、`text` |
| disabled | `boolean` | `false` | 是否禁用 |
| autofocus | `boolean` | `true` | 是否自动聚焦 |

### 事件

| 事件名 | 说明 |
|--------|------|
| update:modelValue | 更新绑定值 |
| complete | 输入完成 |

### 使用示例

```html
<cl-input-otp v-model="code" :length="6" @complete="onComplete"></cl-input-otp>
```

---

## cl-select-date

### 概述

日期选择器。

### 属性

| 属性名 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| pt | `Object` | `{}` | 样式穿透 |
| modelValue | `string` | `''` | 绑定值 |
| title | `string` | `t('选择日期')` | 标题 |
| placeholder | `string` | `t('请选择日期')` | 占位提示 |
| format | `string` | `'yyyy-MM-dd'` | 日期格式 |
| minDate | `string` | - | 最小日期 |
| maxDate | `string` | - | 最大日期 |
| disabled | `boolean` | `false` | 是否禁用 |

### 事件

| 事件名 | 说明 |
|--------|------|
| update:modelValue | 更新绑定值 |
| change | 选择完成 |

### 使用示例

```html
<cl-select-date v-model="birthday" :placeholder="t('请选择出生日期')"></cl-select-date>
```

---

## cl-select-time

### 概述

时间选择器。

### 属性

| 属性名 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| pt | `Object` | `{}` | 样式穿透 |
| modelValue | `string` | `''` | 绑定值 |
| title | `string` | `t('选择时间')` | 标题 |
| placeholder | `string` | `t('请选择时间')` | 占位提示 |
| format | `string` | `'HH:mm'` | 时间格式 |
| disabled | `boolean` | `false` | 是否禁用 |

### 事件

| 事件名 | 说明 |
|--------|------|
| update:modelValue | 更新绑定值 |
| change | 选择完成 |

### 使用示例

```html
<cl-select-time v-model="time" :placeholder="t('请选择时间')"></cl-select-time>
```

---

## cl-calendar

### 概述

日历组件。

### 属性

| 属性名 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| pt | `Object` | `{}` | 样式穿透 |
| modelValue | `string \| string[]` | - | 绑定值 |
| mode | `string` | `'single'` | 选择模式：`single`、`multiple`、`range` |
| minDate | `string` | - | 最小日期 |
| maxDate | `string` | - | 最大日期 |
| firstDayOfWeek | `number` | `1` | 每周第一天 |

### 事件

| 事件名 | 说明 |
|--------|------|
| update:modelValue | 更新绑定值 |
| change | 选择完成 |

### 使用示例

```html
<cl-calendar v-model="date" mode="single"></cl-calendar>
```

---

## cl-cascader

### 概述

级联选择器。

### 属性

| 属性名 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| pt | `Object` | `{}` | 样式穿透 |
| modelValue | `any[]` | `[]` | 绑定值 |
| options | `ClCascaderOption[]` | `[]` | 选项数据 |
| placeholder | `string` | `t('请选择')` | 占位提示 |
| disabled | `boolean` | `false` | 是否禁用 |

### 事件

| 事件名 | 说明 |
|--------|------|
| update:modelValue | 更新绑定值 |
| change | 选择完成 |

### 使用示例

```html
<cl-cascader v-model="address" :options="regions"></cl-cascader>
```

---

## cl-upload

### 概述

文件上传组件。

### 属性

| 属性名 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| pt | `Object` | `{}` | 样式穿透 |
| modelValue | `any[]` | `[]` | 文件列表 |
| action | `string` | - | 上传地址 |
| maxCount | `number` | `1` | 最大上传数量 |
| maxSize | `number` | `10` | 最大文件大小（MB） |
| accept | `string` | `'image'` | 接受的文件类型 |
| disabled | `boolean` | `false` | 是否禁用 |

### 事件

| 事件名 | 说明 |
|--------|------|
| update:modelValue | 更新文件列表 |
| success | 上传成功 |
| fail | 上传失败 |
| remove | 移除文件 |

### 使用示例

```html
<cl-upload v-model="files" :max-count="3"></cl-upload>
```
