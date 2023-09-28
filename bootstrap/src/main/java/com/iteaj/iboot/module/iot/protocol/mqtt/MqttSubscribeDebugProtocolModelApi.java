package com.iteaj.iboot.module.iot.protocol.mqtt;

import com.iteaj.framework.spi.iot.ProtocolModelApiInvokeParam;
import com.iteaj.framework.spi.iot.protocol.AbstractProtocolModelApi;
import com.iteaj.framework.spi.iot.protocol.ProtocolApiType;
import com.iteaj.framework.spi.iot.protocol.ProtocolModelApiConfig;

import java.util.Map;
import java.util.function.Consumer;

/**
 * mqtt协议订阅调试api
 */
public class MqttSubscribeDebugProtocolModelApi extends AbstractProtocolModelApi {

    private static String remark = "用于从指定的代理(Broker)订阅相应的topic消息";

    public MqttSubscribeDebugProtocolModelApi() {
        super("subscribe", "订阅", remark, ProtocolApiType.event);
    }

    public MqttSubscribeDebugProtocolModelApi(String code, String name, Map<String, ProtocolModelApiConfig> upConfig, Map<String, ProtocolModelApiConfig> downConfig) {
        super(code, name, ProtocolApiType.event, upConfig, downConfig);
        this.setRemark(remark);
    }

    @Override
    public void invoke(ProtocolModelApiInvokeParam arg, Consumer<Object> result) {

    }
}
