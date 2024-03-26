package com.iteaj.iboot.module.iot.collect.model;

import com.alibaba.fastjson.JSONObject;
import com.iteaj.framework.spi.iot.DeviceProtocolSupplier;
import com.iteaj.framework.spi.iot.ProtocolModelApiInvokeParam;
import com.iteaj.framework.spi.iot.ProtocolSupplierManager;
import com.iteaj.framework.spi.iot.protocol.AbstractProtocolModelApi;
import com.iteaj.iboot.module.iot.cache.IotCacheManager;
import com.iteaj.iboot.module.iot.entity.DeviceGroup;
import com.iteaj.iboot.module.iot.entity.ModelApi;

import java.util.List;

public class CollectGroupTask implements Runnable{

    private DeviceGroup group;
    private List<ModelApi> apis;
    private IotCacheManager cacheManager;

    public CollectGroupTask(DeviceGroup group
            , List<ModelApi> apis, IotCacheManager cacheManager) {
        this.group = group;
        this.apis = apis;
        this.cacheManager = cacheManager;
    }

    @Override
    public void run() {
        cacheManager.listByGroupId(group.getId()).forEach(device -> {
            apis.forEach(api -> {
                // 对此设备同一产品的模型接口进行调用
                if(api.getProductId().equals(device.getProductId())) {
                    DeviceProtocolSupplier supplier = ProtocolSupplierManager.get(device.getProtocolCode());
                    if(supplier != null) {
                        AbstractProtocolModelApi modelApi = supplier.getProtocol().getApi(api.getDirect());
                        if(modelApi != null) {
                            JSONObject param = new JSONObject();
                            api.getDownConfig().forEach(item -> {
                                param.put(item.getProtocolAttrField(), item.getValue());
                            });
                            ProtocolModelApiInvokeParam invokeParam = new ProtocolModelApiInvokeParam(device.getUid().toString(), param);
                            invokeParam.setDeviceSn(device.getDeviceSn())
                                    .setParentDeviceSn(device.getDeviceSn())
                                    .addConfig("device", device);
                            modelApi.invoke(invokeParam, result -> {

                            });
                        }
                    }
                }
            });
        });
    }
}
