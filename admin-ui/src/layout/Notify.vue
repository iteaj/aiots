<template>
  <a-border-beam :outset="0" :color="beamColor">
  <div class="iv-notify-container">
    <a-tabs v-model:activeKey="activeKey" centered :classes="{content: 'iv-notify-content'}">
      <template #rightExtra>
        <a-button :disabled="activeKey !== 'warn'" type="link" @click="clickHandle">查看更多>></a-button>
      </template>
      <a-tab-pane key="warn" tab="告警">
        <div v-if="warns.length > 0" class="iv-notify-item warn">
          <div class="iv-item-content" v-for="warn in warns" :key="warn.id">
            <div class="desc">
              {{warn.content}}
            </div>
            <div class="meta">
              <a-space>
                <span>{{warn.warnTime}}</span>
                <span> · </span>
                <span :class="warn.grade">{{warn.gradeName}}</span>
                <span> · </span>
                <span>{{warn.deviceName}}</span>
              </a-space>
            </div>
          </div>
        </div>
        <a-empty v-else description="暂无告警" />
      </a-tab-pane>
      <a-tab-pane key="notify" tab="通知">
        <a-empty description="暂无通知" />
      </a-tab-pane>
    </a-tabs>
  </div>
  </a-border-beam>
</template>
<script>
import {ref} from "vue";
import CoreConsts from "@/components/CoreConsts.js";
import dayjs from "dayjs";

export default {
  name: "Notify",
  props: ["modelValue", "onUpdate:modelValue"],
  setup(props) {
    let warns = ref([
        {content: '温度超出40℃', grade: 'fatal', gradeName: '严重', deviceName: '温湿度设备', warnTime: '2026-10-30 10:10:10', uid: '2394032'},
        {content: '温度超出40℃', grade: 'warn', gradeName: '告警', deviceName: '温湿度设备', warnTime: '2026-10-30 10:10:10', uid: '2394032'},
        {content: '温度超出40℃', grade: 'urgent', gradeName: '紧急', deviceName: '温湿度设备', warnTime: '2026-10-30 10:10:10', uid: '2394032'},
        {content: '温度超出40℃', grade: 'notice', gradeName: '通知', deviceName: '温湿度设备', warnTime: '2026-10-30 10:10:10', uid: '2394032'},
        {content: '温度超出40℃', grade: 'status', gradeName: '状态', deviceName: '温湿度设备', warnTime: '2026-10-30 10:10:10', uid: '2394032'},
    ]);
    let notifys = ref([]);
    let activeKey = ref('warn');
    let beamColor = [
      {
        color: '#1677ff',
        percent: 0,
      },
      {
        color: '#36cfc9',
        percent: 52,
      },
      {
        color: '#95de64',
        percent: 100,
      },
    ];
    return {warns, notifys, beamColor, activeKey}
  },
  mounted() {
    let startTimestamp = dayjs().startOf('day');
    this.$http.get("/iot/warnRecord/view?current=1&size=5&startTimestamp="+startTimestamp).then(({code, data, message}) => {
      if(code == CoreConsts.SuccessCode) {
        this.warns = data['records'];
      } else {
        console.error(message);
      }
    })
  },
  methods: {
    clickHandle() {
      if(this.activeKey === 'warn') {
        this.$router.push({path: '/iot/warnRecord'});
      }
    }
  }
}
</script>
<style scoped>
.iv-notify-container {
  padding: 8px 16px;
  border: var(--iv-color-split);
  //list-style-type: none;
  background-color: var(--ant-color-bg-elevated);
  //background-clip: padding-box;
  border-radius: var(--ant-border-radius-lg);
  //outline: none;
  width: 430px;
  box-shadow: var(--ant-box-shadow-secondary);
}
.iv-notify-item .iv-item-content {
  padding: 6px 12px;
  cursor: pointer;
  margin: 3px 0;
  border-radius: var(--ant-border-radius);
}
.iv-notify-item .iv-item-content:hover {
  background-color: var(--ant-color-bg-container-disabled);
}
.iv-notify-item .iv-item-content .meta {
  color: var(--ant-color-text-tertiary);
  font-size: var(--ant-font-size-sm);
}
.meta .fatal {
 color: var(--ant-color-error-text);
}
.meta .warn {
 color: var(--ant-color-warning);
}
.meta .urgent {
 color: var(--ant-color-error-text-active)
}
.meta .notice {
  color: var(--ant-color-primary-text)
}
.meta .normal {
  color: var(--ant-color-success-text);
}
.meta .status {
 color: var(--ant-color-warning-text-active)
}
.iv-notify-container :deep(.iv-notify-content) {
  min-height: 120px;
  max-height: 360px;

}
</style>