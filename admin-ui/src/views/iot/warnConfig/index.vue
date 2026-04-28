<template>
  <UView name="告警配置">
    <UViewSearch v-model="searchModel">
      <URow col="search">
        <UInputItem field="name" label="告警名称" allowClear/>
        <USelectItem field="productId" label="所属产品" url="/iot/product/list?deviceTypes=Direct,Child" allowClear />
        <USelectItem field="grade" label="告警等级" url="/iot/warnConfig/grades" optionType="button" buttonStyle="solid"/>
        <UButton func="query" url="/iot/warnConfig/view">搜索</UButton>
        <UButton func="reset">重置</UButton>
        <UButton func="add" url="/iot/warnConfig/add" v-auth="'iot:warnConfig:add'">新增</UButton>
      </URow>
    </UViewSearch>
    <UViewModal title="告警配置管理" :span="[5, 17]" v-model="editModel" :rules="rules" width="700px">
      <template #default="{model}">
        <UInputItem field="name" label="告警名称" :maxlength="32" />
        <URadioItem field="grade" label="告警等级" url="/iot/warnConfig/grades" optionType="button" buttonStyle="solid"/>
        <UInputNumberItem field="frequency" label="告警频率(秒)" :min="0" extra="在此频率范围内不重复告警, 0为实时告警"
                          :step="1" :defaultValue="0"/>
        <USelectItem field="productId" label="所属产品" treeDefaultExpandAll url="/iot/product/list?deviceTypes=Direct,Child"/>
        <USelectItem v-if="model.grade != 'status'" field="modelAttrId" label="告警属性" labelField="name" valueField="id"
                 url="/iot/modelAttr/listByProductId?productId={productId}" @change="modelAttrChange"/>
        <UInputItem field="template" label="告警内容" extra="支持占位符告警的设备编号{deviceSn}、值{value}、时间{warnTime}、等级{grade}" :maxlength="128"/>
        <USelectItem field="messageTemplateId" label="消息模板" url="/core/messageTemplate/list"
                     labelField="templateName" valueField="id" extra="用于短信或者邮件等通知"/>
        <UInputItem field="remark" label="告警说明" :maxlength="64" />
      </template>
      <template #footer>
        <UButton func="cancel">取消</UButton>
        <UButton func="submit" url="/iot/warnConfig/saveOrUpdate">提交</UButton>
        <UButton func="reset">重置</UButton>
      </template>
    </UViewModal>
    <UViewTable :columns="columns" :scroll="{x: 1200}">
      <template #action="{record}">
        <UTag func="edit" :data="record" url="/iot/warnConfig/edit" v-auth="'iot:warnConfig:edit'">修改</UTag>
        <UTag func="del" :data="record" url="/iot/warnConfig/del" v-auth="'iot:warnConfig:del'">删除</UTag>
      </template>
    </UViewTable>
  </UView>
</template>
<script>
import {ref, reactive} from "vue";
export default {
  name: "WarnConfig",
  setup() {
    let columns = ref([
      {field: "name", title: '告警名称', width: 180},
      {field: "gradeName", title: "告警等级", width: 80},
      {field: "productName", title: "所属产品", width: 160},
      {field: "template", title: "告警模板", width: 260, resizable: true},
      {field: "remark", title: "告警说明", width: 220, resizable: true},
      {field: "createTime", title: "创建时间", type: 'date', format: 'YYYY-MM-DD', width: 120},
      {field: 'action', title: '操作', type: 'action', fixed: 'right', width: 180},
    ]);

    let rules = reactive({
      name: {required: true, message: '告警名称必填'},
      grade: {required: true, message: '告警等级必填'},
      template: {required: true, message: '告警模板必填'},
      frequency: {required: true, message: '告警频率必填'},
      productId: {required: true, message: '所属产品必填'},
      modelAttrId: {required: true, message: '告警属性必填'},
    })

    let editModel = ref({});
    let searchModel = ref({});
    return {columns, rules, editModel, searchModel}
  },
  methods: {
    modelAttrChange(value, model) {
      this.editModel['modelAttrField'] = model['field'];
    }
  }
}
</script>
<!-- 告警配置功能 -->
<style scoped> </style>
