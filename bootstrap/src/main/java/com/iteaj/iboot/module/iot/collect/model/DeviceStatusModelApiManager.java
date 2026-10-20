package com.iteaj.iboot.module.iot.collect.model;

import cn.hutool.core.collection.CollectionUtil;
import com.iteaj.framework.spi.iot.IotCoreApiService;
import com.iteaj.framework.spi.iot.data.RealtimeDeviceStatus;
import com.iteaj.framework.spi.iot.listener.EntityPayload;
import com.iteaj.framework.spi.iot.listener.IotEvenPublisher;
import com.iteaj.framework.spi.iot.listener.IotEventType;
import com.iteaj.framework.spi.iot.listener.IotListener;
import com.iteaj.framework.thread.IBootThreadManger;
import com.iteaj.iboot.module.iot.cache.IotCacheManager;
import com.iteaj.iboot.module.iot.cache.entity.RealtimeStatus;
import com.iteaj.iboot.module.iot.consts.DeviceStatus;
import com.iteaj.iboot.module.iot.consts.DeviceType;
import com.iteaj.iboot.module.iot.consts.FuncStatus;
import com.iteaj.iboot.module.iot.entity.ModelApi;
import com.iteaj.iboot.module.iot.entity.Product;
import com.iteaj.iboot.module.iot.utils.IotLogger;
import com.iteaj.iboot.module.iot.utils.ProtocolInvokeUtil;
import com.iteaj.iot.consts.ExecStatus;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 物模型的设备在线状态协议管理
 * 一般用于网关子设备的状态采集
 */
public class DeviceStatusModelApiManager implements IotListener<Object> {

    @Autowired
    private IotCacheManager cacheManager;
    private static Map<Long, ModelApi> statusApis = new ConcurrentHashMap<>(16);

    private static DeviceStatusModelApiManager instance = new DeviceStatusModelApiManager();

    public static DeviceStatusModelApiManager getInstance() {
        return instance;
    }

    /**
     * 获取指定产品的状态api接口
     * @param productId 产品id
     */
    public static ModelApi get(Long productId) {
        return statusApis.get(productId);
    }

    /**
     * 注册api接口
     */
    public static void register(ModelApi api) {
        if(!statusApis.containsKey(api.getProductId())) {
            statusApis.put(api.getProductId(), api);
        }
    }

    /**
     * 更新接口
     */
    public static ModelApi update(ModelApi newModelApi) {
        if(!Boolean.TRUE.equals(newModelApi.getAsStatus())) {
            throw new IllegalArgumentException("必须是[asStatus == true]接口");
        }

        return statusApis.put(newModelApi.getProductId(), newModelApi);
    }

    /**
     * 移除接口
     */
    public static ModelApi remove(Long productId) {
        return statusApis.remove(productId);
    }

    /**
     * 获取所有的状态位接口
     */
    public Collection<ModelApi> apis() {
        return Collections.unmodifiableCollection(statusApis.values());
    }

     class GatewayDeviceStatusTask implements Runnable {

        private final RealtimeDeviceStatus parent;

        public GatewayDeviceStatusTask(RealtimeDeviceStatus parent) {
            this.parent = parent;
        }

        @Override
        public void run() {
            cacheManager.listForParent(parent.getProtocolCode(), parent.getDeviceSn()).forEach((key, child) -> {
                ModelApi modelApi = get(child.getProductId());
                if(modelApi != null && child.getStatus() == DeviceStatus.offline) {
                    doInvoke(modelApi, child, 3000L);
                }
            });
        }
    }

    private void doInvoke(ModelApi api, RealtimeStatus device, long timeout) {
        ProtocolInvokeUtil.invoke(api, device, timeout, result -> {
            if(result.getStatus() == ExecStatus.success) {
                // 如果现在的状态和上一次的状态不一致则更新状态
                if(DeviceStatus.online != device.getStatus()) {
                    device.setStatus(DeviceStatus.online);
                    IotEvenPublisher.publish(IotEventType.DeviceStatus, device);
                }
            } else {
                IotLogger.debug(IotLogger.NAME, "采集状态", device.getUid(), "采集状态失败 {} - 设备编号: {} - 接口: {}/{}"
                        , result.getStatus(), device.buildDeviceKey(), api.getCode(), api.getDirect());
            }
        });
    }

    @Override
    public boolean isEventMatcher(IotEventType eventType) {
        return eventType == IotEventType.DeviceStatus || eventType == IotEventType.ProductSwitch;
    }

    @Override
    public void onIotEvent(IotEventType eventType, Object value) {
        if(eventType == IotEventType.DeviceStatus) {
            RealtimeDeviceStatus device = (RealtimeDeviceStatus) value;
            // 有网关设备上线
            if(device.getType() == DeviceType.Gateway && device.getStatus() == DeviceStatus.online) {
                if(IBootThreadManger.isVirtualThreadSupported()) {
                    IBootThreadManger.execute(new GatewayDeviceStatusTask(device));
                } else {
                    IBootThreadManger.getInstance().getDeviceStatusScheduler().execute(new GatewayDeviceStatusTask(device));
                }
            }
        } else if(eventType == IotEventType.ProductSwitch) {
            EntityPayload<Product> entityPayload = (EntityPayload<Product>) value;
            Product payload = entityPayload.getPayload();
            if(payload.getStatus() == FuncStatus.enabled && payload.getDeviceType() == DeviceType.Child) {
                Collection<? extends RealtimeDeviceStatus> devices = IotCoreApiService.listByProductCode(payload.getCode());
                if(CollectionUtil.isNotEmpty(devices)) {
                    IBootThreadManger.getInstance().getDeviceStatusScheduler().execute(() -> {
                        devices.forEach(device -> {
                            if(device.getStatus() == DeviceStatus.offline) {
                                RealtimeDeviceStatus parent = IotCoreApiService.getDeviceStatus(device.getProtocolCode(), device.getParentDeviceSn());
                                if(parent != null && parent.getStatus() == DeviceStatus.online) {
                                    ModelApi modelApi = get(device.getProductId());
                                    if(modelApi != null) {
                                        doInvoke(modelApi, (RealtimeStatus) device, 3000L);
                                    }
                                }
                            }
                        });
                    });
                }
            }
        }
    }
}
