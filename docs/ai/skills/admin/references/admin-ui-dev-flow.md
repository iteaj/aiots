# admin-ui 管理端开发流程

> 页面完整模板见：[page-template.vue](./page-template.vue)

---

## 一、开发流程与检查清单

```
1. 后端接口开发（Controller/Service/Mapper）
        ↓
2. 新建页面：views/{模块}/{功能}/index.vue
        ↓
3. 复制模板，按需修改（搜索项、列定义、表单项、接口路径、权限标识、子视图）
        ↓
4. 后台菜单管理中添加菜单，URL 填写 {模块}/{功能}
```

**代码生成检查清单**：

- [ ] **组件 name** 全局唯一，建议 `{模块}{功能}`
- [ ] **权限标识** `v-auth` 格式为 `{模块}:{功能}:{操作}`，与后台菜单配置保持一致
- [ ] **接口路径** 所有 `url` 与后端 Controller 路径完全匹配
- [ ] **列定义** 覆盖实体所有展示字段，类型映射正确；`width` 必填（`action` 列建议 220，其他列最小 100）
- [ ] **表单项** 覆盖实体所有可编辑字段：关联数据用 `url` 指向列表接口并配置 `labelField`/`valueField`；字典用 `dict`；静态选项用 `options`
- [ ] **校验规则** `rules` 与后端 `@NotNull`/`@NotBlank` 等校验注解对应
- [ ] **uid 唯一** 同一视图上下文内不得有重复 uid（默认：`UVSearch` / `UVTable` / `UVEdit`）
- [ ] **子视图** `UChildView`/`UDrawerView`/`UModalView` 嵌套时 uid 全局唯一，内部接口 url 正确关联主记录
- [ ] **跨视图引用** UTag/UButton 引用其他视图内的组件时，uid 须使用 fullUid 格式（如 `UView:paramDrawer`）；目标组件自身 uid 保持简单格式
- [ ] **编辑容器选择** Drawer vs Modal 按场景选用：
  - **Drawer（抽屉）**：字段多（>6）、含关联子表、需参照主列表、宽度可自定义（默认680）
  - **Modal（弹窗）**：字段少（≤6）、简单表单、确认类操作、宽度默认520
- [ ] **组件属性**
  - `UViewDrawer`/`UViewModal`/`UFormDrawer`/`UFormModal` 须配置 `span` 属性
  - 编辑组件默认监听 `@edit` 事件；展示组件监听 `@open` 事件
  - 表格列options的value → label 转换
  - `UViewTable`/`UEditableTable` 须设置 `:scroll="{x: 1200}"` 开启横向滚动

---

## 二、页面规范

- **路径**：`views/{模块}/{功能}/index.vue`
- **组件 name**：全局唯一（KeepAlive），建议 `{模块}{功能}`
- **菜单 URL**：`{模块}/{功能}`，**权限标识**：`{模块}:{功能}:{操作}`

> 字段类型映射、表单属性、关联数据用法等速查内容见 [admin-quick-ref.md](./admin-quick-ref.md)

---

## 三、组件体系

**命名规律**：`UView` 结尾的是视图容器（UView、UChildView、UDrawerView、UModalView），提供视图上下文；`UViewXx` 结尾的是视图内的 CRUD 组件（UViewSearch、UViewTable、UViewModal、UViewDrawer、UViewEditable），必须放在视图容器内使用。其中 `UView` 是页面级容器，CRUD 页面必须以 `UView` 作为顶级元素。

### 3.1 组件总览

| 组件 | 上下文 | uid | v-model | 事件 | 显隐 | 何时使用 |
|------|--------|-----|---------|------|------|---------|
| `UViewSearch` | SearchContext | `UVSearch` | `Object`（搜索模型） | - | 始终显示 | 每个 CRUD 页面顶部 |
| `UViewTable` | TableContext | `UVTable` | - | - | 始终显示 | 标准列表，`:dataSource` 传入数据 |
| `UViewEditable` | EditContext | `UVEdit` | - | - | 始终显示 | 行内直接编辑 |
| `UViewDrawer` | EditContext | `UVEdit` | `Object`（编辑模型） | `@open` `@edit` | `func="add/edit"` | 主编辑表单（字段多） |
| `UViewModal` | EditContext | `UVEdit` | `Object`（编辑模型） | `@open` `@edit` | `func="add/edit"` | 主编辑表单（字段少） |
| `UFormModal` | EditContext | **必填** | `Object`（编辑模型） | `@open` `@edit` | `func="edit:set"` | 额外编辑弹窗 |
| `UFormDrawer` | EditContext | **必填** | `Object`（编辑模型） | `@open` `@edit` | `func="edit:set"` | 额外编辑抽屉 |
| `UDrawer` | VisibleContext | **必填** | - | `@open` | `func="open"` | 纯展示抽屉 |
| `UModal` | VisibleContext | **必填** | - | `@open` | `func="open"` | 纯展示弹窗 |
| `UChildView` | 独立上下文 | **必填** | - | - | 始终显示 | 嵌套子页面（主从表、多Tab） |
| `UDrawerView` | 独立上下文 | **必填** | - | `@open` | `func="open"` | UDrawer+UChildView，抽屉内完整CRUD |
| `UModalView` | 独立上下文 | **必填** | - | `@open` | `func="open"` | UModal+UChildView，弹窗内完整CRUD |

> 显隐均由内部 `visible` ref 控制，无需手动绑定。`UChildView` 内部 uid 与外部不冲突，内部 func 只作用于当前子视图。

### 3.2 事件说明

**有表单的组件**（UViewDrawer/Modal、UFormModal/Drawer）：
- `@edit(model)` 后台获取详情后触发，参数是编辑模型。新增和编辑时都会触发此事件，适合初始化字段值，**默认使用此事件**
- `@open(model)` 打开时立即触发，参数是当前行数据。主要用于记录当前行，编辑时先 `@open` 再 `@edit`

**无表单的组件**（UDrawer/UModal、UDrawerView/UModalView）：`@open(row)` 打开时触发，参数为行数据，用于记录当前行、初始化子视图参数。

**数据源事件**：凡有 `options`/`url`/`dict` 的表单项组件（USelectItem、UTreeSelectItem、URadioItem、UCheckboxItem 等）均支持以下事件：
- **`@change(value, select)`**：选中项变更时触发，`select` 是当前选中项完整数据。**优先使用**，用于联动选择、填充关联字段
- **`@loaded(sourceMap)`**：数据加载完成时触发，参数是 **value → data** 的 Map 映射。用于非事件上下文下根据 value 获取完整数据（如 `@edit` 回调中取关联字段）
```vue
<USelectItem field="productId" label="所属产品" url="/iot/product/list" labelField="name" valueField="id"
             @change="productChange" @loaded="productLoaded"/>
```
```js
// 优先方式：@change 获取当前选中项数据
function productChange(value, select) {
  editModel.protocolId = select['protocolId'];
}

// 辅助方式：@loaded 获取完整数据源映射，用于非事件上下文
function productLoaded(map) {
  productSourceMap.value = map;  // {id1: {name:'产品A', protocolId:1, ...}, id2: {...}}
  // 在 @edit 回调中通过 map[value] 取关联数据
  const product = map[editModel.productId];
  if(product) editModel.protocolId = product.protocolId;
}
```

### 3.3 组合组件：UDrawerView / UModalView

`UDrawerView` = UDrawer + UChildView，`UModalView` = UModal + UChildView。`uid` 同时传给两者，`rowKey` 默认 `id`，其余属性透传给 UDrawer/UModal。

```vue
<UDrawerView uid="xxx" title="xxx" @open="onOpen" rowKey="id">
  <UViewSearch>...</UViewSearch>
  <UViewTable>...</UViewTable>
  <UViewModal>...</UViewModal>
</UDrawerView>
```

### 3.4 跨视图组件引用

`UChildView`/`UDrawerView`/`UModalView` 各自拥有独立的视图上下文（LinkContext），内部 CRUD 组件默认只能操作当前视图内的组件。当需要跨视图操作时（如 UChildView 内的 UTag 打开 UView 级别的 UDrawerView），使用 **fullUid** 格式：

**查找机制**（`LinkContext.getChildrenContext`）：
1. **本地查找**：先按 `当前视图uid:uid` 查找 → 兼容现有同视图调用
2. **全局查找**：若本地未找到且 uid 包含 `:`，则视为 fullUid 直接查全局注册表 `ViewContext.uidContextMaps`

**使用规范**：
- 同一视图内引用：`uid="xxx"`（和之前一样）
- 跨视图引用：`uid="prefix:xxx"`（prefix 是目标 LinkView 的 uid，如 `UView`、`signalView`）

```vue
<!-- UChildView 内的 UTag 打开 UView 级别的 UDrawerView -->
<UChildView uid="signalView">
  <UViewTable :columns="columns">
    <template #action="{record}">
      <!-- 跨视图引用：uid 使用 fullUid 格式 -->
      <UTag func="open" uid="UView:paramDrawer" :data="record">参数</UTag>
    </template>
  </UViewTable>
</UChildView>

<!-- 目标 UDrawerView 的 uid 保持简单格式，不加前缀 -->
<UDrawerView uid="paramDrawer" title="模型参数" @open="onOpen" rowKey="id">
  ...
</UDrawerView>
```

> **注意**：`UDrawerView`/`UModalView`/`UChildView` 的 `uid` 属性始终使用简单格式（无冒号），只有 `UTag`/`UButton` 的 `uid` 在跨视图引用时才使用 fullUid 格式。

### 3.5 关键属性

> UViewTable、UViewDrawer/Modal、URow 等组件属性速查见 [admin-quick-ref.md](./admin-quick-ref.md)

---

## 四、UButton / UTag

> func 列表、子修饰符、ajax:confirm 示例等速查内容见 [admin-quick-ref.md](./admin-quick-ref.md)

**共用属性**：`func`（必填）、`url`、`method`、`params`、`reload`（默认 true）、`config`、`callback`、`onClick`（`({data, event}) => void`）
**UTag 额外**：`data`（必填）、`uid`、`color`、`disabled`（支持函数）
