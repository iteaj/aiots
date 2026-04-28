<template>
  <UView name="产品数据">
    <UViewSearch v-model="searchModel">
      <URow col="search">
        <USelectItem field="productId" label="所属产品" labelField="name" valueField="id"
                     url="/iot/product/listByNonGateway" @loaded="productLoaded" @change="productChange"/>
        <UInputItem field="deviceSn" label="设备编号"/>
        <UButton func="query" url="/iot/collectData/product" ref="searchRef">搜索</UButton>
      </URow>
    </UViewSearch>
    <UViewTable :columns="columns" :scroll="{x: 1100}">
      <template #title>
        <span style="color: #d46b08;">功能说明: 产品数据是将模型数据按产品的模型属性合并成一行(按同一采集时间)</span>
      </template>
    </UViewTable>
  </UView>
</template>
<!-- 设备功能 -->
<script>
import {ref} from "vue";
import CoreConsts from "@/components/CoreConsts";
export default {
  name: "ProductData",
  setup() {
    let columns = ref([
      {field: "deviceName", title: "设备名称", width: 120, ellipsis: true},
      {field: "deviceSn", title: "设备编号", width: 120, ellipsis: true},
      {field: "collectTime", title: "采集时间", width: 150, type: 'date', format: 'YY-MM-DD HH:mm:ss', fixed: 'right'},
    ]);

    // 只显示点位采集的数据
    let searchModel = ref({collectMode: 'model'});
    return {columns, searchModel}
  },
  beforeRouteEnter(to, form, next) {
    next(vm => {
      if(to.query.taskId) {
        vm.searchModel['collectTaskId'] = to.query.taskId;
      }
    })
  },
  methods: {
    selectParent(selectedKeys) {
      this.searchModel.deviceTypeId = selectedKeys[0]
      this.$refs['searchRef'].trigger();
    },
    columnFormatter({column, record}) {
      let value = record.value, result;
      if(value) {
        let split = value.split(':::');
        if(split && split.length > 0) {
          for(let item of split) {
            if(item.startsWith(column.field)) {
              result = item.substring(column.field.length + 1);
              break
            }
          }
        }
      }

      return result;
    },
    loadModelAttr(productId) {
      this.$http.get(`/iot/modelAttr/list?productId=${productId}`).then(({code, message, data}) => {
        if(code === CoreConsts.SuccessCode) {
          if(data instanceof Array) {
            let columns = data.filter(item => item.dataType !== 'json' && item.dataType !== 'bit').map(item => {
              let formatter = this.columnFormatter;
              let title = item.unit ? `${item.name}(${item.unit})` : item.name;
              return {field: item.field, title, width: 100, unit: item.unit, formatter, resizable: true, ellipsis: true}
            })

            this.columns = [
              {field: "deviceName", title: "设备名称", width: 120, ellipsis: true, fixed: 'left'},
              {field: "deviceSn", title: "设备编号", width: 120, ellipsis: true, fixed: 'left'}
              , ...columns,
              {field: "collectTime", title: "采集时间", width: 150, type: 'date', format: 'YY-MM-DD HH:mm:ss', fixed: 'right'}
            ]
          }
          this.$refs['searchRef'].trigger();
        } else {
          this.$msg.error(message);
        }
      })
    },
    productLoaded(list) {
      if(typeof list == 'object') {
        if(!this.searchModel['productId']) {
          this.searchModel['productId'] = Object.values(list)[0].id;
          this.loadModelAttr(this.searchModel.productId);
        }
      }
    },
    productChange(value) {
      this.searchModel['productId'] = value;
      this.loadModelAttr(this.searchModel.productId);
    },
  }
}
</script>
<style scoped> </style>
