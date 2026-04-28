<template>
  <UView name="开放平台">
    <UViewSearch v-model="searchModel">
      <URow col="search">
        <UInputItem field="clientName" label="应用名称" :allowClear="true"/>
        <UButton func="query" url="/oauth2/app/view">搜索</UButton>
        <UButton func="reset">重置</UButton>
        <UButton func="add" url="/oauth2/app/add" v-auth="'oauth2:app:add'">新增</UButton>
      </URow>
    </UViewSearch>
    <UViewModal title="开放平台管理" :span="[7, 14]" v-model="editModel" :rules="rules">
      <template #default="{model}">
        <UInputItem field="clientName" label="应用名称" allowClear />
        <USelectItem field="adminAccount" label="关联账号" url="/oauth2/app/adminAccounts" />
<!--        <UInputItem field="contractScope" label="应用签约权限" />-->
        <UTextareaItem field="allowUrl" label="授权主机" extra="多个主机地址用换行符分隔" />
        <UTextareaItem field="remark" label="备注" />
      </template>
      <template #footer>
        <UButton func="cancel">取消</UButton>
        <UButton func="submit" url="/oauth2/app/saveOrUpdate">提交</UButton>
        <UButton func="reset">重置</UButton>
      </template>
    </UViewModal>
    <UViewTable :columns="columns" :scroll="{x: 1200}">
      <template #clientId="{record}">
        <a-typography-paragraph :copyable="{text: record.clientId, tooltip: true}" style="margin-bottom: 0px">
          <template #copyableIcon>
            <span>{{record.clientId}}</span>
          </template>
        </a-typography-paragraph>
      </template>
      <template #clientSecret="{record}">
        <a-typography-paragraph :copyable="{text: record.clientSecret, tooltip: true}" style="margin-bottom: 0px">
          <template #copyableIcon>
            <span>{{record.clientSecret}}</span>
          </template>
        </a-typography-paragraph>
      </template>
      <template #status="{record}">
        <span>{{record.status ? "启用" : "停用"}}</span>
      </template>
      <template #action="{record}">
        <UTag func="ajax" :data="{clientId: record.clientId, status: (record.status!==true)}" v-auth="'oauth2:app:status'"
              url="/oauth2/app/status">{{record.status ? "停用" : "启用"}}</UTag>
        <UTag func="edit" :data="record" url="/oauth2/app/edit" v-auth="'oauth2:app:edit'">修改</UTag>
        <UTag func="del" :data="record" :url="`/oauth2/app/del?clientId=${record.clientId}`" :params="delParams" v-auth="'oauth2:app:del'">删除</UTag>
      </template>
    </UViewTable>
  </UView>
</template>
<!-- oauth2应用功能 -->
<script>
import {ref, reactive} from "vue";
export default {
  name: "Oauth2App",
  setup() {
    let columns = ref([
      {field: "clientName", title: "应用名称", width: 220},
      {field: "adminAccount", title: "管理员账号", width: 120, resizable: true, url: "/oauth2/app/adminAccounts"},
      {field: "clientId", title: "应用标识", width: 260, resizable: true},
      {field: "clientSecret", title: "应用密钥", width: 280, resizable: true},
      // {field: "allowUrl", title: "授权url", width: 220, resizable: true},
      // {field: "contractScope", title: "应用权限", width: 220, resizable: true},
      {field: "remark", title: "备注", width: 220},
      {field: "createTime", title: "创建时间", type: 'date', format: 'YY-MM-DD HH:MM', width: 120},
      {field: "status", title: '状态', fixed: "right", width: 80},
      {field: 'action', title: '操作', type: 'action', fixed: 'right', width: 180},
      // {field: "accessTokenTimeout", title: "access_token保存的时间"},
      // {field: "refreshTokenTimeout", title: "refresh-token保存的时间"},
      // {field: "clientTokenTimeout", title: "client-token保存的时间"},
      // {field: "pastTokenTimeout", title: "past-client-token保存的时间"},
      ]);

    let rules = reactive({
      clientId: {required: true, message: '应用标识必填'},
      clientName: {required: true, message: '应用名称必填'},
      adminAccount: {required: true, message: '管理员账号必填'},
    })

    let editModel = ref({});
    let searchModel = ref({});
    return {columns, rules, editModel, searchModel}
  },
  methods: {
    delParams(value) {
      return Promise.resolve();
    }
  }
}
</script>
<style scoped> </style>
