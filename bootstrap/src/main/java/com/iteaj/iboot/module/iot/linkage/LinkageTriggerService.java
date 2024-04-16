package com.iteaj.iboot.module.iot.linkage;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.iteaj.framework.spi.iot.*;
import com.iteaj.framework.spi.iot.consts.CollectStatus;
import com.iteaj.iboot.module.iot.cache.IotCacheManager;
import com.iteaj.iboot.module.iot.cache.data.RealtimeData;
import com.iteaj.iboot.module.iot.cache.data.RealtimeDataService;
import com.iteaj.iboot.module.iot.cache.entity.RealtimeStatus;
import com.iteaj.iboot.module.iot.collect.CollectListenerManager;
import com.iteaj.iboot.module.iot.collect.model.CollectInfo;
import com.iteaj.iboot.module.iot.collect.model.EventGroupCollectListener;
import com.iteaj.iboot.module.iot.consts.ActionType;
import com.iteaj.iboot.module.iot.consts.ConditionType;
import com.iteaj.iboot.module.iot.consts.DeviceStatus;
import com.iteaj.iboot.module.iot.consts.LinkageLogical;
import com.iteaj.iboot.module.iot.dto.LinkageActionExecDto;
import com.iteaj.iboot.module.iot.entity.*;
import com.iteaj.iboot.module.iot.utils.ProtocolInvokeUtil;
import com.iteaj.iot.Message;
import com.iteaj.iot.Protocol;
import com.iteaj.iot.consts.ExecStatus;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

public class LinkageTriggerService implements EventGroupCollectListener {

    private final IotCacheManager cacheManager;
    private final RealtimeDataService realtimeDataService;
    private final LinkageCacheService linkageCacheService;
    private Logger logger = LoggerFactory.getLogger("log:linkage:trigger");

    public LinkageTriggerService(IotCacheManager cacheManager, RealtimeDataService realtimeDataService
            , LinkageCacheService linkageCacheService) {
        this.cacheManager = cacheManager;
        this.realtimeDataService = realtimeDataService;
        this.linkageCacheService = linkageCacheService;
    }

    /**
     * 设备状态事件触发
     * @param device
     */
    public void trigger(RealtimeStatus device) {
        Set<LinkageItem> linkageItems = linkageCacheService.listLinkageItem(device.getProductCode(), device.getStatus().name());
        this.invokeLinkage(linkageItems, null, device);
    }

    /**
     * 设备状态事件触发
     * @param device
     */
    public void trigger(Collection<RealtimeStatus> device) {
        if(CollectionUtil.isNotEmpty(device)) {
            device.forEach(item -> this.trigger(item));
        }
    }

    /**
     * 设备物模型属性事件触发
     * @param protocol
     */
    public void trigger(Protocol protocol) {
        Optional<DataSupplier> supplier = ProtocolSupplierManager.getDataSupplier(protocol);
        if(supplier.isPresent()) {
            DataSupplier dataSupplier = supplier.get();
            RealtimeStatus device = cacheManager.get(dataSupplier.getProtocolCode(), dataSupplier.getKey());

            dataSupplier.getValues().forEach(signalOrFieldValue -> {
                // 获取此点位的联动项
                Set<LinkageItem> linkageItems = linkageCacheService.listLinkageItem(device.getProductCode(), signalOrFieldValue.getSignalOrField());
                if(CollectionUtil.isNotEmpty(linkageItems)) {
                    // 只触发包含触发设备同一组的联动项
                    Set<LinkageItem> linkageItemSet = linkageItems.stream()
                            .filter(item -> item.getDeviceGroupIds().contains(device.getGroupId()))
                            .collect(Collectors.toSet());
                    invokeLinkage(linkageItemSet, dataSupplier, device);
                }
            });
        } else {
            if(logger.isDebugEnabled()) {
                Message message = protocol.requestMessage();
                if(message != null) {
                    DeviceProtocolSupplier protocolSupplier = ProtocolSupplierManager.get(message.getClass());
                    if(protocolSupplier != null) {
                        logger.debug("执行联动触发取消 没有提供数据 - 协议码: {} - 协议: {}", protocolSupplier.getProtocol().getCode(), protocol.getClass().getSimpleName());
                    } else {
                        logger.debug("执行联动触发取消 没有协议提供 - 协议: {}", protocolSupplier.getProtocol().getCode(), protocol.getClass().getSimpleName());
                    }

                    return;
                }

                logger.debug("执行联动触发取消 没有提供数据 - 协议: {}", protocol.getClass().getSimpleName());
            }
        }
    }

    protected void invokeLinkage(Set<LinkageItem> linkageItems, DataSupplier dataSupplier, Object deviceOrGroup) {
        Map<String, SuccessDeviceAndField> validateSuccessField = new HashMap<>();
        linkageItems.forEach(item -> {
            if(validateCondition(item, dataSupplier, deviceOrGroup, validateSuccessField)) {
                execLinkageAction(item, deviceOrGroup, validateSuccessField);
            }

            validateSuccessField.clear(); // 清除所有的参数
        });
    }

    protected void execLinkageAction(LinkageItem item, Object deviceOrGroup, Map<String, SuccessDeviceAndField> validateSuccessField) {
        if(CollectionUtil.isNotEmpty(item.getActions())) {
            Long groupId;
            RealtimeStatus triggerDevice = null;
            if(deviceOrGroup instanceof RealtimeStatus) { // 设备触发
                triggerDevice = (RealtimeStatus) deviceOrGroup;
                groupId = triggerDevice.getGroupId();
            } else if(deviceOrGroup instanceof DeviceGroup) { // 组触发
                groupId = ((DeviceGroup) deviceOrGroup).getId();
            } else {
                logger.error("动作项执行失败 未知的方式({})", deviceOrGroup); return;
            }

            for (int i = 0; i < item.getActions().size(); i++) {
                LinkageActionExecDto linkageAction = (LinkageActionExecDto) item.getActions().get(i);
                if(linkageAction.getType() == ActionType.model) {// 调用物模型接口
                    ModelApi api = linkageAction.getApi();
                    if(api != null) {
                        if("-1".equals(linkageAction.getUid())) { // 触发组全部设备
                            cacheManager.listGroupAndProduct(groupId, linkageAction.getProductCode()).forEach(device -> {
                                doInvokeAction(linkageAction, api, device);
                            });
                        } else if("-2".equals(linkageAction.getUid())){ // 当前触发设备
                            if(triggerDevice != null) { // 设备触发
                                doInvokeAction(linkageAction, api, triggerDevice);
                            } else {
                                // 组触发不做任何事情
                            }
                        } else { // 指定设备
                            cacheManager.listByProductCode(linkageAction.getProductCode()).forEach(device -> {
                                if(device.getUid().equals(linkageAction.getUid())) {
                                    doInvokeAction(linkageAction, api, device);
                                }
                            });
                        }
                    }
                } else if(linkageAction.getType() == ActionType.warn) { // 触发告警
                    WarnConfig warn = linkageAction.getWarn();
                    if(warn != null) {
                        // 成功触发此次联动条件的设备和字段
                        SuccessDeviceAndField deviceAndField = validateSuccessField.get(warn.getModelAttrField());
                        if(deviceAndField != null) {
                            Date date = new Date();
                            String content = resolverWarnTemplate(warn, deviceAndField);
                            WarnRecord warnRecord = new WarnRecord().setDeviceSn(deviceAndField.getDeviceSn()).
                                    setUid(deviceAndField.getUid()).setCreateTime(date).setWarnTime(date).setGrade(warn.getGrade())
                                    .setWarnConfigId(warn.getId()).setContent(content);
                            CollectListenerManager.getInstance().forEach(listener -> {
                                if(listener instanceof LinkageActionListener) {
                                    ((LinkageActionListener) listener).warnTrigger(warn, warnRecord, deviceAndField.device);
                                }
                            });
                        } else {
                            logger.error("动作项执行失败 未找到触发属性 - 属性: {} - 联动项: {} - 动作项: {}/{} - 动作id: {}", warn.getModelAttrField()
                                    , item.getName(), linkageAction.getDeviceName(), linkageAction.getLinkedName(), linkageAction.getId());
                        }
                    } else {
                        logger.error("动作项执行失败 未找到告警配置 - 联动项: {} - 动作项: {}/{} - 动作id: {}"
                                , item.getName(), linkageAction.getDeviceName(), linkageAction.getLinkedName(), linkageAction.getId());
                    }
                }
            }
        }
    }

    /**
     * 解析告警配置模板
     * @param warn
     * @param deviceAndField
     * @return
     */
    private String resolverWarnTemplate(WarnConfig warn, SuccessDeviceAndField deviceAndField) {
        Map<String, String> value = new HashMap<>();
        SignalOrFieldValue fieldValue = deviceAndField.value;
        value.put("deviceSn", deviceAndField.getDeviceSn());
        value.put("value", fieldValue.getValue().toString());

        return StrUtil.format(warn.getTemplate(), value, true);
    }

    private void doInvokeAction(LinkageActionExecDto linkageAction, ModelApi api, RealtimeStatus device) {
        if (device.getStatus() == DeviceStatus.online) {
            try {
                ProtocolModelApiInvokeParam param = createInvokeParam(linkageAction, device);
                ProtocolInvokeUtil.invoke(api, param, device, (result) -> {
                    if(result.getStatus() == ExecStatus.success) {

                    } else {
                        logger.warn("执行联动动作失败 {} - 接口: {}/{} - 设备: {}"
                                , result.getStatus(), api.getName(), api.getCode(), device.buildKey());
                    }
                    CollectListenerManager.getInstance().forEach(item -> {
                        if(item instanceof LinkageActionListener) {
                            ((LinkageActionListener) item).apiInvoke(api, device, result);
                        }
                    });
                });
            } catch (Exception e) {
                logger.error("执行联动动作失败 调用物模型接口异常 - 接口: {}/{} - 设备: {}", api.getName(), api.getCode(), device.buildKey(), e);
            }
        } else {
            logger.warn("执行联动动作失败 设备不在线 - 接口: {}/{} - 设备: {}", api.getName(), api.getCode(), device.buildKey());
        }
    }

    private ProtocolModelApiInvokeParam createInvokeParam(LinkageActionExecDto linkageAction, RealtimeStatus device) {
        if(CollectionUtil.isNotEmpty(linkageAction.getApi().getDownConfig())) {
            JSONObject execParam = linkageAction.getApi().resolveStrValue(linkageAction.getTypeValue());
            ProtocolModelApiInvokeParam invokeParam = new ProtocolModelApiInvokeParam(device.getUid(), execParam);
            invokeParam.setDeviceSn(device.getDeviceSn()).setParentDeviceSn(device.getParentDeviceSn());
            return invokeParam;
        } else {
            logger.error("执行联动动作异常 api没有下行配置 - api: {}/{} - 设备: {}"
                    , linkageAction.getApi().getName(), linkageAction.getApi().getCode()
                    , device != null ? device.buildKey() : null);
        }

        return null;
    }

    protected boolean validateCondition(LinkageItem item, DataSupplier dataSupplier, Object deviceOrGroup, Map<String, SuccessDeviceAndField> validateSuccessField) {
        Iterator<LinkageCondition> iterator = item.getConditions().iterator();
        boolean prev = false;

        while (iterator.hasNext()) {
            LinkageCondition condition = iterator.next();
            LinkageLogical logical = condition.getLogical();
            if(logical == null) { // 第一个条件
                // 当前条件校验状态
                prev = doValidateCondition(condition, dataSupplier, deviceOrGroup, validateSuccessField);
            } else { // 有两个以上条件
                if(logical == LinkageLogical.and) { // and关系
                    if(prev) { // 上一个为true则继续校验下一个
                        prev = doValidateCondition(condition, dataSupplier, deviceOrGroup, validateSuccessField);
                    } else { // 上一个为false直接返回false
                        return false;
                    }
                } else { // or关系
                    if(prev) {
                        return true;
                    } else {
                        prev = doValidateCondition(condition, dataSupplier, deviceOrGroup, validateSuccessField);
                    }
                }
            }
        }

        return prev;
    }

    private boolean doValidateCondition(LinkageCondition condition, DataSupplier dataSupplier, Object deviceOrGroup, Map<String, SuccessDeviceAndField> validateSuccessField) {
        List<LinkageConditionItem> items = condition.getItems();
        Iterator<LinkageConditionItem> iterator = items.iterator();
        boolean prev = false;
        while (iterator.hasNext()) {
            LinkageConditionItem conditionItem = iterator.next();
            LinkageLogical logical = conditionItem.getLogical();
            if(logical == null) { // 第一个条件
                // 当前条件校验状态
                prev = validateConditionItem(conditionItem, dataSupplier, deviceOrGroup, validateSuccessField);
            } else { // 有两个以上条件
                if(logical == LinkageLogical.and) { // and关系
                    if(prev) { // 上一个为true则继续校验下一个
                        prev = validateConditionItem(conditionItem, dataSupplier, deviceOrGroup, validateSuccessField);
                    } else { // 上一个为false直接返回false
                        return false;
                    }
                } else { // or关系
                    if(prev) {
                        return true;
                    } else {
                        prev = validateConditionItem(conditionItem, dataSupplier, deviceOrGroup, validateSuccessField);
                    }
                }
            }
        }

        return prev;
    }

    private boolean validateConditionItem(LinkageConditionItem conditionItem, DataSupplier dataSupplier
            , Object deviceOrGroup, Map<String, SuccessDeviceAndField> validateSuccessField) {
        ConditionCalc conditionCalc = ConditionalsManager.get(conditionItem.getOperator());
        if(conditionCalc != null) {
            Long groupId;
            RealtimeStatus triggerDevice = null;
            if(deviceOrGroup instanceof RealtimeStatus) { // 设备触发
                triggerDevice = (RealtimeStatus) deviceOrGroup;
                groupId = triggerDevice.getGroupId();
            } else if(deviceOrGroup instanceof DeviceGroup) { // 组触发
                groupId = ((DeviceGroup) deviceOrGroup).getId();
            } else {
                logger.error("条件项校验失败 未知的校验方式({})", deviceOrGroup); return false;
            }

            if("-1".equals(conditionItem.getUid())){ // 组下面且属于此联动项产品的所有设备
                Collection<RealtimeStatus> statuses = cacheManager.listGroupAndProduct(groupId, conditionItem.getProductCode());
                Iterator<RealtimeStatus> iterator = statuses.iterator();
                // 同一组且同一产品下包含多台设备的按或运算, 只要有一台条件满足就行
                while (iterator.hasNext()) {
                    RealtimeStatus next = iterator.next();
                    RealtimeData data = realtimeDataService.getOfDeviceAndKey(next.getProtocolCode(), next.buildKey(), conditionItem.getTypeValue());
                    if (calcCondition(conditionItem, conditionCalc, data, next)) {
                        validateSuccessField.put(conditionItem.getTypeValue(), new SuccessDeviceAndField(next, data.getRealtime()));
                        return true;
                    }
                }
            } else {
                RealtimeStatus linkageDevice;
                if(triggerDevice != null && triggerDevice.getUid().equals(conditionItem.getUid())) {
                    linkageDevice = triggerDevice;
                } else {
                    linkageDevice = cacheManager.getByUid(groupId, conditionItem.getUid());
                }

                if(linkageDevice != null) {
                    RealtimeData data = realtimeDataService.getOfDeviceAndKey(linkageDevice.getProtocolCode(), linkageDevice.buildKey(), conditionItem.getTypeValue());
                    if(calcCondition(conditionItem, conditionCalc, data, linkageDevice)) {
                        validateSuccessField.put(conditionItem.getTypeValue(), new SuccessDeviceAndField(linkageDevice, data.getRealtime()));
                    }
                } else {
                    logger.error("条件项校验失败 校验设备不存在 - 触发组id: {} - 联动条件设备uid: {}", groupId, conditionItem.getUid());
                }
            }

        } else {
            logger.warn("条件项校验失败 无此条件执行器 - 运算符: {}", conditionItem.getOperator());
        }

        return false;
    }

    @Nullable
    private boolean calcCondition(LinkageConditionItem conditionItem, ConditionCalc conditionCalc, RealtimeData data, RealtimeStatus device) {
        if (data != null) {
            if(conditionItem.getType() == ConditionType.Model) { // 模型属性计算
                SignalOrFieldValue realtime = data.getRealtime();
                if (realtime.getStatus() == CollectStatus.Success) {
                    return conditionCalc.calc(realtime.getValue(), conditionItem.getValue());
                } else {
                    if(logger.isDebugEnabled()) {
                        logger.debug("条件项计算失败 最新采集值状态失败 - 设备: {} - 实时状态: {} - 表达式: {} {} {}"
                                , device.buildDeviceKey(), realtime.getStatus(), data.getSignalOrField(), conditionCalc.getOperational(), conditionItem.getValue());
                    }
                }
            } else if(conditionItem.getType() == ConditionType.DeviceStatus) { // 设备状态校验
                DeviceStatus status = DeviceStatus.valueOf(conditionItem.getTypeValue());
                if(status == device.getStatus()) {
                    return true;
                }
            }
        }

        return false;
    }

    public void trigger(CollectInfo info) {
        if(CollectionUtil.isNotEmpty(info.getInfos())) {
            Set<LinkageItem> linkageItems = linkageCacheService.listLinkageItem(info.getSource().getId(), info.getGroup().getId());
            if(CollectionUtil.isNotEmpty(linkageItems) && CollectionUtil.isNotEmpty(info.getInfos())) {
                this.invokeLinkage(linkageItems, null, info.getGroup());
            }
        }
    }

    @Getter
    public class SuccessDeviceAndField {
        private RealtimeStatus device;
        private SignalOrFieldValue value;

        public SuccessDeviceAndField(RealtimeStatus device, SignalOrFieldValue value) {
            this.device = device;
            this.value = value;
        }

        public String getDeviceSn() {
            return device.getDeviceSn();
        }

        public String getUid() {
            return device.getUid();
        }
    }

    @Override
    public void listener(CollectInfo info) {
        this.trigger(info);
    }

    @Override
    public void supplier(EventSource source, DeviceGroup group, CollectInfo.DeviceInfo info) {

    }
}
