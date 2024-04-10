package com.iteaj.iboot.module.iot.collect.model;

import com.iteaj.framework.exception.ServiceException;
import com.iteaj.framework.spi.iot.ProtocolInvokeException;
import com.iteaj.framework.spi.iot.ProtocolSupplierManager;
import com.iteaj.framework.spi.iot.consts.FuncType;
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
import java.util.List;

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
        CollectInfo info = new CollectInfo(source, group);
        Collection<RealtimeStatus> devices = cacheManager.listByGroupId(group.getId());
        devices.forEach(device -> {
            if(device.getStatus() == DeviceStatus.offline) {
                logger.warn("放弃设备采集任务 设备不在线 - 事件源: {} - 设备组: {} - 设备编号: {}"
                        , source.getName(), group.getName(), device.buildDeviceKey());
                return;
            }
            apis.forEach(api -> {
                try {
                    // 对此设备同一产品的模型接口进行调用
                    if(api.getProductId().equals(device.getProductId())) {
                        ProtocolInvokeUtil.invoke(api, device, result -> {
                            if(result.getStatus() != ExecStatus.success) {
                                logger.warn("接口执行失败({}) {} - 事件源: {} - 设备组: {} - 设备编号: {} - 接口: {}/{}", result.getStatus()
                                        , result.getReason(), source.getName(), group.getName(), device.buildDeviceKey(), api.getCode(), api.getDirect());
                            }

                            if(api.getFuncType() != FuncType.W) { // 单纯写的功能没有采集数据
                                ProtocolSupplierManager.getDataSupplier(result.getProtocol()).ifPresent(data -> {
                                    CollectInfo.DeviceInfo deviceInfo = info.addDeviceInfo(device, api, data);

                                    CollectListenerManager.getInstance().modelForEach(item -> {
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
            });
        });

        CollectListenerManager.getInstance().modelForEach(listener -> {
            try {
                listener.listener(info);
            } catch (Exception e) {
                logger.error("采集事件完成发布异常 {} - 事件源: {} - 设备组: {}", e.getMessage(), source.getName(), group.getName(), e);
            }
        });
    }
}
