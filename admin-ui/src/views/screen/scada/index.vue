<template>
  <div style="padding: 8px 16px">
    <a-tabs v-model:activeKey="activeKey" centered @change="tabChange">
      <template #rightExtra>
        <ASpace>
          <AInput v-model:value="searchModel.name" allow-clear placeholder="请输入名称"></AInput>
          <a-radio-group v-model:value="searchModel.type" @change="search" optionType="button" button-style="solid" :options="type" />
        </ASpace>
      </template>
      <a-tab-pane key="online">
        <template #tab>
        <span>
          <apple-outlined />在线列表
        </span>
        </template>
        <ARow :gutter="16">
          <template v-for="item in listComputed">
            <ACol style="margin-bottom: 16px">
              <a-card style="width: 300px">
                <template #cover>
                  <img alt="example" :src="item.image" height="200px"/>
                </template>
                <template #actions>
                  <a :href="`https://${item.type}.le5le.com/?id=${item.id}`" target="_blank">
                    <edit-outlined key="edit" /> 编辑
                  </a>
                  <span @click="() => previewHandle(item)">
                <RouterLink :to="{path: `/screen/${item.type}/${item.id}`}" target="_blank">
                  <UIcon type="iz-icon-preview" style="font-size: 18px" /> 预览
                </RouterLink>
              </span>
                </template>
                <a-card-meta>
                  <template #title>
                    <a :href="`https://${item.type}.le5le.com/?id=${item.id}`" target="_blank">{{item.name}}</a>
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
      </a-tab-pane>
      <a-tab-pane key="offline">
        <template #tab>
        <span>
          <android-outlined />离线安装
        </span>
        </template>
        <ARow :gutter="16">
          <ACol style="margin-bottom: 16px; cursor: pointer" @click="() => openDeploy({id: null, path: null})">
            <a-card style="width: 220px; background-color: #fefefe" :bodyStyle="{height: '300px'}">
              <div style="text-align: center; padding-top: 58px">
                <plus-outlined style="font-size: 120px; color: rgba(152,152,152,0.76)"></plus-outlined>
                <div class="ant-upload-text">点击安装</div>
              </div>
            </a-card>
          </ACol>
          <template v-for="item in installComputed">
            <ACol style="margin-bottom: 16px">
              <a-card style="width: 300px">
                <template #cover>
                  <img alt="example" :src="item.image" height="135px"/>
                </template>
                <template #actions>
                  <span @click="() => deleteDeploy(item)">
                    <DeleteOutlined key="delete" /> 删除
                  </span>
                  <span @click="() => previewHandle(item)">
                    <a :href="`${this.$env.http.getApiUrl()}/scada/${item.path}/view/index.html`" target="_blank">
                      <UIcon type="iz-icon-preview" style="font-size: 18px" /> 预览
                    </a>
                  </span>
                </template>
                <a-card-meta>
                  <template #title>
                    <div>
                      <div style="text-align: center">{{item.name}}</div>
                      <div style="color: #6d6d6d; font-weight: 100;">
                       <span style=" font-size: 12px">发布路径</span>
                        <span style="font-size: 13px; margin-left: 8px;">{{item.path}}</span>
                      </div>
                    </div>
                  </template>
                  <template #description>
                    <div style="color: #6d6d6d; font-weight: 100;">
                      <span style="font-size: 12px">发布时间</span>
                      <span style="font-size: 13px; margin-left: 8px;">{{getUpdateDate(item)}}</span>
                    </div>
                  </template>
                </a-card-meta>
              </a-card>
            </ACol>
          </template>
        </ARow>
      </a-tab-pane>
    </a-tabs>

    <AModal v-model:open="active" title="2d/大屏/组态离线安装" width="620px" >
      <div style="margin: 24px 36px;">
        <AFormItem label="路径" name="path" extra="用于设置图纸访问路径, 只支持字母或者数字且以字母开头">
          <AInput v-model:value="this.editModel.path"></AInput>
        </AFormItem>
        <a-upload-dragger v-model:fileList="fileList" name="file" accept=".zip"
            @change="handleChange" :beforeUpload="beforeUpload" :maxCount="1" >
          <p class="ant-upload-drag-icon">
            <inbox-outlined></inbox-outlined>
          </p>
          <p class="ant-upload-text">点击或者拖拽文件到这里进行上传</p>
          <p class="ant-upload-hint">
            只支持乐吾乐的离线部署包(.zip), 在乐吾乐编辑器中打开要安装的图纸 -> 点击文件 -> 点击下载离线部署包
          </p>
        </a-upload-dragger>
      </div>

      <template #footer>
        <div style="text-align: center">
          <AButton type="primary" @click="submit">部署</AButton>
        </div>
      </template>
    </AModal>
  </div>
</template>
<!--组态-->
<script>
import {ref, reactive} from "vue";
import screenfull from "screenfull";
import CoreConsts from "@/components/CoreConsts";
import {EyeFilled, StarFilled, EditOutlined, InboxOutlined, PlusOutlined, DeleteOutlined} from "@ant-design/icons-vue";
import {confirm} from "@/utils/message";
export default {
  name: "Scada",
  components: {StarFilled, EyeFilled, EditOutlined, InboxOutlined, PlusOutlined, DeleteOutlined},
  setup() {
    let type = ref([
      {label: '全部', value: ''},
      {label: '大屏/组态', value: 'v'},
      {label: '2D图纸', value: '2d'},
    ]);
    let path = ref("");
    let active = ref(false);
    let list = ref([])
    let installList = ref([])
    let activeKey = ref("online");
    let fileList = ref([]);
    let searchModel = ref({type: '', name: null});
    let editModel = ref({id: null, path: null});
    return {editModel, searchModel, list, type, active, path, activeKey, fileList, installList}
  },
  computed: {
    listComputed() {
      if(this.list && this.list.length > 0 && this.searchModel.name) {
        return this.list.filter(item => item.name.indexOf(this.searchModel.name) > -1);
      }

      return this.list;
    },
    installComputed() {
      if(this.installList && this.installList.length > 0 && this.searchModel.name) {
        return this.installList.filter(item => item.name.indexOf(this.searchModel.name) > -1);
      }

      return this.installList;
    }
  },
  mounted() {
    this.search();
  },
  methods: {
    search() {
      if(this.activeKey == 'online') {
        this.$http.get("/iot/screen/list", {params: this.searchModel}).then(({code, message, data}) => {
          if(code == CoreConsts.SuccessCode) {
            this.list = data;
          }
        })
      } else {
        this.$http.get("/iot/screen/datasources", {params: this.searchModel}).then(({code, message, data}) => {
          if(code == CoreConsts.SuccessCode) {
            this.installList = data;
          }
        })
      }
    },
    getUpdateDate(item) {
      let date = item['createdAt'];
      if(date) {
        if(date.length > 20) {
          return date.substring(0, 19).replace('T', ' ');
        } else {
          return date;
        }
      }
    },
    syncDrawing(item) {
      this.$http.get(`/iot/screen/sync?id=${item['id']}&type=${this.searchModel.type}`).then(({code, message}) => {
        if(code != CoreConsts.SuccessCode) {
          this.$msg.error(message);
        } else {
          this.$msg.success(message);
        }
      })
    },
    openDeploy(item) {
      this.active = true;
      this.editModel = item;
    },
    beforeUpload(file) {
      return false;
    },
    handleChange(file) {

    },
    tabChange(activeKey) {
      this.search();
    },
    deleteDeploy(item) {
      confirm({title: '确认删除吗', content: '删除部署包后将不能恢复', onOk: () => {
          return new Promise((resolve) => {
            this.$http.post(`/iot/screen/delDatasource`, [item.id]).then(({code, message}) => {
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
    },
    submit() {
      if(!this.editModel.path) {
        return this.$msg.warn("路径必填");
      }
      if(this.fileList && this.fileList.length == 0) {
        return this.$msg.warn("请选择要安装的离线部署包(.zip)");
      }
      const regex = /^[a-zA-Z][a-zA-Z0-9]{0,15}$/;
      if(!regex.test(this.editModel.path)) {
        return this.$msg.warn("路径只支持字母和数字的组合且以字母开头长度在16以内");
      }

      // 创建 FormData 对象
      const formData = new FormData();
      if(this.fileList && this.fileList.length == 1) {
        // 将文件添加到 FormData 中
        formData.append('file', this.fileList[0].originFileObj);
      }

      formData.append("path", this.editModel.path)
      if(this.editModel.id) {
        formData.append("id", this.editModel.id)
      }

      this.$http.post("/iot/screen/offlineInstall", formData, {
        headers: {
          'Content-Type': 'multipart/form-data'
        }
      }).then(({message, code, data}) => {
        if(code == CoreConsts.SuccessCode) {
          this.search();
          this.active = false;
          this.$msg.success(message);
        } else {
          this.$msg.error(message);
        }
      })
    },
    previewHandle(item) {
      // screenfull.onchange(e => {
      //   if(screenfull.isFullscreen) {
      //     // window.history.h
      //     window.open('https://www.baidu.com', '_blank');
      //   }
      // })
    }
  }
}
</script>

<style scoped>

</style>