<template>
  <UView >
    <UViewSearch v-model="searchModel">
      <URow col="search">
        <UInputItem field="name" label="" :allowClear="true"/>
        <UInputItem field="customName" label="" :allowClear="true"/>
        <UInputNumberItem field="ptzType" label="" :allowClear="true"/>
        <URadioItem field="status" label="" :allowClear="true"/>
        <UButton func="query" url="/gbs/device/channels">搜索</UButton>
        <UButton func="reset">重置</UButton>
        <UButton func="add" url="/gbs/deviceChannel/add" v-auth="'iot:deviceChannel:add'">新增</UButton>
      </URow>
    </UViewSearch>
    <UViewModal title="管理" :span="[7, 14]" v-model="editModel" :rules="rules">
      <template #default="{model}">
        <UInputItem field="channelId" label="" />
        <UInputItem field="name" label="" />
        <UInputItem field="customName" label="" />
        <UInputItem field="manufacture" label="" />
        <UInputItem field="model" label="" />
        <UInputItem field="owner" label="" />
        <UInputItem field="civilCode" label="" />
        <UInputItem field="block" label="" />
        <UInputItem field="address" label="" />
        <UInputItem field="parentId" label="" />
        <UInputNumberItem field="safetyWay" label="" />
        <UInputNumberItem field="registerWay" label="" />
        <UInputItem field="certNum" label="" />
        <UInputNumberItem field="certifiable" label="" />
        <UInputNumberItem field="errCode" label="" />
        <UInputItem field="endTime" label="" />
        <UInputItem field="secrecy" label="" />
        <UInputItem field="ipAddress" label="" />
        <UInputNumberItem field="port" label="" />
        <UInputItem field="password" label="" />
        <UInputNumberItem field="ptzType" label="" />
        <UInputNumberItem field="customPtzType" label="" />
        <URadioItem field="status" label="" />
        <UInputNumberItem field="longitude" label="" />
        <UInputNumberItem field="customLongitude" label="" />
        <UInputNumberItem field="latitude" label="" />
        <UInputNumberItem field="customLatitude" label="" />
        <UInputItem field="streamId" label="" />
        <UInputItem field="deviceId" label="" />
        <UInputItem field="parental" label="" />
        <URadioItem field="hasAudio" label="" />
        <UInputNumberItem field="subCount" label="" />
        <UInputNumberItem field="longitudeGcj02" label="" />
        <UInputNumberItem field="latitudeGcj02" label="" />
        <UInputNumberItem field="longitudeWgs84" label="" />
        <UInputNumberItem field="latitudeWgs84" label="" />
        <UInputItem field="businessGroupId" label="" />
        <UInputItem field="gpsTime" label="" />
      </template>
      <template #footer>
        <UButton func="cancel">取消</UButton>
        <UButton func="submit" url="/iot/deviceChannel/saveOrUpdate">提交</UButton>
        <UButton func="reset">重置</UButton>
      </template>
    </UViewModal>
    <UViewTable :columns="columns" :scroll="{x: 1200}">
      <template #action="{record}">
        <UTag func="edit" :data="record" url="/iot/deviceChannel/edit" v-auth="'iot:deviceChannel:edit'">修改</UTag>
        <UTag func="del" :data="record" url="/iot/deviceChannel/del" v-auth="'iot:deviceChannel:del'">删除</UTag>
      </template>
    </UViewTable>
  </UView>
</template>
<!-- 功能 -->
<script>
import {ref, reactive} from "vue";
export default {
  name: "DeviceChannel",
  setup() {
    let columns = ref([
      {field: "channelId", title: "通道编号"},
      {field: "deviceId", title: "设备编号"},
      {field: "name", title: "通道名称"},
      {field: "customName", title: ""},
      {field: "manufacture", title: "生产厂商"},
      {field: "model", title: "型号"},
      {field: "owner", title: "设备归属"},
      {field: "civilCode", title: "行政区域"},
      {field: "block", title: "警区"},
      {field: "address", title: "安装地址"},
      {field: "parentId", title: "父级"},
      {field: "safetyWay", title: "信令模式"},
      {field: "registerWay", title: "注册方式"},
      {field: "certNum", title: "证书序号"},
      {field: "certifiable", title: "证书标识"},
      {field: "errCode", title: "证书无效原因"},
      {field: "endTime", title: "证书终止有效期"},
      {field: "secrecy", title: "保密属性"},
      {field: "ipAddress", title: "ip地址"},
      {field: "port", title: "端口号"},
      {field: "password", title: "密码"},
      {field: "ptzType", title: "云台类型"},
      {field: "customPtzType", title: ""},
      {field: "status", title: "在线状态"},
      {field: "longitude", title: "经度"},
      {field: "customLongitude", title: ""},
      {field: "latitude", title: "纬度"},
      {field: "customLatitude", title: ""},
      {field: "streamId", title: "流编号"},
      {field: "deviceId", title: "国标编号"},
      {field: "parental", title: "是否有子设备"},
      {field: "hasAudio", title: "是否有音频"},
      {field: "createTime", title: "创建时间"},
      {field: "updateTime", title: "更新时间"},
      {field: "subCount", title: "子设备数"},
      {field: "longitudeGcj02", title: "GCJ02经度"},
      {field: "latitudeGcj02", title: "GCJ02纬度"},
      {field: "longitudeWgs84", title: "WGS84经度"},
      {field: "latitudeWgs84", title: "WGS84纬度"},
      {field: "businessGroupId", title: "业务分组"},
      {field: "gpsTime", title: "GPS更新时间"},
      {field: 'action', title: '操作', type: 'action', fixed: 'right', width: 180},
    ]);

    let rules = reactive({
        channelId: {required: true, message: '必填'},
        deviceId: {required: true, message: '必填'},
        createTime: {required: true, message: '必填'},
        updateTime: {required: true, message: '必填'},
    })

    let editModel = ref({});
    let searchModel = ref({});
    return {columns, rules, editModel, searchModel}
  },
}
</script>
<style scoped> </style>
