<template>
  <UView>
    <UViewSearch v-model="searchModel">
      <URow col="search">
        <URadioItem field="isOnline" label="状态" :options="Status" optionType="button" buttonStyle="solid"/>
        <UButton func="query" url="/iot/dahua/access" ref="searchRef">搜索</UButton>
        <UButton func="reset">重置</UButton>
      </URow>
    </UViewSearch>
    <UViewTable :columns="columns">
      <template #action="{record}">
        <UTag func="open" uid="channel" :data="record">通道列表</UTag>
      </template>
    </UViewTable>
    <UDrawer uid="channel" title="通道列表" v-model:source="selectModel" placement="top" size="100%">
      <ULinkView uid="channelView">
        <UViewTable :columns="channelColumns" :dataSource="channelData">
          <template #action="{record}">
            <UTag func="ajax" color="processing" url="/iot/dahua/access/ctrl?type=openDoor" :data="{channelCodeList: [record['channelCode']]}">开门</UTag>
            <UTag func="ajax" color="volcano" url="/iot/dahua/access/ctrl?type=closeDoor" :data="{channelCodeList: [record['channelCode']]}">关门</UTag>
            <UTag func="ajax" color="gold" url="/iot/dahua/access/ctrl?type=stayOpen" :data="{channelCodeList: [record['channelCode']]}">设置常开</UTag>
            <UTag func="ajax" color="lime" url="/iot/dahua/access/ctrl?type=stayClose" :data="{channelCodeList: [record['channelCode']]}">设置常闭</UTag>
            <UTag func="ajax" color="cyan" url="/iot/dahua/access/ctrl?type=normal" :data="{channelCodeList: [record['channelCode']]}">正常模式</UTag>
          </template>
        </UViewTable>
      </ULinkView>
    </UDrawer>

  </UView>
</template>

<script>
import {computed, ref} from "vue";
import IccPlay from "@/icc/IccPlay.vue";

export default {
  name: "Access",
  components: {IccPlay},
  setup() {
    let Manufacturer = ref([
      {label: '大华', value: '1'},
      {label: '海康', value: '2'},
    ]);
    let Status = ref([
      {label: '离线', value: '0'},
      {label: '在线', value: '1'},
    ]);
    let Reason = ref([
      {label: '账号或密码错误', value: '1'},
      {label: '用户名不存在', value: '2'},
      {label: '登录超时', value: '3'},
      {label: '重复登录', value: '4'},
      {label: '帐号被锁定', value: '5'},
      {label: '帐号被列入黑名单', value: '6'},
      {label: '系统忙,资源不足', value: '7'},
      {label: '子连接失败', value: '8'},
      {label: '主连接失败', value: '9'},
      {label: '超过最大连接数', value: '10'},
      {label: '只支持3代协议', value: '11'},
      {label: '设备未插入U盾或U盾信息错误', value: '12'},
      {label: '客户端IP地址没有登录权限', value: '13'},
      {label: '设备账号未初始化，无法登陆', value: '14'},
      {label: '账号或密码错误', value: '17'},
      {label: '设备接入服务离线', value: '18'},
      {label: '未知离线原因', value: '99'},
      {label: 'ip冲突', value: '109'},
    ])
    let columns = ref([
      {field: 'deviceName', title: '设备名称'},
      {field: 'deviceSn', title: '设备编号'},
      {field: 'deviceManufacturer', title: '设备厂商', options: Manufacturer, width: 80},
      {field: 'deviceCode', title: '设备码', width: 80},
      {field: 'gpsX', title: '经度', width: 100},
      {field: 'gpsY', title: '纬度', width: 100},
      {field: 'createTime', title: '创建时间', type: 'date', format: 'YYYY-MM-DD'},
      {field: 'onlineUpdateTime', title: '状态更新时间', type: 'date', format: 'YYYY-MM-DD'},
      {field: 'offlineReason', title: '离线原因', options: Reason},
      {field: 'isOnline', title: '是否在线', options: Status, width: 80, fixed: 'right'},
      {field: 'action', title: '操作', type: 'action', fixed: 'right'},
    ]);

    let channelColumns = ref([
      {field: 'channelName', title: '通道名称'},
      {field: 'channelCode', title: '通道编码'},
      {field: 'channelSeq', title: '通道序号'},
      {field: 'access', title: '是否接入'},
      {field: 'memo', title: '通道描述'},
      {field: 'action', type: 'action', title: '操作', width: 360},
    ])

    let searchModel = ref({});
    let selectModel = ref({units: []});
    let channelData = computed(() => {
      let unit = selectModel.value['units'];
      let channels = [];
      if(unit instanceof Array) {
        unit.forEach(item => {
          channels.push(...item['channels']);
        })
      }

      return channels;
    })
    return {columns, searchModel, Manufacturer, Status, selectModel, channelData, channelColumns}
  }
}
</script>

<style scoped>

</style>