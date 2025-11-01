package com.iteaj.iboot.module.iot.collect.model;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSONObject;
import com.iteaj.framework.exception.ServiceException;
import com.iteaj.framework.spi.iot.*;
import com.iteaj.framework.spi.iot.consts.DataType;
import com.iteaj.framework.spi.iot.consts.FuncType;
import com.iteaj.framework.spi.iot.consts.PointProtocolConfig;
import com.iteaj.framework.spi.iot.consts.ProtocolCodes;
import com.iteaj.framework.spi.iot.listener.IotEvenPublisher;
import com.iteaj.framework.spi.iot.listener.IotEventType;
import com.iteaj.framework.spi.iot.protocol.InvokeResult;
import com.iteaj.iboot.module.iot.cache.IotCacheManager;
import com.iteaj.iboot.module.iot.cache.entity.RealtimeStatus;
import com.iteaj.iboot.module.iot.collect.CollectListenerManager;
import com.iteaj.iboot.module.iot.consts.DeviceStatus;
import com.iteaj.iboot.module.iot.consts.DeviceType;
import com.iteaj.iboot.module.iot.dto.EventSourceDto;
import com.iteaj.iboot.module.iot.entity.DeviceGroup;
import com.iteaj.iboot.module.iot.entity.ModelApi;
import com.iteaj.iboot.module.iot.entity.ModelAttrDict;
import com.iteaj.iboot.module.iot.utils.IotLogger;
import com.iteaj.iboot.module.iot.utils.ProtocolInvokeUtil;
import com.iteaj.iot.consts.ExecStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

import static com.iteaj.iboot.module.iot.collect.model.BatchProductManagerListener.NOT_BATCH_POINTS;

/**
 * 批量采集任务
 */
public class BatchGroupTask implements Runnable{

    private DeviceGroup group;
    private EventSourceDto source;
    private IotCacheManager cacheManager;

    public BatchGroupTask(DeviceGroup group, EventSourceDto source, IotCacheManager cacheManager) {
        this.group = group;
        this.source = source;
        this.cacheManager = cacheManager;
    }

    private List<BatchOfPoint> resolveModelApiToPoints(Long productId) {
        return source.getApis().stream().filter(item -> productId.equals(item.getProductId())).map(item -> {
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

    @Override
    public void run() {
        // 没有任何采集接口, 放弃采集任务
        if(CollectionUtil.isEmpty(this.source.getApis())) {
            IotLogger.warn(source, "-", "放弃采集任务 没有任何采集接口 - 设备组: {}", group.getName());
            return;
        }

        CollectInfo info = new CollectInfo(source, group);
        Collection<RealtimeStatus> devices = cacheManager.listByGroupId(group.getId());
        for (RealtimeStatus device : devices) {
            // 当前设备的产品不在事件源包含的产品里面
            if (device.getProductId() == null || !source.getProductIds().contains(device.getProductId().intValue())) {
                continue;
            }

            RealtimeStatus parent = null;
            if (device.getType() == DeviceType.Child) {
                parent = cacheManager.get(device.getProtocolCode(), device.getParentDeviceSn());
                if(parent == null) {
                    IotLogger.warn(source, device.getUid(), "放弃设备采集任务 父设备不存在({}) - 设备组: {} - 设备编号: {}"
                            , device.getParentDeviceSn(), group.getName(), device.buildDeviceKey());

                    continue;
                }

                if(parent.getStatus() == DeviceStatus.offline) {
                    IotLogger.warn(source, device.getUid(), "放弃设备采集任务 父设备不在线({}) - 设备组: {} - 设备编号: {}"
                            , device.getParentDeviceSn(), group.getName(), device.buildDeviceKey());

                    continue;
                }
            }

            Map<String, List<List<BatchOfPoint>>> batchOfPoints = resolverBatchInfo(device);
            if(batchOfPoints == NOT_BATCH_POINTS) { // 非批量采集
                for (ModelApi api : source.getApis()) {
                    try {
                        // 对此设备同一产品的模型接口进行调用
                        if (api.getProductId().equals(device.getProductId())) {
                            doInvoke(info, device, api, 3000L, false);
                        }
                    } catch (ProtocolInvokeException e) {
                        IotLogger.error(source, device.getUid(), "接口采集执行异常 {} - 设备组: {} - 设备编号: {} - 接口: {}/{}", e.getMessage()
                                , group.getName(), device.buildDeviceKey(), api.getCode(), api.getDirect(), e);
                    } catch (ServiceException e) {
                        IotLogger.error(source, device.getUid(), "接口采集执行错误 {} - 设备组: {} - 设备编号: {} - 接口: {}/{}", e.getMessage()
                                , group.getName(), device.buildDeviceKey(), api.getCode(), api.getDirect(), e);
                    } catch (Exception e) {
                        IotLogger.error(source, device.getUid(), "接口采集未知异常 {} - 设备组: {} - 设备编号: {} - 接口: {}/{}", e.getMessage()
                                , group.getName(), device.buildDeviceKey(), api.getCode(), api.getDirect(), e);
                    }
                }
            } else {
                DeviceProtocolSupplier supplier = ProtocolSupplierManager.getInstance().get(device.getProtocolCode());
                final ProtocolModelApiInvokeParam invokeParam = ProtocolInvokeUtil.createModelApiInvokeParam(device, parent);
                ((BatchProtocolSupplier) supplier).getBatchEventModelApi().invoke(invokeParam, batchOfPoints, result -> {
                    dataPublish(info, device, null, result);
                });
            }
        }

//        CollectListenerManager.getInstance().eventGroupPublish(listener -> {
//            try {
//                listener.finished(info);
//            } catch (Exception e) {
//                IotLogger.error(source, "-", "采集事件完成发布异常 {} - 设备组: {}", e.getMessage(), group.getName(), e);
//            }
//        });
    }

    private Map<String, List<List<BatchOfPoint>>> resolverBatchInfo(RealtimeStatus device) {
        Map<String, List<List<BatchOfPoint>>> batchOfPoints = NOT_BATCH_POINTS;
        if(isBatch(device)) { // 此设备是否支持批量
            batchOfPoints = BatchProductManagerListener.getPoints(device.getProductId());
            if(batchOfPoints == null) {
                final DeviceProtocolSupplier supplier = ProtocolSupplierManager.getInstance().get(device.getProtocolCode());
                if(supplier instanceof BatchProtocolSupplier) {
                    final BatchEventModelApi batchEventModelApi = ((BatchProtocolSupplier) supplier).getBatchEventModelApi();
                    final List<BatchOfPoint> modelApiToPoints = resolveModelApiToPoints(device.getProductId());
                    batchOfPoints = batchEventModelApi.resolver(modelApiToPoints);
                    BatchProductManagerListener.register(device.getProductId(), batchOfPoints);
                } else { // 非批量
                    BatchProductManagerListener.register(device.getProductId(), batchOfPoints = NOT_BATCH_POINTS);
                }
            }
        }

        return batchOfPoints;
    }

    private boolean isBatch(RealtimeStatus device) {
        final JSONObject config = device.getConfig();
        return config != null && Boolean.TRUE.equals(config.getBoolean("isBatch"));
    }

    private void doInvoke(CollectInfo info, RealtimeStatus device, ModelApi api, long timeout, boolean retry) {
        ProtocolInvokeUtil.invoke(api, device, timeout, result -> {
            if(result.getStatus() == ExecStatus.success) {
                if(api.getFuncType() != FuncType.W) { // 单纯写的功能没有采集数据
                    // 发布采集的数据
                    dataPublish(info, device, api, result);
                }
            } else {
                if(result.getStatus() == ExecStatus.timeout && !retry) {
                    this.doInvoke(info, device, api, 6000L, true);
                } else {
                    if(retry) {
                        IotLogger.warn(source, device.getUid(), "接口采集重试失败 {} - 设备组: {} - 设备编号: {} - 接口: {}/{}", result.getReason()
                                , group.getName(), device.buildDeviceKey(), api.getCode(), api.getDirect());
                    } else {
                        IotLogger.warn(source, device.getUid(), "接口采集失败 {} - 设备组: {} - 设备编号: {} - 接口: {}/{}", result.getReason()
                                , group.getName(), device.buildDeviceKey(), api.getCode(), api.getDirect());
                    }

                    // 发布采集的数据
                    dataPublish(info, device, api, result);
                }
            }
        });
    }

    private void dataPublish(CollectInfo info, RealtimeStatus device, ModelApi api, InvokeResult result) {
        /**
         * 动态更新设备的状态
         * @see DeviceStatusModelApiManager#doInvoke(ModelApi, RealtimeStatus, long, boolean)
         */
        DeviceStatus status = result.getStatus() == ExecStatus.success ? DeviceStatus.online : DeviceStatus.offline;
        if(device.getStatus() != status) {
            device.setStatus(status);
            IotEvenPublisher.publish(IotEventType.DeviceStatus, device);
        }

        CollectInfo.DeviceInfo deviceInfo;
        if(result instanceof BatchResult) {
            deviceInfo = info.addDeviceInfo(device, api, ((BatchResult) result).getValue());
        } else {
            if(result.getStatus() != ExecStatus.success && result.getProtocol() == null) {
                return;
            }

            deviceInfo = ProtocolSupplierManager.getDataSupplier(result.getProtocol()).map(data -> info.addDeviceInfo(device, api, data)).get();
        }

        if(deviceInfo != null) {
            CollectListenerManager.getInstance().eventGroupPublish(item -> {
                try {
                    item.supplier(source, group, deviceInfo);
                } catch (Exception e) {
                    IotLogger.error(source, device.getUid(), "采集事件实时发布异常 {} - 设备组: {} - 设备编号: {} - 接口: {}/{}", e.getMessage()
                            , group.getName(), device.buildDeviceKey(), api.getCode(), api.getDirect(), e);
                }
            });
        }
    }

}
