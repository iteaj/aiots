package com.iteaj.iboot.module.iot.linkage;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.iteaj.framework.spi.iot.*;
import com.iteaj.framework.spi.iot.consts.CollectStatus;
import com.iteaj.iboot.module.iot.cache.IotCacheManager;
import com.iteaj.iboot.module.iot.cache.data.RealtimeData;
import com.iteaj.iboot.module.iot.cache.data.RealtimeDataService;
import com.iteaj.iboot.module.iot.cache.entity.RealtimeStatus;
import com.iteaj.iboot.module.iot.collect.CollectListenerManager;
import com.iteaj.iboot.module.iot.collect.DeviceStatusListener;
import com.iteaj.iboot.module.iot.collect.model.CollectInfo;
import com.iteaj.iboot.module.iot.collect.model.EventGroupCollectListener;
import com.iteaj.iboot.module.iot.collect.model.ModelAttrListener;
import com.iteaj.iboot.module.iot.consts.*;
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
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.iteaj.iboot.module.iot.entity.LinkageConditionItem.DEVICE_OFFLINE;
import static com.iteaj.iboot.module.iot.entity.LinkageConditionItem.DEVICE_ONLINE;

public class LinkageTriggerService implements EventGroupCollectListener, ModelAttrListener, DeviceStatusListener {

    private final IotCacheManager cacheManager;
    private final RealtimeDataService realtimeDataService;
    private final LinkageCacheService linkageCacheService;
    private final Logger logger = LoggerFactory.getLogger("log:linkage:trigger");
    private final Map<String, WarnInfo> warnStatus = new ConcurrentHashMap<>(512);

    public LinkageTriggerService(IotCacheManager cacheManager, RealtimeDataService realtimeDataService
            , LinkageCacheService linkageCacheService) {
        this.cacheManager = cacheManager;
        this.realtimeDataService = realtimeDataService;
        this.linkageCacheService = linkageCacheService;
    }

    @Override
    public void supplier(Collection<RealtimeStatus> devices) {
        this.trigger(devices);
    }

    /**
     * 设备状态事件触发
     * @param device
     */
    public void trigger(RealtimeStatus device) {
        try {
            String status = device.getStatus() == DeviceStatus.online ? DEVICE_ONLINE[2] : DEVICE_OFFLINE[2];
            Set<LinkageItem> linkageItems = linkageCacheService.listLinkageItem(device.getProductCode(), status);
            this.invokeLinkage(linkageItems, null, device);
        } catch (Exception e) {
            logger.error("联动触发失败 设备状态触发 - 设备: {} - 状态: {}", device.buildDeviceKey(), device.getStatus(), e);
        }
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
        try {
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
                                .filter(item -> item.getDeviceGroupIds().contains(device.getGroupId().intValue()))
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
        } catch (Exception e) {
            logger.error("联动触发失败 设备属性主动触发 - 协议: {} - 设备编号: {}", protocol.getClass().getSimpleName(), protocol.getEquipCode());
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
                try {
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
                                } else { //组触发
                                    // 从条件校验成功的记录中查找相同产品的设备
                                    validateSuccessField.values().forEach(trigger -> {
                                        // 对成功触发此次动作的设备进行api调用
                                        if(trigger.getDevice().getProductCode().equals(linkageAction.getProductCode())) {
                                            doInvokeAction(linkageAction, api, trigger.getDevice());
                                        }
                                    });
                                }
                            } else { // 指定设备
                                RealtimeStatus device = cacheManager.getByUid(linkageAction.getUid());
                                if(device != null) {
                                    doInvokeAction(linkageAction, api, device);
                                } else {
                                    logger.error("动作项执行失败 未找到设备 - uid: {} - 联动项: {} - 动作项: {}/{} - 动作id: {}", linkageAction.getUid()
                                            , item.getName(), linkageAction.getDeviceName(), linkageAction.getLinkedName(), linkageAction.getId());
                                }
                            }
                        } else {
                            logger.error("动作项执行失败 未找到模型接口 - modelApiId: {} - 联动项: {} - 动作项: {}/{} - 动作id: {}", linkageAction.getLinkedId()
                                    , item.getName(), linkageAction.getDeviceName(), linkageAction.getLinkedName(), linkageAction.getId());
                        }
                    } else if(linkageAction.getType() == ActionType.warn) { // 触发告警
                        WarnConfig warn = linkageAction.getWarn();
                        if(warn != null) {
                            // 成功触发此次联动条件的设备和字段
                            SuccessDeviceAndField deviceAndField = validateSuccessField.get(warn.getModelAttrField());
                            if(deviceAndField != null) {
                                doHandleWarn(warn, deviceAndField.getDevice(), deviceAndField.getValue().getValue());
                            } else if(triggerDevice != null) {
                                doHandleWarn(warn, triggerDevice, triggerDevice.getStatus().getDesc());
                            } else {
                                logger.error("动作项执行失败 未找到触发属性 - 属性: {} - 联动项: {} - 动作项: {}/{} - 动作id: {}", warn.getModelAttrField()
                                        , item.getName(), linkageAction.getDeviceName(), linkageAction.getLinkedName(), linkageAction.getId());
                            }
                        } else {
                            logger.error("动作项执行失败 未找到告警配置 - 联动项: {} - 动作项: {}/{} - 动作id: {}"
                                    , item.getName(), linkageAction.getDeviceName(), linkageAction.getLinkedName(), linkageAction.getId());
                        }
                    }
                } catch (Exception e) {
                    logger.error("动作项执行失败 未知异常 - 联动项: {} - 动作项: {}/{} - 动作id: {}"
                            , item.getName(), linkageAction.getDeviceName(), linkageAction.getLinkedName(), linkageAction.getId(), e);
                }
            }
        }
    }

    private void doHandleWarn(WarnConfig warn, RealtimeStatus device, Object value) {
        WarnInfo warnInfo = null;
        if(warn.getModelAttrId() != null) {
            warnInfo = warnStatus.get(device.getUid() + ":" + warn.getModelAttrId());
        }

        WarnGrade grade = warn.getGrade();
        // 属于解除告警并且已经执行一次
        if(grade == WarnGrade.normal) {
            // 等于null说明是第一次触发解除
            if(warnInfo == null) {
                return;
            }

            warnInfo.setStatus(true); // 已经解除告警
            warnStatus.remove(device.getUid() + ":" + warn.getModelAttrId()); // 移除告警信息
        } else if(grade != WarnGrade.status){ // 状态告警不涉及模型属性
            if(warnInfo == null) {
                warnStatus.put(device.getUid() + ":" + warn.getModelAttrId(), warnInfo = new WarnInfo()); // 持续告警中
            }
        }

        // 如果是第一次告警或者超出了频率范围就发送告警
        if(warnInfo == null || warnInfo.isFirst() || warnInfo.isExpire(warn.getFrequency())) {
            if(warnInfo != null) {
                warnInfo.trigger();
            }

            Date date = new Date();
            String warnValue = value == null ? StrUtil.EMPTY : value.toString();
            String content = resolverWarnTemplate(warn, device.getDeviceSn(), warnValue, date, device.getDeviceName());
            WarnRecord warnRecord = new WarnRecord().setDeviceSn(device.getDeviceSn()).
                    setUid(device.getUid()).setCreateTime(date).setWarnTime(date).setGrade(grade)
                    .setWarnConfigId(warn.getId()).setContent(content).setField(warn.getModelAttrField())
                    .setProductCode(device.getProductCode()).setValue(warnValue);

            CollectListenerManager.getInstance().publish(listener -> {
                if(listener instanceof LinkageActionListener) {
                    ((LinkageActionListener) listener).warnTrigger(warn, warnRecord, device);
                }
            });
        }
    }

    /**
     * 解析告警配置模板
     * @param warn
     * @param warnTime
     * @param deviceName
     * @return
     */
    private String resolverWarnTemplate(WarnConfig warn, String deviceSn, String value, Date warnTime, String deviceName) {
        Map<String, String> param = new HashMap<>();
        param.put("value", value);
        param.put("deviceSn", deviceSn);
        param.put("deviceName", deviceName);
        param.put("grade", warn.getGrade().toString());
        param.put("warnTime", DateUtil.format(warnTime, "yy-MM-dd HH:mm:ss"));

        return StrUtil.format(warn.getTemplate(), param, true);
    }

    private void doInvokeAction(LinkageActionExecDto linkageAction, ModelApi api, RealtimeStatus device) {
        if(device.getType() == DeviceType.Child) { // 获取网关设备并且校验网关设备是否在线
            RealtimeStatus parent = cacheManager.get(device.getProtocolCode(), device.getParentDeviceSn());
            if(parent == null || parent.getStatus() == DeviceStatus.offline) {
                logger.error("执行联动动作失败 父网关设备不存在或不在线({}) - 接口: {}/{} - 设备: {}"
                        , device.getParentDeviceSn(), api.getName(), api.getCode(), device.buildKey());
                return;
            }
        } else if(device.getType() == DeviceType.Direct) {
            if(device.getStatus() == DeviceStatus.offline) {
                logger.error("执行联动动作失败 设备不在线 - 接口: {}/{} - 设备: {}", api.getName(), api.getCode(), device.buildKey());
                return;
            }
        }

        try {
            ProtocolModelApiInvokeParam param = createInvokeParam(linkageAction, device);
            ProtocolInvokeUtil.invoke(api, param, device, (result) -> {
                if(result.getStatus() != ExecStatus.success) {
                    logger.warn("执行联动动作失败 {} - 接口: {}/{} - 设备: {}"
                            , result.getStatus(), api.getName(), api.getCode(), device.buildKey());
                }

                CollectListenerManager.getInstance().publish(item -> {
                    if(item instanceof LinkageActionListener) {
                        ((LinkageActionListener) item).apiInvoke(api, device, result);
                    }
                });
            });
        } catch (Exception e) {
            logger.error("执行联动动作失败 调用物模型接口异常 - 接口: {}/{} - 设备: {}", api.getName(), api.getCode(), device.buildKey(), e);
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

            if(LinkageConditionItem.DEVICE_ANY[0].equals(conditionItem.getUid())){ // 组下面且属于此联动项产品的所有设备
                if(triggerDevice == null) { // 组级别的触发
                    Collection<RealtimeStatus> statuses = cacheManager.listGroupAndProduct(groupId, conditionItem.getProductCode());
                    // 同一组且同一产品下包含多台设备的按或运算, 只要有一台条件满足就行
                    for (RealtimeStatus next : statuses) {
                        RealtimeData data = realtimeDataService.getOfDeviceAndKey(next.getProtocolCode(), next.buildKey(), conditionItem.getTypeValue());
                        if (calcCondition(conditionItem, conditionCalc, data, next)) {
                            validateSuccessField.put(conditionItem.getTypeValue(), new SuccessDeviceAndField(next, data.getRealtime()));
                            return true;
                        }
                    }
                } else if(triggerDevice.getProductCode().equals(conditionItem.getProductCode())){ // fix: 2025-12-15 修复触发设备校验
                    RealtimeData data = realtimeDataService.getOfDeviceAndKey(triggerDevice.getProtocolCode(), triggerDevice.buildKey(), conditionItem.getTypeValue());
                    if (calcCondition(conditionItem, conditionCalc, data, triggerDevice)) {
                        validateSuccessField.put(conditionItem.getTypeValue(), new SuccessDeviceAndField(triggerDevice, data.getRealtime()));
                        return true;
                    }
                }
            } else if(LinkageConditionItem.DEVICE_TRIGGER[0].equals(conditionItem.getUid())) { // 当前触发设备(只支持设备状态校验)
                if(conditionItem.getType() == ConditionType.Status) { // 设备状态校验
                    if(triggerDevice != null) {
                        DeviceStatus status = DeviceStatus.getInstance(conditionItem.getTypeValue());
                        if(status == triggerDevice.getStatus()) {
                            return true;
                        }
                    } else if(logger.isDebugEnabled()){
                        logger.debug("条件项校验 状态校验不支持组触发 - 触发组id: {} - 联动条件设备uid: {}", groupId, conditionItem.getUid());
                    }
                } else {
                    logger.error("条件项校验失败 只支持状态校验 - 触发组id: {} - 联动条件设备uid: {}", groupId, conditionItem.getUid());
                }
            } else { // 指定设备
                RealtimeStatus linkageDevice;
                if(triggerDevice != null && triggerDevice.getUid().equals(conditionItem.getUid())) {
                    linkageDevice = triggerDevice;
                } else {
                    linkageDevice = cacheManager.getByUid(conditionItem.getUid());
                }

                if(linkageDevice != null) {
                    RealtimeData data = realtimeDataService.getOfDeviceAndKey(linkageDevice.getProtocolCode(), linkageDevice.buildKey(), conditionItem.getTypeValue());
                    if(calcCondition(conditionItem, conditionCalc, data, linkageDevice)) {
                        validateSuccessField.put(conditionItem.getTypeValue(), new SuccessDeviceAndField(linkageDevice, data.getRealtime()));
                        return true;
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
            }
        }

        return false;
    }

    public void trigger(CollectInfo info) {
        try {
            if(CollectionUtil.isNotEmpty(info.getInfos())) {
                Set<LinkageItem> linkageItems = linkageCacheService.listLinkageItem(info.getSource().getId(), info.getGroup().getId());
                if(CollectionUtil.isNotEmpty(linkageItems) && CollectionUtil.isNotEmpty(info.getInfos())) {
                    this.invokeLinkage(linkageItems, null, info.getInfos().get(0));
                }
            }
        } catch (Exception e) {
            logger.error("联动触发失败 事件源触发 - 事件源: {} - 触发组: {}", info.getSource().getName(), info.getGroup().getName());
        }
    }

    @Override
    public void supplier(DataSupplier supplier, RealtimeStatus device) {
        List<SignalOrFieldValue> values = supplier.getValues();
        if(CollectionUtil.isNotEmpty(values)) {
            values.forEach(item -> {
                Set<LinkageItem> linkageItems = linkageCacheService.listLinkageItem(device.getProductCode(), item.getSignalOrField());
                if(CollectionUtil.isNotEmpty(linkageItems)) {
                    this.invokeLinkage(linkageItems, supplier, device);
                }
            });
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
    public void finished(CollectInfo info) {
        this.trigger(info);
    }

    @Override
    public void supplier(EventSource source, DeviceGroup group, CollectInfo.DeviceInfo info) {
        try {
            if(info != null) {
                Set<LinkageItem> linkageItems = linkageCacheService.listLinkageItem(source.getId(), group.getId());
                if(CollectionUtil.isNotEmpty(linkageItems)) {
                    this.invokeLinkage(linkageItems, null, group);
                }
            }
        } catch (Exception e) {
            logger.error("联动触发失败 事件源触发 - 事件源: {} - 触发组: {}", source.getName(), group.getName());
        }
    }

    public class WarnInfo {

        /**
         * 第一次告警的毫秒数
         */
        private long firstOfMs;

        /**
         * 解除状态(true 解除, false 未解除)
         */
        private boolean status;

        /**
         * 发送次数
         */
        private int times;

        public WarnInfo() {
            this.times = 0;
            this.status = false;
            this.firstOfMs = System.currentTimeMillis();
        }

        public boolean isStatus() {
            return status;
        }

        public void setStatus(boolean status) {
            this.times = 0;
            this.status = status;
        }

        public boolean isFirst() {
            return this.times == 0;
        }

        /**
         * 是否超出频率范围
         * @param frequency
         * @return
         */
        public boolean isExpire(Integer frequency) {
            if(frequency == null || frequency <= 0) {
                return true; // 小于等于0的值代表实时告警
            }

            return TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - firstOfMs) > frequency;
        }

        /**
         * 触发告警信息
         * @return
         */
        public void trigger() {
            this.firstOfMs = System.currentTimeMillis();
            this.times ++;
        }
    }
}
