package com.iteaj.iboot.module.iot.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ArrayUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.iteaj.framework.BaseServiceImpl;
import com.iteaj.framework.ParamMeta;
import com.iteaj.framework.exception.ServiceException;
import com.iteaj.framework.result.BooleanResult;
import com.iteaj.framework.result.DetailResult;
import com.iteaj.framework.result.ListResult;
import com.iteaj.framework.result.PageResult;
import com.iteaj.framework.spi.iot.DeviceProtocolSupplier;
import com.iteaj.framework.spi.iot.ProtocolSupplierManager;
import com.iteaj.framework.spi.iot.consts.ConnectionType;
import com.iteaj.framework.spi.iot.data.BaseEchartsCount;
import com.iteaj.framework.spi.iot.data.EchartsCountValue;
import com.iteaj.iboot.module.iot.cache.IotCacheManager;
import com.iteaj.iboot.module.iot.cache.data.RealtimeDataService;
import com.iteaj.iboot.module.iot.cache.entity.RealtimeStatus;
import com.iteaj.iboot.module.iot.collect.CollectListenerManager;
import com.iteaj.iboot.module.iot.consts.DeviceStatus;
import com.iteaj.iboot.module.iot.consts.DeviceType;
import com.iteaj.iboot.module.iot.consts.FuncStatus;
import com.iteaj.iboot.module.iot.dto.*;
import com.iteaj.iboot.module.iot.entity.Device;
import com.iteaj.iboot.module.iot.mapper.DeviceMapper;
import com.iteaj.iboot.module.iot.service.IDeviceService;
import com.iteaj.iboot.module.iot.service.IProductService;
import com.iteaj.iboot.module.iot.utils.IotNetworkUtil;
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
    private final RealtimeDataService realtimeDataService;
    public DeviceServiceImpl(IProductService productService, IotCacheManager iotCacheManager
            , RealtimeDataService realtimeDataService) {
        this.productService = productService;
        this.iotCacheManager = iotCacheManager;
        this.realtimeDataService = realtimeDataService;
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
        validate(entity, null);
        entity.setUid(String.valueOf(IdWorker.getId()));
        return super.save(entity).ifPresentAndTrue(item -> {
            RealtimeStatus status = this.getDeviceStatusFromCache(entity.getId());
            iotCacheManager.cache(status);
        });
    }

    private void validate(Device entity, RealtimeStatus oldStatus) {
        productService.detailById(entity.getProductId()).ifNotPresentThrow("产品不存在").ifPresent(productDto -> {
            DeviceType deviceType = productDto.getDeviceType();
            // 网关子设备在同一网关设备下的设备编号必须唯一
            if(deviceType == DeviceType.Child) {
                this.getById(entity.getPid()).ifNotPresentThrow("网关子设备必须指定父网关设备").ifPresent(parent -> {
                    entity.setParentDeviceSn(parent.getDeviceSn());
                    if(oldStatus != null && (!parent.getDeviceSn().equals(oldStatus.getParentDeviceSn())
                            || !entity.getProductId().equals(oldStatus.getProductId()))) { // 切换了网关或者产品
                        entity.setStatus(DeviceStatus.offline); // 重置设备状态到离线
                    }

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

                // 客户端的网关设备如果产品启用则尝试连接网关
                // 交由客户端自己去连接(2024-06-24)
//                if(deviceType == DeviceType.Gateway) {
//                    DeviceProtocolSupplier supplier = ProtocolSupplierManager.get(productDto.getProtocolCode());
//                    if(entity.getConfig() == null) {
//                        if(supplier != null) {
//                            ParamMeta[] config = supplier.getGatewayConfig();
//                            if(ArrayUtil.isNotEmpty(config)) {
//                                throw new ServiceException("未设置网关配置项");
//                            }
//                        }
//
//                        entity.setConfig(new JSONObject()); // 空配置项
//                    }
//                    if(productDto.getStatus() == FuncStatus.enabled && productDto.getConnectType() == ConnectionType.Client) {
//                        if(supplier != null) {
//                            try {
//                                IotNetworkUtil.networkCtrl(supplier, entity, FuncStatus.enabled);
//                            } catch (ServiceException e) {
//                                throw e;
//                            } catch (Exception e) {
//                                // ...
//                            }
//                        }
//                    }
//                }
            }
        });
    }

    @Override
    @Transactional
    public BooleanResult updateById(Device entity) {
        RealtimeStatus oldCache = this.getDeviceStatusFromCache(entity.getId());
        if(oldCache == null) {
            return BooleanResult.buildFalse("设备不存在");
        }

        validate(entity, oldCache);

        return super.updateById(entity).ifPresentAndTrue(item -> {
            RealtimeStatus newCache = this.getDeviceStatusFromCache(entity.getId());
            iotCacheManager.update(oldCache, newCache);
                // 切换了网关或者切换了产品清除实时数据
                if(!oldCache.getProductId().equals(entity.getProductId()) || // 切换产品
                        (newCache.getType() == DeviceType.Child && !oldCache.getParentDeviceSn().equals(entity.getParentDeviceSn()))) { // 切换网关
                    try {
                        realtimeDataService.remove(oldCache.getProtocolCode(), oldCache.buildKey());
                    } catch (Exception e) {
                        //
                    }
                }
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
                CollectListenerManager.getInstance().deviceStatusPublish(listener -> listener.supplier(childDeviceStatus.values())); // 推送离线信息到前端
                this.iotCacheManager.updateChildStatus(device.getProtocolCode(), device.getDeviceSn(), DeviceStatus.offline);
            }
        }

        this.iotCacheManager.updateStatus(device.getProtocolCode(), device.buildDeviceKey(), device.getStatus());
        CollectListenerManager.getInstance().deviceStatusPublish(listener -> listener.supplier(device));
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
    public DeviceStatusDto countCommonDeviceStatus(GroupAndProductParamDto param) {
        return getBaseMapper().countDeviceStatus(param);
    }

    @Override
    public List<EMapDeviceDto> listOfEMap(Long productId, Long deviceGroupId) {
        return this.getBaseMapper().listOfEMap(productId, deviceGroupId);
    }

    @Override
    public List<EchartsCountValue> countCommonDeviceStatus(StatusCountCondition condition) {
        List<EchartsCountValue> values = new ArrayList<>();
        List<DeviceStatusDto> statusDtos = this.getBaseMapper().countCommonDeviceStatus(condition);
        if(CollectionUtil.isNotEmpty(condition.getProductCodes())) {
            List<BaseEchartsCount> online = new ArrayList<>();
            List<BaseEchartsCount> offline = new ArrayList<>();
            statusDtos.forEach(item -> {
                online.add(new BaseEchartsCount(item.getProductName(), item.getOnlineNum()));
                offline.add(new BaseEchartsCount(item.getProductName(), item.getOfflineNum()));
            });
            values.add(new EchartsCountValue(null, "在线", online));
            values.add(new EchartsCountValue(null, "离线", offline));
        } else {
            DeviceStatusDto statusDto = statusDtos.get(0);
            values.add(new EchartsCountValue(null, "在线",Arrays.asList(new BaseEchartsCount("", statusDto.getOnlineNum()))));
            values.add(new EchartsCountValue(null, "离线",Arrays.asList(new BaseEchartsCount("", statusDto.getOfflineNum()))));
        }
        return values;
    }

    @Override
    @Transactional
    public BooleanResult removeByIds(Collection<? extends Serializable> idList) {
        if(CollectionUtil.isEmpty(idList)) {
            return BooleanResult.buildFalse("未指定要移除的记录");
        }

        if(idList.size() > 1) {
            return BooleanResult.buildFalse("不支持批量删除");
        }

        List<RealtimeStatus> statusList = this.listDeviceStatusById(idList);
        if(CollectionUtil.isNotEmpty(statusList)) {
            return super.removeByIds(idList).ifPresentAndTrue(item -> {
                statusList.forEach(status -> {
                    if(status.getType() == DeviceType.Child) {
                        iotCacheManager.removeByKey(status.getProtocolCode(), status.getDeviceSn(), status.getParentDeviceSn());
                    } else {
                        if(status.getType() == DeviceType.Gateway) {
                            // 获取此网关设备下的所有网关子设备
                            Map<String, RealtimeStatus> fromCache = this.listChildDeviceStatusFromCache(status.getProtocolCode(), status.getDeviceSn());
                            if(CollectionUtil.isNotEmpty(fromCache)) {
                                List<String> uids = fromCache.values().stream()
                                        .map(device -> device.getUid())
                                        .collect(Collectors.toList());

                                // 从数据库移除子设备记录
                                this.remove(Wrappers.<Device>lambdaQuery().in(Device::getUid, uids));

                                // 从缓存移除所有的子设备
                                fromCache.forEach((key, value) -> {
                                    iotCacheManager.removeByKey(value.getProtocolCode(), value.getDeviceSn(), value.getParentDeviceSn());
                                });
                            }
                        }

                        IotNetworkUtil.removeClient(status);
                        iotCacheManager.removeByKey(status.getProtocolCode(), status.getDeviceSn());
                    }
                });
            });
        } else {
            return BooleanResult.buildTrue("移除成功");
        }
    }
}
