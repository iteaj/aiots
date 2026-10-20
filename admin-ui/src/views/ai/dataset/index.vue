<template>
  <UView name="AI数据集管理" rowKey="id">

    <UViewSearch v-model="searchModel">
      <URow col="search">
        <UInputItem field="name" label="名称"/>
        <USelectItem field="type" label="类型" :options="datasetTypeOptions"/>
        <UButton func="query" url="/ai/dataset/view">搜索</UButton>
        <UButton func="reset">重置</UButton>
        <UButton func="add" v-auth="'ai:dataset:add'">新增</UButton>
      </URow>
    </UViewSearch>

    <UViewTable :columns="columns" :scroll="{x: 1200}">
      <template #action="{record}">
        <UTag func="edit" :data="record" url="/ai/dataset/edit" v-auth="'ai:dataset:edit'">修改</UTag>
        <UTag func="del"  :data="record" url="/ai/dataset/del"  v-auth="'ai:dataset:del'">删除</UTag>
        <UTag func="open" uid="itemView" :data="record" color="#2db7f5" v-auth="'ai:dataset:view'">明细</UTag>
      </template>
    </UViewTable>

    <UViewModal title="AI数据集" v-model="editModel" :rules="rules" :span="[6, 18]">
      <template #default="{model}">
        <URow :responsive="1">
          <UInputItem field="name" label="名称" required/>
          <USelectItem field="type" label="类型" required :options="datasetTypeOptions"/>
<!--          <UInputNumberItem field="config.splitRatio" label="训练集比例" :min="0" :max="1" :step="0.1" :precision="2"/>-->
          <UTextareaItem field="remark" label="备注" :maxlength="512"/>
        </URow>
      </template>
      <template #footer="{model}">
        <UButton func="cancel">取消</UButton>
        <UButton func="submit" url="/ai/dataset/saveOrUpdate">提交</UButton>
        <UButton func="reset">重置</UButton>
      </template>
    </UViewModal>

    <!-- 数据集明细抽屉 -->
    <UDrawerView uid="itemView" title="数据集明细" size="100%" @open="onOpenItemView">
      <a-tabs v-model:activeKey="activeKey">
        <!-- 数据库类型 -->
        <a-tab-pane key="db" tab="模型数据集" :disabled="activeKey !== 'db'">
          <UChildView uid="dbView">
            <UViewSearch v-model="dbSearchModel" :span="[6, 16]">
              <USelectItem field="split" label="数据集划分" :options="splitOptions" allowClear style="width: 120px;"/>
              <UButton func="query" url="/ai/dataset/item/view">搜索</UButton>
              <UButton func="reset">重置</UButton>
             <UButton func="add" v-auth="'ai:dataset:add'">新增</UButton>
            </UViewSearch>
            <UViewTable :columns="dbColumns" :scroll="{x: 1200}">
              <template #action="{record}">
                <UTag func="edit" :data="record" url="/ai/dataset/item/edit" v-auth="'ai:dataset:edit'">修改</UTag>
                <UTag func="del"  :data="record" url="/ai/dataset/item/del"  v-auth="'ai:dataset:del'">删除</UTag>
              </template>
            </UViewTable>
            <UViewDrawer title="物模型属性数据项" v-model="itemEditModel" :rules="itemRules" size="960" :span="[6, 18]" @edit="itemEdit">
              <template #default="{model}">
                <URow :responsive="3">
                  <USelectItem field="config.productId" url="/iot/product/listByNonGateway" labelField="name" valueField="id" label="产品编码" required @change="onProductChange"/>
                  <USelectItem field="config.attrIds" url="/iot/modelAttr/listByProductId?productId={config.productId}" label="模型属性" labelField="name" valueField="id" required @change="onAttrChange"/>
                  <USelectItem field="split" label="数据划分" :options="splitOptions" required />
                  <UDateTimeItem field="config.startTime" showTime label="开始时间" valueFormat="YYYY-MM-DD HH:mm:ss" format="YYYY-MM-DD HH:mm:ss" required/>
                  <UDateTimeItem field="config.endTime" showTime label="结束时间" valueFormat="YYYY-MM-DD HH:mm:ss" format="YYYY-MM-DD HH:mm:ss" required/>
                </URow>

                <!-- 历史数据表格 -->
                <a-divider style="margin: 0 0 16px;">历史采集数据</a-divider>
                <a-table :loading="historyLoading" :columns="historyColumns" :dataSource="historyData" bordered :pagination="historyPagination"
                         @change="onHistoryPageChange" :scroll="{x: 600, y: 180}" size="small" rowKey="collectTime"/>
              </template>
              
              <template #footer="{model}">
                <UButton func="cancel">取消</UButton>
                <UButton func="submit" url="/ai/dataset/item/saveOrUpdate">提交</UButton>
                <UButton func="reset">重置</UButton>
              </template>
            </UViewDrawer>
          </UChildView>
        </a-tab-pane>

        <!-- 图像类型 -->
        <a-tab-pane key="image" tab="图像数据集" :disabled="activeKey !== 'image'">
          <UChildView uid="imageView">
            <UViewSearch v-model="imageSearchModel" :span="[6, 16]">
              <UInputItem field="name" label="名称"/>
              <USelectItem field="split" label="数据集划分" :options="splitOptions" allowClear style="width: 120px;"/>
              <UButton func="query" url="/ai/dataset/item/view">搜索</UButton>
              <UButton func="reset">重置</UButton>
            </UViewSearch>
            <UViewTable :columns="imageColumns" :scroll="{x: 1200}">
              <template #func>
                <UButton func="add" v-auth="'ai:dataset:add'">新增</UButton>
              </template>
              <template #action="{record}">
                <UTag func="edit" :data="record" url="/ai/dataset/item/edit" v-auth="'ai:dataset:edit'">修改</UTag>
                <UTag func="del"  :data="record" url="/ai/dataset/item/del"  v-auth="'ai:dataset:del'">删除</UTag>
              </template>
            </UViewTable>
            <UViewDrawer title="图像数据项" v-model="itemEditModel" :rules="itemRules" size="680" :span="[6, 18]" @edit="itemEdit">
              <template #default="{model}">
                <URow :responsive="2">
                  <UInputItem field="name" label="名称" required/>
                  <UInputItem field="fileName" label="文件名" required/>
                  <UInputItem field="filePath" label="文件路径"/>
                  <UInputItem field="label" label="标签"/>
                </URow>
              </template>
              <template #footer="{model}">
                <UButton func="cancel">取消</UButton>
                <UButton func="submit" url="/ai/dataset/item/saveOrUpdate">提交</UButton>
                <UButton func="reset">重置</UButton>
              </template>
            </UViewDrawer>
          </UChildView>
        </a-tab-pane>

        <!-- 文本类型 -->
        <a-tab-pane key="text" tab="文本数据集" :disabled="activeKey !== 'text'">
          <UChildView uid="textView">
            <UViewSearch v-model="textSearchModel" :span="[6, 16]">
              <UInputItem field="name" label="名称"/>
              <USelectItem field="split" label="数据集划分" :options="splitOptions" allowClear style="width: 120px;"/>
              <UButton func="query" url="/ai/dataset/item/view">搜索</UButton>
              <UButton func="reset">重置</UButton>
            </UViewSearch>
            <UViewTable :columns="textColumns" :scroll="{x: 1200}">
              <template #func>
                <UButton func="add" v-auth="'ai:dataset:add'">新增</UButton>
              </template>
              <template #action="{record}">
                <UTag func="edit" :data="record" url="/ai/dataset/item/edit" v-auth="'ai:dataset:edit'">修改</UTag>
                <UTag func="del"  :data="record" url="/ai/dataset/item/del"  v-auth="'ai:dataset:del'">删除</UTag>
              </template>
            </UViewTable>
            <UViewDrawer title="文本数据项" v-model="itemEditModel" :rules="itemRules" size="680" :span="[6, 18]" @edit="itemEdit">
              <template #default="{model}">
                <URow :responsive="2">
                  <UInputItem field="name" label="名称" required/>
                  <UInputItem field="fileName" label="文件名" required/>
                  <UInputItem field="filePath" label="文件路径"/>
                  <UInputItem field="label" label="标签"/>
                </URow>
              </template>
              <template #footer="{model}">
                <UButton func="cancel">取消</UButton>
                <UButton func="submit" url="/ai/dataset/item/saveOrUpdate">提交</UButton>
                <UButton func="reset">重置</UButton>
              </template>
            </UViewDrawer>
          </UChildView>
        </a-tab-pane>
      </a-tabs>
    </UDrawerView>
  </UView>
</template>

<script>
import { reactive, ref, watch, nextTick } from 'vue'
import { GET } from '@/utils/request'
import CoreConsts from '@/components/CoreConsts'

export default {
  name: 'AiDataset',
  setup() {
    const statusOptions = [
      { label: '处理中', value: 'processing' },
      { label: '就绪', value: 'ready' },
      { label: '失败', value: 'error' }
    ]

    const splitOptions = [
      { label: '训练集', value: 'train' },
      { label: '验证集', value: 'val' },
      { label: '测试集', value: 'test' }
    ]

    const datasetTypeOptions = [
      { label: '模型属性', value: 'db' },
      { label: '图像', value: 'image' },
      { label: '文本', value: 'text' }
    ]

    const columns = reactive([
      { title: '名称', field: 'name', width: 200 },
      { title: '数据条数', field: 'recordCount', width: 100, align: 'right' },
      { title: '类型', field: 'type', width: 100, options: datasetTypeOptions },
      { title: '备注', field: 'remark', width: 200, ellipsis: true },
      { title: '创建时间', field: 'createTime', width: 160, type: 'date' },
      { title: '操作', field: 'action', width: 220, type: 'action', fixed: 'right' }
    ])

    const rules = {
      name: { required: true, message: '数据集名称必填' }
    }

    const dbColumns = reactive([
    //   { title: '名称', field: 'name', width: 180 },
      { title: '产品', field: 'config.productName', width: 150},
      { title: '模型属性', field: 'config.attrNames', width: 150},
      { title: '记录数', field: 'recordCount', width: 120},
      { title: '数据集划分', field: 'split', width: 100, options: splitOptions },
      { title: '创建时间', field: 'createTime', width: 160, type: 'date' },
      { title: '操作', field: 'action', width: 160, type: 'action', fixed: 'right' }
    ])

    const imageColumns = reactive([
      { title: '名称', field: 'name', width: 180 },
      { title: '文件名', field: 'fileName', width: 180, ellipsis: true },
      { title: '标签', field: 'label', width: 120 },
      { title: '数据集划分', field: 'split', width: 100, options: splitOptions },
      { title: '创建时间', field: 'createTime', width: 160, type: 'date' },
      { title: '操作', field: 'action', width: 160, type: 'action', fixed: 'right' }
    ])

    const textColumns = reactive([
      { title: '名称', field: 'name', width: 180 },
      { title: '文件名', field: 'fileName', width: 180, ellipsis: true },
      { title: '标签', field: 'label', width: 120 },
      { title: '数据集划分', field: 'split', width: 100, options: splitOptions },
      { title: '创建时间', field: 'createTime', width: 160, type: 'date' },
      { title: '操作', field: 'action', width: 160, type: 'action', fixed: 'right' }
    ])

    const itemRules = {
      name: { required: true, message: '名称必填' }
    }

    const searchModel = ref({})
    const editModel = ref({})
    const itemSearchModel = ref({})
    const dbSearchModel = ref({ type: 'db' })
    const imageSearchModel = ref({ type: 'image' })
    const textSearchModel = ref({ type: 'text' })
    const itemEditModel = ref({})
    const activeKey = ref('db')

    // 历史数据相关
    const historyData = ref([])
    const historyLoading = ref(false)
    const historyColumns = reactive([
      { dataIndex: 'deviceName', title: '设备名称', width: 180, align: 'center' },
      { dataIndex: 'fieldName', title: '属性', width: 180, align: 'center' },
      { dataIndex: 'value', title: '采集值', width: 150, ellipsis: true, align: 'center' },
      { dataIndex: 'collectTime', title: '采集时间', width: 220, customRender: ({text}) => text ? new Date(text).toLocaleString() : '', align: 'center' },
    ])
    const historyPagination = reactive({ current: 1, pageSize: 20, total: 0, showTotal: (total) => `共 ${total} 条` })

    // 监听config字段变化，条件齐全时自动查询历史数据
    watch(() => [
      itemEditModel.value.config?.productId,
      itemEditModel.value.config?.attrIds,
      itemEditModel.value.config?.startTime,
      itemEditModel.value.config?.endTime,
    ], ([productId, attrIds, startTime, endTime]) => {
      if (productId && attrIds && startTime && endTime) {
        historyPagination.current = 1
        nextTick(() => loadHistoryData(productId, attrIds, startTime, endTime))
      } else {
        historyData.value = []
      }
    })

    function loadHistoryData(productId, attrIds, startTime, endTime) {
      historyLoading.value = true
      GET('/iot/collectData/model', {
        productId, field: attrIds,
        startTime, endTime,
        current: historyPagination.current, size: historyPagination.pageSize,
      }).then(({code, data}) => {
        if (code === CoreConsts.SuccessCode && data) {
          historyData.value = data.records || []
          historyPagination.total = data.total || 0
          itemEditModel.value.recordCount = data.total || 0
          itemEditModel.value.config = itemEditModel.value.config || {}
        }
      }).finally(() => {
        historyLoading.value = false
      })
    }

    return {
      columns, rules, searchModel, editModel,
      dbColumns, imageColumns, textColumns, itemRules, datasetTypeOptions,
      dbSearchModel, imageSearchModel, textSearchModel, itemEditModel, activeKey,
      statusOptions, splitOptions,
      historyData, historyLoading, historyColumns, historyPagination, loadHistoryData,
    }
  },
  methods: {
    onProductChange(value, select) {
      this.itemEditModel.config.productName = select?.name || ''
    },
    onAttrChange(value, select) {
      // 多选时select是数组
      if (Array.isArray(select)) {
        this.itemEditModel.config.attrNames = select.map(item => item.name).join(',')
      } else {
        this.itemEditModel.config.attrNames = select?.name || ''
      }
    },
    onOpenItemView(row) {
      this.activeKey = row.type
      this.dbSearchModel.datasetId = row.id
      this.imageSearchModel.datasetId = row.id
      this.textSearchModel.datasetId = row.id
    },
    itemEdit(model) {
      if (!model.id) {
        const searchModel = this.activeKey === 'db' ? this.dbSearchModel
          : this.activeKey === 'image' ? this.imageSearchModel : this.textSearchModel
        model.datasetId = searchModel.datasetId
        model.type = this.activeKey
        model.config = model.config || {}
      }
      // watch 会自动检测 config 变化并触发查询
    },
    onHistoryPageChange(pagination) {
      this.historyPagination.current = pagination.current
      const config = this.itemEditModel.config || {}
      if (config.productId && config.attrIds && config.startTime && config.endTime) {
        this.loadHistoryData(config.productId, config.attrIds, config.startTime, config.endTime)
      }
    }
  }
}
</script>
