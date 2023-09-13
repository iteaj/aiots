package com.iteaj.iboot.module.iot.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.iteaj.framework.IBaseService;
import com.iteaj.framework.result.PageResult;
import com.iteaj.iboot.module.iot.dto.ProductDto;
import com.iteaj.iboot.module.iot.entity.DeviceModel;

/**
 * <p>
 * 设备型号 服务类
 * </p>
 *
 * @author iteaj
 * @since 2022-07-22
 */
public interface IDeviceModelService extends IBaseService<DeviceModel> {

    PageResult<IPage<ProductDto>> detailOfPage(Page page, DeviceModel entity);
}
