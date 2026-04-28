<template>
  <UView name="数据集">
    <UViewSearch v-model="searchModel">
      <URow col="search">
        <UInputItem field="name" label="设备组" :allowClear="true"/>
<!--        <USelectItem field="type" label="源类型" :options="type" :allowClear="true"/>-->
        <UButton func="query" url="/iot/screen/listDatasource">搜索</UButton>
        <UButton func="reset">重置</UButton>
<!--        <UButton func="add" v-auth="'iot:screen:addDatasource'">新增</UButton>-->
      </URow>
    </UViewSearch>
<!--    <UViewModal title="数据源管理" :span="[7, 14]" v-model="editModel" :rules="rules">-->
<!--      <template #default="{model}">-->
<!--        <UInputItem field="name" label="名称" />-->
<!--        <UTreeSelectItem field="deviceGroupId" label="所属设备组" url="/iot/deviceGroup/tree" labelField="name" valueField="id" />-->
<!--        &lt;!&ndash;        <USelectItem field="type" label="类型" :options="type" />&ndash;&gt;-->
<!--      </template>-->
<!--      <template #footer>-->
<!--        <UButton func="cancel">取消</UButton>-->
<!--        <UButton func="submit" url="/iot/screen/saveOrUpdate">提交</UButton>-->
<!--        <UButton func="reset">重置</UButton>-->
<!--      </template>-->
<!--    </UViewModal>-->
    <UViewTable :columns="columns">
      <template #title>
        <div>
          <div style="color: #ed4014; font-size: 12px">注：乐吾乐图纸配置说明</div>
          <div>
            1. 乐吾乐编辑器导入iboot平台模型属性流程:
            <div>
              <div style="margin-left: 15px; font-size: 12px">
                <span style="color: #3d62ab">2d导入方式:</span>
                <span style="color: #3e3e3e;"> 在乐吾乐[2d]编辑器下 点击右侧栏的[通讯]栏目 -> 拉到最下方的获取变量树 ->
                  <a-typography-paragraph :copyable="{text: $env.http.getApiUrl() + '/iot/le5le/api/2d'}" style="margin-bottom: 0px; display: inline-block">
                    <template #copyableIcon>
                      <span>复制地址填入</span>
                    </template>
                  </a-typography-paragraph>
                </span>
              </div>
              <div style="margin-left: 15px; font-size: 12px">
                <span style="color: #3d62ab">大屏导入方式:</span>
                <span style="color: #3e3e3e;"> 在乐吾乐[大屏]编辑器的[数据]面板下 -> 新增属性 -> 在线导入(复制表格的大屏地址)</span>
              </div>
            </div>
          </div>
          <div>
            2. 乐吾乐编辑器初始化iboot平台插件流程: 进入图纸编辑器 -> 点击画布 -> 右侧导航栏
            <div>
              <div style="margin-left: 15px; font-size: 12px">
                <span style="color: #3d62ab">2d初始化方式:</span>
                <span style="color: #3e3e3e;"> 在乐吾乐[2d]编辑器下 点击右侧栏的[图纸]栏目 -> 点击[初始化js] ->
                <a-typography-paragraph :copyable="{text: `
import('${getPluginUrl()}').then(() => {
    // url为接口请求的基础地址, wsUrl为ws接口请求的基础地址
    init_le5le_plugin_2d(meta2d, { url: null, wsUrl: null });
})
                `}" style="margin-bottom: 0px; display: inline-block">
                    <template #copyableIcon>
                      <span>复制代码填入</span>
                    </template>
                  </a-typography-paragraph>
                </span>
              </div>
              <div style="margin-left: 15px; font-size: 12px">
                <span style="color: #3d62ab">大屏初始化方式:</span>
                <span style="color: #3e3e3e;"> 在乐吾乐[大屏]编辑器下 点击右侧的[画布]栏目 -> 点击[初始化动作] ->
                  <a-typography-paragraph :copyable="{text: `
import('${getPluginUrl()}').then(() => {
    // url为接口请求的基础地址, wsUrl为ws接口请求的基础地址
    init_le5le_plugin(meta2d, { url: null, wsUrl: null });
})
                `}" style="margin-bottom: 0px; display: inline-block">
                    <template #copyableIcon>
                      <span>复制代码填入</span>
                    </template>
                  </a-typography-paragraph>
                </span>
              </div>
            </div>
          </div>
        </div>
      </template>
      <template #url2d="{record}">

      </template>
      <template #url="{record}">
        <a-typography-paragraph :copyable="{text: $env.http.getApiUrl() + '/iot/le5le/api/attr?groupId=' + record.id}" style="margin-bottom: 0px">
          <template #copyableIcon>
            <span>{{$env.http.getApiUrl() + '/iot/le5le/api/attr?groupId=' + record.id}}</span>
          </template>
        </a-typography-paragraph>
      </template>
      <template #action="{record}">
<!--        <UTag func="edit" :data="record" url="/iot/screen/getDatasource" v-auth="'iot:screen:editDatasource'">修改</UTag>-->
<!--        <UTag func="del" :data="record" url="/iot/screen/delDatasource" v-auth="'iot:screen:delDatasource'">删除</UTag>-->
        <UTag func="ajax" :data="{}" :url="`/iot/screen/syncDatasource?id=${record.id}`" color="#55acee" :disabled="true" v-auth="'iot:screen:syncDatasource'">同步</UTag>
      </template>
    </UViewTable>
  </UView>
</template>
<!-- 串口设备功能 -->
<script>
import {ref, reactive} from "vue";
export default {
  name: "Datasource",
  setup() {

    let type = ref([
      {label: '发布', value: 'publish'},
      {label: '订阅', value: 'subscribe'},
      {label: '数据集', value: 'dataset'},
    ])

    let columns = ref([
      {field: "name", title: "设备组", width: 280},
      {field: "type", title: "类型", options: type.value, width: 120},
      {field: "url", title: "大屏在线接口"},
      {field: 'action', title: '操作', type: 'action', width: 180},
    ]);

    let rules = reactive({
      name: {required: true, message: '名称必填'},
      type: {required: true, message: '类型必填'},
      deviceGroupId: {required: true, message: '设备组必填'},
    })


    let editModel = ref({type: 'dataset'});
    let searchModel = ref({type: 'dataset'});
    return {columns, rules, editModel, searchModel, type}
  },
  methods: {
    getPluginUrl() {
      let dev = this.$env.isDev;
      let baseUrl = `${window.location.protocol}//${window.location.host}`;
      return dev ? `${baseUrl}/src/plugin/le5le/le5le_plugin.js` : `${baseUrl}/assets/plugin/le5le.js`;
    }
  }
}
</script>
<style scoped> </style>
