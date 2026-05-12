<template>
  <div style="padding: 24px 100px 64px; background: #ffffff">
    <ASpin :spinning="spinning" description="数据保存中...">
      <a-tabs v-model:activeKey="activeKey" centered>
        <template v-for="(item, index) in northwards" :key="item.name">
          <a-tab-pane v-if="item['type']=='mqtt_publish'" :key="item.name" :tab="item.name">
            <UForm :ref="item.name" v-model="northwards[index]" :span="[6, 12]">
              <URow :gutter="12" :span="12">
                <UInputItem label="主机" required field="config.host" />
                <UInputNumberItem label="端口" required field="config.port" :min="0" :max="65535" :step="1" />
                <UInputItem label="用户名" field="config.username" />
                <UInputPasswordItem label="密码" field="config.password" />
                <UInputItem label="clientId" required field="config.clientId" />
                <UInputNumberItem label="消息等级" required field="config.qos" :min="0" :max="2" :step="1" :defaultValue="0" />
                <UInputItem label="发布主题" required field="config.topic" />
                <URadioItem label="状态" required field="status" :options="statusOptions" />
              </URow>
            </UForm>
            <div style="text-align: center">
              <ASpace>
                <AButton type="primary" v-auth="'open:northward:edit'" @click="() => submit(item)">保存</AButton>
                <AButton type="dashed" danger v-auth="'open:northward:edit'" @click="() => statusUpdate(item)">
                  <span v-if="item.connectStatus=='online'">断开</span>
                  <span v-else>连接</span>
                </AButton>
                <span style="color: #a0a0a0">修改完配置先保存后才能做连接/断开的操作</span>
              </ASpace>
            </div>
          </a-tab-pane>
        </template>
      </a-tabs>
    </ASpin>
  </div>
</template>
<!-- 北向接入功能 -->
<script>
import {ref, reactive} from "vue";
import CoreConsts from "@/components/CoreConsts";
export default {
  name: "Northward",
  setup() {
    let spinning = ref(false);
    let northwards = ref([]);
    let activeKey = ref('');
    let statusOptions = ref([
      {label: '启用', value: true},
      {label: '禁用', value: false},
    ]);
    return {activeKey, northwards, statusOptions, spinning}
  },
  mounted() {
    this.loadData();
  },
  methods: {
    loadData() {
      this.$http.get("/open/northward/view").then(({code, message, data}) => {
        if(code == CoreConsts.SuccessCode) {
          this.northwards = data || [];
          if(this.northwards[0]) {
            this.activeKey = this.northwards[0].name;
          }
        } else {
          this.$msg.error(message);
        }
      })
    },
    submit(model) {
      this.$refs[model.name][0].validate().then(() => {
        this.spinning = true;
        this.$http.post("/open/northward/update", model).then(({code, message}) => {
          if(code != CoreConsts.SuccessCode) {
            this.$msg.error(message);
          } else {
            this.$msg.success("更新成功")
          }
        }).finally(() => this.spinning = false)
      })
    },
    statusUpdate(model) {
      this.$refs[model.name][0].validate().then(() => {
        this.spinning = true;

        let connectStatus = model.connectStatus == 'online' ? 'offline' : 'online';
        this.$http.post("/open/northward/switchStatus?" + `connectKey=${model.connectKey}&connectStatus=${connectStatus}`, model).then(({code, message}) => {
          if(code != CoreConsts.SuccessCode) {
            this.$msg.error(message);
          } else {
            this.loadData();
            this.$msg.success("切换成功");
          }
        }).finally(() => this.spinning = false)
      })
    }
  }
}
</script>
<style scoped> </style>
