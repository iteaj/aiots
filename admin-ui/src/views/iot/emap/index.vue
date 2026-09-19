<template>
  <UView class="iv-emap">
    <AFlex vertical justify="space-between" style="height: 100%">
      <UViewSearch v-model="searchModel">
        <URow col="search">
          <UTreeSelectItem field="deviceGroupId" label="所属组" url="/iot/eventSource/deviceGroups" :allowClear="true"/>
          <USelectItem field="productId" label="所属产品" url="/iot/product/list" :allowClear="true"/>
          <AButton type="primary" @click="search">搜索</AButton>
          <UButton func="reset">重置</UButton>
        </URow>
      </UViewSearch>
      <div id="map-container" style="flex-grow: 1; height: 100%;">
        <AMap id="map-container" ref="map">
          <AMarker v-for="device in devices" :key="device.uid" :position="[device.lon, device.lat]"
                   icon="/img/map-marker-c.png" @click="() => deviceDetail(device)">
            <AMarkerLabel>
              <div class="iv-device-label animate__animated" :class="[device.type, device.repeat, device['animate']]">
                {{device.name}}[{{device['typeName']}}]
              </div>
            </AMarkerLabel>
          </AMarker>
        </AMap>
      </div>
    </AFlex>
    <DeviceDetailModal v-model:open="open" :device="selectDevice" />
  </UView>
</template>

<script>
import {ref} from "vue";
import CoreConsts from "@/components/CoreConsts";
import DeviceDetailModal from "@msn/iot/device/detail/modal.vue";
import {urlConfig} from "@/utils/request";

export default {
  name: "EMap",
  components: {DeviceDetailModal},
  setup() {
    let map = ref();
    let selectDevice = ref({uid: null});
    let open = ref(false);
    let devices = ref([]);
    let spinning = ref(false);
    let deviceMap = ref({});
    let searchModel = ref({});
    let realtimeSocket = ref({});
    return {searchModel, devices, map, deviceMap, open, selectDevice, spinning, realtimeSocket}
  },
  mounted() {
    this.search();
    this.realtimeSocket = new WebSocket(urlConfig.getFullWsURL(`/ws/iot/realtime?uid=-1&type=123`));
    this.realtimeSocket.onmessage = (message) => {
      let {type, uid, value} = JSON.parse(message.data);
      let device = this.deviceMap[uid];
      if(device) {
        if(type == 'status') {
          device['type'] = value['status'];
          if(value['status'] == 'offline') {
            device['typeName'] = '离线';
            device['animate'] = 'animate__shakeX';
            device['repeat'] = "animate__repeat-2"; // 播放三次
          } else {
            device['repeat'] = ""
            device['animate'] = 'animate__flipInY';
            device['typeName'] = '在线';
          }
        } else if(type == 'warn') {
          let grade = value['grade'];
          if(grade != 'status') {
            device['animate'] = 'animate__flash';
            if(grade == 'notice' || grade == 'normal') {
              device['repeat'] = "animate__repeat-2";
            } else {
              device['repeat'] = "animate__repeat-3";
            }

            device['type'] = grade; // 根据告警等级显示颜色
            device['typeName'] = value['gradeName'];
          }
        }
      }
    }
  },
  unmounted() {
    if(this.realtimeSocket instanceof WebSocket) {
      this.realtimeSocket.close(1000);
    }
  },
  updated() {
    if(this.map.instance) {
      this.map.setFitView();
    }
  },
  methods: {
    search() {
      this.$http.get('/iot/emap/devices', {params: this.searchModel}).then(({code, message, data}) => {
        if(code == CoreConsts.SuccessCode) {
          this.devices = data || [];
          this.deviceMap = {};
          // animate__repeat-3 动画次数
          // infinite 无限循环
          this.devices.forEach(device => {
            device['type'] = device['status']; // 区分类型
            device['repeat'] = device['']; // 动画次数
            device['typeName'] = device['status'] == 'online' ? '在线' : '离线'; //
            this.deviceMap[device['uid']] = device;
          })
        } else {
          this.$msg.error(message);
        }
      })
    },
    deviceDetail(device) {
      this.selectDevice = device;
      this.open = true;
    }
  }
}
</script>

<style scoped>
.iv-device-label {
  padding: 3px 6px;
  box-shadow: 0px 0px 6px 0px #8e8e8e;
}
.iv-device-label.online,.iv-device-label.normal {
  background: #a9fc88;
}
.iv-device-label.offline {
  background: #e2e1e1;
}
/*严重*/
.iv-device-label.fatal {
  background: rgba(253, 95, 95, 0.78);
}
/*紧急*/
.iv-device-label.urgent {
  background: #EA8857BA;
}
/*告警*/
.iv-device-label.warn {
  background: #fab06b;
}
.iv-efunc-item {
  padding: 3px;
}
.iv-efunc-item .title {
  color: rgba(227, 227, 227, 0.49);
  height: 24px;
  font-size: 13px;
  text-align: center;
  line-height: 24px;
}
.iv-efunc-item>.content {
  padding: 6px;
  border-radius: 6px;
  overflow-x: auto;
  height: calc(100% - 24px);
  backdropFilter: blur(16px);
  background: rgba(252, 251, 251, 0.2);
}
.iv-efunc-item.warn {
  height: 120px;
}
.iv-efunc-item.data {
  height: 124px;
}
.iv-efunc-item.ctrl {
  height: 130px;
}
.iv-efunc-item.api {
 flex-grow: 1;
}
.iv-ewarn-item {
  padding: 3px;
  height: 24px;
  text-align: center;
  line-height: 24px;
}
.iv-ewarn-item.grade {
  width: 46px;
}
.iv-ewarn-item.time {
  width: 130px;
}
.iv-eattr-item {

}
.iv-efunc-item.data .content>div {
  padding: 18px 8px;
  background: rgba(62, 62, 62, 0.07);
}
.iv-eattr-item div {
  padding: 3px 6px;
  text-align: center;
}
.iv-eattr-item div:first-child {
  font-weight: 600;
  font-size: 16px;
}
.iv-eapi-item {
  padding: 6px;
  border-radius: 3px;
  background: rgba(64, 64, 64, 0.11);
}
.iv-eapi-item .ant-card-actions {
  background-color: unset;
}
.iv-eapi-item .ant-card-actions li {
  margin: 5px 0px;
}
</style>