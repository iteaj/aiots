package com.iteaj.iboot.module.iot.listener;

import com.iteaj.framework.spi.iot.listener.IotEventType;
import com.iteaj.framework.spi.iot.listener.IotListener;
import com.iteaj.iboot.module.iot.entity.DeviceCmdLog;
import com.iteaj.iboot.module.iot.service.IDeviceCmdLogService;
import org.springframework.stereotype.Component;

/**
 * 设备其他事件监听器
 */
@Component
public class DeviceOtherEventListener implements IotListener<Object> {

    private final IDeviceCmdLogService deviceCmdLogService;

    public DeviceOtherEventListener(IDeviceCmdLogService deviceCmdLogService) {
        this.deviceCmdLogService = deviceCmdLogService;
    }

    @Override
    public boolean isEventMatcher(IotEventType eventType) {
        return eventType == IotEventType.DeviceCmdLog;
    }

    @Override
    public void onIotEvent(IotEventType eventType, Object value) {
        if(eventType == IotEventType.DeviceCmdLog) {
            DeviceCmdLog log = (DeviceCmdLog) value;
            this.deviceCmdLogService.save(log);
        }
    }

}
