package com.iteaj.iboot.module.iot.listener;

import cn.hutool.core.collection.CollectionUtil;
import com.iteaj.framework.spi.iot.IotCoreApiService;
import com.iteaj.framework.spi.iot.data.RealtimeCollectData;
import com.iteaj.framework.spi.iot.data.RealtimeDeviceStatus;
import com.iteaj.framework.spi.iot.listener.EntityPayload;
import com.iteaj.framework.spi.iot.listener.IotEventType;
import com.iteaj.framework.spi.iot.listener.IotListener;
import com.iteaj.framework.spi.iot.listener.RealtimePayload;
import com.iteaj.iboot.module.iot.consts.FuncStatus;
import com.iteaj.iboot.module.iot.entity.DeviceCmdLog;
import com.iteaj.iboot.module.iot.entity.DeviceRealtimeCache;
import com.iteaj.iboot.module.iot.entity.Product;
import com.iteaj.iboot.module.iot.service.IDeviceCmdLogService;
import com.iteaj.iboot.module.iot.service.IDeviceRealtimeCacheService;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * 设备其他事件监听器
 * 包括: 设备命令日志、设备实时数据缓存
 */
@Component
public class DeviceOtherEventListener implements IotListener<Object> {

    private final IDeviceCmdLogService deviceCmdLogService;
    private final IDeviceRealtimeCacheService deviceRealtimeCacheService;

    public DeviceOtherEventListener(IDeviceRealtimeCacheService deviceRealtimeCacheService
            , IDeviceCmdLogService deviceCmdLogService) {
        this.deviceRealtimeCacheService = deviceRealtimeCacheService;
        this.deviceCmdLogService = deviceCmdLogService;
    }

    @Override
    public boolean isEventMatcher(IotEventType eventType) {
        return eventType == IotEventType.DeviceCmdLog || eventType == IotEventType.Realtime;
    }

    @Override
    public void onIotEvent(IotEventType eventType, Object value) {
        if(eventType == IotEventType.DeviceCmdLog) {
            DeviceCmdLog log = (DeviceCmdLog) value;
            this.deviceCmdLogService.save(log);
        } else if(eventType == IotEventType.Realtime){
            RealtimePayload payload = (RealtimePayload) value;
            Collection<?> datas = payload.getPayload();
            if(CollectionUtil.isNotEmpty(datas)) {
                if(payload.isData() || payload.isDataActive() || payload.isDataCmd()) {
                    List<DeviceRealtimeCache> realtimeCaches = datas.stream().filter(Objects::nonNull).map(item -> {
                        RealtimeCollectData collectData = (RealtimeCollectData) item;
                        return new DeviceRealtimeCache().setUid(collectData.getUid())
                                .setCollectTime(collectData.getCollectTime())
                                .setFieldId(collectData.getFieldId()).setField(collectData.getField())
                                .setValue(collectData.getValue() != null ? collectData.getValue().toString() : "");
                    }).toList();
                    if(!realtimeCaches.isEmpty()) {
                        deviceRealtimeCacheService.cacheOrUpdate(realtimeCaches);
                    }
                }
            }
        }
    }

}
