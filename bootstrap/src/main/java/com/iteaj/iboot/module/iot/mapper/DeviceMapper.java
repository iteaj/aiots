package com.iteaj.iboot.module.iot.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.iteaj.iboot.module.iot.dto.CurrentDeviceDto;
import com.iteaj.iboot.module.iot.dto.DeviceDto;
import com.iteaj.iboot.module.iot.entity.Device;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * <p>
 * 设备表 Mapper 接口
 * </p>
 *
 * @author iteaj
 * @since 2022-05-15
 */
public interface DeviceMapper extends BaseMapper<Device> {

    /**
     * 获取设备详情分页列表
     * @param page
     * @param entity
     * @return
     */
    IPage<DeviceDto> pageOfDetail(Page<Device> page, DeviceDto entity);

    /**
     * 统计当前设备信息
     * @return
     */
    CurrentDeviceDto countCurrentDevice();

    DeviceDto detailById(Long id);

    /**
     * 获取网关设备
     * @see com.iteaj.iboot.module.iot.consts.DeviceType#Gateway
     * @return
     */
    List<Device> listOfGateway();
}
