# admin-ui 管理端功能速查

> 开发管理端页面时，遇到不确定的组件用法、属性、事件等问题，优先查阅本文档。

---

## 全局常量 CoreConsts

> 来源：`@/components/CoreConsts.js`，手动调用接口判断成功时**必须使用常量**，禁止硬编码。

| 常量 | 值 | 用途 |
|------|-----|------|
| `SuccessCode` | `200` | 接口成功码（禁止硬编码200） |
| `DefaultRowKey` | `'id'` | 默认行唯一标识 |
| `PageSize` | `'size'` | 分页条数字段名 |
| `PageCurrent` | `'current'` | 分页页数字段名 |
| `AccessToken` | `'access_token'` | Token键名 |
| `PrimaryUid` | `'UView'` | 主视图uid前缀（跨视图引用时用） |
| `DefaultEditUid` | `'UVEdit'` | 编辑组件默认uid |
| `DefaultTableUid` | `'UVTable'` | 表格组件默认uid |
| `DefaultSearchUid` | `'UVSearch'` | 搜索组件默认uid |

```js
import CoreConsts from '@/components/CoreConsts'
if (code === CoreConsts.SuccessCode) { ... }  // ✅ 正确
if (code === 200) { ... }                       // ❌ 禁止
```

---

## HTTP 请求

> 来源：`@/utils/request.js`，基于 Axios 封装

| 方法 | 签名 | 说明 |
|------|------|------|
| `GET` | `GET(url, data?, config?)` | data 作为 params 传入 |
| `POST` | `POST(url, data?, config?)` | data 作为 body 传入 |
| `PUT` | `PUT(url, data?, config?)` | 同上 |
| `PATCH` | `PATCH(url, data?, config?)` | 同上 |
| `DELETE` | `DELETE(url, data?, config?)` | 同上 |

- **响应码处理**：200→resolve，401→跳转登录，其他→reject
- **Token**：自动从 localStorage 读取注入请求头
- **参数序列化**：使用 qs，支持 `allowDots: true`（嵌套对象用点号）

---

## 组件体系总览

**命名规律**：`UView` 结尾 = 视图容器（提供上下文）；`UViewXx` 结尾 = 视图内CRUD组件（必须在视图容器内）

| 组件 | 上下文 | uid默认 | v-model | 事件 | 何时使用 |
|------|--------|---------|---------|------|---------|
| `UView` | ViewContext | `UView` | - | - | 页面顶级容器，**CRUD页面必须以此包裹** |
| `UViewSearch` | SearchContext | `UVSearch` | 搜索模型Object | - | 搜索栏 |
| `UViewTable` | TableContext | `UVTable` | - | - | 数据表格 |
| `UViewDrawer` | EditContext | `UVEdit` | 编辑模型Object | `@open` `@edit` | 主编辑抽屉（字段多） |
| `UViewModal` | EditContext | `UVEdit` | 编辑模型Object | `@open` `@edit` | 主编辑弹窗（字段少） |
| `UFormDrawer` | EditContext | **必填** | 编辑模型Object | `@open` `@edit` | 额外编辑抽屉 |
| `UFormModal` | EditContext | **必填** | 编辑模型Object | `@open` `@edit` | 额外编辑弹窗 |
| `UDrawer` | VisibleContext | **必填** | - | `@open` | 纯展示抽屉 |
| `UModal` | VisibleContext | **必填** | - | `@open` | 纯展示弹窗 |
| `UChildView` | 独立上下文 | **必填** | - | - | 子页面（Tab/主从） |
| `UDrawerView` | 独立上下文 | **必填** | - | `@open` | 抽屉内完整CRUD |
| `UModalView` | 独立上下文 | **必填** | - | `@open` | 弹窗内完整CRUD |
| `UViewEditable` | EditContext | `UVTable` | - | - | 行内可编辑表格 |

> 显隐由内部 `visible` ref 控制，无需手动绑定。`UChildView` 内部uid与外部不冲突。

---

## UTag / UButton func 速查

| func | 行为 | 默认方法 | 常用属性 |
|------|------|---------|----------|
| `add` | 打开编辑框（新增） | - | - |
| `edit` | 打开编辑框（编辑） | GET | `url`（获取详情接口） |
| `del` | 确认后删除 | POST | - |
| `query` | 触发查询 | GET | `url`（查询接口） |
| `submit` | 提交表单 | POST | `url`（保存接口） |
| `cancel` | 关闭编辑框 | - | - |
| `reset` | 重置表单 | - | - |
| `open` | 打开 UDrawer/UModal | - | `uid`（目标组件uid） |
| `ajax` | 自定义 HTTP 请求 | POST | `url`、`method`、`params`、`config` |
| `ajax:confirm` | 确认后执行HTTP请求 | POST | 同 `ajax`，`config.confirmTitle` |
| `download` | 文件下载 | POST | - |
| `export` | 导出文件 | GET | - |
| `import` | 导入文件 | POST | - |
| `detail` | 打开详情 | GET | - |

**共用属性**：`func`（必填）、`url`、`method`、`params`、`reload`（默认true）、`config`、`callback`
**UTag额外**：`data`（必填）、`uid`、`color`、`disabled`（支持函数）

### callback 回调速查

UTag/UButton 的 `callback` 属性用于在操作完成后执行自定义逻辑。不同 func 的回调参数不同：

| func | callback 签名 | 参数说明 |
|------|---------------|----------|
| `submit` | `callback({code, message, data, editContext, query})` | `editContext`编辑上下文，`query()`刷新列表函数。**必须返回Promise** |
| `del` | `callback({code, message, data}, params)` | `params`是实际发送的请求参数 |
| `query` | `callback({code, message, data})` | `data`是查询响应数据（分页对象或数组） |
| `ajax` / `ajax:confirm` | `callback({code, message, data}, params)` | 同del，`params`是实际请求参数 |
| `add` | 无callback | 打开编辑框，触发`@edit`事件 |
| `edit` | 无callback | 打开编辑框并加载详情，触发`@edit`事件 |
| `open` | 无callback | 打开抽屉/弹窗，触发`@open`事件 |
| `cancel` | 无callback | 关闭编辑框 |
| `reset` | 无callback | 重置表单 |

**重要**：`submit` 的 callback **必须返回 Promise**，框架会等待 resolve 后才执行默认行为（成功提示→关闭编辑框→刷新列表）。返回 `Promise.reject()` 可阻止默认行为。

```vue
<!-- submit callback 示例：提交后自定义处理 -->
<UButton func="submit" url="/api/save" :callback="onSubmitCallback">提交</UButton>

<script>
// 必须返回Promise
function onSubmitCallback({code, message, data, editContext, query}) {
  return new Promise((resolve, reject) => {
    if (code === 200) {
      // 自定义成功逻辑，比如更新其他数据
      doSomething(data);
      resolve(); // 继续执行默认行为（关闭+刷新）
    } else {
      reject(); // 阻止默认行为
    }
  });
}

// del callback 示例
function onDelCallback({code, message, data}, params) {
  if (code === 200) {
    // 删除成功后的自定义逻辑
  }
}
</script>
```

### func 子修饰符

| 修饰符 | 用法 | 说明 |
|--------|------|------|
| `edit:set` | `func="edit:set"` + `uid` | 打开额外编辑弹窗，设置模式 |
| `edit:loading:uid` | `func="edit:loading:xxxUid"` | 打开指定uid编辑框并加载数据 |
| `del:batch` | `func="del:batch"` | 批量删除（需表格启用行选择） |
| `add:child` | `func="add:child"` + `config.pid` | 新增子记录，自动设置父ID |

### ajax:confirm 示例

```vue
<UTag func="ajax:confirm" :data="record" url="/api/toggle" method="POST"
  :params="getParams" :config="getConfig(record)"
>{{ record.status === 'running' ? '取消' : '开始' }}</UTag>

<script>
const getParams = (data, funcPath, rowKey) => Promise.resolve({ id: data[rowKey] })
const getConfig = (record) => record.status === 'running'
  ? { confirmTitle: '确定取消？' } : { confirmTitle: '确定开始？' }
</script>
```

---

## 事件速查

| 组件类型 | 事件 | 触发时机 | 用途 |
|---------|------|---------|------|
| 有表单（Drawer/Modal） | `@edit(model)` | 获取详情后 | 初始化字段值，**默认用此** |
| 有表单（Drawer/Modal） | `@open(model)` | 打开时立即 | 记录当前行（编辑时先open再edit） |
| 无表单（Drawer/Modal/View） | `@open(row)` | 打开时 | 记录当前行、初始化子视图参数 |
| 数据源组件 | `@change(value, select)` | 选中项变更 | 联动选择、填充关联字段，**优先使用** |
| 数据源组件 | `@loaded(sourceMap)` | 数据加载完成 | 非事件上下文取关联数据（如@edit回调中） |

### 数据源事件详解

凡有 `options`/`url`/`dict` 的表单项组件均支持：

```vue
<USelectItem field="productId" url="/iot/product/list" labelField="name" valueField="id"
             @change="productChange" @loaded="productLoaded"/>
```
```js
// 优先：@change 获取当前选中项完整数据(数据改变后触发)
function productChange(value, select) {
  editModel.protocolId = select['protocolId'];
}

// 辅助：@loaded 获取完整映射，用于非事件上下文(数据加载完成后触发)
function productLoaded(map) {
  productSourceMap.value = map;  // {id1: {name:'产品A', ...}, id2: {...}}
}
```

---

## 跨视图引用速查

| 场景 | uid 格式 | 示例 |
|------|---------|------|
| 同一视图内 | `uid="xxx"` | `uid="editDrawer"` |
| 跨视图引用 | `uid="前缀:xxx"` | `uid="UView:paramDrawer"` |

> 目标组件的 uid 始终用简单格式，只有 UTag/UButton 引用时用 fullUid。
> 查找机制：先按本地视图查找 → 未找到且uid含`:`则查全局注册表。

---

## 表格相关

### UViewTable 关键属性

| 功能 | 属性/配置 | 说明 |
|------|---------|------|
| 横/纵向滚动 | `:scroll="{x: 1200, y: 600}"` | **必须设置** |
| 关闭分页 | `:pagination="false"` | 默认分页 |
| 多选框 | `:rowSelection="{}"` | 配合 `del:batch` |
| 列字段 | `title`、`field`（支持`a.b`嵌套）、`width`、`fixed`、`type`、`dict`、`url`、`options`、`formatter`、`ellipsis` | - |
| 插槽 | `#func` 顶部按钮区、`#action="{record}"` 行操作、`#{field}` 自定义列渲染 | - |

### 列值转换

| 数据源 | 列配置 | 示例 |
|--------|--------|------|
| 静态选项 | `options` | `{ field: 'status', options: [{label:'启用', value:1}] }` |
| 字典数据 | `dict` | `{ field: 'status', dict: 'sys_status' }` |
| 关联数据 | `url` + `labelField`/`valueField` | `{ field: 'productId', url: '/product/list', labelField: 'name', valueField: 'id' }` |

> 禁止在 `#{field}` 插槽中手动编写 value→label 转换逻辑。

---

## 编辑容器（Drawer/Modal）

### 选择决策

| 场景 | 组件 | 默认宽度 |
|------|------|---------|
| 字段多（>6）、含关联子表、需参照主列表 | `UViewDrawer` | 980 |
| 字段少（≤6）、简单表单、确认类操作 | `UViewModal` | 680 |

### 关键属性

| 属性 | 说明 |
|------|------|
| `v-model` | 绑定编辑模型 |
| `width` | Drawer默认680，Modal默认520 |
| `rules` | 校验规则 |
| `:span="[6, 16]"` | **必须配置**，label与wrapper比例 |
| `placement` | Drawer专属，默认左 |
| 插槽 | `#default({model})`、`#footer({model})`、`#title({model, func})` |

---

## 表单项组件速查

### 字段类型映射（后端 → 前端）

| 后端字段 | 列定义 | 表单项组件 |
|---------|--------|-----------|
| 文本 | `{ title, field, width }` | `UInputItem` |
| 长文本 | `{ field, ellipsis: true }` | `UTextareaItem` |
| 数字/金额 | `{ field, align: 'right' }` | `UInputNumberItem` |
| 日期时间 | `{ field, type: 'date' }` | `UDateTimeItem` / `UDateItem` |
| 时间范围 | - | `URangePickerItem` / `URangeItem` |
| 状态/类型（字典） | `{ field, dict: '字典' }` | `URadioItem` / `USelectItem` |
| 关联数据（外键） | `{ field, url: '/xxx/list' }` | `USelectItem`（url加载） |
| 树形关联 | - | `UTreeSelectItem` |
| 布尔值 | `{ field, dict: '是/否字典' }` | `USwitchItem` |
| 多选 | - | `UCheckboxItem` |
| 密码 | - | `UInputPasswordItem` |
| 富文本 | - | `UEditorItem` |
| Markdown | - | `UMarkdownItem` |
| 评分 | - | `URateItem` |
| 滑块 | - | `USliderItem` |
| 级联 | - | `UCascaderItem` |
| 自动补全 | - | `UAutoCompleteItem` |
| 穿梭框 | - | `UTransferItem` |
| 分段控制器 | - | `USegmentedItem` |
| 文件上传 | - | `UUploadItem` |
| 时间 | - | `UTimePickerItem` |
| Cron表达式 | - | `UCron` |
| 图片裁剪 | - | `UCropper` |

### 表单项通用属性

`field`（必填）、`label`、`required`、`span`、`disabled`（支持函数）、`dict`、`url`（支持 `{field}` 动态变量）、`options`、`labelField`/`valueField`（默认 `label`/`value`）、`defaultValue`

### 关联数据用法

```vue
<USelectItem field="productId" label="所属产品" url="/iot/product/list" labelField="name" valueField="id"/>
<!-- url支持动态变量，当productId变化时自动重新加载 -->
<USelectItem field="attrIds" label="模型属性" url="/iot/modelAttr/listByProductId?productId={config.productId}" labelField="name" valueField="id"/>
<!-- 树形 -->
<UTreeSelectItem field="orgId" label="所属部门" url="/core/org/parent" labelField="name" valueField="id"/>
```

> url 中的 `{field}` 会被自动解析为编辑模型中对应字段的值，字段变化时自动重新请求数据。

---

## 代码编辑器组件速查

### AceScriptEditor - 代码脚本编辑器

用于编辑JavaScript、Java、SQL等编程语言的代码片段，支持代码补全和语法高亮。

| 属性 | 类型 | 默认值 | 说明 |
|------|------|--------|------|
| `value` / `v-model:value` | string | '' | 编辑器的初始内容 |
| `completions` | array | [] | 代码补全建议数组 |
| `theme` | string | 'monokai' | 编辑器主题 |
| `lang` | string | 'javascript' | 编程语言 |
| `options` | object | {} | ACE编辑器配置选项 |

基本用法：
```vue
<AceScriptEditor v-model:value="scriptCode" style="height: 420px"/>
```

高级用法 - 代码补全：
```vue
<AceScriptEditor v-model:value="scriptCode" :completions="completions" />
<script>
import {ref} from "vue";
export default {
  setup() {
    let scriptCode = ref('');
    let completions = ref([
      {name: 'console', value: 'console.log()', score: 100, meta: 'console'},
      {name: 'alert', value: 'alert()', score: 95, meta: 'alert'},
      {name: 'fetch', value: 'fetch(url)', score: 90, meta: 'fetch'}
    ]);
    return {scriptCode, completions}
  }
}
</script>
```

实际应用示例 - 多语言脚本编辑：
```vue
<ATabs v-model:activeKey="languageKey" tab-position="left">
  <a-tab-pane :tab="language.language">
    <AceScriptEditor v-model:value="language.script" 
                     :completions="completions" 
                     style="height: 420px"/>
  </a-tab-pane>
</ATabs>
```

### UEditor - 富文本编辑器

用于编辑富文本内容，支持HTML格式，基于wangeditor实现。

```vue
<UEditor v-model="htmlContent" />
```

### UMarkdown - Markdown编辑器

用于编辑Markdown格式文档，基于md-editor-v3实现。

| 属性 | 类型 | 默认值 | 说明 |
|------|------|--------|------|
| `placeholder` | string | '' | 输入提示文字 |
| `showCodeRowNumber` | boolean | true | 是否显示代码行号 |
| `toolbarsExclude` | array | ['github', 'mermaid', 'save', 'catalog'] | 排除的工具栏按钮 |

```vue
<UMarkdown v-model="markdownContent" />
```

### 表单中的编辑器用法

在 `UViewDrawer` 或 `UViewModal` 中使用编辑器：
```vue
<UViewDrawer v-model="editModel" title="脚本管理" :rules="rules" @edit="openEdit" placement="top" height="100%">
  <template #default="{model}">
    <URow col="drawer">
      <UInputItem field="name" label="名称"/>
      <URadioItem field="language" label="语言" :options="languageOptions" />
    </URow>
    <ATabs v-model:activeKey="languageKey" tab-position="left">
      <a-tab-pane :tab="lang" v-for="lang in ['JavaScript', 'Python', 'SQL']">
        <div style="height: 320px">
          <AceScriptEditor v-model:value="model.scripts[lang]" 
                           :completions="completions[lang]"/>
        </div>
      </a-tab-pane>
    </ATabs>
  </template>
</UViewDrawer>
```

> **注意**：编辑器组件需要在 `main.js` 中注册为全局组件。所有编辑器组件都支持 v-model 双向绑定。


---

## URow 布局速查

| 用法 | 属性 | 说明 |
|------|------|------|
| 预设断点 | `col="search"` / `"modal"` / `"drawer"` | search每行4列、modal/drawer 3列 |
| 按列数 | `:responsive="2"` | 1=单列、2=两列、3=三列 |
| 固定span | `:span="24"` | 指定固定值 |
| 列间距 | `:gutter="16"` | 列间距 |
| 自定义 | `{xs:24, sm:24, md:12, lg:12, xl:8, xxl:8}` | 响应式配置 |

### 响应式预设值

| 预设 | xs | sm | md | lg | xl | xxl |
|------|----|----|----|----|----|-----|
| search | 24 | 24 | 12 | 8 | 8 | 6 |
| modal | 24 | 24 | 12 | 12 | 12 | 8 |
| drawer | 24 | 24 | 12 | 12 | 8 | 8 |

---

## 组合组件：UDrawerView / UModalView

`UDrawerView` = UDrawer + UChildView，`UModalView` = UModal + UChildView。uid 同时传给两者，rowKey 默认 id。

```vue
<UDrawerView uid="xxx" title="xxx" @open="onOpen" rowKey="id">
  <UViewSearch>...</UViewSearch>
  <UViewTable>...</UViewTable>
  <UViewModal>...</UViewModal>
</UDrawerView>
```

---

## 全局实例属性

| 属性 | 类型 | 说明 |
|------|------|------|
| `this.$http` | Axios实例 | 直接发HTTP请求 |
| `this.$msg` | Message | 消息提示（success/error/warning/info） |
| `this.$notify` | Notification | 通知提醒框 |
| `this.$env` | Object | 环境配置 |
| `this.$apiUrl` | Object | API地址配置 |

---

## 常见问题

**Q: 如何实现状态切换按钮（如开始/取消）？**
A: 用 `func="ajax:confirm"` + `params` 函数动态计算目标状态：
```vue
<UTag func="ajax:confirm" :data="record" url="/api/toggle" method="POST"
  :params="getParams" :config="getConfig(record)"
>{{ record.status === 'running' ? '取消' : '开始' }}</UTag>
```

**Q: 表格操作列放不下？**
A: `action` 列 `width` 建议设为 `220+`，并用 `fixed: 'right'` 固定。

**Q: UViewDrawer/UViewModal 的 @open 和 @edit 有什么区别？**
A: `@open` 打开时立即触发（参数是当前行数据），`@edit` 是获取详情后触发（参数是编辑模型）。初始化字段用 `@edit`，记录行用 `@open`。

**Q: 如何在 @edit 回调中获取关联表的完整数据？**
A: 用 `@loaded(sourceMap)` 保存映射，在 `@edit` 中通过 `sourceMap[value]` 取数据。

**Q: 手动调用接口时如何判断成功？**
A: 必须使用已定义的常量 `CoreConsts.SuccessCode`（值为 200），禁止硬编码：
```js
import CoreConsts from '@/components/CoreConsts'
if (code === CoreConsts.SuccessCode) { ... }  // ✅
if (code === 200) { ... }                       // ❌
```

**Q: url 中的动态变量怎么用？**
A: 用 `{field}` 语法，字段变化时自动重新加载：`url="/xxx/list?pid={parentId}"`

**Q: @change 回调的 select 参数是什么？**
A: `select` 是当前选中项的**完整数据对象**（不是label，是原始数据），可直接取关联字段：`select.protocolId`
