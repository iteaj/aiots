package com.iteaj.iboot.module.iot.service.impl;

import com.iteaj.framework.BaseServiceImpl;
import com.iteaj.iboot.module.iot.entity.DeviceRealtimeCache;
import com.iteaj.iboot.module.iot.mapper.DeviceRealtimeCacheMapper;
import com.iteaj.iboot.module.iot.service.IDeviceRealtimeCacheService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 实时数据缓存 Service 实现
 */
@Service
public class DeviceRealtimeCacheServiceImpl extends BaseServiceImpl<DeviceRealtimeCacheMapper, DeviceRealtimeCache> implements IDeviceRealtimeCacheService {

    @Override
    public void cacheOrUpdate(List<DeviceRealtimeCache> entities) {
        getBaseMapper().cacheOrUpdate(entities);
    }
}
