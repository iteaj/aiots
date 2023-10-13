package com.iteaj.iboot.module.iot.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.iteaj.framework.result.*;
import com.iteaj.iboot.module.iot.dto.CurrentDeviceDto;
import com.iteaj.iboot.module.iot.dto.DeviceDto;
import com.iteaj.iboot.module.iot.consts.DeviceStatus;
import com.iteaj.iboot.module.iot.entity.Device;
import com.iteaj.iboot.module.iot.mapper.DeviceMapper;
import com.iteaj.iboot.module.iot.service.IDeviceService;
import com.iteaj.framework.BaseServiceImpl;
import org.springframework.stereotype.Service;

import java.util.Date;

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
    public Result<IPage<DeviceDto>> pageOfDetail(Page<Device> page, DeviceDto entity) {
        return new PageResult<>(getBaseMapper().pageOfDetail(page, entity));
    }

    @Override
    public BooleanResult save(Device entity) {
        Device device = this.getByDeviceSn(entity.getDeviceSn()).getData();
        if(device != null) {
            return new BooleanResult(false).fail("设备编号已经存在["+entity.getDeviceSn()+"]");
        }

        entity.setUid(IdWorker.getId());
        return super.save(entity);
    }

    @Override
    public BooleanResult updateById(Device entity) {
        Device device = this.getOne(Wrappers.<Device>lambdaQuery()
                .eq(Device::getDeviceSn, entity.getDeviceSn())
                .ne(Device::getId, entity.getId())).getData();
        if(device != null) {
            return new BooleanResult(false).fail("设备编号已经存在["+entity.getDeviceSn()+"]");
        }
        return super.updateById(entity);
    }

    @Override
    public void update(String deviceSn, DeviceStatus status) {
        update(Wrappers.<Device>lambdaUpdate()
                .set(Device::getStatus, status)
                .set(Device::getSwitchTime, new Date())
                .eq(Device::getDeviceSn, deviceSn));
    }

    @Override
    public DetailResult<Device> getByDeviceSn(String deviceSn) {
        return getOne(Wrappers.<Device>lambdaQuery().eq(Device::getDeviceSn, deviceSn));
    }

    @Override
    public CurrentDeviceDto countCurrentDevice() {
        return getBaseMapper().countCurrentDevice();
    }

    @Override
    public DetailResult<DeviceDto> detailById(Long id) {
        return new DetailResult<>(this.getBaseMapper().detailById(id));
    }

    @Override
    public ListResult<Device> listOfGateway() {
        return new ListResult<>(this.getBaseMapper().listOfGateway());
    }
}
