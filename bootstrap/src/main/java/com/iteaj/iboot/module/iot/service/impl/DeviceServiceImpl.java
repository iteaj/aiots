package com.iteaj.iboot.module.iot.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.iteaj.framework.exception.ServiceException;
import com.iteaj.framework.result.*;
import com.iteaj.iboot.module.iot.cache.IotCacheManager;
import com.iteaj.iboot.module.iot.cache.entity.RealtimeStatus;
import com.iteaj.iboot.module.iot.collect.websocket.RealtimePushListener;
import com.iteaj.iboot.module.iot.consts.DeviceType;
import com.iteaj.iboot.module.iot.dto.CurrentDeviceDto;
import com.iteaj.iboot.module.iot.dto.DeviceDto;
import com.iteaj.iboot.module.iot.consts.DeviceStatus;
import com.iteaj.iboot.module.iot.dto.DeviceStatusDto;
import com.iteaj.iboot.module.iot.dto.GroupAndProductParamDto;
import com.iteaj.iboot.module.iot.entity.Device;
import com.iteaj.iboot.module.iot.linkage.LinkageTriggerService;
import com.iteaj.iboot.module.iot.mapper.DeviceMapper;
import com.iteaj.iboot.module.iot.service.IDeviceService;
import com.iteaj.framework.BaseServiceImpl;
import com.iteaj.iboot.module.iot.service.IProductService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

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
    private final IotCacheManager iotCacheManager;
    private final RealtimePushListener pushListener;
    private final LinkageTriggerService linkageTriggerService;
    public DeviceServiceImpl(IProductService productService, IotCacheManager iotCacheManager
            , RealtimePushListener pushListener, LinkageTriggerService linkageTriggerService) {
        this.productService = productService;
        this.iotCacheManager = iotCacheManager;
        this.pushListener = pushListener;
        this.linkageTriggerService = linkageTriggerService;
    }


    @Override
    public RealtimeStatus getDeviceStatusFromCache(String protocolCode, String deviceSn) {
        RealtimeStatus realtimeStatus = iotCacheManager.get(protocolCode, deviceSn);
        if(realtimeStatus == null) {
            realtimeStatus = this.getDeviceStatus(protocolCode, deviceSn, null);
            if(realtimeStatus != null) {
                iotCacheManager.cache(realtimeStatus);
            }
        }

        return realtimeStatus;
    }

    @Override
    public RealtimeStatus getDeviceStatusFromCache(String protocolCode, String deviceSn, String parentDeviceSn) {
        RealtimeStatus realtimeStatus = iotCacheManager.get(protocolCode, deviceSn, parentDeviceSn);
        if(realtimeStatus == null) {
            realtimeStatus = this.getDeviceStatus(protocolCode, deviceSn, parentDeviceSn);
            if(realtimeStatus != null) {
                iotCacheManager.cache(realtimeStatus);
            }
        }

        return realtimeStatus;
    }

    @Override
    public Map<String, RealtimeStatus> listDeviceStatusFromCache(String protocolCode, String... deviceSn) {
        Map<String, RealtimeStatus> statusMap = iotCacheManager.list(protocolCode, deviceSn);
        if(CollectionUtil.isEmpty(statusMap)) {
            List<RealtimeStatus> statuses = this.getBaseMapper().listDeviceStatus(protocolCode, Arrays.asList(deviceSn), null);
            if(CollectionUtil.isNotEmpty(statuses)) {
                statuses.forEach(status -> {
                    iotCacheManager.cache(status);
                    statusMap.put(status.getDeviceSn(), status);
                });
            }
        }
        return statusMap;
    }

    @Override
    public Map<String, RealtimeStatus> listChildDeviceStatusFromCache(String protocolCode, String parentDeviceSn, String... deviceSn) {
        Map<String, RealtimeStatus> statusMap = iotCacheManager.listForParent(protocolCode, parentDeviceSn, deviceSn);
        if(CollectionUtil.isEmpty(statusMap)) {
            List<RealtimeStatus> statuses = this.getBaseMapper().listDeviceStatus(protocolCode, Arrays.asList(deviceSn), parentDeviceSn);
            if(CollectionUtil.isNotEmpty(statuses)) {
                statuses.forEach(status -> {
                    iotCacheManager.cache(status);
                    statusMap.put(status.getDeviceSn(), status);
                });
            }
        }
        return statusMap;
    }

    @Override
    public PageResult<IPage<DeviceDto>> pageOfDetail(Page<Device> page, DeviceDto entity) {
        return new PageResult<>(getBaseMapper().pageOfDetail(page, entity));
    }

    @Override
    @Transactional
    public BooleanResult save(Device entity) {
        validate(entity);
        entity.setUid(String.valueOf(IdWorker.getId()));
        return super.save(entity).ifPresentAndTrue(item -> {
            RealtimeStatus status = this.getDeviceStatusFromCache(entity.getId());
            iotCacheManager.cache(status);
        });
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
                                } else if(!item.getId().equals(entity.getId())) { // 更新
                                    throw new ServiceException("网关设备["+parent.getDeviceSn()+"]已包含子设备编号["+ entity.getDeviceSn()+"]");
                                }
                            });
                });
            } else { // 边缘网关设备和直连设备在同一网络组件(协议)下设备编号必须唯一
                this.getByDeviceSn(entity.getDeviceSn(), productDto.getProtocolCode(), null)
                        .ifPresent(item -> {
                            if(entity.getId() == null) { // 新增
                                throw new ServiceException("网络组件["+productDto.getProtocolCode()+"]已包含设备编号["+ entity.getDeviceSn()+"]");
                            } else if(!item.getId().equals(entity.getId())) { // 更新
                                throw new ServiceException("网络组件["+productDto.getProtocolCode()+"]已包含设备编号["+ entity.getDeviceSn()+"]");
                            }
                        });
            }
        });
    }

    @Override
    @Transactional
    public BooleanResult updateById(Device entity) {
        validate(entity);
        RealtimeStatus oldCache = this.getDeviceStatusFromCache(entity.getId());
        return super.updateById(entity).ifPresentAndTrue(item -> {
            RealtimeStatus newCache = this.getDeviceStatusFromCache(entity.getId());
            iotCacheManager.update(oldCache, newCache);
        });
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

    @Override
    public RealtimeStatus getDeviceStatusFromCache(Long deviceId) {
        return this.getBaseMapper().getDeviceCacheById(deviceId);
    }

    @Override
    public List<RealtimeStatus> listDeviceStatusById(Collection<?> idList) {
        if(CollectionUtil.isEmpty(idList)) {
            return Collections.EMPTY_LIST;
        }

        return this.getBaseMapper().listDeviceStatusById(idList);
    }

    @Override
    public ListResult<RealtimeStatus> listDeviceStatus() {
        return new ListResult<>(this.getBaseMapper().listDeviceCache());
    }

    @Override
    @Transactional
    public void updateDeviceStatus(RealtimeStatus device) {
        Date switchTime = new Date();
        this.update(Wrappers.<Device>lambdaUpdate()
                .set(Device::getStatus, device.getStatus())
                .set(Device::getSwitchTime, switchTime)
                .eq(Device::getUid, device.getUid()));

        // 如果是边缘网关下线则所有的网关子设备也必须下线
        if(device.getType() == DeviceType.Gateway && device.getStatus() == DeviceStatus.offline) {
            Map<String, RealtimeStatus> childDeviceStatus = this.listChildDeviceStatusFromCache(device.getProtocolCode(), device.getDeviceSn());
            if(CollectionUtil.isNotEmpty(childDeviceStatus)) {
                Collection<RealtimeStatus> values = childDeviceStatus.values();
                List<String> uidList = values.stream().map(item -> item.getUid()).collect(Collectors.toList());
                this.update(Wrappers.<Device>lambdaUpdate()
                        .set(Device::getSwitchTime, switchTime)
                        .set(Device::getStatus, DeviceStatus.offline)
                        .in(Device::getUid, uidList));

                values.forEach(item -> item.setStatus(DeviceStatus.offline));
                pushListener.push(values); // 推送离线信息到前端
                linkageTriggerService.trigger(values);
                this.iotCacheManager.updateChildStatus(device.getProtocolCode(), device.getDeviceSn(), DeviceStatus.offline);
            }
        }

        this.iotCacheManager.updateStatus(device.getProtocolCode(), device.buildDeviceKey(), device.getStatus());
        pushListener.push(device);
        linkageTriggerService.trigger(device);
    }

    @Override
    public RealtimeStatus getDeviceStatus(String protocolCode, String deviceSn, String parentDeviceSn) {
        return this.getBaseMapper().getDeviceStatus(protocolCode, deviceSn, parentDeviceSn);
    }

    @Override
    public DetailResult<Device> getByUid(String uid) {
        return new DetailResult<>(this.getBaseMapper().getByUid(uid));
    }

    @Override
    public DeviceStatusDto countDeviceStatus(GroupAndProductParamDto param) {
        return getBaseMapper().countDeviceStatus(param);
    }

    @Override
    @Transactional
    public BooleanResult removeByIds(Collection<? extends Serializable> idList) {
        if(CollectionUtil.isEmpty(idList)) {
            return BooleanResult.buildFalse("未指定要移除的记录");
        }

        List<RealtimeStatus> statusList = this.listDeviceStatusById(idList);
        return super.removeByIds(idList).ifPresentAndTrue(item -> {
            statusList.forEach(status -> {
                if(status.getType() == DeviceType.Child) {
                    iotCacheManager.removeByKey(status.getProtocolCode(), status.getDeviceSn(), status.getParentDeviceSn());
                }
            });
        });
    }
}
