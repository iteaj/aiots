<template>
  <UView name="告警消息模板">
    <UViewSearch v-model="searchModel">
      <URow col="search">
        <UInputItem field="templateName" label="模板名称" :allowClear="true"/>
        <USelectItem field="acceptId" label="接收人" url="/core/messageTemplate/accepts" allowClear />
        <UButton func="query" url="/core/messageTemplate/view">搜索</UButton>
        <UButton func="reset">重置</UButton>
        <UButton func="add" url="/core/messageTemplate/add" v-auth="'iot:messageTemplate:add'">新增</UButton>
      </URow>
    </UViewSearch>
    <UViewModal title="消息模板管理" :span="[5, 17]" width="720px" v-model="editModel" :rules="rules">
      <template #default="{model}">
<!--        <UInputItem field="type" label="消息类型" />-->
        <UInputItem field="templateName" label="名称" />
        <USelectItem field="accepts" label="接收人列表" url="/core/messageTemplate/accepts" :defaultValue="[]"
                     extra="根据配置的手机号等信息自动选择发送" mode="multiple" :maxTagCount="3" />
        <UInputItem field="templateId" label="短信模板编号" extra="支持的模板变量(deviceName、deviceSn、grade、value、warnTime)" />
<!--        <UTextareaItem field="content" label="发送内容" extra="不指定则使用告警内容" :autosize="{minRows: 3}"-->
<!--                       placeholder="支持占位符 deviceSn(设备编号)、grade(告警等级)、value(告警值)、warnTime(告警时间)" />-->
        <UTextareaItem field="remark" label="备注" />
      </template>
      <template #footer>
        <UButton func="cancel">取消</UButton>
        <UButton func="submit" url="/core/messageTemplate/saveOrUpdate">提交</UButton>
        <UButton func="reset">重置</UButton>
      </template>
    </UViewModal>
    <UViewTable :columns="columns" :scroll="{x: 1100}">
      <template #id="{record}">
        <a-typography-paragraph :copyable="{text: record.id + '', tooltip: true}" style="margin-bottom: 0">
          {{record.id}}
        </a-typography-paragraph>
      </template>
      <template #title>
        <div>
          <span style="color: #f8a269">告警模板变量包含: </span>
          <span style="color: #a8a8a8">deviceSn(设备编号)、 deviceName(设备名称)、grade(告警等级)、value(告警值)、warnTime(告警时间)</span>
        </div>
      </template>
      <template #action="{record}">
        <UTag func="edit" :data="record" url="/core/messageTemplate/edit" v-auth="'iot:messageTemplate:edit'">修改</UTag>
        <UTag func="del" :data="record" url="/core/messageTemplate/del" v-auth="'iot:messageTemplate:del'">删除</UTag>
      </template>
    </UViewTable>
  </UView>
</template>
<!-- 消息模板功能 -->
<script>
import {ref, reactive} from "vue";
export default {
  name: "WarnMessageTemplate",
  setup() {
    let columns = ref([
      {field: "id", title: "消息模板ID", width: 120},
      {field: "templateName", title: "消息模板名称", width: 160},
      {field: "templateId", title: "短信模板编号", width: 160},
      {field: "acceptNames", title: "接收人", width: 280, resizable: true},
      // {field: "content", title: "发送内容"},
      {field: "remark", title: "备注", width: 280, resizable: true},
      {field: "createTime", title: "创建时间", width: 150},
      {field: 'action', title: '操作', type: 'action', fixed: 'right', width: 120},
    ]);

    let rules = reactive({
      accepts: {required: true, message: '接收人必填'},
      templateName: {required: true, message: '模板名称必填'},
    })

    let editModel = ref({type: 'iot:warn'});
    let searchModel = ref({type: 'iot:warn'});
    return {columns, rules, editModel, searchModel}
  },
}
</script>
<style scoped> </style>
