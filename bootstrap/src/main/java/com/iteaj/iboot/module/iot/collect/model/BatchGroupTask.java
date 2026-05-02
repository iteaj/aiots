package com.iteaj.iboot.module.iot.collect.model;

import com.iteaj.framework.spi.iot.BatchOfPoint;
import com.iteaj.iboot.module.iot.cache.IotCacheManager;
import com.iteaj.iboot.module.iot.cache.entity.RealtimeStatus;
import com.iteaj.iboot.module.iot.entity.DeviceGroup;
import com.iteaj.iboot.module.iot.entity.EventSource;
import com.iteaj.iboot.module.iot.entity.ModelApi;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 批量采集任务
 */
public class BatchGroupTask implements Runnable{

    private CollectInfo info;
    private DeviceGroup group;
    private EventSource source;
    private IotCacheManager cacheManager;
    private Map<Long, List<ModelApi>> productApis;
    private Map<Long, Map<String, List<List<BatchOfPoint>>>> productPoints;

    public BatchGroupTask(DeviceGroup group, CollectInfo info, Map<Long, List<ModelApi>> productApis
            , Map<Long, Map<String, List<List<BatchOfPoint>>>> productPoints, IotCacheManager cacheManager) {
        this.info = info;
        this.group = group;
        this.source = info.getSource();
        this.productApis = productApis;
        this.productPoints = productPoints;
        this.cacheManager = cacheManager;
    }

    @Override
    public void run() {
        Collection<RealtimeStatus> devices = cacheManager.listByGroupId(group.getId());
        for (RealtimeStatus device : devices) {
            // 当前设备的产品不在事件源包含的产品里面
            if (device.getProductId() == null || !source.getProductIds().contains(device.getProductId())) {
                continue;
            }

            // 获取设备对应的产品模型接口
            List<ModelApi> modelApis = productApis.get(device.getProductId());
            // 获取设备对应的产品模型批量采集点
            Map<String, List<List<BatchOfPoint>>> batchOfPoints = productPoints.get(device.getProductId());
            new DeviceCollectTask(device, group, info, modelApis, batchOfPoints, cacheManager).run();
        }
    }

}
