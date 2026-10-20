package com.iteaj.iboot.module.iot.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.iteaj.iboot.module.iot.entity.DeviceRealtimeCache;

import java.util.List;

/**
 * 实时数据缓存 Mapper
 */
public interface DeviceRealtimeCacheMapper extends BaseMapper<DeviceRealtimeCache> {

    void cacheOrUpdate(List<DeviceRealtimeCache> entities);
}
