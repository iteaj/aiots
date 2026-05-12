<template>
  <UModal :uid="uid" width="85%" @open="open" @cancel="close">
    <template #title>
      <AFlex justify="space-between">
        <span>{{title ? title : `设备[${device.name}]指令集`}}</span>
        <a-tag color="cyan" v-if="copyValue != null">{{copyValue}}</a-tag>
        <span style="margin-right: 24px; color: #a8a8a8; font-weight: 0; font-size: 12px;">注: 点击指令进行复制</span>
      </AFlex>
    </template>
    <ASpin :description="tipText" :spinning="spinning">
      <UTable uid="cmdTable" :columns="columns" :pagination="false" ref="cmdTable" size="small">
        <template #commandValue="{record}">
          <a-typography-paragraph :copyable="{text: record.commandValue, tooltip: false, onCopy: () => copy(record)}" style="margin-bottom: 0px">
            <template #copyableIcon>
              <span>{{record.commandName}}</span>
            </template>
          </a-typography-paragraph>
        </template>
        <template #action="{record}">
          <ATag color="#87d068" style="cursor: pointer;" @click="() => invoke(record)">执行</ATag>
        </template>
      </UTable>
    </ASpin>
    <template #footer>
      <span></span>
    </template>
  </UModal>
</template>

<script>
import {ref} from "vue";
import CoreConsts from "@/components/CoreConsts";

export default {
  name: "commands",
  props: ["uid", "title"],
  setup: (props) => {
    let cmdTable = ref();
    let tipText = ref();
    let spinning = ref(false);
    let device = ref({});
    let copyValue = ref();
    let columns = ref([
      {field: "apiName", title: "接口名称", width: 180, resizable: true},
      {field: "commandValue", title: "指令"},
      {field: "remark", title: "指令说明", width: 320, resizable: true},
      {field: "apiTypeName", title: "接口类型", width: 80},
      {field: 'action', title: '操作', type: 'action' , width: 80, fixed: 'right'},
    ]);

    return {columns, device, cmdTable, copyValue, spinning, tipText}
  },
  methods: {
    open(device) {
      this.device = device;
      this.spinning = true;
      this.tipText = "指令集加载中...";
      this.$http.get(`/iot/instruct/commands?uid=${device.uid}`).then(({code, data, message}) => {
        if(code == CoreConsts.SuccessCode) {
          this.cmdTable.getTableContext().setDataSource(data);
        } else {
          this.$msg.error(message);
        }
      }).finally(() => this.spinning = false)
    },
    invoke(record) {
      this.spinning = true;
      this.tipText = "执行指令中...";
      this.$http.post(`/iot/instruct/command/invoke`, {code: record.commandValue}).then(({code, data, message}) => {
        if(code != CoreConsts.SuccessCode) {
          this.$msg.error(message);
        } else {
          this.$msg.success("调用成功");
        }
      }).finally(() => this.spinning = false)
    },
    copy(value) {
      this.copyValue = "成功复制 " + value.commandValue;
    },
    close() {
      this.copyValue = null;
      this.cmdTable.getTableContext().setDataSource([]);
    }
  }
}
</script>

<style scoped>

</style>