package com.iteaj.iboot.module.iot.listener;

import cn.hutool.core.collection.CollectionUtil;
import com.iteaj.framework.spi.iot.DataSupplier;
import com.iteaj.framework.spi.iot.data.DefaultRealtimeData;
import com.iteaj.framework.spi.iot.data.ModelAttrMeta;
import com.iteaj.framework.spi.iot.data.ModelAttrMetaService;
import com.iteaj.framework.spi.iot.listener.IotEvenPublisher;
import com.iteaj.framework.spi.iot.listener.IotEventType;
import com.iteaj.framework.spi.iot.listener.RealtimePayload;
import com.iteaj.iboot.module.iot.cache.entity.RealtimeStatus;
import com.iteaj.iboot.module.iot.collect.DeviceStatusListener;
import com.iteaj.iboot.module.iot.collect.model.CollectInfo;
import com.iteaj.iboot.module.iot.collect.model.EventGroupCollectListener;
import com.iteaj.iboot.module.iot.collect.model.ModelAttrListener;
import com.iteaj.iboot.module.iot.entity.*;
import com.iteaj.iboot.module.iot.linkage.LinkageActionListener;

import java.util.*;
import java.util.stream.Collectors;

import static com.iteaj.framework.spi.iot.listener.RealtimePayload.TYPE_DATA_ACTIVE;

/**
 * 将采集的数据、设备的实时状态、上报的模型属性数据发布出去, 用于给其他插件模块使用
 */
public class PluginRealtimeInfoListenerPublisher implements EventGroupCollectListener
        , DeviceStatusListener, ModelAttrListener, LinkageActionListener {

    private final static String BATCH = "batch";
    private final ModelAttrMetaService modelAttrMetaService;

    public PluginRealtimeInfoListenerPublisher(ModelAttrMetaService modelAttrMetaService) {
        this.modelAttrMetaService = modelAttrMetaService;
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
        doPublisher(source.getId(), info.getSupplier(), info.getDevice(), info.getCid(), "data");
    }

    @Override
    public void finished(CollectInfo info) {
        List<DefaultRealtimeData> collectData = new ArrayList<>();
        if(CollectionUtil.isNotEmpty(info.getInfos())) {
            info.getInfos().forEach(item -> {
                RealtimeStatus device = item.getDevice();
                DataSupplier supplier = item.getSupplier();
                if(CollectionUtil.isNotEmpty(supplier.getValues())) {
                    Date createTime = new Date();
                    supplier.getValues().forEach(value -> {
                        DefaultRealtimeData data = DefaultRealtimeData.of(value, device
                                , info.getSource().getId(), info.getCid(), createTime, null);

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

    private void doPublisher(Long taskId, DataSupplier supplier, RealtimeStatus device, String cid, String type) {
        Date createTime = new Date();
        List<DefaultRealtimeData> collect = supplier.getValues().stream()
                .map(item -> {
                    ModelAttrMeta attrMeta = modelAttrMetaService.getAttrMeta(device.getProductCode(), item.getId());
                    return DefaultRealtimeData.of(item, device, taskId, cid, createTime, attrMeta != null ? attrMeta.getName() : null);
                })
                .collect(Collectors.toList());

        IotEvenPublisher.publish(IotEventType.Realtime, new RealtimePayload(type, device.getUid(), collect));
    }

    @Override
    public void supplier(DataSupplier supplier, RealtimeStatus device) {
        doPublisher(null, supplier, device, supplier.getCid(), TYPE_DATA_ACTIVE);
    }

    @Override
    public void supplier(DataSupplier supplier, RealtimeStatus device, String type) {
        doPublisher(null, supplier, device, supplier.getCid(), type);
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
        return EventGroupCollectListener.super.getOrder() - 1000;
    }
}
