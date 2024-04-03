package com.iteaj.iboot.module.iot.utils;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSONObject;
import com.iteaj.framework.exception.ServiceException;
import com.iteaj.framework.spi.iot.DeviceProtocolSupplier;
import com.iteaj.framework.spi.iot.ProtocolModelApiInvokeParam;
import com.iteaj.framework.spi.iot.ProtocolSupplierManager;
import com.iteaj.framework.spi.iot.consts.CtrlMode;
import com.iteaj.framework.spi.iot.consts.PointProtocolConfig;
import com.iteaj.framework.spi.iot.protocol.AbstractProtocolModelApi;
import com.iteaj.framework.spi.iot.protocol.InvokeResult;
import com.iteaj.iboot.module.iot.cache.entity.RealtimeStatus;
import com.iteaj.iboot.module.iot.entity.ModelApi;
import com.iteaj.iboot.module.iot.entity.ModelApiConfig;

import java.util.Objects;
import java.util.function.Consumer;

public class ProtocolInvokeUtil {

    public static void invoke(ModelApi api, RealtimeStatus device, Consumer<InvokeResult> result) {
        invoke(api, device, 5000, result);
    }

    public static void invoke(ModelApi api, RealtimeStatus device, long timeout, Consumer<InvokeResult> result) {

        DeviceProtocolSupplier supplier = ProtocolSupplierManager.get(device.getProtocolCode());
        if(supplier != null) {
            AbstractProtocolModelApi modelApi = supplier.getProtocol().getApi(api.getDirect());
            if(modelApi != null) {
                if(CollectionUtil.isEmpty(modelApi.getDownConfig())) {
                    throw new ServiceException("协议对象["+modelApi.getCode()+"]异常, 没有下行配置");
                }

                JSONObject param = new JSONObject();
                ProtocolModelApiInvokeParam invokeParam = new ProtocolModelApiInvokeParam(device.getUid().toString(), param);
                invokeParam.setTimeout(timeout);

                api.getDownConfig().forEach(item -> {
                    if(item.getValue().startsWith("@") && item.getModelAttrId() != null) {
                        throw new ServiceException("协议参数使用@提及属性导致没有默认值");
                    }

                    param.put(item.getProtocolAttrField(), item.getValue());

                    // 如果是点位采集
                    if(supplier.getProtocol().getCtrlMode() == CtrlMode.POINT) {
                        if(Objects.equals(item.getProtocolAttrField(), PointProtocolConfig.POINT_ADDRESS)) {
                            for (int i = 0; i < api.getUpConfig().size(); i++) {
                                ModelApiConfig apiConfig = api.getUpConfig().get(i); // 获取此点位地址绑定的物模型属性
                                if(Objects.equals(apiConfig.getProtocolAttrField(), PointProtocolConfig.POINT_VALUE)) {
                                    if(apiConfig.getModelAttrId() != null && apiConfig.getAttrField() != null) {
                                        invokeParam.addConfig(PointProtocolConfig.POINT_FIELD_KEY + item.getValue(), apiConfig.getAttrField());
                                    } else {
                                        throw new IllegalArgumentException("上行配置[protocolAttrName="+apiConfig.getProtocolAttrName()+"]未指定物模型属性");
                                    }
                                }
                            }
                        }
                    }
                });

                // 组装调用参数
                invokeParam.setDeviceSn(device.getDeviceSn())
                        .setParentDeviceSn(device.getParentDeviceSn())
                        .addConfig("device", device);

                // 发起调用
                modelApi.invoke(invokeParam, result);
            } else {
                throw new ServiceException("没有找到协议指令[direct="+api.getDirect()+"]");
            }
        }
    }
}
