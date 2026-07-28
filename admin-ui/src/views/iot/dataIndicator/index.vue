<template>
  <UView name="数据指标">
    <div class="di-content">
      <!-- 搜索栏 -->
      <UViewSearch v-model="searchModel">
        <URow :responsive="3">
          <USelectItem field="productId" label="产品" url="/iot/product/listByNonGateway"
                       allowClear @change="onProductChange" labelField="name" valueField="id"/>
          <USelectItem field="uid" label="设备" url="/iot/device/listOfProducts?productIds={productId}" labelField="name" valueField="uid" allowClear/>
          <URangePickerItem field="searchTime" label="时间范围" :labelCol="{span: 6}" :wrapperCol="{span: 18}" :presets="timePresets" showTime/>
          <UButton func="query" @click="doSearch">查询</UButton>
          <UButton func="reset" @click="doReset">重置</UButton>
        </URow>
      </UViewSearch>

      <!-- 告警点位列表 -->
      <ACard size="small" class="di-table-card" title="告警点位列表">
        <template #extra>
          <span style="font-size: 12px; color: #999;">点击"分析"查看该告警点位的关联数据</span>
        </template>
        <ATable :columns="alarmColumns" :dataSource="alarmList" :pagination="alarmPagination"
                 :rowKey="(r) => r.id" size="small" bordered :loading="tableLoading"
                 :rowClassName="(r) => selectedAlarm && selectedAlarm.id === r.id ? 'di-row-selected' : ''"
                 @change="onTableChange">
          <template #bodyCell="{column, record}">
            <template v-if="column.dataIndex === 'gradeName'">
              <ATag :color="gradeColor(record.grade)">{{ record.gradeName }}</ATag>
            </template>
            <template v-if="column.key === 'action'">
              <ATag color="#108ee9" style="cursor: pointer" @click="() => onAlarmClick(record)">分析</ATag>
            </template>
          </template>
        </ATable>
      </ACard>

      <!-- 分析区域(全屏抽屉) -->
      <ADrawer v-model:open="analysisVisible" width="100%" placement="right"
               :afterOpenChange="onAnalysisOpenChange" class="di-analysis-drawer">
        <template #title>
          <ASpace>
            <span style="font-weight: bold;">{{ selectedAlarmFieldName }}</span>
            <ATag color="blue">{{ selectedAlarm?.deviceName }}</ATag>
            <span style="font-size: 12px; color: #999;">告警值: {{ selectedAlarm?.value }}</span>
            <span style="font-size: 12px; color: #999;">告警时间: {{ formatTime(selectedAlarm?.warnTime) }}</span>
          </ASpace>
        </template>
        <!-- 聚合统计 -->
        <ARow :gutter="8" class="di-stats-row">
          <ACol :span="4">
            <div class="di-stat-card">
              <div class="di-stat-value">{{ indicatorStats.current != null ? indicatorStats.current : '-' }}</div>
              <div class="di-stat-label">当前值</div>
            </div>
          </ACol>
          <ACol :span="5">
            <div class="di-stat-card">
              <div class="di-stat-value">{{ indicatorStats.max != null ? indicatorStats.max : '-' }}</div>
              <div class="di-stat-label">最大值 ({{ indicatorStats.maxTime ? dayjs(indicatorStats.maxTime).format('MM-DD HH:mm') : '-' }})</div>
            </div>
          </ACol>
          <ACol :span="5">
            <div class="di-stat-card">
              <div class="di-stat-value">{{ indicatorStats.min != null ? indicatorStats.min : '-' }}</div>
              <div class="di-stat-label">最小值 ({{ indicatorStats.minTime ? dayjs(indicatorStats.minTime).format('MM-DD HH:mm') : '-' }})</div>
            </div>
          </ACol>
          <ACol :span="5">
            <div class="di-stat-card">
              <div class="di-stat-value">{{ indicatorStats.avg != null ? Number(indicatorStats.avg).toFixed(2) : '-' }}</div>
              <div class="di-stat-label">平均值</div>
            </div>
          </ACol>
          <ACol :span="5">
            <div class="di-stat-card">
              <div class="di-stat-value">{{ indicatorStats.count != null ? indicatorStats.count : '-' }}</div>
              <div class="di-stat-label">数据量</div>
            </div>
          </ACol>
        </ARow>

        <!-- 历史趋势图 -->
        <ACard size="small" class="di-chart-card">
          <template #title>
            <ASpace>
              <span>历史趋势</span>
              <ASelect v-model:value="analysisRange" style="width: 130px" size="small" @change="onAnalysisRangeChange">
                <ASelectOption value="1h">前后 1 小时</ASelectOption>
                <ASelectOption value="2h">前后 2 小时</ASelectOption>
                <ASelectOption value="6h">前后 6 小时</ASelectOption>
                <ASelectOption value="24h">前后 24 小时</ASelectOption>
              </ASelect>
            </ASpace>
          </template>
          <ASpin :spinning="chartLoading">
            <div id="di-echarts-line" style="height: 280px"></div>
          </ASpin>
        </ACard>

        <!-- 关联点位数据 -->
        <ARow :gutter="8" class="di-assoc-row">
          <ACol :span="8">
            <ACard size="small" class="di-assoc-card" title="同设备其他点位">
              <template #extra>
                <span style="font-size: 12px; color: #999;" v-if="selectedAlarm">采集时间: {{ formatTime(selectedAlarm.warnTime) }}</span>
              </template>
              <div style="height: 260px; overflow-y: auto;">
                <div v-if="sameDevicePoints.length > 0">
                  <div class="iv-od-attr" v-for="item in sameDevicePoints" :key="item.fieldId"
                       @click="() => onAssociatedPointClick(item)">
                    <div class="iv-text-ellipsis iv-od-title">
                      <span style="width: 65%" class="iv-text-ellipsis">{{ item.fieldName || item.field }}</span>
                      <span style="width: 35%; text-align: right; font-size: 12px;" class="iv-text-ellipsis"
                            :class="gradeClass(item.grade)">{{ item.indicator || '' }}</span>
                    </div>
                    <div class="iv-od-content">
                      <AFlex style="height: 38px" justify="space-between">
                        <div style="width: 40%">
                          <div class="iv-text-ellipsis" style="font-weight: bold">{{ item.value || '' }}</div>
                          <div class="iv-od-name">采集值</div>
                        </div>
                        <div style="width: 60%; line-height: 38px">
                          <div class="iv-text-ellipsis">{{ item.deviceGroupName || '' }}</div>
                        </div>
                      </AFlex>
                    </div>
                    <div class="iv-text-ellipsis iv-od-time">{{ item.collectTime }}</div>
                  </div>
                </div>
                <AEmpty v-else description="暂无其他点位数据" />
              </div>
            </ACard>
          </ACol>
          <ACol :span="8">
            <ACard size="small" class="di-assoc-card" title="其他设备同点位">
              <template #extra>
                <span style="font-size: 12px; color: #999;" v-if="selectedAlarm">采集时间: {{ formatTime(selectedAlarm.warnTime) }}</span>
              </template>
              <div style="height: 260px; overflow-y: auto;">
                <div v-if="otherDevicePoints.length > 0">
                  <div class="iv-od-attr" v-for="(item, index) in otherDevicePoints" :key="index"
                       @click="() => onAssociatedPointClick(item)">
                    <div class="iv-text-ellipsis iv-od-title">
                      <a href="javascript:void(0)" style="width: 65%" class="iv-text-ellipsis">{{ item.deviceName }}</a>
                      <span style="width: 35%; text-align: right; font-size: 12px;" class="iv-text-ellipsis"
                            :class="gradeClass(item.grade)">{{ item.indicator || '' }}</span>
                    </div>
                    <div class="iv-od-content">
                      <AFlex style="height: 38px" justify="space-between">
                        <div style="width: 40%">
                          <div class="iv-text-ellipsis" style="font-weight: bold">{{ item.value || '' }}</div>
                          <div class="iv-od-name">采集值</div>
                        </div>
                        <div style="width: 60%; line-height: 38px">
                          <div class="iv-text-ellipsis">{{ item.deviceGroupName || '' }}</div>
                        </div>
                      </AFlex>
                    </div>
                    <div class="iv-text-ellipsis iv-od-time">{{ item.collectTime }}</div>
                  </div>
                </div>
                <AEmpty v-else description="暂无其他设备数据" />
              </div>
            </ACard>
          </ACol>
          <ACol :span="8">
            <ACard size="small" class="di-assoc-card" title="关联维度点位">
              <template #extra>
                <ASelect v-model:value="selectedGrade" placeholder="等级" style="width: 100px" size="small"
                         allowClear @change="onGradeChange">
                  <ASelectOption value="">全部</ASelectOption>
                  <ASelectOption value="fatal">严重</ASelectOption>
                  <ASelectOption value="urgent">紧急</ASelectOption>
                  <ASelectOption value="warn">告警</ASelectOption>
                  <ASelectOption value="notice">通知</ASelectOption>
                  <ASelectOption value="normal">正常</ASelectOption>
                  <ASelectOption value="indicator">指标</ASelectOption>
                </ASelect>
              </template>
              <div style="height: 260px; overflow-y: auto;">
                <div v-if="associatedStatsList.length > 0">
                  <div class="iv-od-attr" v-for="item in associatedStatsList" :key="item.fieldId"
                       @click="() => onAssociatedPointClick(item)">
                    <div class="iv-text-ellipsis iv-od-title">
                      <span style="width: 65%" class="iv-text-ellipsis">{{ item.deviceName }}/{{ item.fieldName || item.field }}</span>
                      <span style="width: 35%; text-align: right; font-size: 12px;" class="iv-text-ellipsis"
                            :class="gradeClass(item.grade)">{{ item.indicator || '' }}</span>
                    </div>
                    <div class="iv-od-content">
                      <AFlex style="height: 38px" justify="space-between">
                        <div style="width: 40%">
                          <div class="iv-text-ellipsis" style="font-weight: bold">{{ item.value || '' }}</div>
                          <div class="iv-od-name">采集值</div>
                        </div>
                        <div style="width: 60%; line-height: 38px">
                          <div class="iv-text-ellipsis">{{ item.deviceGroupName || '' }}</div>
                        </div>
                      </AFlex>
                    </div>
                    <div class="iv-text-ellipsis iv-od-time">{{ item.collectTime }}</div>
                  </div>
                </div>
                <AEmpty v-else description="暂无关联点位数据" />
              </div>
            </ACard>
          </ACol>
        </ARow>
      </ADrawer>
    </div>
  </UView>
</template>
<!-- 数据指标分析 -->
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
import {ref, shallowRef, nextTick} from "vue";
import CoreConsts from "@/components/CoreConsts";
import {GET} from "@/utils/request";
import {msgError, msgWarn} from "@/utils/message";
import dayjs from "dayjs";

echarts.use([CanvasRenderer, TitleComponent, GridComponent, VisualMapComponent, LineChart
  , UniversalTransition, TooltipComponent, LegendComponent, DatasetComponent, ToolboxComponent, DataZoomComponent])

export default {
  name: "DataIndicator",
  components: {},
  setup() {
    // ============ 搜索条件 ============
    // UViewSearch搜索模型: productId-产品id uid-设备标识 searchTime-告警时间范围
    let searchModel = ref({productId: null, uid: null, searchTime: [dayjs().subtract(7, 'day'), dayjs()]});
    // fieldName -> {id, name, field}
    let attrFieldMap = ref({});
    let selectedFieldId = ref(null);

    // ============ 告警列表 ============
    let alarmList = ref([]);
    let tableLoading = ref(false);
    let alarmPagination = ref({current: 1, pageSize: 20, total: 0, showSizeChanger: false});

    // ============ 选中的告警 ============
    let selectedAlarm = ref(null);
    let selectedAlarmFieldName = ref('');
    let analysisVisible = ref(false);
    let analysisRange = ref('1h');

    // ============ 分析数据 ============
    let indicatorStats = ref({});
    let chartLoading = ref(false);
    let trendData = ref([]);
    let sameDevicePoints = ref([]);
    let otherDevicePoints = ref([]);
    let associatedStatsList = ref([]);
    let selectedGrade = ref('');
    let collectDataEcharts = shallowRef(null);

    // ============ 表格列定义 ============
    let alarmColumns = ref([
      {title: '设备名称', dataIndex: 'deviceName', width: 150, ellipsis: true},
      {title: '告警名称', dataIndex: 'warnConfigName', width: 150, ellipsis: true},
      {title: '属性', dataIndex: 'field', width: 100},
      {title: '告警值', dataIndex: 'value', width: 100},
      {title: '等级', dataIndex: 'gradeName', width: 80},
      {title: '告警时间', dataIndex: 'warnTime', width: 160},
      {title: '操作', key: 'action', width: 80, fixed: 'right'}
    ]);

    // ============ 时间预设 ============
    let timePresets = ref([
      {label: '今天', value: [dayjs().startOf('day'), dayjs().endOf('day')]},
      {label: '近7天', value: [dayjs().subtract(7, 'day'), dayjs()]},
      {label: '近30天', value: [dayjs().subtract(30, 'day'), dayjs()]},
      {label: '当月', value: [dayjs().startOf('month'), dayjs().endOf('month')]},
    ]);

    return {
      searchModel,
      attrFieldMap, selectedFieldId,
      alarmList, tableLoading, alarmPagination,
      selectedAlarm, selectedAlarmFieldName, analysisVisible, analysisRange,
      indicatorStats, chartLoading, trendData, sameDevicePoints, otherDevicePoints,
      associatedStatsList, selectedGrade, collectDataEcharts,
      alarmColumns, timePresets,
      dayjs
    }
  },
  methods: {
    // ============ 搜索事件 ============
    onProductChange(value) {
      // uid由级联url({productId})变化后自动重置, 这里只需清理属性映射
      this.attrFieldMap = {};
      this.selectedFieldId = null;
      if (value) {
        this.loadModelAttrs(value);
      }
    },

    loadModelAttrs(productId) {
      this.$http.get('/iot/modelAttr/listByProductId', {params: {productId}}).then(({code, data}) => {
        if (code === CoreConsts.SuccessCode) {
          let map = {};
          if (data instanceof Array) {
            data.forEach(item => {
              map[item['field']] = {id: item['id'], name: item['name'], field: item['field']};
            });
          }
          this.attrFieldMap = map;
        }
      });
    },

    doSearch() {
      let params = {current: this.alarmPagination.current, size: this.alarmPagination.pageSize};
      let model = this.searchModel;
      if (model.productId) params['productId'] = model.productId;
      if (model.uid) params['uid'] = model.uid;
      if (model.searchTime && model.searchTime.length === 2) {
        params['startTimestamp'] = model.searchTime[0].valueOf();
        params['endTimestamp'] = model.searchTime[1].valueOf();
      }

      this.tableLoading = true;
      GET('/iot/warnRecord/view', params).then(({code, message, data}) => {
        if (code === CoreConsts.SuccessCode) {
          this.alarmList = data['records'] || [];
          this.alarmPagination = {
            ...this.alarmPagination,
            total: data['total'] || 0,
            current: data['current'] || 1,
            pageSize: data['size'] || 20
          };
        } else {
          msgError(message);
        }
      }).finally(() => {
        this.tableLoading = false;
      });
    },

    doReset() {
      // 就地修改字段, 保持UViewSearch表单模型引用不变
      this.searchModel.productId = null;
      this.searchModel.uid = null;
      this.searchModel.searchTime = [dayjs().subtract(7, 'day'), dayjs()];
      this.attrFieldMap = {};
      this.selectedFieldId = null;
      this.analysisVisible = false;
      this.alarmList = [];
      this.alarmPagination.current = 1;
    },

    onTableChange(pagination) {
      this.alarmPagination.current = pagination.current;
      this.alarmPagination.pageSize = pagination.pageSize;
      this.doSearch();
    },

    // ============ 告警点击分析 ============
    onAlarmClick(record) {
      this.selectedAlarm = record;
      let fieldName = record['field'];
      let attrInfo = this.attrFieldMap[fieldName];
      if (attrInfo) {
        this.selectedFieldId = attrInfo.id;
        this.selectedAlarmFieldName = attrInfo.name;
      } else {
        this.selectedFieldId = null;
        this.selectedAlarmFieldName = fieldName;
      }
      this.analysisVisible = true;
      this.selectedGrade = '';
      this.$nextTick(() => {
        this.loadAnalysisData();
      });
    },

    onAnalysisRangeChange() {
      this.loadAnalysisData();
    },

    // 抽屉打开动画结束后重置图表尺寸, 避免动画期间初始化宽度异常
    onAnalysisOpenChange(open) {
      if (open && this.collectDataEcharts) {
        this.collectDataEcharts.resize();
      }
    },

    loadAnalysisData() {
      let record = this.selectedAlarm;
      if (!record) return;
      let uid = record['uid'];
      let fieldId = this.selectedFieldId;
      if (!fieldId) {
        msgWarn('未找到属性映射，请先选择产品');
        return;
      }
      let warnTime = record['warnTime'];
      if (!warnTime) return;

      let warnTimeDayjs = dayjs(warnTime);
      let range = this.analysisRange;
      let rangeNum = parseInt(range);
      let startTime = warnTimeDayjs.subtract(rangeNum, 'hour').format('YYYY-MM-DD HH:mm:ss');
      let endTime = warnTimeDayjs.add(rangeNum, 'hour').format('YYYY-MM-DD HH:mm:ss');
      let collectTime = warnTimeDayjs.format('YYYY-MM-DD HH:mm:ss');
      let productCode = record['productCode'];

      this.loadStats(uid, fieldId, startTime, endTime);
      this.loadTrendData(uid, fieldId, startTime, endTime);
      this.loadSameDevicePoints(uid, fieldId, collectTime);
      if (productCode) {
        this.loadOtherDevicePoints(productCode, fieldId, collectTime, uid);
      }
      this.loadAssociatedStats(uid, fieldId, collectTime);
    },

    loadStats(uid, fieldId, start, end) {
      GET('/iot/dataIndicator/stats', {uid, fieldId, start, end}).then(({code, data}) => {
        if (code === CoreConsts.SuccessCode) {
          this.indicatorStats = data || {};
        }
      });
    },

    loadTrendData(uid, fieldId, start, end) {
      this.chartLoading = true;
      GET('/iot/dataIndicator/listData', {uid, fieldId, start, end}).then(({code, data}) => {
        if (code === CoreConsts.SuccessCode) {
          this.trendData = data || [];
          this.renderChart();
        }
      }).finally(() => {
        this.chartLoading = false;
      });
    },

    renderChart() {
      if (!this.collectDataEcharts) {
        this.collectDataEcharts = echarts.init(document.getElementById("di-echarts-line"));
      }
      let dataList = this.trendData;
      let xAxis = dataList.map(item => item['collectTime'] ? dayjs(item['collectTime']).format('MM-DD HH:mm') : '');
      let yAxis = dataList.map(item => item['value'] != null ? parseFloat(item['value']) : 0);
      let option = {
        title: {text: '', left: 'center', show: false},
        dataZoom: {type: 'slider', show: true, bottom: '12px'},
        tooltip: {trigger: 'axis'},
        toolbox: {feature: {saveAsImage: {show: true}}},
        grid: {top: '8%', left: '4%', right: '3%', bottom: '45px', containLabel: true},
        xAxis: {type: 'category', boundaryGap: false, data: xAxis},
        yAxis: {type: 'value', nameTextStyle: {padding: [0, 0, 10, -40]}},
        series: [{type: 'line', data: yAxis, smooth: true, areaStyle: {opacity: 0.15}}]
      };
      this.collectDataEcharts.setOption(option);
    },

    loadSameDevicePoints(uid, excludeFieldId, collectTime) {
      GET('/iot/dataIndicator/associatedPoints', {uid, excludeFieldId, collectTime}).then(({code, data}) => {
        if (code === CoreConsts.SuccessCode) {
          this.sameDevicePoints = data || [];
        }
      });
    },

    loadOtherDevicePoints(productCode, fieldId, collectTime, excludeUid) {
      GET('/iot/dataIndicator/crossDevicePoints', {productCode, fieldId, collectTime, excludeUid}).then(({code, data}) => {
        if (code === CoreConsts.SuccessCode) {
          this.otherDevicePoints = data || [];
        }
      });
    },

    loadAssociatedStats(uid, fieldId, collectTime) {
      let params = {uid, fieldId, collectTime};
      if (this.selectedGrade) params['grade'] = this.selectedGrade;
      GET('/iot/dataIndicator/associatedStats', params).then(({code, data}) => {
        if (code === CoreConsts.SuccessCode) {
          this.associatedStatsList = data || [];
        }
      });
    },

    onGradeChange() {
      let record = this.selectedAlarm;
      if (!record) return;
      let uid = record['uid'];
      let fieldId = this.selectedFieldId;
      let collectTime = dayjs(record['warnTime']).format('YYYY-MM-DD HH:mm:ss');
      this.loadAssociatedStats(uid, fieldId, collectTime);
    },

    // ============ 工具方法 ============
    gradeColor(grade) {
      let map = {fatal: 'red', urgent: 'magenta', warn: 'orange', notice: 'cyan', normal: 'green', indicator: 'purple'};
      return map[grade] || '#108ee9';
    },

    gradeClass(grade) {
      return 'iv-grade-' + (grade || 'void');
    },

    formatTime(time) {
      return time ? dayjs(time).format('YYYY-MM-DD HH:mm:ss') : '-';
    },

    onAssociatedPointClick(item) {
      if (item['uid']) {
        window.open(`#/iot/device/detail/${item['uid']}`, '_blank');
      }
    }
  }
}
</script>
<style scoped>
.di-content {
  padding: 10px;
}
.di-table-card {
  margin-bottom: 8px;
}
.di-row-selected {
  background-color: #e6f4ff !important;
}
.di-stats-row {
  margin-bottom: 8px;
}
.di-stat-card {
  background: #fafafa;
  border: 1px solid #e8e8e8;
  border-radius: 6px;
  padding: 12px;
  text-align: center;
}
.di-stat-value {
  font-size: 24px;
  font-weight: bold;
  color: #1677ff;
  line-height: 1.4;
}
.di-stat-label {
  font-size: 12px;
  color: #8e8e8e;
  margin-top: 4px;
}
.di-chart-card {
  margin-bottom: 8px;
}
.di-assoc-row {
  margin-bottom: 8px;
}
.iv-od-attr {
  width: 100%;
  margin: 4px 0;
  padding: 0 8px;
  font-size: 12px;
  border: 1px solid #d9d9d9;
  border-radius: 8px;
  cursor: pointer;
  background: #fff;
}
.iv-od-attr:hover {
  border-color: #1677ff;
}
.iv-od-attr .iv-od-content {
  text-align: center;
  border-top: 1px dashed #d9d9d9;
  border-bottom: 1px dashed #d9d9d9;
}
.iv-od-name {
  font-size: 12px;
  color: #8e8e8e;
}
.iv-od-time, .iv-od-title {
  height: 24px;
  line-height: 24px;
  color: #8e8e8e;
  display: flex;
}
.iv-text-ellipsis {
  overflow: hidden;
  white-space: nowrap;
  text-overflow: ellipsis;
}
.iv-grade-fatal { color: #ff4d4f; }
.iv-grade-urgent { color: #eb2f96; }
.iv-grade-warn { color: #fa8c16; }
.iv-grade-notice { color: #13c2c2; }
.iv-grade-normal { color: #52c41a; }
.iv-grade-indicator { color: #722ed1; }
.iv-grade-void { color: #8e8e8e; }
</style>
