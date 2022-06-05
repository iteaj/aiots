package com.iteaj.iboot.msn.iot.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.iteaj.framework.result.Result;
import com.iteaj.iboot.msn.iot.dto.DeviceDto;
import com.iteaj.iboot.msn.iot.entity.Device;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

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
    IPage<Device> pageOfDetail(Page<Device> page, DeviceDto entity);
}
