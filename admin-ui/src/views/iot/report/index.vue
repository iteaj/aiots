<template>
  <UView>
    <AFlex vertical style="flex-grow: 1;">
      <div style="padding: 5px; background-color: #ffffff; border-radius: 5px">
        <ARow :gutter="16" style="padding: 8px;" align="middle" warp>
          <ACol :span="12">
            <div v-for="(item, index) in conditions" :key="index" style="margin: 3px 0;">
              <ASpace>
                <USelect size="small" :options="products" v-model:value="item.productId" placeholder="产品" style="width:220px;"></USelect>
                <USelect size="small" :url="`/iot/device/listOfProducts?productIds=${item.productId || ''}`" @loaded="() => item.uid=null"
                         v-model:value="item.uid" @change="(value, model) => deviceChange(item, model)" placeholder="设备" style="width: 150px;" valueField="uid" labelField="name"></USelect>
                <USelect size="small" :url="`/iot/modelAttr/listByProductId?productId=${item.productId || ''}`" @loaded="() => item.field=null"
                         v-model:value="item.field" @change="(value, model) => fieldChange(item, model)" placeholder="属性" style="width: 120px;" valueField="field" labelField="name"></USelect>
                <AButton size="small" danger type="dashed" @click="() => delItem(index)">删除</AButton>
              </ASpace>
            </div>
          </ACol>
          <ACol :span="12">
            <ASpace>
              <div @click="addItem"><AButton type="dashed">添加</AButton></div>
              <div><ARangePicker v-model:value="selectTimes" :presets="rangePresets" @change="timeChange"></ARangePicker></div>
              <div><AButton type="primary" @click="search" :loading="spinning">搜索</AButton></div>
            </ASpace>
          </ACol>
        </ARow>
      </div>
      <div style="margin-top: 8px">
        <ASpin :spinning="spinning" description="数据加载中...">
          <ACard class="u-row" size="small">
            <template #title>
              <AFlex style="width: 100%; font-weight: unset; padding: 5px 0;">
                <div style="">
                  <ARadioGroup v-model:value="selectCountType" button-style="solid" optionType="button">
                    <a-radio-button value="all">采集值</a-radio-button>
                    <!--                    <a-radio-button value="count">采集量</a-radio-button>-->
                    <a-radio-button value="avg">平均值</a-radio-button>
                    <a-radio-button value="max">最大值</a-radio-button>
                    <a-radio-button value="min">最小值</a-radio-button>
                  </ARadioGroup>
                </div>
              </AFlex>
            </template>
            <div id="iv-echarts-line" style="height: 380px"></div>
          </ACard>
        </ASpin>
      </div>
    </AFlex>
  </UView>
</template>
<!--通用数据报表-->
<script>
import * as echarts from 'echarts/core'
import {LineChart} from 'echarts/charts';
import {CanvasRenderer} from 'echarts/renderers';
import {UniversalTransition} from 'echarts/features';
import {
  DataZoomComponent,
  ToolboxComponent,
  DatasetComponent,
  GridComponent,
  LegendComponent,
  TitleComponent,
  TooltipComponent,
  VisualMapComponent
} from 'echarts/components'

import {computed, ref, shallowRef, watch} from "vue";
import CoreConsts from "@/components/CoreConsts";
import {GET, POST} from "@/utils/request";
import {msgError} from "@/utils/message";
import dayjs from "dayjs";
echarts.use([CanvasRenderer, TitleComponent, GridComponent, VisualMapComponent, LineChart
  , UniversalTransition, TooltipComponent, LegendComponent, DatasetComponent, ToolboxComponent, DataZoomComponent])

export default {
  name: "Report",
  setup() {
    let spinning = ref(false);
    let selectDevice = ref();
    let selectAttr = ref();
    let selectAttrId = ref(0);
    let selectProduct = ref();
    let attrs = ref([]);
    let products = ref([]);
    let devices = ref([]);
    let selectTimes = ref([dayjs().startOf('day'), dayjs().endOf('day')]);

    let rangePresets = ref([
      {label: '近一周', value: [dayjs().subtract(6, 'day'), dayjs()]},
      {label: '当月', value: [dayjs().startOf('month'), dayjs()]},
      {label: '上个月', value: [dayjs().subtract(1, 'month'), dayjs().subtract(1, 'month')]},
    ]);
    let conditions = ref([{productId: null, uid: null, field: null}]);
    let collectDataEcharts = shallowRef();
    let selectTimeType = ref('month');
    let selectCountType = ref('all');

    watch(() => products.value, (newValue) => {
      if(newValue instanceof Array && newValue.length > 0) {
        selectProduct.value = newValue[0].value;
      }
    })

    let attrsOption = computed(() => {
      return attrs.value.map(attr => {
        return {value: attr.id, payload: attr};
      })
    })

    let collectDataOptions = ref({
      title: {
        text: '',
        left: 'center',
        show: true
      },
      dataZoom: {
        type: 'slider',
        id: 'iv-echarts-line',
        bottom: '12px',
        show: true
      },
      // visualMap: [
      //   {
      //     show: false,
      //     type: 'continuous',
      //     seriesIndex: 0,
      //     min: 0,
      //     max: 20
      //   }
      // ],
      legend: {
        data: []
      },
      tooltip: {
        trigger: 'axis',
        // formatter: '',
        // formatter: function (params) {
        //   console.log(params)
        //   return '{b0}: {c0}'
        // }
      },
      toolbox: {
        feature: {
          saveAsImage: {show: true}
        }
      },
      grid: {
        top: '12%',
        left: '4%',
        right: '3%',
        bottom: '45px',
        containLabel: true
      },
      xAxis: {
        type: 'category',
        boundaryGap: false,
        data: [],
      },
      yAxis: {
        type: 'value',
        // name: '单位: -',
        // nameLocation: 'end',
        nameTextStyle: {
          // padding: [0, 0, 10, -40]
        }
      },
      series: []
    });

    const doSearch = (timeRange, value) => {
      let unit = '-';
      // collectDataOptions.value.title.text = `${selectDevice.value.name}的${selectAttr.value.name}值报表`;
      if(selectCountType.value == 'all') {
        // collectDataOptions.value.tooltip.formatter = `{a0}<br />{b0}   &nbsp;&nbsp;&nbsp; {c0} ${unit}`;
        // collectDataOptions.value.yAxis.name = `单位: ${unit}`
        spinning.value = true;
        POST('/iot/common/value/data', {searchType: 'list', timeType: 'day', timeRange, value})
            .then(({code, message, data}) => {
              if(code == CoreConsts.SuccessCode) {
                if(data instanceof Array) {
                  data.forEach((datum, index) => {
                    if(datum) {
                      let xAxis = datum.values.map(item => item.collectTime);
                      let yAxis = datum.values.map(item => item.value);
                      collectDataOptions.value.xAxis.data = xAxis;
                      collectDataOptions.value.series[index].data = yAxis;
                    } else {
                      collectDataOptions.value.xAxis.data = [''];
                      collectDataOptions.value.series[index].data = [0];
                    }
                  })

                  collectDataEcharts.value.setOption(collectDataOptions.value);
                }
              } else {
                // this.$msg.error(message)
              }
            }).finally(() => spinning.value = false);
      } else {
        if(selectCountType.value == 'count') {
          // collectDataOptions.value.yAxis.name = `单位: 条`
          // collectDataOptions.value.tooltip.formatter = `{a0}<br />{b0}   &nbsp;&nbsp;&nbsp; {c0}条`;
        } else {
          // collectDataOptions.value.yAxis.name = `单位: ${unit}`
          // collectDataOptions.value.tooltip.formatter = `{a0}<br />{b0}   &nbsp;&nbsp;&nbsp; {c0} ${unit}`;
        }

        spinning.value = true;
        POST('/iot/common/count/data', {calcType: selectCountType.value, timeType: 'day', timeRange, value})
            .then(({code, message, data}) => {
              if(code == CoreConsts.SuccessCode) {
                if(data instanceof Array) {
                  data.forEach((datum, index) => {
                    if(datum) {
                      let xAxis = datum.data.map(item => item.category);
                      let yAxis = datum.data.map(item => item.value);
                      collectDataOptions.value.xAxis.data = xAxis;
                      collectDataOptions.value.series[index].data = yAxis;
                    } else {
                      collectDataOptions.value.xAxis.data = [''];
                      collectDataOptions.value.series[index].data = [0];
                    }
                  })

                  collectDataEcharts.value.setOption(collectDataOptions.value);
                }
              } else {
                // this.$msg.error(message)
              }
            }).finally(() => spinning.value = false);
      }
    }

    const search = () => {
      for (let condition of conditions.value) {
        if(!condition.uid || !condition.field || !condition.productId) {
          return msgError("参数不完成");
        }
      }
      if(!selectTimes.value) {
        return msgError("请选择时间");
      }

      let timeRange = [selectTimes.value[0].format('YYYY-MM-DD HH:mm:ss'), selectTimes.value[1].format('YYYY-MM-DD HH:mm:ss')];
      collectDataOptions.value.legend.data = conditions.value.map(item => item.deviceName + '[' +item.name+']');
      collectDataOptions.value.series = conditions.value.map(item => {return {name: item.deviceName + '[' + item.name + ']', type: 'line', data: []}});
      let value = conditions.value.map(item => `D:${item['uid']}:${item['field']}`);
      doSearch(timeRange, value);
    }

    return {selectProduct, products, devices, attrs, selectAttr, selectAttrId, attrsOption, selectDevice, rangePresets
      , collectDataOptions, selectTimeType, collectDataEcharts, selectCountType, conditions, selectTimes, search, spinning}
  },
  mounted() {
    this.$http.get('/iot/product/list', {params: {deviceType: 'Child'}}).then(({code, message, data}) => {
      if(code == CoreConsts.SuccessCode) {
        this.products = data;
      } else {
        this.$msg.warn(message);
      }
    })

    this.collectDataEcharts = echarts.init(document.getElementById("iv-echarts-line"));
    this.collectDataEcharts.setOption(this.collectDataOptions);

  },
  methods: {
    addItem() {
      this.conditions.push({productId: null, uid: null, field: null});
    },
    delItem(index) {
      if(this.conditions.length == 1) {
        return;
      }
      this.conditions.splice(index, 1);
    },
    deviceChange(item, model) {
      item.deviceName = model.name;
    },
    fieldChange(item, model) {
      item.name = model.name;
    },
    timeChange(value) {
      if(value && value.length == 2) {
        this.selectTimes[0] = value[0].startOf('day');
        this.selectTimes[1] = value[1].endOf('day');
      }
    }
  }
}
</script>

<style scoped>
.iv-attr-item {
  cursor: pointer;
  padding: 8px 0px;
  margin: 2px 0px;
  border-radius: 5px;
  text-align: center;
}
.iv-attr-item:hover {
  background-color: #f9f9f9;
}
.iv-attr-item.active {
  background-color: rgba(25, 176, 254, 0.08);
}
</style>