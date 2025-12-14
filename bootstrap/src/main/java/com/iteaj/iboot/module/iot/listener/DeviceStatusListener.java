package com.iteaj.iboot.module.iot.listener;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.iteaj.framework.autoconfigure.FrameworkProperties;
import com.iteaj.framework.spi.event.ApplicationReadyListener;
import com.iteaj.framework.spi.iot.DeviceProtocolSupplier;
import com.iteaj.framework.spi.iot.ProtocolSupplierManager;
import com.iteaj.framework.spi.iot.listener.IotEventType;
import com.iteaj.framework.spi.iot.listener.IotListener;
import com.iteaj.framework.thread.IBootThreadManger;
import com.iteaj.iboot.module.iot.cache.IotCacheManager;
import com.iteaj.iboot.module.iot.cache.entity.RealtimeStatus;
import com.iteaj.iboot.module.iot.consts.DeviceStatus;
import com.iteaj.iboot.module.iot.entity.Device;
import com.iteaj.iboot.module.iot.service.IDeviceService;
import com.iteaj.iboot.module.iot.utils.IotLogger;
import com.iteaj.iot.FrameworkComponent;
import com.iteaj.iot.event.CombinedEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * 设备上下线状态事件监听
 */
@Component
public class DeviceStatusListener implements CombinedEventListener, InitializingBean, IotListener<RealtimeStatus> {

    private final IotCacheManager cacheManager;
    private final IDeviceService deviceService;
    private final FrameworkProperties properties;
    private final IBootThreadManger bootThreadManger;

    public DeviceStatusListener(IotCacheManager cacheManager, IDeviceService deviceService
            , FrameworkProperties properties, IBootThreadManger bootThreadManger) {
        this.cacheManager = cacheManager;
        this.deviceService = deviceService;
        this.properties = properties;
        this.bootThreadManger = bootThreadManger;
    }

    @Override
    public void online(String source, FrameworkComponent component) {
        if(!isSkip(source, component)) {
            // 设备上线事件
            DeviceProtocolSupplier supplier = ProtocolSupplierManager.get(component.getMessageClass());
            if(supplier != null) {
                RealtimeStatus device = cacheManager.get(supplier.getProtocol().getCode(), source);
                if(device != null) {
                    device.setStatus(DeviceStatus.online);
                    this.onIotEvent(IotEventType.DeviceStatus, device);
                } else {
                    IotLogger.warn(DeviceStatus.online, null, "设备上线 更新失败(设备不存在) - 设备编号: {} - 驱动: {}", source, supplier.getDesc());
                }
            } else {
                IotLogger.warn(DeviceStatus.online, null, "设备上线 更新失败(未找到驱动) - 设备编号: {} - 网关: {}", source, component.getName());
            }
        }
    }

    @Override
    public void offline(String source, FrameworkComponent component) {
        if(!isSkip(source, component)) {
            // 设备掉线事件
            DeviceProtocolSupplier supplier = ProtocolSupplierManager.get(component.getMessageClass());
            if(supplier != null) {
                RealtimeStatus device = cacheManager.get(supplier.getProtocol().getCode(), source);
                if(device != null) {
                    device.setStatus(DeviceStatus.offline);
                    this.onIotEvent(IotEventType.DeviceStatus, device);
                } else {
                    IotLogger.warn(DeviceStatus.offline, null, "设备离线 更新失败(设备不存在) - 设备编号: {} - 驱动: {}", source, supplier.getDesc());
                }
            } else {
                IotLogger.warn(DeviceStatus.online, null, "设备离线 更新失败(未找到驱动) - 设备编号: {} - 网关: {}", source, component.getName());
            }
        }
    }

    public boolean isSkip(String source, FrameworkComponent component) {
        String name = component.getName();
        return name.equals("MQTT(Gateway)") || name.equals("EDGE_SERVER");
    }

    @Override
    public boolean isEventMatcher(IotEventType eventType) {
        return IotEventType.DeviceStatus == eventType;
    }

    @Override
    public void onIotEvent(IotEventType eventType, RealtimeStatus value) {
        bootThreadManger.getDeviceStatusScheduler()
                .execute(() -> deviceService.updateDeviceStatus(value));
    }

    @Override
    public int getOrder() {
        return Integer.MIN_VALUE + 1000;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if(!properties.isCluster()) {
            // 系统重启时 更新所有的设备状态到离线
            deviceService.update(Wrappers.<Device>lambdaUpdate()
                    .set(Device::getStatus, DeviceStatus.offline)
                    .set(Device::getSwitchTime, new Date()));
        }
    }
}
