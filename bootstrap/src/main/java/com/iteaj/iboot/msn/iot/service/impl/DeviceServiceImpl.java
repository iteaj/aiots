package com.iteaj.iboot.msn.iot.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.iteaj.framework.result.PageResult;
import com.iteaj.framework.result.Result;
import com.iteaj.iboot.msn.iot.dto.DeviceDto;
import com.iteaj.iboot.msn.iot.entity.Device;
import com.iteaj.iboot.msn.iot.mapper.DeviceMapper;
import com.iteaj.iboot.msn.iot.service.IDeviceService;
import com.iteaj.framework.BaseServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 设备表 服务实现类
 * </p>
 *
 * @author iteaj
 * @since 2022-05-15
 */
@Service
public class DeviceServiceImpl extends BaseServiceImpl<DeviceMapper, Device> implements IDeviceService {

    @Override
    public Result<IPage<Device>> pageOfDetail(Page<Device> page, DeviceDto entity) {
        return new PageResult<>(getBaseMapper().pageOfDetail(page, entity));
    }
}
