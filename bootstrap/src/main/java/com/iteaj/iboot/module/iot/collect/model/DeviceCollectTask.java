package com.iteaj.iboot.module.iot.collect.model;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson2.JSONObject;
import com.iteaj.framework.exception.ServiceException;
import com.iteaj.framework.spi.iot.*;
import com.iteaj.framework.spi.iot.listener.IotEvenPublisher;
import com.iteaj.framework.spi.iot.listener.IotEventType;
import com.iteaj.iboot.module.iot.cache.entity.RealtimeStatus;
import com.iteaj.iboot.module.iot.collect.CollectListenerManager;
import com.iteaj.iboot.module.iot.consts.DeviceStatus;
import com.iteaj.iboot.module.iot.consts.DeviceType;
import com.iteaj.iboot.module.iot.entity.DeviceGroup;
import com.iteaj.iboot.module.iot.entity.EventSource;
import com.iteaj.iboot.module.iot.entity.ModelApi;
import com.iteaj.iboot.module.iot.utils.IotLogger;
import com.iteaj.iboot.module.iot.utils.ProtocolInvokeUtil;
import com.iteaj.iot.consts.ExecStatus;

import java.util.List;
import java.util.Map;

public class DeviceCollectTask implements Runnable {

    private final DeviceGroup group;
    private final List<ModelApi> apis;
    private final RealtimeStatus device;
    private final CollectInfo collectInfo;
    // direct -> 批量点位
    private final Map<String, List<List<BatchOfPoint>>> batchOfPoints;

    public DeviceCollectTask(RealtimeStatus device, DeviceGroup group, CollectInfo collectInfo
            , List<ModelApi> apis, Map<String, List<List<BatchOfPoint>>> batchOfPoints) {
        this.apis = apis;
        this.group = group;
        this.device = device;
        this.collectInfo = collectInfo;
        this.batchOfPoints = batchOfPoints;
    }

    @Override
    public void run() {
        RealtimeStatus parent = null;
        EventSource source = collectInfo.getSource();
        if (device.getType() == DeviceType.Child) {
            parent = (RealtimeStatus) IotCoreApiService.getDeviceStatus(device.getProtocolCode(), device.getParentDeviceSn());
            if(parent == null) {
                IotLogger.warn(source, device.getUid(), "放弃设备采集任务 父设备不存在({}) - 设备组: {} - 设备编号: {}"
                        , device.getParentDeviceSn(), group.getName(), device.buildKey());

                return;
            }

            if(parent.getStatus() == DeviceStatus.offline) {
                IotLogger.warn(source, device.getUid(), "放弃设备采集任务 父设备不在线({}) - 设备组: {} - 设备编号: {}"
                        , device.getParentDeviceSn(), group.getName(), device.buildKey());

                return;
            }
        } else if(device.getType() == DeviceType.Direct) {
            if(device.getStatus() == DeviceStatus.offline) {
                IotLogger.warn(source, device.getUid(), "放弃设备采集任务 设备不在线({}) - 设备组: {} - 设备编号: {}"
                        , device.getDeviceSn(), group.getName(), device.buildKey());

                return;
            }
        }

        boolean batch = isBatch(device); // 是否启用批量
        boolean isBatchPoints = CollectionUtil.isNotEmpty(batchOfPoints); // 是否有批量的点位
        CollectInfo.DeviceInfo deviceInfo = collectInfo.addDeviceInfo(device);
        if(batch && isBatchPoints) { // 批量采集
            DeviceProtocolSupplier<?, ?> supplier = ProtocolSupplierManager.get(device.getProtocolCode());
            final ProtocolModelApiInvokeParam invokeParam = ProtocolInvokeUtil.createModelApiInvokeParam(device, parent);
            try {
                ((BatchProtocolSupplier) supplier).getBatchEventModelApi().invoke(invokeParam, batchOfPoints, result -> {
                    deviceInfo.merge(result.getStatus()).merge(result.getValue());
                    updateDeviceStatus(device, result.getStatus());
                    dataPublish(deviceInfo, device);
                });
            } catch (Exception e) {
                IotLogger.error(source, device.getUid(), "批量采集执行异常 {} - 设备组: {} - 设备编号: {}"
                        , e.getMessage(), group.getName(), device.buildDeviceKey(), e);
            }
        } else {
            for (ModelApi api : this.apis) {
                try {
                    // 调用接口采集数据
                    doInvoke(deviceInfo, device, api, 3000L, false);
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

            // 状态不为null说明有做数据采集
            if(deviceInfo.getStatus() != null) {
                updateDeviceStatus(device, deviceInfo.getStatus());
                dataPublish(deviceInfo, device);
            }
        }
    }

    /**
     * 设备是否支持批量采集
     * @param device 设备
     * @return 是否支持批量采集
     */
    protected boolean isBatch(RealtimeStatus device) {
        final JSONObject config = device.getConfig();
        return config != null && Boolean.TRUE.equals(config.getBoolean("isBatch"));
    }

    /**
     * 调用采集接口
     * @param device 设备
     */
    protected void doInvoke(CollectInfo.DeviceInfo info, RealtimeStatus device, ModelApi api, long timeout, boolean retry) {
        ProtocolInvokeUtil.invoke(api, device, timeout, result -> {
            info.merge(result.getStatus()); // 合并接口执行状态
            if(result.getStatus() == ExecStatus.success) {
                // 获取采集的数据并且合并到设备信息
                ProtocolSupplierManager.getDataSupplier(result.getProtocol()).ifPresent(info::merge);
            } else {
                if(result.getStatus() == ExecStatus.timeout && !retry) {
                    // 失败后的重试
                    this.doInvoke(info, device, api, 5000L, true);
                } else {
                    if(retry) {
                        IotLogger.warn(collectInfo.getSource(), device.getUid(), "接口采集重试失败 {} - 设备组: {} - 设备编号: {} - 接口: {}/{}"
                                , result.getReason(), group.getName(), device.buildDeviceKey(), api.getCode(), api.getDirect());
                    } else {
                        IotLogger.warn(collectInfo.getSource(), device.getUid(), "接口采集失败 {} - 设备组: {} - 设备编号: {} - 接口: {}/{}"
                                , result.getReason(), group.getName(), device.buildDeviceKey(), api.getCode(), api.getDirect());
                    }
                }
            }
        });
    }

    protected void dataPublish(CollectInfo.DeviceInfo deviceInfo, RealtimeStatus device) {
        if(CollectionUtil.isNotEmpty(deviceInfo.getSupplier().getValues())) {
            // 调用计算脚本
//            CalcAttrScriptMethodProvide.invokeCalcScript(deviceInfo.getSupplier(), device);

            // 发布设备采集事件
            CollectListenerManager.getInstance().eventGroupPublish(item -> {
                try {
                    item.supplier(collectInfo.getSource(), group, deviceInfo);
                } catch (Exception e) {
                    IotLogger.error(collectInfo.getSource(), device.getUid(), "采集事件实时发布异常 {} - 设备组: {} - 设备编号: {}"
                            , e.getMessage(), group.getName(), device.buildDeviceKey(), e);
                }
            });
        } else {
            IotLogger.debug(collectInfo.getSource(), device.getUid(), "设备未采集到任务数据 {} - 设备组: {} - 设备编号: {}"
                    , group.getName(), device.buildDeviceKey());
        }
    }

    /**
     * 动态更新设备的状态
     * @see DeviceStatusModelApiManager#doInvoke(ModelApi, RealtimeStatus, long, boolean)
     */
    protected void updateDeviceStatus(RealtimeStatus device, ExecStatus execStatus) {
        DeviceStatus status = execStatus == ExecStatus.success ? DeviceStatus.online : DeviceStatus.offline;
        if(device.getStatus() != status) {
            device.setStatus(status);
            IotEvenPublisher.publish(IotEventType.DeviceStatus, device);
        }
    }
}
