<template>
  <UView>
    <AFlex :gap="8" style="height: 100%; background-color: #ffffff">
      <div style="padding: 12px; width: 320px; min-width: 320px; position: relative">
        <a-input-search v-model:value="searchValue" style="margin-bottom: 8px" placeholder="请输入要搜索的设备" />
        <UTree url="/gbs/device/trees" childUrl="/gbs/device/trees?deviceId={deviceId}" blockNode checkable :resolverNode="resolverNode" :autoExpandParent="true"
               :fieldNames="{title: 'name', key: 'id', children: 'children'}" v-model:source="treeSourceModel" :checkStrictly="true" @check="checkNode" ref="deviceTreeRef">
          <template #title="{data, title}">
            <div style="position: relative">
              <FolderFilled v-if="!data['isLeaf']" :class="'iv-device-status-' + (data.status ? 'online' : 'offline')"/>
              <UIcon v-else type="iz-icon-camera" :class="'iv-device-status-' + (data.status ? 'online' : 'offline')"/>
              {{data.name}}
            </div>
          </template>
        </UTree>
        <div class="iv-btn-yuntai" title="云台控制" @click="yuntaiHandel">
          <UIcon type="iz-icon-yuntai" style="font-size: 26px" />
        </div>
        <div class="animate__animated iv-realtime-ptz" :class="ptzAnimate">
          <PTZ @ctrl="ptzCamera" @voice="voice" ref="ptzRef" />
        </div>
      </div>
      <div style="flex-grow: 1">
        <AFlex :vertical="true" style="height: 100%">
          <a-segmented v-model:value="windowsNum" :options="segmentedOptions" size="large" style="margin-top: 3px; flex-grow: 0" @change="windowsNumChange"/>
          <div style="flex-grow: 1; margin-top: 3px" ref="playContainerRef">
            <Rtvs ref="rtvsPlayer" :videoNums="windowsNum" :host="this.$env.rtvs.host" @end-by-server="onEndByServer"></Rtvs>
          </div>
        </AFlex>
      </div>
    </AFlex>
  </UView>
</template>
<!-- 功能 -->
<script>
import {defineAsyncComponent, reactive, ref, watch} from "vue";
import {difference} from "lodash";
import {FolderFilled} from "@antdv-next/icons";
import {RealTimeVideoConfig} from "@/components/video";
import PTZ from "@/components/video/PTZ.vue";
import CoreConsts from "@/components/CoreConsts";
const Rtvs= defineAsyncComponent(()=>import("@/components/video/Rtvs.vue"))
export default {
  name: "Video",
  components: {PTZ, Rtvs, FolderFilled},
  setup() {
    let showPtz = ref(false);
    let ptzRef =ref();
    let rtvsPlayer = ref(null);
    let deviceTreeRef = ref(null);
    let playContainerRef = ref(null);
    let ptzAnimate = ref("hidden");
    let videoPosition = ref({width: 0, height: 0})

    let segmentedOptions = reactive([1, 4, 9, 10, 16]);

    let onlineOptions = [
      {label: '在线', value: true},
      {label: '离线', value: false},
    ]

    let searchValue = ref('');
    watch(() => searchValue.value, () => {

    })

    let cols = ref([]);
    let windowsNum = ref(10);
    let editModel = ref({});

    // 正在播放的设备
    let playingDevices = ref([]);
    let treeSourceModel = ref([]);
    return {editModel, searchValue, onlineOptions, treeSourceModel, cols, playingDevices, showPtz, ptzRef
      , windowsNum, segmentedOptions, playContainerRef, videoPosition, rtvsPlayer, deviceTreeRef, ptzAnimate}
  },
  methods: {
    resolverNode(data) {
      // 如果类型是设备说明有下面子节点通道
      if(data['type'] == 'device') {
        data['isLeaf'] = false;
        data['disableCheckbox'] = true;
      } else {
        data['isLeaf'] = true;
      }

      // data['disableCheckbox'] = !data['status'];
    },
    onStopVideo(id, video) {
      if (id > -1) {
        // const item = state.map[video.config.treekey];
        // if (!item.autoChange) unCheck(state.map[video.config.treekey]);
      }
    },
    onChange265(playerMode, id, video) {

    },
    onEndByServer(msg, id, video) {
      let treeKey = video.config.extra.treeKey;
      let value = this.deviceTreeRef.getValueByKey(treeKey);
      if(msg == '车机不在线') {
        this.$msg.warning(`设备不在线[${value?.name}]`)
        let checkedKeys = this.deviceTreeRef.getCheckedKeys()['checked'];
        if(checkedKeys instanceof Array) {
          let indexOf = checkedKeys.indexOf(treeKey);
          checkedKeys.splice(indexOf, 1);
          // this.deviceTreeRef.setCheckedKeys([...checkedKeys]);
        }
      } else {
        this.$msg.warning(`${msg}[${value?.name}]`)
      }
    },
    checkNode(checkedKeys, {checked, checkedNodes, node}) {
      checkedKeys = this.deviceTreeRef.getCheckedKeys()['checked'];
      let _add = difference(checkedKeys, this.playingDevices);
      let _remove = difference(this.playingDevices, checkedKeys);
      _add.forEach(item => {
        let value = this.deviceTreeRef.getValueByKey(item);
        if(value['type'] == 'channel') {
          this.rtvsPlayer.instanceDo(instance => {
            let videoId = this.rtvsPlayer.getIdleVideoId(false);
            if(videoId) {
              value['videoId'] = videoId;
              this.rtvsPlayer.playRealTimeVideo(value.deviceId, value.channelId, null
                  , new RealTimeVideoConfig(2, {channel: value, treeKey: item}), videoId);
            }
          });
        }
      });
      _remove.forEach(item => {
        let value = this.deviceTreeRef.getValueByKey(item);
        if(value['type'] == 'channel' && value['videoId']) {
          this.rtvsPlayer.instanceDo(instance => {
            instance.Stop(value['videoId']);
          });
        }
      })
      this.playingDevices = checkedKeys;
    },
    windowsNumChange(num) {
      this.rtvsPlayer.instanceDo(instance => {
        let playContainer = this.playContainerRef;
        let computedStyle = playContainer.getBoundingClientRect();
        instance.Resize(computedStyle.width, computedStyle.height);
        instance.LayoutByScreens(num);
      });
    },
    ptzCamera({command, speed}) {
      let {isPlay, extra} = this.rtvsPlayer.getStatusOfSelectWindow();
      if(isPlay) {
        let channel = extra['channel'];
        let deviceId = channel['deviceId'];
        let channelId = channel['channelId'];
        this.$http.post('/gbs/ptz/ctrl/' + deviceId + '/' + channelId + '?command=' + command + '&speed=' + speed)
            .then((resp) => {
              let {code, message} = resp;
              if(code != CoreConsts.SuccessCode) {
                this.$msg.error(message);
              }
            });
      } else {
        this.$msg.warning("此窗口未建立链接");
      }

    },
    voice(status) {
      let {isPlay, extra} = this.rtvsPlayer.getStatusOfSelectWindow();
      if(isPlay) {
        let channel = extra['channel'];
        let deviceId = channel['deviceId'];
        let channelId = channel['channelId'];
        if(!status) {
          this.rtvsPlayer.startSpeak(deviceId, channelId, 2, status => {
            if(!status) {
              this.$msg.warning('未能获取到麦克风，请确认当前页面是https且未设置阻止。');
            }

            this.ptzRef.setSpeakStatus(status);
          });
        } else {
          this.rtvsPlayer.stopSpeak();
          this.ptzRef.setSpeakStatus(false);
        }
      } else {
        this.$msg.warning("此窗口未建立链接");
      }
    },
    yuntaiHandel() {
      this.showPtz = !this.showPtz;
      this.ptzAnimate = this.showPtz ? 'animate__fadeInUp' : 'animate__fadeOutLeftBig'
    }
  }
}
</script>
<style scoped>
.iv-window-num {
  width: 60px;
  height: 30px;
  padding: 8px;
  text-align: center;
  display: inline-block;
}
.iv-device-status-online {
  color: #19be6b;
}
.iv-btn-yuntai {
  position: absolute;
  bottom: 36%;
  right: 12px;
  width: 30px;
  height: 30px;
  padding: 2px;
  cursor: pointer;
  border-radius: 100%;
  box-shadow: 0px 0px 6px 0px #848383;
  background-color: rgba(253, 252, 252, 0.54)
}
.iv-realtime-ptz {
  position: absolute;
  bottom: 158px;
  left: 88px;
  width: 30px;
  height: 30px;
  padding: 2px;
}
.iv-realtime-ptz.hidden {
  display: none;
}
</style>
