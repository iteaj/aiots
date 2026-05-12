<template>
  <div style="width: 100%; height: 100%; background-color: #ffffff; padding: 8px">
    <AFlex gap="8" style="height: 100%;">
      <div style="width: 460px">
        <div style="padding:0px 3px 8px; font-size: 18px; text-align: center" :class="'iv-device-status-' + ($route.query?.status == 'true' ? 'online' : 'offline')">
          <UIcon type="iz-icon-camera" style="font-size: 18px"/> {{$route.query?.name}}
        </div>
        <ASpace>
          <ARangePicker v-model:value="searchTime" show-time />
          <AButton type="primary" @click="queryVideoFileList" :loading="tableLoading">查询</AButton>
        </ASpace>
        <ATable :columns="historyColumns" :scroll="{x: 460, y: 470}" size="small" :pagination="false" :loading="tableLoading"
                style="margin-top: 5px" :dataSource="dataSource" bordered>
          <template #bodyCell="{column, record}">
            <template v-if="column.key === 'action'">
              <AButton type="link" @click="playBack(record)">播放</AButton>
<!--              <AButton type="link" @click="download(record)">下载</AButton>-->
            </template>
          </template>
        </ATable>
      </div>
      <div style="flex-grow: 1">
        <div style="width: 100%; height: 100%" ref="playContainerRef">
          <Rtvs ref="rtvsPlayer" :host="this.$env.rtvs.host" :videoNums="1" @end-by-server="onEndByServer"></Rtvs>
        </div>
      </div>
    </AFlex>
  </div>
</template>
<!-- 功能 -->
<script>
import {defineAsyncComponent, getCurrentInstance, reactive, ref, watch} from "vue";
import {FolderFilled} from "@antdv-next/icons";
import dayjs from "dayjs";
import {DownloadFmp4Config, PlaybackVideoConfig, QueryFileConfig} from "@/components/video";

const Rtvs= defineAsyncComponent(()=>import("@/components/video/Rtvs.vue"))
export default {
  name: "History",
  components: {Rtvs, FolderFilled},
  setup() {
    let rtvsPlayer = ref(null);
    let dataSource = ref([]);
    let tableLoading = ref(false);
    let playContainerRef = ref(null);
    let videoPosition = ref({width: 0, height: 0})

    let historyColumns = ref([
      { title: '开始时间', dataIndex: 'StartTime', align: 'center', width: 155 },
      { title: '结束时间', dataIndex: 'EndTime', align: 'center', width: 155 },
      { title: '大小(KB)', dataIndex: 'FileSize', align: 'center', width: 80 },
      { title: '操作', key: 'action', align: 'center', width: 70, fixed: 'right' },
    ])

    let segmentedOptions = reactive([1, 4]);

    let onlineOptions = [
      {label: '在线', value: true},
      {label: '离线', value: false},
    ]

    let resize = () => {
      let playContainer = playContainerRef.value;
      let computedStyle = playContainer.getBoundingClientRect();
      videoPosition.value.width = computedStyle.width;
      videoPosition.value.height = computedStyle.height
    }

    watch(() => playContainerRef.value, () => resize())

    watch(() => rtvsPlayer.value, (newValue) => {
      if(newValue) {
        newValue.resizeToParent();
      }
    })

    let windowsNum = ref(1);
    let searchTime = ref([dayjs().subtract(1, 'hour'), dayjs()]);

    // 正在播放的设备
    let playingDevices = ref([]);
    let treeSourceModel = ref([]);
    return {searchTime, onlineOptions, treeSourceModel, historyColumns, playingDevices, resize
      , windowsNum, segmentedOptions, playContainerRef, videoPosition, rtvsPlayer, tableLoading, dataSource}
  },
  methods: {
    onStopVideo(id, video) {
      if (id > -1) {
        // const item = state.map[video.config.treekey];
        // if (!item.autoChange) unCheck(state.map[video.config.treekey]);
      }
    },
    onChange265(playerMode, id, video) {

    },
    onEndByServer(msg, id, video) {
      if(msg == '车机不在线') {
        this.$msg.warn(`设备不在线`)
      } else {
        this.$msg.warn(`${msg}`)
      }
    },
    queryVideoFileList() {
      let _this = this;
      let query = this.$route.query;
      if(!query['deviceId']) {
        this.$msg.warn('设备id不存在'); return;
      }
      if(!query['channelId']) {
        this.$msg.warn('通道id不存在'); return;
      }
      if(!this.searchTime[0] || !this.searchTime[1]) {
        this.$msg.warn('请选择查询时间'); return;
      }

      this.tableLoading = true;
      this.rtvsPlayer.queryVideoFileList(query['deviceId'], query['channelId'], (data) => {
        _this.dataSource = [];
        if(data.FileCount > 0) {
          for (let index = 0; index < data.FileCount; index++) {
            const element = data.FileList[index];
            _this.dataSource.push({
              StartTime: _this.time1078ToTime(element.StartTime),
              EndTime: _this.time1078ToTime(element.EndTime),
              FileSize: parseInt(element.FileSize / 1024),
              odata: element,
            });
          }
        } else {
          this.$msg.warn("没有录像记录");
        }
        _this.tableLoading = false;
      }, new QueryFileConfig(2, _this.searchTime[0], _this.searchTime[1]))
    },
    time1078ToTime(time1078) {
      return `20${time1078.substring(0, 2)}-${time1078.substring(2, 4)}-${time1078.substring(4,6,
      )} ${time1078.substring(6, 8)}:${time1078.substring(8, 10)}:${time1078.substring(10, 12)}`;
    },
    /**
     *
     * @param num
     */
    windowsNumChange(num) {
      this.rtvsPlayer.instanceDo(instance => {
        let playContainer = this.playContainerRef;
        let computedStyle = playContainer.getBoundingClientRect();
        instance.Resize(computedStyle.width, computedStyle.height);
        instance.LayoutByScreens(num);
      });
    },

    /**
     * 历史视频播放
     * @param record
     */
    playBack(record) {
      let query = this.$route.query;
      if(!query['deviceId']) {
        this.$msg.warn('设备id不存在'); return;
      }
      if(!query['channelId']) {
        this.$msg.warn('通道id不存在'); return;
      }

      let endTime = record.EndTime;
      let startTime = record.StartTime;
      this.rtvsPlayer.playbackVideo(query['deviceId'], query['channelId'], null
          , new PlaybackVideoConfig(2, startTime, endTime
              , record.odata.MediaType, record.odata.StreamType, record.odata.StorageType))
    },

    /**
     * 下载历史视频
     * @param record
     */
    download(record) {
      let query = this.$route.query;
      if(!query['deviceId']) {
        this.$msg.warn('设备id不存在'); return;
      }
      if(!query['channelId']) {
        this.$msg.warn('通道id不存在'); return;
      }

      this.rtvsPlayer.downloadFmp4(query['deviceId'], query['channelId']
          , new DownloadFmp4Config(2, record.StartTime, record.EndTime
              , record.odata.MediaType, record.odata.StreamType, record.odata.StorageType))
    }
  }
}
</script>
<style scoped>
.iv-device-status-online {
  color: #19be6b;
}
.iv-device-status-offline {
  color: #3e3e3e;
}
</style>
