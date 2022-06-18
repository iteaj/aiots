package com.iteaj.iboot.msn.iot.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.iteaj.framework.result.DetailResult;
import com.iteaj.framework.result.Result;
import com.iteaj.iboot.msn.iot.consts.DeviceStatus;
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

    /**
     * 更新设备状态
     * @param deviceSn
     * @param status
     */
    void update(String deviceSn, DeviceStatus status);

    /**
     * 获取指定设备编号的记录
     * @param deviceSn
     * @return
     */
    DetailResult<Device> getByDeviceSn(String deviceSn);
}
