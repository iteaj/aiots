<template>
  <UView name="属性解析">
    <UViewSearch>
      <URow col="search">
        <UInputItem field="name" label="配置名称"/>
        <UFuncBtn func="query" url="/iot/script/attr/view">搜索</UFuncBtn>
        <UFuncBtn func="reset">重置</UFuncBtn>
        <UFuncBtn func="add" url="/iot/script/attr/add" v-auth="'iot:script:attr:add'">新增</UFuncBtn>
        <UButton func="open" uid="logger" :data="{}" type="dashed">运行日志</UButton>
      </URow>
    </UViewSearch>
    <UViewTable :columns="columns" :scroll="{x: 1000}">
      <template #title>
        <span style="color: #a8a8a8">此解析器用于模型属性的值解析器, 支持通过脚本对采集到的数据进行处理</span>
      </template>
      <template #c_action="{record}">
        <UFuncTag func="edit" :data="record" url="/iot/script/edit" v-auth="'iot:script:attr:edit'">修改</UFuncTag>
        <UFuncTag func="del" :data="record" url="/iot/script/attr/del" v-auth="'iot:script:attr:del'">删除</UFuncTag>
        <UFuncTag func="open" uid="bind" :url="`/iot/script/attr/binds?id=${record.id}`" :data="[]">已绑定列表</UFuncTag>
      </template>
    </UViewTable>
    <UModal uid="bind" title="已绑定的模型属性列表" :footer="null" width="60%" v-model:source="bindDataSource">
      <UTable uid="bindTable" :columns="bindColumns" :dataSource="bindDataSource">
        <template #productName="{record}">
          <router-link :to="`/iot/product?code=${record['productCode']}`">{{record['productName']}}</router-link>
        </template>
      </UTable>
    </UModal>
    <UDrawerLogger uid="logger"/>
    <UViewDrawer v-model="editModel" title="解析器管理" :rules="rules" @edit="openEdit" placement="top" height="100%">
      <template #default="{model}">
        <URow col="drawer">
          <UInputItem field="name" label="解析器名称"/>
          <URadioItem v-if="configs.length > 0" field="language" label="使用语言" :options="configs" required
                      labelField="language" valueField="language" optionType="button" buttonStyle="solid"/>
          <UInputItem field="remark" label="备注"/>
        </URow>
        <ATabs v-model:activeKey="languageKey" tab-position="left">
          <template v-for="language in model['script']" :key="language.language">
            <a-tab-pane :tab="language.language">
              <div style="height: 320px">
                <AceScriptEditor v-model:value="language.script" :completions="completions"></AceScriptEditor>
              </div>
            </a-tab-pane>
          </template>
        </ATabs>
      </template>
      <template #footer="{model}">
        <UFuncBtn func="cancel">取消</UFuncBtn>
        <UFuncBtn func="submit" url="/iot/script/attr/saveOrUpdate">提交</UFuncBtn>
        <UFuncBtn func="reset">重置</UFuncBtn>
      </template>
    </UViewDrawer>
  </UView>
</template>
<script>

import {ref} from "vue";
import CoreConsts from "@/components/CoreConsts";
import AceScriptEditor from "@/components/code/AceScriptEditor.vue";
export default {
  name: "AttrScript",
  components: {AceScriptEditor},
  setup() {
    let columns = [
      {field: 'name', title: '配置名称', width: 250},
      {field: 'language', title: '语言', width: 120},
      {field: 'remark', title: '备注'},
      {field: 'updateTime', title: '更新时间', type: 'date', format: 'YYYY-MM-DD HH:mm', width: 130},
      {field: 'action', type: 'action', title: '操作', width: 200}
    ]
    let bindColumns = [
      {field: 'name', title: '属性名称', width: 150},
      {field: 'attrName', title: '模型属性', width: 150},
      {field: 'productName', title: '所属产品', width: 200},
      {field: 'productCode', title: '产品编码', width: 200},
    ]
    let rules = {
      name: {required: true, message: '解析器名称必填'},
      language: {required: true, message: '使用语言必填'},
    }
    let bindDataSource = ref([]);
    let editModel = ref({});
    let configs = ref([]);
    let languageKey = ref("");
    let completions = ref([]);
    return {editModel, columns, rules, completions, languageKey, configs, bindColumns, bindDataSource}
  },
  mounted() {
    this.$http.get("/iot/script/attr/completes").then(({code, message, data}) => {
      if(code == CoreConsts.SuccessCode) {
        if(data && data.length > 0) {
          data.forEach(item => this.completions.push(item))
        }
      } else {
        this.$msg.error(message);
      }
    });
    this.$http.get("/iot/script/attr/configs").then(({code, message, data}) => {
      if(code == CoreConsts.SuccessCode) {
        this.configs = data;
        if(data && data.length > 0) {
          this.languageKey = data?.[0]?.language;
        }
      } else {
        this.$msg.error(message);
      }
    })
  },
  methods: {
    openEdit(model) {
      if(!model.id) {
        model['script'] = JSON.parse(JSON.stringify(this.configs));
      }
    }
  }
}
</script>

<style scoped>

</style>
