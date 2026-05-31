<template>
  <UView name="设备指令日志">
    <UViewSearch v-model="searchModel">
      <URow col="search">
        <UInputItem field="uid" label="设备标识" allowClear/>
        <UInputItem field="deviceName" label="设备名称" allowClear/>
        <USelectItem field="status" label="执行状态" url="/iot/deviceCmdLog/statusList" allowClear />
        <USelectItem field="method" label="调用场景" url="/iot/deviceCmdLog/methodList" allowClear />
        <USelectItem field="cmdType" label="指令类型" url="/iot/deviceCmdLog/cmdTypeList" allowClear />
        <UButton func="query" url="/iot/deviceCmdLog/view">搜索</UButton>
        <UButton func="reset">重置</UButton>
      </URow>
    </UViewSearch>
    <UViewTable :columns="columns" :scroll="{x: 1200}">
      <template #config_cmdName="{record, text}">
        <a-tooltip>
          <template #title>
            {{ record.config?.cmdParams || text }}
          </template>
          <span>{{ text || record.config?.cmdParams }}</span>
        </a-tooltip>
      </template>
      <template #status="{text, value}">
        <a-tag variant="filled" :color="text != 'SUCCESS' ? 'error' : 'success'">{{value}}</a-tag>
      </template>
      <template #action="{record}">
        <UTag func="del" :data="record" url="/iot/deviceCmdLog/del" v-auth="'iot:deviceCmdLog:del'">删除</UTag>
      </template>
    </UViewTable>
  </UView>
</template>

<script>
import {ref} from "vue";
export default {
  name: "DeviceCmdLog",
  setup() {
    let columns = ref([
      {field: "uid", title: "设备标识", width: 168},
      {field: "deviceName", title: "设备名称", width: 150},
      {field: "cmdType", title: "指令类型", width: 80, url: "/iot/deviceCmdLog/cmdTypeList"},
      {field: "config.cmdName", title: "模型指令", width: 200},
      {field: "method", title: "调用场景", width: 100, url: "/iot/deviceCmdLog/methodList"},
      {field: "resultMsg", title: "错误信息", width: 230, resizable: true},
      {field: "elapsed", title: "耗时(ms)", width: 80},
      {field: "operator", title: "操作人", width: 100},
      {field: "createTime", title: "下发时间", type: 'date', width: 160},
      {field: "status", title: "执行状态", width: 80, url: "/iot/deviceCmdLog/statusList", fixed: 'right'},
      {field: 'action', title: '操作', type: 'action', fixed: 'right', width: 100},
    ]);

    let searchModel = ref({});
    return {columns, searchModel}
  },
}
</script>
<style scoped> </style>
