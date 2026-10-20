<template>
  <div>
    <a-descriptions bordered :column="{xs: 1, sm: 2, md: 3, lg: 3, xl: 5}" size="small">
      <a-descriptions-item label="产品名称">{{modelDetailModel['name']}}</a-descriptions-item>
      <a-descriptions-item label="产品代码">{{modelDetailModel['code']}}</a-descriptions-item>
      <a-descriptions-item label="产品类型">{{modelDetailModel['typeName']}}</a-descriptions-item>
      <a-descriptions-item label="设备类型">{{modelDetailModel['deviceTypeName']}}</a-descriptions-item>
      <a-descriptions-item label="操作方式">{{modelDetailModel['ctrlModelName']}}</a-descriptions-item>
      <a-descriptions-item label="绑定协议">{{modelDetailModel['protocolName']}}</a-descriptions-item>
      <a-descriptions-item label="接入网关">{{modelDetailModel['gatewayName']}}</a-descriptions-item>
      <a-descriptions-item label="传输协议">{{modelDetailModel['transportProtocolName']}}</a-descriptions-item>
      <a-descriptions-item label="备注" span="3">{{modelDetailModel['remark']}}</a-descriptions-item>
    </a-descriptions>
    <UTabs uid="modelTabs" v-model:activeKey="activeKey" @change="paneChange" style="margin-top: 16px">
      <!--          <template #rightExtra>-->
      <!--            <AButton type="dashed">快速导入</AButton>-->
      <!--          </template>-->
      <!-- 面板内容统一经#contentRender按item.key分发(面板声明见script.modelItems): 未访问面板惰性渲染, 访问过切走仅隐藏保持挂载 -->
      <UTabPane key="attr" tab="模型属性">
          <UViewSearch>
            <UInputItem label="名称" field="name" allowClear/>
            <UInputItem label="字段" field="field" allowClear/>
            <USelectItem label="数据类型" field="dataType" style="width: 150px" :options="dataType" allowClear/>
            <UButton func="query" :url="`/iot/modelAttr/list?productId=${modelDetailModel['id']}`">查询</UButton>
            <UButton func="add" v-auth="'iot:modelAttr:add'">新增</UButton>
          </UViewSearch>
          <UViewTable :columns="attrColumns" v-model:source="ModelAttrList" size="small" :scroll="{x: 1080, y: 320}"
                      :virtual="true" :pagination="false">
            <template #action="{record, editing}">
              <!--                  <UTag func="ajax" :data="{id: record['id'], status: !record['ctrlStatus']}" :url="`/iot/modelAttr/ctrlStatus`"-->
              <!--                        :disabled="record['attrType'] == 'R'">控制属性</UTag>-->
              <UTag func="edit" :data="record" url="/iot/modelAttr/edit" v-auth="'iot:modelAttr:edit'" :disabled="record['origin']=='Protocol'">修改</UTag>
              <UTag func="del" :data="record" url="/iot/modelAttr/del" v-auth="'iot:modelAttr:del'" :disabled="record['origin']=='Protocol'">删除</UTag>
            </template>
          </UViewTable>
          <UViewDrawer title="物模型属性配置" placement="right" @edit="attrEdit" v-model="attrDetailModel" size="92%" :span="[8, 16]" :rules="attrRules">
            <template #default="{model}">
              <URow :gutter="6" :responsive="4">
                <UInputItem label="属性名称" field="name" :maxlength="18"/>
                <UInputItem label="属性字段" field="field" :maxlength="36"/>
                <USelectItem label="数据类型" field="dataType" :options="dataType" />
                <USelectItem label="属性类型" field="attrType" :options="attrType" defaultValue="R"/>
                <USelectItem label="值解析器" required field="resolver" url="/iot/product/resolvers" defaultValue="DEFAULT" />
                <UInputNumberItem label="序号" required field="sort" :defaultValue="0" />
                <UInputItem label="默认值" field="defaultValue" :disabled="attrDetailModel['attrType'] == 'R'" :maxlength="430"/>
                <UInputItem label="属性单位" field="unit" :maxlength="18"/>
                <UInputNumberItem label="精度/增益" class="iv-group-addon-padding-0" field="accuracy" placeholder="精度值" :min="0" :max="8" :step="1" :precision="0">
                  <template #addonAfter>
                    <AInputNumber v-model:value="model['gain']" placeholder="增益值" :min="0.00001" :max="10000" style="width: 110px;" />
                  </template>
                </UInputNumberItem>
                <UInputItem label="属性备注" field="remark" :maxlength="64"/>
              </URow>
              <UTabs uid="attrConfigTabs" v-model:activeKey="attrActiveKey" size="small" type="card" @change="attrPaneChange"
                     tabBarExtraContent="属性值配置(适用于枚举、json、bit、group类型)以及属性的基础告警配置">
                <UTabPane key="dict" tab="属性值配置">
<!--                  <UChildView uid="attrValue">-->
                    <UViewSearch v-model="attrDictSearchModel">
                      <UInputItem label="名称" field="dictName"/>
                      <UButton func="query" url="/iot/modelAttr/dict/view" ref="attrModelQueryRef">查询</UButton>
                      <UButton func="add" :disabled="attrDetailModel['id'] == null">新增</UButton>
                      <span style="color: rgba(168,168,168,0.44); font-size: 13px; height: 32px; line-height: 32px;">先保存数据后才能新增</span>
                    </UViewSearch>
                    <UViewEditable :columns="attrComputeColumns" @edit="editModelAttrDict" :pagination="false" size="small" position="top" v-model:source="modelAttrDictList">
                      <template #dictName="{record, editing}">
                        <UInput v-if="attrDetailModel['dataType']!='json' && attrDetailModel['dataType']!='bit' && attrDetailModel['dataType'] != 'group'" placeholder="请输入名称" v-model:value="record['dictName']" size="small" :bordered="false" />
                        <USelect v-else v-model:value="record['dictValue']" size="small" :options="attrFieldOptions"
                                 :bordered="false" placeholder="请选择属性" style="width: 100%" @change="(value, model) => attrFieldChange(value, model, record)"/>
                      </template>
                      <template #dictValue="{record, editing}">
                        <UInput v-if="attrDetailModel['dataType']!='json' && attrDetailModel['dataType']!='bit' && attrDetailModel['dataType'] != 'group'" v-model:value="record['dictValue']" placeholder="请输入值" size="small" :bordered="false" />
                      </template>
                      <template #path="{record, editing}">
                        <UInput v-model:value="record['path']" size="small" :bordered="false" placeholder="$.xx或比特位或值" />
                      </template>
                      <template #action="{record, editing}">
                        <UTag func="edit" :data="record">修改</UTag>
                        <UTag func="cancel" :data="record">取消</UTag>
                        <UTag func="submit" :data="record" url="/iot/modelAttr/dict/saveOrUpdate" :params="resolveAttrDictSubmitParams">保存</UTag>
                        <UTag func="del" :data="record" url="/iot/modelAttr/dict/del">删除</UTag>
                      </template>
                    </UViewEditable>
<!--                  </UChildView>-->
                </UTabPane>
                <UTabPane key="warn" tab="告警/指标配置">
<!--                  <UChildView uid="warnConfig">-->
                    <UViewEditable :columns="attrWarnColumns" @edit="editModelAttrWarn" :pagination="false" size="small" >
                      <template #title>
                        <UButton v-show="false" func="query" :url="`/iot/warnConfig/listByModelAttrId?modelAttrId=${attrDetailModel['id'] || ''}`" ref="attrWarnQueryRef">查询</UButton>
                        <span style="color: var(--iv-color-tip); font-size: 13px; height: 32px; line-height: 32px;">
                          1. 告警内容支持占位符包括设备编号{deviceSn}、值{value}、时间{warnTime}、等级{grade}
                          2. 指标等级不参与告警
                          3. 在告警频率内多次告警只通知一次(为0不限制)
                        </span>
                        <UButton func="add" :disabled="attrDetailModel['id'] == null">新增</UButton>
                      </template>
                      <template #operator="{record}">
                        <USelect v-model:value="record['operator']" url="/iot/linkage/operators" size="small" :bordered="false" placeholder="请选择操作" />
                      </template>
                      <template #threshold="{record}">
                        <AInput v-model:value="record['threshold']" size="small" :bordered="false" placeholder="请输入阈值" />
                      </template>
                      <template #frequency="{record}">
                        <AInputNumber v-model:value="record['frequency']" :min="0" :step="1" size="small" :bordered="false" placeholder="告警频率" />
                      </template>
                      <template #grade="{record}">
                        <USelect v-model:value="record['grade']" size="small"
                                 :bordered="false" url="/iot/warnConfig/gradesRmStatus" placeholder="请选择属性" style="width: 100%"/>
                      </template>
                      <template #indicator="{record}">
                        <AAutoComplete v-model:value="record['indicator']" :options="warnIndicatorOptions" placeholder="简短指标" size="small" :bordered="false" />
                      </template>
                      <template #messageTemplateId="{record}">
                        <USelect v-model:value="record['messageTemplateId']" url="/core/messageTemplate/list?type=iot:warn"
                                 size="small" :bordered="false" placeholder="推送消息" labelField="templateName" valueField="id" />
                      </template>
                      <template #template="{record}">
                        <AInput v-model:value="record['template']" placeholder="请输入告警内容" size="small" :bordered="false" />
                      </template>
                      <template #action="{record, editing}">
                        <UTag func="edit" :data="record">修改</UTag>
                        <UTag func="cancel" :data="record">取消</UTag>
                        <UTag func="submit" :data="record" url="/iot/warnConfig/saveOrUpdateFormProduct" :params="resolveAttrWarnSubmitParams">保存</UTag>
                        <UTag func="del" :data="record" url="/iot/warnConfig/del">删除</UTag>
                      </template>
                    </UViewEditable>
<!--                  </UChildView>-->
                </UTabPane>
              </UTabs>
            </template>
            <template #footer>
              <div>
                <UButton func="cancel">取消</UButton>
                <UButton func="submit" url="/iot/modelAttr/saveOrUpdate">提交</UButton>
                <UButton func="submit" url="/iot/modelAttr/saveOrUpdate" :callback="modelAttrCallback">
                  仅保存<QuestionCircleOutlined style="font-size: 12px" title="数据只做保存, 不关闭窗口" />
                </UButton>
                <UButton func="reset">重置</UButton>
              </div>
            </template>
          </UViewDrawer>
        </UTabPane>
      <u-tab-pane key="func" tab="功能模型">
        <UViewSearch>
          <UInputItem label="名称" field="name"/>
          <UInputItem label="代码" field="code"/>
          <UButton func="query" :url="`/iot/modelApi/view?type=func&productId=${modelDetailModel['id']}`">查询</UButton>
          <UButton func="add" v-auth="'iot:modelApi:add'">新增</UButton>
        </UViewSearch>
        <UViewModal :title="`功能配置[${modelDetailModel['code']}]`" :span="[7, 15]" width="920px" v-model="funcEditModel" :centered="false" @edit="apiOpenEdit" :rules="apiRules">
          <URow :responsive="3">
            <UInputItem label="功能名称" field="name" required :maxlength="18" />
            <USelectItem label="协议指令" field="direct" v-model:source="funcApis" labelField="name" valueField="code" required
                         url="/iot/protocol/apis?id={protocolId}&type=func" @change="apiChange" @loaded="funcApisLoaded" :disabled="funcEditModel['id'] != null"/>
            <UInputItem label="接口代码" field="code" :maxlength="64" required :disabled="funcEditModel['id'] != null" />
            <URadioItem label="触发方式" field="triggerMode" required :options="triggerModeOptions" :disabled="true" />
            <UInputNumberItem label="序号" field="sort" :defaultValue="1000" required />
            <UInputItem label="功能说明" field="remark" :maxlength="64" />
            <a-divider dashed>指令配置(可用@提及属性、#提及执行脚本)</a-divider>
            <!-- 弹框内容域容器(编辑弹框内UChildView经LinkView收编editContext.childLinkContext): apiConfigTabs与直挂表格注册本容器,
                 焦点弹框序列化经containers输出标签清单+各面板表格, 与宿主主体(UChildView uid=func)隔离不混入 -->
            <UTabs uid="funcApiConfig" v-model:activeKey="apiConfigActiveKey" style="width: 873px">
              <UTabPane key="down" tab="下行配置">
                <UTable uid="downConfig" :pagination="false" :dataSource="funcEditModel['downConfig']" :scroll="{x: 870}" :columns="funcDownConfigColumns" size="small">
                  <template #value="{record}">
                    <ASelect v-if="record['options']" style="width: 100%" v-model:value="record['value']" :options="record['options']">
                      <template #option="{label, value, extra}">
                        <div>
                          <span>{{label}}</span>
                          <span style="float: right; color: #a3a3a3; font-size: 12px; margin-top: 2px">{{extra}}</span>
                        </div>
                      </template>
                    </ASelect>
                    <UMentions v-else v-model:value="record['value']" labelField="name" valueField="field" :options="configMentionsOptions(record)"
                               :prefix="['@', '#']" split="" @select="(model) => directParamsChange(model, record)" :maxlength="360" />
                  </template>
                </UTable>
              </UTabPane>
              <UTabPane key="up" tab="上行配置">
                <UTable uid="upConfig" :pagination="false" :dataSource="funcEditModel['upConfig']" :scroll="{x: 870}" :columns="funcUpConfigColumns" size="small">
                  <template #value="{record}">
                    <span v-if="record['protocolDataType'] != 'any' && record['protocolDataType'] != 'json'"></span>
                    <UMentions v-else v-model:value="record['value']" labelField="name" valueField="field" size="small" :options="ModelAttrList"
                               split="" @select="(model) => directParamsChange(model, record)" :maxlength="360" />
                  </template>
                </UTable>
              </UTabPane>
            </UTabs>
          </URow>
          <template #footer>
            <UButton func="cancel">取消</UButton>
            <UButton func="submit" url="/iot/modelApi/saveOrUpdate" :params="apiConfigResolve">提交</UButton>
            <UButton func="reset">重置</UButton>
          </template>
        </UViewModal>
        <UViewTable :columns="apiColumns" size="small" :scroll="{x: 1080}">
          <template #action="{record}">
            <UTag func="ajax:confirm" v-if="record['triggerMode']=='passive' && record['funcType']=='W'" :data="{}"
                  :url="`/iot/modelApi/asStatus?productId=${record.productId}&code=${record.code}&status=${!record.asStatus}`"
                  :config="{confirmTitle: '此接口将作为管理设备的重要接口?', confirmContent: '一般用于控制设备的开关、启停等接口,放于最显眼位置,一个产品最多只能有4个状态接口'}">
              {{record.asStatus ? '取消状态' : '作为状态'}}
            </UTag>
            <UTag func="edit" :data="record" url="/iot/modelApi/edit" v-auth="'iot:modelApi:edit'">修改</UTag>
            <UTag func="del" :data="record" url="/iot/modelApi/del" :disabled="record.protocol === true" v-auth="'iot:modelApi:del'">删除</UTag>
          </template>
        </UViewTable>
      </u-tab-pane>
      <u-tab-pane key="event" tab="事件模型">
        <UViewSearch>
          <UInputItem label="名称" field="name"/>
          <UInputItem label="代码" field="code"/>
          <UButton func="query" :url="`/iot/modelApi/view?type=event&productId=${modelDetailModel['id']}`">查询</UButton>
          <UButton func="add" v-auth="'iot:modelApi:add'">新增</UButton>
        </UViewSearch>
        <UViewModal :title="`事件配置[${modelDetailModel['code']}]`" :span="[7, 15]" width="920px" v-model="eventEditModel" :centered="false" @edit="apiOpenEdit" :rules="apiRules">
          <URow :responsive="3">
            <UInputItem label="事件名称" field="name" required :maxlength="18" />
            <USelectItem label="协议指令" field="direct" v-model:source="eventApis" labelField="name" valueField="code" required
                         url="/iot/protocol/apis?id={protocolId}&type=event" @change="apiChange" @loaded="funcApisLoaded" :disabled="eventEditModel['id'] != null"/>
            <UInputItem label="接口代码" field="code" :maxlength="64" required :disabled="eventEditModel['id'] != null"/>
            <URadioItem label="触发方式" field="triggerMode" :options="triggerModeOptions" :disabled="true" />
            <USelectItem label="频率标签" field="collectTag" :options="collectTagOptions" defaultValue="DEFAULT" required/>
            <UInputItem label="事件说明" field="remark" :maxlength="64"/>
            <a-divider dashed>指令配置(可用@提及属性)</a-divider>
            <!-- 弹框内容域容器(编辑弹框内UChildView经LinkView收编editContext.childLinkContext): apiConfigTabs与直挂表格注册本容器,
                 焦点弹框序列化经containers输出标签清单+各面板表格, 与宿主主体(UChildView uid=event)隔离不混入 -->
            <UTabs uid="eventApiConfig" v-model:activeKey="apiConfigActiveKey" style="width: 873px">
              <UTabPane key="down" tab="下行配置">
                <UTable uid="downConfig" :pagination="false" :dataSource="eventEditModel['downConfig']" :scroll="{x: 870}" :columns="eventDownConfigColumns" rowKey="protocolAttrField" size="small">
                  <template #value="{record}">
                    <USelect v-if="record['options']" style="width: 100%;" v-model:value="record['value']" :options="record['options']">
                      <template #option="{label, value, extra}">
                        <div>
                          <span>{{label}}</span>
                          <span style="float: right; color: #a3a3a3; font-size: 12px; margin-top: 2px">{{extra}}</span>
                        </div>
                      </template>
                    </USelect>
                    <UMentions v-else v-model:value="record['value']" labelField="name" valueField="field" :options="ModelAttrList"
                               split="" @select="(model) => directParamsChange(model, record)" :maxlength="360"/>
                  </template>
                </UTable>
              </UTabPane>
              <UTabPane key="up" tab="上行配置">
                <UTable uid="upConfig" :pagination="false" :dataSource="eventEditModel['upConfig']" :scroll="{x: 870}" :columns="eventUpConfigColumns" rowKey="protocolAttrField" size="small">
                  <template #value="{record}">
                    <USelect v-if="record['options']" style="width: 100%;" v-model:value="record['value']" :options="record['options']">
                      <template #option="{label, value, extra}">
                        <div>
                          <span>{{label}}</span>
                          <span style="float: right; color: #a3a3a3; font-size: 12px; margin-top: 2px">{{extra}}</span>
                        </div>
                      </template>
                    </USelect>
                    <UInput v-else-if="record['protocolDataType'] != 'any' && record['protocolDataType'] != 'json'" v-model:value="record['value']" />
                    <USelect v-else v-model:value="record['value']" labelField="name" valueField="field" :options="ModelAttrList"
                               split="" @select="(value, model) => eventUpValueChange(value, model, record)" :maxlength="360" style="width: 100%"/>
                  </template>
                </UTable>
              </UTabPane>
            </UTabs>
          </URow>
          <template #footer>
            <UButton func="cancel">取消</UButton>
            <UButton func="submit" url="/iot/modelApi/saveOrUpdate" :params="apiConfigResolve">提交</UButton>
            <UButton func="reset">重置</UButton>
          </template>
        </UViewModal>
        <UViewTable :columns="eventColumns" size="small" :scroll="{x: 1080}">
          <template #action="{record}">
            <!--  点位控制的网关子设备  -->
            <!--                  <UTag func="ajax:confirm" v-if="modelDetailModel['deviceType'] == 'Child'" :data="{}"-->
            <!--                        :url="`/iot/modelApi/asStatus?productId=${record.productId}&code=${record.code}`" :disabled="record['asStatus']"-->
            <!--                        :config="{confirmTitle: '确认将此接口作为在线状态接口?', confirmContent: '声明此接口将用于采集设备的状态(在线/离线)'}">在线状态</UTag>-->
            <UTag func="edit" :data="record" url="/iot/modelApi/edit" v-auth="'iot:modelApi:edit'">修改</UTag>
            <UTag func="del" :data="record" url="/iot/modelApi/del" v-auth="'iot:modelApi:del'">删除</UTag>
          </template>
        </UViewTable>
      </u-tab-pane>
      <UTabPane key="commands" tab="指令集">
        <UTable uid="cmdTable" :columns="cmdColumns" :pagination="false" ref="cmdTable" size="small" :scroll="{y: 360}">
          <template #title>
            <AFlex justify="space-between">
              <span>&nbsp;</span>
              <a-tag color="cyan" v-if="copyValue != null">{{copyValue}}</a-tag>
              <span style="margin-right: 24px; color: #a8a8a8; font-weight: 0; font-size: 12px;">注: 点击指令进行复制</span>
            </AFlex>
          </template>
          <template #commandValue="{record}">
            <a-typography-paragraph :copyable="{text: record.commandValue, tooltip: false, onCopy: () => copy(record)}" style="margin-bottom: 0px">
              {{record.commandName}}
            </a-typography-paragraph>
          </template>
        </UTable>
      </UTabPane>
      <UTabPane key="protocol" tab="协议驱动">
        <!--              <UViewSearch>-->
        <UButton func="query" :url="`/iot/protocol/apis?id=${modelDetailModel['protocolId'] || ''}`" v-show="false">查询</UButton>
        <!--              </UViewSearch>-->
        <UViewTable :columns="protocolColumns" rowKey="code" :pagination="false" size="small">
          <template #title>
            <span style="color: #a8a8a8;">注: 以下协议列表由产品所属驱动({{modelDetailModel['protocolName']}})提供</span>
          </template>
        </UViewTable>
      </UTabPane>
    </UTabs>
  </div>
</template>
<script>
import AceScriptEditor from "@/components/code/AceScriptEditor.vue";
import {computed, reactive, ref, shallowRef, watchEffect} from "vue";
import CoreConsts from "@/components/CoreConsts.js";
import {GET} from "@/utils/request.js";
import {QuestionCircleOutlined} from "@antdv-next/icons";
import UTabPane from "@/components/view/UTabPane.jsx";
import UTable from "@/components/table/BasicTable.jsx";

export default {
  name: "ThingModel",
  components: {UTable, UTabPane, AceScriptEditor, QuestionCircleOutlined},
  props: {
    modelDetailModel: {
      type: Object,
      default: () => {}
    }
  },
  setup(props) {
    let attrModelQueryRef = ref(null);
    let attrWarnQueryRef = ref(null);
    let apiType = ref([
      {label: '功能', value: 'func'},
      {label: '事件', value: 'event'},
    ]);
    let debugOptions = ref([
      {label: '是', value: true},
      {label: '否', value: false},
    ]);
    let calcScriptStatus = ref([
      {label: '启用', value: 'javascript'},
      {label: '禁用', value: 'disabled'},
    ]);
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
    let cmdColumns = ref([
      {field: "apiName", title: "接口名称", width: 260, resizable: true},
      {field: "commandValue", title: "指令"},
      {field: "remark", title: "指令说明", width: 320, resizable: true},
      {field: "apiTypeName", title: "接口类型", width: 80},
    ]);

    let copyValue = ref();
    let funcApis = ref({});
    let funcEditModel = ref({type: 'func'});
    let funcApiDownConfig = ref({});
    let eventApis = ref({});
    let eventEditModel = ref({type: 'event'});
    let eventApiDownConfig = ref([]);
    let calcAttrCompletions = ref([]);
    let attrDictSearchModel = ref({}); // 物模型
    let attrDetailModel = ref({}); // 物模型属性详情
    let ModelAttrList = ref([]); // 物模型属性列表
    let modelAttrDictList = ref([]);
    let triggerModeOptions = ref([
      {label: '主动', value: 'active'},
      {label: '被动', value: 'passive'},
    ]);
    let collectTagOptions = ref([
      {label: '默认', value: 'DEFAULT'},
      {label: '低频', value: 'LOW'},
      {label: '中频', value: 'MEDIUM'},
      {label: '高频', value: 'HIGH'},
    ]);
    let attrFieldOptions = computed(() => {
      let attrDataType = attrDetailModel.value['dataType'];
      return ModelAttrList.value.filter(item => {
        let dataType = item['dataType'];
        let status = item['field'] != attrDetailModel.value['field'];
        if(attrDataType === 'group') {
          return status;
        } else if(attrDataType === 'bit') {
          return status && (dataType != 'bit' && dataType != 'json' && dataType != 'group')
        } else if(attrDataType === 'json') {
          return status && dataType != 'json' && dataType != 'group';
        } else {
          return status;
        }
      }).map(item => {
        return {label: item.name, value: item.id + '', field: item['field'], dataType: item['dataType']}
      })
    });
    let attrValueColumns = ref([
      {field: "dictName", title: "名称", width: 200, editable: true},
      {field: "dictValue", title: "值", width: 200, editable: true},
      {field: "action", title: "操作", type: 'action', width: 180},
    ]);
    let attrJsonColumns = ref([
      {field: "dictName", title: "名称", width: 180, editable: true},
      {field: "attrField", title: "字段", width: 160, editable: false},
      {field: "dataType", title: "数据类型", width: 120, editable: false},
      {field: "path", title: "Json路径/比特位/值", width: 180, editable: true},
      {field: "action", title: "操作", type: 'action', width: 220},
    ]);
    let attrComputeColumns = computed(() => {
      let dataType = attrDetailModel.value['dataType'];
      if(dataType == 'json' || dataType == 'bit' || dataType == 'group') {
        return attrJsonColumns.value;
      } else {
        return attrValueColumns.value;
      }
    })

    let attrRules = reactive({
      name: {required: true, message: '属性名称必填'},
      field: [{required: true, message: '属性字段必填'}, {pattern: /^[a-zA-Z][a-zA-Z0-9_\-]{1,35}$/, message: '只能用2位以上字符[a-zA-Z0-9_-]且以字母开头'}],
      attrType: {required: true, message: '属性类型必填'},
      dataType: {required: true, message: '数据类型必填'},
    })
    let apiRules = reactive({
      code: {pattern: /^[a-zA-Z][a-zA-Z0-9-_]{5,63}$/, message: '只能用6位以上字符[a-zA-Z0-9-_]且以字母开头'},
    })
    let attrType = ref([
      {label: '读', value: 'R'},
      {label: '写', value: 'W'},
      {label: '读写', value: 'RW'},
    ]);
    let attrColumns = ref([
      {field: 'sort', title: '序号', width: 80},
      {field: "name", title: "属性名称", width: 160, resizable: true},
      {field: "field", title: "属性字段", width: 130, resizable: true},
      {field: "origin", title: "属性来源", width: 80},
      {field: "defaultValue", title: "默认值", width: 100},
      {field: "dataType", title: "数据类型", width: 120, options: dataType.value},
      {field: "attrType", title: "属性类型", width: 80, options: attrType.value},
      {field: "ctrlStatus", title: "控制属性", width: 80, options: debugOptions.value},
      {field: "unit", title: "单位", width: 80},
      {field: "accuracy", title: "精度", width: 80},
      {field: "gain", title: "增益", width: 80},
      {field: "resolver", title: "解析器", width: 160, resizable: true},
      {field: "remark", title: "属性说明", ellipsis: true, width: 220, resizable: true},
      {field: 'action', title: '操作', type: 'action', fixed: 'right', width: 200},
    ])
    let warnIndicatorOptions = shallowRef([
      {label: '偏高', value: '偏高'},
      {label: '偏低', value: '偏低'},
      {label: '正常', value: '正常'},
      {label: '过高', value: '过高'},
      {label: '过低', value: '过低'},
    ]);
    let attrWarnColumns = shallowRef([
      {field: 'operator', title: '操作*', width: 110, editable: true, url: '/iot/linkage/operators'},
      {field: 'threshold', title: '阈值*', width: 160, editable: true},
      {field: 'grade', title: '告警等级*', width: 80, editable: true, url: '/iot/warnConfig/gradesRmStatus'},
      {field: 'frequency', title: '频率(秒)*', width: 80, editable: true},
      {field: 'indicator', title: '指标', width: 100, editable: true},
      {field: 'messageTemplateId', title: '推送消息', width: 100, editable: true
        , url: '/core/messageTemplate/list?type=iot:warn', labelField: 'templateName', valueField: 'id'},
      {field: 'template', title: '告警内容', editable: true},
      {field: 'action', title: '操作', type: 'action', fixed: 'right', width: 200},
    ])
    let isAttrContainer = computed(() => {
      let dataType = attrDetailModel.value['dataType'];
      return dataType == 'json' || dataType == 'bit' || dataType == 'group';
    })
    let apiColumns = ref([
      {field: 'sort', title: '序号', width: 80},
      {field: "name", title: "功能名称", width: 200},
      {field: "code", title: "接口代码", width: 200},
      {field: "direct", title: "指令代码", width: 160},
      {field: "triggerMode", title: "触发方式", width: 100, options: triggerModeOptions},
      // {field: "debug", title: "是否调试", width: 80, options: debugOptions},
      {field: "remark", title: "功能说明", ellipsis: true, width: 220, resizable: true},
      {field: "asStatus", title: "状态位", width: 80, options: debugOptions, fixed: 'right'},
      {field: 'action', title: '操作', type: 'action', fixed: 'right', width: 200},
    ])

    let funcDownConfigColumns = ref([
      {field: "protocolAttrName", title: "名称", width: 150},
      {field: "protocolAttrField", title: "协议属性", width: 130},
      {field: "dataType", title: "数据类型", width: 100},
      {field: "value", title: "配置值", width: 280, resizable: true, editable: true},
      {field: "remark", title: "备注", width: 200, resizable: true},
    ]);
    let funcUpConfigColumns = ref([
      {field: "attrName", title: "名称", width: 150},
      {field: "protocolAttrField", title: "协议属性", width: 130},
      {field: "dataType", title: "数据类型", width: 100},
      {field: "value", title: "配置值", width: 280, resizable: true, editable: true},
      {field: "remark", title: "备注", width: 200, resizable: true},
    ]);

    let eventDownConfigColumns = ref([
      {field: "protocolAttrName", title: "名称", width: 150},
      {field: "protocolAttrField", title: "协议属性", width: 130},
      {field: "dataType", title: "数据类型", width: 100},
      {field: "value", title: "配置值", width: 280, resizable: true, editable: true},
      {field: "remark", title: "备注", width: 200, resizable: true},
    ]);

    let eventUpConfigColumns = ref([
      {field: "attrName", title: "名称", width: 150},
      {field: "protocolAttrField", title: "协议属性", width: 130},
      {field: "dataType", title: "数据类型", width: 100},
      {field: "value", title: "配置值", width: 280, resizable: true, editable: true, options: ModelAttrList.value},
      {field: "remark", title: "备注", width: 200, resizable: true},
    ]);

    let protocolColumns = ref([
      {field: "name", title: "协议名称", width: 220},
      {field: "code", title: "协议代码", width: 220},
      {field: "type", title: "接口类型", width: 100, options: apiType},
      {field: "triggerMode", title: "触发方式", width: 120, options: triggerModeOptions},
      {field: "remark", title: "功能说明"},
      // {field: 'action', title: '操作', type: 'action', fixed: 'right', width: 260},
    ])

    let eventColumns = ref([
      // {field: 'sort', title: '序号', width: 80},
      {field: "name", title: "事件名称", width: 200},
      {field: "code", title: "接口代码", width: 200},
      {field: "direct", title: "指令代码", width: 160},
      {field: "triggerMode", title: "触发方式", width: 100, options: triggerModeOptions},
      // {field: "debug", title: "是否调试", width: 80, options: debugOptions},
      // {field: "asStatus", title: "状态位", width: 80, options: debugOptions},
      {field: "remark", title: "事件说明", ellipsis: true, width: 220, resizable: true},
      {field: 'action', title: '操作', type: 'action', fixed: 'right', width: 160},
    ])
    let activeKey = ref('attr');
    // 主标签页面板声明(items化: key=切换标识, label=标签文字; 自描述直读items输出标签列表; calcLanguage未开启不含计算属性面板)
    let modelItems = computed(() => {
      let items = [
        {key: 'attr', label: '属性模型'},
        {key: 'func', label: '功能模型'},
        {key: 'event', label: '事件模型'},
      ];
      if(props.modelDetailModel?.config?.calcLanguage) {
        items.push({key: 'calcAttr', label: '计算属性'});
      }
      items.push({key: 'commands', label: '指令集'});
      items.push({key: 'protocol', label: '协议列表'});
      return items;
    });
    let attrActiveKey = ref('dict');
    let apiConfigActiveKey = ref('down');
    // 下行配置脚本解析器
    let downConfigScriptResolver = ref([]);
    let configMentionsOptions = computed(() => {
      return (record) => {
        if(record.value && record.value.startsWith('#')) {
          return downConfigScriptResolver.value;
        } else {
          return ModelAttrList.value;
        }
      }
    });

    GET('/iot/product/scripts').then(({code, message, data}) => {
      if(code == CoreConsts.SuccessCode) {
        downConfigScriptResolver.value = data.map(item => {
          return {name: item.label, field: item.value, remark: '此值将由脚本引擎构建'};
        });
      } else {
        console.error(message);
      }
    })
    watchEffect(() => {
      if(funcEditModel.value) {
        if(!funcEditModel.value['productId']) {
          funcEditModel.value['productId'] = props.modelDetailModel['id'];
        }

        if(!funcEditModel.value['protocolId']) {
          funcEditModel.value['protocolId'] = props.modelDetailModel['protocolId'];
        }
      }

      if(eventEditModel.value) {
        if(!eventEditModel.value['productId']) {
          eventEditModel.value['productId'] = props.modelDetailModel['id'];
        }

        if(!eventEditModel.value['protocolId']) {
          eventEditModel.value['protocolId'] = props.modelDetailModel['protocolId'];
        }
      }
    })
    return {dataType, activeKey, modelItems, apiConfigActiveKey, attrActiveKey, attrColumns, attrWarnColumns, isAttrContainer, apiColumns, funcDownConfigColumns
      , funcUpConfigColumns, eventDownConfigColumns, eventUpConfigColumns, protocolColumns, eventColumns, attrFieldOptions, attrComputeColumns
      , modelAttrDictList, ModelAttrList, funcApis, funcEditModel, funcApiDownConfig, eventApis, eventEditModel, eventApiDownConfig, attrDetailModel
      , attrDictSearchModel, attrModelQueryRef, attrWarnQueryRef, attrType, attrRules, apiRules, cmdColumns, copyValue, triggerModeOptions, collectTagOptions
      , calcAttrCompletions, configMentionsOptions, warnIndicatorOptions, calcScriptStatus}
  },
  mounted() {
    this.$http.get("/iot/product/calcScriptMethods").then(({code, message, data}) => {
      if(code == CoreConsts.SuccessCode) {
        if(data && data.length > 0) {
          data.forEach(item => this.calcAttrCompletions.push(item))
        }
      } else {
        console.error(message);
      }
    });
  },
  methods: {
    apiChange(value, selectModel) {
      if(selectModel['type'] == 'func') {
        this.funcEditModel['remark'] = selectModel['remark'];
        this.funcEditModel['funcType'] = selectModel['funcType'];
        this.funcEditModel['triggerMode'] = selectModel['triggerMode'];
        if(selectModel['downConfigs']) {
          this.funcEditModel['downConfig'] = selectModel['downConfigs'].map((item, index) => {
            return {protocolAttrField: item['protocolAttrField'], protocolAttrName: item['attrName'], protocolDataType: item['dataType'], dataType: item['dataType'], options: item['options']
              , remark: item['remark'], direction: 'DOWN', value: null, productId: this.modelDetailModel['id'], sort: index + 1, attrName: item['attrName'], fieldType: item['fieldType']}
          })
        }
        if(selectModel['upConfigs']) {
          this.funcEditModel['upConfig'] = selectModel['upConfigs'].map((item, index) => {
            return {protocolAttrField: item['protocolAttrField'], protocolAttrName: item['attrName'], protocolDataType: item['dataType'], dataType: item['dataType']
              , remark: item['remark'], direction: 'UP', value: null, productId: this.modelDetailModel['id'], sort: index + 1, attrName: item['attrName'], fieldType: item['fieldType']}
          })
        }
      } else {
        this.eventEditModel['remark'] = selectModel['remark'];
        this.eventEditModel['funcType'] = selectModel['funcType'];
        this.eventEditModel['triggerMode'] = selectModel['triggerMode'];
        if(selectModel['downConfigs']) {
          this.eventEditModel['downConfig'] = selectModel['downConfigs'].map((item, index) => {
            return {protocolAttrField: item['protocolAttrField'], protocolAttrName: item['attrName'], protocolDataType: item['dataType'], dataType: item['dataType'], options: item['options']
              , remark: item['remark'], direction: 'DOWN', value: null, productId: this.modelDetailModel['id'], sort: index + 1, attrName: item['attrName'], fieldType: item['fieldType']}
          })
        }
        if(selectModel['upConfigs']) {
          this.eventEditModel['upConfig'] = selectModel['upConfigs'].map((item, index) => {
            return {protocolAttrField: item['protocolAttrField'], protocolAttrName: item['attrName'], protocolDataType: item['dataType'], dataType: item['dataType'], options: item['options']
              , remark: item['remark'], direction: 'UP', value: null, productId: this.modelDetailModel['id'], sort: index + 1, attrName: item['attrName'], fieldType: item['fieldType']}
          })
        }
      }
    },

    apiOpenEdit(model) {
      model['productId'] = this.modelDetailModel['id'];
      model['protocolId'] = this.modelDetailModel['protocolId'];

      let api;
      let upConfigs = model['upConfig'];
      let downConfigs = model['downConfig'];
      if(model['direct']) {
        if(model['type'] == 'func') {
          api = this.funcApis[model['direct']];
        } else {
          api = this.eventApis[model['direct']];
        }
      }

      if(downConfigs instanceof Array) {
        if(api && api['downConfigs'] instanceof Array) {
          let configs = api['downConfigs'];
          downConfigs.forEach((value, index) => {
            let config = configs[index];
            if(config) {
              value['options'] = config['options'];
            }
          });
        }
      } else {
        model['downConfig'] = [];
      }

      if(upConfigs instanceof Array) {
        if(api && api['upConfigs'] instanceof Array) {
          let configs = api['upConfigs'];
          upConfigs.forEach((value, index) => {
            let config = configs[index];
            if(config) {
              value['options'] = config['options'];
            }
          });
        }
      } else {
        model['upConfig'] = [];
      }
    },
    /**
     * 物模型属性提交拿回保存的id
     */
    modelAttrCallback({code, data, message, query}) {
      if(code == CoreConsts.SuccessCode) {
        this.attrDetailModel['id'] = data;
        this.attrDictSearchModel['modelAttrId'] = data;
        query(); // 重新加载表数据
        return Promise.reject(message || "提交成功"); // 不关闭页面
      } else {
        this.$msg.error(message);
        return Promise.reject(message);
      }
    },
    funcApisLoaded(funcApis) {
      if(funcApis != null) {
        Object.values(funcApis).forEach(item => {
          if(item.protocol === true) {
            item['disabled'] = item.protocol;
          }
        })
      }
    },
    editModelAttrDict(row) {
      if(!row['modelAttrId']) {
        row['modelAttrId'] = this.attrDetailModel['id'];
      }
    },
    editModelAttrWarn(row) {
      if(!row['modelAttrId']) {
        row['modelAttrId'] = this.attrDetailModel['id'];
      }

      if(row['frequency'] == null) {
        row['frequency'] = 0;
      }
    },
    resolveAttrDictSubmitParams(record) {
      if(!record['dictName']) {
        return Promise.reject("字典名称必填");
      }
      if(!record['dictValue']) {
        return Promise.reject("字典值必填");
      }

      return Promise.resolve(record);
    },
    resolveAttrWarnSubmitParams(record) {
      if(!record['operator']) {
        return Promise.reject("操作符必填");
      }
      if(record['threshold'] == null || record['threshold'] == '') {
        return Promise.reject("告警阈值必填");
      }
      if(!record['grade']) {
        return Promise.reject("告警等级必填");
      }
      return Promise.resolve(record);
    },
    directParamsChange(model, record) {
      if(record['protocolDataType'] == 'any') {
        record['dataType'] = model['realType'] || 'any';
      } else {
        if(record['protocolDataType'] != model['dataType']) {
          record['value'] = null;
          return this.$msg.warning(`数据类型不匹配(${model['dataType']} -> ${record['protocolDataType']})`);
        }
      }

      if(record['value'].startsWith('@')) {
        record['modelAttrId'] = model['id'];
        record['attrName'] = model['name'];
        record['attrField'] = model['field'];
        record['remark'] = model['remark'] || record['remark']
      } else {
        record['attrField'] = null;
        record['modelAttrId'] = null;
        record['attrName'] = model['name'];
        record['remark'] = `${model['name']}(由脚本引擎构建)`
      }
    },
    eventUpValueChange(value, model, record) {
      if(record['protocolDataType'] == 'any') {
        record['dataType'] = model['realType'] || 'any';
      } else {
        if(record['protocolDataType'] != model['dataType']) {
          record['value'] = null;
          return this.$msg.warning(`数据类型不匹配(${model['dataType']} -> ${record['protocolDataType']})`);
        }
      }

      record['modelAttrId'] = model['id'];
      record['attrName'] = model['name'];
      record['attrField'] = model['field'];
      record['remark'] = model['remark'] || record['remark']
    },
    apiConfigResolve(data) {
      let downConfig = data['downConfig'];
      if(downConfig instanceof Array) {
        for (let i = 0; i < downConfig.length; i++) {
          let item = downConfig[i];
          if(!item['value']) {
            this.$msg.warning(`下行配置项[${item['protocolAttrName']}]必填`)
            return Promise.reject("");
          }
        }
      }
      let upConfig = data['upConfig'];
      if(upConfig instanceof Array) {
        for (let i = 0; i <upConfig.length; i++) {
          let item = upConfig[i];
          if(item['protocolDataType'] == 'any') {
            if(!item['value']) {
              this.$msg.warning(`上行配置项[${item['protocolAttrName']}]值必填`);
              return Promise.reject("");
            }
          }
        }
      }
      return Promise.resolve(data);
    },
    attrFieldChange(value, model, record) {
      let dictList = this.modelAttrDictList;
      if(dictList != null) {
        if(dictList.filter(item => item['dictValue'] == value && record != item).length > 0) {
          record['dictValue'] = null;
          this.$msg.warning("名称不允许重复"); return;
        }
      }
      record['dictValue'] = value;
      record['attrField'] = model['field'];
      record['dictName'] = model['label'];
      record['dataType'] = model['dataType'];
      record['modelAttrId'] = this.attrDetailModel['id'];
    },
    paneChange(activeKey) {
      if(activeKey == 'commands') {
        this.$nextTick().then(() => {
          let cmdTable = this.$refs['cmdTable'];
          let productId = this.modelDetailModel.id;
          this.$http.get(`/iot/product/commands?productId=${productId}`).then(({code, data, message}) => {
            if(code == CoreConsts.SuccessCode) {
              cmdTable.getTableContext().setDataSource(data);
            } else {
              this.$msg.error(message);
            }
          })
        })
      } else {
        this.copyValue = null;
      }
    },
    attrEdit(row) {
      row['origin'] = 'Model';
      if(!row['productId']) {
        row['productId'] = this.modelDetailModel['id'];
      }
      this.$nextTick().then(() => {
        this.attrDictSearchModel['modelAttrId'] = row['id'] || null
        this.attrModelQueryRef.trigger();
        if(this.attrWarnQueryRef) {
          this.attrWarnQueryRef.trigger();
        }
      })
    },
    updateCalcScript() {
      return this.$http.post(`/iot/product/updateCalcScript`, this.modelDetailModel).then(({code, message}) => {
        if(code == CoreConsts.SuccessCode) {
          this.$msg.success(message);
        } else {
          this.$msg.error(message);
        }

        return message;
      })
    },
    copy(value) {
      this.copyValue = "成功复制 " + value.commandValue;
    },
    attrPaneChange(activeKey) {

    }
  }
}
</script>
<style scoped>

</style>