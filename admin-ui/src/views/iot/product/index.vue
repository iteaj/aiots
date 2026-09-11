<template>
  <UView name="产品" class="iv-product">
      <UViewSearch v-model="searchModel">
        <URow col="search">
          <UInputItem field="name" label="产品名称" :allowClear="true" :maxlength="20"/>
          <UInputItem field="code" label="产品代码" :allowClear="true" :maxlength="36"/>
          <UTreeSelectItem field="productTypeId" label="产品类型" url="/iot/productType/tree"
                           labelField="name" valueField="id" treeDefaultExpandAll allowClear/>
          <USelectItem field="deviceTypes" label="设备类型" mode="multiple" :defaultValue="['Child', 'Direct']" url="/iot/product/deviceTypes" allowClear />
          <USelectItem field="status" label="产品状态" :allowClear="true" :options="statusOptions"/>
          <UButton func="query" url="/iot/product/view" ref="searchRef">搜索</UButton>
          <UButton func="reset">重置</UButton>
          <UButton func="add" url="/iot/product/add" v-auth="'iot:product:add'">新增</UButton>
        </URow>
      </UViewSearch>
      <UViewTable :columns="columns" :scroll="{x: 1200}">
        <template #status="{record, text, value}">
          <ABadge v-if="text=='enabled'" status="processing" color="#87d068" :text="value" />
          <ABadge v-else status="warning" :text="value" />
        </template>
        <template #action="{record}">
          <UTag func="ajax:confirm" :data="record" :url="`/iot/product/switch/${record['id']}/${record['status'] == 'enabled' ? 'disabled' : 'enabled'}`"
                v-auth="'iot:product:switch'" :config="{confirmTitle: `确认${record['status'] == 'enabled' ? '停用' : '启用'}产品[${record.name}]`, confirmContent: `将启用/停用此产品下的所有设备`}">
            {{record['status'] == 'enabled' ? '停用' : '启用'}}
          </UTag>
          <UTag func="edit" :data="record" url="/iot/product/edit" v-auth="'iot:product:edit'">修改</UTag>
          <UTag func="open" uid="model" :data="record" v-auth="'iot:product:model'">物模型</UTag>
          <UTag func="open" uid="batch" :data="record" v-auth="'iot:product:model'"
                :disabled="record['ctrlMode'] != 'POINT' || record['deviceType'] == 'Gateway'" @click="(config) => batchModel(config)">批量</UTag>
          <UTag func="del" :data="record" url="/iot/product/del" v-auth="'iot:product:del'">删除</UTag>
        </template>
      </UViewTable>
    <UDrawerView uid="model" :title="'物模型配置['+modelDetailModel['code']+']'" placement="bottom" size="100%" v-model:source="modelDetailModel" :push="false" @close="modelClose">
      <template #extra>
        <span style="color: #fc804a; font-size: 13px;">所有修改将在重启产品后生效</span>
<!--        <UButton size="small" v-auth="'iot:product:switch'" :url="`/iot/product/switch/${modelDetailModel['id']}/disabled`" danger-->
<!--                func="ajax:confirm" type="primary" :config="{confirmTitle: `确认停用产品[${modelDetailModel.name}]`, confirmContent: `将停用此产品下的所有设备`}">-->
<!--          停用-->
<!--        </UButton>-->
      </template>
      <ThingModel :model-detail-model="modelDetailModel" />
    </UDrawerView>
    <UViewModal title="产品管理" :span="[6, 16]" v-model="editModel" :rules="rules" @edit="editOpen">
      <template #default="{model}">
        <UInputItem field="name" label="产品名称" :maxlength="20" />
        <UTreeSelectItem field="productTypeId" label="所属类型" url="/iot/productType/tree" treeDefaultExpandAll labelField="name" valueField="id" />
        <UInputItem field="code" label="产品代码" :maxlength="36" :disabled="model['id'] != null"/>
        <URadioItem field="deviceType" label="设备类型" url="/iot/product/deviceTypes" defaultValue="Child" optionType="button" buttonStyle="solid"/>
        <USelectItem v-if="model['deviceType'] == 'Child'" field="parentId" label="父产品" @change="parentChange"
                     url="/iot/product/parent" required labelField="name" valueField="id" />
        <USelectItem field="gatewayId" label="网络组件" url="/iot/gateway/list?type={deviceType}" v-show="model['deviceType'] != 'Child'"
                     labelField="name" valueField="id" @change="gatewayChange" extra="切换网关将会删除所有物模型数据(谨慎)"/>
<!--        <UInputItem field="logo" label="logo地址" />-->
        <UInputItem field="remark" label="产品说明" :maxlength="64" />
      </template>
      <template #footer>
        <UButton func="cancel">取消</UButton>
        <UButton func="submit" url="/iot/product/saveOrUpdate">提交</UButton>
        <UButton func="reset">重置</UButton>
      </template>
    </UViewModal>
    <UDrawerView uid="batch" :title="`批量物模型配置(${modelDetailModel.name})`" placement="bottom" size="100%"
             :styles="{body: {padding: '6px'}}" :getContainer="null">
<!--      <UChildView uid="batchModel">-->
        <UViewSearch style="margin-bottom: 0;" v-model="batchSearchModel">
          <UInputItem label="属性名称" field="name"/>
          <UInputItem label="属性字段" field="field"/>
          <USelectItem label="频率标签" field="collectTag" :options="collectTagOptions" allowClear style="width: 180px"/>
          <UButton func="query" url="/iot/product/listBatchModel" ref="batchModelQueryRef">查询</UButton>
          <UButton func="add">新增</UButton>
        </UViewSearch>
        <UViewEditable :columns="batchColumns" :pagination="false" size="small" :scroll="{x: 1200, y:500}"
                       @edit="onBatchEditModel" position="top" :virtual="true">
          <template #action="{record, editing}">
            <UTag func="edit" :data="record">修改</UTag>
            <UTag func="cancel" :data="record">取消</UTag>
            <UTag func="submit" :data="record" url="/iot/product/batchModelSaveOrUpdate" :params="batchSubmitValidate">保存</UTag>
            <UTag func="del" :data="record" :params="{id: record.id, apiCode: record.apiCode}" url="/iot/product/batchModelDel">删除</UTag>
          </template>
        </UViewEditable>
<!--      </UChildView>-->
    </UDrawerView>
  </UView>
</template>
<!-- 产品功能 -->
<script>
import {ref, reactive, watch, watchEffect, computed, h, shallowRef} from "vue";
import CoreConsts from "@/components/CoreConsts";
import {useRoute} from "vue-router";
import {GET} from "@/utils/request";
import {QuestionCircleOutlined} from "@antdv-next/icons";
import {USelect} from "@/components/form/basic";
import {Input, InputNumber} from "antdv-next";
import {getMetaValue, setMetaValue} from "@/utils/MetaUtils";
import AceScriptEditor from "@/components/code/AceScriptEditor.vue";
import ThingModel from "@msn/iot/product/ThingModel.vue";
export default {
  name: "Product",
  components: {ThingModel, AceScriptEditor, QuestionCircleOutlined},
  setup() {
    let route = useRoute();
    let batchModelQueryRef = ref(null);
    let statusOptions = [
      {label: '启用', value: 'enabled'},
      {label: '停用', value: 'disabled'},
    ]
    let columns = ref([
      {field: "name", title: "产品名称", resizable: true, width: 180},
      {field: "code", title: "产品代码", resizable: true, width: 180},
      {field: "gatewayName", title: "网络组件", width: 180, resizable: true},
      {field: "protocolName", title: "协议驱动", width: 180, resizable: true},
      {field: "deviceTypeName", title: "设备类型", width: 100},
      {field: "typeName", title: "产品类型", width: 120},
      {field: "remark", title: "产品说明", width: 320, resizable: true},
      {field: "createTime", title: "创建时间", type: 'date', format: 'YYYY-MM-DD', width: 130},
      {field: "status", title: "产品状态", width: 80, fixed: 'right', options: statusOptions},
      {field: 'action', title: '操作', type: 'action', fixed: 'right', width: 270},
    ]);

    let rules = reactive({
      name: {required: true, message: '产品名称必填'},
      code: [{required: true, message: '产品代码必填'}, {pattern: /^[a-zA-Z][a-zA-Z0-9-_]{5,35}$/, message: '只能用6位以上字符[a-zA-Z0-9-_]且以字母开头'}],
      deviceType: {required: true, message: '设备类型必填'},
      productTypeId: {required: true, message: '产品类型必填'},
      gatewayId: {required: true, message: '所属网关必填'},
      protocolId: {required: true, message: '所属协议必填'},
    })
    let dataType = ref([
      {label: 'string(字符串)', value: 'string'},
      {label: 'boolean(布尔类型)', value: 'boolean'},
      {label: 'byte(字节)', value: 'byte'},
      {label: 'short(短整型)', value: 'short'},
      // {label: 'ushort(无符号短整型)', value: 'ushort'},
      {label: 'int(整型)', value: 'int'},
      // {label: 'uint(无符号整型)', value: 'uint'},
      {label: 'long(长整型)', value: 'long'},
      {label: 'float(单精度)', value: 'float'},
      {label: 'double(双精度)', value: 'double'},
      {label: 'date(日期)', value: 'date'},
      {label: 'bit(比特位)', value: 'bit'},
      {label: 'group(分组)', value: 'group'},
      {label: 'json(json类型)', value: 'json'},
    ]);
    let attrType = ref([
      {label: '读', value: 'R'},
      {label: '写', value: 'W'},
      {label: '读写', value: 'RW'},
    ]);
    let collectTagOptions = ref([
      {label: '默认', value: 'DEFAULT'},
      {label: '低频', value: 'LOW'},
      {label: '中频', value: 'MEDIUM'},
      {label: '高频', value: 'HIGH'},
    ]);
    let batchColumns = ref([])
    let batchCommonColumns = ref([
      {field: 'sort', title: '排序', width: 100, formType: 'number', editable: true},
      {field: "name", title: "属性名称*", width: 120, resizable: true, editable: true},
      {field: "field", title: "属性字段*", width: 120, resizable: true, editable: true},
      {field: "dataType", title: "数据类型*", width: 120, options: dataType.value, editable: true},
      {field: "attrType", title: "属性类型*", width: 80, options: attrType.value, editable: true},
      {field: "resolver", title: "解析器*", width: 150, resizable: true, url: "/iot/product/resolvers", editable: true},
      {field: "collectTag", title: "频率标签*", width: 120, editable: true, options: collectTagOptions},
      {field: "unit", title: "单位", width: 80, editable: true},
      {field: "accuracy", title: "精度", width: 80, formType: 'number', min: 0, max: 8, editable: true},
      {field: "gain", title: "增益", width: 80, formType: 'number', min: 0, editable: true},
      {field: "apiCode", title: "接口码", width: 160, resizable: true},
      {field: "apiName", title: "接口名称", width: 160, resizable: true},
    ]);

    let copyValue = ref();
    let searchRef = ref(null);
    let editModel = ref({});
    let searchModel = ref({});

    let modelDetailModel = ref({id: ''}); // 物模型
    let batchSearchModel = ref({productCode: null});
    let protocolBatchColumnsMap = ref({});

    watch(() => route.query, (newValue) => {
      let code = newValue['code'];
      if(code) {
        searchModel.value['code'] = code;
        if(searchRef.value) {
          searchRef.value.trigger();
        }
      }
    }, {immediate: true})

    return {searchRef, columns, rules, editModel, searchModel, modelDetailModel, dataType, attrType, collectTagOptions, statusOptions
      , protocolBatchColumnsMap, batchColumns, batchCommonColumns, batchModelQueryRef, batchSearchModel, copyValue}
  },
  mounted() {
    this.initBatchProtocolColumn();
  },
  methods: {
    parentChange(value, model) {
      this.editModel['gatewayId'] = model['gatewayId'];
      this.editModel['protocolId'] = model['protocolId'];
    },
    gatewayChange(value, model) {
      this.editModel['protocolId'] = model['protocolId'];
    },
    editOpen(editModel) {
      if(!editModel['productTypeId']) {
        editModel['productTypeId'] = this.searchModel['productTypeId']
      }
    },
    initBatchProtocolColumn() {
      this.batchCommonColumns.forEach(item => {
        if(item['options'] instanceof Array) {
          item['__editableSlot'] = ({record, column}) => {
            return h(USelect, {style: {width: '100%'}, options: item.options, size: 'small'
              , value: record[column.field], 'onUpdate:value': (newValue) => {
                record[column.field] = newValue;
              }
            })
          }
        } else if(item['url']) {
          item['__editableSlot'] = ({record, column}) => {
            return h(USelect, {style: {width: '100%'}, url: item.url, size: 'small'
              , value: record[column.field], 'onUpdate:value': (newValue) => {
                record[column.field] = newValue;
              }
            })
          }
        } else if(item['formType'] === 'number') {
          item['__editableSlot'] = ({record, column}) => {
            return h(InputNumber, {...item, style: {width: '80px'},
              size: 'small', value: record[column.field], 'onUpdate:value': (newValue) => {
                record[column.field] = newValue;
              }
            })
          }
        } else {
          item['__editableSlot'] = ({record, column}) => {
            return h(Input, {
               size: 'small', value: record[column.field], 'onUpdate:value': (newValue) => {
                record[column.field] = newValue;
              }
            })
          }
        }
      });
      this.$http.get("/iot/protocol/listEventApiTableHead").then(({code, message, data}) => {
        if(code === CoreConsts.SuccessCode) {
          this.protocolBatchColumnsMap = data;
          if(typeof data === 'object') {
            Object.keys(data).forEach(protocolCode => {
              let protocolConfigItems = data[protocolCode];
              if(protocolConfigItems instanceof Array) {
                let columns = protocolConfigItems.map(item => {
                  if(item.type === 'select') {
                    let field = item.field == 'direct' ? 'direct' : ('config.' + item.field);
                    return {field, title: item.label, fixed: 'right', width: 160, options: item.options, editable: true, __editableSlot: ({record, column}) => {
                        let value = getMetaValue(column['dataIndex'], record);
                        return h(USelect, {style: {width: '100%'}, allowClear: true, options: item.options, size: 'small', value, 'onUpdate:value': (newValue) => {
                            setMetaValue(column['dataIndex'], record, newValue);
                          }
                        })
                      }}
                  } else {
                    return {field: 'config.' + item.field, title: item.label, fixed: 'right', width: 120, editable: true, __editableSlot: ({record, column}) => {
                        let value = getMetaValue(column['dataIndex'], record);
                      return h(Input, {size: 'small', value, 'onUpdate:value': (newValue) => {
                            setMetaValue(column['dataIndex'], record, newValue);
                          }
                        });
                      }}
                  }
                });

                this.protocolBatchColumnsMap[protocolCode] = columns;
              }
            })
          }
        } else {
          this.$msg.error(message);
        }
      })
    },
    onBatchEditModel(editRow) {
      if(!editRow.id) {
        editRow['attrType'] = 'R';
        editRow['resolver'] = 'DEFAULT';
        editRow['collectTag'] = 'DEFAULT';
      }
      if(!editRow.config) {
        editRow.config = {};
      }
    },
    batchSubmitValidate(record) {
      if(!record.sort) {
        return Promise.reject('序号必填');
      }
      if(!record.name) {
        return Promise.reject('属性名称必填');
      }
      if(!record.field) {
        return Promise.reject('属性字段必填');
      }
      if(!record.dataType) {
        return Promise.reject('数据类型必填');
      }
      if(!record.attrType) {
        return Promise.reject('属性类型必填');
      }
      if(!record.resolver) {
        return Promise.reject('解析器必填');
      }

      record['productId'] = this.modelDetailModel['id'];
      try {
        if(record.direct) { // 有选择指令需要校验指令配置
          let protocolCode = this.modelDetailModel['protocolCode'];
          let protocolBatchColumns = this.protocolBatchColumnsMap[protocolCode];
          if(protocolBatchColumns instanceof Array) {
            protocolBatchColumns.forEach(column => {
              if (column.field == 'direct') {
                column['options'].forEach(({value, children}) => {
                  if (value == record.direct) {
                    if (children instanceof Array) {
                      children.forEach(config => {
                        let configValue = record['config'][config.value];
                        if (!configValue) {
                          throw new Error(`${config.label}必填`)
                        }
                      })
                    }
                  }
                })
              }
            })
          } else {
            return Promise.reject(`找不到驱动[${protocolCode}]`);
          }
        }
      } catch (e) {
        return Promise.reject(e.message);
      }

      return Promise.resolve(record);
    },
    batchModel({data: record, event}) {
      this.modelDetailModel = record;
      this.batchSearchModel.productCode = record['code'];
      let protocolBatchColumns = this.protocolBatchColumnsMap[record['protocolCode']];
      if(protocolBatchColumns && protocolBatchColumns.length > 0) {
        this.batchColumns = [...this.batchCommonColumns, ...protocolBatchColumns
          , {field: "action", title: "动作", type: 'action', width: 220, fixed: 'right'}
        ]
      } else {
        this.batchColumns = [...this.batchCommonColumns, {field: "action", title: "动作", type: 'action', width: 220, fixed: 'right'}];
      }

      if(this.batchModelQueryRef) {
        this.batchModelQueryRef.trigger();
      }

      event();
    },
    modelClose() {
      this.copyValue = null;
      this.activeKey = 'attr';
      let cmdTableRef = this.$refs['cmdTable'];
      if(cmdTableRef) {
        cmdTableRef.getTableContext().setDataSource([]);
      }
    },
  }
}
</script>
<style scoped>
.method-item .method-value, .method-item .method-label {
  display: inline-block;
  overflow: hidden;
  white-space: nowrap;
  text-overflow: ellipsis;
  margin-left: 12px;
}
.method-item .method-value {
  width: 45%;
}
</style>
