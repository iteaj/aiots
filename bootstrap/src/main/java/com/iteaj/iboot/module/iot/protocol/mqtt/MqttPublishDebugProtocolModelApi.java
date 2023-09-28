package com.iteaj.iboot.module.iot.protocol.mqtt;

import com.iteaj.framework.spi.iot.ProtocolModelApiInvokeParam;
import com.iteaj.framework.spi.iot.protocol.AbstractProtocolModelApi;
import com.iteaj.framework.spi.iot.protocol.ProtocolApiType;
import com.iteaj.framework.spi.iot.protocol.ProtocolModelApiConfig;

import java.util.Map;
import java.util.function.Consumer;

/**
 * mqtt协议发布调试api
 */
public class MqttPublishDebugProtocolModelApi extends AbstractProtocolModelApi {

    public MqttPublishDebugProtocolModelApi() {
        super("", "", ProtocolApiType.event);
    }

    public MqttPublishDebugProtocolModelApi(String code, String name, Map<String, ProtocolModelApiConfig> upConfig, Map<String, ProtocolModelApiConfig> downConfig) {
        super(code, name, ProtocolApiType.event, upConfig, downConfig);
    }

    @Override
    public void invoke(ProtocolModelApiInvokeParam arg, Consumer<Object> result) {

    }
}
