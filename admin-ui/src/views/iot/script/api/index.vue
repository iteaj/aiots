<template>
  <UView name="属性解析">
    <UViewSearch>
      <URow col="search">
        <UInputItem field="name" label="配置名称"/>
        <UFuncBtn func="query" url="/iot/script/api/view">搜索</UFuncBtn>
        <UFuncBtn func="reset">重置</UFuncBtn>
        <UFuncBtn func="add" url="/iot/script/api/add" v-auth="'iot:script:api:add'">新增</UFuncBtn>
        <UButton func="open" uid="logger" :data="{}" type="dashed">运行日志</UButton>
      </URow>
    </UViewSearch>
    <UViewTable :columns="columns" :scroll="{x: 1000}">
      <template #title>
        <span style="color: #b8b8b8; font-weight: 0;">注: 此解析器用于功能模型的下行配置, 支持通过脚本自定义要写的数据</span>
      </template>
      <template #c_action="{record}">
        <UTag func="edit" :data="record" url="/iot/script/edit" v-auth="'iot:script:api:edit'">修改</UTag>
        <UTag func="del" :data="record" url="/iot/script/api/del" v-auth="'iot:script:api:del'">删除</UTag>
        <UTag func="open" uid="bind" :url="`/iot/script/api/binds?id=${record.id}`" :data="[]">已绑定列表</UTag>
      </template>
    </UViewTable>
    <UDrawerLogger uid="logger"/>
    <UModal uid="bind" title="已绑定的接口列表" :footer="null" width="60%" v-model:source="bindDataSource">
      <UTable uid="bindTable" :columns="bindColumns" :dataSource="bindDataSource">
        <template #productName="{record}">
          <router-link :to="`/iot/product?code=${record['productCode']}`">{{record['productName']}}</router-link>
        </template>
      </UTable>
    </UModal>
    <UViewDrawer v-model="editModel" title="解析器管理" :rules="rules" @edit="openEdit" placement="top" size="100%">
      <template #default="{model}">
        <URow col="drawer">
          <UInputItem field="name" label="解析器名称"/>
          <URadioItem v-if="configs.length > 0" field="language" label="脚本语言" :options="scriptOptions"
                      optionType="button" buttonStyle="solid"/>
          <UInputItem field="remark" label="备注"/>
        </URow>
        <a-tabs v-model:activeKey="languageKey" tab-placement="left" :items="scriptItems">
          <template #contentRender="{item}">
            <div style="height: 380px">
              <AceScriptEditor v-model:value="item.content.script" :completions="completions"></AceScriptEditor>
            </div>
          </template>
        </a-tabs>
      </template>
      <template #footer="{model}">
        <UButton func="cancel">取消</UButton>
        <UButton func="submit" url="/iot/script/api/saveOrUpdate">提交</UButton>
        <UButton func="reset">重置</UButton>
      </template>
    </UViewDrawer>
  </UView>
</template>
<script>

import {ref} from "vue";
import CoreConsts from "@/components/CoreConsts";
import AceScriptEditor from "@/components/code/AceScriptEditor.vue";
export default {
  name: "ApiScript",
  components: {AceScriptEditor},
  setup() {
    let scriptOptions = ref([{label: '禁用', value: 'disabled'}]);

    let columns = [
      {field: 'name', title: '配置名称', width: 250, resizable: true},
      {field: 'language', title: '语言', width: 120, options: scriptOptions.value},
      {field: 'remark', title: '备注'},
      {field: 'updateTime', title: '更新时间', type: 'date', format: 'YYYY-MM-DD HH:mm', width: 130},
      {field: 'action', type: 'action', title: '操作', width: 200}
    ]
    let bindColumns = [
      {field: 'name', title: '接口名称', width: 150},
      {field: 'attrName', title: '下行配置项(功能模型)', width: 180},
      {field: 'apiCode', title: '接口码', width: 150},
      {field: 'productName', title: '所属产品', width: 180},
      {field: 'productCode', title: '产品编码', width: 150},
    ]
    let rules = {
      name: {required: true, message: '解析器名称必填'},
      language: {required: true, message: '使用语言必填'},
    }
    let scriptItems = ref([]);
    let bindDataSource = ref([]);
    let editModel = ref({language: 'disabled'});
    let configs = ref([]);
    let languageKey = ref("");
    let completions = ref([]);
    return {editModel, columns, rules, completions, languageKey, configs, bindColumns, bindDataSource, scriptOptions, scriptItems}
  },
  mounted() {
    this.$http.get("/iot/script/api/completes").then(({code, message, data}) => {
      if(code == CoreConsts.SuccessCode) {
        if(data && data.length > 0) {
          data.forEach(item => this.completions.push(item))
        }
      } else {
        this.$msg.error(message);
      }
    });
    this.$http.get("/iot/script/api/configs").then(({code, message, data}) => {
      if(code == CoreConsts.SuccessCode) {
        this.configs = data;
        if(data && data.length > 0) {
          data.forEach(item => {
            item['key'] = item['label'] = item.language;
            this.scriptOptions.push({label: item.language, value: item.language});
          })
        }
      } else {
        this.$msg.error(message);
      }
    });
  },
  methods: {
    openEdit(model) {
      if(!model.id) {
        model['script'] = JSON.parse(JSON.stringify(this.configs));
      }

      this.scriptItems = model['script'].map(item => {
        let key = new Date().getTime() + "";
        return {key, label: item.language, content: item};
      });
      this.languageKey = this.scriptItems[0].key
    }
  }
}
</script>

<style scoped>

</style>
