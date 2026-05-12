<template>
  <AFlex class="control-wrapper" vertical>
    <AFlex gap="16">
      <div class="iv-ctrl-item">
        <div class="inner">
          <div class="iv-control-btn control-top" @mousedown="ptzCamera('up')" @mouseup="ptzCamera('stop')">
            <CaretUpOutlined />
            <div class="control-inner-btn control-inner"></div>
          </div>
          <div class="iv-control-btn control-left" @mousedown="ptzCamera('left')" @mouseup="ptzCamera('stop')">
            <CaretLeftOutlined />
            <div class="control-inner-btn control-inner"></div>
          </div>
          <div class="iv-control-btn control-bottom" @mousedown="ptzCamera('down')" @mouseup="ptzCamera('stop')">
            <CaretDownOutlined />
            <div class="control-inner-btn control-inner"></div>
          </div>
          <div class="iv-control-btn control-right" @mousedown="ptzCamera('right')" @mouseup="ptzCamera('stop')">
            <CaretRightOutlined />
            <div class="control-inner-btn control-inner"></div>
          </div>
          <div class="control-round">
            <div class="control-round-inner" :class="[voice ? 'enabled' : 'disabled', speakStatus + '']" title="语音对讲">
              <UIcon v-if="voice" type="iz-icon-voice" style="font-size: 28px;" @click="voiceHandle" />
              <UIcon v-else type="iz-icon-voice" style="font-size: 28px; color: #f85b40" />
            </div>
          </div>
        </div>
      </div>
      <div class="iv-ctrl-zoom" v-if="zoom">
        <div style="padding: 28px 5px 8px; cursor: pointer" @mousedown="ptzCamera('zoomin')" @mouseup="ptzCamera('stop')" title="放大">
          <ZoomInOutlined style="font-size: 22px; color: #78aee4" />
        </div>
        <div style="padding: 8px 5px; cursor: pointer" @mousedown="ptzCamera('zoomout')" @mouseup="ptzCamera('stop')" title="缩小">
          <ZoomOutOutlined style="font-size: 22px; color: #78aee4" />
        </div>
      </div>
    </AFlex>
    <div class="iv-ctrl-speed" title="控制速度">
      <ASlider v-model:value="ctrlSpeed" :max="255" :marks="marks"></ASlider>
    </div>
  </AFlex>
</template>
<!--云控制台组件-->
<script>
import {CaretUpOutlined, ZoomInOutlined, ZoomOutOutlined, CaretDownOutlined, CaretLeftOutlined, CaretRightOutlined} from "@antdv-next/icons";
import {reactive, ref} from "vue";

export default {
  name: "PTZ",
  props: {
    zoom: {type: Boolean, default: true}, // 控制缩放
    voice: {type: Boolean, default: true}, // 启用语音
  },
  components: {CaretUpOutlined, ZoomInOutlined, ZoomOutOutlined, CaretDownOutlined, CaretLeftOutlined, CaretRightOutlined},
  setup() {
    let speakStatus = ref(false);
    let ctrlSpeed = ref(127);
    let marks =reactive({
      0: '0',
      127: '127',
      255: '255'
    });
    return {marks, ctrlSpeed, speakStatus}
  },
  methods: {
    voiceHandle() {
      /**
       * voiceStatus 当前对讲状态
       */
      this.$emit("voice", this.speakStatus);
    },
    ptzCamera(command) {
      this.$emit("ctrl", {command, speed: this.ctrlSpeed})
    },
    setSpeakStatus(status) {
      this.speakStatus = status;
    }
  }
}
</script>

<style scoped>
.control-wrapper {
  width: 150px;
  height: 100px;
  position: relative;
}
.iv-ctrl-zoom {
  width: 38px;
}
.control-wrapper .iv-ctrl-item .inner {
  width: 6.3rem;
  height: 6.3rem;
  position: relative;
  border-radius: 100%;
  background: #ffffff;
  transition: all 0.2s linear;
}
.iv-control-btn {
  display: flex;
  justify-content: center;
  position: absolute;
  width: 44%;
  height: 44%;
  border-radius: 5px;
  box-sizing: border-box;
  border: 1px solid #78aee4;
  /*transition: all 0.3s linear;*/
  transition: all 0.3s ease-out;
}

.iv-control-btn:hover, .control-round-inner.enabled:hover {
  cursor:pointer;
  background-color: rgba(120, 174, 228, 0.12);
}
.iv-control-btn:active, .control-round-inner:active {
  background-color: #ffffff;
}
.iv-control-btn span {
  font-size: 20px;
  color: #78aee4;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s linear;
}
.iv-control-btn span:hover {
  cursor:pointer
}
.control-inner-btn {
  position: absolute;
  width: 60%;
  height: 60%;
  display: block;
  transition: all 0.3s linear;
}

.control-top {
  top: -8%;
  left: 27%;
  transform: rotate(-45deg);
  border-radius: 5px 100% 5px 0;
}

.control-top span {
  transform: rotate(45deg);
  border-radius: 5px 100% 5px 0;
}

.control-top .control-inner {
  left: -1px;
  bottom: 0;
  border-top: 1px solid #78aee4;
  border-right: 1px solid #78aee4;
  border-radius: 0 100% 0 0;
}

.control-top .fa {
  transform: rotate(45deg) translateY(-7px);
}

.control-left {
  top: 27%;
  left: -8%;
  transform: rotate(45deg);
  border-radius: 5px 0 5px 100%;
}

.control-left span {
  transform: rotate(-45deg);
}

.control-left .control-inner {
  right: -1px;
  top: -1px;
  border-bottom: 1px solid #78aee4;
  border-left: 1px solid #78aee4;
  border-radius: 0 0 0 100%;
}

.control-left .fa {
  transform: rotate(-45deg) translateX(-7px);
}

.control-right {
  top: 27%;
  right: -8%;
  transform: rotate(45deg);
  border-radius: 5px 100% 5px 0;
}

.control-right span {
  transform: rotate(-45deg);
}

.control-right .control-inner {
  left: -1px;
  bottom: -1px;
  border-top: 1px solid #78aee4;
  border-right: 1px solid #78aee4;
  border-radius: 0 100% 0 0;
}

.control-right .fa {
  transform: rotate(-45deg) translateX(7px);
}

.control-bottom {
  left: 27%;
  bottom: -8%;
  transform: rotate(45deg);
  border-radius: 0 5px 100% 5px;
}

.control-bottom span {
  transform: rotate(-45deg);
}

.control-bottom .control-inner {
  top: -1px;
  left: -1px;
  border-bottom: 1px solid #78aee4;
  border-right: 1px solid #78aee4;
  border-radius: 0 0 100% 0;
}

.control-bottom .fa {
  transform: rotate(-45deg) translateY(7px);
}
.control-round {
  position: absolute;
  top: 21%;
  left: 21%;
  width: 58%;
  height: 58%;
  background: #fff;
  border-radius: 100%;
}

.control-round-inner {
  position: absolute;
  left: 16%;
  top: 16%;
  display: flex;
  justify-content: center;
  align-items: center;
  width: 70%;
  height: 70%;
  font-size: 40px;
  color: #a3a3a3;
  border: 1px solid #a3a3a3;
  border-radius: 100%;
  cursor: pointer;
  transition: all 0.3s linear;
}
.control-round-inner.disabled {
  cursor: not-allowed;
}
.control-round-inner.true {
  color: #01d26d;
  border: 1px solid #01d26d;
  background-color: rgba(11, 250, 139, 0.13);
}
</style>