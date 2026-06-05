<template>
  <UView name="场景联动" id="linkage">
    <UViewSearch v-model="searchModel">
      <URow col="search">
        <UInputItem field="name" label="场景名称" allowClear />
        <USelectItem field="productId" label="所属产品" treeDefaultExpandAll url="/iot/product/list" allowClear />
        <USelectItem field="eventSourceId" label="所属事件源" url="/iot/eventSource/list" labelField="name" valueField="id" allowClear/>
        <UButton func="query" url="/iot/linkage/view">搜索</UButton>
        <UButton func="reset">重置</UButton>
        <UButton func="add" url="/iot/linkage/add" v-auth="'iot:linkage:add'">新增</UButton>
      </URow>
    </UViewSearch>
    <UViewModal title="场景联动配置" :span="[7, 14]" v-model="editModel" :rules="rules">
      <template #default="{model}">
        <UInputItem field="name" label="场景名称" />
        <USelectItem field="eventSourceId" label="所属事件源" url="/iot/linkage/sources" extra="警告：如果修改事件源将会删除所有联动项">
          <template #option="{value, label, extra}">
            <div>
              <span>{{label}}</span>
              <span style="float: right; margin-right: 8px">+{{extra}}</span>
            </div>
          </template>
        </USelectItem>
      </template>
      <template #footer>
        <UButton func="cancel">取消</UButton>
        <UButton func="submit" url="/iot/linkage/saveOrUpdate">提交</UButton>
        <UButton func="reset">重置</UButton>
      </template>
    </UViewModal>
    <UDrawer uid="ruleConfig" v-model:source="detailModel" size="100%" @open="openRuleConfig">
      <template #title>
        <AFlex justify="space-between">
          <div>场景联动规则配置</div>
          <div>
            <a-space-compact block size="small">
              <AButton danger size="small" @click="resetLinkage" shape="round">刷新</AButton>
              <AButton size="small" @click="switchLinkage">{{detailModel.status=='enabled' ? '停用' : '启用'}}</AButton>
              <AButton type="primary" size="small" @click="addLinkage" shape="round">新增</AButton>
            </a-space-compact>
          </div>
        </AFlex>
      </template>
      <ASpin description="数据处理中..." :spinning="loadingSpinning">
        <div class="iv-linkage-config" v-for="(item, index) in detailModel.items" :key="index">
          <AFlex class="iv-linkage-item" gap="small" vertical>
          <AFlex class="iv-li-header" :gap="18" justify="space-between">
            <ASpace>
              <span>名称</span>
              <AInput :maxlength="20" placeholder="请输入联动项名称" v-model:value="item.name"></AInput>
            </ASpace>
            <ASpace>
              <span>设备组</span>
              <USelect v-model:value="item['deviceGroupIds']" :url="`/iot/linkage/listDeviceGroupTree?id=${detailModel.id || ''}`" labelField="name" valueField="id"
                       placeholder="不选默认事件源所有组" :defaultValue="[]" allowClear mode="multiple" show-search optionFilterProp="label" :maxTagCount="2" style="min-width: 300px;"/>
            </ASpace>
            <ASpace style="flex-grow: 2">
              <span>备注</span>
              <AInput placeholder="请输入联动项备注" :maxlength="42" v-model:value="item.remark"></AInput>
            </ASpace>
            <a-space-compact block class="iv-item-action" size="small">
              <a-popover title="确定删除此项吗?" trigger="click">
                <template #content>
                  <div style="text-align: center">
                    <AButton size="small" type="text" danger @click="() => removeConfirm(item, 'item')">确定</AButton>
                  </div>
                </template>
                <a-button type="primary" danger shape="round">删除</a-button>
              </a-popover>
              <a-button type="dashed" @click="() => resetLinkageItem(item)">重置</a-button>
              <a-button type="primary" shape="round" @click="() => submitLinkageItem(item)">提交</a-button>
            </a-space-compact>
          </AFlex>
          <AFlex gap="middle">
            <div class="iv-linkage-condition">
              <div class="iv-title"><ATag :bordered="false" color="processing">如果</ATag></div>
              <AFlex vertical justify="space-between" style="overflow-x: auto;">
                <div v-for="(group, index) in item.conditions" :key="index" class="iv-condition-group">
                  <a-popover title="确定删除此条件组吗?" trigger="click">
                    <template #content>
                      <div style="text-align: center">
                        <AButton size="small" type="primary" danger @click="() => removeConfirm(group, 'group', item)">删除</AButton>
                      </div>
                    </template>
                    <div class="iv-close">x</div>
                  </a-popover>
                  <div style="text-align: center">
                    <AButton v-if="group.logical != null" shape="circle" type="dashed" size="small" @click="() => switchLogical(group)">
                      {{group.logical == 'or' ? '或' : '且'}}
                    </AButton>
                  </div>
                  <ASpace class="iv-item-container">
                    <div class="iv-group-item" v-for="(condition, index) in group.items" :key="index">
                      <a-popover title="确定删除此条件项吗?" trigger="click">
                        <template #content>
                          <div style="text-align: center">
                            <AButton size="small" type="primary" danger @click="() => removeConfirm(condition, 'condition', group)">删除</AButton>
                          </div>
                        </template>
                        <div class="iv-group-item-remove">x</div>
                      </a-popover>
                      <ASpace>
                        <AButton v-if="condition.logical != null" type="dashed" shape="circle" @click="() => switchLogical(condition)">
                          {{condition.logical == 'or' ? '或' : '且'}}
                        </AButton>
                        <ASpace size="small" class="iv-condition-item" @click="() => editConditionItem(condition, item)">
                          <ATag :bordered="false" color="green" v-if="condition.type === 'None'">不做校验, 条件一直通过</ATag>
                          <template v-else>
                            <ATag :bordered="false" color="cyan">{{condition.productName ? condition.productName : '请选择产品'}} /
                              {{condition.deviceName ? condition.deviceName : '选择设备'}} /
                              {{condition.linkedName ? condition.linkedName : '选择触发条件'}}{{condition.typeName ? `[${condition.typeName}]` : ''}}
                            </ATag>
                            <ATag :bordered="false" color="blue">{{condition['operatorName']}}</ATag>
                            <ATag :bordered="false" color="cyan">{{condition.value ? condition.value : '操作值'}}</ATag>
                          </template>
                        </ASpace>
                      </ASpace>
                    </div>
                    <AButton type="dashed" shape="circle" class="iv-item-add" @click="() => addGroupItem(group)">+</AButton>
                  </ASpace>
                </div>
                <div style="text-align: center" class="iv-group-add">
                  <AButton type="primary" shape="circle" size="small" @click="() => addGroup(item)">+</AButton>
                </div>
              </AFlex>
            </div>
            <div class="iv-linkage-action">
              <div class="iv-title"><ATag :bordered="false" color="warning">执行</ATag></div>
              <div style="overflow-x: auto; height: 100%">
                <ASpace direction="vertical" align="center" style="width: 100%;">
                  <template v-for="action in item.actions">
                    <div class="iv-action-item" style="margin: 6px 0px;">
                      <a-popover title="确定删除此动作项吗?" trigger="click">
                        <template #content>
                          <div style="text-align: center">
                            <AButton size="small" type="primary" danger @click="() => removeConfirm(action, 'action', item)">删除</AButton>
                          </div>
                        </template>
                        <div class="iv-action-item-remove">x</div>
                      </a-popover>
                      <ATag :bordered="false" color="cyan" @click.prevent="() => editActionItem(action, item)">
                        {{action.productName ? action.productName : '请选择产品'}} /
                        {{action.deviceName ? `${action.deviceName}` : '选择设备'}} /
                        ({{action.type == 'warn' ? '告警' : '动作'}})
                        {{action.linkedName ? action.linkedName : '选择动作'}}
                        {{action.typeName ? `[${action.typeName}]` : ''}}
                      </ATag>
                    </div>
                  </template>
                </ASpace>
              </div>
              <div class="iv-group-add">
                <AButton type="primary" shape="circle" title="增加动作" size="small" @click="() => addAction(item)">+</AButton>
              </div>
            </div>
          </AFlex>
        </AFlex>
        </div>
      </ASpin>
    </UDrawer>
    <AModal v-model:open="openConditionItemModal" width="80%" class="iv-condition-modal"
            :footer="null" @cancel="cancelModal" :styles="{body: {overflowX: 'auto'}}" centered>
      <template #title>
        <AFlex justify="space-between" gap="1">
          <div>条件项配置</div>
          <div>
            <ATag :bordered="false" color="cyan">
              {{selectConditionItem.productName}} / {{selectConditionItem.deviceName}} / {{selectConditionItem.linkedName}}
              {{selectConditionItem.typeName ? `[${selectConditionItem.typeName}]` : ''}}
            </ATag>
            <ATag :bordered="false" color="blue">{{selectConditionItem.operatorName}}</ATag>
            <ATag :bordered="false" color="cyan">{{selectConditionItem.value}}</ATag>
          </div>
          <div>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</div>
        </AFlex>
      </template>
      <ASpin :spinning="modalLoadingSpinning" description="数据加载中...">
        <AFlex gap="3" align="stretch" style="height: 520px; min-width: 1130px;">
          <div style="min-width: 210px;" class="iv-config-item">
            <div class="iv-item-title">请选择产品</div>
            <div class="iv-item-value iv-product">
              <AFlex vertical :gap="2">
                <div v-for="product in productList" :class="['iv-list-item', product['id']==selectConditionItem.productId?'active': null]"
                     :key="product['id']" @click="() => productClick(product)">{{product.name}}</div>
              </AFlex>
            </div>
          </div>
          <div style="min-width: 260px;" class="iv-config-item">
            <div class="iv-item-title">请选择触发条件</div>
            <div class="iv-item-value iv-event" style="width: 200px;">
              <AEmpty v-if="eventList == null || eventList.length == 0" style="margin-top: 30%;"/>
              <ATree v-else :treeData="eventList" :fieldNames="{title: 'label', key: 'value'}" v-model:expandedKeys="expandedKeys"
                     v-model:selectedKeys="selectConditionItem.mergeValue" blockNode @select="eventClick">
                <template #title="{label, config, extra}">
                  <div class="tree-node-title">
                    <span style="float: left; display: inline-block">{{label}}</span>
                    <span style="float: right; display: inline-block; padding-right: 5px; font-size: 12px; color: #747474">
                      {{`${extra}${config ? '('+config.attrType+')' : ''}`}}
                    </span>
                  </div>
                </template>
              </ATree>
            </div>
          </div>
          <div style="min-width: 220px;" class="iv-config-item">
            <div class="iv-item-title">请选择触发设备</div>
            <div class="iv-item-value iv-device" style="width: 200px;">
              <AEmpty v-if="deviceList == null || deviceList.length == 0" style="margin-top: 30%"/>
              <AFlex v-else vertical :gap="2">
                <div v-for="device in deviceList" :class="['iv-list-item', device['status'], device['uid']==selectConditionItem.uid?'active': null]"
                     :key="device['uid']" @click="() => deviceClick(device)">{{device.name}}</div>
              </AFlex>
            </div>
          </div>
          <div style="min-width: 180px" class="iv-config-item">
            <div class="iv-item-title">操作符</div>
            <div class="iv-item-value iv-operator">
              <AFlex vertical :gap="2" justify="center">
                <div v-for="operator in operatorList" :class="['iv-list-item', operator['status'], operator['value']==selectConditionItem.operator ? 'active' : null]"
                     :key="operator['value']" @click="() => operatorClick(operator)">{{operator.label}}{{operator.extra}}</div>
              </AFlex>
            </div>
          </div>
          <div style="min-width: 260px;" class="iv-config-item">
            <div class="iv-item-title">
              操作值
            </div>
            <div class="iv-item-value iv-value">
<!--              <AInput v-if="formType == 'input'" v-model:value="selectConditionItem.value" :disabled="valueStatus" style="width: 100%" />-->
<!--              <AInput v-else-if="formType == 'status'" v-model:value="selectConditionItem.value" defaultValue="true" style="width: 100%" disabled/>-->
<!--              <AInputNumber v-else-if="formType == 'number'" v-model:value="selectConditionItem.value" :disabled="valueStatus" style="width: 100%"/>-->
<!--              <ASelect v-else-if="formType == 'boolean'" v-model:value="selectConditionItem.value" :disabled="valueStatus"-->
<!--                       :options="[{label: '是', value: 'true'}, {label: '否', value: 'false'}]" defaultValue="true" style="width: 100%" />-->
              <AAutoComplete v-model:value="selectConditionItem.value" :options="formType" :disabled="valueStatus" @change="conditionValueChange" style="width: 100%" ></AAutoComplete>
            </div>
          </div>
        </AFlex>
      </ASpin>
    </AModal>
    <AModal v-model:open="openActionItemModal" width="830px" class="iv-action-modal" :footer="null"
            @cancel="cancelActionModal" :styles="{body: {overflowX: 'auto'}}" centered>
      <template #title>
        <AFlex justify="space-between">
          <div>场景动作项配置</div>
          <div>
            <ATag :bordered="false" color="cyan">
              {{selectActionItem.productName}} / {{selectActionItem.deviceName}} / {{selectActionItem.linkedName}}{{selectActionItem.typeName ? `[${selectActionItem.typeName}]` : ''}}
            </ATag>
          </div>
          <div>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</div>
        </AFlex>
      </template>
      <ASpin :spinning="modalLoadingSpinning" description="数据加载中...">
        <AFlex gap="small" align="stretch" style="height: 520px; min-width: 780px">
          <div style="min-width: 200px;" class="iv-config-item">
            <div class="iv-item-title">请选择产品</div>
            <div class="iv-item-value iv-product">
              <AFlex vertical :gap="2">
                <div v-for="product in productList" :class="['iv-list-item', product['id']==selectActionItem.productId?'active': null]"
                     :key="product['id']" @click="() => productClick(product, 'action')">{{product.name}}</div>
              </AFlex>
            </div>
          </div>
          <div style="min-width: 200px;" class="iv-config-item">
            <div class="iv-item-title">请选择执行的设备</div>
            <div class="iv-item-value iv-device" style="width: 200px;">
              <AEmpty v-if="actionDeviceList == null || actionDeviceList.length == 0" style="margin-top: 30%"/>
              <AFlex v-else vertical :gap="2">
                <div v-for="device in actionDeviceList" :class="['iv-list-item', device['value']==selectActionItem.uid?'active': null]"
                     :key="device['value']" @click="() => deviceClick(device, 'action')">{{device['label']}}</div>
              </AFlex>
            </div>
          </div>
          <div style="min-width: 350px;" class="iv-config-item">
            <div class="iv-item-title">请选择执行动作</div>
            <div class="iv-item-value iv-event" style="width: 350px;">
              <AEmpty v-if="eventList == null || eventList.length == 0" style="margin-top: 30%;"/>
              <ATree v-else :treeData="eventList" :fieldNames="{title: 'label', key: 'value'}" v-model:selectedKeys="selectActionItem.mergeValue"
                     blockNode @select="actionSelect" v-model:expandedKeys="expandedKeys" showLine>
                <template #title="{label, extra}">
                  <div class="tree-node-title">
                    <span style="float: left; display: inline-block">{{label}}</span>
                    <span style="float: right; display: inline-block; padding-right: 5px; font-size: 12px; color: #747474">{{extra}}</span>
                  </div>
                </template>
              </ATree>
            </div>
          </div>
        </AFlex>
      </ASpin>
    </AModal>
    <UViewTable :columns="columns" :scroll="{x: 1100}">
      <template #status="{record, text, value}">
        <ABadge v-if="text=='enabled'" status="processing" color="#87d068" :text="value" />
        <ABadge v-else status="warning" :text="value" />
      </template>
      <template #action="{record}">
        <UTag func="edit" :data="record" url="/iot/linkage/edit" v-auth="'iot:linkage:edit'">修改</UTag>
        <UTag func="ajax:confirm" :data="{id: record['id'], status: `${record['status'] == 'enabled' ? 'disabled' : 'enabled'}`}" url="/iot/linkage/switch"
              v-auth="'iot:linkage:switch'" :config="{confirmTitle: `确认${record['status'] == 'enabled' ? '停用' : '启用'}场景[${record.name}]`, confirmContent: `停用后此联动场景将不在被触发`}">
          {{record['status'] == 'enabled' ? '停用' : '启用'}}
        </UTag>
        <UTag func="open" uid="ruleConfig" :data="record" url="/iot/linkage/detail" v-auth="'iot:linkage:rule'">规则配置</UTag>
        <UTag func="del" :data="record" url="/iot/linkage/del" :config="{confirmContent: '将一并删除所有相关的联动配置项(不可恢复)'}" v-auth="'iot:linkage:del'">删除</UTag>
      </template>
    </UViewTable>
  </UView>
</template>
<!-- 场景联动功能 -->
<script>
import {ref, reactive, watch, computed, watchEffect} from "vue";
import {GET} from "@/utils/request";
import CoreConsts from "@/components/CoreConsts";
import {msgError} from "@/utils/message";
import dayjs from "dayjs";
export default {
  name: "Linkage",
  setup() {
    let statusOptions = ref([
      {label: '启用', value: 'enabled'},
      {label: '禁用', value: 'disabled'},
    ]);

    let columns = ref([
      {field: "name", title: "场景名称"},
      {field: "eventSourceName", title: "所属事件源"},
      {field: "eventSourceStatusName", title: "事件源状态"},
      {field: "createTime", title: "创建时间", type: 'date', format: 'YYYY-MM-DD HH:mm:ss'},
      {field: "statusName", title: "状态"},
      {field: 'action', title: '操作', type: 'action', fixed: 'right', width: 260},
    ]);

    let rules = reactive({
      name: {required: true, message: '场景名称必填'},
      eventSourceId: {required: true, message: '所属事件源必填'},
    })

    let editModel = ref({});
    let searchModel = ref({});
    let detailModel = ref({
      id: null,
      items: [
        {
          actions: [{mergeValue: []}],
          conditions: [
              {
                items: [{ }]
              }
          ]}
      ]});
    let eventList = ref([]);
    let eventSource = ref({type: null});
    let deviceList = ref([]);
    let actionDeviceList = ref([]);
    let productList = ref([]);
    let operatorList = ref([]);
    let expandedKeys = ref([]);
    let selectLinkageItem = ref({});
    let selectConditionItem = ref({
      id: null,
      logical: null,
      uid: null,
      direct: null,
      value: "",
      type: null,
      mergeValue: null,
      typeValue: null,
      typeName: null,
      operator: '',
      dataType: null,
      productId: null,
      linkedId: null,
      deviceName: null,
      productName: null,
      operatorName: null,
      linkedName: null
    });
    let valueStatus = computed({get: () => {
        return selectConditionItem.value['type'] == 'Status' ? true : false
      }, set: () => {}})
    let selectActionItem = ref({
      uid: null,
      type: null,
      mergeValue: null,
      typeValue: null,
      typeName: null,
      productId: null,
      actionId: null,
      deviceName: null,
      productName: null,
      linkedName: null
    })
    let loadingSpinning = ref(false);
    let modalLoadingSpinning = ref(false);
    let eventSelect = ref({});
    let parentEventSelect = ref({});
    let deviceSelect = ref({})
    let productSelect = ref({});
    let operatorSelect = ref({});

    let openActionItemModal = ref(false);
    let openConditionItemModal = ref(false);

    let resolverExpandedKeys = (data) => {
      if(data instanceof Array) {
        data.forEach(item => {
          if(item['selectable'] == false) {
            expandedKeys.value.push(item['value']);
          }

          if(item['children']) {
            resolverExpandedKeys(item['children']);
          }
        })
      }
    }

    let formType = computed({get: () => {
        return [];
      }, set: () => {}});

    let getDataTypeDefaultValue = (oriValue, dataType) => {
      if(dataType == 'string') {
        return oriValue ? oriValue : '""';
      } else if(dataType == 'datetime') {

      } else { // number类型
        return oriValue && !isNaN(oriValue) ? oriValue : 0;
      }
    }
    let resolverBetweenAndIn = (operator, dataType) => {
      if(operator == 'between' || operator == 'in') {
        formType.value.length = 0;
        let itemValue = selectConditionItem.value.value || "";
        if(!itemValue.startsWith("[")) {
          itemValue = getDataTypeDefaultValue(itemValue, dataType);
          selectConditionItem.value.value = itemValue = ("["+itemValue);
        }

        if(!itemValue.endsWith("]")) {
          selectConditionItem.value.value = itemValue + "]";
        }

        return true;
      }

      return false;
    }
    let resolverOperatorStatus = (type, operator, dataType) => {
      if(type == 'Status' || dataType == 'boolean') {
        operatorList.value.forEach(operator => {
          if(operator['value'] == 'eq') {
            operator['status'] = 'enabled'
            selectConditionItem.value.operatorName = operator['label']; // 等于
          } else {
            operator['status'] = 'disabled'
          }
        })
      } else {
        operatorList.value.forEach(operator => operator['status'] = 'enabled');
      }
    }
    let resolveTypeValue = () => {
      let dataType = selectConditionItem.value.dataType;
      if(dataType) {
        formType.value.length = 0;
        switch (dataType) {
          case "boolean":
            selectConditionItem.value.value = 'true';
            selectConditionItem.value.operator = 'eq';
            selectConditionItem.value.operatorName = '等于';
            formType.value.push({label: '是', value: 'true'}, {label: '否', value: 'false'});

            return;
          case "string":
            if(!resolverBetweenAndIn(selectConditionItem.value.operator, dataType)) {
              selectConditionItem.value.value = '';
            }
            break;
          case "datetime":
            if(!resolverBetweenAndIn(selectConditionItem.value.operator, dataType)) {
              selectConditionItem.value.value = dayjs().format("YYYY-MM-DD HH:mm:ss");
            }
            return 'datetime';
          case "offline":
          case 'online':
            selectConditionItem.value.value = 'true';
            break;
          default:
            if(!resolverBetweenAndIn(selectConditionItem.value.operator, dataType)) {
              selectConditionItem.value.value = '0';
            }
            break;
        }
      }
    }

    let resolverOperator = () => {
      if(selectConditionItem.value.operator) {
        let operator = selectConditionItem.value.operator;
        if(!resolverBetweenAndIn(operator, selectConditionItem.value.dataType)) {
          resolveTypeValue();
        }
      }
    }
    watch(() => selectConditionItem.value.operator, value => {
      resolverOperator();
    });
    watch(() => selectConditionItem.value.typeValue, typeValue => {
      resolveTypeValue();
      // resolverOperator();
      if(selectConditionItem.value.type == 'Status') {
        selectConditionItem.value.uid = '-2'; // 当前触发设备
        selectConditionItem.value.operator = 'eq'; // 等于
        selectConditionItem.value.value = 'true'; // true
        deviceList.value.forEach(device => {
          device['status'] = 'disabled'
          if(device['uid'] == '-2') {
            selectConditionItem.value.deviceName = device['name'];
          }
        });
      } else {
        selectConditionItem.value.uid = '-1'; // 所有设备
        deviceList.value.forEach(device => {
          if(device['uid'] != '-2') {
            device['status'] = 'enabled'
            if(device['uid'] == '-1') {
              selectConditionItem.value.deviceName = device['name'];
            }
          } else {
            device['status'] = 'disabled'
          }
        })
      }

      resolverOperatorStatus(selectConditionItem.value.type, selectConditionItem.value.operator, selectConditionItem.value.dataType);
    })

    GET(`/iot/linkage/operators`).then(({code, message, data}) => {
      if(code == CoreConsts.SuccessCode) {
        operatorList.value = data;
      }
    })

    watch(() => detailModel.value, (newValue, oldValue) => {
      if(newValue.id != oldValue.id) {
        GET(`/iot/linkage/listProductByLinkageId?id=${newValue.id}`).then(({code, message, data}) => {
          if(code == CoreConsts.SuccessCode) {
            productList.value = data;
          }
        })
      }
    })
    let loadActionData = (productId) => {
      modalLoadingSpinning.value = true;
      let linkageId = detailModel.value.id;
      return GET(`/iot/linkage/listActionByProductId?productId=${productId}&linkageId=${linkageId}`).then(({code, message, data}) => {
        if (code == CoreConsts.SuccessCode) {
          eventList.value = data;
          expandedKeys.value.length = 0;
          resolverExpandedKeys(data);

          let deviceGroupIds = selectLinkageItem.value['deviceGroupIds'];
          GET(`/iot/linkage/listDeviceOfAction?productId=${productId}&linkageId=${detailModel.value.id}&deviceGroupIds=${deviceGroupIds}`).then(({code, message, data}) => {
            if (code == CoreConsts.SuccessCode) {
              actionDeviceList.value = data;
              if (selectActionItem.value && !selectActionItem.value.id) {
                let datum = data[0];
                selectActionItem.value.uid = datum.value;
                selectActionItem.value.deviceName = datum.label;
              }
            } else {
              msgError(message);
            }
          }).finally(() => modalLoadingSpinning.value = false)
        } else {
          msgError(message);
          modalLoadingSpinning.value = false;
        }
      });
    }
    // 监听产品改变
    watch(() => selectActionItem.value.productId, (newValue, oldValue) => {
      if(newValue) {
        if(newValue != oldValue) {
          loadActionData(newValue);
        }
      } else {
        eventList.value = [];
        deviceList.value = [];
      }
    });
    watch(() => openActionItemModal.value, (newValue, oldValue) => {
      if(newValue == true) {
        if(selectActionItem.value.productId) {
          loadActionData(selectActionItem.value.productId);
        }
      }
    })
    let loadingConditionData = (productId) => {
      modalLoadingSpinning.value = true;
      let linkageId = detailModel.value.id;
      GET(`/iot/linkage/listEventByProductId?productId=${productId}&linkageId=${linkageId}`)
          .then(({code, message, data}) => {
        if(code == CoreConsts.SuccessCode) {
          eventList.value = data;
          expandedKeys.value.length = 0;
          resolverExpandedKeys(data);

          let linkageId = detailModel.value.id;
          let deviceGroupIds = selectLinkageItem.value['deviceGroupIds'];
          GET(`/iot/linkage/listDeviceByProductAndDeviceGroupIds?linkageId=${linkageId}&productId=${productId}&deviceGroupIds=${deviceGroupIds}`).then(({code, message, data}) => {
            if(code == CoreConsts.SuccessCode) {
              deviceList.value = data;
              deviceList.value.forEach(device => {
                if(!selectConditionItem.value.id) { // 新增
                  if(device['uid'] == '-1') {
                    selectConditionItem.value.uid = device['uid'];
                    selectConditionItem.value.deviceName = device['name'];
                  }
                } else { // 编辑
                  if(device['uid'] == selectConditionItem.value.uid) {
                    selectConditionItem.value.deviceName = device['name'];
                  }
                  if(selectConditionItem.value.uid == '-2') { // 当前触发设备
                    device['status'] = 'disabled';
                  }
                }

                if(device['uid'] == '-2') {
                  device['status'] = 'disabled';
                }
              })

            } else {
              msgError(message);
            }
          }).finally(() => modalLoadingSpinning.value = false)
        } else {
          msgError(message);
          modalLoadingSpinning.value = false;
        }
      });
    }
    watch(() => openConditionItemModal.value, (newValue, oldValue) => {
      if(newValue == true && selectConditionItem.value.productId) {
        loadingConditionData(selectConditionItem.value.productId);
      }
    })

    watch(() => selectConditionItem.value.productId, (newValue, oldValue) => {
      if(newValue) {
        if(newValue != oldValue) {
          loadingConditionData(newValue);
        }
      } else {
        eventList.value = [];
        deviceList.value = [];
      }
    })

    return {columns, rules, editModel, searchModel, detailModel, openConditionItemModal, productList, eventList, deviceList, operatorList
      , selectConditionItem, eventSelect, productSelect, operatorSelect, parentEventSelect, deviceSelect, openActionItemModal, expandedKeys
      , selectActionItem, loadingSpinning, statusOptions, selectLinkageItem, formType, actionDeviceList, modalLoadingSpinning
      , valueStatus, eventSource}
  },
  methods: {
    addLinkage() {
      let type = this.eventSource.type;
      if(type == 'event') {
        this.detailModel.items.push({
          linkageId: this.detailModel.id,
          actions: [{}],
          deviceGroupIds: [],
          conditions: [
            {
              items: [
                {
                  uid: '-1',
                  value: null,
                  deviceName: '设备',
                  productName: '请选择产品',
                  operatorName: '操作符',
                  linkedName: '操作事件',
                }
              ]
            }
          ]
        })
      } else {
        this.detailModel.items.push({
          linkageId: this.detailModel.id,
          actions: [{}],
          deviceGroupIds: [],
          conditions: [
            {
              items: [
                {
                  uid: '-1',
                  value: true,
                  type: 'None',
                  deviceName: '-',
                  productName: '-',
                  operatorName: '-',
                  linkedName: '-',
                }
              ]
            }
          ]
        })
      }
    },
    switchLinkage() {
      this.loadingSpinning = true;
      let {id, status} = this.detailModel;
      status = status == 'enabled' ? 'disabled' : 'enabled';
      this.$http.post("/iot/linkage/switch", {id, status}).then(({code, message, data}) => {
        if(code != CoreConsts.SuccessCode) {
          this.$msg.warn(message);
        } else {
          this.detailModel['status'] = status;
        }
      }).finally(() => this.loadingSpinning = false)
    },
    resetLinkage() {
      let id = this.detailModel['id'];
      this.loadingSpinning = true;
      this.$http.get('/iot/linkage/detail', {params: {id}}).then(({code, message, data}) => {
        if(code == CoreConsts.SuccessCode) {
          this.detailModel = data;
        } else {
          this.$msg.warn(message);
        }
      }).finally(() => this.loadingSpinning = false)
    },
    resetLinkageItem(item) {
      let id = item['id'];
      if(id) {
        this.loadingSpinning = true;
        this.$http.get("/iot/linkage/detailItemById", {params: {id}}).then(({code, message, data}) => {
          if(code == CoreConsts.SuccessCode) {
            item['name'] = data['name'];
            item['remark'] = data['remark'];
            item['actions'] = data['actions'];
            item['conditions'] = data['conditions'];
          } else {
            this.$msg.warn(message);
          }
        }).finally(() => this.loadingSpinning = false)
      } else {
        item['name'] = "";
        item['remark'] = "";
        item['actions'] = [
          {
            uid: '-2',
            mergeValue: [],
          }
        ];
        let type = this.eventSource.type;
        if(type == 'event') {
          item['conditions'] = [
            {
              items: [
                {
                  uid: '-1',
                  value: null,
                  deviceName: '设备',
                  productName: '请选择产品',
                  operatorName: '操作符',
                  linkedName: '操作事件',
                }
              ]
            }
          ];
        } else {
          item['conditions'] = [
            {
              items: [
                {
                  uid: '-1',
                  value: true,
                  type: 'None',
                  deviceName: '-',
                  productName: '-',
                  operatorName: '-',
                  linkedName: '-',
                }
              ]
            }
          ];
        }
      }
    },
    submitLinkageItem(item) {
      let {actions, conditions} = item;
      let isEvent = this.eventSource.type == 'event';
      if(conditions instanceof Array) {
        for (let i = 0; i < conditions.length; i++) {
          let {items} = conditions[i];
          if(items instanceof Array) {
            for (let j = 0; j < items.length; j++) {
              let {uid, productId, operator, linkedId, value, typeValue} = items[j];
              if(isEvent) {
                if(!uid) return this.$msg.warn("未选择设备");
                if(!productId) return this.$msg.warn("未选择产品");
                if(!typeValue) return this.$msg.warn("未选择触发事件");
                if(!operator) return this.$msg.warn("未选择操作符");
                if(!value) return this.$msg.warn("未选择操作值");
              } else {
                if(productId) {
                  if(!uid) return this.$msg.warn("未选择设备");
                  if(!typeValue) return this.$msg.warn("未选择触发事件");
                  if(!operator) return this.$msg.warn("未选择操作符");
                  if(!value) return this.$msg.warn("未选择操作值");
                } else {
                  items[j].type = 'None';
                  items[j].typeValue = 'None';
                  items[j].typeName = '';
                  items[j].linkedId = '-99';
                  items[j].productId = null;
                }
              }
            }
          } else {
            return this.$msg.warn("未配置条件项");
          }
        }
      } else {
        return this.$msg.warn("未配置条件");
      }

      if(actions instanceof Array) {
        if(actions.length == 0) return this.$msg.warn("未配置动作项");
        for (let i = 0; i < actions.length; i++) {
          let {uid, productId, typeValue} = actions[i];
          if(!uid) return this.$msg.warn("未选择执行设备");
          if(!productId) return this.$msg.warn("未选择执行产品");
          if(!typeValue) return this.$msg.warn("未选择执行动作");
        }
      } else {
        return this.$msg.warn("未配置动作项");
      }

      this.loadingSpinning = true;
      this.$http.post("/iot/linkage/saveOrUpdateItem", item).then(({code, message, data}) => {
        if(code == CoreConsts.SuccessCode) {
          item['id'] = data;
          this.$msg.success(message || "提交成功");
        } else {
          this.$msg.warn(message);
        }
      }).finally(() => this.loadingSpinning = false)
    },
    addGroup(linkageItem) {
      let conditions = linkageItem['conditions'];
      if(conditions instanceof Array) {
        let type = this.eventSource.type;
        if(type == 'event') {
          conditions.push({
            logical: 'and',
            items: [
              {
                uid: '-1',
                value: null,
                deviceName: '设备',
                productName: '请选择产品',
                operatorName: '操作符',
                linkedName: '操作事件',
              }
            ]
          })
        } else {
          conditions.push({
            logical: 'and',
            items: [
              {
                uid: '-1',
                value: true,
                type: 'None',
                deviceName: '-',
                productName: '-',
                operatorName: '-',
                linkedName: '-',
              }
            ]
          })
        }
      }
    },
    addAction(linkageItem) {
      let actions = linkageItem['actions'];
      if(actions instanceof Array) {
        actions.push({
          uid: '-2',
          mergeValue: [],
        })
      }
    },
    addGroupItem(group) {
      let conditionItems = group['items'];
      let type = this.eventSource.type;
      if(conditionItems instanceof Array) {
        if(type == 'event') {
          conditionItems.push({
            value: null,
            logical: 'or',
            deviceName: '设备',
            productName: '请选择产品',
            operatorName: '操作符',
            linkedName: '操作事件',
          })
        } else {
          conditionItems.push({
            uid: '-1',
            value: true,
            type: 'None',
            logical: 'or',
            deviceName: '-',
            productName: '-',
            operatorName: '-',
            linkedName: '-',
          })
        }
      }
    },
    editConditionItem(condition, item) {
      this.selectLinkageItem = item;
      this.selectConditionItem = condition;
      this.openConditionItemModal = true;
    },
    editActionItem(action, item) {
      this.selectLinkageItem = item;
      this.selectActionItem = action;
      this.openActionItemModal = true;
    },
    openRuleConfig(model) {
      this.$http.get(`/iot/eventSource/edit?id=${model.eventSourceId}`).then(({code, message, data}) => {
        if(code == CoreConsts.SuccessCode) {
          this.eventSource = data;
        } else {
          this.$msg.warn(message);
        }
      })
    },
    productClick(product, type) {
      if(type == 'action') {
        this.selectActionItem.productId = product.id;
        this.selectActionItem.productName = product.name;
      } else {
        this.selectConditionItem.productId = product.id;
        this.selectConditionItem.productName = product.name;
      }
    },
    eventClick(selectedKeys, {node}) {
      let dataRef = node;
      let config = dataRef.config;
      if(dataRef['extra'] == '属性') {
        this.selectConditionItem.typeName = data['label'];
      } else {
        this.selectConditionItem.typeName = '';
      }

      this.selectConditionItem.dataType = config['attrType'];
      this.selectConditionItem.typeValue = selectedKeys[0];
      this.selectConditionItem.type = config['type'];
      this.selectConditionItem.linkedId = config['linkedId'];
      this.selectConditionItem.linkedName = config['linkedName'];
    },
    actionSelect(selectedKeys, {node}) {
      let data = node;
      let config = data.config;

      if(data['extra'] == '参数') {
        this.selectActionItem.typeName=data['label'];
      } else {
        this.selectActionItem.typeName = '';
      }

      this.selectActionItem.type=config['type'];
      this.selectActionItem.typeValue=selectedKeys[0];
      this.selectActionItem.linkedId = config['linkedId']
      this.selectActionItem.linkedName = config['linkedName']
    },
    operatorClick(operator) {
      if(operator['status'] != 'disabled') {
        this.selectConditionItem.operator = operator.value;
        this.selectConditionItem.operatorName = operator.label;
        switch (operator.value) {
          case "in":
            return;
          case "between":
            return;
        }
      }
    },
    deviceClick(device, type) {
      if(type == 'action') {
        this.selectActionItem.uid = device.value
        this.selectActionItem.deviceName = device.label;
      } else {
        if(device['status'] != 'disabled') {
          this.deviceSelect = device;
          this.selectConditionItem.uid = device.uid
          this.selectConditionItem.deviceName = device.name;
        }
      }
    },
    switchLogical(condition) {
      condition.logical = condition.logical == 'or' ? 'and' : 'or';
    },
    cancelModal() {
      // this.selectConditionItem = {};
    },
    cancelActionModal() {

    },
    removeConfirm(value, type, linkageItemOrConditionGroup) {
      if(type == 'item') {
        if(value['id']) {
          this.loadingSpinning = true;
          this.$http.post("/iot/linkage/delItem", [value['id']]).then(({code, message}) => {
            if(code == CoreConsts.SuccessCode) {
              let index = this.detailModel.items.findIndex(item => value == item);
              if(index > -1) {
                this.detailModel.items.splice(index, 1);
              }
            } else {
              this.$msg.warn(message);
            }
          }).finally(() => this.loadingSpinning = false)
        } else {
          let index = this.detailModel.items.findIndex(item => value == item);
          if(index > -1) {
            this.detailModel.items.splice(index, 1);
          }
        }
      } else if(type == 'group') {
        let linkageItem = linkageItemOrConditionGroup;
        let conditions = linkageItem['conditions'];
        if(conditions instanceof Array) {
          if(conditions.length > 1) {
            let findIndex = conditions.findIndex(item => value == item);
            if(findIndex > -1) {
              conditions.splice(findIndex, 1);
            }
          }
        }
      } else if(type == 'condition') {
        let group = linkageItemOrConditionGroup;
        let conditionItems = group['items'];
        if(conditionItems instanceof Array) {
          if(conditionItems.length > 1) {
            let findIndex = conditionItems.findIndex(item => value == item);
            if(findIndex > -1) {
              conditionItems.splice(findIndex, 1);
            }
          }
        }
      } else if(type == 'action') {
        let actions = linkageItemOrConditionGroup.actions;
        if(actions instanceof Array) {
          let findIndex = actions.findIndex(item => value == item);
          if(findIndex > -1) {
            actions.splice(findIndex, 1);
          }
        }
      }
    },
    conditionValueChange(value) {
      let isArray = false;
      if(this.selectConditionItem.operator == 'between' || this.selectConditionItem.operator == 'in') {
        if(!value.startsWith("[") && !value.endsWith("]")) {
          this.selectConditionItem.value = "[" + this.selectConditionItem.value + "]";
        } else if(!value.endsWith("]")) {
          this.selectConditionItem.value = this.selectConditionItem.value + "]"
        } else if(!value.startsWith("[")) {
          this.selectConditionItem.value = "[" + this.selectConditionItem.value
        }

        isArray = true;
      }

      let dataType = this.selectConditionItem.dataType;
      if(dataType) {
        if(dataType == 'boolean') {
          if(value != "true" && value != "false") {
            this.selectConditionItem.value = "true";
          }
        } else if(dataType == 'string') {

        } else if(dataType == 'date') {

        } else { // number类型
          if(isArray) {
            value = value.substring(1, value.length - 1)
            if(value) {
              let parse = value.split(',');
              if(parse instanceof Array) {
                parse.forEach((item, index) => {
                  if(isNaN(item) && item != '-') {
                    parse[index] = 0;
                  }
                })
              }

              if(this.selectConditionItem.operator == 'between' && parse.length > 2) {
                parse.splice(2, parse.length - 2);
              }
              this.selectConditionItem.value = `[${parse.join(',')}]`;
            }
          } else {
            if(isNaN(value) && value != '-') {
              this.selectConditionItem.value = "0";
            }
          }
        }
      }
    },
    removeLinkageItem() {

    },
    filterDeviceGroup(input) {
      // debugger
    }
  }
}
</script>
<style scoped>
.iv-linkage-config {
  padding: 8px;
  margin: 3px 0px 20px;
  border-radius: 5px;
  box-shadow: 0px 0px 6px 1px rgba(217, 217, 217, 0.41);
  /*background-color: #fcfcfc;*/
}
.iv-linkage-item {
  position: relative;
  margin: 6px 0px 0px;
  min-width: 1180px;
  border-radius: 5px;
  /*border: 1px solid #d9d9d9;*/
  /*background-color: rgba(0, 175, 96, 0.04);*/
}
.iv-li-header {
  height: 32px;
  line-height: 32px;
  padding: 0px 8px;
}
.iv-linkage-item .iv-title {
  width: 60px;
  /*height: 20px;*/
  left: 12px;
  top: -12px;
  font-weight: bold;
  line-height: 20px;
  text-align: center;
  position: absolute;
  /*background-color: #ffffff;*/
}
.iv-condition-group:nth-child(n+2):hover .iv-close, .iv-action-item:hover .iv-action-item-remove
, .iv-item-container .ant-space-item:nth-child(n+2):hover .iv-group-item-remove {
  display: block;
}
.iv-linkage-item .iv-group-add {
  /*width: 32px;*/
  /*height: 32px;*/
  left: 50%;
  bottom: -12px;
  border-radius: 50%;
  position: absolute;
  display: none;
}
.iv-linkage-condition:hover .iv-group-add,.iv-linkage-action:hover .iv-group-add {
  display: block;
}
.iv-linkage-condition {
  width: 720px;
}
.iv-linkage-action {
  width: 460px;
}
.iv-linkage-condition,.iv-linkage-action {
  border-radius: 6px;
  margin: 6px 0px;
  padding: 12px;
  position: relative;
  border: 1px solid #d9d9d9;
}
.iv-condition-group {
  position: relative;
}
.iv-group-item {
  position: relative;
}
.iv-condition-item, .iv-action-item {
  padding: 3px 8px;
  cursor: pointer;
  border-radius: 5px;
  position: relative;
  border: 1px dashed #d9d9d9;
}
.iv-condition-item .ant-tag, .iv-action-item .ant-tag {
  font-size: 16px;
  height: 36px;
  line-height: 34px;
}
.iv-condition-item:hover {
  background-color: rgba(217, 217, 217, 0.22);
}
.iv-condition-group .iv-item-container {
  width: 680px;
  padding: 6px;
  /*overflow-x: auto;*/
}
.iv-linkage-item .iv-item-action {
  width: 168px;
  height: 32px;
  cursor: pointer;
  z-index: 3;
  padding: 4px 0px;
  text-align: center;
}
/*.iv-linkage-item:hover .iv-item-action {*/
/*  display: block;*/
/*}*/
.iv-condition-group .iv-close,.iv-action-item .iv-action-item-remove,.iv-group-item .iv-group-item-remove {
  display: none;
  cursor: pointer;
  z-index: 3;
  width: 24px;
  height: 24px;
  background-color: rgba(217, 217, 217, 0.34);
  line-height: 18px;
  text-align: center;
  border: 1px solid #d9d9d9;
  border-radius: 50%;
  position: absolute;
}
.iv-group-item .iv-group-item-remove,.iv-action-item .iv-action-item-remove {
  right: -12px;
  top: -12px;
}
.iv-condition-group .iv-close {
  left: 0px;
  top: 26px;
}
.iv-config-item {
  overflow-y: auto;
  border: 1px solid #d9d9d9;
}
.iv-item-title {
  height: 36px;
  padding: 6px;
  text-align: center;
  background-color: rgba(217, 217, 217, 0.29);
}
.iv-item-value {
  /*width: inherit;*/
  overflow: auto;
  padding: 8px 2px;
}
.iv-item-value .iv-list-item {
  padding: 3px 6px;
  cursor: pointer;
  text-align: center;
  border-radius: 3px;
}
.iv-item-value .iv-list-item:hover {
  background-color: rgba(217, 217, 217, 0.22);
}
.iv-item-value .iv-list-item.active {
  color: #ffffff;
  background-color: #19B0FE;
}
.iv-item-value .iv-list-item.disabled {
  color: #ffffff;
  cursor: not-allowed;
  background-color: #e0e0e0;
}
.iv-item-value .iv-list-item.disabled.active {
  background-color: rgba(25, 176, 254, 0.47);
}
</style>
