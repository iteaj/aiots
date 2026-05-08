package com.iteaj.iboot.module.iot.collect.model;

import cn.hutool.core.collection.CollectionUtil;
import com.iteaj.framework.spi.iot.*;
import com.iteaj.framework.spi.iot.consts.DataType;
import com.iteaj.framework.spi.iot.consts.PointProtocolConfig;
import com.iteaj.framework.spi.iot.data.IModelAttrRealtimeDataService;
import com.iteaj.framework.spi.iot.data.RealtimeDeviceStatus;
import com.iteaj.framework.thread.IBootThreadManger;
import com.iteaj.iboot.module.iot.cache.entity.RealtimeStatus;
import com.iteaj.iboot.module.iot.dto.EventSourceDto;
import com.iteaj.iboot.module.iot.entity.ModelApi;
import com.iteaj.iboot.module.iot.entity.ModelAttrDict;
import com.iteaj.iboot.module.iot.utils.IotLogger;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.*;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

public class EventSourceTask implements Runnable {

    private boolean initialized;
    private final Executor executor;
    private final EventSourceDto source;
    private Map<Long, List<ModelApi>> productApis;
    private final Map<Long, Map<String, List<List<BatchOfPoint>>>> productPoints = new HashMap<>(8);

    public EventSourceTask(Executor executor, EventSourceDto source) {
        this.executor = executor;
        this.source = source;
    }

    @Override
    public void run() {
        initTask();

        CollectInfo collectInfo = new CollectInfo(source);
        // >=jdk21使用虚拟线程
        if(IBootThreadManger.isVirtualThreadSupported()) {
            source.getGroups().forEach(group -> {
                // 注: 由于设备可以实时更新所以获取的设备对象不能再次缓存
                IotCoreApiService.listByGroupId(group.getId()).forEach(device -> {
                    if(source.getProductIds().contains(device.getProductId())) {
                        List<ModelApi> modelApis = productApis.get(device.getProductId());
                        Map<String, List<List<BatchOfPoint>>> productPoints = this.productPoints.get(device.getProductId());
                        executor.execute(new DeviceCollectTask((RealtimeStatus) device, group, collectInfo, modelApis, productPoints));
                    }
                });
            });
        } else {
            if(CollectionUtil.isNotEmpty(source.getGroups())) {
                source.getGroups().forEach(group -> { // 按组采集
                    executor.execute(() -> {
                        // 获取当前组下的所有设备
                        Collection<? extends RealtimeDeviceStatus> devices = IotCoreApiService.listByGroupId(group.getId());
                        for (RealtimeDeviceStatus device : devices) {
                            // 当前设备的产品不在事件源包含的产品里面
                            if (!source.getProductIds().contains(device.getProductId())) {
                                continue;
                            }

                            // 获取设备对应的产品模型接口
                            List<ModelApi> modelApis = productApis.get(device.getProductId());
                            // 获取设备对应的产品模型批量采集点
                            Map<String, List<List<BatchOfPoint>>> batchOfPoints = productPoints.get(device.getProductId());
                            new DeviceCollectTask((RealtimeStatus) device, group, collectInfo, modelApis, batchOfPoints).run();
                        }
                    });
                });
            }
        }

        int remainTaskSize = 0, maxPoolSize = 0;
        if(executor instanceof ThreadPoolTaskExecutor) {
            maxPoolSize = ((ThreadPoolTaskExecutor) executor).getMaxPoolSize();
            remainTaskSize = ((ThreadPoolTaskExecutor) executor).getQueueSize();
        } else if(executor instanceof ThreadPoolExecutor) {
            maxPoolSize = ((ThreadPoolExecutor) executor).getMaximumPoolSize();
            remainTaskSize = ((ThreadPoolExecutor) executor).getQueue().size();
        }

        if(remainTaskSize > 1000) {
            IotLogger.warn(source, "采集任务堆积", "请降低采集频率或增加线程数 - 堆积任务数: {} - 最大线程数: {} - 告警阈值: 1000", remainTaskSize, maxPoolSize);
        }
    }

    private void initTask() {
        if(initialized) {
            return;
        }

        this.productApis = source.getApis()
                .stream().collect(Collectors.groupingBy(ModelApi::getProductId));

        this.productApis.forEach((key, value) -> {
            String protocolCode = source.getProductMaps().get(key).getProtocolCode();
            if(protocolCode != null) {
                DeviceProtocolSupplier<?,?> supplier = ProtocolSupplierManager.get(protocolCode);
                if(supplier instanceof BatchProtocolSupplier) {
                    List<BatchOfPoint> batchOfPoints = resolveModelApiToPoints(value);
                    final BatchEventModelApi batchEventModelApi = ((BatchProtocolSupplier) supplier).getBatchEventModelApi();
                    Map<String, List<List<BatchOfPoint>>> resolver = batchEventModelApi.resolver(batchOfPoints);
                    this.productPoints.put(key, resolver);
                }
            }
        });

        this.initialized = true;
    }

    private List<BatchOfPoint> resolveModelApiToPoints(List<ModelApi> apis) {
        return apis.stream().map(item -> {
            if(CollectionUtil.isNotEmpty(item.getDownConfig())) {
                BatchOfPoint point = new BatchOfPoint();
                point.setDirect(item.getDirect());
                item.getDownConfig().forEach(config -> {
                    if(config.getProtocolAttrField().equals(PointProtocolConfig.POINT_ADDRESS)) {
                        point.setAddress(config.getValue());
                        item.getUpConfig().forEach(upConfig -> {
                            if(upConfig.getProtocolAttrField().equals(PointProtocolConfig.POINT_VALUE)) {
                                final UpModelAttr upModelAttr = new UpModelAttr(upConfig.getModelAttrId(), upConfig.getAttrField(), upConfig.getAttrName()
                                        , DataType.build(upConfig.getDataType()), upConfig.getAccuracy(), upConfig.getGain()
                                        , upConfig.getResolver(), upConfig.getScript(), null);
                                point.setModelAttr(upModelAttr);

                                // 位属性配置
                                if(upConfig.getDataType().equals(DataType.t_bit.getValue())) {
                                    final List<ModelAttrDict> dicts = upConfig.getDicts();
                                    if(CollectionUtil.isNotEmpty(dicts)) {
                                        final List<UpModelAttr> bitAttrs = dicts.stream().map(dict -> new UpModelAttr(dict.getModelAttrId(), dict.getAttrField(), dict.getDictName()
                                                , DataType.build(dict.getDataType()), dict.getAccuracy(), dict.getGain()
                                                , dict.getResolver(), dict.getScript(), dict.getPath()).buildBit()).collect(Collectors.toList());
                                        point.setBitAttrs(bitAttrs);
                                    }
                                }
                            }
                        });

                    } else {
                        point.addDownConfig(config.getProtocolAttrField(), config.getValue());
                    }
                });

                return point;
            }

            return null;
        }).filter(Objects::nonNull).collect(Collectors.toList());
    }
}
