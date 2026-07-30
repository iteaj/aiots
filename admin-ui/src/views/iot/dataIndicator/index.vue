<template>
  <UView name="数据指标">
    <div class="di-content">
      <!-- 搜索栏 -->
      <UViewSearch v-model="searchModel">
        <URow :responsive="3">
          <USelectItem field="productId" label="产品" url="/iot/product/listByNonGateway"
                       allowClear labelField="name" valueField="id"/>
          <USelectItem field="fieldId" label="模型属性" url="/iot/modelAttr/listByProductId?productId={productId}" labelField="name" valueField="id" allowClear/>
          <USelectItem field="uid" label="设备" url="/iot/device/listOfProducts?productIds={productId}" labelField="name" valueField="uid" allowClear/>
          <URangePickerItem field="searchTime" label="时间范围" :labelCol="{span: 4}" :wrapperCol="{span: 20}" :presets="timePresets" showTime/>
          <UButton func="query" @click="doSearch">查询</UButton>
          <UButton func="reset" @click="doReset">重置</UButton>
        </URow>
      </UViewSearch>

      <!-- 采集点位卡片列表 -->
      <ACard size="small" class="di-table-card" title="告警点位">
        <template #extra>
          <span style="font-size: 12px; color: #999;">点击卡片查看该点位的关联分析(用于查看指定告警点位发生时其他点位的趋势信息)</span>
        </template>
        <ASpin :spinning="listLoading">
          <DeviceIndicatorCard v-if="cardList.length > 0" :data-source="cardList" @attr-click="onCardClick" />
          <AEmpty v-else description="暂无采集数据" />
          <AFlex justify="flex-end" style="margin-top: 8px; text-align: center;">
            <APagination v-model:current="pagination.current" :total="pagination.total" :pageSize="pagination.pageSize"
                         :showSizeChanger="false" :showTotal="(total) => `共 ${total} 条`" size="small" @change="onPageChange"/>
          </AFlex>
        </ASpin>
      </ACard>

      <!-- 分析区域(全屏抽屉) -->
      <ADrawer v-model:open="analysisVisible" width="100%" placement="right"
               :afterOpenChange="onAnalysisOpenChange" class="di-analysis-drawer">
        <template #title>
          <AFlex justify="space-between" align="center" style="padding-right: 16px">
            <ASpace>
              <span style="font-weight: bold;">{{ selectedPoint?.name || selectedPoint?.field }}</span>
              <ATag color="blue">{{ selectedPoint?.deviceName }}</ATag>
              <span style="font-size: 12px; color: #999;">采集值: {{ selectedPoint?.value }}</span>
              <span style="font-size: 12px; color: #999;">采集时间: {{ formatTime(selectedPoint?.collectTime) }}</span>
            </ASpace>
            <!-- 聚合统计 -->
            <ASpace :size="16">
              <span class="di-header-stat">当前值: <b>{{ indicatorStats.current != null ? indicatorStats.current : '-' }}</b></span>
              <span class="di-header-stat">最大值: <b>{{ indicatorStats.max != null ? indicatorStats.max : '-' }}</b> ({{ indicatorStats.maxTime ? dayjs(indicatorStats.maxTime).format('MM-DD HH:mm') : '-' }})</span>
              <span class="di-header-stat">最小值: <b>{{ indicatorStats.min != null ? indicatorStats.min : '-' }}</b> ({{ indicatorStats.minTime ? dayjs(indicatorStats.minTime).format('MM-DD HH:mm') : '-' }})</span>
              <span class="di-header-stat">平均值: <b>{{ indicatorStats.avg != null ? Number(indicatorStats.avg).toFixed(2) : '-' }}</b></span>
              <span class="di-header-stat">数据量: <b>{{ indicatorStats.count != null ? indicatorStats.count : '-' }}</b></span>
            </ASpace>
          </AFlex>
        </template>

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
        <ARow :gutter="[8, 8]" class="di-assoc-row" wrap="wrap" justify="start">
          <ACol :xxxl="8" :xxl="8" :xl="12" :xs="24" class="di-assoc-col">
            <ACard size="small" class="di-assoc-card" title="同设备同时采集的其他点位">
              <template #extra>
                <span style="font-size: 12px; color: #999;" v-if="selectedPoint">采集时间: {{ formatTime(selectedPoint.collectTime) }}</span>
              </template>
              <div style="height: 260px; overflow-y: auto;">
                <ATable size="small" rowKey="id" :columns="assocPointColumns" :data-source="sameDevicePoints"
                        :pagination="false" :scroll="{y: 215}" :customRow="assocCustomRow">
                  <template #bodyCell="{column, record}">
                    <template v-if="column.dataIndex === 'indicator'">
                      <span :class="gradeClass(record.grade)">{{ record.indicator || '-' }}</span>
                    </template>
                  </template>
                </ATable>
              </div>
            </ACard>
          </ACol>
          <ACol :xxxl="8" :xxl="8" :xl="12" :xs="24" class="di-assoc-col">
            <ACard size="small" class="di-assoc-card" title="同产品下其他设备点位">
              <template #extra>
                <span style="font-size: 12px; color: #999;" v-if="selectedPoint">采集时间: {{ formatTime(selectedPoint.collectTime) }}</span>
              </template>
              <div style="height: 260px; overflow-y: auto;">
                <ATable size="small" rowKey="id" :columns="crossDeviceColumns" :data-source="otherDevicePoints"
                        :pagination="false" :scroll="{y: 215}" :customRow="assocCustomRow">
                  <template #bodyCell="{column, record}">
                    <template v-if="column.dataIndex === 'indicator'">
                      <span :class="gradeClass(record.grade)">{{ record.indicator || '-' }}</span>
                    </template>
                  </template>
                </ATable>
              </div>
            </ACard>
          </ACol>
          <ACol :xxxl="8" :xxl="8" :xl="12" :xs="24" class="di-assoc-col">
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
                <ATable size="small" rowKey="id" :columns="assocStatsColumns" :data-source="associatedStatsList"
                        :pagination="false" :scroll="{y: 215}" :customRow="assocCustomRow">
                  <template #bodyCell="{column, record}">
                    <template v-if="column.dataIndex === 'deviceName'">
                      {{ record.deviceName }}/{{ record.name }}
                    </template>
                    <template v-else-if="column.dataIndex === 'indicator'">
                      <span :class="gradeClass(record.grade)">{{ record.indicator || '-' }}</span>
                    </template>
                  </template>
                </ATable>
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
import IndicatorCard from "@/components/iot/IndicatorCard.vue";
import {GET} from "@/utils/request";
import {msgError, msgWarn} from "@/utils/message";
import dayjs from "dayjs";
import DeviceIndicatorCard from "@/components/iot/DeviceIndicatorCard.vue";

echarts.use([CanvasRenderer, TitleComponent, GridComponent, VisualMapComponent, LineChart
  , UniversalTransition, TooltipComponent, LegendComponent, DatasetComponent, ToolboxComponent, DataZoomComponent])

export default {
  name: "DataIndicator",
  components: {DeviceIndicatorCard, IndicatorCard},
  setup() {
    // ============ 搜索条件 ============
    // UViewSearch搜索模型: productId-产品id uid-设备标识 searchTime-采集时间范围(默认当日0点至当前时间)
    let searchModel = ref({productId: null, uid: null, fieldId: null, searchTime: [dayjs().startOf('day'), dayjs()]});

    // ============ 采集点位卡片列表 ============
    let cardList = ref([]);
    let listLoading = ref(false);
    let pagination = ref({current: 1, pageSize: 20, total: 0});

    // ============ 选中的采集点位 ============
    let selectedPoint = ref(null);
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

    // ============ 关联点位表格列 ============
    // 同设备其他点位
    let assocPointColumns = [
      {title: '属性', dataIndex: 'name', width: 120, ellipsis: true, align: 'center'},
      {title: '采集值', dataIndex: 'value', width: 120, ellipsis: true, align: 'center'},
      {title: '指标', dataIndex: 'indicator', width: 120, ellipsis: true, align: 'center'},
      // {title: '采集时间', dataIndex: 'collectTime', width: 150},
    ];
    // 其他设备同点位
    let crossDeviceColumns = [
      {title: '设备', dataIndex: 'deviceName', ellipsis: true},
      {title: '采集值', dataIndex: 'value', width: 80, ellipsis: true},
      {title: '指标', dataIndex: 'indicator', width: 80, ellipsis: true},
      {title: '采集时间', dataIndex: 'collectTime', width: 150},
    ];
    // 关联维度点位
    let assocStatsColumns = [
      {title: '设备/属性', dataIndex: 'deviceName', ellipsis: true},
      {title: '采集值', dataIndex: 'value', width: 80, ellipsis: true},
      {title: '指标', dataIndex: 'indicator', width: 80, ellipsis: true},
      {title: '采集时间', dataIndex: 'collectTime', width: 150},
    ];

    // ============ 时间预设 ============
    let timePresets = ref([
      {label: '今天', value: [dayjs().startOf('day'), dayjs().endOf('day')]},
      {label: '近3天', value: [dayjs().subtract(3, 'day'), dayjs()]},
      {label: '近7天', value: [dayjs().subtract(7, 'day'), dayjs()]},
      // {label: '当月', value: [dayjs().startOf('month'), dayjs().endOf('month')]},
    ]);

    return {
      searchModel,
      cardList, listLoading, pagination,
      selectedPoint, analysisVisible, analysisRange,
      indicatorStats, chartLoading, trendData, sameDevicePoints, otherDevicePoints,
      associatedStatsList, selectedGrade, collectDataEcharts,
      assocPointColumns, crossDeviceColumns, assocStatsColumns,
      timePresets,
      dayjs
    }
  },
  methods: {
    // ============ 搜索事件 ============
    doSearch() {
      let params = {current: this.pagination.current, size: this.pagination.pageSize};
      let model = this.searchModel;
      params['uid'] = model.uid;
      params['fieldId'] = model.fieldId;
      params['productId'] = model.productId;
      if (model.searchTime && model.searchTime.length === 2) {
        params['startTimestamp'] = model.searchTime[0].valueOf();
        params['endTimestamp'] = model.searchTime[1].valueOf();
      } else {
        return this.$msg.error('请选择时间范围');
      }

      this.listLoading = true;
      GET('/iot/dataIndicator/view', params).then(({code, message, data}) => {
        if (code === CoreConsts.SuccessCode) {
          this.cardList = data['records'] || [];
          this.pagination = {
            ...this.pagination,
            total: data['total'] || 0,
            current: data['current'] || 1,
            pageSize: data['size'] || 20
          };
        } else {
          msgError(message);
        }
      }).finally(() => {
        this.listLoading = false;
      });
    },

    doReset() {
      // 就地修改字段, 保持UViewSearch表单模型引用不变
      this.searchModel.productId = null;
      this.searchModel.uid = null;
      this.searchModel.searchTime = [dayjs().startOf('day'), dayjs()];
      this.analysisVisible = false;
      this.cardList = [];
      this.pagination.current = 1;
    },

    onPageChange(current, pageSize) {
      this.pagination.current = current;
      this.pagination.pageSize = pageSize;
      this.doSearch();
    },

    // ============ 卡片点击分析 ============
    // IndicatorCard组件点击事件, attr为null表示取消选中
    onCardClick(attr) {
      if (!attr) return;
      this.selectedPoint = attr;
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
      let record = this.selectedPoint;
      if (!record) return;
      let uid = record['uid'];
      let fieldId = record['fieldId'];
      if (!fieldId) {
        msgWarn('该采集记录缺少属性ID, 无法分析');
        return;
      }
      if (!record['collectTime']) return;

      let collectTimeDayjs = dayjs(record['collectTime']);
      let range = this.analysisRange;
      let rangeNum = parseInt(range);
      let startTime = collectTimeDayjs.subtract(rangeNum, 'hour').format('YYYY-MM-DD HH:mm:ss');
      let endTime = collectTimeDayjs.add(rangeNum, 'hour').format('YYYY-MM-DD HH:mm:ss');
      let collectTime = collectTimeDayjs.format('YYYY-MM-DD HH:mm:ss');
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
          // 补充name字段供DeviceIndicatorCard展示属性名称
          this.sameDevicePoints = (data || []).map(item => ({...item, name: item.fieldName || item.field}));
        }
      });
    },

    loadOtherDevicePoints(productCode, fieldId, collectTime, excludeUid) {
      GET('/iot/dataIndicator/crossDevicePoints', {productCode, fieldId, collectTime, excludeUid}).then(({code, data}) => {
        if (code === CoreConsts.SuccessCode) {
          this.otherDevicePoints = (data || []).map(item => ({...item, name: item.fieldName || item.field}));
        }
      });
    },

    loadAssociatedStats(uid, fieldId, collectTime) {
      let params = {uid, fieldId, collectTime};
      if (this.selectedGrade) params['grade'] = this.selectedGrade;
      GET('/iot/dataIndicator/associatedStats', params).then(({code, data}) => {
        if (code === CoreConsts.SuccessCode) {
          this.associatedStatsList = (data || []).map(item => ({...item, name: item.fieldName || item.field}));
        }
      });
    },

    onGradeChange() {
      let record = this.selectedPoint;
      if (!record) return;
      let uid = record['uid'];
      let fieldId = record['fieldId'];
      let collectTime = dayjs(record['collectTime']).format('YYYY-MM-DD HH:mm:ss');
      this.loadAssociatedStats(uid, fieldId, collectTime);
    },

    // ============ 工具方法 ============
    // 指标列按告警等级着色
    gradeClass(grade) {
      return 'iv-grade-' + (grade || 'void');
    },

    formatTime(time) {
      return time ? dayjs(time).format('YYYY-MM-DD HH:mm:ss') : '-';
    },

    // 关联点位表格行点击, 跳转设备详情
    assocCustomRow(record) {
      return {
        style: {cursor: 'pointer'},
        onClick: () => this.onAssociatedPointClick(record)
      };
    },

    onAssociatedPointClick(item) {
      if (item && item['uid']) {
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
/* 抽屉头聚合统计 */
.di-header-stat {
  font-size: 12px;
  color: #8e8e8e;
  font-weight: normal;
}
.di-header-stat b {
  font-size: 14px;
  color: #1677ff;
}
.di-chart-card {
  margin-bottom: 8px;
}
.di-assoc-row {
  margin-bottom: 8px;
}
/* 指标列告警等级颜色 */
.iv-grade-fatal { color: #ff4d4f; }
.iv-grade-urgent { color: #eb2f96; }
.iv-grade-warn { color: #fa8c16; }
.iv-grade-notice { color: #13c2c2; }
.iv-grade-normal { color: #52c41a; }
.iv-grade-indicator { color: #722ed1; }
.iv-grade-void { color: #8e8e8e; }
</style>
