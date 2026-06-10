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
        <AFlex wrap="wrap" gap="middle" justify="space-around">
          <template v-for="({device, ctrlCmds, values, ctrlValue, collectTime, cmdSize}) in devices" :key="device['uid']">
            <a-card variant="borderless" size="small" class="iv-device-card" @click="routeTo(device, 'device')"
                    :styles="{header: {padding: '0 8px'}, actions: {padding: '0 8px'}}" hoverable>
              <template #title>
                <div class="iv-pd-header">
                  <AFlex gap="10" justify="space-between">
                    <div style="font-weight: normal; color: #6a6a6a" :title="device.name"><span>{{device.name}}</span></div>
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
              <div class="iv-device-name">
                <AFlex gap="8" align="center" justify="space-evenly">
                  <div style="text-align: center;" v-for="attr in values">
                    <div>
                      <span>{{attr.label || attr.value || '-'}}</span>
                      <span>{{attr.unit}}</span>
                    </div>
                    <div style="font-size: 12px; opacity: 0.9">{{attr.name}}</div>
                  </div>
                </AFlex>
              </div>
              <template #actions>
                <div>
                  <ASpace style="height: 32px; line-height: 32px">
                    <template  v-if="ctrlCmds && ctrlCmds.length > 0">
                        <template v-for="(ctrlCommand, index) in ctrlCmds" :key="index">
                          <a-space-compact v-if="ctrlCommand.attrField">
                            <template v-for="(cmd, cmdIndex) in ctrlCommand.options" :key="cmdIndex">
                              <AButton variant="filled" :color="(cmd.value && cmd.value == ctrlValue?.[ctrlCommand.attrField]) ? 'primary' : 'default'"
                                       :style="{width: `calc(280px / ${cmdSize})`}" @click.stop="switchCtrlStatus(cmd, device, ctrlCommand)">
                                {{cmd.commandName}}
                              </AButton>
                            </template>
                          </a-space-compact>
                          <AButton v-else variant="filled" color="default" :style="{width: `calc(280px / ${cmdSize})`}"
                                   @click.stop="switchCtrlStatus(ctrlCommand.options[0], device, ctrlCommand)">
                            {{ctrlCommand.options[0]?.commandName}}
                          </AButton>
                        </template>
                    </template>
                    <span v-else>没有控制项</span>
                  </ASpace>
                  <div style="font-size: 12px;">
                    <!--                    <span style="color: var(&#45;&#45;ant-color-primary)">{{ctrlCommand ? ctrlCommand.apiName + ' · ' : ''}}</span>-->
                    采集时间: {{collectTime || '暂未采集'}}
                  </div>
                </div>

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
    switchCtrlStatus(cmd, device, ctrl) {
      this.spinning = true;
      const params = {uid: device.uid, value: cmd.value, field: ctrl.attrField
        , code: cmd.commandValue, name: ctrl.apiName + "->" + cmd.commandName, scenario: '运行展板'};
      this.$http.post(`/iot/instruct/command/invoke/update`, params).then(({code, message}) => {
        if(code == CoreConsts.SuccessCode) {
          this.search();
          this.$msg.success(message);
        } else {
          this.search();
          this.$msg.error(message);
        }
      }).finally(() => this.spinning = false)
    },
    routeTo(device, type) {
      window.open(`/#/iot/device/detail/${device.uid}`, '_blank');
    }
  }
}
</script>

<style>
.iv-device-card {
  width: 300px;
  overflow: hidden;
}
.iv-device-name,.iv-device-sn {
  //max-width: 130px;
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
.iv-pd-header .iv-device-status {
  position: absolute;
  top: 7px;
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
.iv-device-card .ant-card-actions>li {
  margin: 3px;
}
</style>