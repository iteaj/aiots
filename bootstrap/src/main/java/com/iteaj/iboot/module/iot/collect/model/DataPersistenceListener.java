package com.iteaj.iboot.module.iot.collect.model;

import cn.hutool.core.collection.CollectionUtil;
import com.iteaj.framework.spi.iot.DataSupplier;
import com.iteaj.framework.spi.iot.consts.CollectStatus;
import com.iteaj.framework.spi.iot.data.DefaultRealtimeData;
import com.iteaj.iboot.module.iot.cache.entity.RealtimeStatus;
import com.iteaj.iboot.module.iot.consts.CollectMode;
import com.iteaj.iboot.module.iot.entity.CollectData;
import com.iteaj.iboot.module.iot.entity.DeviceGroup;
import com.iteaj.iboot.module.iot.entity.EventSource;
import com.iteaj.iot.utils.UniqueIdGen;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 数据持久化监听
 */
public interface DataPersistenceListener extends EventGroupCollectListener, ModelAttrListener {

    @Override
    default void supplier(DataSupplier supplier, RealtimeStatus device) {
        if(supplier != null && CollectionUtil.isNotEmpty(supplier.getValues())) {
            long cid = UniqueIdGen.nextLong(); // 声明同一组
            List<CollectData> data = new ArrayList<>();
            supplier.getValues().forEach(value -> {
                String strValue = value.getValue() == null ? null : value.getValue().toString();
                data.add(new CollectData(CollectMode.model).setCid(cid + "")
                        .setReason("").setCreateTime(value.getCollectTime())
                        .setFieldId(value.getId()).setValue(strValue)
                        .setCollectTime(value.getCollectTime())
                        .setCollectStatus(value.getStatus())
                        .setUid(device.getUid()).setDeviceName(device.getDeviceName())
                        .setCollectMode(CollectMode.model.name())
                        .setProductCode(device.getProductCode())
                        .setProtocolCode(device.getProtocolCode())
                        .setDeviceGroupId(device.getDeviceGroupId())
                        .setField(value.getSignalOrField())
                        .setFieldName(value.getSignalOrField())
                );
            });

            this.persistence(data);
        }
    }

    @Override
    default void supplier(EventSource source, DeviceGroup group, CollectInfo.DeviceInfo info) {

    }

    @Override
    default void finished(CollectInfo info) {
        List<DefaultRealtimeData> collectData = new ArrayList<>();
        if(CollectionUtil.isNotEmpty(info.getInfos())) {
            String cid = info.getCid(); // 声明同一组
            info.getInfos().forEach(item -> {
                final RealtimeStatus device = item.getDevice();
                DataSupplier supplier = item.getSupplier();
                if(CollectionUtil.isNotEmpty(supplier.getValues())) {
                    supplier.getValues().forEach(value -> {
                        String strValue = value.getValue() == null ? null : value.getValue().toString();
                        DefaultRealtimeData data = new DefaultRealtimeData()
                                .setCid(cid).setDeviceSn(device.getDeviceSn())
                                .setCreateTime(new Date()).setValue(strValue)
                                .setAddress(value.getAddress())
                                .setUid(device.getUid())
                                .setFieldId(value.getId())
                                .setCollectMode(CollectMode.model.name())
                                .setField(value.getSignalOrField())
                                .setProductCode(device.getProductCode())
                                .setProtocolCode(device.getProtocolCode())
                                .setDeviceGroupId(device.getDeviceGroupId())
                                .setFieldName(value.getSignalOrField())
                                .setDeviceName(device.getDeviceName())
                                .setCollectTime(supplier.getCollectTime())
                                .setTaskId(info.getSource().getId())
                                .buildFromStatus(value.getStatus());

                        collectData.add(data);
                    });
                }
            });

            this.persistence(collectData);
        }
    }

    /**
     * 持久化
     * @param collectData
     */
    void persistence(List collectData);
}
