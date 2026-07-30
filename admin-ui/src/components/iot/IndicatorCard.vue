<template>
  <AFlex class="iv-attr-container" justify="center" align="middle" wrap="wrap" gap="8">
    <div v-for="attr in dataSource" :key="attr.id" class="iv-attr-item">
      <a-badge-ribbon :text="attr.indicator || ''" :color="indicatorColors[attr.indicator] || gradeColorMap[attr.grade]?.[0] || 'default'">
        <a-card variant="borderless" hoverable size="small" :class="activeAttrId==attr.id? 'active' : ''" @click="() => onAttrClick(attr)" :styles="{head: {minHeight: '28px'}, body: {padding: '6px'}}">
          <template #title>
            <AFlex gap="10" justify="space-between">
              <div>
                <span style="font-weight: normal; font-size: 12px; color: #818181;">{{attr.field}}</span>
              </div>
            </AFlex>
          </template>
          <ARow>
            <ACol span="14" style="text-align: center">
              <div>
                <span style="font-weight: bold; font-size: 14px;">{{attr.label || (attr.value != null ? attr.value : '-')}}</span>
                <span class="iv-minor-text">&nbsp;{{attr.unit ? `(${attr.unit})` : ''}}</span>
              </div>
              <div class="iv-text-ellipsis iv-color-text-tertiary iv-font-text-tertiary">{{attr.name}}</div>
            </ACol>
            <ACol span="10" style="text-align: center">
              <div class="iv-text-ellipsis" style="text-align: center">
                {{attr.address || '-'}}
              </div>
              <div class="iv-color-text-tertiary iv-font-text-tertiary">地址</div>
            </ACol>
          </ARow>
          <div style="font-size: 12px; color: #818181; text-align: center; padding-top: 6px; border-top: 1px dashed var(--ant-color-split)">
            <span>{{attr.collectTime || '暂未采集'}}</span>
          </div>
        </a-card>
      </a-badge-ribbon>
    </div>
  </AFlex>
</template>
<script setup>
/**
 * 指标卡片组件 - 模型属性卡片列表
 * 点击卡片选中并触发attr-click事件(参数为当前属性), 再次点击取消选中(参数为null)
 */
import {ref} from "vue";
import {useStore} from "vuex";

defineOptions({
  name: "IndicatorCard",
})
const props = defineProps({
  dataSource: {type: Array, default: () => []}, // 模型属性列表
})
const emits = defineEmits(['attr-click']);
let activeAttrId = ref();
let indicatorColors = useStore().getters["sys/indicatorColors"];
let gradeColorMap = {
  fatal: ['red', '严重'], urgent: ['magenta', '紧急'], warn: ['yellow', '警告'], notice: ['cyan', '通知']
  , normal: ['green', '正常'], indicator: ['purple', '指标'], default: ['gray', '']
}
const onAttrClick = (attr) => {
  if(activeAttrId.value == attr.id) {
    activeAttrId.value = null;
    emits('attr-click', null);
  } else {
    activeAttrId.value = attr.id;
    emits('attr-click', attr);
  }
}
</script>

<style scoped>
.iv-attr-container {
  flex: 1;
  overflow: auto;
}
.iv-attr-item {
  min-width: 180px;
  padding: 0 5px;
  height: 116px;
  margin: 5px 0 5px;
}
.iv-attr-item .active {
  box-shadow: 0 0 2px -2px rgb(0 129 251 / 61%), 0 0 8px 0 rgb(45 159 250 / 61%), 0 1px 12px 0 rgb(185 211 251 / 75%)
}
.iv-minor-text {
  font-size: 12px;
  color: #8e8e8e;
}
</style>
