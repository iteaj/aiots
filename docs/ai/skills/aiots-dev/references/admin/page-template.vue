<template>
  <UView name="功能名称" rowKey="id">

    <!-- 搜索栏 -->
    <UViewSearch v-model="searchModel">
      <URow col="search">
        <UInputItem field="name" label="名称"/>
        <USelectItem field="status" label="状态" dict="sys_func_status"/>
        <!-- <URangeItem field="dateRange" label="时间范围"/> -->
        <UButton func="query" url="/module/func/view">搜索</UButton>
        <UButton func="reset">重置</UButton>
        <UButton func="add" v-auth="'module:func:add'">新增</UButton>
      </URow>
    </UViewSearch>

    <!-- 表格 -->
    <UViewTable :columns="columns" :scroll="{x: 1200}">
      <template #func> <!-- 操作按钮默认放到搜索栏(重要)，如果需要单独放，可以使用此模板 -->
        <!-- <UButton func="add" v-auth="'module:func:add'">新增</UButton> -->
        <!-- <UButton func="del:batch" url="/module/func/del">批量删除</UButton> -->
        <!-- <UButton func="export" url="/module/func/export">导出</UButton> -->
        <!-- <UButton func="import" url="/module/func/import" :config="{uploadConfig: {templateUrl: '/static/模板.xlsx'}}">导入</UButton> -->
      </template>
      <template #action="{record}">
        <UTag func="edit" :data="record" url="/module/func/edit" v-auth="'module:func:edit'">修改</UTag>
        <UTag func="del"  :data="record" url="/module/func/del"  v-auth="'module:func:del'">删除</UTag>
        <!-- <UTag func="edit:set" uid="extraModal" :data="record" :config="{copy: ['id']}" url="/module/func/xxx">子操作</UTag> -->
        <!-- <UTag func="add:child" :data="record" :config="{pid: 'parentId'}">添加子项</UTag> -->
        <!-- <UTag func="ajax" :data="record" url="/module/func/enable" :onClick="({data, event}) => { event() }">启用</UTag> -->
      </template>
      <!-- <template #status="{text, record}"><ATag :color="text === 'enabled' ? 'green' : 'red'">{{ text }}</ATag></template> -->
    </UViewTable>

    <!-- 编辑表单（UViewDrawer 侧边抽屉 | UViewModal 居中弹窗） -->
    <UViewDrawer title="功能名称" v-model="editModel" :rules="rules">
      <template #default="{model}">
        <URow :responsive="2">
          <UInputItem field="name" label="名称" required/>
          <UInputNumberItem field="sort" label="排序" :min="0"/>
          <!-- 如果需要获取选中项记录：加@change="xxxChange"(当前选中项) 或 @loaded="xxxLoaded"(整个数据源Map)获取选中项完整数据 -->
          <USelectItem field="typeId" label="类型" url="/module/type/list" labelField="name" valueField="id"/>
          <!-- disabled 支持函数：<UInputItem field="code" :disabled="model.id != null"/> -->
          <UTextareaItem field="remark" label="备注" :span="24" :maxlength="256"/>
        </URow>
      </template>
      <template #footer="{model}">
        <UButton func="cancel">取消</UButton>
        <UButton func="submit" url="/module/func/saveOrUpdate">提交</UButton>
        <UButton func="reset">重置</UButton>
      </template>
    </UViewDrawer>

    <!-- 额外弹窗（同一上下文多个编辑场景，需指定 uid，通过 UTag func="edit:set" uid="xxx" 触发） -->
    <!-- <UFormModal uid="extraModal" title="子操作" :span="[6, 15]" :rules="extraRules">
      <UInputItem field="xxx" label="xxx"/>
      <template #footer>
        <UButton func="submit" url="/module/func/xxx">提交</UButton>
        <UButton func="cancel">取消</UButton>
      </template>
    </UFormModal> -->

    <!--
      UChildView（=ULinkView）：子视图容器，提供独立的 CRUD 上下文
        - uid 必填，在当前 UView 内唯一
        - 内部可使用完整的 UViewSearch / UViewTable / UViewDrawer
        - 内部 func 操作只作用于当前子视图上下文
        - 可任意嵌套，自由组合完成复杂功能
    -->
    <!-- <UViewDrawer title="详情" v-model="editModel" :rules="rules">
      <template #default="{model}">
        <a-tabs>
          <a-tab-pane key="attr" tab="属性">
            <UChildView uid="attr">
              <UViewSearch v-model="childSearchModel">
                <UButton func="query" :url="`/module/attr/view?pid=${editModel.id || ''}`">搜索</UButton>
              </UViewSearch>
              <UViewTable :columns="attrColumns" :scroll="{x: 1200}">
                <template #func><UButton func="add">新增</UButton></template>
                <template #action="{record}">
                  <UTag func="edit" :data="record" url="/module/attr/edit">修改</UTag>
                  <UTag func="del"  :data="record" url="/module/attr/del">删除</UTag>
                </template>
              </UViewTable>
              <UViewDrawer title="属性编辑" :rules="attrRules" :span="[6, 16]">
                <template #default><UInputItem field="name" label="名称" required/></template>
                <template #footer="{model}">
                  <UButton func="cancel">取消</UButton>
                  <UButton func="submit" url="/module/attr/saveOrUpdate">提交</UButton>
                </template>
              </UViewDrawer>
            </UChildView>
          </a-tab-pane>
        </a-tabs>
      </template>
    </UViewDrawer> -->

  </UView>
</template>

<script>
import { reactive, ref } from 'vue'

export default {
  // name 全局唯一，用于 KeepAlive 缓存
  name: 'ModuleFunc',
  setup() {
    /**
     * 表格列定义
     * 根据后端实体类字段定义，常见字段类型映射：
     *   文本字段 → { title: 'xxx', field: 'xxx', width: 150 }
     *   状态/类型 → { field: 'xxx', dict: '字典类型' } 或 {field: 'xxx', options: '选项列表'} 或 { field: 'xxx', url: '/xxx/list', labelField: 'xxx', valueField: 'xxx' }
     *   日期时间  → { field: 'xxx', type: 'date', width: 160, format: 'YYYY-MM-DD HH:mm:ss' }
     *   长文本    → { field: 'xxx', ellipsis: true, width: 200 }
     *   操作列    → { field: 'action', type: 'action', width: 160, fixed: 'right' }
     */
    const columns = reactive([
      { title: '名称',     field: 'name',       width: 150 },
      { title: '状态',     field: 'status',     width: 80,  dict: 'sys_func_status' },
      { title: '创建时间', field: 'createTime', width: 160, type: 'date' },
      { title: '操作',     field: 'action',     width: 160, type: 'action', fixed: 'right' }
    ])

    /**
     * 表单校验规则
     * 根据后端实体类 @NotNull / @NotBlank / @NotEmpty 等校验注解定义
     */
    const rules = { name: { required: true, message: '名称必填' } }

    /**
     * 搜索组件表单数据
     */
    const searchModel = ref({})
    /**
     * 编辑组件表单数据
     */
    const editModel = ref({})
    return { columns, rules, searchModel, editModel }
  },
  methods: {

  }
}
</script>
