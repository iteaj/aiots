package com.iteaj.iboot.module.iot.collect.model;

import cn.hutool.core.collection.CollectionUtil;
import com.iteaj.iboot.module.iot.consts.CollectMode;
import com.iteaj.iboot.module.iot.entity.CollectData;
import com.iteaj.iot.utils.UniqueIdGen;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 数据持久化监听
 */
public interface DataPersistenceListener extends EventGroupCollectListener {

    @Override
    default void listener(CollectInfo info) {
        List<CollectData> collectData = new ArrayList<>();
        if(CollectionUtil.isNotEmpty(info.getInfos())) {
            long cid = UniqueIdGen.nextLong(); // 声明同一组
            info.getInfos().forEach(item -> {
                item.getSupplier().getValues().forEach(value -> {
                    CollectData data = new CollectData(CollectMode.model)
                            .setCid(cid).setStatus(true).setReason("")
                            .setCollectTime(item.getCollectTime())
                            .setAddress(value.getAddress())
                            .setUid(item.getDevice().getUid())
                            .setValue(value.getValue().toString())
                            .setSignalId(item.getApi().getId())
                            .setCreateTime(new Date())
                            .setCollectTaskId(info.getSource().getId());

                    collectData.add(data);
                });
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
