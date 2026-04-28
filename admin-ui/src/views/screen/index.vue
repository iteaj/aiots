<template>
  <div id="iv-meta2d-container">
    <div id="meta2d"></div>
  </div>
</template>
<script  setup>
import '/public/plugin/echarts/echarts.min.js'
import '/public/plugin/highcharts/highcharts.js'
import '/public/plugin/highcharts/highcharts-more.js'
import '/public/plugin/le5le/r.js'
import '/public/plugin/le5le/lcjs.iife.js'
import '/public/plugin/le5le/marked.min.js'
import {getCurrentInstance, onMounted, onUnmounted} from 'vue';
import {Meta2d, register, registerAnchors, registerCanvasDraw,} from '@meta2d/core';
import {flowAnchors, flowPens} from '@meta2d/flow-diagram';
import {activityDiagram, activityDiagramByCtx,} from '@meta2d/activity-diagram';
import {classPens} from '@meta2d/class-diagram';
import {sequencePens, sequencePensbyCtx} from '@meta2d/sequence-diagram';
import {register as registerEcharts, registerHighcharts, registerLightningChart} from '@meta2d/chart-diagram';
import {formPath2DPens, formPens} from '@meta2d/form-diagram';
import {chartsPens} from '@meta2d/le5le-charts';
import {ftaAnchors, ftaPens, ftaPensbyCtx} from '@meta2d/fta-diagram';
import {useRoute} from 'vue-router';
import screenfull from "screenfull";
import CoreConsts from "@/components/CoreConsts";

const route = useRoute();

let type = null;
let meta2d = null;
let webSocket = null;
let {proxy} = getCurrentInstance();

//注册基本图形库
registerBasicDiagram();
onMounted(() => {

  const options = {
    // width: 1920,
    // height: 1080,
    // background: '#1e2430',
    // color: '#bdc7db',
    disableScale: true,
    disableTranslate: true,
    // background: '#fff',
    // color: '#000',
    // width:undefined,
    // height:undefined,
  }

  meta2d = new Meta2d('meta2d',options);

  // meta2d.socketFn = (e, {meta2d, type, topic, url}) => {
  //   // Do sth
  //   // meta2d.setValue(pen);
  //   return true;
  //   //return false; //表示仅执行自定义的回调函数方法
  //   //return true; //表示除了执行自定义的回调方法外，还会执行核心库方法
  // };
  meta2d.on('opened', opened);

  /**
   * 移除掉/ws/iot/scada地址的连接, 由iboot系统的websocket连接接管
   * @param json
   * @return {*}
   */
  function initJsonData(json) {
    let {networks, initJs} = json;
    // 移除调试地址
    if(networks instanceof Array && networks.length > 0) {
      let newNetworks = json['networks'] = []
      networks.forEach(item => {
        if(item['url'].indexOf("/ws/iot/scada") <= 0) {
          newNetworks.push(item);
        }
      })
    }

    // 设置是否有指定initJs
    if(initJs && initJs.indexOf("init_le5le_plugin") > 0) {
      meta2d['store'].initJs = true;
    }
    return json;
  }
  let params = proxy.$route.params;
  if(params && params['id'] && params['type']) {
    type = params['type'];
    proxy.$http.get(`/iot/screen/get?id=${params['id']}&type=${params['type']}`).then(({code, message, data}) => {
      if(code === CoreConsts.SuccessCode) {
        let datum = initJsonData(data['data']);
        meta2d.open(datum);
      } else {
        proxy.$msg.error(message);
      }
    })
  } else {
    console.error("访问路径错误");
  }
});

const opened = () =>{
  (globalThis).userId = meta2d.store.data.ownerId;
  (globalThis).registerIot((globalThis).userId,'/plugin/le5le/2d-components.js');
  if ((globalThis).meta2dTools && (globalThis).registerToolsNew) {
    (globalThis).registerToolsNew();
  }

  if(type == '2d') {
    meta2d.fitView(true, 0); //充满屏幕
  } else {
    meta2d.fitSizeView(true, 0); //大屏充满屏幕
  }
  if(!screenfull.isFullscreen && screenfull.isEnabled) {
    // window.
    // let elementById = document.getElementById("iv-meta2d-container");
    // debugger
    // screenfull.request(elementById);
    // screenfull.toggle(elementById)
    // let data1 = meta2d.data();
    // data1.width = 180;
    // debugger
  }

  // 初始化插件
  if(meta2d['store'].initJs !== true) {
    init_le5le_plugin(meta2d);
  }
}

//参考文档:https://doc.le5le.com/document/119754049
function registerBasicDiagram() {
  register(flowPens());
  registerAnchors(flowAnchors());
  register(activityDiagram());
  registerCanvasDraw(activityDiagramByCtx());
  register(classPens());
  register(sequencePens());
  registerCanvasDraw(sequencePensbyCtx());
  registerEcharts();
  registerHighcharts();
  registerLightningChart();
  register(formPath2DPens());
  registerCanvasDraw(formPens());
  registerCanvasDraw(chartsPens());
  register(ftaPens());
  registerCanvasDraw(ftaPensbyCtx());
  registerAnchors(ftaAnchors());
}


onUnmounted(() => {
  // meta2d?.off('opened',opened);
  meta2d?.destroy();
  if(webSocket != null) {
    webSocket.close(200);
  }
});
</script>

<style scoped>
#iv-meta2d-container {
  width: 100%;
  height: 100%;
  position: relative;
}
#meta2d {
  position: absolute !important;
  width: 100%;
  height: 100%;
  touch-action: none;
  overflow: hidden;
}
</style>
