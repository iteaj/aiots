<template>
  <UView name="AI模型管理" rowKey="id">

    <a-tabs v-model:activeKey="activeKey">

      <!-- 工业信号模态 -->
      <a-tab-pane key="signal" tab="工业信号模型">
        <UChildView uid="signalView">
          <UViewSearch v-model="signalSearchModel">
            <URow col="search">
              <UInputItem field="name" label="模型名称"/>
              <USelectItem field="taskType" label="任务类型" dict="ai_model_task_type_signal"/>
              <!-- <USelectItem field="status" label="状态" :options="statusOptions"/> -->
              <UButton func="query" url="/ai/model/view">搜索</UButton>
              <UButton func="reset">重置</UButton>
              <UButton func="add" v-auth="'ai:model:add'">新增</UButton>
            </URow>
          </UViewSearch>
          <UViewTable :columns="columns" :scroll="{x: 1200}">
            <template #action="{record}">
              <UTag func="edit" :data="record" url="/ai/model/edit" v-auth="'ai:model:edit'">修改</UTag>
              <UTag func="del"  :data="record" url="/ai/model/del"  v-auth="'ai:model:del'">删除</UTag>
              <UTag func="open" uid="UView:paramDrawer" :data="record" color="#87d068" v-auth="'ai:model:edit'">参数</UTag>
            </template>
          </UViewTable>
          <UViewModal title="工业信号模型" v-model="editModel" :span="[6, 16]" :rules="rules" @edit="onModelEdit">
            <template #default="{model}">
              <UInputItem field="name" label="模型名称" required/>
              <USelectItem field="modelType" label="模型类型" required :options="modelTypeOptions" defaultValue="base" disabled/>
              <USelectItem field="taskType" label="任务类型" required dict="ai_model_task_type_signal"/>
              <UUploadItem field="modelPath" label="模型文件" :withCredentials="true"
              :action="`${$apiUrl}/ai/model/upload`" filename="file" accept=".pt,.pth,.bin,.onnx,.ckpt,.pb,.gguf,.trt,.tflite,.safetensors" :maxCount="1">
              <a-input-group compact>
                <AInput v-model:value="model['modelPath']" disabled style="width: 60%"/>
                <AButton><UIcon type="iz-icon-upload" /> 点击上传</AButton>
              </a-input-group>
              </UUploadItem>
              <!-- <USelectItem field="status" label="状态" :options="statusOptions" defaultValue="enabled"/> -->
              <UTextareaItem field="remark" label="备注" :maxlength="256"/>
            </template>
            <template #footer>
              <UButton func="cancel">取消</UButton>
              <UButton func="submit" url="/ai/model/saveOrUpdate">提交</UButton>
              <UButton func="reset">重置</UButton>
            </template>
          </UViewModal>
        </UChildView>
      </a-tab-pane>

      <!-- 视觉模态 -->
      <a-tab-pane key="vision" tab="视觉模型" disabled>
        <UChildView uid="visionView">
          <UViewSearch v-model="visionSearchModel">
            <URow col="search">
              <UInputItem field="name" label="模型名称"/>
              <USelectItem field="taskType" label="任务类型" dict="ai_model_task_type_vision"/>
              <USelectItem field="status" label="状态" :options="statusOptions"/>
              <UButton func="query" url="/ai/model/view">搜索</UButton>
              <UButton func="reset">重置</UButton>
              <UButton func="add" v-auth="'ai:model:add'">新增</UButton>
            </URow>
          </UViewSearch>
          <UViewTable :columns="columns" :scroll="{x: 1200}">
            <template #action="{record}">
              <UTag func="edit" :data="record" url="/ai/model/edit" v-auth="'ai:model:edit'">修改</UTag>
              <UTag func="del"  :data="record" url="/ai/model/del"  v-auth="'ai:model:del'">删除</UTag>
              <UTag func="open" uid="UVie:paramDrawer" :data="record" color="#87d068" v-auth="'ai:model:edit'">参数</UTag>
            </template>
          </UViewTable>
          <UViewModal title="视觉模型" v-model="editModel" :span="[6, 16]" :rules="rules" @edit="onModelEdit">
            <template #default="{model}">
              <UInputItem field="name" label="模型名称" required/>
              <USelectItem field="modelType" label="模型类型" required :options="modelTypeOptions" defaultValue="base" disabled/>
              <USelectItem field="taskType" label="任务类型" required dict="ai_model_task_type_vision"/>
              <USelectItem field="status" label="状态" :options="statusOptions" defaultValue="enabled"/>
              <UTextareaItem field="remark" label="备注" :maxlength="256"/>
            </template>
            <template #footer>
              <UButton func="cancel">取消</UButton>
              <UButton func="submit" url="/ai/model/saveOrUpdate">提交</UButton>
              <UButton func="reset">重置</UButton>
            </template>
          </UViewModal>
        </UChildView>
      </a-tab-pane>

      <!-- 文本模态 -->
      <a-tab-pane key="text" tab="文本模型" disabled>
        <UChildView uid="textView">
          <UViewSearch v-model="textSearchModel">
            <URow col="search">
              <UInputItem field="name" label="模型名称"/>
              <USelectItem field="taskType" label="任务类型" dict="ai_model_task_type_text"/>
              <USelectItem field="status" label="状态" :options="statusOptions"/>
              <UButton func="query" url="/ai/model/view">搜索</UButton>
              <UButton func="reset">重置</UButton>
              <UButton func="add" v-auth="'ai:model:add'">新增</UButton>
            </URow>
          </UViewSearch>
          <UViewTable :columns="columns" :scroll="{x: 1200}">
            <template #action="{record}">
              <UTag func="edit" :data="record" url="/ai/model/edit" v-auth="'ai:model:edit'">修改</UTag>
              <UTag func="del"  :data="record" url="/ai/model/del"  v-auth="'ai:model:del'">删除</UTag>
              <UTag func="open" uid="UView:paramDrawer" :data="record" color="#87d068" v-auth="'ai:model:edit'">参数</UTag>
            </template>
          </UViewTable>
          <UViewModal title="文本模型" v-model="editModel" :span="[6, 16]" :rules="rules" @edit="onModelEdit">
            <template #default="{model}">
              <UInputItem field="name" label="模型名称" required/>
              <USelectItem field="modelType" label="模型类型" required :options="modelTypeOptions"/>
              <USelectItem field="taskType" label="任务类型" required dict="ai_model_task_type_text"/>
              <USelectItem field="status" label="状态" :options="statusOptions" defaultValue="enabled"/>
              <UTextareaItem field="remark" label="备注" :maxlength="256"/>
            </template>
            <template #footer>
              <UButton func="cancel">取消</UButton>
              <UButton func="submit" url="/ai/model/saveOrUpdate">提交</UButton>
              <UButton func="reset">重置</UButton>
            </template>
          </UViewModal>
        </UChildView>
      </a-tab-pane>

      <!-- 音频模态 -->
      <a-tab-pane key="audio" tab="音频模型" disabled>
        <UChildView uid="audioView">
          <UViewSearch v-model="audioSearchModel">
            <URow col="search">
              <UInputItem field="name" label="模型名称"/>
              <USelectItem field="taskType" label="任务类型" dict="ai_model_task_type_audio"/>
              <USelectItem field="status" label="状态" :options="statusOptions"/>
              <UButton func="query" url="/ai/model/view">搜索</UButton>
              <UButton func="reset">重置</UButton>
              <UButton func="add" v-auth="'ai:model:add'">新增</UButton>
            </URow>
          </UViewSearch>
          <UViewTable :columns="columns" :scroll="{x: 1200}">
            <template #action="{record}">
              <UTag func="edit" :data="record" url="/ai/model/edit" v-auth="'ai:model:edit'">修改</UTag>
              <UTag func="del"  :data="record" url="/ai/model/del"  v-auth="'ai:model:del'">删除</UTag>
              <UTag func="open" uid="UView:paramDrawer" :data="record" color="#87d068" v-auth="'ai:model:edit'">参数</UTag>
            </template>
          </UViewTable>
          <UViewModal title="音频模型" v-model="editModel" :span="[6, 16]" :rules="rules" @edit="onModelEdit">
            <template #default="{model}">
              <UInputItem field="name" label="模型名称" required/>
              <USelectItem field="modelType" label="模型类型" required :options="modelTypeOptions"/>
              <USelectItem field="taskType" label="任务类型" required dict="ai_model_task_type_audio"/>
              <USelectItem field="status" label="状态" :options="statusOptions" defaultValue="enabled"/>
              <UTextareaItem field="remark" label="备注" :maxlength="256"/>
            </template>
            <template #footer>
              <UButton func="cancel">取消</UButton>
              <UButton func="submit" url="/ai/model/saveOrUpdate">提交</UButton>
              <UButton func="reset">重置</UButton>
            </template>
          </UViewModal>
        </UChildView>
      </a-tab-pane>

      <!-- 视频模态 -->
      <a-tab-pane key="video" tab="视频模型" disabled>
        <UChildView uid="videoView">
          <UViewSearch v-model="videoSearchModel">
            <URow col="search">
              <UInputItem field="name" label="模型名称"/>
              <USelectItem field="taskType" label="任务类型" dict="ai_model_task_type_video"/>
              <!-- <USelectItem field="status" label="状态" :options="statusOptions"/> -->
              <UButton func="query" url="/ai/model/view">搜索</UButton>
              <UButton func="reset">重置</UButton>
              <UButton func="add" v-auth="'ai:model:add'">新增</UButton>
            </URow>
          </UViewSearch>
          <UViewTable :columns="columns" :scroll="{x: 1200}">
            <template #action="{record}">
              <UTag func="edit" :data="record" url="/ai/model/edit" v-auth="'ai:model:edit'">修改</UTag>
              <UTag func="del"  :data="record" url="/ai/model/del"  v-auth="'ai:model:del'">删除</UTag>
              <UTag func="open" uid="UView:paramDrawer" :data="record" color="#87d068" v-auth="'ai:model:edit'">参数</UTag>
            </template>
          </UViewTable>
          <UViewModal title="视频模型" v-model="editModel" :span="[6, 16]" :rules="rules" @edit="onModelEdit">
            <template #default="{model}">
              <UInputItem field="name" label="模型名称" required/>
              <USelectItem field="modelType" label="模型类型" required :options="modelTypeOptions"/>
              <USelectItem field="taskType" label="任务类型" required dict="ai_model_task_type_video"/>
              <!-- <USelectItem field="status" label="状态" :options="statusOptions" defaultValue="enabled"/> -->
              <UTextareaItem field="remark" label="备注" :maxlength="256"/>
            </template>
            <template #footer>
              <UButton func="cancel">取消</UButton>
              <UButton func="submit" url="/ai/model/saveOrUpdate">提交</UButton>
              <UButton func="reset">重置</UButton>
            </template>
          </UViewModal>
        </UChildView>
      </a-tab-pane>

      <!-- 多模态 -->
      <a-tab-pane key="multimodal" tab="多模态模型" disabled>
        <UChildView uid="multimodalView">
          <UViewSearch v-model="multimodalSearchModel">
            <URow col="search">
              <UInputItem field="name" label="模型名称"/>
              <USelectItem field="taskType" label="任务类型" dict="ai_model_task_type_multimodal"/>
              <USelectItem field="status" label="状态" :options="statusOptions"/>
              <UButton func="query" url="/ai/model/view">搜索</UButton>
              <UButton func="reset">重置</UButton>
              <UButton func="add" v-auth="'ai:model:add'">新增</UButton>
            </URow>
          </UViewSearch>
          <UViewTable :columns="columns" :scroll="{x: 1200}">
            <template #action="{record}">
              <UTag func="edit" :data="record" url="/ai/model/edit" v-auth="'ai:model:edit'">修改</UTag>
              <UTag func="del"  :data="record" url="/ai/model/del"  v-auth="'ai:model:del'">删除</UTag>
              <UTag func="open" uid="UView:paramDrawer" :data="record" color="#87d068" v-auth="'ai:model:edit'">参数</UTag>
            </template>
          </UViewTable>
          <UViewModal title="多模态模型" v-model="editModel" :span="[6, 16]" :rules="rules" @edit="onModelEdit">
            <template #default="{model}">
              <UInputItem field="name" label="模型名称" required/>
              <USelectItem field="modelType" label="模型类型" required :options="modelTypeOptions"/>
              <USelectItem field="taskType" label="任务类型" required dict="ai_model_task_type_multimodal"/>
              <!-- <USelectItem field="status" label="状态" :options="statusOptions" defaultValue="enabled"/> -->
              <UTextareaItem field="remark" label="备注" :maxlength="256"/>
            </template>
            <template #footer>
              <UButton func="cancel">取消</UButton>
              <UButton func="submit" url="/ai/model/saveOrUpdate">提交</UButton>
              <UButton func="reset">重置</UButton>
            </template>
          </UViewModal>
        </UChildView>
      </a-tab-pane>
    </a-tabs>

    <!-- 模型参数抽屉 -->
    <UDrawerView uid="paramDrawer" :title="'模型参数 - ' + currentModelName" size="1200" @open="onParamDrawerOpen" rowKey="id">
      <UViewSearch v-model="paramSearchModel">
        <URow :responsive="2">
          <UInputItem field="paramName" label="参数名称"/>
          <UButton func="query" :url="'/ai/model/param/view?modelId=' + (currentModelId || '')">搜索</UButton>
          <UButton func="add" v-auth="'ai:model:add'">新增</UButton>
        </URow>
      </UViewSearch>
      <UViewTable :columns="paramColumns" :scroll="{x: 1000}" size="small">
        <template #action="{record}">
          <UTag func="edit" :data="record" url="/ai/model/param/edit" v-auth="'ai:model:edit'">修改</UTag>
          <UTag func="del"  :data="record" url="/ai/model/param/del"  v-auth="'ai:model:del'">删除</UTag>
        </template>
      </UViewTable>
      <UViewModal title="模型参数" v-model="paramEditModel" :span="[6, 16]" :rules="paramRules" @edit="onParamModalOpen">
        <template #default="{model}">
          <UInputItem field="paramName" label="参数名称" required/>
          <UInputItem field="paramKey" label="参数键" required/>
          <UInputItem field="paramValue" label="默认值" required/>
          <!-- <UInputNumberItem field="sort" label="排序" :min="0"/> -->
          <UTextareaItem field="remark" label="备注"/>
        </template>
        <template #footer>
          <UButton func="cancel">取消</UButton>
          <UButton func="submit" url="/ai/model/param/saveOrUpdate">提交</UButton>
          <UButton func="reset">重置</UButton>
        </template>
      </UViewModal>
    </UDrawerView>

  </UView>
</template>

<script>
import { computed, reactive, ref } from 'vue'

export default {
  name: 'AiModel',
  setup() {
    const modalityOptions = ref([
      {label: '工业信号', value: 'signal'},
      {label: '文本', value: 'text'},
      {label: '视觉', value: 'vision'},
      {label: '音频', value: 'audio'},
      {label: '视频', value: 'video'},
      {label: '多模态', value: 'multimodal'}
    ])

    const modelTypeOptions = ref([
      {label: '通用模型(预训练)', value: 'base'},
      {label: '定制模型', value: 'custom'}
    ])

    // 模态→任务类型字典映射
    const taskTypeDictMap = {
      signal: 'ai_model_task_type_signal',
      text: 'ai_model_task_type_text',
      vision: 'ai_model_task_type_vision',
      audio: 'ai_model_task_type_audio',
      video: 'ai_model_task_type_video',
      multimodal: 'ai_model_task_type_multimodal'
    }

    const statusOptions = ref([
      {label: '启用', value: 'enabled'},
      {label: '禁用', value: 'disabled'}
    ])

    const activeKey = ref('signal')

    const columns = computed(() => [
      { title: '模型名称', field: 'name', width: 150 },
      { title: '模型类型', field: 'modelType', width: 100, options: modelTypeOptions.value },
      { title: '任务类型', field: 'taskType', width: 120, dict: taskTypeDictMap[activeKey.value] },
      { title: '状态', field: 'status', width: 100, options: statusOptions.value },
      { title: '备注', field: 'remark', width: 200, ellipsis: true },
      { title: '创建时间', field: 'createTime', width: 160, type: 'date' },
      { title: '操作', field: 'action', width: 280, type: 'action', fixed: 'right' }
    ])

    const rules = {
      name: { required: true, message: '模型名称必填' },
      modelType: { required: true, message: '模型类型必填' },
      taskType: { required: true, message: '任务类型必填' },
      status: { required: true, message: '状态必填' }
    }

    const signalSearchModel = ref({ modality: 'signal' })
    const textSearchModel = ref({ modality: 'text' })
    const visionSearchModel = ref({ modality: 'vision' })
    const audioSearchModel = ref({ modality: 'audio' })
    const videoSearchModel = ref({ modality: 'video' })
    const multimodalSearchModel = ref({ modality: 'multimodal' })
    const editModel = ref({})

    // ============ 模型参数相关 ============
    const currentModelId = ref(null)
    const currentModelName = ref('')
    const paramSearchModel = ref({})
    const paramEditModel = ref({})

    const onParamDrawerOpen = (row) => {
      currentModelId.value = row?.id
      currentModelName.value = row?.name || ''
    }

    const onParamModalOpen = (model) => {
      if (!model.id) {
        model.modelId = currentModelId.value
      }
    }

    const paramColumns = reactive([
      { title: '参数名称', field: 'paramName', width: 160 },
      { title: '参数键', field: 'paramKey', width: 160 },
      { title: '默认值', field: 'paramValue', width: 150, ellipsis: true },
      // { title: '排序', field: 'sort', width: 80 },
      { title: '备注', field: 'remark', width: 150, ellipsis: true },
      { title: '操作', field: 'action', type: 'action', fixed: 'right' }
    ])

    const paramRules = {
      paramName: { required: true, message: '参数名称必填' },
      paramKey: { required: true, message: '参数键必填' },
      paramValue: { required: true, message: '参数值必填' }
    }

    return {
      columns, rules, activeKey, editModel, statusOptions, modalityOptions, modelTypeOptions,
      signalSearchModel, textSearchModel, visionSearchModel, audioSearchModel,
      videoSearchModel, multimodalSearchModel,
      currentModelId, currentModelName, paramSearchModel, paramEditModel,
      paramColumns, paramRules, onParamDrawerOpen, onParamModalOpen
    }
  },
  methods: {
    onModelEdit(model) {
      if (!model.id) {
        model.modality = this.activeKey
      }
    }
  }
}
</script>
