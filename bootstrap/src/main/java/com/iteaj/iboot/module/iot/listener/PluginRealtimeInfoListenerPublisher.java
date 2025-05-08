package com.iteaj.iboot.module.iot.listener;

import cn.hutool.core.collection.CollectionUtil;
import com.iteaj.framework.spi.iot.DataSupplier;
import com.iteaj.framework.spi.iot.SignalOrFieldValue;
import com.iteaj.framework.spi.iot.data.DefaultRealtimeData;
import com.iteaj.framework.spi.iot.listener.IotEvenPublisher;
import com.iteaj.framework.spi.iot.listener.IotEventType;
import com.iteaj.framework.spi.iot.listener.RealtimePayload;
import com.iteaj.iboot.module.iot.cache.entity.RealtimeStatus;
import com.iteaj.iboot.module.iot.collect.CollectDevice;
import com.iteaj.iboot.module.iot.collect.DeviceStatusListener;
import com.iteaj.iboot.module.iot.collect.SignalCollectListener;
import com.iteaj.iboot.module.iot.collect.model.CollectInfo;
import com.iteaj.iboot.module.iot.collect.model.EventGroupCollectListener;
import com.iteaj.iboot.module.iot.collect.model.ModelAttrListener;
import com.iteaj.iboot.module.iot.consts.CollectMode;
import com.iteaj.iboot.module.iot.entity.*;
import com.iteaj.iboot.module.iot.linkage.LinkageActionListener;
import com.iteaj.iot.utils.UniqueIdGen;
import org.springframework.beans.BeanUtils;
import org.springframework.core.Ordered;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 将采集的数据、设备的实时状态、上报的模型属性数据发布出去, 用于给其他插件模块使用
 */
public class PluginRealtimeInfoListenerPublisher implements SignalCollectListener, EventGroupCollectListener
        , DeviceStatusListener, ModelAttrListener, LinkageActionListener {

    private final static String BATCH = "batch";

    @Override
    public void supplier(CollectDevice device, CollectData data) {
        DefaultRealtimeData realtimeData = new DefaultRealtimeData();
        realtimeData.setCid(data.getCid()).setUid(data.getUid())
                .setCollectTime(data.getCollectTime()).setCollectMode(data.getCollectMode())
                .setField(data.getField()).setFieldId(data.getFieldId()).setAddress(data.getAddress())
                .setReason(data.getReason()).setStatus(data.getStatus()).setDeviceSn(data.getDeviceSn())
                .setDeviceName(data.getDeviceName()).setTaskId(data.getTaskId())
                .setValue(data.getValue()).setCreateTime(data.getCreateTime()).setDeviceGroupId(data.getDeviceGroupId())
                .setProductCode(data.getProductCode()).setProtocolCode(data.getProtocolCode()).setFieldName(data.getFieldName());

        RealtimePayload payload = new RealtimePayload("data", data.getUid(), Collections.singletonList(realtimeData));
        IotEvenPublisher.publish(IotEventType.Realtime, payload);
    }

    @Override
    public void supplier(Collection<RealtimeStatus> devices) {
        String uid = BATCH;
        if(devices.size() == 1 && devices instanceof List) {
            uid = ((List<RealtimeStatus>) devices).get(0).getUid();
        }

        IotEvenPublisher.publish(IotEventType.Realtime, new RealtimePayload("status", uid, devices));
    }

    @Override
    public void supplier(EventSource source, DeviceGroup group, CollectInfo.DeviceInfo info) {
        doPublisher(source.getId(), info.getSupplier(), info.getDevice(), "data");
    }

    @Override
    public void finished(CollectInfo info) {
        List<DefaultRealtimeData> collectData = new ArrayList<>();
        if(CollectionUtil.isNotEmpty(info.getInfos())) {
            Long cid = UniqueIdGen.nextLong(); // 声明同一组
            info.getInfos().forEach(item -> {
                RealtimeStatus device = item.getDevice();
                DataSupplier supplier = item.getSupplier();
                if(CollectionUtil.isNotEmpty(supplier.getValues())) {
                    supplier.getValues().forEach(value -> {
                        DefaultRealtimeData data = new DefaultRealtimeData()
                                .setCid(cid.toString()).setUid(device.getUid())
                                .setCreateTime(new Date()).setValue(value.getValue())
                                .setAddress(value.getAddress())
                                .setFieldId(value.getId())
                                .setCollectMode(CollectMode.model.name())
                                .setDeviceSn(device.getDeviceSn())
                                .setDeviceName(device.getDeviceName())
                                .setField(value.getSignalOrField())
                                .setProductCode(device.getProductCode())
                                .setProtocolCode(device.getProtocolCode())
                                .setDeviceGroupId(info.getGroup().getId())
                                .setFieldName(value.getSignalOrField())
                                .setCollectTime(supplier.getCollectTime())
                                .setTaskId(info.getSource().getId())
                                .buildFromStatus(value.getStatus());

                        collectData.add(data);
                    });
                }
            });

            if(!collectData.isEmpty()) {
                String uid = BATCH;
                if(collectData.size() == 1) {
                    uid = collectData.get(0).getUid();
                }

                IotEvenPublisher.publish(IotEventType.Realtime, new RealtimePayload("data_full", uid, collectData));
            }
        }
    }

    private void doPublisher(Long taskId, DataSupplier supplier, RealtimeStatus device, String type) {
        List<DefaultRealtimeData> collect = supplier.getValues().stream()
                .map(item -> new DefaultRealtimeData()
                        .setFieldId(item.getId())
                        .setUid(device.getUid())
                        .setField(item.getSignalOrField())
                        .setCollectTime(item.getCollectTime())
                        .setAddress(item.getAddress())
                        .setCollectMode("model").setTaskId(taskId)
                        .setDeviceGroupId(device.getGroupId())
                        .setProductCode(device.getProductCode())
                        .setProtocolCode(device.getProtocolCode())
                        .setDeviceSn(device.getDeviceSn())
                        .setDeviceName(device.getDeviceName())
                        .setCreateTime(new Date())
                        .setValue(item.getValue())
                        .buildFromStatus(item.getStatus())
                ).collect(Collectors.toList());

        IotEvenPublisher.publish(IotEventType.Realtime, new RealtimePayload(type, device.getUid(), collect));
    }

    @Override
    public void supplier(DataSupplier supplier, RealtimeStatus device) {
        doPublisher(null, supplier, device, "data_active");
    }

    /**
     * @param config
     * @param record
     * @param status
     */
    @Override
    public void warnTrigger(WarnConfig config, WarnRecord record, RealtimeStatus status) {
        IotEvenPublisher.publish(IotEventType.Realtime, new RealtimePayload("warn", status.getUid(), Arrays.asList(record)));
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE - 15000;
    }
}
