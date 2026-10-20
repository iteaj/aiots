<template>
  <UView >
    <UViewSearch v-model="searchModel">
      <URow col="search">
        <UInputItem field="alarmType" label="" :allowClear="true"/>
        <UButton func="query" url="/iot/deviceAlarm/view">搜索</UButton>
        <UButton func="reset">重置</UButton>
        <UButton func="add" url="/iot/deviceAlarm/add" v-auth="'iot:deviceAlarm:add'">新增</UButton>
      </URow>
    </UViewSearch>
    <UViewTable :columns="columns" :scroll="{x: 1200}">
      <template #action="{record}">
        <UTag func="edit" :data="record" url="/iot/deviceAlarm/edit" v-auth="'iot:deviceAlarm:edit'">修改</UTag>
        <UTag func="del" :data="record" url="/iot/deviceAlarm/del" v-auth="'iot:deviceAlarm:del'">删除</UTag>
      </template>
    </UViewTable>
  </UView>
</template>
<!-- 功能 -->
<script>
import {ref} from "vue";

export default {
  name: "DeviceAlarm",
  setup() {
    let columns = ref([
      {field: "deviceId", title: "国标编号"},
      {field: "channelId", title: "通道编号"},
      {field: "alarmPriority", title: "报警级别"},
      {field: "alarmMethod", title: "报警方式"},
      {field: "longitude", title: "经度"},
      {field: "latitude", title: "纬度"},
      {field: "alarmType", title: "报警类型"},
      {field: "createTime", title: "创建时间"},
      {field: "alarmTime", title: "报警时间", fixed: 'right'},
      {field: "alarmDescription", title: "报警内容", fixed: 'right'},
      {field: 'action', title: '操作', type: 'action', fixed: 'right', width: 180},
    ]);

    let editModel = ref({});
    let searchModel = ref({});
    return {columns, editModel, searchModel}
  },
}
</script>
<style scoped> </style>
