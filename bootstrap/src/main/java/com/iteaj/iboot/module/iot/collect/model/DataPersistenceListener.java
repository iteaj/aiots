package com.iteaj.iboot.module.iot.collect.model;

import cn.hutool.core.collection.CollectionUtil;
import com.iteaj.framework.spi.iot.DataSupplier;
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
public interface DataPersistenceListener extends EventGroupCollectListener {

    @Override
    default void supplier(EventSource source, DeviceGroup group, CollectInfo.DeviceInfo info) {

    }

    @Override
    default void listener(CollectInfo info) {
        List<CollectData> collectData = new ArrayList<>();
        if(CollectionUtil.isNotEmpty(info.getInfos())) {
            long cid = UniqueIdGen.nextLong(); // 声明同一组
            info.getInfos().forEach(item -> {
                DataSupplier supplier = item.getSupplier();
                if(CollectionUtil.isNotEmpty(supplier.getValues())) {
                    supplier.getValues().forEach(value -> {
                        CollectData data = new CollectData(CollectMode.model)
                                .setCid(cid).setStatus(true).setReason("")
                                .setCreateTime(new Date())
                                .setAddress(value.getAddress())
                                .setUid(Long.valueOf(item.getDevice().getUid()))
                                .setSignalId(item.getApi().getId())
                                .setValue(value.getValue().toString())
                                .setCollectTime(supplier.getCollectTime())
                                .setCollectTaskId(info.getSource().getId());

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
