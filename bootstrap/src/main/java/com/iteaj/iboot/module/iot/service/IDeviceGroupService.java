package com.iteaj.iboot.module.iot.service;

import com.iteaj.framework.result.DetailResult;
import com.iteaj.framework.result.ListResult;
import com.iteaj.framework.result.Result;
import com.iteaj.iboot.module.iot.entity.DeviceGroup;
import com.iteaj.framework.IBaseService;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

/**
 * <p>
 * 设备分组 服务类
 * </p>
 *
 * @author iteaj
 */
public interface IDeviceGroupService extends IBaseService<DeviceGroup> {

    ListResult<DeviceGroup> tree(DeviceGroup entity);

    boolean hasBindDevice(Collection<?> idList);

    DetailResult<DeviceGroup> detailById(Long id);
}
