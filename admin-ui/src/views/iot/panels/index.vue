<template>
  <UView>
    <UViewSearch v-model="searchModel">
      <URow col="search">
        <UTreeSelectItem field="deviceGroupId" label="所属组" url="/iot/eventSource/deviceGroups" :allowClear="true"/>
        <USelectItem field="productId" label="所属产品" url="/iot/product/list?deviceTypes=Child,Direct" :allowClear="true"/>
        <AButton type="primary" @click="search">搜索</AButton>
      </URow>
    </UViewSearch>
    <ASpin :spinning="spinning">
      <div v-if="total > 0">
        <AFlex wrap="wrap" gap="small" justify="space-between">
          <template v-for="({device, options, values}, index) in devices" :key="device['uid']">
            <a-card :bordered="false" class="iv-device-card" :headStyle="{minHeight: '50px', padding: '0 8px'}" hoverable style="min-width: 240px">
              <template #title>
                <div class="iv-header">
                  <AFlex gap="10" justify="space-between">
                    <div style="font-weight: normal; color: #6a6a6a" :title="device.name"><span>{{device.switchTime}}</span></div>
<!--                    <div class="iv-device-sn" :title="device['deviceSn']">{{device['deviceSn']}}</div>-->
                    <div style="width: 38px"></div>
                  </AFlex>
                  <div class="iv-device-status" :class="device['status']">
                    <div class="iv-status-value">
                      <template v-if="device['status'] == 'online'">
                        <UIcon type="iz-icon-device-online" :style="{fontSize: '16px'}"/>
                        <span style="margin-left: 5px">在线</span>
                      </template>
                      <template v-else>
                        <UIcon type="iz-icon-device-offline" :style="{fontSize: '16px'}"/>
                        <span style="margin-left: 5px">离线</span>
                      </template>
                    </div>
                  </div>
                </div>
              </template>
              <div class="card-bg-left" :class="device['status']"></div>
              <div class="card-bg-right" :class="device['status']"></div>
              <div class="iv-device-name">{{device.name}}</div>
              <template #actions>
                <ASwitch v-if="options && options.length == 2" v-model:checked="devices[index]['ctrlValue']"
                         :checked-children="options[0]['label']" :checked-value="options[0]['value']"
                         :un-checked-children="options[1]['label']" :un-checked-value="options[1]['value']"
                         @change="(value) => switchCtrlStatus(value, device)">
                </ASwitch>
                <ASelect v-else-if="options && options.length > 2" v-model:value="devices[index]['ctrlValue']" :options="options" size="small"
                         :bordered="false" style="width: 100%" placeholder="请选择控制项" @change="(value) => switchCtrlStatus(value, device)"></ASelect>
                <span v-else>没有控制项</span>
                <AButton type="dashed" shape="round" size="small">
                  <span style="color: #0f8bc7"></span>
                  <a :href="`/#/iot/device/detail/${device.uid}`" target="_blank">运行展板</a>
                </AButton>
              </template>
            </a-card>
          </template>
        </AFlex>
        <a-pagination v-model:current="searchModel.current" :total="total" v-model:pageSize="searchModel.size"
                      :page-size-options="[12, 36, 64]" style="text-align: center; padding: 8px;" />
      </div>
      <a-empty v-else style="margin-top: 132px"/>
    </ASpin>
  </UView>
</template>
<!--运行展板-->
<script>
import {getCurrentInstance, ref, watch, watchEffect} from "vue"
import CoreConsts from "@/components/CoreConsts";
import {urlConfig} from "@/utils/request";
import {msgWarn} from "@/utils/message";
import dayjs from "dayjs";
export default {
  name: "Panels",
  setup() {
    let total = ref(0);
    let isOpen = ref(false)
    let devices = ref([]);
    let spinning = ref(false);
    let {proxy} = getCurrentInstance();
    let searchModel = ref({current: 1, size: 20});

    watch(() => searchModel.value.current, (newValue, oldValue) => {
      proxy.search();
    })
    return {searchModel, isOpen, devices, total, spinning}
  },
  mounted() {
    this.search();
  },
  methods: {
    search() {
      this.spinning = true;
      this.$http.get('/iot/panels/devices', {params: this.searchModel}).then(({code, message, data}) => {
        if(code == CoreConsts.SuccessCode) {
          this.total = data.total;
          this.devices = data['records'];
        } else {
          this.$msg.error(message);
        }
      }).finally(() => this.spinning = false)
    },
    switchCtrlStatus(value, device) {
      this.spinning = true;
      this.$http.post(`/iot/panels/switchCtrlStatus`, {id: device.uid, status: value}).then(({code, message}) => {
        if(code == CoreConsts.SuccessCode) {
          this.search();
        } else {
          this.search();
          this.$msg.error(message);
        }
      }).finally(() => this.spinning = false)
    },
    routeTo(device, type) {
      if(type == 'device') {
        let url = '';
        let deviceType = device['deviceType'];
        if(deviceType == 'Child') {
          url = '/child';
        } else if(deviceType == 'Gateway') {
          url = '/gateway'
        }
        this.$router.push(`/iot/device${url}?deviceSn=${device['deviceSn']}`);
      } else {

      }

    }
  }
}
</script>

<style>
.iv-device-card {
  overflow: hidden;
}
.iv-device-name,.iv-device-sn {
  max-width: 130px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
.iv-desc-type .value, .iv-desc-product .value, .iv-item-api .ant-card .ant-card-actions>li >span
, .iv-content-value, .model-attr-name, .model-attr-value {
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
.iv-header .iv-device-status {
  position: absolute;
  top: 13px;
  right: -13px;
  width: 96px;
  height: 25px;
  line-height: 25px;
  text-align: center;
  transform: skew(45deg);
}
.iv-device-status.online {
  background-color: rgba(163, 243, 163, 0.15);
}
.iv-device-status.offline {
  background-color: rgba(248, 227, 164, 0.15);
}
.iv-device-status .iv-status-value {
  transform: skew(-45deg);
  font-weight: 360;
  font-size: 14px;
}
.card-bg-left {
  position: absolute;
  left: -32px;
  height: 100%;
  width: 44.65%;
  top: 0;
  transform: skew(-15deg);
  background: linear-gradient(188.4deg, rgba(71, 180, 238, 0.02) 22.94%, rgba(71, 180, 238, 0) 94.62%);
}
.card-bg-right {
  position: absolute;
  right: -32px;
  height: 100%;
  width: 55%;
  top: 0;
  transform: skew(-15deg);
}
.card-bg-right.online {
  background: linear-gradient(188.4deg, rgba(189, 246, 189, 0.15) 22.94%, rgba(163, 246, 189, 0) 94.62%);
}
.card-bg-right.offline {
  background: linear-gradient(188.4deg, rgba(229, 0, 18, .03) 22.94%, rgba(229, 0, 18, 0) 94.62%);
}
</style>