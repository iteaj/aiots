<template>
  <div ref="mediaContainer" class="iv-rtvs" style="width: 100%; height: 100%; position: relative"></div>
</template>

<script>
import '/public/video/rtvs/CvNetVideo.js'
import {defineComponent, getCurrentInstance, nextTick, ref, watch} from 'vue';
import {useStore} from "vuex";
import {msgWarn} from "@/utils/message";

export default defineComponent({
  name: "Rtvs",
  props: {
    host: {type: String, required: true}, // 主机
    port: {type: Number, default: 17000}, // 端口
    videoNums: {type: Number, default: 1},

    onStop: {type: Function},
    onStartSpeek: {type: Function},
    onEndByServer: {type: Function, default: function () {
        return (msg, id, video) => {
          if(msg == '车机不在线') {
              msgWarn(`设备不在线`)
          } else {
            msgWarn(`${msg}`)
          }
        }
      }
    },
    onChangeH265Player: {type: Function},
    hasAudio: {type: Boolean, default: false},
  },
  setup(props, {emit, attrs}) {
    let isInit = ref(false);
    let mediaContainer = ref();

    const waitDo = ref([]);
    let instance = ref({
      GetContainerSize: () => null, // 获取容器大小
      Resize: (width, height) => null, // 重置尺寸
      LayoutByScreens: (num) => null, // 重置分屏数量
      StartRealTimeVideo: null, // 播放实时视频
      PlaybackVideo: null, // 播放历史视频
      DownLoadFmp4: null, // 下载视频文件
      QueryVideoFileList: null, // 获取录像文件列表
      IsPlaying: (index) => null, // 当前是否再播放
      Stop: (index) => null, // 停止指定的视频
    });

    let {proxy} = getCurrentInstance();
    let finishedInitCallback = () => {

      while (waitDo.value.length > 0) {
        const ucdo = waitDo.value.shift();
        if (ucdo) {
          ucdo(instance.value, mediaConfig);
        }
      }

      emit("finished", proxy);
    }

    let mediaConfig = {host: props.host, port: props.port};
    let instanceConfig = ref({
      // ..._this.$attrs,
      //wasm库加载地址
      // libffmpegUrl: "/public/video/rtvs/libffmpeg.js",
      //css地址 如需修改控件样式，请自行指定css地址
      // cssUrl: "/public/video/rtvs/CVNetVideo.css",
      // DecodeWasmWorkerUrl: '/public/video/rtvs/DecodeVideoWorker.js',
      callback(a) {
        isInit.value = true;
      },
      //事件通知
      events: {
        //超时警告通知 仅通知
        //参数1 leftMsec 剩余关闭毫秒
        //参数2 time 上次有动作时间
        timeoutWarning: null,
        //超时警告取消 表示警告通知后用户有了操作 重新开始计时
        timeoutCanceled: null,
        //超时通知 仅通知，如需关闭需用户在此事件响应中自行调用关闭接口
        //参数 time 上次有动作时间
        timeoutClose: null,
        //截图时事件
        //参数1 base64png base64编码的png图片
        //参数2 id 表示第几个分屏 从1开始 -1表示对讲通道
        //参数3 UCVideo对象
        //返回值表示是否取消默认后续下载操作，为真时表示取消
        onCapture: null,
        //设备开始传输视频事件
        //参数1 id 表示第几个分屏 从1开始 -1表示对讲通道
        //参数2 UCVideo对象
        onDevConnect: null,
        //设备断开传输视频事件
        //参数1 id 表示第几个分屏 从1开始 -1表示对讲通道
        //参数2 UCVideo对象
        onDevDisconnect: null,
        //Websocket通道关闭事件
        //参数1 id 表示第几个分屏 从1开始 -1表示对讲通道
        //参数2 UCVideo对象
        onWsClose: null,
        //服务端通知事件
        //参数1 事件类型 字符串(onDevConnect onDevDisconnect onWsClose)之一
        //参数2 id 表示第几个分屏 从1开始 -1表示对讲通道
        //参数3 UCVideo对象
        onServerNotice: (e, id, video) => {

        },
        //停止视频时事件
        //参数1 id 表示第几个分屏 从1开始 -1表示对讲通道
        //参数2 停止的UCVideo对象
        onStop: props.onStop,
        //hls模式获取到HLS地址时触发
        //参数1 id 表示第几个分屏 从1开始
        //参数2 UCVideo对象
        //参数3 hls_http地址
        //参数4 hls_https地址
        //参数5 rtmp地址
        //参数6 clinetID 客户端ID 每次请求会不一样
        //返回值表示是否取消自动播放，为真时表示取消
        onHlsPlay: null,

        // 客户端获取麦克风错误，一般多见于无麦克风强行开启对讲的场景
        onMicError: (e) => {
          console.error(e)
          debugger
        },
        //分屏双击回调，
        //参数1 id 表示第几个分屏 从1开始
        //参数2 UCVideo对象
        //返回值 true表示取消双击放大
        onUcDbclick: null,
        //流量统计回调，
        //参数1 KBps 数值，建议toFixed(2)后再显示
        //参数2 id 表示第几个分屏 从1开始
        //参数3 UCVideo对象
        onKBpsChange: null,
        //
        onChangeH265Player: props.onChangeH265Player,
        //服务端结束
        //参数1 结束原因 字符串
        //参数2 id 表示第几个分屏 从1开始 -1表示对讲通道
        //参数3 UCVideo对象
        //返回值表示是否取消自动停止，为真时表示取消
        onEndByServer: props.onEndByServer,
        //对讲开始 与设备链路建立完成，且可开始对讲时触发
        //参数 无
        onStartSpeek: props.onStartSpeek,
      },
    })

    watch(() => isInit.value && instance.value, (status) => {
      if(status) {
        finishedInitCallback();
        // 自适应尺寸
        let clientWidth = proxy.$el.clientWidth;
        let clientHeight = proxy.$el.clientHeight;
        instance.value.Resize(clientWidth, clientHeight);
      }
    })

    return {mediaContainer, isInit, instance, waitDo, finishedInitCallback, mediaConfig, instanceConfig}
  },
  mounted() {
    this.instance = CvNetVideo.Init(this.$el, this.videoNums, this.instanceConfig);
  },
  beforeUnmount() {
    this.instance?.Stop(-1);
  },
  methods: {
    destroy() {
      this.stop(-1);
    },
    /**
     * 停止播放某个窗口
     * -1 关闭所有窗口
     * 0 代表当前选中窗口
     */
    stop(videoId = 0) {
      this.instance?.Stop(videoId);
    },
    /**
     * 返回播放器实例
     */
    getInstance() {
      return this.instance
    },
    instanceDo(callback) {
      if (this.isInit) {
        callback(this.instance, this.mediaConfig);
      } else {
        this.waitDo.push(callback);
      }
    },
    /**
     * 重置尺寸
     * @param width {Number}
     * @param height {Number}
     */
    resize(width, height) {
      if(width != undefined && height != undefined) {
        this.instanceDo(instance => {
          instance.Resize(width, height)
        });
      }
    },
    /**
     * 重置尺寸到父级的宽高
     */
    resizeToParent() {
      let parent = this.$el.parentNode;
      if(parent) {
        let computedStyle = parent.getBoundingClientRect();
        this.resize(computedStyle.width, computedStyle.height)
      }
    },
    /**
     * 获取当前选中的窗口
     */
    getSelectWindow() {
      return this.instance?.GetOperateUCVideo();
    },
    /**
     * 获取当前选中窗口的自定义配置
     */
    getExtraOfSelectWindow() {
      return this.getSelectWindow().config.extra;
    },
    /**
     * 获取当前选中窗口的状态信息
     */
    getStatusOfSelectWindow() {
      let selectWindow = this.getSelectWindow();
      return {isPlay: selectWindow['IsPlay'], extra: selectWindow.config.extra}
    },
    /**
     * 当前选中的窗口是否播放
     */
    isPlayOfSelectWindow() {
      return this.getSelectWindow()['IsPlay'];
    },
    /**
     * 重置分屏数量
     * @param videoNums
     */
    layoutByScreens(videoNums) {
      this.instanceDo(instance => {
        instance.LayoutByScreens(videoNums);
      })
    },
    /**
     * 播放实时视频
     * @param simOrDeviceId {String} 设备id
     * @param channelId {String} 通道id
     * @param callback {Function} 查询回调
     * @param config {RealTimeVideoConfig} 实时视频配置
     * @param videoId {Number} 在第几个屏中播放 0. 使用选中的屏
     */
    playRealTimeVideo(simOrDeviceId, channelId, callback, config, videoId = 0) {
      this.instanceDo((instance, mediaConfig) => {
        instance.StartRealTimeVideo(
            simOrDeviceId, channelId, config.streamType, config.hasAudio, videoId,
            {
              clusterHost: mediaConfig.host,
              clusterPort: mediaConfig.port,
              protocol: config.protocol,
              extra: config.extra
            },
            callback, config.playerMode,
        );
      })
    },
    /**
     * 播放历史视频
     * @param simOrDeviceId {String} 设备id
     * @param channelId {String} 通道id
     * @param callback {Function} 查询回调
     * @param config {PlaybackVideoConfig} 实时视频配置
     * @param videoId {Number} 在第几个屏中播放 0. 使用选中的屏
     */
    playbackVideo(simOrDeviceId, channelId, callback, config, videoId = 0) {
      this.instanceDo((instance, mediaConfig) => {
        instance.PlaybackVideo(
            simOrDeviceId, channelId, config.mediaType, config.streamType, config.storageType, config.playbackMode
            , config.multiple, config.startTime, config.endTime, videoId,
            {
              clusterHost: mediaConfig.host,
              clusterPort: mediaConfig.port,
              protocol: config.protocol,
              extra: config.extra
            },
            callback, config.playerMode, config.dataSource
        );
      })
    },
    /**
     * 开始语音对讲
     * @param protocol 0.808-2013 1.808-2019 2.GB28181
     */
    startSpeak(simOrDeviceId, channelId, protocol, callback) {
      this.instanceDo((instance, config) => {
        let startSpeek = instance.StartSpeek(simOrDeviceId, channelId,
            {
              clusterHost: config.host,
              clusterPort: config.port,
              protocol: protocol
            }
        );

        if(callback instanceof Function) {
          callback(startSpeek, instance);
        } else {
          if (!startSpeek) {
            this.$msg.warn('未能获取到麦克风，请确认当前页面是https且未设置阻止。');
          }
        }
      })
    },
    /**
     * 停止对讲
     */
    stopSpeak() {
      this.instanceDo((instance, config) => {
        instance.StopSpeak();
      })
    },
    /**
     * 设置对讲音量
     * volume 音量大小 0.0-10.0 1.0是指原始音量大小，10.0是指音量放大10倍。
     */
    setSpeakVolume(volume) {
      this.instanceDo((instance, config) => {
        instance.SetSpeekVolume(volume);
      })
    },
    /**
     *  请求服务器视频文件列表
     * @param simOrDeviceId {String} 设备id
     * @param channelId {String} 通道id
     * @param config {DownloadFmp4Config} 下载mp4文件配置
     */
    downloadFmp4(simOrDeviceId, channelId, config) {
      this.instanceDo((instance, mediaConfig) => {
        instance.DownLoadFmp4(
            simOrDeviceId, channelId, config.mediaType, config.streamType, config.storageType
            , config.playbackMode, config.multiple, config.startTime, config.endTime,
            {
              clusterHost: mediaConfig.host,
              clusterPort: mediaConfig.port,
              protocol: config.protocol,
            }, config.dataSource, config.speed
        );
      })
    },
    /**
     *  请求服务器视频文件列表
     * @param simOrDeviceId {String} 设备id
     * @param channelId {String} 通道id
     * @param callback {Function} 查询回调
     * @param config {QueryFileConfig} 查询视频配置
     * @param videoId {Number} 在第几个屏中播放 0. 使用选中的屏
     */
    queryVideoFileList(simOrDeviceId, channelId, callback, config, videoId) {
      this.instanceDo((instance, mediaConfig) => {
        instance.QueryVideoFileList(
            simOrDeviceId, channelId,
            config.startTime.unix(),
            config.endTime.unix(),
            config.alarm,
            config.mediaType,
            config.streamType,
            config.storageType,
            callback,
            videoId,
            {
              clusterHost: mediaConfig.host,
              clusterPort: mediaConfig.port,
              protocol: config.protocol,
            },
            config.dataSource
        );
      })
    },
    // 获取空闲的分屏
    getIdleVideoId(autoClose) {
      let svid = 0;
      for (let index = 1; index <= this.videoNums; index++) {
        if (!this.instance.IsPlaying(index)) {
          svid = 0;
          return index;
        }
      }

      if (autoClose) {
        this.instance.Stop(++svid);
        return svid;
      }
    }
  }
})
</script>

<style>

</style>
