package com.iteaj.iboot.module.iot.service;

import com.iteaj.framework.IBaseService;
import com.iteaj.iboot.module.iot.entity.DeviceRealtimeCache;

import java.util.List;

/**
 * 实时数据缓存 Service
 */
public interface IDeviceRealtimeCacheService extends IBaseService<DeviceRealtimeCache> {

    void cacheOrUpdate(List<DeviceRealtimeCache> entities);
}
