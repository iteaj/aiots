package com.iteaj.iboot.module.iot.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.iteaj.framework.result.DetailResult;
import com.iteaj.framework.result.ListResult;
import com.iteaj.framework.result.Result;
import com.iteaj.iboot.module.iot.dto.CurrentDeviceDto;
import com.iteaj.iboot.module.iot.dto.DeviceDto;
import com.iteaj.iboot.module.iot.consts.DeviceStatus;
import com.iteaj.iboot.module.iot.entity.Device;
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
    Result<IPage<DeviceDto>> pageOfDetail(Page<Device> page, DeviceDto entity);

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

    /**
     * 统计当前设备信息
     * @return
     */
    CurrentDeviceDto countCurrentDevice();

    /**
     * 获取设备详情
     * @param id
     * @return
     */
    DetailResult<DeviceDto> detailById(Long id);

    /**
     * 获取网关设备
     * @return
     * @param gatewayId
     */
    ListResult<Device> listOfGateway(Long gatewayId);

    /**
     * 获取指定产品下面的所有设备
     * @param productId
     * @return
     */
    ListResult<DeviceDto> listByProductId(Long productId);
}
