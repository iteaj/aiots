package com.iteaj.iboot.module.iot.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.iteaj.framework.exception.ServiceException;
import com.iteaj.framework.result.*;
import com.iteaj.iboot.module.iot.consts.DeviceType;
import com.iteaj.iboot.module.iot.dto.CurrentDeviceDto;
import com.iteaj.iboot.module.iot.dto.DeviceDto;
import com.iteaj.iboot.module.iot.consts.DeviceStatus;
import com.iteaj.iboot.module.iot.entity.Device;
import com.iteaj.iboot.module.iot.mapper.DeviceMapper;
import com.iteaj.iboot.module.iot.service.IDeviceService;
import com.iteaj.framework.BaseServiceImpl;
import com.iteaj.iboot.module.iot.service.IProductService;
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

    private final IProductService productService;

    public DeviceServiceImpl(IProductService productService) {
        this.productService = productService;
    }

    @Override
    public Result<IPage<DeviceDto>> pageOfDetail(Page<Device> page, DeviceDto entity) {
        return new PageResult<>(getBaseMapper().pageOfDetail(page, entity));
    }

    @Override
    public BooleanResult save(Device entity) {
        validate(entity);
        entity.setUid(IdWorker.getId());
        return super.save(entity);
    }

    private void validate(Device entity) {
        productService.detailById(entity.getProductId()).ifNotPresentThrow("产品不存在").ifPresent(productDto -> {
            DeviceType deviceType = productDto.getDeviceType();
            // 网关子设备在同一网关设备下的设备编号必须唯一
            if(deviceType == DeviceType.Child) {
                this.getById(entity.getPid()).ifNotPresentThrow("网关子设备必须指定父网关设备").ifPresent(parent -> {
                    this.getByDeviceSn(entity.getDeviceSn(), productDto.getProtocolCode(), parent.getDeviceSn())
                            .ifPresent(item -> {
                                if(entity.getId() == null) { // 新增
                                    throw new ServiceException("网关设备["+parent.getDeviceSn()+"]已包含子设备编号["+ entity.getDeviceSn()+"]");
                                } else if(item.getId() != entity.getId()) { // 更新
                                    throw new ServiceException("网关设备["+parent.getDeviceSn()+"]已包含子设备编号["+ entity.getDeviceSn()+"]");
                                }
                            });
                });
            } else { // 边缘网关设备和直连设备在同一网络组件(协议)下设备编号必须唯一
                this.getByDeviceSn(entity.getDeviceSn(), productDto.getProtocolCode(), null)
                        .ifPresent(item -> {
                            if(entity.getId() == null) { // 新增
                                throw new ServiceException("网络组件["+productDto.getProtocolCode()+"]已包含设备编号["+ entity.getDeviceSn()+"]");
                            } else if(item.getId() != entity.getId()) { // 更新
                                throw new ServiceException("网络组件["+productDto.getProtocolCode()+"]已包含设备编号["+ entity.getDeviceSn()+"]");
                            }
                        });
            }
        });
    }

    @Override
    public BooleanResult updateById(Device entity) {
        validate(entity);
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
    public ListResult<Device> listOfGateway(Long gatewayId) {
        return new ListResult<>(this.getBaseMapper().listOfGateway(gatewayId));
    }

    @Override
    public ListResult<DeviceDto> listByProductId(Long productId) {
        return new ListResult<>(this.getBaseMapper().listByProductId(productId));
    }

    @Override
    public DetailResult<Device> getByDeviceSn(String deviceSn, String protocolCode, String gatewayDeviceSn) {
        return new DetailResult<>(getBaseMapper().getByDeviceSn(deviceSn, protocolCode, gatewayDeviceSn));
    }
}
