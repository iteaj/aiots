<template>
  <VAceEditor theme="monokai" :value="value" @update:value="valueChange" :lang="lang" :options="options" ref="editor" @change="onChange" style="width: 100%; height: 100%"></VAceEditor>
</template>

<script>
import {ref, watch} from "vue";
import {VAceEditor} from 'vue3-ace-editor'
import 'ace-builds/esm-resolver';
import 'ace-builds/src-noconflict/mode-javascript';
import 'ace-builds/src-noconflict/theme-monokai';
import 'ace-builds/src-noconflict/ext-language_tools'; // 引入代码提示扩展
export default {
  name: "AceScriptEditor",
  components: {VAceEditor},
  props: {
    value: String,
    completions: Array,
    lang: {type: String, default: 'javascript'}
  },
  emits: ['update:value'],
  setup(props, {emit}) {
    let editor = ref();
    let options = ref({
      fontSize: 14,
      showPrintMargin: false,
      enableBasicAutocompletion: true,
      enableSnippets: true,
      enableLiveAutocompletion: true,
      // 自定义关键字或规则等...
    });
    let session = ref();
    return {options, editor, session}
  },
  mounted() {
    let _this = this;
    let instance = this.editor.getAceInstance();
    this.session = instance.getSession();
    if(this.completions) {
      instance.completers.push({
        id: 'IvzoneCompletions',
        getCompletions: (editor, session, pos, prefix, callback) => {
          if(session.id == _this.session.id) {
            callback(null, this.completions);
          } else {
            callback(null, []);
          }
        }
      });
    }
  },
  methods: {
    valueChange(value) {
      this.$emit('update:value', value);
    },
    onChange(value, b) {

    }
  }
}
</script>

<style>

</style>