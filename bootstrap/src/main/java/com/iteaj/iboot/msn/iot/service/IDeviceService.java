package com.iteaj.iboot.msn.iot.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.iteaj.framework.result.Result;
import com.iteaj.iboot.msn.iot.dto.DeviceDto;
import com.iteaj.iboot.msn.iot.entity.Device;
import com.iteaj.framework.IBaseService;

/**
 * <p>
 * 设备表 服务类
 * </p>
 *
 * @author iteaj
 * @since 2022-05-15
 */
public interface IDeviceService extends IBaseService<Device> {

    /**
     * 获取设备详情分页列表
     * @param page
     * @param entity
     * @return
     */
    Result<IPage<Device>> pageOfDetail(Page<Device> page, DeviceDto entity);
}
