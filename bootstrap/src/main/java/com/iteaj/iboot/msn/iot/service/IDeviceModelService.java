package com.iteaj.iboot.msn.iot.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.iteaj.framework.result.PageResult;
import com.iteaj.framework.result.Result;
import com.iteaj.iboot.msn.iot.entity.DeviceModel;
import com.iteaj.framework.IBaseService;

/**
 * <p>
 * 设备型号 服务类
 * </p>
 *
 * @author iteaj
 * @since 2022-07-22
 */
public interface IDeviceModelService extends IBaseService<DeviceModel> {

    PageResult<IPage<DeviceModel>> detailOfPage(Page<DeviceModel> page, DeviceModel entity);
}
