<template>
  <AModal placement="left" width="860px" centered :footer="null"
          :styles="{body: {padding: '0px', height: '380px', overflow: 'auto'}}" destroyOnHidden>
    <template #title>
      <AFlex justify="space-evenly" class="iv-device-dt">
        <div>
          设备名称: {{selectDevice.name}}
        </div>
        <div>
          设备编号: {{selectDevice.deviceSn}}
        </div>
        <div>
          设备类型: {{selectDevice.deviceTypeName}}
        </div>
        <div>
          设备状态: {{selectDevice.status == 'online' ? '在线' : '离线'}}
        </div>
        <div>
          <AButton type="link" size="small" @click="openDeviceDetail">运行总览</AButton>
        </div>
      </AFlex>
    </template>
    <ASpin :spinning="spinning" description="数据加载中..." style="height: 100%" :classes="{root: 'iv-emap-spin'}">
      <ATabs v-model:active-key="activeKey" tabPlacement="left">
        <ATabPane key="attr" tab="最新数据">
          <a-row justify="start" align="middle" wrap>
            <a-col v-for="attr in modelAttrs" :key="attr.id" style="min-width: 170px; padding: 3px; margin: 5px 0; height: 85px;">
              <a-card variant="borderless" hoverable size="small" :styles="{head: {minHeight: '28px'}, body: {padding: '6px'}}">
                <template #title>
                  <AFlex gap="10" justify="space-between">
                    <div>
                      <span style="font-weight: normal;">{{attr.name}}</span>
                    </div>
                    <div>
                      <a-badge :status="attr.__type" />
                    </div>
                  </AFlex>
                </template>
                <div>
                  <span style="font-weight: bold; font-size: 14px;">{{attr.value != null ? attr.value : '-'}}</span>
                  <span class="iv-minor-text">&nbsp;{{attr.unit ? `(${attr.unit})` : ''}}</span>
                </div>
                <div style="font-size: 12px; color: #818181; padding-top: 6px; border-top: 1px dashed #d6d6d6">
                  <span>{{attr.collectTime || '暂未采集'}}</span>
                </div>
              </a-card>
            </a-col>
          </a-row>
        </ATabPane>
        <ATabPane key="warn" tab="告警数据">
          <ATable :columns="warnColumns" :bordered="true" size="small" :pagination="false"
                  :data-source="warns" :scroll="{x: 560, y: 320}"></ATable>
        </ATabPane>
        <ATabPane key="history" tab="历史数据">
          <ATable :columns="fieldColumns" :bordered="true" size="small" :pagination="false"
                  :data-source="historyData" :scroll="{x: 560, y: 320}"></ATable>
        </ATabPane>
        <ATabPane key="ctrl" tab="控制命令">
          <div v-if="funcApis && funcApis.length > 0">
            <AFlex justify="space-between" class="iv-api-item" v-for="item in funcApis" :key="item.code">
              <div>
                <div class="iv-text-ellipsis">{{item.apiName}}</div>
                <div class="iv-text-ellipsis iv-cmd-desc" :title="item.remark">{{item.remark}}</div>
              </div>
              <div style="margin-right: 36px;">
                <AButton type="dashed" danger @click="() => apiCmdClick(item)">执行</AButton>
              </div>
            </AFlex>
          </div>
          <AEmpty v-else description="无控制接口" style="margin-top: 36px;" />
        </ATabPane>
      </ATabs>
    </ASpin>
  </AModal>
</template>
<script>
import {ref, watch, defineComponent, getCurrentInstance} from "vue";
import {GET, POST, urlConfig} from "@/utils/request";
import CoreConsts from "@/components/CoreConsts";
import {msgError, msgSuccess} from "@/utils/message";
import {SendOutlined} from "@antdv-next/icons";

export default defineComponent({
  name: "DeviceDetailModal",
  components: {SendOutlined},
  props: {
    device: {type: Object, required: true},
  },
  setup(props, { attrs }) {
    let map = ref();
    let warns = ref([]);
    let historyData = ref([]);
    let selectDevice = props.device;
    let modelAttrs = ref([]);
    let funcApis = ref([]);
    let activeKey = ref('attr');
    let apiCodeDebugMap = ref({});
    let devices = ref([]);
    let spinning = ref(false);
    let deviceMap = ref({});
    let searchModel = ref({});
    let currentInstance = getCurrentInstance().proxy;
    let warnColumns = ref([
      {title: '名称', dataIndex: 'name', align: 'center', width: 100, ellipsis: true},
      {title: '等级', dataIndex: 'grade', align: 'center', width: 100},
      {title: '内容', dataIndex: 'content', align: 'center', width: 230, ellipsis: true, resizable: true},
      {title: '告警时间', dataIndex: 'warnTime', align: 'center', width: 130},
    ]);
    let fieldColumns = ref([
      // {title: '名称', dataIndex: 'fieldName', align: 'center', width: 100, ellipsis: true, resizable: true},
      {title: '值', dataIndex: 'value', align: 'center', ellipsis: true, width: 100, resizable: true},
      {title: '单位', dataIndex: 'unit', align: 'center', ellipsis: true, width: 100},
      // {title: '设备编号', dataIndex: 'deviceSn', align: 'center', ellipsis: true, width: 100},
      {title: '采集时间', dataIndex: 'collectTime', align: 'center', width: 160},
    ])
    watch(() => [props.device, attrs.open], (newValue) => {
      let device = newValue[0]; let open = newValue[1];
      if(open && device && device.uid) {
        selectDevice.value = device;
        let uid = selectDevice.value.uid;
        currentInstance.loadCmds(uid);
        currentInstance.deviceDetail(device);
        currentInstance.loadModelAttr(device);
        currentInstance.loadProductData(device);
      }
    }, {immediate: true});

    return {searchModel, devices, map, deviceMap, selectDevice, spinning, warns, modelAttrs
      , apiCodeDebugMap, activeKey, warnColumns, funcApis, fieldColumns, historyData}
  },
  mounted() {

  },
  methods: {
    deviceDetail(device) {
      this.spinning = true;
      this.selectDevice = device;
      this.$http.get('/iot/emap/detail', {params: {uid: device.uid}})
          .then(({code, message, data}) => {
            if (code == CoreConsts.SuccessCode) {
              let {attrs, warns} = data;
              this.modelAttrs = attrs || [];
              this.warns = warns || [];
              // this.ctrlApi = ctrlApi;
              // this.apis = funcApis || [];
              // this.ctrlAttrs = ctrlAttrs;
              // this.ctrlAttrValue = ctrlAttrValue;
              attrs.forEach(attr => {
                this.apiCodeDebugMap[attr.field] = attr;
              });
            } else {
              this.$msg.error(message);
            }
          }).finally(() => this.spinning = false)
    },
    loadCmds(uid) {
      GET(`/iot/instruct/commands?uid=${uid}`).then(({code, data, message}) => {
        if(code == CoreConsts.SuccessCode) {
          if(data instanceof Array) {
            this.funcApis = data.filter(item => item['apiType'] == 'func');
            // eventApis.value = data.filter(item => item['apiType'] == 'event');
          }
        } else {
          msgError(message);
        }
      })
    },
    apiCmdClick(api) {
      this.spinning = true;
      this.tipText = "执行指令中...";
      this.$http.post(`/iot/instruct/command/invoke`, {code: api.commandValue}).then(({code, data, message}) => {
        if(code != CoreConsts.SuccessCode) {
          this.$msg.error(message);
        } else {
          this.$msg.success("执行成功");
        }
      }).finally(() => this.spinning = false)
    },
    loadProductData(device) {
      this.$http.get(`/iot/collectData/product?productId=${device.productId}&uid=${device.uid}&current=1&size=20`).then(({code, data}) => {
        if(code == CoreConsts.SuccessCode) {
          if(data.records instanceof Array) {
            data.records.forEach(item => {
              if(item.value) {
                let attrItem = item.value.split(':::');
                for (let attr of attrItem) {
                  let info = attr.split('=');
                  if(info.length === 2) {
                    item[info[0]] = info[1];
                  }
                }
              }
            });

            this.historyData = data.records;
          }
        }
      })
    },
    loadModelAttr(device) {
      let productId = device.productId;
      this.$http.get(`/iot/modelAttr/list?productId=${productId}`).then(({code, message, data}) => {
        if(code === CoreConsts.SuccessCode) {
          if(data instanceof Array) {
            let columns = data.filter(item => item.dataType !== 'json' && item.dataType !== 'bit').map(item => {
              // let formatter = this.columnFormatter;
              let title = item.unit ? `${item.name}(${item.unit})` : item.name;
              return {dataIndex: item.field, title, width: 100, align: 'center', resizable: true, ellipsis: true}
            })

            this.fieldColumns = [
              // {field: "deviceName", title: "设备名称", width: 120, ellipsis: true, fixed: 'left'}
              ...columns,
              {dataIndex: "collectTime", title: "采集时间", width: 150, type: 'date', format: 'YY-MM-DD HH:mm:ss', fixed: 'right'}
            ]
          }
        } else {
          this.$msg.error(message);
        }
      })
    },
    openDeviceDetail() {
      window.open('/#/iot/device/detail/' + this.selectDevice.uid, '_blank')
    }
  }
})
</script>
<style scoped>
.iv-api-item {
  padding: 6px 0;
  position: relative;
  border-bottom: 1px dashed #d9d9d9;
}
.iv-device-dt {
  padding: 0 0 12px;
  border-bottom: 1px solid var(--ant-color-split);
}
.iv-api-item .iv-cmd-desc, .iv-minor-text {
  font-size: 12px;
  color: #8e8e8e;
}
</style>