package com.iteaj.framework.spi.iot;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ClassUtil;
import com.iteaj.framework.IVOption;
import com.iteaj.framework.ParamMeta;
import com.iteaj.framework.spi.iot.consts.*;
import com.iteaj.framework.spi.iot.protocol.*;
import com.iteaj.iot.FrameworkComponent;
import com.iteaj.iot.Protocol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * 协议提供
 */
public interface DeviceProtocolSupplier<C extends NetworkConfig, T extends FrameworkComponent> {

    Logger supplierLogger = LoggerFactory.getLogger(DeviceProtocolSupplier.class);

    /**
     * 协议描述
     * @return
     */
    String getDesc();

    /**
     * 协议版本
     * @return
     */
    String getVersion();

    /**
     * 返回协议配置
     * @return
     */
    ProtocolModel getProtocol();

    /**
     * 网关类型
     * @return
     */
    GatewayType getGatewayType();

    /**
     * 连接类型
     * @return
     */
    ConnectionType getConnectionType();

    /**
     * 实现方式
     * @return
     */
    ProtocolImplMode getImplMode();

    ParamMeta[] EMPTY_CONFIG = new ParamMeta[]{};

    /**
     * 产品额外配置
     * @return
     */
    default ParamMeta[] getProductConfig() {
        return EMPTY_CONFIG;
    }

    /**
     * 子设备配置
     * @return
     */
    default ParamMeta[] getDeviceConfig() {
        return EMPTY_CONFIG;
    }

    /**
     * 网关设备配置
     * @return
     */
    default ParamMeta[] getGatewayConfig() {
        return EMPTY_CONFIG;
    }

    /**
     * 网络组件额外配置
     * @return
     */
    default ParamMeta[] getNetworkConfig() {
        return EMPTY_CONFIG;
    }

    /**
     * 返回网络配置类型
     * @return
     */
    default Class<C> getNetworkConfigClazz() {
        return (Class<C>) ClassUtil.getTypeArgument(getClass(), 0);
    }

    /**
     * 获取协议提供的数据
     * @param protocol
     * @param param {@link org.springframework.lang.Nullable} 平台主动调用时的参数
     * @param value 协议请求返回值
     * @return
     */
    DataSupplier getDataSupplier(Protocol protocol, ProtocolModelApiInvokeParam param, Object value);

    /**
     * 获取已经创建的组件
     * @return
     */
    T getComponent();

    /**
     * 创建组件
     * @param config 服务端组件必填
     * @return 如果已经存在直接返回, 不存在则创建
     */
    T createComponent(C config);

    default Map<String, ParamMeta> resolverMetaConfig(Predicate<AbstractProtocolModelApi> filter) {
        final ProtocolModel protocolModel = getProtocol();
        Map<String, ParamMeta> fieldMetas = new LinkedHashMap<>();
        final Map<String, AbstractProtocolModelApi> apiMap = protocolModel.getApis();

        if(apiMap != null && !apiMap.isEmpty()) {

            final List<AbstractProtocolModelApi> modelApis = apiMap
                    .values().stream().filter(filter).collect(Collectors.toList());

            if(!modelApis.isEmpty()) {
                // 新增指令项
                IVOption[] directOptions = new IVOption[modelApis.size()];
                fieldMetas.put("direct", ParamMeta.buildRequiredSelect("direct", "指令", null, null, directOptions));

                // 新增下行配置项
                for (int i = 0; i < modelApis.size(); i++) {
                    AbstractProtocolModelApi modelApi = modelApis.get(i);
                    final IVOption directOption = new IVOption(modelApi.getName(), modelApi.getCode());
                    if(modelApi.getDownConfig() != null) {
                        modelApi.getDownConfig().values().stream()
                                .filter(apiConfig -> apiConfig.getDirection() == ApiConfigDirection.DOWN)
                                .forEach(apiConfig -> {
                                    final ProtocolModelAttr modelAttr = protocolModel.getAttrs().get(apiConfig.getProtocolModelAttrField());
                                    String attrName = modelAttr != null ? modelAttr.getName() : apiConfig.getProtocolModelAttrField();
                                    directOption.addChildren(attrName, apiConfig.getProtocolModelAttrField());
                                    if(!fieldMetas.containsKey(apiConfig.getProtocolModelAttrField())) {
                                        if(CollectionUtil.isNotEmpty(apiConfig.getOptions())) {
                                            final IVOption[] options = apiConfig.getOptions().stream()
                                                    .map(item -> new IVOption(item.getLabel(), item.getValue()))
                                                    .toArray(size -> new IVOption[size]);
                                            fieldMetas.put(apiConfig.getProtocolModelAttrField(), ParamMeta
                                                    .buildSelect(apiConfig.getProtocolModelAttrField(), attrName, null, options));
                                        } else {
                                            fieldMetas.put(apiConfig.getProtocolModelAttrField(), ParamMeta.build(apiConfig.getProtocolModelAttrField(), attrName));
                                        }
                                    }
                                });
                    }

                    directOptions[i] = directOption;
                }
            }
        }

        return fieldMetas;
    }
}
