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
      <ADrawer v-model:open="analysisVisible" width="100%" placement="right" :styles="{body: {padding: '8px'}}"
               :afterOpenChange="onAnalysisOpenChange" class="di-analysis-drawer">
        <template #title>
          <AFlex justify="space-between" align="center" style="padding-right: 16px">
            <ASpace>
              <span style="font-weight: bold;">{{ selectedPoint?.name || selectedPoint?.field }}</span>
              <ATag color="blue">{{ selectedPoint?.deviceName }}</ATag>
              <span style="font-size: 12px; color: #999;">采集值: <ATag>{{ selectedPoint?.value }}</ATag></span>
              <ATag v-if="selectedPoint?.indicator" :color="gradeTagColor(selectedPoint?.grade)">{{ selectedPoint?.indicator }}</ATag>
            </ASpace>
            <!-- 分析时间范围 -->
            <ARangePicker v-model:value="analysisTime" size="small" showTime :allowClear="false"
                          :presets="analysisPresets" style="width: 340px; font-weight: normal;" @change="onAnalysisTimeChange"/>
            <!-- 采集地址和采集时间 -->
            <ASpace :size="16" style="font-weight: normal; font-size: 12px; color: #999;">
              <span>采集时间: <ATag>{{ formatTime(selectedPoint?.collectTime) }}</ATag></span>
              <span>设备分组: <ATag>{{ selectedPoint?.deviceGroupName || '-' }}</ATag></span>
              <span>点位地址: <ATag>{{ selectedPoint?.address || '-' }}</ATag></span>
            </ASpace>
          </AFlex>
        </template>

        <!-- 当前设备同一时间采集的其他点位(精简卡片一行横向滚动, 点击弹出底部统计抽屉) -->
        <div class="di-same-device-row" v-if="sameDevicePoints.length > 0">
          <div class="di-point-card" v-for="pt in sameDevicePoints" :key="pt.id" @click="onSameDevicePointClick(pt)">
            <a-badge-ribbon :text="pt.indicator || ''" :color="gradeTagColor(pt.grade)">
              <a-card variant="borderless" hoverable size="small" :styles="{body: {padding: '8px'}}">
                <div class="iv-text-ellipsis" style="width: 70px; text-align: center;">
                  <div class="di-point-value">
                    {{ pt.value != null ? pt.value : '-' }}
                    <span class="di-point-unit" v-if="pt.unit">({{ pt.unit }})</span>
                  </div>
                  <div class="di-point-name">{{ pt.name || pt.field || '-' }}</div>
                </div>
              </a-card>
            </a-badge-ribbon>
          </div>
        </div>

        <!-- 历史趋势图 -->
        <ACard size="small" class="di-chart-card" title="历史趋势">
          <template #extra>
            <!-- 聚合统计 -->
            <ASpace :size="16">
<!--              <span class="di-header-stat">当前值: <b>{{ indicatorStats.current != null ? indicatorStats.current : '-' }}</b></span>-->
              <span class="di-header-stat">最大值: <b>{{ indicatorStats.max != null ? indicatorStats.max : '-' }}</b> ({{ indicatorStats.maxTime ? dayjs(indicatorStats.maxTime).format('MM-DD HH:mm') : '-' }})</span>
              <span class="di-header-stat">最小值: <b>{{ indicatorStats.min != null ? indicatorStats.min : '-' }}</b> ({{ indicatorStats.minTime ? dayjs(indicatorStats.minTime).format('MM-DD HH:mm') : '-' }})</span>
              <span class="di-header-stat">平均值: <b>{{ indicatorStats.avg != null ? Number(indicatorStats.avg).toFixed(2) : '-' }}</b></span>
              <span class="di-header-stat">数据量: <b>{{ indicatorStats.count != null ? indicatorStats.count : '-' }}</b></span>
            </ASpace>
          </template>
          <ASpin :spinning="chartLoading">
            <div id="di-echarts-line" style="height: 280px"></div>
          </ASpin>
        </ACard>

        <!-- 关联点位数据 -->
        <ARow :gutter="[8, 8]" class="di-assoc-row" wrap="wrap" justify="start">
          <ACol span="10" class="di-assoc-col">
            <div style="height: 260px; overflow-y: auto;">
              <ATable size="small" rowKey="id" :columns="crossDeviceColumns" :data-source="otherDevicePoints"
                      :pagination="false" :bordered="true" :scroll="{y: 215}" :customRow="assocCustomRow">
                <template #title>
                  <AFlex justify="space-between" align="center">
                    <div>相同产品其他设备同一时间采集的<ATag color="blue">{{ selectedPoint?.name }}值</ATag>列表</div>
                    <div style="font-size: 12px; color: #999;" v-if="selectedPoint">
                      采集时间: <ATag>{{ formatTime(selectedPoint.collectTime) }}</ATag>
                    </div>
                  </AFlex>
                </template>
                <template #bodyCell="{column, record}">
                  <template v-if="column.dataIndex === 'indicator'">
                    <span :class="gradeClass(record.grade)">{{ record.indicator || '-' }}</span>
                  </template>
                  <template v-else-if="column.dataIndex === 'action'">
                    <AButton type="link" size="small" @click.stop="onSameDevicePointClick(record)">统计</AButton>
                  </template>
                </template>
              </ATable>
            </div>
          </ACol>
          <ACol span="14" class="di-assoc-col">
            <div style="height: 260px; overflow-y: auto;">
              <ATable size="small" rowKey="id" :columns="assocStatsColumns" :data-source="associatedStatsList" :loading="assocLoading"
                      :pagination="false" :bordered="true" :scroll="{y: 215}" :customRow="assocCustomRow">
                <template #title>
                  <AFlex justify="space-between" align="center">
                    <div>关联维度(<ATag color="blue">同采集器</ATag>、<ATag color="blue">同网关</ATag>、<ATag color="blue">同设备分组</ATag>)</div>
                    <div style="font-size: 12px; color: #999;" v-if="selectedPoint">
                      <span style="margin-right: 4px;">关联时间:</span>
                      <ASelect v-model:value="assocOffset" :options="assocOffsetOptions" style="width: 100px; margin-right: 8px;"
                               size="small" @change="onOffsetChange" />
                      <span style="margin-right: 4px;">告警等级:</span>
                      <ASelect v-model:value="selectedGrade" :options="gradeOptions" placeholder="等级" style="width: 100px"
                               size="small" allowClear @change="onGradeChange" />
                    </div>
                  </AFlex>
                </template>
                <template #bodyCell="{column, record}">
                  <template v-if="column.dataIndex === 'action'">
                    <AButton type="link" size="small" @click.stop="onSameDevicePointClick(record)">统计</AButton>
                  </template>
                </template>
              </ATable>
            </div>
          </ACol>
        </ARow>

        <!-- 同设备点位统计抽屉(从下往上弹出) -->
        <ADrawer v-model:open="subVisible" placement="bottom" height="68%" :styles="{body: {padding: '8px'}}"
                 :afterOpenChange="onSubOpenChange">
          <template #title>
            <ASpace>
              <span style="font-weight: bold;">{{ subPoint?.name || subPoint?.field }}</span>
              <ATag color="blue">{{ subPoint?.deviceName || selectedPoint?.deviceName }}</ATag>
              <span style="font-size: 12px; color: #999;">采集值: <ATag>{{ subPoint?.value }}</ATag></span>
              <ATag v-if="subPoint?.indicator" :color="gradeTagColor(subPoint?.grade)">{{ subPoint?.indicator }}</ATag>
              <span style="font-weight: normal; font-size: 12px; color: #999;">采集时间: <ATag>{{ formatTime(subPoint?.collectTime) }}</ATag></span>
              <span style="font-size: 12px; color: #999;">设备分组: <ATag>{{ subPoint?.deviceGroupName || selectedPoint?.deviceGroupName || '-' }}</ATag></span>
              <span style="font-size: 12px; color: #999;">点位地址: <ATag>{{ subPoint?.address || '-' }}</ATag></span>
            </ASpace>
          </template>
          <ACard size="small" class="di-chart-card" title="历史趋势">
            <template #extra>
              <!-- 聚合统计 -->
              <ASpace :size="16">
                <span class="di-header-stat">最大值: <b>{{ subStats.max != null ? subStats.max : '-' }}</b> ({{ subStats.maxTime ? dayjs(subStats.maxTime).format('MM-DD HH:mm') : '-' }})</span>
                <span class="di-header-stat">最小值: <b>{{ subStats.min != null ? subStats.min : '-' }}</b> ({{ subStats.minTime ? dayjs(subStats.minTime).format('MM-DD HH:mm') : '-' }})</span>
                <span class="di-header-stat">平均值: <b>{{ subStats.avg != null ? Number(subStats.avg).toFixed(2) : '-' }}</b></span>
                <span class="di-header-stat">数据量: <b>{{ subStats.count != null ? subStats.count : '-' }}</b></span>
              </ASpace>
            </template>
            <ASpin :spinning="subChartLoading">
              <div id="di-sub-echarts-line" style="height: calc(68vh - 150px)"></div>
            </ASpin>
          </ACard>
        </ADrawer>
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
    // 分析时间范围(抽屉标题中ARangePicker), 默认取采集时间前后1小时
    let analysisTime = ref(null);
    // 分析时间快捷选项
    let analysisPresets = ref([
      {label: '近一小时', value: [dayjs().subtract(1, 'hour'), dayjs()]},
      {label: '近三小时', value: [dayjs().subtract(3, 'hour'), dayjs()]},
      {label: '近6小时', value: [dayjs().subtract(6, 'hour'), dayjs()]},
      {label: '近12小时', value: [dayjs().subtract(12, 'hour'), dayjs()]},
      {label: '今天', value: [dayjs().startOf('day'), dayjs().endOf('day')]},
      {label: '昨天', value: [dayjs().subtract(1, 'day').startOf('day'), dayjs().subtract(1, 'day').endOf('day')]},
      {label: '近5天', value: [dayjs().subtract(5, 'day'), dayjs()]},
    ]);

    // ============ 分析数据 ============
    let indicatorStats = ref({});
    let chartLoading = ref(false);
    let trendData = ref([]);
    let sameDevicePoints = ref([]);
    let otherDevicePoints = ref([]);
    let associatedStatsList = ref([]);
    // 关联维度表格加载状态
    let assocLoading = ref(false);
    let selectedGrade = ref('');
    // 关联维度时间范围(±秒数), 默认±60秒
    let assocOffset = ref(60);
    // 关联时间范围选项
    let assocOffsetOptions = [
      {value: 30, label: '±30秒'},
      {value: 60, label: '±1分钟'},
      {value: 300, label: '±5分钟'},
      {value: 600, label: '±10分钟'},
      {value: 1800, label: '±半小时'},
      {value: 3600, label: '±1小时'},
    ];
    // 告警等级选项
    let gradeOptions = [
      {value: '', label: '全部'},
      {value: 'fatal', label: '严重'},
      {value: 'urgent', label: '紧急'},
      {value: 'warn', label: '告警'},
      {value: 'notice', label: '通知'},
      {value: 'normal', label: '正常'},
      {value: 'indicator', label: '指标'},
    ];
    let collectDataEcharts = shallowRef(null);

    // ============ 同设备点位统计抽屉 ============
    // 点击同设备点位卡片后选中的点位
    let subPoint = ref(null);
    let subVisible = ref(false);
    let subStats = ref({});
    let subChartLoading = ref(false);
    let subEcharts = shallowRef(null);

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
      {title: '设备', dataIndex: 'deviceName', ellipsis: true, align: 'center', width: 160},
      {title: '设备组', dataIndex: 'deviceGroupName', ellipsis: true, align: 'center', width: 160},
      // {title: '属性', dataIndex: 'name', ellipsis: true, align: 'center', width: 120},
      {title: '采集值', dataIndex: 'value', width: 100, ellipsis: true, align: 'center'},
      {title: '指标', dataIndex: 'indicator', width: 120, ellipsis: true, align: 'center'},
      // {title: '采集时间', dataIndex: 'collectTime', align: 'center'},
      {title: '操作', dataIndex: 'action', width: 70, align: 'center'},
    ];
    // 关联维度点位
    let assocStatsColumns = [
      {title: '设备', dataIndex: 'deviceName', ellipsis: true, align: 'center', width: 150},
      {title: '设备组', dataIndex: 'deviceGroupName', ellipsis: true, align: 'center', width: 150},
      {title: '属性', dataIndex: 'name', ellipsis: true, align: 'center', width: 120},
      {title: '采集值', dataIndex: 'value', width: 100, ellipsis: true, align: 'center'},
      {title: '指标', dataIndex: 'indicator', width: 100, ellipsis: true, align: 'center'},
      {title: '采集时间', dataIndex: 'collectTime', width: 150, align: 'center'},
      {title: '操作', dataIndex: 'action', width: 70, align: 'center'},
    ];

    // ============ 时间预设 ============
    let timePresets = ref([
      {label: '近一小时', value: [dayjs().subtract(1, 'hour'), dayjs()]},
      {label: '近6小时', value: [dayjs().subtract(6, 'hour'), dayjs()]},
      {label: '近12小时', value: [dayjs().subtract(12, 'hour'), dayjs()]},
      {label: '今天', value: [dayjs().startOf('day'), dayjs().endOf('day')]},
      {label: '昨天', value: [dayjs().subtract(1, 'day').startOf('day'), dayjs().subtract(1, 'day').endOf('day')]},
      {label: '近3天', value: [dayjs().subtract(3, 'day'), dayjs()]},
      {label: '近7天', value: [dayjs().subtract(7, 'day'), dayjs()]},
      // {label: '当月', value: [dayjs().startOf('month'), dayjs().endOf('month')]},
    ]);

    return {
      searchModel,
      cardList, listLoading, pagination,
      selectedPoint, analysisVisible, analysisTime, analysisPresets,
      indicatorStats, chartLoading, trendData, sameDevicePoints, otherDevicePoints,
      associatedStatsList, assocLoading, selectedGrade, assocOffset, assocOffsetOptions, gradeOptions, collectDataEcharts,
      subPoint, subVisible, subStats, subChartLoading, subEcharts,
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
      this.searchModel.fieldId = null;
      this.searchModel.searchTime = [dayjs().startOf('day'), dayjs()];
      this.analysisVisible = false;
      this.cardList = [];
      this.pagination.current = 1;
      this.doSearch();
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
      this.assocOffset = 60;
      // 重置分析时间, loadAnalysisData里按采集时间前后1小时初始化
      this.analysisTime = null;
      this.$nextTick(() => {
        this.loadAnalysisData();
      });
    },

    // 分析时间范围变更, 重新加载统计和趋势数据
    onAnalysisTimeChange() {
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
      // 未选择分析时间时默认取采集时间前后1小时
      if (!this.analysisTime || this.analysisTime.length !== 2) {
        this.analysisTime = [collectTimeDayjs.subtract(1, 'hour'), collectTimeDayjs.add(1, 'hour')];
      }
      let startTime = this.analysisTime[0].format('YYYY-MM-DD HH:mm:ss');
      let endTime = this.analysisTime[1].format('YYYY-MM-DD HH:mm:ss');
      let collectTime = collectTimeDayjs.format('YYYY-MM-DD HH:mm:ss');
      let productCode = record['productCode'];

      this.loadStats(uid, fieldId, startTime, endTime);
      this.loadTrendData(uid, fieldId, startTime, endTime);
      this.loadSameDevicePoints(uid, fieldId, collectTime);
      this.loadOtherDevicePoints(fieldId, collectTime, uid);
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
        // dataZoom: {type: 'slider', show: true, bottom: '12px'},
        tooltip: {trigger: 'axis'},
        toolbox: {feature: {saveAsImage: {show: true}}},
        grid: {top: '8%', left: '4%', right: '3%', bottom: '8px', containLabel: true},
        xAxis: {type: 'category', boundaryGap: false, data: xAxis},
        yAxis: {type: 'value', nameTextStyle: {padding: [0, 0, 10, -40]}},
        series: [{type: 'line', data: yAxis, smooth: true, areaStyle: {opacity: 0.15}}]
      };
      this.collectDataEcharts.setOption(option);
    },

    // ============ 同设备点位统计抽屉 ============
    // 同设备点位卡片点击, 弹出底部抽屉展示该点位的统计信息和趋势图
    onSameDevicePointClick(pt) {
      if (!pt || !pt.fieldId) return;
      this.subPoint = pt;
      this.subStats = {};
      this.subVisible = true;
      this.$nextTick(() => {
        this.loadSubData();
      });
    },

    // 抽屉打开动画结束后重置图表尺寸, 避免动画期间初始化宽度异常
    onSubOpenChange(open) {
      if (open && this.subEcharts) {
        this.subEcharts.resize();
      }
    },

    // 加载点位的聚合统计和趋势数据(沿用当前分析时间范围)
    loadSubData() {
      let pt = this.subPoint;
      if (!pt || !this.analysisTime || this.analysisTime.length !== 2) return;
      let uid = pt.uid || this.selectedPoint?.uid;
      let fieldId = pt.fieldId;
      let start = this.analysisTime[0].format('YYYY-MM-DD HH:mm:ss');
      let end = this.analysisTime[1].format('YYYY-MM-DD HH:mm:ss');

      GET('/iot/dataIndicator/stats', {uid, fieldId, start, end}).then(({code, data}) => {
        if (code === CoreConsts.SuccessCode) {
          this.subStats = data || {};
        }
      });

      this.subChartLoading = true;
      GET('/iot/dataIndicator/listData', {uid, fieldId, start, end}).then(({code, data}) => {
        if (code === CoreConsts.SuccessCode) {
          this.renderSubChart(data || []);
        }
      }).finally(() => {
        this.subChartLoading = false;
      });
    },

    renderSubChart(dataList) {
      if (!this.subEcharts) {
        this.subEcharts = echarts.init(document.getElementById("di-sub-echarts-line"));
      }
      let xAxis = dataList.map(item => item['collectTime'] ? dayjs(item['collectTime']).format('MM-DD HH:mm:ss') : '');
      let yAxis = dataList.map(item => item['value'] != null ? parseFloat(item['value']) : 0);
      let option = {
        tooltip: {trigger: 'axis'},
        toolbox: {feature: {saveAsImage: {show: true}}},
        grid: {top: '8%', left: '4%', right: '3%', bottom: '8px', containLabel: true},
        xAxis: {type: 'category', boundaryGap: false, data: xAxis},
        yAxis: {type: 'value', nameTextStyle: {padding: [0, 0, 10, -40]}},
        series: [{type: 'line', data: yAxis, smooth: true, areaStyle: {opacity: 0.15}}]
      };
      this.subEcharts.setOption(option);
    },

    loadSameDevicePoints(uid, excludeFieldId, collectTime) {
      GET('/iot/dataIndicator/associatedPoints', {uid, excludeFieldId, collectTime}).then(({code, data}) => {
        if (code === CoreConsts.SuccessCode) {
          // 补充name字段供DeviceIndicatorCard展示属性名称
          this.sameDevicePoints = (data || []).map(item => ({...item, name: item.fieldName || item.field}));
        }
      });
    },

    loadOtherDevicePoints(fieldId, collectTime, excludeUid) {
      GET('/iot/dataIndicator/crossDevicePoints', {fieldId, collectTime, excludeUid}).then(({code, data}) => {
        if (code === CoreConsts.SuccessCode) {
          this.otherDevicePoints = (data || []).map(item => ({...item, name: item.fieldName || item.field}));
        }
      });
    },

    loadAssociatedStats(uid, fieldId, collectTime) {
      let params = {uid, fieldId, collectTime};
      params['grade'] = this.selectedGrade;
      params['offsetSeconds'] = this.assocOffset;
      this.assocLoading = true;
      GET('/iot/dataIndicator/associatedStats', params).then(({code, data}) => {
        if (code === CoreConsts.SuccessCode) {
          this.associatedStatsList = (data || []).map(item => ({...item, name: item.fieldName || item.field}));
        }
      }).finally(() => {
        this.assocLoading = false;
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

    // 关联时间范围变更, 重新加载关联维度点位
    onOffsetChange() {
      this.onGradeChange();
    },

    // ============ 工具方法 ============
    // 指标标签按告警等级取色
    gradeTagColor(grade) {
      let colorMap = {fatal: 'red', urgent: 'magenta', warn: 'orange', notice: 'cyan', normal: 'green', indicator: 'purple'};
      return colorMap[grade] || 'default';
    },

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
/* 同设备点位卡片一行横向滚动 */
.di-same-device-row {
  margin-bottom: 8px;
  overflow-x: auto;
  overflow-y: hidden;
  display: flex;
  flex-wrap: nowrap;
  gap: 8px;
  padding: 6px;
}
.di-point-card {
  flex-shrink: 0;
  min-width: 120px;
  cursor: pointer;
}
.di-point-value {
  font-weight: bold;
  font-size: 16px;
  white-space: nowrap;
}
.di-point-unit {
  font-size: 12px;
  font-weight: normal;
  color: #8e8e8e;
}
.di-point-name {
  font-size: 12px;
  color: #8e8e8e;
  margin-top: 4px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
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
