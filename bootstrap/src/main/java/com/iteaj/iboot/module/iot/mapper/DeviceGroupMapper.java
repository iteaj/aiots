package com.iteaj.iboot.module.iot.mapper;

import com.iteaj.iboot.module.iot.entity.DeviceGroup;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.io.Serializable;
import java.util.Collection;

/**
 * <p>
 * 设备分组 Mapper 接口
 * </p>
 *
 * @author iteaj
 */
public interface DeviceGroupMapper extends BaseMapper<DeviceGroup> {

    boolean hasBindDevice(Collection<?> idList);

    DeviceGroup detailById(Long id);

    boolean hasBindEventSource(Collection<? extends Serializable> idList);
}
