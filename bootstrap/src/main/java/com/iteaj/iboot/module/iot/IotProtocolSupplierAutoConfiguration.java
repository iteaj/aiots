package com.iteaj.iboot.module.iot;

import cn.hutool.core.collection.CollectionUtil;
import com.iteaj.framework.autoconfigure.FrameworkProperties;
import com.iteaj.framework.spi.iot.DeviceProtocolSupplier;
import com.iteaj.framework.spi.iot.ProtocolSupplierManager;
import com.iteaj.iboot.module.iot.consts.IotConsts;
import com.iteaj.iboot.module.iot.protocol.mqtt.MqttDebugProtocolSupplier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;

import java.io.File;
import java.util.List;

/**
 * 协议提供配置
 */
public class IotProtocolSupplierAutoConfiguration {

    private final FrameworkProperties properties;

    public IotProtocolSupplierAutoConfiguration(FrameworkProperties properties) {
        this.properties = properties;
    }

    /**
     * 协议提供管理
     * @return
     */
    @Bean
    public ProtocolSupplierManager protocolSupplierManager(@Autowired(required = false) List<DeviceProtocolSupplier> suppliers) {
        ProtocolSupplierManager supplierManager = ProtocolSupplierManager.build(properties);

        // 注册jar spi协议
        List<DeviceProtocolSupplier> jarSupplier = ProtocolSupplierManager.loadFromProtocol();
        if(CollectionUtil.isNotEmpty(jarSupplier)) {
            jarSupplier.forEach(item -> ProtocolSupplierManager.register(item));
        }

        // 注册内置协议
        if(CollectionUtil.isNotEmpty(suppliers)) {
            suppliers.forEach(item -> ProtocolSupplierManager.register(item));
        }

        return supplierManager;
    }

    /**
     * mqtt协议提供
     * @return
     */
    @Bean
    public MqttDebugProtocolSupplier mqttDebugProtocolSupplier() {
        return new MqttDebugProtocolSupplier();
    }
}
