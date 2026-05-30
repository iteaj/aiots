<template>
  <UView>
    <ARow :gutter="8" style="height: 100%; min-width: 1230px">
      <ACol span="7" style="height: 100%">
<!--        <a-input-search v-model:value="searchValue" style="margin-bottom: 8px" placeholder="请输入要搜索的设备" />-->
        <div style="height: calc(100% - 0px); overflow: auto">
          <UTree url="/iot/debug/tree" childUrl="/iot/debug/tree?productId={id}" blockNode :resolverNode="resolverNode" ref="treeRef" style="padding: 12px;"
                 :fieldNames="{title: 'name', key: 'key', children: 'children'}" showLine selectable @select="deviceSelect" @loaded="loadedData">
            <template #title="{data, title}">
              <div>
                <FolderFilled v-if="!data['isLeaf']"/>
                <span>&ensp;{{data.name}}</span>
                <span style="float: right; color: #a8a8a8; font-size: 10px">
                <template v-if="!data['isLeaf']">
                  {{data['type'] == 'productType' ? '产品类型' : '产品'}}
                </template>
                <template v-else>
                  <template v-if="data['status'] == 'online'">
                    <UIcon type="iz-icon-device-online" :style="{fontSize: '16px'}"/>
                  </template>
                  <template v-else>
                    <UIcon type="iz-icon-device-offline" :style="{fontSize: '16px'}"/>
                  </template>
                </template>
              </span>
              </div>
            </template>
          </UTree>
        </div>

      </ACol>
      <ACol span="17" style="padding: 12px;" class="iv-main-border radius">
        <a-spin size="large" :spinning="spinning" description="数据加载中">
          <a-empty v-if="device['id'] == null" description="请先选择要调试的设备" style="margin-top: 160px;"/>
          <div v-else>
            <a-descriptions bordered :column="{xs: 1, sm: 2, md: 3, lg: 3, xl: 4}" size="small">
              <template #title>
                <div>
                  <span style="float: left">设备详情({{device.name}})</span>
                  <span style="float: right; padding-right: 18px">
                    <ABadge v-if="device.status=='online'" status="processing" color="#87d068" text="在线" />
                    <ABadge v-else status="warning" text="离线" />
                  </span>
                </div>
              </template>
              <a-descriptions-item label="设备名称">
                <router-link :to="getToUrl(device)">{{device['name']}}</router-link>
              </a-descriptions-item>
              <a-descriptions-item label="设备编号">{{device['deviceSn']}}</a-descriptions-item>
              <a-descriptions-item label="设备类型">{{product['deviceTypeName']}}</a-descriptions-item>
              <a-descriptions-item label="网关设备">{{product['deviceTypeName']}}</a-descriptions-item>
              <a-descriptions-item label="产品类型">{{device['productTypeName']}}</a-descriptions-item>
              <a-descriptions-item label="所属产品">
                <router-link :to="`/iot/product?code=${product['code']}`">{{product['name']}}</router-link>
              </a-descriptions-item>
<!--              <a-descriptions-item label="安装地址" span="2">{{device['address']}}</a-descriptions-item>-->
              <a-descriptions-item label="接入网关">
                <router-link :to="`/iot/gateway?name=${product['gatewayName']}`">{{product['gatewayName']}}</router-link>
              </a-descriptions-item>
              <a-descriptions-item label="所属协议">
                <router-link :to="`/iot/protocol?code=${product['protocolCode']}`">{{product['protocolName']}}</router-link>
              </a-descriptions-item>
<!--              <a-descriptions-item label="备注" span="3">{{device['remark']}}</a-descriptions-item>-->
            </a-descriptions>
            <div style="margin-top: 8px" class="iv-debug-tabs">
              <a-tabs v-model:activeKey="activeKey" size="small">
                <a-tab-pane key="cus" tab="自定义">
                  <div style="padding-bottom: 8px; ">
                      <span type="ghost">请选择指令: </span>
                      <USelect :options="directs" v-model:value="directDebugParam.direct" style="width: 180px; margin-right: 32px;" @change="selectDirect"></USelect>
                      <AButton type="primary" @click="cusDebug">调试</AButton>
                  </div>
                  <ARow v-if="directModel" style="margin: 8px 0; min-height: 32px;" :gutter="8">
                    <ACol v-for="config in directModel.downConfigs" :key="config.attrField">
                      <div v-if="config.options">
                        <span type="ghost">{{config.name}}: </span>
                        <ASelect :options="config.options" v-model:value="directDebugParam[config.attrField]" style="width: 120px"></ASelect>
                      </div>
                      <div v-else>
                        <span type="ghost">{{config.name}}: </span>
                        <AInput style="width: 120px" v-model:value="directDebugParam[config.attrField]"></AInput>
                      </div>
                    </ACol>
                  </ARow>
                </a-tab-pane>
                <a-tab-pane key="func" tab="功能接口">
                  <ATabs v-if="funcApis.length > 0" class="iv-api-item">
                    <ATabPane v-for="item in funcApis" :key="item['code']" class="iv-api-func">
                      <template #tab>
                        <div style="padding: 0 0 6px">
                          <a-card variant="bordered" class="iv-model-api" hoverable size="small">
                            <template #title>
                              <UIcon type="iz-icon-debug-api" :style="{marginRight: '3px'}"/>{{item.apiName}}
                            </template>
                            <template #actions>
                              <AButton block type="primary" @click="() => submitDebug(item, false)">调试</AButton>
                            </template>
                          </a-card>
                        </div>
                      </template>
                    </ATabPane>
                  </ATabs>
                  <a-empty v-else description="暂无功能接口"/>
                </a-tab-pane>
                <a-tab-pane key="event" tab="事件接口">
                  <ATabs v-if="eventApis.length > 0" class="iv-api-item">
                    <ATabPane v-for="item in eventApis" :key="item['code']" class="iv-api-func">
                      <template #tab>
                        <div style="padding: 0 0 6px">
                          <a-card variant="bordered" class="iv-model-api" hoverable size="small">
                            <template #title>
                              <UIcon type="iz-icon-debug-api" :style="{marginRight: '3px'}"/>{{item.apiName}}
                            </template>
                            <template #actions>
                              <AButton block type="primary" @click="() => submitDebug(item, false)">调试</AButton>
                            </template>
                          </a-card>
                        </div>
                      </template>
                    </ATabPane>
                  </ATabs>
                  <a-empty v-else description="暂无事件接口"/>
                </a-tab-pane>
              </a-tabs>
            </div>
            <AFlex style="height: 26px; margin-top: 10px;" class="iv-debug-header">
              <div style="flex-grow: 1">调试参数</div>
              <div style="width: 280px;">请求参数</div>
            </AFlex>
            <AFlex >
              <div class="iv-debug-container" style="overflow: auto; flex-grow: 1">
                <div v-if="debugResult">
                  <div class="iv-debug result">
                    <span class="status-item">
                      <label class="title">[执行状态]</label>
                      <label class="value" v-if="debugResult?.['status'] == 'success'" style="color: #008d4c">
                        成功
                      </label>
                      <label v-else class="value" style="color: #d4380d">
                        失败<label>({{debugResult?.['reason']}})</label>
                      </label>
                    </span>
                  </div>
                  <div class="iv-debug result">
                    <span class="status-item">
                      <label class="title">[执行指令]</label>
                      <label class="value">{{currentDebugApi?.['code']}} -> {{currentDebugApi?.['direct']}}</label>
                    </span>
                  </div>
                  <div class="iv-debug result">
                    [请求报文] {{debugResult?.['reqMsg']}}
                  </div>
                  <div class="iv-debug result">
                    [响应报文] {{debugResult?.['respMsg']}}
                  </div>
                  <div class="iv-debug result">
                    <span class="status-item">
                      <label class="title">[执行耗时]</label>
                      <label class="value"> {{debugResult?.['respTime'] - debugResult?.['reqTime']}}(ms)</label>
                    </span>
                  </div>
                  <div class="iv-debug result">
                    [执行结果] {{debugResult?.['value']}}
                  </div>
                </div>
                <AEmpty v-else description="暂无调试参数" style="margin-top: 16px"/>
              </div>
              <div style="border-left: var(--iv-color-split); min-width: 280px;">
                <pre v-if="requestParamCompute">{{requestParamCompute}}</pre>
                <AEmpty v-else description="暂无请求参数" style="margin-top: 16px"/>
              </div>
            </AFlex>
          </div>
        </a-spin>
      </ACol>
    </ARow>
  </UView>
</template>
<!-- 设备功能 -->
<script>
import {ref, reactive, watch, computed} from "vue";
import {FolderFilled} from "@antdv-next/icons";
import CoreConsts from "@/components/CoreConsts";
export default {
  name: "Debug",
  components: {FolderFilled},
  setup() {

    let spinning = ref(false);
    let device = ref({name: '', status: '', uid: ''});
    let product = ref({});
    let funcApis = ref([]);
    let treeRef = ref(null);
    let eventApis = ref([]);
    let debugParam = ref(null);
    let debugResult = ref(null);
    let expandedKeys = ref([]);
    let requestParam = ref(null);
    let activeKey = ref('cus');
    let currentDebugApi = ref(null);
    let apiCodeDebugMap = ref([]);
    let directs = ref([]);
    let directModel = ref({});
    let directDebugParam = ref({});
    let requestParamCompute = computed(() => {
      return requestParam.value ? JSON.stringify(requestParam.value, null, 2) : '';
    })

    return {device, funcApis, spinning, eventApis, product, expandedKeys, treeRef, activeKey, directDebugParam
      , apiCodeDebugMap, debugParam, requestParam, requestParamCompute, currentDebugApi, debugResult, directs, directModel}
  },
  methods: {
    loadedData(data) {
      if(this.expandedKeys.length == 0) {
        Object.values(data).forEach(item => {
          if(item['type']=='productType') {
            this.expandedKeys.push(item['key']);
          }
        });

        this.treeRef.setExpandedKeys(this.expandedKeys);
      }
    },
    selectDirect(value, model) {
      this.directModel = model;
      this.directDebugParam = {direct: value};
      if(model.downConfigs) {
        model.downConfigs.forEach(item => {
          this.directDebugParam[item.attrField] = null;
        })
      }
    },
    cusDebug() {
      if(!this.directDebugParam.direct) {
        return this.$msg.error("请先选择调试指令");
      }

      this.requestParam = this.directDebugParam;
      let requestParams = {uid: this.device.uid, param: this.directDebugParam};
      this.spinning = true;
      this.$http.post(`/iot/instruct/debug`, requestParams).then(({data, message, code}) => {
        if(code == CoreConsts.SuccessCode) {
          this.debugResult = data;
          this.currentDebugApi = {code: this.product.protocolCode, direct: this.directDebugParam.direct}
        } else {
          this.$msg.error(message);
        }
      }).finally(() => this.spinning = false)
    },
    submitDebug(api) {

      this.spinning =true;
      this.currentDebugApi = api;
      let uid = this.device['uid'];
      let {commandValue, apiName} = api
      this.requestParam = {};
      commandValue.split('&').forEach(item => {
        let [key, value] = item.split('->');
        this.requestParam[key] = value
      });

      this.$http.post(`/iot/instruct/debug/${api.code}`, {uid, param: this.requestParam, config: {apiName}}).then(({data, message, code}) => {
        if(code == CoreConsts.SuccessCode) {
          this.debugResult = data;
        } else {
          this.$msg.error(message);
        }
      }).finally(() => this.spinning = false)
    },
    resolverNode(data) {
      // data['disableCheckbox'] = !data['status'];
    },
    deviceSelect(data, {node}) {
      if(node['type'] == 'device') {
        this.spinning = true;
        this.debugParam = null;
        this.requestParam = null;
        this.debugResult = null;
        this.$http.get(`/iot/debug/detail?deviceId=${node['id']}`).then(({code, message, data}) => {
          if(code == CoreConsts.SuccessCode) {
            this.device = data['device'];
            this.product = data['product']

            this.apiCodeDebugMap = data['apiCodeDebugMap'] || [];
            this.funcApis = this.apiCodeDebugMap.filter(item => item['apiType'] == 'func');
            this.eventApis = this.apiCodeDebugMap.filter(item => item['apiType'] == 'event');

            this.$http.get(`/iot/protocol/apis?id=${data.product.protocolId}`).then(({code, message, data}) => {
              if(code == CoreConsts.SuccessCode) {
                if(data instanceof Array) {
                  this.directs = data.map(item => {
                    let downConfigs = [];
                    if(item.downConfigs instanceof Array) {
                      downConfigs = item.downConfigs.map(item => {
                        return {name: item['attrName'], attrField: item['protocolAttrField'], options: item.options}
                      });
                    }

                    return {label: item.name, value: item.code, downConfigs}
                  })
                }
              }
            })
          } else {
            this.$msg.error(message);
          }
        }).finally(() => this.spinning = false)
      }
    },
    getToUrl(device) {
      let type = device['deviceType'];
      let uri = type == 'Child' ? 'child' : (type == 'Gateway' ? "gateway" : '');
      return `/iot/device/${uri}?deviceSn=${device['deviceSn']}`
    }
  }
}
</script>
<style>
.iv-model-api .ant-card-head {
  padding: 0px 12px!important;
  min-height: 43px!important;
}
.iv-model-api .ant-card-actions li {
  margin: 3px!important;
}
.iv-api-item .ant-tabs-tab{
 padding: 0px;
}
.iv-api-item .ant-card-body {
  padding: 12px;
  height: 78px;
  word-wrap: break-word;
  overflow-y: auto;
  overflow-x: hidden;
}
.iv-debug-tabs {
  padding: 6px;
  border-radius: 6px;
  background-color: var(--iv-color-bgt-container);
}

.iv-debug-header {
  padding: 1px 0;
  text-align: center;
  border-top-left-radius: 6px;
  border-top-right-radius: 6px;
  background-color: var(--iv-color-bgt-container);
}
.iv-debug.result {
  padding: 1px;
}
.iv-debug .status-item .title {
  color: #282727;
}
.iv-debug .status-item .value {
  margin-left: 3px;
}
</style>
