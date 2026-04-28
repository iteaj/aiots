<template>
  <UView rowKey="deviceId">
    <UViewSearch v-model="searchModel">
      <URow col="search">
        <UInputItem field="name" label="名称" :allowClear="true"/>
        <UInputItem field="deviceId" label="国标编号" :allowClear="true"/>
        <USelectItem field="online" label="在线状态" :options="onlineOptions" :allowClear="true"/>
        <UButton func="query" url="/gbs/device/view">搜索</UButton>
        <UButton func="reset">重置</UButton>
<!--        <UButton func="add" url="/gbs/device/add" v-auth="'gbs:device:add'">新增</UButton>-->
      </URow>
    </UViewSearch>
    <UViewModal title="管理" :span="[7, 14]" v-model="editModel" width="680px" :rules="rules">
      <template #default="{model}">
        <URow :gutter="12" :responsive="2">
          <UInputItem field="deviceId" label="国标编号" disabled/>
          <UInputItem field="nickName" label="设备别名" />
        </URow>
      </template>
      <template #footer>
        <UButton func="cancel">取消</UButton>
        <UButton func="submit" url="/gbs/device/update">提交</UButton>
        <UButton func="reset">重置</UButton>
      </template>
    </UViewModal>
    <UViewTable :columns="columns" :scroll="{x: 1200}">
      <template #online="{record, value}">
        <a-badge v-if="value" color="#87d068" status="processing" text="在线" />
        <a-badge v-else status="warning" text="离线" />
      </template>
      <template #action="{record}">
        <UTag func="open" :data="record" uid="channel" v-auth="'gbs:device:channels'">通道列表</UTag>
        <UTag func="edit" :data="record" url="/gbs/device/edit" v-auth="'gbs:device:edit'">修改</UTag>
        <UTag func="ajax" :data="record" url="/gbs/device/sync" v-auth="'gbs:device:sync'">刷新</UTag>
        <UTag func="del" :data="record" url="/gbs/device/del" v-auth="'gbs:device:del'">删除</UTag>
      </template>
    </UViewTable>
    <UDrawer uid="channel" v-model:source="channelDeviceModel" placement="top" height="100%"
             :title="'通道列表('+(channelDeviceModel['nickName'] || channelDeviceModel.name)+')'" @open="channelOpenHandle">
      <UChildView uid="channelView">
        <UViewSearch v-model="channelSearchModel">
          <URow col="search">
            <UInputItem field="name" label="通道名称" :allowClear="true"/>
            <URadioItem field="status" label="设备状态" :allowClear="true" :options="onlineOptions"/>
            <UButton func="query" url="/gbs/device/channels">搜索</UButton>
            <UButton func="reset">重置</UButton>
          </URow>
        </UViewSearch>
        <UViewModal title="通道管理" :span="[7, 14]" width="680px">
          <template #default="{model}">
            <UInputItem field="deviceId" label="国标编号" disabled/>
            <UInputItem field="channelId" label="通道编号" disabled/>
            <UInputItem field="nickName" label="通道别名" />
          </template>
          <template #footer>
            <UButton func="cancel">取消</UButton>
            <UButton func="submit" url="/gbs/device/updateChannel">提交</UButton>
            <UButton func="reset">重置</UButton>
          </template>
        </UViewModal>
        <UViewTable :columns="channelColumns" :scroll="{x: 1200}">
          <template #status="{record, value}">
            <a-badge v-if="value" color="#87d068" status="processing" text="在线" />
            <a-badge v-else status="warning" text="离线" />
          </template>
          <template #action="{record}">
            <ATag v-auth="'gbs:play:start'" @click="() => openPlayModal(record)" style="cursor: pointer">实时视频</ATag>
            <UTag func="link" :data="{deviceId: record['deviceId'], channelId: record['channelId'], name: record['name'], status: record['status']}"
                  url="/gbs/device/history" v-auth="'gbs:device:history'">历史视频</UTag>
            <UTag func="edit" :data="record" url="/gbs/device/getChannel" :params="resolveParams" v-auth="'gbs:device:updateChannel'">修改</UTag>
            <UTag func="del" :data="record" url="/gbs/device/del" v-auth="'gbs:device:del'">删除</UTag>
          </template>
        </UViewTable>
        <GbsVideoModal v-model:open="playModal" :ptz="{}" :host="this.$env.rtvs.host" :port="this.$env.rtvs.port" :channel="playChannel"></GbsVideoModal>
      </UChildView>
    </UDrawer>
  </UView>
</template>
<!-- 功能 -->
<script>
import {ref, reactive, defineAsyncComponent, getCurrentInstance} from "vue";
const GbsVideoModal= defineAsyncComponent(()=>import("@/components/video/GbsVideoModal.vue"))

export default {
  name: "WvpDevice",
  components: {GbsVideoModal},
  setup() {
    let columns = ref([
      {field: "deviceId", title: "国标编号", width: 180},
      {field: "name", title: "设备名称", width: 160},
      {field: "nickName", title: "设备别名", width: 160},
      {field: "channelCount", title: "通道数", width: 60},
      {field: "manufacturer", title: "生产厂商", width: 160},
      {field: "transport", title: "传输协议", width: 80},
      {field: "registerTime", title: "注册时间", width: 120, type: 'date', format: 'YYYY-MM-DD'},
      // {field: "hostAddress", title: "设备地址", width: 150},

      {field: "ip", title: "设备ip", width: 120},
      {field: "port", title: "设备端口", width: 80},
      // {field: "sdpIp", title: "收流ip", width: 120},
      // {field: "subscribeCycleForCatalog", title: ""},
      // {field: "subscribeCycleForMobilePosition", title: ""},
      // {field: "mobilePositionSubmissionInterval", title: ""},
      // {field: "subscribeCycleForAlarm", title: ""},
      // {field: "expires", title: "过期时间", width: 80},
      // {field: "charset", title: "字符集", width: 100},
      // {field: "ssrcCheck", title: ""},
      // {field: "geoCoordSys", title: "地理坐标系", width: 100},
      // {field: "mediaServerId", title: ""},
      // {field: "customName", title: ""},
      // {field: "localIp", title: "sip服务ip", width: 120},
      {field: "model", title: "型号", width: 120},
      {field: "firmware", title: "固件版本", width: 120},
      // {field: "keepaliveIntervalTime", title: "心跳周期"},
      // {field: "keepaliveTime", title: "心跳时间", width: 120},
      // {field: "password", title: "密码"},
      // {field: "asMessageChannel", title: ""},
      {field: "createTime", title: "创建时间"},
      // {field: "switchPrimarySubStream", title: ""},
      // {field: "broadcastPushAfterAck", title: ""},
      {field: "online", title: "是否在线", fixed: 'right', width: 80},
      {field: 'action', title: '操作', type: 'action', fixed: 'right', width: 260},
    ]);

    let channelColumns = ref([
      {field: "channelId", title: "通道编号", width: 180},
      {field: "deviceId", title: "国标编号", width: 180},
      {field: "name", title: "通道名称", width: 180},
      {field: "nickName", title: "通道别名", width: 180},
      // {field: "customName", title: ""},
      {field: "manufacture", title: "生产厂商", width: 130},
      // {field: "model", title: "型号", width: 130},
      // {field: "owner", title: "设备归属"},
      // {field: "civilCode", title: "行政区域", width: 80},
      // {field: "block", title: "警区"},
      // {field: "address", title: "安装地址"},
      // {field: "parentId", title: "父级", width: 130},
      {field: "safetyWay", title: "信令模式", width: 80},
      {field: "registerWay", title: "注册方式", width: 80},
      // {field: "certNum", title: "证书序号"},
      // {field: "certifiable", title: "证书标识"},
      // {field: "errCode", title: "证书无效原因"},
      // {field: "endTime", title: "证书终止有效期"},
      // {field: "secrecy", title: "保密属性"},
      {field: "ipAddress", title: "ip地址", width: 150},
      {field: "port", title: "端口号", width: 80},
      // {field: "password", title: "密码"},
      // {field: "customPtzType", title: ""},
      {field: "longitude", title: "经度", width: 100},
      {field: "latitude", title: "纬度", width: 100},
      // {field: "customLongitude", title: ""},
      {field: "createTime", title: "创建时间", type: 'date', format: 'YYYY-MM-DD', width: 120},
      // {field: "customLatitude", title: ""},
      // {field: "streamId", title: "流编号"},
      // {field: "parental", title: "是否有子设备"},
      // {field: "hasAudio", title: "是否有音频"},
      {field: "updateTime", title: "更新时间"},
      {field: "status", title: "是否在线", width: 80, fixed: 'right'},
      // {field: "longitudeGcj02", title: "GCJ02经度"},
      // {field: "latitudeGcj02", title: "GCJ02纬度"},
      // {field: "longitudeWgs84", title: "WGS84经度"},
      // {field: "latitudeWgs84", title: "WGS84纬度"},
      {field: 'action', title: '操作', type: 'action', fixed: 'right', width: 260},
    ])

    let rules = reactive({
        deviceId: {required: true, message: '必填'},
    })

    let onlineOptions = [
      {label: '在线', value: 'true'},
      {label: '离线', value: 'false'},
    ]

    let {proxy} = getCurrentInstance();
    let rtvs = ref(proxy.$env.rtvs);
    let playChannel = ref();
    let playModal = ref(false);
    let editModel = ref({});
    let searchModel = ref({});
    let channelSearchModel =ref({});
    let channelDeviceModel =ref({});
    return {columns, rules, editModel, searchModel, onlineOptions, channelDeviceModel, channelColumns, channelSearchModel, rtvs, playChannel, playModal}
  },
  methods: {
    resolveParams({data}) {
      return Promise.resolve({deviceId: data.deviceId, channelId: data.channelId});
    },
    channelOpenHandle(model) {
      this.channelSearchModel['deviceId'] = model['deviceId']
    },
    openPlayModal(channel) {
      this.playModal = true;
      this.playChannel = channel;
    }
  }
}
</script>
<style scoped> </style>
