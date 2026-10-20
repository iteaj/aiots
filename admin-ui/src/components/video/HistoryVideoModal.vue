<template>
  <UModal :uid="uid" :title="title" :maskClosable="false" v-model:source="channelModel" :height="height" :width="width"
          :footer="null" @cancel="cancelHandle" :destroyOnHidden="true">
<!--      <Jessibuca v-if="media=='wvp'" ref="mediaRef" :videoUrl="videoUrl" :error="videoError" :message="videoError" :hasAudio="hasAudio" fluent autoplay live></Jessibuca>-->
    <ARow :gutter="8" style="height: 100%; background-color: #ffffff">
      <ACol span="8" style="padding: 16px;">
        <ASpace>
          <ARangePicker v-model:value="searchTime" show-time />
          <AButton type="primary" @click="queryVideoFileList" :loading="tableLoading">查询</AButton>
        </ASpace>
<!--        <ATable :columns="historyColumns" :scroll="{x: 360}" size="small" :pagination="false" :loading="tableLoading"-->
<!--                style="margin-top: 5px" :dataSource="dataSource">-->
<!--          <template #bodyCell="{column, record}">-->
<!--            <template v-if="column.key === 'action'">-->
<!--              <AButton type="link" @click="playBack(record)">播放</AButton>-->
<!--              <AButton type="link" @click="download(record)">下载</AButton>-->
<!--            </template>-->
<!--          </template>-->
<!--        </ATable>-->
      </ACol>
      <ACol span="16">
        <AFlex :vertical="true" style="height: 100%">
          <ARangePicker v-model:value="searchTime" show-time />
          <div style="flex-grow: 1; margin-top: 3px" ref="playContainerRef">
            <Rtvs ref="rtvsPlayer" :videoNums="1" :videoWidth="width" :videoHeight="height"></Rtvs>
          </div>
        </AFlex>
      </ACol>
    </ARow>
  </UModal>
</template>

<!-- 历史视频弹框播放器 -->
<script>
import Rtvs from "@/components/video/Rtvs.vue";
// import Jessibuca from "@/components/video/Jessibuca.vue";
import {watch, ref, nextTick} from "vue";
import {GET} from "@/utils/request";
import CoreConsts from "@/components/CoreConsts";
import {CaretUpOutlined, ZoomOutOutlined, ZoomInOutlined} from '@antdv-next/icons'
import {mapGetters, useStore} from "vuex";
import {RealTimeVideoConfig} from "@/components/video/index";
export default {
  name: "HistoryVideoModal",
  components: {Rtvs, CaretUpOutlined, ZoomInOutlined, ZoomOutOutlined},
  props: {
    uid: {required: true, type: String},
    title: {default: '历史视频'},
    hasAudio: {default: false},
    protocol: {default: 2}, // 协议类型
    width: {default: 720, type: Number},
    height: {default: 480, type: Number},
    media: {type: String, default: 'rtvs'},
  },
  setup(props) {
    let mediaRef = ref(null);
    let searchTime = ref([]);
    let channelModel = ref({});

    watch(() => mediaRef.value, (newValue) => {
      if(newValue) {
        let model = channelModel.value;
        newValue.playRealTimeVideo(model['deviceId'], model['channelId'], null
            , new RealTimeVideoConfig(2));
      }
    })

    // let loadVideoUrl = (newVal) => {
    //   if(newVal) {
    //     GET("/gbs/play/start", {deviceId: newVal['deviceId'], channelId: newVal['channelId']}).then(({code, data, message}) => {
    //       if(code == CoreConsts.SuccessCode) {
    //         videoUrl.value=data['ws_flv'];
    //       }
    //     })
    //   }
    // }
    // watch(() => channelModel.value, loadVideoUrl)
    // loadVideoUrl(channelModel.value);
    return {searchTime, channelModel, mediaRef}
  },
  mounted() {

  },
  methods: {
    videoError() {

    },
    ptzCamera: function (command) {
      console.log('云台控制：' + command);
      let ctrlSpeed = this.ctrlModel['ctrlSpeed'] || '';
      let deviceId = this.channelModel['deviceId'];
      let channelId = this.channelModel['channelId'];
      this.$http.post('/gbs/ptz/ctrl/' + deviceId + '/' + channelId + '?command=' + command + '&horizonSpeed=' + ctrlSpeed + '&verticalSpeed=' + ctrlSpeed + '&zoomSpeed=' + ctrlSpeed).then(function (res) {});
    },
    cancelHandle() {
      this.mediaRef.destroy();
    }
  }
}
</script>

<style scoped>

</style>
