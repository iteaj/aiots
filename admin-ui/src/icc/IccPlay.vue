<template>
  <div id="ws-real-player" style="width: 100%; height: 100%" muted="muted"></div>
</template>

<script>
import {ref, watch} from 'vue'
import PlayerManager from "./PlayerManager.js";
export default {
  name: "IccPlay",
  props: {
    num: {default: 1},
    type: {default: 'real'},
    maxNum: {default: 1},
    channels: {default: () => {return []}}
  },
  setup(props) {
    let isInit = ref(false);
    let realPlayer = ref();
    let startPlay = () => {
      realPlayer.value.playRealVideo({
        channelList: [
          ...props.channels
          // {
          //     id: _this.channel.channelCode, // {String} 通道编码 -- 用于预览，必填
          //     deviceCode: _this.channel.deviceCode, // {String} 设备编码 -- 用于对讲，对讲必填，无对讲功能可不填
          //     deviceType: _this.channel.deviceType, // {String} 设备类型 -- 用于对讲，对讲必填，无对讲功能可不填
          //     channelSeq: _this.channel.channelSeq, // {String|Number} 通道序号 -- 用于对讲，对讲必填，无对讲功能可不填
          //     cameraType: _this.channel.cameraType, // {String|Number} 摄像头类型 -- 用于云台，云台必填，无云台功能可不填
          //     capability: _this.channel.capability, // {String} 能力集 -- 用于云台，选填
          // }
        ],
        streamType: 1, // {Number} 码流类型 1-主码流 2-辅码流
        windowIndex: 0 // {Number} 播放窗口序号（从0开始）
      })
    }

    watch(() => props.channels, () => {
      if(isInit.value) {
        realPlayer.value.close();
        startPlay();
      }
    })
    return {realPlayer, isInit, startPlay};
  },
  mounted() {
    let _this = this;
    this.$nextTick(() => {
      this.realPlayer = new PlayerManager({
        el: "ws-real-player", /** 实时预览容器id，创建多个播放器，传入不同的容器id即可 **/
        type: _this.type, /** real - 实时预览  record - 录像回放 **/
        maxNum: _this.maxNum,  /** 一个播放器上限能播放的路数，可根据实际情况设置，支持 1 4 9 16 25 **/
        num: _this.num,   /** 初始化，页面显示的路数 **/
        showControl: true, /** 是否显示工具栏，默认显示 **/
        showIcons: { // 自定义按钮，需要的请配置true, 不需要的按钮请配置false，所有的按钮属性都要写上
          streamChangeSelect: true, // 主辅码流切换
          // talkIcon: true, // 对讲功能按钮
          localRecordIcon: true, // 录制视频功能按钮
          // audioIcon: true, // 开启关闭声音按钮
          // snapshotIcon: true, // 抓图按钮
          // closeIcon: true, // 关闭视频按钮
        },
        streamServerIp: "124.160.33.135:4079",
        intranetMap: {
          "10.10.8.5": "124.160.33.135:4079"
        },
        setWSUrl: (wsList) => { // 返回服务器的多个websocket连接
          return "ws://124.160.33.135:4079" // 此处示例代码
        },
        isIntranet: false, // 告知是否是内网，如果是内网，传入 true, 外网传入 false
        showRecordProgressBar: true, // 录像回放时，录像的进度条是否需要
        useH265MSE: true, // true-表示硬解  false-软解 默认不传该字段
        receiveMessageFromWSPlayer: (methods, data, err) => { /* 回调函数，可以在以下回调函数里面做监听 */
          // debugger
          switch(methods) {
            case 'initializationCompleted':
              _this.isInit = true;
              // 初始化完成，可调用播放方法（适用于动态加载解码库）
              // 若回调未触发时就使用实时预览/录像回放，则无法播放。
              // 此时我们可以调用一个
              _this.startPlay();
              break;
            case "realSuccess": // 实时预览成功
              console.log("实时预览成功",)
              break;
            case "realError": // 实时预览失败
              console.log("实时预览失败")
              break;
            case "talkError": // 对讲失败
              console.log("对讲失败");
              break;
            case 'selectWindowChanged': // 选中的窗口发生改变
              console.log(data, "返回窗口信息")
              break;
            case 'windowNumChanged': // 播放器显示的路数发生改变
              console.log(data, "返回显示的窗口数量")
              break;
            case 'closeVideo': // 视频关闭回调
              // 点击关闭按钮引发的视频关闭进行提示
              // 切换视频引发的视频关闭不进行提示
              if(!data.changeVideoFlag) {
                console.log(`窗口${data.selectIndex}的视频已关闭`)
              }
              break;
            case 'statusChanged': // 视频状态发生改变

              break;
            case 'errorInfo': // 错误信息汇总
              console.log(data, "可打印查看错误消息");
          }
        }
      })
    })
  }
}
</script>

<style scoped>

</style>