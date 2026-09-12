<template>
  <UView class="iv-view-border">
      <UTabs uid="script" v-model:activeKey="typeKey" centered>
        <template v-for="(item) in filters" :key="item.name">
          <u-tab-pane :tab="item.name">
            <ASpin description="数据保存中..." :spinning="spinning">
              <UScript v-model:value="flatScriptMap[item.name]" :label="item.name + '过滤脚本'" :completions="item['completes']" style="height: 450px"></UScript>
              <div style="text-align: center; margin-top: 8px;">
                <URadioGroup v-model:value="item['language']" button-style="solid">
                  <template v-for="language in item['script']" :key="language.language">
                    <ARadioButton :value="language.language">{{language.language}}</ARadioButton>
                  </template>
                  <ARadioButton value="disabled">禁用</ARadioButton>
                </URadioGroup>
                <UButton type="dashed" func="submit" style="margin-left: 8px" @click="() => submit(item)">保存</UButton>
              </div>
            </ASpin>
          </u-tab-pane>
        </template>
        <u-tab-pane tab="运行日志" key="logger">
          <URunningLogger style="min-height: 490px; max-height: 640px; padding: 8px;"/>
        </u-tab-pane>
      </UTabs>
  </UView>
</template>
<!-- 脚本引擎功能 -->
<script>
import {ref, watch} from "vue";
import CoreConsts from "@/components/CoreConsts";
import AceScriptEditor from "@/components/code/AceScriptEditor.vue";
export default {
  name: "FilterScript",
  components: {AceScriptEditor},
  setup() {
    let spinning = ref(false);
    let typeKey = ref("");
    let languageKey = ref("");
    let filters = ref([]);
    let flatScriptMap = ref({});
    watch(() => flatScriptMap.value, (newVal) => {
      if(typeof newVal === 'object') {
        filters.value.forEach(item => {
          let script = newVal[item.name];
          if(item.script?.[0]) {
            item.script[0].script = script;
          }
        })
      }

    }, {deep: true})
    return {typeKey, filters, languageKey, spinning, flatScriptMap}
  },
  mounted() {
    this.$http.get("/iot/script/filter/filters").then(({code, message, data}) => {
      if(code == CoreConsts.SuccessCode) {
        this.filters = data;
        this.filters.forEach(item => {
          this.flatScriptMap[item.name] = item.script?.[0]?.script;
        })
        if(data && data.length > 0) {
          this.typeKey = data[0].name;
          this.languageKey = data[0]['script']?.[0]?.language;
        }
      } else {
        this.$msg.error(message);
      }
    })
  },
  methods: {
    submit(item) {
      this.spinning = true;
      this.$http.post("/iot/script/saveOrUpdate", item).then(({code, message, data}) => {
        if(code != CoreConsts.SuccessCode) {
          this.$msg.error(message);
        }
      }).finally(() => this.spinning = false)
    }
  }
}
</script>
<style scoped> </style>
