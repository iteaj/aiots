<template>
  <UView name="模型训练管理" rowKey="id">

    <a-tabs v-model:activeKey="activeKey">

      <!-- 模型训练 -->
      <a-tab-pane key="train" tab="模型训练">
        <UChildView uid="trainView">
          <UViewSearch v-model="trainSearchModel">
            <URow col="search">
              <UInputItem field="taskName" label="任务名称"/>
              <USelectItem field="status" label="状态" :options="statusOptions"/>
              <UButton func="query" url="/ai/train/task/view">搜索</UButton>
              <UButton func="reset">重置</UButton>
              <UButton func="add" v-auth="'ai:train:task:add'">新增</UButton>
            </URow>
          </UViewSearch>
          <UViewTable :columns="trainColumns" :scroll="{x: 1400}">
            <template #action="{record}">
              <UTag func="edit" :data="record" url="/ai/train/task/edit" v-auth="'ai:train:task:edit'">修改</UTag>
              <UTag func="del"  :data="record" url="/ai/train/task/del"  v-auth="'ai:train:task:del'">删除</UTag>
              <UTag func="open" uid="UView:metricDrawer" :data="record" color="#1890ff">指标</UTag>
              <UTag v-if="record.status !== 'completed'" func="ajax:confirm" :data="record" url="/ai/train/task/toggle" method="POST"
                :color="record.status === 'running' ? 'orange' : 'green'"
                :params="getToggleParams" :config="getToggleConfig(record)"
              >{{ record.status === 'running' ? '取消' : '开始' }}</UTag>
            </template>
          </UViewTable>
          <UViewDrawer title="训练任务" v-model="editModel" width="920" :rules="rules" :span="[6, 16]" @edit="onOpenEdit">
            <template #default="{model}">
              <URow :responsive="3">
                <UInputItem field="taskName" label="任务名称" required/>
                <USelectItem field="modelId" label="基于模型" url="/ai/model/list" labelField="name" valueField="id" required @change="onModelChange"/>
                <USelectItem field="datasetId" label="数据集" url="/ai/dataset/list" labelField="name" valueField="id" required/>
                <UInputNumberItem field="totalEpochs" label="总轮次" :min="1" :defaultValue="1" required/>
                <UInputNumberItem field="dropout" label="丢弃率" :min="0" :max="1" :step="0.1" :defaultValue="0.5"/>
                <UInputNumberItem field="lr" label="学习率" :min="0.0001" :step="0.0001" :defaultValue="0.001"/>
                <UInputNumberItem field="batchSize" label="批尺寸" :min="1" :defaultValue="32"/>
                <UInputNumberItem field="numClasses" label="类别数" :min="0" :defaultValue="0"/>
                <UInputItem field="remark" label="备注" :maxlength="128"/>
              </URow>
              <a-divider style="margin: 12px 0">模型参数</a-divider>
              <URow :responsive="3">
                <template v-for="param in modelParams" :key="param.paramKey">
                  <UInputItem required :field="'config.'+param.paramKey" :label="param.paramName"/>
                </template>
              </URow>
            </template>
            <template #footer="{model}">
              <UButton func="cancel">取消</UButton>
              <UButton func="submit" url="/ai/train/task/saveOrUpdate">提交</UButton>
              <UButton func="reset">重置</UButton>
            </template>
          </UViewDrawer>
        </UChildView>
      </a-tab-pane>

      <!-- 模型测试 -->
      <a-tab-pane key="test" tab="模型测试">
        <UChildView uid="testView">
          <UViewSearch v-model="testSearchModel">
            <URow col="search">
              <UInputItem field="taskName" label="任务名称"/>
              <USelectItem field="status" label="状态" :options="testStatusOptions"/>
              <UButton func="query" url="/ai/test/task/view">搜索</UButton>
              <UButton func="reset">重置</UButton>
              <UButton func="add" v-auth="'ai:test:task:add'">新增</UButton>
            </URow>
          </UViewSearch>
          <UViewTable :columns="testColumns" :scroll="{x: 1400}">
            <template #action="{record}">
              <UTag func="edit" :data="record" url="/ai/test/task/edit" v-auth="'ai:test:task:edit'">修改</UTag>
              <UTag func="del"  :data="record" url="/ai/test/task/del"  v-auth="'ai:test:task:del'">删除</UTag>
              <UTag v-if="record.status !== 'completed' && record.status !== 'failed'" func="ajax:confirm" :data="record" url="/ai/test/task/toggle" method="POST"
                :color="record.status === 'running' ? 'orange' : 'green'"
                :params="getTestToggleParams" :config="getTestToggleConfig(record)"
              >{{ record.status === 'running' ? '取消' : '开始' }}</UTag>
            </template>
          </UViewTable>
          <UViewModal title="测试任务" v-model="testEditModel" :rules="testRules" :span="[6, 16]">
            <template #default="{model}">
              <URow :responsive="1">
                <UInputItem field="taskName" label="任务名称" required/>
                <USelectItem field="trainTaskId" label="训练任务" required url="/ai/train/task/list" labelField="taskName" valueField="id"/>
                <!-- <UTextareaItem field="testReport" label="测试报告" :span="24" :rows="4"/> -->
                <UTextareaItem field="remark" label="备注" :span="24" :maxlength="256"/>
              </URow>
            </template>
            <template #footer="{model}">
              <UButton func="cancel">取消</UButton>
              <UButton func="submit" url="/ai/test/task/saveOrUpdate">提交</UButton>
              <UButton func="reset">重置</UButton>
            </template>
          </UViewModal>
        </UChildView>
      </a-tab-pane>

      <!-- 模型推理 -->
      <a-tab-pane key="inference" tab="模型推理">
        
      </a-tab-pane>
    </a-tabs>

    <!-- 训练指标抽屉 -->
    <UDrawerView uid="metricDrawer" :title="'训练指标 - ' + currentTaskName" :width="1200" @open="onMetricDrawerOpen" rowKey="id">
      <UViewSearch v-model="metricSearchModel">
        <URow col="search">
          <UInputNumberItem field="epoch" label="轮次" :min="1" :step="1"/>
          <UButton func="query" :url="'/ai/train/metric/view?taskId=' + (currentTaskId || '')">搜索</UButton>
          <UButton func="reset">重置</UButton>
        </URow>
      </UViewSearch>
      <UViewTable :columns="metricColumns" :scroll="{x: 1000}"></UViewTable>
    </UDrawerView>

  </UView>
</template>

<script>
import { GET } from '@/utils/request';
import { reactive, ref } from 'vue'

export default {
  name: 'AiTrainTask',
  setup() {
    const activeKey = ref('train')

    // ============ 训练任务相关 ============
    const statusOptions = ref([
      {label: '待训练', value: 'pending'},
      {label: '训练中', value: 'running'},
      {label: '已完成', value: 'completed'},
      {label: '已取消', value: 'cancelled'},
      {label: '失败', value: 'failed'},
    ])

    const trainColumns = reactive([
      { title: '任务名称', field: 'taskName', width: 150 },
      { title: '基于模型', field: 'modelId', width: 120, url: '/ai/model/list', labelField: 'name', valueField: 'id' },
      { title: '数据集', field: 'datasetId', width: 120, url: '/ai/dataset/list', labelField: 'name', valueField: 'id' },
      { title: '总轮次', field: 'totalEpochs', width: 100 },
      { title: '当前轮次', field: 'currentEpoch', width: 100 },
      { title: '学习率', field: 'lr', width: 100 },
      { title: '丢弃率', field: 'dropout', width: 100 },
      { title: '批尺寸', field: 'batchSize', width: 100 },
      { title: '开始时间', field: 'startTime', width: 160, type: 'date' },
      { title: '结束时间', field: 'endTime', width: 160, type: 'date' },
      { title: '创建时间', field: 'createTime', width: 160, type: 'date' },
      { title: '状态', field: 'status', width: 100, options: statusOptions.value, fixed: 'right' },
      { title: '进度', field: 'progress', width: 80, fixed: 'right' },
      { title: '操作', field: 'action', width: 260, type: 'action', fixed: 'right' }
    ])

    const rules = {
      taskName: { required: true, message: '任务名称必填' }
    }

    const trainSearchModel = ref({})
    const editModel = ref({})
    const modelParams = ref([])

    // ============ 测试任务相关 ============
    const testStatusOptions = ref([
      {label: '待测试', value: 'pending'},
      {label: '测试中', value: 'running'},
      {label: '已完成', value: 'completed'},
      {label: '失败', value: 'failed'},
    ])

    const testColumns = reactive([
      { title: '任务名称', field: 'taskName', width: 150 },
      { title: '训练任务', field: 'trainTaskId', width: 120, url: '/ai/train/task/list', labelField: 'taskName', valueField: 'id' },
      { title: '准确率', field: 'accuracy', width: 100 },
      { title: '精确率', field: 'precisionRate', width: 100 },
      { title: '召回率', field: 'recall', width: 100 },
      { title: '开始时间', field: 'startTime', width: 160, type: 'date' },
      { title: '结束时间', field: 'endTime', width: 160, type: 'date' },
      { title: '创建时间', field: 'createTime', width: 160, type: 'date' },
      { title: '状态', field: 'status', width: 100, options: testStatusOptions.value, fixed: 'right' },
      { title: '操作', field: 'action', width: 220, type: 'action', fixed: 'right' }
    ])

    const testRules = {
      taskName: { required: true, message: '任务名称必填' }
    }

    const testSearchModel = ref({})
    const testEditModel = ref({})

    // ============ 训练指标相关 ============
    const currentTaskId = ref(null)
    const currentTaskName = ref('')
    const metricSearchModel = ref({})

    const metricColumns = reactive([
      { title: '轮次', field: 'epoch', width: 80 },
      { title: '训练损失', field: 'trainLoss', width: 100 },
      { title: '验证损失', field: 'valLoss', width: 100 },
      { title: '训练准确率', field: 'trainAcc', width: 110 },
      { title: '验证准确率', field: 'valAcc', width: 110 },
      { title: '学习率', field: 'learningRate', width: 100 },
      { title: '记录时间', field: 'recordTime', width: 160, type: 'date' }
    ])

    const onMetricDrawerOpen = (row) => {
      currentTaskId.value = row?.id
      currentTaskName.value = row?.taskName || ''
    }

    // ============ 推理测试相关 ============
    const inferenceSearchModel = ref({})
    const inferenceEditModel = ref({})

    const inferenceRules = {
      modelId: { required: true, message: '推理模型必选' }
    }

    const onInferenceEdit = (model) => {
      if (!model.id) {
        model.modelId = inferenceSearchModel.value.modelId
      }
    }

    const onModelChange = (value, select) => {
      if (value) {
        GET('/ai/model/param/list', { modelId: value }).then(({code, data, message}) => {
          modelParams.value = data  || [];
          if(modelParams.value.length > 0 && !editModel.value.id) { // 新增
            modelParams.value.forEach((param) => {
              editModel.value.config[param.paramKey] = param.paramValue
            })
          }
        })
      } else {
        modelParams.value = []
      }
    }

    const onOpenEdit = (model) => {
      modelParams.value = []; // 清空模型参数
      if (model.id) {
        onModelChange(model.modelId, null);
      } else {
        editModel.value.config = {}
      }
    }

    // 切换按钮：根据当前状态返回目标状态
    const getToggleParams = (data, funcPath, rowKey) => {
      const targetStatus = data.status === 'running' ? 'cancelled' : 'running'
      return Promise.resolve({ id: data[rowKey], status: targetStatus })
    }

    // 切换按钮：根据当前状态返回确认配置
    const getToggleConfig = (record) => {
      return record.status === 'running'
        ? { confirmTitle: '确定取消训练？' }
        : { confirmTitle: '确定开始训练？' }
    }

    // 测试任务切换按钮
    const getTestToggleParams = (data, funcPath, rowKey) => {
      const targetStatus = data.status === 'running' ? 'cancelled' : 'running'
      return Promise.resolve({ id: data[rowKey], status: targetStatus })
    }

    const getTestToggleConfig = (record) => {
      return record.status === 'running'
        ? { confirmTitle: '确定取消测试？' }
        : { confirmTitle: '确定开始测试？' }
    }

    return {
      activeKey,
      trainColumns, rules, trainSearchModel, editModel, statusOptions,
      testColumns, testRules, testSearchModel, testEditModel, testStatusOptions,
      inferenceSearchModel, inferenceEditModel, inferenceRules, onInferenceEdit,
      modelParams, onModelChange, onOpenEdit, getToggleParams, getToggleConfig,
      getTestToggleParams, getTestToggleConfig,
      currentTaskId, currentTaskName, metricSearchModel, metricColumns, onMetricDrawerOpen
    }
  },
}
</script>
