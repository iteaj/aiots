<template>
  <AModal :title="title" :maskClosable="false" :height="height" :width="width" :footer="null" wrapClassName="iv-rtvs-modal"
          @cancel="cancelHandle" destroyOnHidden :styles="{body: {height: height + 'px', width: (width - 48) + 'px', position: 'relative'}}">
    <div v-if="ptz != null" class="iv-rtvs-modal-ptz">
      <PTZ :zoom="ptz.zoom != false" @voice="voice" @ctrl="ptzCamera" ref="ptzRef"/>
      <div class="iv-icon-ptz">
        <UIcon type="iz-icon-yuntai" style="font-size: 26px"/>
      </div>
    </div>
      <Rtvs ref="mediaRef" :videoNums="1" :host="host" :port="port" @finished="rtvsLoadedFinished"></Rtvs>
  </AModal>
</template>

<!-- 国标GB28181视频播放器 -->
<script>
import PTZ from '@/components/video/PTZ.vue';
import Rtvs from "@/components/video/Rtvs.vue";
import {watch, ref, nextTick} from "vue";
import {GET} from "@/utils/request";
import {RealTimeVideoConfig} from "@/components/video/index";
import CoreConsts from "@/components/CoreConsts";
export default {
  name: "GbsVideoModal",
  components: {Rtvs, PTZ},
  props: {
    title: {default: '实时视频'},
    hasAudio: {default: false},
    ptz: {type: Object, default: () => {return {zoom: true}}},
    width: {default: 720, type: Number},
    height: {default: 480, type: Number},
    media: {type: String, default: 'rtvs'},
    channel: {type: Object},
    host: {type: String, required: true}, // 主机
    port: {type: Number, default: 17000}, // 端口
  },
  setup(props) {
    let ptzRef = ref();
    let mediaRef = ref();
    let videoUrl = ref("");
    let ctrlModel = ref({
      ctrlSpeed: 30
    });
    let channelModel = ref({});

    watch(() => props.channel, (newValue) => {
      if(newValue && mediaRef.value) {
        newValue.playRealTimeVideo(newValue['deviceId'], newValue['channelId'], null
            , new RealTimeVideoConfig(2));
      }
    })

    return {videoUrl, channelModel, ctrlModel, mediaRef, ptzRef}
  },
  mounted() {

  },
  methods: {
    videoError() {

    },
    voice(status) {
      let deviceId = this.channel['deviceId'];
      let channelId = this.channel['channelId'];
      if(!status) {
        this.mediaRef.startSpeak(deviceId, channelId, 2, status => {
          if(!status) {
            this.$msg.warning('未能获取到麦克风，请确认当前页面是https且未设置阻止。');
          }

          this.ptzRef.setSpeakStatus(status);
        });
      } else {
        this.mediaRef.stopSpeak();
        this.ptzRef.setSpeakStatus(false);
      }
    },
    ptzCamera: function ({command, speed}) {
      let deviceId = this.channel['deviceId'];
      let channelId = this.channel['channelId'];
      this.$http.post('/gbs/ptz/ctrl/' + deviceId + '/' + channelId + '?command=' + command + '&speed=' + speed)
          .then((resp) => {
            let {code, message} = resp;
            if(code != CoreConsts.SuccessCode) {
              this.$msg.error(message);
            }
          });
    },
    cancelHandle() {
      this.mediaRef.destroy();
    },
    rtvsLoadedFinished(instance) {
      if(this.channel) {
        instance.playRealTimeVideo(this.channel['deviceId'], this.channel['channelId'], null, new RealTimeVideoConfig(2));
      }
    }
  }
}
</script>

<style>
.iv-rtvs-modal-ptz {
  top: 16px;
  right: 16px;
  z-index: 1000;
  width: 160px;
  position: absolute;
}
.iv-rtvs-modal-ptz:hover .iv-ctrl-item {
  width: 106px;
  height: 106px;
  padding: 5px;
  border-radius: 100%;
  background-color: rgba(16, 15, 15, 0.08);
}
.iv-rtvs-modal-ptz .iv-icon-ptz {
  top: 3px;
  left: 3px;
  position: absolute;
}
.iv-rtvs-modal-ptz:hover .iv-icon-ptz {
  display: none;
}
.iv-rtvs-modal-ptz .control-wrapper .iv-ctrl-item .inner {
  width: 2rem;
  height: 2rem;
  background-color: rgba(254, 254, 254, 0.09);
}
.iv-rtvs-modal-ptz:hover .iv-ctrl-item .inner {
  width: 6rem;
  height: 6rem;
  background-color: #ffffff;
}
.iv-rtvs-modal-ptz:hover .iv-ctrl-zoom,.iv-rtvs-modal-ptz:hover .iv-ctrl-item .control-round, .iv-rtvs-modal-ptz:hover .iv-ctrl-speed {
  display: block;
}
.iv-rtvs-modal-ptz:hover .iv-ctrl-item .iv-control-btn {
  display: flex;
}
.iv-rtvs-modal-ptz .iv-ctrl-item .inner>div, .iv-rtvs-modal-ptz .iv-ctrl-zoom, .iv-rtvs-modal-ptz .iv-ctrl-speed {
  display: none;
}


</style>
