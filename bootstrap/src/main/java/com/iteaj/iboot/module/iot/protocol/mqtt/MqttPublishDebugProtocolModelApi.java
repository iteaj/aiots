package com.iteaj.iboot.module.iot.protocol.mqtt;

import com.iteaj.framework.spi.iot.ProtocolModelApiInvokeParam;
import com.iteaj.framework.spi.iot.protocol.*;
import com.iteaj.iboot.module.iot.entity.ProtocolApiConfig;
import com.iteaj.iot.client.mqtt.impl.DefaultMqttPublishProtocol;

import java.util.Map;
import java.util.function.Consumer;

/**
 * mqtt协议发布调试api
 */
public class MqttPublishDebugProtocolModelApi extends AbstractProtocolModelApi {

    private static String remark = "用于向指定的代理(Broker)发布topic消息";

    public MqttPublishDebugProtocolModelApi() {
        super("publish", "发布", remark, ProtocolApiType.func, new ProtocolLinkedMap<>()
                , new ProtocolLinkedMap<>()
                        .add("msg", ProtocolModelApiConfig.downBuild("msg", "发布的报文"))
                        .add("topic", ProtocolModelApiConfig.downBuild("topic", "发布的主题")));
    }

    @Override
    public void invoke(ProtocolModelApiInvokeParam arg, Consumer<InvokeResult> result) {
        new DefaultMqttPublishProtocol(null, null).request();
    }
}
