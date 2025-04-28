package com.iteaj.iboot.module.iot.utils;

import cn.hutool.core.collection.CollectionUtil;
import com.iteaj.framework.exception.ServiceException;
import com.iteaj.framework.script.*;
import com.iteaj.framework.spi.iot.DeviceProtocolSupplier;
import com.iteaj.framework.spi.iot.ProtocolModelApiInvokeParam;
import com.iteaj.framework.spi.iot.ProtocolSupplierManager;
import com.iteaj.framework.spi.iot.UpModelAttr;
import com.iteaj.framework.spi.iot.consts.DataType;
import com.iteaj.framework.spi.iot.consts.TriggerMode;
import com.iteaj.framework.spi.iot.protocol.AbstractProtocolModelApi;
import com.iteaj.framework.spi.iot.protocol.InvokeResult;
import com.iteaj.framework.spring.SpringUtils;
import com.iteaj.iboot.module.iot.cache.IotCacheManager;
import com.iteaj.iboot.module.iot.cache.entity.RealtimeStatus;
import com.iteaj.iboot.module.iot.consts.DeviceType;
import com.iteaj.iboot.module.iot.entity.ModelApi;
import com.iteaj.iboot.module.iot.entity.ModelApiConfig;
import com.iteaj.framework.script.ScriptCmdMethodsProvider;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public class ProtocolInvokeUtil {

    private static IotCacheManager cacheManager;

    public static void invoke(ModelApi api, RealtimeStatus device, Consumer<InvokeResult> result) {
        invoke(api, device, ProtocolModelApiInvokeParam.DEFAULT_TIMEOUT, result);
    }

    public static void invoke(ModelApi api, ProtocolModelApiInvokeParam param, RealtimeStatus device, Consumer<InvokeResult> result) {
        invoke(ProtocolSupplierManager.get(device.getProtocolCode()), api, param, device, result);
    }

    public static void invoke(DeviceProtocolSupplier supplier, ModelApi api, ProtocolModelApiInvokeParam param, RealtimeStatus device, Consumer<InvokeResult> result) {
        if(supplier != null) {
            AbstractProtocolModelApi modelApi = supplier.getProtocol().getApi(api.getDirect());
            if(modelApi != null) {
                if(modelApi.getTriggerMode() == TriggerMode.active) {
                    throw new ServiceException("设备主动触发的协议不允许调用");
                }

                // 组装调用参数
                initInvokeConfig(param, device, api);

                if(CollectionUtil.isNotEmpty(api.getUpConfig())) {
                    buildUpConfigAttrs(api, param);
                }

                if(CollectionUtil.isNotEmpty(api.getDownConfig())) {
                    api.getDownConfig().forEach(item -> buildDownConfig(device, api, param, item));
                }

                // 发起调用
                modelApi.invoke(param, result);
            } else {
                throw new ServiceException("没有找到协议指令[direct="+api.getDirect()+"]");
            }
        }
    }

    private static void buildDownConfig(RealtimeStatus device, ModelApi api, ProtocolModelApiInvokeParam param, ModelApiConfig item) {
        final String attrValue = param.getString(item.getProtocolAttrField());
        if(attrValue == null) { // 已经包含了要写的值
            final String trimValue = item.getValue().trim();
            if(trimValue.startsWith("@") && item.getModelAttrId() != null) {
                if(item.getAttrDefaultValue() == null) {
                    throw new ServiceException("协议/字段["+api.getName()+" / "+item.getAttrField()+"]没有默认值");
                }

                param.addParam(item.getProtocolAttrField(), item.getAttrDefaultValue());
            } else if(trimValue.startsWith("#")) { // 功能脚本引用
                param.addConfig("protocolAttrField", item.getProtocolAttrField());
                invokeScript(param, trimValue.substring(1));
            } else {
                param.addParam(item.getProtocolAttrField(), item.getValue());
            }
        } else if(attrValue.startsWith("#script:")) { // 功能脚本引用
            param.addConfig("protocolAttrField", item.getProtocolAttrField());
            invokeScript(param, attrValue.substring(1));
        }
    }

    @Nullable
    public static void invokeScript(ProtocolModelApiInvokeParam param, String scriptName) {
        final FunctionScript script = ScriptConfigService.instance.getScript(scriptName);

        final ScriptConfig scriptConfig = ScriptConfigService.instance.getScriptConfig(script);
        if(scriptConfig == null) {
            throw new ServiceException("未找到脚本实现[" + scriptName + "->" + script.getLanguage() + "]");
        }

        ScriptManager.exec(script.getLanguage(), ScriptContext.build(script.getKey(), scriptConfig.getScript(), scriptConfig.getInvoke())
                .addParam("data", param).addParam("methods", ScriptCmdMethodsProvider.INSTANCE));

    }

    private static void initInvokeConfig(ProtocolModelApiInvokeParam param, RealtimeStatus device, ModelApi api) {
        param.addDevice(device).setDeviceSn(device.getDeviceSn())
                .setParentDeviceSn(device.getParentDeviceSn())
                .addDeviceConfig(device.getConfig()).addApiCode(api.getCode());

        // 设置默认超时时间
        if(param.getTimeout() == null || param.getTimeout() <= 0) {
            param.setTimeout(ProtocolModelApiInvokeParam.DEFAULT_TIMEOUT);
        }

        if(device.getType() == DeviceType.Child) {
            IotCacheManager cacheManager = getCacheManager();
            RealtimeStatus parent = cacheManager.get(device.getProtocolCode(), device.getParentDeviceSn());
            if(parent != null) {
                param.addParentDeviceConfig(parent.getConfig());
            }
        }
    }

    /**
     * 创建调用参数
     * @param device 设备
     * @param parent 父设备(非必填)
     * @return
     */
    public static ProtocolModelApiInvokeParam createModelApiInvokeParam(RealtimeStatus device, RealtimeStatus parent) {
        final ProtocolModelApiInvokeParam apiInvokeParam = new ProtocolModelApiInvokeParam(device.getUid());
        apiInvokeParam.setDeviceSn(device.getDeviceSn())
                .setParentDeviceSn(device.getParentDeviceSn())
                .addDeviceConfig(device.getConfig());

        // 设置默认超时时间
        if(apiInvokeParam.getTimeout() == null || apiInvokeParam.getTimeout() <= 0) {
            apiInvokeParam.setTimeout(ProtocolModelApiInvokeParam.DEFAULT_TIMEOUT);
        }

        if(parent != null) {
            apiInvokeParam.addParentDeviceConfig(parent.getConfig());
        }

        return apiInvokeParam;
    }

    private static IotCacheManager getCacheManager() {
        return cacheManager != null ? cacheManager : (cacheManager = SpringUtils.getBean(IotCacheManager.class));
    }

    public static void invoke(ModelApi api, RealtimeStatus device, long timeout, Consumer<InvokeResult> result) {
        DeviceProtocolSupplier supplier = ProtocolSupplierManager.get(device.getProtocolCode());
        if(supplier != null) {
            AbstractProtocolModelApi modelApi = supplier.getProtocol().getApi(api.getDirect());
            if(modelApi != null) {
                if(modelApi.getTriggerMode() == TriggerMode.active) {
                    throw new ServiceException("设备主动触发的协议不允许调用");
                }

                ProtocolModelApiInvokeParam invokeParam = new ProtocolModelApiInvokeParam(device.getUid());
                invokeParam.setTimeout(timeout);

                // 组装调用参数
                initInvokeConfig(invokeParam, device, api);

                if(CollectionUtil.isNotEmpty(api.getUpConfig())) {
                    buildUpConfigAttrs(api, invokeParam);
                }

                if(CollectionUtil.isNotEmpty(api.getDownConfig())) {
                    api.getDownConfig().forEach(item -> buildDownConfig(device, api, invokeParam, item));
                } else {
                    // 允许没有下行配置的接口
                }

                // 发起调用
                modelApi.invoke(invokeParam, result);
            } else {
                throw new ServiceException("没有找到协议指令[direct="+api.getDirect()+"]");
            }
        }
    }

    private static void buildUpConfigAttrs(ModelApi api, ProtocolModelApiInvokeParam invokeParam) {
        List<UpModelAttr> upConfigAttrs = new ArrayList<>();
        api.getUpConfig().forEach(item -> {
            if(DataType.t_json.getValue().equals(item.getDataType())) {
                item.getDicts().forEach(dict -> {
                    upConfigAttrs.add(new UpModelAttr(dict.getModelAttrId(), dict.getAttrField(), dict.getDictName()
                            , DataType.build(dict.getDataType()), dict.getAccuracy(), dict.getGain(), dict.getResolver()
                            , dict.getScript(), dict.getPath()));
                });
            } else {
                upConfigAttrs.add(new UpModelAttr(item.getModelAttrId(), item.getAttrField(), item.getAttrName()
                        , DataType.build(item.getDataType()), item.getAccuracy(), item.getGain(), item.getResolver()
                        , item.getScript(), null));
            }
        });

        invokeParam.addUpAttrs(upConfigAttrs);
    }
}
