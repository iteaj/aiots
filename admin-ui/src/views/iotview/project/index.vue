<template>
  <div style="padding: 8px 36px">
    <div style="margin: 0 0 16px; text-align: center;">
      <ASpace>
        <AInput v-model:value="searchModel.projectName" allow-clear placeholder="请输入名称"></AInput>
        <AButton type="primary" @click="addProject" :loading="loading">新增</AButton>
      </ASpace>
    </div>
    <ARow :gutter="12">
      <template v-for="item in listComputed">
        <ACol style="margin-bottom: 16px">
          <a-card style="width: 280px">
            <template #cover>
              <img :alt="item.projectName" :src="$apiUrl + item.indexImage" height="200px"/>
            </template>
            <template #actions>
              <a :href="`${getViewUrl() +'/#/chart/home/'+ item.id}`" target="_blank">
                <edit-outlined key="edit" /> 编辑
              </a>
              <a :href="`${getViewUrl() + '/#/chart/preview/' + item.id}`" target="_blank">
                <UIcon type="iz-icon-preview" style="font-size: 18px" /> 预览
              </a>
              <a @click="() => deleteDeploy(item)">
                <DeleteOutlined /> 删除
              </a>
            </template>
            <a-card-meta>
              <template #title>
                {{item.projectName}}
              </template>
              <template #description>
                <div>
                  <span style="color: #6d6d6d; font-weight: 100; font-size: 12px">创建时间</span>
                  {{getUpdateDate(item)}}
                </div>
              </template>
            </a-card-meta>
          </a-card>
        </ACol>
      </template>
    </ARow>
  </div>
</template>
<!--组态-->
<script>
import {ref, reactive} from "vue";
import screenfull from "screenfull";
import CoreConsts from "@/components/CoreConsts";
import {EyeFilled, StarFilled, EditOutlined, InboxOutlined, PlusOutlined, DeleteOutlined} from "@antdv-next/icons";
import {confirm} from "@/utils/message";
export default {
  name: "Scada",
  components: {StarFilled, EyeFilled, EditOutlined, InboxOutlined, PlusOutlined, DeleteOutlined},
  setup() {
    let path = ref("");
    let list = ref([]);
    let loading = ref(false);
    let searchModel = ref({type: '', projectName: null});
    return {searchModel, list, path, loading}
  },
  computed: {
    listComputed() {
      if(this.list && this.list.length > 0 && this.searchModel.projectName) {
        return this.list.filter(item => item.projectName.indexOf(this.searchModel.projectName) > -1);
      }

      return this.list;
    }
  },
  mounted() {
    this.search();
  },
  methods: {
    search() {
      this.$http.get("/isv/project/list", {params: this.searchModel}).then(({code, message, data}) => {
        if(code === CoreConsts.SuccessCode) {
          this.list = data.records;
        }
      })
    },
    getUpdateDate(item) {
      return item.createTime
    },
    addProject() {
      this.loading = true;
      let projectName = 'id_' + Number(Math.random().toString().substring(2, 8) + Date.now()).toString(36);
      this.$http.post("/isv/project/create", {remarks: null, projectName}).then(({code, msg, data}) => {
        if(code === CoreConsts.SuccessCode) {
          const { id } = data;
          window.open(this.getViewUrl() + '/#/chart/home/' + id, '_blank');
        } else {
          this.$msg.warn(msg);
        }
      }).finally(() => this.loading = false);
    },
    getViewUrl() {
      return this.$env.http.getViewUrl();
    },
    deleteDeploy(item) {
      confirm({title: '确认删除项目吗?', content: item.projectName, onOk: () => {
          return new Promise((resolve) => {
            this.$http.post(`/isv/project/delete?id=${item.id}`).then(({code, message}) => {
              if(code == CoreConsts.SuccessCode) {
                this.search();
                this.$msg.success(message);
              } else {
                this.$msg.error(message);
              }
            }).finally(() => resolve())
          })
        }
      })
    }
  }
}
</script>

<style scoped>

</style>