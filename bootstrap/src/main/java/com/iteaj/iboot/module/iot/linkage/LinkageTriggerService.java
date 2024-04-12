package com.iteaj.iboot.module.iot.linkage;

import cn.hutool.core.collection.CollectionUtil;
import com.iteaj.framework.spi.iot.*;
import com.iteaj.framework.spi.iot.consts.CollectStatus;
import com.iteaj.iboot.module.iot.cache.IotCacheManager;
import com.iteaj.iboot.module.iot.cache.data.RealtimeData;
import com.iteaj.iboot.module.iot.cache.data.RealtimeDataService;
import com.iteaj.iboot.module.iot.cache.entity.RealtimeStatus;
import com.iteaj.iboot.module.iot.collect.model.CollectInfo;
import com.iteaj.iboot.module.iot.collect.model.EventGroupCollectListener;
import com.iteaj.iboot.module.iot.consts.LinkageLogical;
import com.iteaj.iboot.module.iot.entity.*;
import com.iteaj.iot.Message;
import com.iteaj.iot.Protocol;
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

    public LinkageTriggerService(IotCacheManager cacheManager, RealtimeDataService realtimeDataService, LinkageCacheService linkageCacheService) {
        this.cacheManager = cacheManager;
        this.realtimeDataService = realtimeDataService;
        this.linkageCacheService = linkageCacheService;
    }

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
        linkageItems.forEach(item -> {
            if(validateCondition(item, dataSupplier, deviceOrGroup)) {
                execLinkageAction(item, deviceOrGroup);
            }
        });
    }

    protected void execLinkageAction(LinkageItem item, Object deviceOrGroup) {

    }

    protected boolean validateCondition(LinkageItem item, DataSupplier dataSupplier, Object deviceOrGroup) {
        Iterator<LinkageCondition> iterator = item.getConditions().iterator();
        boolean prev = false;

        while (iterator.hasNext()) {
            LinkageCondition condition = iterator.next();
            LinkageLogical logical = condition.getLogical();
            if(logical == null) { // 第一个条件
                // 当前条件校验状态
                prev = doValidateCondition(condition, dataSupplier, deviceOrGroup);
            } else { // 有两个以上条件
                if(logical == LinkageLogical.and) { // and关系
                    if(prev) { // 上一个为true则继续校验下一个
                        prev = doValidateCondition(condition, dataSupplier, deviceOrGroup);
                    } else { // 上一个为false直接返回false
                        return false;
                    }
                } else { // or关系
                    if(prev) {
                        return true;
                    } else {
                        prev = doValidateCondition(condition, dataSupplier, deviceOrGroup);
                    }
                }
            }
        }

        return prev;
    }

    private boolean doValidateCondition(LinkageCondition condition, DataSupplier dataSupplier, Object deviceOrGroup) {
        List<LinkageConditionItem> items = condition.getItems();
        Iterator<LinkageConditionItem> iterator = items.iterator();
        boolean prev = false;
        while (iterator.hasNext()) {
            LinkageConditionItem conditionItem = iterator.next();
            LinkageLogical logical = conditionItem.getLogical();
            if(logical == null) { // 第一个条件
                // 当前条件校验状态
                prev = validateConditionItem(conditionItem, dataSupplier, deviceOrGroup);
            } else { // 有两个以上条件
                if(logical == LinkageLogical.and) { // and关系
                    if(prev) { // 上一个为true则继续校验下一个
                        prev = validateConditionItem(conditionItem, dataSupplier, deviceOrGroup);
                    } else { // 上一个为false直接返回false
                        return false;
                    }
                } else { // or关系
                    if(prev) {
                        return true;
                    } else {
                        prev = validateConditionItem(conditionItem, dataSupplier, deviceOrGroup);
                    }
                }
            }
        }

        return prev;
    }

    private boolean validateConditionItem(LinkageConditionItem conditionItem, DataSupplier dataSupplier, Object deviceOrGroup) {
        ConditionCalc conditionCalc = ConditionalsManager.get(conditionItem.getOperator());
        if(conditionCalc != null) {
            Long groupId = null;
            RealtimeStatus device;
            if(deviceOrGroup instanceof RealtimeStatus) { // 设备触发
                device = (RealtimeStatus) deviceOrGroup;

            } else if(deviceOrGroup instanceof DeviceGroup) { // 组触发

            } else {
                logger.error("条件项校验失败 未知的校验方式");
            }

            if(conditionItem.getUid().equals(device.getUid())) { // 条件项设备刚好和触发设备属于同一台

            } else if("-1".equals(conditionItem.getUid())){ // 组下面且属于此联动项产品的所有设备
                Collection<RealtimeStatus> statuses = cacheManager.listGroupAndProduct(groupId, conditionItem.getProductCode());
                Iterator<RealtimeStatus> iterator = statuses.iterator();
                // 同一产品下面包含有台设备的按或运算, 只要有一台条件满足就行
                while (iterator.hasNext()) {
                    RealtimeStatus next = iterator.next();
                    RealtimeData data = realtimeDataService.getOfDeviceAndKey(next.getProtocolCode(), next.buildKey(), conditionItem.getTypeValue());
                    if (calcCondition(conditionItem, conditionCalc, data)) {
                        return true;
                    }
                }
            } else {
//                cacheManager.getByUid(conditionItem.getUid());
            }
            RealtimeData data = realtimeDataService.getOfDeviceAndKey(dataSupplier.getProtocolCode(), device.buildKey(), conditionItem.getTypeValue());
            Boolean realtime = calcCondition(conditionItem, conditionCalc, data);
            if (realtime != null) return realtime;
        } else {
            logger.warn("条件项校验失败 无此条件运行 - 运算符: {}", conditionItem.getOperator());
        }
        return false;
    }

    @Nullable
    private boolean calcCondition(LinkageConditionItem conditionItem, ConditionCalc conditionCalc, RealtimeData data) {
        if (data != null) {
            SignalOrFieldValue realtime = data.getRealtime();
            if (realtime.getStatus() == CollectStatus.Success) {
                return conditionCalc.calc(realtime.getValue(), conditionItem.getValue());
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

    @Override
    public void listener(CollectInfo info) {
        this.trigger(info);
    }

    @Override
    public void supplier(EventSource source, DeviceGroup group, CollectInfo.DeviceInfo info) {

    }
}
