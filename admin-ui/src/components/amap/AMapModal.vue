<template>
  <AModal :closable="false" :mask-closable="false" destroyOnHidden :width="width" :zIndex="1005">
    <template #title>
      <AFlex justify="space-between" style="font-weight: 200">
        <div>
          {{selectPosition.formattedAddress}}
        </div>
        <div>
          关键词
          <AAutoComplete @change="triggerSearch" size="small" style="width: 180px; margin-right: 8px"/>
        </div>
      </AFlex>
    </template>
    <div id="modal-container">
      <AMap id="modal-container" :place-search="{}" :geocoder="{}" :center="center" ref="instance"
            @click="getPosition" @complete="loadingComplete" :zoom="zoom">
        <slot :position="selectPosition.position"
              :address="selectPosition.formattedAddress" :info="selectPosition.address">
          <AMarker v-if="selectPosition.position" title="点击选中地址" :position="selectPosition.position"></AMarker>
        </slot>
      </AMap>
    </div>
    <template #footer>
      <div style="text-align:center">
        <AButton type="primary" @click="selectMarker">关闭</AButton>
        <AButton @click="clearMarker">重置</AButton>
      </div>
    </template>
  </AModal>
</template>

<script>
import AMap from "@/components/amap/AMap.vue";
import {ref, watch} from "vue";
export default {
  name: "AMapModal",
  components: {AMap},
  props: {
    width: {default: '680px'},
    center: {type: Array}, // 中心点
    zoom: {type: Number, default: 10},
    address: {type: String},
    onChange: {type: Function, default: null},
  },
  setup(props, {attrs}) {
    let instance = ref();
    let selectPosition = ref({
      address: null,
      position: props.center,
      formattedAddress: props.address,
    });
    watch(() => props.center, (value) => {
      selectPosition.value.position = value;
    })
    // let markers = ref([])
    return {instance, selectPosition}
  },
  updated() {
    if(this.center && this.instance) {
      this.instance.setFitView();
    }
  },
  methods: {
    triggerSearch(value) {
      if(value) {
        this.instance.startPlaceSearch(value, (result, data) => {})
      } else {
        this.instance.removeAllOverlay();
      }
    },
    selectMarker() {
      this.$emit("update:open", false)
    },
    clearMarker() {
      this.selectPosition.position = null;
      this.selectPosition.formattedAddress = null;
      this.instance.removeAllOverlay();
      this.$emit("change", { position: this.selectPosition.position, address: this.selectPosition.formattedAddress });
    },
    // clickMarker() {
    //   this.$emit("change", this.selectPosition.position, this.selectPosition.formattedAddress, this.selectPosition.address);
    // },
    getPosition(e) {
      this.selectPosition.position = [e.lnglat.lng, e.lnglat.lat];
      this.instance.getGeoCoder(e.lnglat, result => {
        if(result.info == 'OK') {
          this.selectPosition.address = result.regeocode.addressComponent;
          this.selectPosition.formattedAddress = result.regeocode.formattedAddress;
          this.$emit("change", {address: this.selectPosition.formattedAddress, position: this.selectPosition.position});
          // this.$emit("geo", {e, instance: this.instance, position: this.selectPosition.position, geo: result.regeocode});
        } else {
          // console.warn(`转换地址失败[${result}]`)
          // this.$emit("geo", {e, instance: this.instance, position: this.selectPosition.position, geo: null});
          this.selectPosition.formattedAddress = null;
          this.$emit("change", {address: this.selectPosition.formattedAddress, position: this.selectPosition.position});
        }
      })
    },

    loadingComplete() {
      this.$emit("complete", this.instance);
      if(this.center) {
        this.instance.setFitView();
      }
    }
  }
}
</script>

<style scoped>
#modal-container {
  width: 100%;
  height: 360px;
}
</style>