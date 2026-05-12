<template>
  <div style=" background-color: #fafafa; height: 100%; overflow: hidden auto">
    <div class="iv-design-header">
      <div class="title">
        <span>设备运行总览</span>
      </div>
      <div class="iv-header-status">
        <div style="width: 24px; height: 24px; position: absolute; left: 33px; top: 20px;">
          <a-badge :status="selectDevice.status == 'online' ? 'processing' : 'warning'" />
        </div>
      </div>
      <AFlex class="iv-header-content" justify="end" :gap="18">
        <div>
          <div class="iv-content-title">设备名称</div>
          <div class="iv-content-value iv-text-ellipsis">
            <span>{{selectDevice['name']}}</span>
          </div>
        </div>
        <div>
          <div class="iv-content-title">设备编号</div>
          <div class="iv-content-value iv-text-ellipsis">
            <span>{{selectDevice['deviceSn']}}</span>
          </div>
        </div>
        <div>
          <div class="iv-content-title">设备类型</div>
          <div class="iv-content-value iv-text-ellipsis">
            <span>{{ selectProduct['deviceTypeName'] }}</span>
          </div>
        </div>
        <div>
          <div class="iv-content-title">所属产品</div>
          <div class="iv-content-value iv-text-ellipsis">
            <span>{{ selectProduct['name'] }}</span>
          </div>
        </div>
      </AFlex>
    </div>
    <div style="padding: 16px 8px;">
      <ASpin :spinning="spinning" :description="tipText" size="large">
        <AFlex style="max-height: 270px; " justify="space-between" gap="small">
          <ACard size="small" style="min-width: 250px" :styles="{head: {padding: '0'}}">
            <template #title>
              <div class="iv-api-contain-head">
                <span>事件模型</span>
              </div>
            </template>
            <div style="height: 220px; overflow: auto;" v-if="eventApis && eventApis.length > 0">
              <div class="iv-api-item" v-for="item in eventApis" :key="item.code">
                <div class="iv-text-ellipsis">{{item.apiName}}</div>
                <div class="iv-text-ellipsis iv-cmd-desc" :title="item.remark">{{item.remark}}</div>
                <div class="iv-api-exec">
                  <AButton type="dashed" shape="circle" @click="() => apiCmdClick(item)">
                    <template #icon><SendOutlined style="color: #1c79f4"/></template>
                  </AButton>
                </div>
              </div>
            </div>
            <AEmpty v-else description="暂无事件接口" />
          </ACard>
          <a-row style="max-height: 270px; overflow: auto" justify="center" align="middle" wrap>
            <a-col v-for="attr in realtimeData" :key="attr.id" :span="3" class="iv-attr-item">
              <a-card variant="borderless" hoverable size="small" :class="activeAttrId==attr.id? 'active' : ''" @click="() => onAttrClick(attr)" :styles="{head: {minHeight: '28px'}, body: {padding: '6px'}}">
                <template #title>
                  <AFlex gap="10" justify="space-between">
                    <div>
                      <span style="font-weight: normal;">{{attr.name}}</span>
                    </div>
                    <div>
                      <a-badge :status="attr.__type" />
                    </div>
                  </AFlex>
                </template>
                <div>
                  <span style="font-weight: bold; font-size: 14px;">{{attr.value != null ? attr.value : '-'}}</span>
                  <span class="iv-minor-text">&nbsp;{{attr.unit ? `(${attr.unit})` : ''}}</span>
                </div>
                <div style="font-size: 12px; color: #818181; padding-top: 6px; border-top: 1px dashed #d6d6d6">
                  <span>{{attr.collectTime || '暂未采集'}}</span>
                </div>
              </a-card>
            </a-col>
          </a-row>
          <ACard size="small" style="min-width: 250px" :styles="{head: {padding: '0'}}">
            <template #title>
              <div class="iv-api-contain-head">
                <span>功能模型</span>
              </div>
            </template>
            <div style="height: 220px; overflow: auto;"  v-if="funcApis && funcApis.length > 0">
              <div class="iv-api-item" v-for="item in funcApis" :key="item.code">
                <div class="iv-text-ellipsis">{{item.apiName}}</div>
                <div class="iv-text-ellipsis iv-cmd-desc" :title="item.remark">{{item.remark}}</div>
                <div class="iv-api-exec">
                  <AButton type="dashed" shape="circle" @click="() => apiCmdClick(item)">
                    <template #icon><SendOutlined style="color: #1c79f4"/></template>
                  </AButton>
                </div>
              </div>
            </div>
            <AEmpty v-else description="暂无功能接口" />
          </ACard>
        </AFlex>
      </ASpin>
      <ACard size="small" style="margin-top: 12px;" :styles="{body: {padding: '8px 3px'}}">
        <template #title>
          <AFlex style="width: 100%; font-weight: unset; padding: 5px 0;">
            <div style="">
              <ARadioGroup v-model:value="selectCountType" button-style="solid" optionType="button">
                <a-radio-button value="all">采集值</a-radio-button>
                <a-radio-button value="count">采集量</a-radio-button>
                <a-radio-button value="avg">平均值</a-radio-button>
                <a-radio-button value="max">最大值</a-radio-button>
                <a-radio-button value="min">最小值</a-radio-button>
              </ARadioGroup>
            </div>
            <div style="flex-grow: 1; text-align: right; margin-right: 36px;">
              <ARadioGroup v-model:value="selectTimeType" button-style="solid" optionType="button">
                <a-radio-button value="week">近一周</a-radio-button>
                <a-radio-button value="month">近一月</a-radio-button>
                <a-radio-button value="current">当月</a-radio-button>
                <a-radio-button value="prevMonth">上个月</a-radio-button>
                <a-radio-button value="cus">自定义</a-radio-button>
              </ARadioGroup>
              <a-range-picker style="margin-left: 8px;" v-model:value="selectTime" show-time />
            </div>
          </AFlex>
        </template>
        <a-tabs v-model:activeKey="activeKey" tabPlacement="left">
          <a-tab-pane key="chart" tab="图表">
            <ARow :gutter="8">
              <ACol :span="15" id="iv-echarts-line" style="height: 280px"></ACol>
              <ACol :span="9" id="iv-echarts-warn" style="height: 280px"></ACol>
            </ARow>
          </a-tab-pane>
          <a-tab-pane key="table" tab="列表">
            <ARow :gutter="8">
              <ACol :span="12"  style="height: 280px">
                <ATable :columns="fieldColumns" :bordered="true" size="small" :pagination="dataPagination" :data-source="attrListData"></ATable>
              </ACol>
              <ACol :span="12" style="height: 280px">
                <ATable :columns="warnColumns" :bordered="true" size="small" :pagination="warnPagination" :data-source="warnListData"></ATable>
              </ACol>
            </ARow>
          </a-tab-pane>
        </a-tabs>

      </ACard>
    </div>
  </div>
</template>
<script setup>
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
import {getCurrentInstance, ref, defineOptions, onMounted, watch, onUnmounted} from "vue";
import CoreConsts from "@/components/CoreConsts";
import {GET, POST, urlConfig} from "@/utils/request";
import {msgError, msgSuccess, msgWarn} from "@/utils/message";
import {useRoute} from "vue-router";
import dayjs from "dayjs";
import {SendOutlined, HistoryOutlined} from '@antdv-next/icons'
import UTable from "@/components/table/BasicTable";
echarts.use([CanvasRenderer, TitleComponent, GridComponent, VisualMapComponent, LineChart
  , UniversalTransition, TooltipComponent, LegendComponent, DatasetComponent, ToolboxComponent, DataZoomComponent])
  let route = useRoute();
  defineOptions({
    name: "deviceDetail",
  })
  let apis = ref([]);
  let activeKey = ref('chart');
  let spinning = ref(false);
  let tipText = ref("执行中...");
  let selectDevice = ref({})
  let selectProduct = ref({});
  let activeAttrId = ref();
  let {proxy} = getCurrentInstance();
  let apiCodeDebugMap = ref({});

/**
 * @type {WebSocket | null}
 */
let realtimeSocket = null;
  let searchModel = ref({current: 1, size: 12});
  let fieldColumns = ref([
    {title: '名称', dataIndex: 'fieldName', align: 'center', width: 100, ellipsis: true, resizable: true},
    {title: '值', dataIndex: 'value', align: 'center', ellipsis: true, width: 100, resizable: true},
    {title: '单位', dataIndex: 'unit', align: 'center', ellipsis: true, width: 100},
    {title: '设备编号', dataIndex: 'deviceSn', align: 'center', ellipsis: true, width: 100},
    {title: '采集时间', dataIndex: 'collectTime', align: 'center', width: 160},
  ])

  let warnColumns = ref([
    {title: '名称', dataIndex: 'name', align: 'center', width: 100, ellipsis: true},
    {title: '等级', dataIndex: 'grade', align: 'center', width: 100},
    {title: '内容', dataIndex: 'content', align: 'center', width: 230, ellipsis: true, resizable: true},
    {title: '告警时间', dataIndex: 'warnTime', align: 'center', width: 130},
  ]);
  let realtimeWarnData = ref([]);
  let warnListData = ref([]);

  let warnDataEcharts;
  let collectDataEcharts;
  let realtimeData = ref([]);
  let attrListData = ref([]);
  let realtimeMap = ref({});
  let funcApis = ref([])
  let eventApis = ref([])
let selectAttr = ref(null);
let selectTime = ref();
let selectTimeType = ref('month');
let selectCountType = ref('all');
  let startTimestamp = ref("");
  let endTimestamp = ref("");
  const onAttrClick = (attr) => {
    if(selectAttr.value == attr) {
      selectAttr.value = null;
      activeAttrId.value = null;
    } else {
      selectAttr.value = attr;
      activeAttrId.value = attr.id;
    }
  }
  let dataPagination = ref({total: 0, showSizeChanger: false, current: 1, onChange(page, size) {
    this.current = page;
    let fieldId = selectAttr.value ? selectAttr.value.id : '';
    GET(`/iot/panels/data?uid=${selectDevice.value.uid}&fieldId=${fieldId}
      &startTimestamp=${startTimestamp.value}&endTimestamp=${endTimestamp.value}&size=5&current=${page}`).then(({code, message, data}) => {
      if(code === CoreConsts.SuccessCode) {
        if(data.records instanceof Array) {
          data.records.forEach((record) => {
            let attr = realtimeMap.value[record.field];
            if(attr != null) {
              record['unit'] = attr.unit;
            }
          })
          attrListData.value = data.records;
        }
        dataPagination.value.total = data.total;
      } else {
        console.error(message);
      }
    })
  }});
let warnPagination = ref({total: 0, showSizeChanger: false, current: 1, onChange(page, size) {
    this.current = page;
    let fieldId = selectAttr.value ? selectAttr.value.id : '';
    GET(`/iot/panels/warn?uid=${selectDevice.value.uid}&field=${fieldId}
      &startTimestamp=${startTimestamp.value}&endTimestamp=${endTimestamp.value}&size=5&page=${page}`).then(({code, message, data}) => {
      if(code === CoreConsts.SuccessCode) {
        warnListData.value = data.records;
      } else {
        console.error(message);
      }
    })
  }});
  const apiCmdClick = (api) => {
    spinning.value = true;
    tipText.value = "执行指令中...";
    POST(`/iot/instruct/command/invoke`, {code: api.commandValue}).then(({code, data, message}) => {
      if(code != CoreConsts.SuccessCode) {
        msgError(message);
      } else {
        msgSuccess("执行成功");
      }
    }).finally(() => spinning.value = false)
  }
  const realtimeMessage = (message) => {
    let value = JSON.parse(message.data);
    if(typeof value == 'object') {
      let {type, value: data} = value;
      if(type == 'model') {
        Object.values(data).forEach(value => {
          let realtime = realtimeMap.value[value['signalOrField']];
          if(realtime != null) {
            if(typeof value['value'] == 'boolean') {
              realtime['value'] = value['value'] + "";
            } else {
              realtime['value'] = value['value'];
            }

            realtime['status'] = value['status'];
            // realtime['collectTimeLong'] = value['collectTime'] || 0;
            realtime['collectTime'] = dayjs(value['collectTime']).format("YYYY-MM-DD HH:mm:ss");
          }
        })
      } else if(type == 'status') {
        this.selectDevice['status'] = data['status'];
      } else if(type == 'warn') {
        let realtimeItem = realtimeMap[data.field];
        if(realtimeItem != null) {
          realtimeItem['__type'] = 'warn'
        }
      }
    }
  }
  const loadDetail = () => {
    let uid = route.params.uid;
    GET(`/iot/panels/detail?uid=${uid}`).then(({code, message, data}) => {
      if(code == CoreConsts.SuccessCode) {
        realtimeData.value = data['attrs'];
        if(realtimeData.value != null && realtimeData.value.length > 0) {
          realtimeMap.value = {};
          realtimeData.value.forEach(item => {
            item['__type'] = item['status'] == null ? 'default' : (item['status'] === 'Success' ? 'success' : 'error');
            realtimeMap.value[item.field] = item;
          })
        }

        realtimeWarnData.value = data['warns'];
        selectDevice.value = data['device'];
        selectProduct.value = data['product'];
        apiCodeDebugMap.value = data['apiCodeDebugMap'] || {};
        apis = [...selectProduct.value['funcApis'], ...selectProduct.value['eventApis']];
        realtimeSocket = new WebSocket(urlConfig.getFullWsURL(`/ws/iot/realtime?uid=${uid}&type=123`));
        realtimeSocket.onmessage =  realtimeMessage;
        realtimeSocket.onerror = () => {
          setTimeout(() => {
            if(realtimeSocket != null) {
              realtimeSocket = new WebSocket(urlConfig.getFullWsURL(`/ws/iot/realtime?uid=${uid}&type=123`));
            }
          }, 3000)
        }
      } else {
        console.error(message);
      }
    })
  }
  let loadCmds = () => {
    let uid = route.params.uid;
    GET(`/iot/instruct/commands?uid=${uid}`).then(({code, data, message}) => {
      if(code == CoreConsts.SuccessCode) {
        if(data instanceof Array) {
          funcApis.value = data.filter(item => item['apiType'] == 'func');
          eventApis.value = data.filter(item => item['apiType'] == 'event');
        }
      } else {
        msgError(message);
      }
    })
  }
let collectDataOptions = ref({
  title: {
    text: '',
    left: 'center',
    show: true
  },
  dataZoom: {
    type: 'slider',
    id: 'iv-echarts-line',
    show: false
  },
  visualMap: [
    {
      show: false,
      type: 'continuous',
      seriesIndex: 0,
      min: 0,
      max: 20
    }
  ],
  tooltip: {
    // trigger: 'axis',
    axisPointer: {
      // type: 'cross'
    },
    formatter: '',
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
    left: '5%',
    right: '5%',
    bottom: '2%',
    containLabel: true
  },
  xAxis: {
    type: 'category',
    boundaryGap: false,
    data: [],
  },
  yAxis: {
    type: 'value',
    name: '单位: -',
    nameLocation: 'end',
    nameTextStyle: {
      padding: [10, 10, 0, 0]
    }
  },
  series: [
    {
      name: '日期',
      type: 'line',
      data: [],
    }
  ]
});
let warnDataOptions = ref({
  title: {
    text: '',
    left: 'center',
    show: true
  },
  visualMap: [
    {
      show: false,
      type: 'continuous',
      seriesIndex: 0,
      min: 0,
      max: 20
    }
  ],
  tooltip: {
    // trigger: 'axis',
    axisPointer: {
      // type: 'cross'
    },
    formatter: '',
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
    left: '5%',
    right: '5%',
    bottom: '2%',
    containLabel: true
  },
  xAxis: {
    type: 'category',
    boundaryGap: false,
    data: [],
  },
  yAxis: {
    type: 'value',
    name: '单位: 条',
    nameLocation: 'end',
    nameTextStyle: {
      padding: [0, 0, 0, 0]
    }
  },
  series: [
    {
      name: '日期',
      type: 'line',
      data: [],
    }
  ]
});
watch([selectTimeType, selectAttr, selectDevice, selectCountType, selectTime], (newValue, oldValue) => {
  if(selectTimeType.value && selectAttr.value && selectDevice.value) {
    let timeRange = []; let timeDayRange = []
    if(selectTimeType.value === 'cus') {
      if(!selectTime.value || selectTime.value.length != 2) {
        return msgWarn("请选择自定义时间");
      }

      timeDayRange = selectTime.value;
      let startTime = selectTime.value[0].format('YYYY-MM-DD HH:mm:ss');
      let endTime = selectTime.value[1].format('YYYY-MM-DD HH:mm:ss');
      timeRange[0] = startTime; timeRange[1] = endTime;
    } else {
      if(selectTimeType.value == 'week') {
        timeRange = ['now-6', 'now'];
        let now = dayjs();
        timeDayRange = [dayjs().subtract(6, 'day'), now]
      } else if(selectTimeType.value == 'month') {
        timeRange = ['now - 30', 'now'];
        let now = dayjs();
        timeDayRange = [dayjs().subtract(30, 'day'), now]
      } else if(selectTimeType.value == 'current') {
        let now = dayjs();
        let format = now.startOf('month').format('YYYY-MM-DD HH:mm:ss');
        timeRange = [format, 'now']; timeDayRange = [dayjs().startOf('month'), now]
      } else {
        let lastMonth = dayjs().subtract(1, 'month');
        let startDay = lastMonth.startOf('month');
        let startOf = startDay.format('YYYY-MM-DD HH:mm:ss');
        let endDay = lastMonth.endOf('month');
        let endOf = endDay.format('YYYY-MM-DD HH:mm:ss');
        timeRange = [startOf, endOf]; timeDayRange = [startDay, endDay];
      }
    }

    let value = [`D:${selectDevice.value['uid']}:${selectAttr.value['field']}`];
    startTimestamp.value = timeDayRange[0].valueOf();
    endTimestamp.value = timeDayRange[1].valueOf();
    let unit = selectAttr.value.unit || '-';
    collectDataOptions.value.title.text = `[${selectAttr.value.name}]历史数据信息`;
    if(selectCountType.value == 'all') {
      collectDataOptions.value.tooltip.formatter = `{a0}<br />{b0}   &nbsp;&nbsp;&nbsp; {c0} ${unit}`;
      collectDataOptions.value.yAxis.name = `单位: ${unit}`
      spinning.value = true; tipText.value = "数据加载中...";
      POST('/iot/common/value/data', {searchType: 'list', timeType: 'day', timeRange, value})
          .then(({code, message, data}) => {
            if(code == CoreConsts.SuccessCode) {
              if(data instanceof Array) {
                let datum = data[0];
                if(datum) {
                  let xAxis = datum.values.map(item => item.collectTime);
                  let yAxis = datum.values.map(item => item.value);
                  collectDataOptions.value.xAxis.data = xAxis;
                  collectDataOptions.value.series[0].data = yAxis;
                } else {
                  collectDataOptions.value.xAxis.data = [''];
                  collectDataOptions.value.series[0].data = [0];
                }
                collectDataEcharts.setOption(collectDataOptions.value);
              }
            } else {
              msgError(message)
            }
          }).finally(() => spinning.value = false);
    } else {
      if(selectCountType.value == 'count') {
        collectDataOptions.value.yAxis.name = `单位: 条`
        collectDataOptions.value.tooltip.formatter = `{a0}<br />{b0}   &nbsp;&nbsp;&nbsp; {c0}条`;
      } else {
        collectDataOptions.value.yAxis.name = `单位: ${unit}`
        collectDataOptions.value.tooltip.formatter = `{a0}<br />{b0}   &nbsp;&nbsp;&nbsp; {c0} ${unit}`;
      }
      spinning.value = true; tipText.value = "数据加载中...";
      POST('/iot/common/count/data', {calcType: selectCountType.value, timeType: 'day', timeRange, value})
          .then(({code, message, data}) => {
            if(code == CoreConsts.SuccessCode) {
              if(data instanceof Array) {
                let datum = data[0];
                if(datum) {
                  let xAxis = datum.data.map(item => item.category);
                  let yAxis = datum.data.map(item => item.value);
                  collectDataOptions.value.xAxis.data = xAxis;
                  collectDataOptions.value.series[0].data = yAxis;
                } else {
                  collectDataOptions.value.xAxis.data = [''];
                  collectDataOptions.value.series[0].data = [0];
                }
                collectDataEcharts.setOption(collectDataOptions.value);
              }
            } else {
              msgError(message)
            }
          }).finally(() => spinning.value = false);
    }
    if(newValue[0] !== oldValue[0] || newValue[1] !== oldValue[1] || newValue[4] !== oldValue[4]) {
      warnDataOptions.value.title.text = `[${selectAttr.value.name}]告警记录数`;
      POST('/iot/common/count/warn', {calcType: 'count', timeType: 'day', timeRange, value})
            .then(({code, message, data}) => {
              if(code == CoreConsts.SuccessCode) {
                if(data instanceof Array) {
                  let datum = data[0];
                  if(datum) {
                    let xAxis = datum.data.map(item => item.category);
                    let yAxis = datum.data.map(item => item.value);
                    warnDataOptions.value.xAxis.data = xAxis;
                    warnDataOptions.value.series[0].data = yAxis;
                  } else {
                    warnDataOptions.value.xAxis.data = [''];
                    warnDataOptions.value.series[0].data = [0];
                  }

                  warnDataEcharts.setOption(warnDataOptions.value);
                }
              } else {
                msgError(message)
              }
            }).finally(() => spinning.value = false);
      }
  }

  dataPagination.value.onChange(1, 5);
  warnPagination.value.onChange(1, 5);
})
  onMounted(() => {
    loadDetail();
    loadCmds();
    collectDataEcharts = echarts.init(document.getElementById("iv-echarts-line"));
    collectDataEcharts.setOption(collectDataOptions.value);

    warnDataEcharts = echarts.init(document.getElementById("iv-echarts-warn"));
    warnDataEcharts.setOption(warnDataOptions.value);
  })
  onUnmounted(() => {
    if(realtimeSocket != null && !realtimeSocket.CLOSED) {
      let temp = realtimeSocket;
      realtimeSocket = null;
      temp.close(200);
    }
  })
</script>
<style scoped>
.iv-design-header {
  width: 100%;
  height: 58px;
  position: relative;
  background-color: #ffffff;
  background-image: url("/img/iot/大标题背景框6.png");
}
.iv-design-header .iv-header-content {
  top: 0px;
  right: 18px;
  /*width: 88%;*/
  height: 52px;
  position: absolute;
}
.iv-design-header .title {
  top: 7px;
  left: 6%;
  /*color: #76c9fc;*/
  width: 200px;
  height: 42px;
  line-height: 41px;
  font-size: 25px;
  font-weight: 300;
  text-align: center;
  position: absolute;
  transform: skew(-45deg);
  background: linear-gradient(90deg, rgba(61, 98, 171, 0.36) 22.94%, rgba(61, 98, 171, 0.08) 94.62%);
}
.iv-design-header .title span {
  display: inline-block;
  transform: skew(45deg);
}
.iv-header-status {
  position: absolute;
  height: 100%;
  width: 88px;
  left: 50%;
  background-position: center;
  background-size: cover;
  background-image: url("/img/iot/圆形装饰.svg");
}
.iv-design-body {
  height: 100%;
  color: #d9d9d9;
  min-width: 1400px;
  padding: 16px 28px 8px;
}
.iv-content-title {
  height: 28px;
  width: 100px;
  text-align: center;
  font-size: 12px;
  font-weight: 200;
  line-height: 34px;
}
.iv-content-value {
  height: 22px;
  width: 130px;
  font-size: 14px;
  line-height: 20px;
  font-weight: 260;
  padding: 0 12px;
  text-align: center;
  transform: skew(45deg);
  background: linear-gradient(90deg, rgba(61, 98, 171, 0.36) 22.94%, rgba(61, 98, 171, 0.08) 94.62%);
}
.iv-content-value span {
  display: inline-block;
  transform: skew(-45deg);
}
.iv-api-contain-head {
  height: 32px;
  width: 100%;
  padding: 0 22px;
  line-height: 32px;
  font-weight: normal;
  background-size: cover;
  background-image: url('/img/iot/小标题背景2.png');
}
.iv-api-item {
  padding: 6px 0;
  position: relative;
  border-bottom: 1px dashed #d9d9d9;
}
.iv-api-item .iv-cmd-desc, .iv-minor-text {
  font-size: 12px;
  color: #8e8e8e;
}
.iv-api-item .iv-api-exec {
  position: absolute;
  right: 5px;
  top: 11px;
}
.iv-header-status .ant-badge :deep(.ant-badge-status-dot) {
  width: 22px;
  height: 22px;
}
.iv-attr-item {
  min-width: 180px;
  padding: 3px;
  height: 85px;
}
.iv-attr-item .active {
  box-shadow: 0 1px 2px -2px rgba(0, 129, 251, 0.55),0 3px 6px 0 rgba(45, 159, 250, 0.46),0 5px 12px 4px rgba(185, 211, 251, 0.43)
}
</style>