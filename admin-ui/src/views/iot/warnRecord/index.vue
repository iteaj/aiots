<template>
  <UView name="告警记录">
    <UViewSearch v-model="searchModel">
      <URow col="search">
        <UInputItem field="warnConfigName" label="告警名称" allowClear/>
        <UInputItem field="deviceName" label="设备名称" allowClear/>
        <USelectItem field="productId" label="所属产品" url="/iot/product/list" allowClear />
        <URadioItem :span="16" field="grade" label="告警等级" url="/iot/warnConfig/grades" optionType="button" buttonStyle="solid"/>
        <UButton func="query" url="/iot/warnRecord/view">搜索</UButton>
        <UButton func="reset">重置</UButton>
      </URow>
    </UViewSearch>
    <UViewTable :columns="columns" :scroll="{x: 1000}">
      <template #action="{record}">
        <UTag func="del" :data="record" url="/iot/warnRecord/del" v-auth="'iot:warnRecord:del'">删除</UTag>
      </template>
    </UViewTable>
  </UView>
</template>
<!-- 告警记录功能 -->
<script>
import {ref, reactive} from "vue";
export default {
  name: "WarnRecord",
  setup() {
    let columns = ref([
      {field: "deviceName", title: "告警设备", width: 150},
      {field: "warnConfigName", title: "所属告警", width: 150},
      {field: "deviceSn", title: "设备编号", width: 150},
      {field: "gradeName", title: "告警等级", width: 80},
      {field: "content", title: "告警内容", width: 360, resizable: true},
      {field: "warnTime", title: "告警时间", type: 'date', width: 150},
      {field: "createTime", title: "创建时间", type: 'date', format: 'YYYY-MM-DD', width: 100},
      {field: 'action', title: '操作', type: 'action', fixed: 'right', width: 100},
    ]);

    let rules = reactive({
        warnConfigId: {required: true, message: '所属告警配置id必填'},
        createTime: {required: true, message: '必填'},
    })

    let editModel = ref({});
    let searchModel = ref({});
    return {columns, rules, editModel, searchModel}
  },
}
</script>
<style scoped> </style>
