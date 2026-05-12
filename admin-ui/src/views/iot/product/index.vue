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
                :disabled="record['ctrlMode'] != 'POINT' || record['deviceType'] == 'Gateway'" @click="() => batchModel(record)">批量</UTag>
          <UTag func="del" :data="record" url="/iot/product/del" v-auth="'iot:product:del'">删除</UTag>
        </template>
      </UViewTable>
    <UDrawer uid="model" :title="'物模型配置['+modelDetailModel['code']+']'" placement="bottom" size="100%" v-model:source="modelDetailModel" :push="false" @close="modelClose">
      <template #extra>
        <span style="color: #fc804a; font-size: 13px;">所有修改将在重启产品后生效</span>
<!--        <UButton size="small" v-auth="'iot:product:switch'" :url="`/iot/product/switch/${modelDetailModel['id']}/disabled`" danger-->
<!--                func="ajax:confirm" type="primary" :config="{confirmTitle: `确认停用产品[${modelDetailModel.name}]`, confirmContent: `将停用此产品下的所有设备`}">-->
<!--          停用-->
<!--        </UButton>-->
      </template>
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
        <a-tabs v-model:activeKey="activeKey" @change="paneChange" style="margin-top: 16px">
<!--          <template #rightExtra>-->
<!--            <AButton type="dashed">快速导入</AButton>-->
<!--          </template>-->
          <a-tab-pane key="attr" tab="属性模型">
            <UChildView uid="attr">
              <UViewSearch>
                <UInputItem label="名称" field="name" allowClear/>
                <UInputItem label="字段" field="field" allowClear/>
                <USelectItem label="数据类型" field="dataType" style="width: 150px" :options="dataType" allowClear/>
                <UButton func="query" :url="`/iot/modelAttr/list?productId=${modelDetailModel['id']}`">查询</UButton>
                <UButton func="add" v-auth="'iot:modelAttr:add'">新增</UButton>
              </UViewSearch>
              <UViewTable :columns="attrColumns" v-model:source="ModelAttrList" size="small" :scroll="{x: 1080}">
                <template #action="{record, editing}">
                  <UTag func="ajax" :data="{id: record['id'], status: !record['ctrlStatus']}" :url="`/iot/modelAttr/ctrlStatus`"
                        :disabled="record['attrType'] == 'R'">控制属性</UTag>
                  <UTag func="edit" :data="record" url="/iot/modelAttr/edit" v-auth="'iot:modelAttr:edit'" :disabled="record['origin']=='Protocol'">修改</UTag>
                  <UTag func="del" :data="record" url="/iot/modelAttr/del" v-auth="'iot:modelAttr:del'" :disabled="record['origin']=='Protocol'">删除</UTag>
                </template>
              </UViewTable>
              <UViewDrawer title="物模型属性配置" placement="right" @edit="attrEdit" v-model="attrDetailModel" size="920" :span="[7, 17]" :rules="attrRules">
                <template #default="{model}">
                  <URow :gutter="6" :responsive="3">
                    <UInputItem label="属性名称" field="name" :maxlength="18"/>
                    <UInputItem label="属性字段" field="field" :maxlength="36"/>
                    <USelectItem label="数据类型" field="dataType" :options="dataType" />
                    <USelectItem label="属性类型" field="attrType" :options="attrType" defaultValue="R"/>
                    <UInputItem label="默认值" field="defaultValue" :disabled="attrDetailModel['attrType'] == 'R'" :maxlength="430"/>
                    <UInputItem label="属性单位" field="unit" :maxlength="18"/>
                    <USelectItem label="值解析器" required field="resolver" url="/iot/product/resolvers" defaultValue="DEFAULT" />
                    <UInputNumberItem label="精度/增益" class="iv-group-addon-padding-0" field="accuracy" placeholder="精度值" :min="0" :max="5" :step="1" :precision="0">
                      <template #addonAfter>
                        <AInputNumber v-model:value="model['gain']" placeholder="增益值" :bordered="false" :step="1" :precision="5"
                                      :min="0" :max="10000" style="width: 110px; background-color: #ffffff;" />
                      </template>
                    </UInputNumberItem>
                    <UInputItem label="属性备注" field="remark" :maxlength="64"/>
                  </URow>
                  <a-divider dashed style="margin: 0;">属性值配置(适用于枚举、json、bit、group类型)</a-divider>
                  <UChildView uid="attrValue">
                    <UViewSearch v-model="attrDictSearchModel">
                      <UInputItem label="名称" field="dictName"/>
                      <UButton func="query" url="/iot/modelAttr/dict/view" ref="attrModelQueryRef">查询</UButton>
                      <UButton func="add" :disabled="attrDetailModel['id'] == null">新增</UButton>
                      <span style="color: rgba(168,168,168,0.44); font-size: 13px; height: 32px; line-height: 32px;">先保存数据后才能新增</span>
                    </UViewSearch>
                    <UViewEditable :columns="attrComputeColumns" @edit="editModelAttrDict" :pagination="false" size="small" v-model:source="modelAttrDictList">
                      <template #dictName="{record, editing}">
                        <AInput v-if="attrDetailModel['dataType']!='json' && attrDetailModel['dataType']!='bit' && attrDetailModel['dataType'] != 'group'" placeholder="请输入名称" v-model:value="record['dictName']" size="small" :bordered="false" />
                        <ASelect v-else v-model:value="record['dictValue']" size="small" :options="attrFieldOptions"
                                 :bordered="false" placeholder="请选择属性" style="width: 100%" @change="(value, model) => attrFieldChange(value, model, record)"/>
                      </template>
                      <template #dictValue="{record, editing}">
                        <AInput v-if="attrDetailModel['dataType']!='json' && attrDetailModel['dataType']!='bit' && attrDetailModel['dataType'] != 'group'" v-model:value="record['dictValue']" placeholder="请输入值" size="small" :bordered="false" />
                      </template>
                      <template #path="{record, editing}">
                        <AInput v-model:value="record['path']" size="small" :bordered="false" placeholder="$.xx或比特位或值" />
                      </template>
                      <template #action="{record, editing}">
                        <UTag func="edit" :data="record">修改</UTag>
                        <UTag func="cancel" :data="record">取消</UTag>
                        <UTag func="submit" :data="record" url="/iot/modelAttr/dict/saveOrUpdate" :params="resolveAttrDictSubmitParams">保存</UTag>
                        <UTag func="del" :data="record" url="/iot/modelAttr/dict/del">删除</UTag>
                      </template>
                    </UViewEditable>
                  </UChildView>
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
            </UChildView>
          </a-tab-pane>
          <a-tab-pane key="func" tab="功能模型">
            <UChildView uid="func">
              <UViewSearch>
                <UInputItem label="名称" field="name"/>
                <UInputItem label="代码" field="code"/>
                <UButton func="query" :url="`/iot/modelApi/view?type=func&productId=${modelDetailModel['id']}`">查询</UButton>
                <UButton func="add" v-auth="'iot:modelApi:add'">新增</UButton>
              </UViewSearch>
              <UViewModal :title="`功能配置[${modelDetailModel['code']}]`" :span="[7, 15]" width="920px" v-model="funcEditModel" :centered="false" @edit="apiOpenEdit" :rules="apiRules">
                <URow :responsive="2">
                  <UInputItem label="功能名称" field="name" required :maxlength="18" />
                  <UInputItem label="接口代码" field="code" :maxlength="64" required :disabled="funcEditModel['id'] != null" />
                  <USelectItem label="协议指令" field="direct" v-model:source="funcApis" labelField="name" valueField="code" required
                               url="/iot/protocol/apis?id={protocolId}&type=func" @change="apiChange" @loaded="funcApisLoaded" :disabled="funcEditModel['id'] != null"/>
                  <URadioItem label="触发方式" field="triggerMode" :options="triggerModeOptions" :disabled="true" />
                  <URadioItem label="是否调试" field="debug" :options="debugOptions" :defaultValue="false" :disabled="funcEditModel?.['triggerMode']=='active'" />
                  <UInputItem label="功能说明" field="remark" :maxlength="64" />
                  <a-divider dashed>指令配置(可用@提及属性、#提及执行脚本)</a-divider>
                  <a-tabs v-model:activeKey="apiConfigActiveKey" style="width: 873px">
                    <a-tab-pane key="down" tab="下行配置">
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
                          <UMentions v-else v-model:value="record['value']" labelField="name" valueField="field" size="small" :options="configMentionsOptions(record)"
                                     :prefix="['@', '#']" split="" @select="(model) => directParamsChange(model, record)" :maxlength="360" />
                        </template>
                      </UTable>
                    </a-tab-pane>
                    <a-tab-pane key="up" tab="上行配置">
                      <UTable uid="upConfig" :pagination="false" :dataSource="funcEditModel['upConfig']" :scroll="{x: 870}" :columns="funcUpConfigColumns" size="small">
                        <template #value="{record}">
                          <span v-if="record['protocolDataType'] != 'any' && record['protocolDataType'] != 'json'"></span>
                          <UMentions v-else v-model:value="record['value']" labelField="name" valueField="field" size="small" :options="ModelAttrList"
                                     split="" @select="(model) => directParamsChange(model, record)" :maxlength="360" />
                        </template>
                      </UTable>
                    </a-tab-pane>
                  </a-tabs>
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
                        :url="`/iot/modelApi/asStatus?productId=${record.productId}&code=${record.code}`" :disabled="record['asStatus']"
                        :config="{confirmTitle: '确认将此接口作为控制设备开/关等接口?', confirmContent: '声明此接口将用于控制设备的开启、关闭等'}">控制状态</UTag>
                  <UTag func="ajax:confirm" v-else-if="record['triggerMode']=='passive' && record['funcType']=='R' && modelDetailModel['deviceType'] == 'Child'" :data="{}"
                        :url="`/iot/modelApi/asStatus?productId=${record.productId}&code=${record.code}`" :disabled="record['asStatus']"
                        :config="{confirmTitle: '确认将此接口作为在线状态接口?', confirmContent: '声明此接口将用于采集设备的状态(在线/离线)'}">在线状态</UTag>
                  <UTag func="edit" :data="record" url="/iot/modelApi/edit" v-auth="'iot:modelApi:edit'">修改</UTag>
                  <UTag func="del" :data="record" url="/iot/modelApi/del" :disabled="record.protocol === true" v-auth="'iot:modelApi:del'">删除</UTag>
                </template>
              </UViewTable>
            </UChildView>
          </a-tab-pane>
          <a-tab-pane key="event" tab="事件模型">
            <UChildView uid="event">
              <UViewSearch>
                <UInputItem label="名称" field="name"/>
                <UInputItem label="代码" field="code"/>
                <UButton func="query" :url="`/iot/modelApi/view?type=event&productId=${modelDetailModel['id']}`">查询</UButton>
                <UButton func="add" v-auth="'iot:modelApi:add'">新增</UButton>
              </UViewSearch>
              <UViewModal :title="`事件配置[${modelDetailModel['code']}]`" :span="[7, 15]" width="920px" v-model="eventEditModel" :centered="false" @edit="apiOpenEdit" :rules="apiRules">
                <URow :responsive="2">
                  <UInputItem label="事件名称" field="name" required :maxlength="18" />
                  <UInputItem label="接口代码" field="code" :maxlength="64" required :disabled="eventEditModel['id'] != null"/>
                  <USelectItem label="协议指令" field="direct" v-model:source="eventApis" labelField="name" valueField="code" required
                               url="/iot/protocol/apis?id={protocolId}&type=event" @change="apiChange" @loaded="funcApisLoaded" :disabled="eventEditModel['id'] != null"/>
                  <URadioItem label="触发方式" field="triggerMode" :options="triggerModeOptions" :disabled="true" />
                  <USelectItem label="频率标签" field="collectTag" :options="collectTagOptions" defaultValue="DEFAULT" required/>
                  <UInputItem label="事件说明" field="remark" :maxlength="64"/>
                  <a-divider dashed>指令配置(可用@提及属性)</a-divider>
                  <a-tabs v-model:activeKey="apiConfigActiveKey" style="width: 873px">
                    <a-tab-pane key="down" tab="下行配置">
                      <UTable uid="downConfig" :pagination="false" :dataSource="eventEditModel['downConfig']" :scroll="{x: 870}" :columns="eventDownConfigColumns" size="small">
                        <template #value="{record}">
                          <ASelect v-if="record['options']" style="width: 100%;" v-model:value="record['value']" :options="record['options']">
                            <template #option="{label, value, extra}">
                              <div>
                                <span>{{label}}</span>
                                <span style="float: right; color: #a3a3a3; font-size: 12px; margin-top: 2px">{{extra}}</span>
                              </div>
                            </template>
                          </ASelect>
                          <UMentions v-else v-model:value="record['value']" labelField="name" valueField="field" :options="ModelAttrList"
                                     split="" @select="(model) => directParamsChange(model, record)" :maxlength="360"/>
                        </template>
                      </UTable>
                    </a-tab-pane>
                    <a-tab-pane key="up" tab="上行配置">
                      <UTable uid="upConfig" :pagination="false" :dataSource="eventEditModel['upConfig']" :scroll="{x: 870}" :columns="eventUpConfigColumns" size="small">
                        <template #value="{record}">
                          <ASelect v-if="record['options']" style="width: 100%;" v-model:value="record['value']" :options="record['options']">
                            <template #option="{label, value, extra}">
                              <div>
                                <span>{{label}}</span>
                                <span style="float: right; color: #a3a3a3; font-size: 12px; margin-top: 2px">{{extra}}</span>
                              </div>
                            </template>
                          </ASelect>
                          <span v-else-if="record['protocolDataType'] != 'any' && record['protocolDataType'] != 'json'"></span>
                          <UMentions v-else v-model:value="record['value']" labelField="name" valueField="field" :options="ModelAttrList"
                                     split="" @select="(model) => directParamsChange(model, record)" :maxlength="360" />
                        </template>
                      </UTable>
                    </a-tab-pane>
                  </a-tabs>
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
                  <UTag func="ajax:confirm" v-if="modelDetailModel['deviceType'] == 'Child'" :data="{}"
                        :url="`/iot/modelApi/asStatus?productId=${record.productId}&code=${record.code}`" :disabled="record['asStatus']"
                        :config="{confirmTitle: '确认将此接口作为在线状态接口?', confirmContent: '声明此接口将用于采集设备的状态(在线/离线)'}">在线状态</UTag>
                  <UTag func="edit" :data="record" url="/iot/modelApi/edit" v-auth="'iot:modelApi:edit'">修改</UTag>
                  <UTag func="del" :data="record" url="/iot/modelApi/del" v-auth="'iot:modelApi:del'">删除</UTag>
                </template>
              </UViewTable>
            </UChildView>
          </a-tab-pane>
          <a-tab-pane key="calcAttr" tab="计算属性" v-if="modelDetailModel?.config?.calcLanguage">
            <ARow>
              <ACol span="16">
                <AceScriptEditor style="min-height: 320px;" v-model:value="modelDetailModel.config.calcScript"  :completions="calcAttrCompletions"></AceScriptEditor>
              </ACol>
              <ACol span="8" style="position: relative">
                <div style="padding: 8px; font-size: 12px; color: #8e8e8e;">
                  <div>适用场景：有些属性可以通过对采集的值进行计算后得到</div>
                  <div style="color: #1c79f4">使用方法：通过methods对象提供如下方法：</div>
                  <div style="height: 220px; overflow-y: auto;">
                    <div class="method-item" v-for="(item, index) in calcAttrCompletions" :key="index">
                      <span class="method-value">{{item.value}}</span><span class="method-label">{{item.meta}}</span>
                    </div>
                  </div>
                </div>
                <div style="position: absolute; bottom: 8px; text-align: center; width: 100%;">
                  <ARadioGroup button-style="solid" v-model:value="modelDetailModel.config['calcLanguage']" @change="updateCalcScript">
                    <ARadioButton value="javascript">javascript</ARadioButton>
                    <ARadioButton value="disabled">禁用</ARadioButton>
                  </ARadioGroup>
                  <AButton type="primary" style="margin-left: 8px;" @click="updateCalcScript">保存</AButton>
                </div>
              </ACol>
            </ARow>
          </a-tab-pane>
          <a-tab-pane key="commands" tab="指令集">
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
          </a-tab-pane>
          <a-tab-pane key="protocol" tab="协议列表">
            <UChildView uid="protocol">
              <!--              <UViewSearch>-->
              <UButton func="query" :url="`/iot/protocol/apis?id=${modelDetailModel['protocolId'] || ''}`" v-show="false">查询</UButton>
              <!--              </UViewSearch>-->
              <UViewTable :columns="protocolColumns" rowKey="code" :pagination="false" size="small">
                <template #title>
                  <span style="color: #a8a8a8;">注: 以下协议列表由产品所属驱动({{modelDetailModel['protocolName']}})提供</span>
                </template>
              </UViewTable>
            </UChildView>
          </a-tab-pane>
        </a-tabs>
      </div>
    </UDrawer>
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
    <ADrawer v-model:open="batchActive" :title="`批量物模型配置(${modelDetailModel.name})`" placement="bottom" size="100%"
             :styles="{body: {padding: '6px'}}">
      <UChildView uid="batchModel">
        <UViewSearch style="margin-bottom: 0;" v-model="batchSearchModel">
          <UInputItem label="属性名称" field="name"/>
          <UInputItem label="属性字段" field="field"/>
          <USelectItem label="频率标签" field="collectTag" :options="collectTagOptions" allowClear style="width: 180px"/>
          <UButton func="query" url="/iot/product/listBatchModel" ref="batchModelQueryRef">查询</UButton>
          <UButton func="add">新增</UButton>
        </UViewSearch>
        <UViewEditable :columns="batchColumns" :pagination="false" size="small" :scroll="{x: 1200}" @edit="onBatchEditModel">
          <template #action="{record, editing}">
            <UTag func="edit" :data="record">修改</UTag>
            <UTag func="cancel" :data="record">取消</UTag>
            <UTag func="submit" :data="record" url="/iot/product/batchModelSaveOrUpdate" :params="batchSubmitValidate">保存</UTag>
            <UTag func="del" :data="record" :params="{id: record.id, apiCode: record.apiCode}" url="/iot/product/batchModelDel">删除</UTag>
          </template>
        </UViewEditable>
      </UChildView>
    </ADrawer>
  </UView>
</template>
<!-- 产品功能 -->
<script>
import {ref, reactive, watch, watchEffect, computed, h} from "vue";
import CoreConsts from "@/components/CoreConsts";
import {useRoute} from "vue-router";
import {GET} from "@/utils/request";
import {QuestionCircleOutlined} from "@antdv-next/icons";
import {USelect} from "@/components/form/basic";
import {Input, InputNumber} from "antdv-next";
import {getMetaValue, setMetaValue} from "@/utils/MetaUtils";
import AceScriptEditor from "@/components/code/AceScriptEditor.vue";
export default {
  name: "Product",
  components: {AceScriptEditor, QuestionCircleOutlined},
  setup() {
    let route = useRoute();
    let attrModelQueryRef = ref(null);
    let batchModelQueryRef = ref(null);
    let apiType = ref([
      {label: '功能', value: 'func'},
      {label: '事件', value: 'event'},
    ]);
    let debugOptions = ref([
      {label: '是', value: true},
      {label: '否', value: false},
    ]);
    let statusOptions = ref([
      {label: '启用', value: 'enabled'},
      {label: '禁用', value: 'disabled'},
    ]);
    let funcTypeOptions = ref([
      {label: '读', value: 'R'},
      {label: '写', value: 'W'},
      {label: '读写', value: 'RW'},
    ]);
    let triggerModeOptions = ref([
      {label: '主动触发', value: 'active'},
      {label: '被动触发', value: 'passive'},
    ]);
    let collectTagOptions = ref([
      {label: '默认', value: 'DEFAULT'},
      {label: '低频', value: 'LOW'},
      {label: '中频', value: 'MEDIUM'},
      {label: '高频', value: 'HIGH'},
    ]);
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

    let attrRules = reactive({
      name: {required: true, message: '属性名称必填'},
      field: [{required: true, message: '属性字段必填'}, {pattern: /^[a-zA-Z][a-zA-Z0-9_\-]{1,35}$/, message: '只能用2位以上字符[a-zA-Z0-9_-]且以字母开头'}],
      attrType: {required: true, message: '属性类型必填'},
      dataType: {required: true, message: '数据类型必填'},
    })
    let apiRules = reactive({
      code: {pattern: /^[a-zA-Z][a-zA-Z0-9-_]{5,63}$/, message: '只能用6位以上字符[a-zA-Z0-9-_]且以字母开头'},
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
      // {label: '[short](短整型枚举)', value: '[short]'},
      // {label: '[ushort](无符号短整型枚举)', value: '[ushort]'},
      // {label: '[int](整型枚举)', value: '[int]'},
      // {label: '[uint](无符号整型枚举)', value: '[uint]'},
      // {label: '[long](长整型枚举)', value: '[long]'},
      // {label: '[float](单精度枚举)', value: '[float]'},
      // {label: '[double](双精度枚举)', value: '[double]'},
      // {label: '[string](字符串枚举)', value: '[string]'},
    ]);
    let attrType = ref([
      {label: '读', value: 'R'},
      {label: '写', value: 'W'},
      {label: '读写', value: 'RW'},
    ]);
    let attrColumns = ref([
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
      {field: 'action', title: '操作', type: 'action', fixed: 'right', width: 260},
    ])
    let calcAttrCompletions = ref([]);
    let attrDetailModel = ref({}); // 物模型属性详情
    let ModelAttrList = ref([]); // 物模型属性列表
    let modelAttrDictList = ref([]);
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
        return {label: item.name, value: item.id, field: item['field'], dataType: item['dataType']}
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
    let apiColumns = ref([
      {field: "name", title: "功能名称", width: 200},
      {field: "code", title: "接口代码", width: 200},
      {field: "direct", title: "指令代码", width: 160},
      {field: "triggerMode", title: "触发方式", width: 100, options: triggerModeOptions},
      {field: "debug", title: "是否调试", width: 80, options: debugOptions},
      {field: "asStatus", title: "状态位", width: 80, options: debugOptions},
      {field: "remark", title: "功能说明", ellipsis: true, width: 220, resizable: true},
      {field: 'action', title: '操作', type: 'action', fixed: 'right', width: 200},
    ])

    let funcDownConfigColumns = ref([
      {field: "protocolAttrName", title: "名称", width: 150},
      {field: "protocolAttrField", title: "协议属性", width: 130},
      {field: "dataType", title: "数据类型", width: 100},
      {field: "value", title: "配置值", width: 280, resizable: true},
      {field: "remark", title: "备注", width: 200, resizable: true},
    ]);
    let funcUpConfigColumns = ref([
      {field: "attrName", title: "名称", width: 150},
      {field: "protocolAttrField", title: "协议属性", width: 130},
      {field: "dataType", title: "数据类型", width: 100},
      {field: "value", title: "配置值", width: 280, resizable: true},
      {field: "remark", title: "备注", width: 200, resizable: true},
    ]);

    let eventDownConfigColumns = ref([
      {field: "protocolAttrName", title: "名称", width: 150},
      {field: "protocolAttrField", title: "协议属性", width: 130},
      {field: "dataType", title: "数据类型", width: 100},
      {field: "value", title: "配置值", width: 280, resizable: true},
      {field: "remark", title: "备注", width: 200, resizable: true},
    ]);

    let eventUpConfigColumns = ref([
      {field: "attrName", title: "名称", width: 150},
      {field: "protocolAttrField", title: "协议属性", width: 130},
      {field: "dataType", title: "数据类型", width: 100},
      {field: "value", title: "配置值", width: 280, resizable: true},
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
      {field: "name", title: "事件名称", width: 200},
      {field: "code", title: "接口代码", width: 200},
      {field: "direct", title: "指令代码", width: 160},
      {field: "triggerMode", title: "触发方式", width: 100, options: triggerModeOptions},
      {field: "debug", title: "是否调试", width: 80, options: debugOptions},
      {field: "asStatus", title: "状态位", width: 80, options: debugOptions},
      {field: "remark", title: "事件说明", ellipsis: true, width: 220, resizable: true},
      {field: 'action', title: '操作', type: 'action', fixed: 'right', width: 200},
    ])

    let batchColumns = ref([])
    let batchCommonColumns = ref([
      {field: "name", title: "属性名称*", width: 120, resizable: true, editable: true},
      {field: "field", title: "属性字段*", width: 120, resizable: true, editable: true},
      {field: "dataType", title: "数据类型*", width: 120, options: dataType.value, editable: true},
      {field: "attrType", title: "属性类型*", width: 80, options: attrType.value, editable: true},
      {field: "resolver", title: "解析器*", width: 150, resizable: true, url: "/iot/product/resolvers", editable: true},
      {field: "collectTag", title: "频率标签", width: 120, editable: true, options: collectTagOptions},
      {field: "unit", title: "单位", width: 80, editable: true},
      {field: "accuracy", title: "精度", width: 80, formType: 'number', min: 0, max: 8, editable: true},
      {field: "gain", title: "增益", width: 80, formType: 'number', min: 0, editable: true},
      {field: "apiCode", title: "接口码", width: 160, resizable: true},
      {field: "apiName", title: "接口名称", width: 160, resizable: true},
    ]);
    let cmdColumns = ref([
      {field: "apiName", title: "接口名称", width: 260, resizable: true},
      {field: "commandValue", title: "指令"},
      {field: "remark", title: "指令说明", width: 320, resizable: true},
      {field: "apiTypeName", title: "接口类型", width: 80},
    ]);

    let copyValue = ref();
    let searchRef = ref(null);
    let editModel = ref({});
    let searchModel = ref({});
    let funcApis = ref({});
    let funcEditModel = ref({type: 'func'});
    let funcApiDownConfig = ref({});
    let eventApis = ref({});
    let eventEditModel = ref({type: 'event'});
    let eventApiDownConfig = ref([]);
    let activeKey = ref('attr');
    let apiConfigActiveKey = ref('down');
    let modelDetailModel = ref({id: ''}); // 物模型
    let attrDictSearchModel = ref({}); // 物模型
    let apiConfigStatus = ref(false);
    let batchActive = ref(false);
    let batchSearchModel = ref({productCode: null});
    let protocolBatchColumnsMap = ref({});

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
          funcEditModel.value['productId'] = modelDetailModel.value['id'];
        }

        if(!funcEditModel.value['protocolId']) {
          funcEditModel.value['protocolId'] = modelDetailModel.value['protocolId'];
        }
      }

      if(eventEditModel.value) {
        if(!eventEditModel.value['productId']) {
          eventEditModel.value['productId'] = modelDetailModel.value['id'];
        }

        if(!eventEditModel.value['protocolId']) {
          eventEditModel.value['protocolId'] = modelDetailModel.value['protocolId'];
        }
      }
    })

    watch(() => route.query, (newValue) => {
      let code = newValue['code'];
      if(code) {
        searchModel.value['code'] = code;
        if(searchRef.value) {
          searchRef.value.trigger();
        }
      }
    }, {immediate: true})

    return {searchRef, columns, rules, activeKey, apiConfigActiveKey, editModel, searchModel, modelDetailModel, attrColumns, funcApis, funcApiDownConfig, eventApis, attrDictSearchModel, attrComputeColumns
      , apiColumns, eventColumns, dataType, attrType, protocolColumns, apiConfigStatus, apiType, attrValueColumns, attrDetailModel, attrModelQueryRef, funcEditModel, funcDownConfigColumns, attrFieldOptions
      , funcUpConfigColumns, eventDownConfigColumns, eventUpConfigColumns, eventEditModel, eventApiDownConfig, ModelAttrList, debugOptions, statusOptions, funcTypeOptions, triggerModeOptions, attrRules
      , apiRules, modelAttrDictList, batchActive, protocolBatchColumnsMap, batchColumns, batchCommonColumns, batchModelQueryRef, batchSearchModel, cmdColumns, copyValue, configMentionsOptions, collectTagOptions
      , calcAttrCompletions}
  },
  mounted() {
    this.initBatchProtocolColumn();
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
    attrEdit(row) {
      row['origin'] = 'Model';
      if(!row['productId']) {
        row['productId'] = this.modelDetailModel['id'];
      }

      this.attrDictSearchModel['modelAttrId'] = row['id'] || null
      this.attrModelQueryRef.trigger();
    },
    selectParent(selectedKeys) {
      this.searchModel.productTypeId = selectedKeys[0];
      this.$refs['searchRef'].trigger();
    },
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
      } else {
        this.$msg.error(message);
      }
      return Promise.reject();
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
    resolveAttrDictSubmitParams(record) {
      if(!record['dictName']) {
        return Promise.reject("字典名称必填");
      }
      if(!record['dictValue']) {
        return Promise.reject("字典值必填");
      }

      return Promise.resolve(record);
    },
    eventUpConfigValueChange(value, model, record) {
      if(record['protocolDataType'] == 'any') {
        record['dataType'] = model['realType'];
      } else {
        if(record['protocolDataType'] != model['dataType']) {
          record['value'] = null;
          return this.$msg.warn(`数据类型不匹配(${model['dataType']} -> ${record['protocolDataType']})`);
        }
      }
      record['attrName'] = model['name'];
      record['modelAttrId'] = model['id'];
      record['attrField'] = model['field'];
      record['remark'] = model['remark'] || record['remark']
    },
    directParamsChange(model, record) {
      if(record['protocolDataType'] == 'any') {
        record['dataType'] = model['realType'] || 'any';
      } else {
        if(record['protocolDataType'] != model['dataType']) {
          record['value'] = null;
          return this.$msg.warn(`数据类型不匹配(${model['dataType']} -> ${record['protocolDataType']})`);
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
    apiConfigResolve(data) {
      let downConfig = data['downConfig'];
      if(downConfig instanceof Array) {
        for (let i = 0; i < downConfig.length; i++) {
          let item = downConfig[i];
          if(!item['value']) {
            this.$msg.warn(`下行配置项[${item['protocolAttrName']}]必填`)
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
              this.$msg.warn(`上行配置项[${item['protocolAttrName']}]值必填`);
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
          this.$msg.warn("名称不允许重复"); return;
        }
      }
      record['attrField'] = model['field'];
      record['dictName'] = model['label'];
      record['dataType'] = model['dataType'];
      record['modelAttrId'] = this.attrDetailModel['id'];
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
            return h(InputNumber, {...item, style: {width: '70px'},
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
      }
      if(!editRow.config) {
        editRow.config = {};
      }
    },
    batchSubmitValidate(record) {
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
    batchModel(record) {
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
      this.batchActive = true;
    },
    copy(value) {
      this.copyValue = "成功复制 " + value.commandValue;
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
    modelClose() {
      this.copyValue = null;
      this.activeKey = 'attr';
      let cmdTableRef = this.$refs['cmdTable'];
      if(cmdTableRef) {
        cmdTableRef.getTableContext().setDataSource([]);
      }
    },
    updateCalcScript() {
      this.$http.post(`/iot/product/updateCalcScript`, this.modelDetailModel).then(({code, message}) => {
        if(code == CoreConsts.SuccessCode) {
          this.$msg.success(message);
        } else {
          this.$msg.error(message);
        }
      })
    }
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
