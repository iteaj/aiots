package com.iteaj.iboot.module.iot.listener;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.iteaj.framework.spi.event.ApplicationReadyListener;
import com.iteaj.framework.spi.iot.ClientProtocolSupplier;
import com.iteaj.framework.spi.iot.DeviceProtocolSupplier;
import com.iteaj.framework.spi.iot.ProtocolSupplierManager;
import com.iteaj.framework.spi.iot.SignalOrFieldValue;
import com.iteaj.framework.spi.iot.listener.EntityPayload;
import com.iteaj.framework.spi.iot.listener.IotEvenPublisher;
import com.iteaj.framework.spi.iot.listener.IotEventType;
import com.iteaj.framework.spi.iot.listener.IotListener;
import com.iteaj.framework.thread.IBootThreadManger;
import com.iteaj.iboot.module.iot.cache.IotCacheManager;
import com.iteaj.iboot.module.iot.cache.data.HashMapRealtimeDataService;
import com.iteaj.iboot.module.iot.cache.data.RealtimeDataService;
import com.iteaj.iboot.module.iot.cache.entity.RealtimeStatus;
import com.iteaj.iboot.module.iot.collect.model.DeviceStatusModelApiManager;
import com.iteaj.iboot.module.iot.consts.DeviceType;
import com.iteaj.iboot.module.iot.consts.FuncStatus;
import com.iteaj.iboot.module.iot.consts.GatewayStatus;
import com.iteaj.iboot.module.iot.entity.DeviceRealtimeCache;
import com.iteaj.iboot.module.iot.entity.Gateway;
import com.iteaj.iboot.module.iot.entity.Product;
import com.iteaj.iboot.module.iot.service.*;
import com.iteaj.iboot.module.iot.utils.IotNetworkUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * iot模块监听应用重启时的初始化
 */
@Component
public class IotRestartListener implements ApplicationReadyListener {

    private final IGatewayService gatewayService;
    private final IDeviceService deviceService;
    private final IProductService productService;
    private final IotCacheManager iotCacheManager;
    private final IModelApiService modelApiService;
    private final RealtimeDataService realtimeDataService;
    private final IDeviceRealtimeCacheService deviceRealtimeCacheService;
    private Logger logger = LoggerFactory.getLogger(getClass());

    public IotRestartListener(IGatewayService gatewayService
            , IDeviceService deviceService, IProductService productService
            , IotCacheManager iotCacheManager, @Autowired(required = false) List<IotListener<?>> list
            , IModelApiService modelApiService, RealtimeDataService realtimeDataService, IDeviceRealtimeCacheService deviceRealtimeCacheService) {
        this.gatewayService = gatewayService;
        this.deviceService = deviceService;
        this.productService = productService;
        this.iotCacheManager = iotCacheManager;
        this.modelApiService = modelApiService;
        this.realtimeDataService = realtimeDataService;
        this.deviceRealtimeCacheService = deviceRealtimeCacheService;

        // 注册事件监听器
        if(list != null) {
            list.forEach(IotEvenPublisher::register);
        }
    }

    @Override
    public void started(ApplicationContext context) {
        // 缓存设备信息
        cacheDevice();

        // 加载数据缓存
        loadingDataCache();

        // 初始化网关信息
        initGateway();

        // 初始化产品信息
        initProduct();

    }

    private void loadingDataCache() {
        if(realtimeDataService instanceof HashMapRealtimeDataService) {
            IBootThreadManger.execute(() -> {
                Page<DeviceRealtimeCache> page = new Page<>(1, 1000);
                do {
                    deviceRealtimeCacheService.page(page).ifPresent(result -> {
                        if(CollectionUtil.isNotEmpty(result.getRecords())) {
                            result.getRecords().stream().collect(Collectors.groupingBy(DeviceRealtimeCache::getUid))
                                    .forEach((uid, caches) -> {
                                        RealtimeStatus device = iotCacheManager.getByUid(uid);
                                        if(device != null) {
                                            caches.forEach(cache -> {
                                                realtimeDataService.put(device.getProtocolCode(), device.buildKey()
                                                        , new SignalOrFieldValue(cache.getFieldId(), cache.getField(), cache.getCollectTime(), cache.getValue()));
                                            });
                                        }
                                    });
                        }
                    });
                }
                while (page.hasNext());
            });
        }
    }

    private void cacheDevice() {
        this.deviceService.listDeviceStatus().forEach(iotCacheManager::cache);
    }

    private void initGateway() {
        // 启用所有已经存在的网关
        this.gatewayService.list(Wrappers.<Gateway>lambdaQuery().eq(Gateway::getStatus, GatewayStatus.start)).forEach(item -> {
            try {
                this.gatewayService.switchStatus(item.getId(), GatewayStatus.start);
            } catch (Exception e) {
                logger.error("<<< 启动网关[{}]失败", item.getName(), e);
                this.gatewayService.update(Wrappers.<Gateway>lambdaUpdate()
                        .eq(Gateway::getId, item.getId())
                        .set(Gateway::getReason, e.getMessage()));
            }
        });
    }

    protected void initProduct() {
        // 注册网关子设备的状态采集器
        this.modelApiService.listAsStatusModelApi().forEach(DeviceStatusModelApiManager::register);

        // 启动所有已经启用的设备
        productService.listOfDetail(new Product().setStatus(FuncStatus.enabled)).forEach(item -> {
            if(item.getGatewayStatus() == GatewayStatus.stop) {
                logger.warn("<<< 启动产品[{}]失败, 网关[{}]未启用", item.getName(), item.getGatewayName());
                return;
            }

            if(item.getDeviceType() == DeviceType.Gateway || item.getDeviceType() == DeviceType.Direct) { // 启用边缘网关和直连的所有的客户端设备
                DeviceProtocolSupplier<?, ?> supplier = ProtocolSupplierManager.get(item.getProtocolCode());
                if(supplier == null) {
                    logger.warn("<<< 启动产品[{}]失败, 未提供协议[code={}]", item.getName(), item.getProtocolCode());
                    return; // 保存产品启动信息
                }

                if(supplier instanceof ClientProtocolSupplier) {
                    IBootThreadManger.getInstance().getDeviceStatusScheduler().execute(() -> {
                        // 先启动网关后才能启动产品
                        this.deviceService.listByProductId(item.getId()).forEach(device -> {
                            try {
                                IotNetworkUtil.networkCtrl(supplier, device, FuncStatus.enabled);
                            } catch (Exception e) {
                                logger.error("<<< 启动产品[{}]下的网关设备[{}]失败", item.getName(), device.getName(), e);
                            }
                        });
                    });
                }
            }

            // 发布产品启用事件
            IotEvenPublisher.publish(IotEventType.ProductSwitch, new EntityPayload(item));
        });
    }

    /**
     * @return
     */
    @Override
    public int getOrder() {
        return Integer.MIN_VALUE + 10000;
    }
}
