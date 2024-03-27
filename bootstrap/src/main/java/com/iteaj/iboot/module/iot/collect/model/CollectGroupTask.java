package com.iteaj.iboot.module.iot.collect.model;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSONObject;
import com.iteaj.framework.spi.iot.*;
import com.iteaj.framework.spi.iot.consts.CtrlMode;
import com.iteaj.framework.spi.iot.consts.PointProtocolConfig;
import com.iteaj.framework.spi.iot.protocol.AbstractProtocolModelApi;
import com.iteaj.iboot.module.iot.cache.IotCacheManager;
import com.iteaj.iboot.module.iot.cache.entity.RealtimeStatus;
import com.iteaj.iboot.module.iot.dto.EventSourceDto;
import com.iteaj.iboot.module.iot.entity.DeviceGroup;
import com.iteaj.iboot.module.iot.entity.EventSource;
import com.iteaj.iboot.module.iot.entity.ModelApi;
import com.iteaj.iot.consts.ExecStatus;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

public class CollectGroupTask implements Runnable{

    private DeviceGroup group;
    private List<ModelApi> apis;
    private EventSourceDto source;
    private IotCacheManager cacheManager;

    public CollectGroupTask(DeviceGroup group, EventSourceDto source, IotCacheManager cacheManager) {
        this.group = group;
        this.source = source;
        this.apis = source.getApis();
        this.cacheManager = cacheManager;
    }

    @Override
    public void run() {
        CollectInfo info = new CollectInfo(source, group);
        Collection<RealtimeStatus> devices = cacheManager.listByGroupId(group.getId());
        devices.forEach(device -> {
            apis.forEach(api -> {
                try {
                    // 对此设备同一产品的模型接口进行调用
                    if(api.getProductId().equals(device.getProductId())) {
                        DeviceProtocolSupplier supplier = ProtocolSupplierManager.get(device.getProtocolCode());
                        if(supplier != null) {
                            AbstractProtocolModelApi modelApi = supplier.getProtocol().getApi(api.getDirect());
                            if(modelApi != null) {
                                JSONObject param = new JSONObject();
                                ProtocolModelApiInvokeParam invokeParam = new ProtocolModelApiInvokeParam(device.getUid().toString(), param);

                                api.getDownConfig().forEach(item -> {
                                    if(item.getValue().startsWith("@") && item.getModelAttrId() != null) {
                                        throw new IllegalArgumentException("协议参数使用@提及属性导致没有默认值");
                                    }

                                    param.put(item.getProtocolAttrField(), item.getValue());

                                    // 如果是点位采集
                                    if(supplier.getProtocol().getCtrlMode() == CtrlMode.POINT) {
                                        if(Objects.equals(item.getProtocolAttrField(), PointProtocolConfig.POINT_ADDRESS)) {
                                            invokeParam.addConfig(PointProtocolConfig.POINT_FIELD_KEY + item.getValue(), item.getAttrField());
                                        }
                                    }
                                });

                                // 组装调用参数
                                invokeParam.setDeviceSn(device.getDeviceSn())
                                        .setParentDeviceSn(device.getParentDeviceSn())
                                        .addConfig("device", device);

                                // 发起调用
                                modelApi.invoke(invokeParam, result -> {
                                    if(result.getStatus() == ExecStatus.success) {
                                        ProtocolSupplierManager.getDataSupplier(result.getProtocol()).ifPresent(data -> {
                                            info.addDeviceInfo(device, api, data);
                                        });
                                    } else {

                                    }
                                });
                            } else {
                                throw new IllegalArgumentException("没有找到协议指令[direct="+api.getDirect()+"]");
                            }
                        }
                    }

                    EventGroupCollectManager.getInstance().forEach(listener -> {
                        listener.listener(info);
                    });
                } catch (ProtocolInvokeException e) {
                    e.printStackTrace();
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        });
    }
}
