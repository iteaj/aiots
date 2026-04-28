<template>
  <div class="iv-view-border">
      <ATabs v-model:activeKey="typeKey" centered animated>
        <template v-for="(item, index) in filters" :key="item.name">
          <a-tab-pane :tab="item.name">
            <ASpin tip="数据保存中..." :spinning="spinning">
              <ATabs v-model:activeKey="languageKey" tab-position="left">
                <template v-for="language in item['script']" :key="language.language">
                  <a-tab-pane :tab="language.language">
                    <AceScriptEditor v-model:value="language.script" :completions="item['completes']" style="height: 420px"></AceScriptEditor>
                  </a-tab-pane>
                </template>
              </ATabs>
              <div style="text-align: center; margin-top: 8px;">
                <ARadioGroup v-model:value="item['language']" button-style="solid">
                  <template v-for="language in item['script']" :key="language.language">
                    <ARadioButton :value="language.language">{{language.language}}</ARadioButton>
                  </template>
                  <ARadioButton :value="null">禁用</ARadioButton>
                </ARadioGroup>
                <AButton type="dashed" style="margin-left: 8px" @click="() => submit(item)">保存</AButton>
              </div>
            </ASpin>
          </a-tab-pane>
        </template>
        <a-tab-pane tab="运行日志" key="logger">
          <URunningLogger style="min-height: 560px; max-height: 640px; padding: 8px;"/>
        </a-tab-pane>
      </ATabs>
  </div>
</template>
<!-- 脚本引擎功能 -->
<script>
import {ref, reactive} from "vue";
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
    return {typeKey, filters, languageKey, spinning}
  },
  mounted() {
    this.$http.get("/iot/script/filter/filters").then(({code, message, data}) => {
      if(code == CoreConsts.SuccessCode) {
        this.filters = data;
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
