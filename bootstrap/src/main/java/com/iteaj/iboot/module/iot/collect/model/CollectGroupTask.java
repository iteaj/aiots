package com.iteaj.iboot.module.iot.collect.model;

import com.iteaj.framework.exception.ServiceException;
import com.iteaj.framework.spi.iot.ProtocolInvokeException;
import com.iteaj.framework.spi.iot.ProtocolSupplierManager;
import com.iteaj.framework.spi.iot.consts.FuncType;
import com.iteaj.framework.spi.iot.consts.ProtocolCodes;
import com.iteaj.iboot.module.iot.cache.IotCacheManager;
import com.iteaj.iboot.module.iot.cache.entity.RealtimeStatus;
import com.iteaj.iboot.module.iot.collect.CollectListenerManager;
import com.iteaj.iboot.module.iot.consts.DeviceStatus;
import com.iteaj.iboot.module.iot.dto.EventSourceDto;
import com.iteaj.iboot.module.iot.entity.DeviceGroup;
import com.iteaj.iboot.module.iot.entity.ModelApi;
import com.iteaj.iboot.module.iot.utils.ProtocolInvokeUtil;
import com.iteaj.iot.consts.ExecStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

public class CollectGroupTask implements Runnable{

    private DeviceGroup group;
    private List<ModelApi> apis;
    private EventSourceDto source;
    private IotCacheManager cacheManager;
    private static Logger logger = LoggerFactory.getLogger("IOT:MODEL:COLLECT");

    public CollectGroupTask(DeviceGroup group, EventSourceDto source, IotCacheManager cacheManager) {
        this.group = group;
        this.source = source;
        this.apis = source.getApis();
        this.cacheManager = cacheManager;
    }

    @Override
    public void run() {
        // 没有任何采集接口, 放弃采集任务
        if(this.apis == null || this.apis.size() == 0) {
            logger.warn("放弃采集任务 没有任何采集接口 - 事件源: {} - 设备组: {}", source.getName(), group.getName());
            return;
        }

        String prevParentDeviceSn = null;
        CollectInfo info = new CollectInfo(source, group);
        Collection<RealtimeStatus> devices = cacheManager.listByGroupId(group.getId());
        Iterator<RealtimeStatus> iterator = devices.iterator();
        while (iterator.hasNext()) {
            RealtimeStatus device = iterator.next();
            // 当前设备的产品不在事件源包含的产品里面
            if(device.getProductId() == null || !source.getProductIds().contains(device.getProductId().intValue())) {
                return;
            }

            if(device.getStatus() == DeviceStatus.offline) {
                logger.warn("放弃设备采集任务 设备不在线 - 事件源: {} - 设备组: {} - 设备编号: {}"
                        , source.getName(), group.getName(), device.buildDeviceKey());
                return;
            }

            Iterator<ModelApi> apiIterator = apis.iterator();
            while (apiIterator.hasNext()) {
                ModelApi api = apiIterator.next();
                try {
                    // 对此设备同一产品的模型接口进行调用
                    if(api.getProductId().equals(device.getProductId())) {
                        // 处理modbus dtu等网关性能不足的情况
                        if(ProtocolCodes.isModbus(device.getProtocolCode())) {
                            if(prevParentDeviceSn != null && prevParentDeviceSn.equals(device.getParentDeviceSn())) {
                                Thread.sleep(1000); // 延迟一秒
                            }
                        }

                        doInvoke(info, device, api);
                        prevParentDeviceSn = device.getParentDeviceSn();
                    }
                } catch (ProtocolInvokeException e) {
                    logger.error("接口调用执行异常 {} - 事件源: {} - 设备组: {} - 设备编号: {} - 接口: {}/{}", e.getMessage()
                            , source.getName(), group.getName(), device.buildDeviceKey(), api.getCode(), api.getDirect(), e);
                } catch (ServiceException e) {
                    logger.error("接口调用错误 {} - 事件源: {} - 设备组: {} - 设备编号: {} - 接口: {}/{}", e.getMessage()
                            , source.getName(), group.getName(), device.buildDeviceKey(), api.getCode(), api.getDirect(), e);
                } catch (Exception e) {
                    logger.error("接口调用未知异常 {} - 事件源: {} - 设备组: {} - 设备编号: {} - 接口: {}/{}", e.getMessage()
                            , source.getName(), group.getName(), device.buildDeviceKey(), api.getCode(), api.getDirect(), e);
                }
            }
        }

        CollectListenerManager.getInstance().eventGroupPublish(listener -> {
            try {
                listener.finished(info);
            } catch (Exception e) {
                logger.error("采集事件完成发布异常 {} - 事件源: {} - 设备组: {}", e.getMessage(), source.getName(), group.getName(), e);
            }
        });
    }

    private void doInvoke(CollectInfo info, RealtimeStatus device, ModelApi api) {
        ProtocolInvokeUtil.invoke(api, device, result -> {
            if(result.getStatus() != ExecStatus.success) {
                logger.warn("接口执行失败({}) {} - 事件源: {} - 设备组: {} - 设备编号: {} - 接口: {}/{}", result.getStatus()
                        , result.getReason(), source.getName(), group.getName(), device.buildDeviceKey(), api.getCode(), api.getDirect());
            }

            if(api.getFuncType() != FuncType.W) { // 单纯写的功能没有采集数据
                ProtocolSupplierManager.getDataSupplier(result.getProtocol()).ifPresent(data -> {
                    CollectInfo.DeviceInfo deviceInfo = info.addDeviceInfo(device, api, data);

                    CollectListenerManager.getInstance().eventGroupPublish(item -> {
                        try {
                            item.supplier(source, group, deviceInfo);
                        } catch (Exception e) {
                            logger.error("采集事件实时发布异常 {} - 事件源: {} - 设备组: {} - 设备编号: {} - 接口: {}/{}", e.getMessage()
                                    , source.getName(), group.getName(), device.buildDeviceKey(), api.getCode(), api.getDirect(), e);
                        }
                    });
                });
            }
        });
    }
}
