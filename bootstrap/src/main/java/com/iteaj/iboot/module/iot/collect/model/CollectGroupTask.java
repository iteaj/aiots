package com.iteaj.iboot.module.iot.collect.model;

import com.iteaj.framework.spi.iot.ProtocolInvokeException;
import com.iteaj.framework.spi.iot.ProtocolSupplierManager;
import com.iteaj.iboot.module.iot.cache.IotCacheManager;
import com.iteaj.iboot.module.iot.cache.entity.RealtimeStatus;
import com.iteaj.iboot.module.iot.dto.EventSourceDto;
import com.iteaj.iboot.module.iot.entity.DeviceGroup;
import com.iteaj.iboot.module.iot.entity.ModelApi;
import com.iteaj.iboot.module.iot.utils.ProtocolInvokeUtil;
import com.iteaj.iot.consts.ExecStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Date;
import java.util.List;

public class CollectGroupTask implements Runnable{

    private DeviceGroup group;
    private List<ModelApi> apis;
    private EventSourceDto source;
    private IotCacheManager cacheManager;
    private static Logger logger = LoggerFactory.getLogger(CollectGroupTask.class);

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
            apis.forEach(api -> {
                try {
                    // 对此设备同一产品的模型接口进行调用
                    if(api.getProductId().equals(device.getProductId())) {
                        ProtocolInvokeUtil.invoke(api, device, result -> {
                            if(result.getStatus() == ExecStatus.success) {
                                ProtocolSupplierManager.getDataSupplier(result.getProtocol()).ifPresent(data -> {
                                    info.addDeviceInfo(device, api, data.getCollectTime() == null ? data.build(new Date()) : data);
                                });
                            } else {
                                logger.warn("接口执行失败({}) {} - 设备编号: {} - 接口: {}/{}", result.getStatus()
                                        , result.getReason(), device.buildDeviceKey(), api.getCode(), api.getDirect());
                            }
                        });
                    }
                } catch (ProtocolInvokeException e) {
                    logger.error("接口调用执行异常 {} - 设备编号: {} - 接口: {}/{}", e.getMessage()
                            , device.buildDeviceKey(), api.getCode(), api.getDirect(), e);
                } catch (IllegalArgumentException e) {
                    logger.error("接口调用参数错误 {} - 设备编号: {} - 接口: {}/{}", e.getMessage()
                            , device.buildDeviceKey(), api.getCode(), api.getDirect(), e);
                } catch (Exception e) {
                    logger.error("接口调用未知异常 {} - 设备编号: {} - 接口: {}/{}", e.getMessage()
                            , device.buildDeviceKey(), api.getCode(), api.getDirect(), e);
                }
            });
        });

        EventGroupCollectManager.getInstance().forEach(listener -> {
            listener.listener(info);
        });
    }
}
