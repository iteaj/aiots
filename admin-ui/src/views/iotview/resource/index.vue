<template>
  <div style="width: 100%; height: 100%; background: #fff; padding: 16px 8px;" class="u-border-radius">
    <AFlex style="height: 100%" class="iv-flex">
      <ACard style="min-width: 226px;" :styles="{body: {padding: '6px'}}" >
        <template #title>
          <div style="text-align: center;">素材类型</div>
        </template>

        <AFlex vertical align="middle">
          <div :class="['iv-source-type-item', type == selectType ? 'active' : '']" v-for="type in resourceTypes"
               :key="type.type" @click="() => onSelectType(type)">
            {{type.name}}
          </div>
        </AFlex>
      </ACard>
      <ASpin :spinning="spinning" :description="tipText" :classes="{root: 'iv-spin'}">
        <ARow :gutter="10" style="width: 100%; height: calc(100% - 0px); overflow: auto; padding: 0 8px;">
          <ACol>
            <a-upload multiple v-model:file-list="fileList" list-type="picture-card" class="avatar-uploader"
                :show-upload-list="false" :before-upload="beforeUpload" accept="image/*">
              <plus-outlined style="font-size: 28px"></plus-outlined>
            </a-upload>
          </ACol>
          <template v-for="resource in resources" :key="resource.id">
            <ACol style="width: 200px; margin: 2px 0">
              <a-card :styles="{body: {padding: '3px'}}">
                <template #cover>
                  <img alt="example" style="object-fit: contain;" height="150" :src="$apiUrl + resource.url"/>
                </template>
                <template #actions>
                  <span style="color: orangered">
                    <a-popconfirm title="确认删除此素材?" ok-text="删除" cancel-text="取消"
                        @confirm="() => delImg(resource)">
                    <DeleteOutlined key="del" /> 删除
                    </a-popconfirm>
                  </span>
                  <span @click="() => editImg(resource)">
                    <EditOutlined key="edit"/> 编辑
                  </span>
                </template>
                <ACardMeta>
                  <template #title>
                    <div style="text-align: center; height: 32px; line-height: 32px;" class="iv-text-ellipsis">
                      <span v-if="!resource['_editable']">{{resource.name}}</span>
                      <AInput v-else v-model:value="resource.name" size="small" @blur="() => editBlur(resource)" />
                    </div>
                  </template>
                </ACardMeta>
              </a-card>
            </ACol>
          </template>
        </ARow>
      </ASpin>
    </AFlex>
  </div>
</template>
<!-- 大屏资源功能 -->
<script>
import {ref, watch} from "vue";
import CoreConsts from "@/components/CoreConsts";
import {DeleteOutlined, EditOutlined, PlusOutlined} from '@antdv-next/icons'
export default {
  name: "Resource",
  components: {DeleteOutlined, EditOutlined, PlusOutlined},
  setup() {
    let fileList = ref();
    let selectType = ref();
    let spinning = ref(false);
    let tipText = ref("数据加载...");
    let fileData = ref({});
    let resources = ref([]);
    let resourceTypes = ref([])
    return {resourceTypes, resources, selectType, fileList, fileData, spinning, tipText};
  },
  watch: {
    fileList(val) {
      if(val.length > 0){
        this.uploadChange();
      }
    }
  },
  mounted() {
    this.loadResourceType();
  },
  methods: {
    loadResources() {
      this.spinning = true;
      this.tipText = "数据加载中..."
      let type = this.selectType['type'];
      this.$http.get(`/isv/resource/view?type=${type}&current=1&size=10000`).then(({data, code, message}) => {
        if(code == CoreConsts.SuccessCode) {
          this.resources = data.records;
        }
      }).finally(() => this.spinning = false);
    },
    beforeUpload(info, a) {
      return false;
    },
    delImg(resource) {
      this.spinning = true;
      this.tipText = "删除素材中...";
      this.$http.post(`/isv/resource/del`, [resource.id]).then(({code, message}) => {
        if(code == CoreConsts.SuccessCode) {
          this.loadResources();
        } else {
          this.$msg.error(message)
        }
      }).finally(() => this.spinning = false);
    },
    editImg(resource) {
      resource['_editable'] = true;
    },
    editBlur(resource) {
      if(!resource.name) {
        return this.$msg.warning("名称必填");
      }

      this.spinning = true; this.tipText = "名称修改中...";
      this.$http.post('/isv/resource/saveOrUpdate', resource).then(({code, message}) => {
        if(code == CoreConsts.SuccessCode) {
          this.loadResources();
        } else {
          this.$msg.error(message)
        }
      }).finally(() => this.spinning = false);
      resource['_editable'] = false;
    },
    onSelectType(type) {
      this.selectType = type;
      this.loadResources();
    },
    uploadChange() {
      this.fileData['type'] = this.selectType['type'];
      if(!this.selectType['type']) {
        this.$msg.warning("未指定素材类型")
        return Promise.reject();
      }

      const formData = new FormData();
      formData.append('type', this.selectType['type']);
      this.fileList.forEach((item) => {
        formData.append('files', item.originFileObj);
      })
      this.spinning = true; this.tipText = "素材上传中...";
      this.$http.post(`/isv/resource/upload`, formData, {headers: {
          'Content-Type': 'multipart/form-data'
        }}).then(({code, message}) => {
        if(code == CoreConsts.SuccessCode) {
          this.loadResources();
        } else {
          this.$msg.error(message)
        }
      }).finally(() => {
        this.spinning = false;
        this.fileList = [];
      });
    },
    loadResourceType() {
      this.$http.get("/isv/resource/types").then(({code, data, message}) => {
        if(code == CoreConsts.SuccessCode) {
          this.resourceTypes = data;
          if(data && data.length > 0){
            this.selectType = data[0];
            this.loadResources();
          }
        }
      })
    }
  }
}
</script>
<style scoped>
.iv-source-type-item {
  height: 36px;
  line-height: 36px;
  cursor: pointer;
  margin: 2px 0;
  border-radius: 5px;
  background: rgba(248, 248, 248, 0.43);
  text-align: center;
}
.iv-source-type-item.active {
  background: rgba(0, 140, 255, 0.17);
}
.avatar-uploader :deep(.ant-upload-select-picture-card) {
  width: 190px!important;
  height: 229px!important;
}
.avatar-uploader :deep(.ant-upload.ant-upload-select) {
  margin-bottom: 0;
  margin-inline-end: 0;
}
.iv-spin {
  width: 100%;
  height: 100%;
}
.iv-flex :deep(.ant-spin-container) {
  height: 100%;
}
</style>
