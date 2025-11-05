package com.iteaj.iboot.plugin.protocol.dtu.mqtt;

import com.iteaj.framework.ParamMeta;
import com.iteaj.framework.spi.iot.*;
import com.iteaj.framework.spi.iot.consts.*;
import com.iteaj.framework.spi.iot.protocol.DefaultProtocolModel;
import com.iteaj.framework.spi.iot.protocol.ProtocolModel;
import com.iteaj.framework.spi.iot.protocol.ProtocolSupplierException;
import com.iteaj.iboot.plugin.protocol.dtu.AbstractModbusEventProtocolModelApi;
import com.iteaj.iboot.plugin.protocol.dtu.AbstractModbusFuncProtocolModelApi;
import com.iteaj.iboot.plugin.protocol.dtu.rtu.ModbusRtuForDtuProtocolSupplier;
import com.iteaj.iot.Protocol;
import com.iteaj.iot.client.mqtt.MqttClient;
import io.netty.handler.codec.mqtt.MqttQoS;
import io.netty.handler.codec.mqtt.MqttTopicSubscription;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

public class ModbusOverMqttForDtuProtocolSupplier extends StaticClientProtocolSupplier<ModbusOverMqttForDtuProtocolSupplier.ModbusOverMqttNetworkConfig
        , ModbusOverMqttComponent, MqttClient> implements BatchProtocolSupplier {

    private final ProtocolModel model;
    private final ParamMeta[] gatewayConfig;
    private final ModbusRtuForDtuProtocolSupplier protocolSupplier;
    private final AbstractModbusEventProtocolModelApi batchEventModelApi;

    public ModbusOverMqttForDtuProtocolSupplier() {
        protocolSupplier = new ModbusRtuForDtuProtocolSupplier(){
            @Override
            protected AbstractModbusFuncProtocolModelApi createFuncApi(String code, String name, String remark) {
                return new ModbusOverMqttForDtuFuncProtocolModelApi(code, name, remark);
            }

            @Override
            protected AbstractModbusEventProtocolModelApi createEventApi(String code, String name, String remark) {
                return new ModbusOverMqttForDtuEventProtocolModeApi(code, name, remark);
            }
        };

        ProtocolModel protocol = protocolSupplier.getProtocol();
        model = new DefaultProtocolModel(ProtocolCodes.DTU_MODBUS_OVER_MQTT.getValue(), CtrlMode.POINT, TransportProtocol.MQTT, "用于DTU网关使用Mqtt协议做Modbus透传交互");
        protocol.getAttrs().forEach((key, value) -> ((DefaultProtocolModel) model).addModelAttr(value));
        protocol.getApis().forEach((key, value) -> ((DefaultProtocolModel) model).addModelApi(value));

        int length = NetworkConfigImpl.MqttNetworkConfig.mqttMetas.length;
        gatewayConfig =  new ParamMeta[length + 1];
        System.arraycopy(NetworkConfigImpl.MqttNetworkConfig.mqttMetas, 0, gatewayConfig, 0, length - 1);
        gatewayConfig[length - 1] = ParamMeta.buildRequired("publish", "下发主题", "平台发布给DTU的主题", "").setPlaceholder("如: demo/{deviceSn}/write");
        gatewayConfig[length] = ParamMeta.buildRequired("subscription", "上报主题", "DTU发布给平台的主题", "").setPlaceholder("如: demo/{deviceSn}/read}");

        batchEventModelApi = new ModbusOverMqttForDtuEventProtocolModeApi("batch", "批量", "");
        batchEventModelApi.setProtocolModel(model);
    }

    @Override
    public String getDesc() {
        return "基于DTU网关的Modbus Over Mqtt协议提供";
    }

    @Override
    public String getVersion() {
        return "1.0.0";
    }

    @Override
    public ProtocolModel getProtocol() {
        return model;
    }

    @Override
    public GatewayType getGatewayType() {
        return GatewayType.Gateway;
    }

    @Override
    public ProtocolImplMode getImplMode() {
        return ProtocolImplMode.Internal;
    }

    @Override
    public ParamMeta[] getGatewayConfig() {
        return gatewayConfig;
    }

    @Override
    public DataSupplier getDataSupplier(Protocol protocol, ProtocolModelApiInvokeParam param, Object value) {
        return protocolSupplier.getDataSupplier(protocol, param, value);
    }

    @Override
    public ModbusOverMqttComponent doCreateComponent(ModbusOverMqttNetworkConfig config) {
        return new ModbusOverMqttComponent();
    }

    @Override
    protected MqttClient doCreateClient(ModbusOverMqttNetworkConfig config, ModbusOverMqttComponent component) {
        Integer qos = config.getQos();
        String[] split = config.getSubscription().split(",");
        List<MqttTopicSubscription> subscriptions = new ArrayList<>();
        MqttQoS mqttQoS = MqttQoS.valueOf(qos);
        for (String s : split) {
            if(!s.contains("{deviceSn}")) {
                throw new ProtocolSupplierException("订阅主题("+s+")必须包含{deviceSn}占位符");
            }

            s = s.replace("{deviceSn}", config.getDeviceSn());
            subscriptions.add(new MqttTopicSubscription(s, mqttQoS));
        }

        ModbusOverMqttConnectProperties connectProperties = new ModbusOverMqttConnectProperties(config.getHost()
                , config.getPort(), config.getClientId(), subscriptions);
        connectProperties.setUsername(config.getUsername());
        connectProperties.setPassword(config.getPassword());
        connectProperties.setPublishTopic(config.getPublish());
        connectProperties.setSubscribeTopic(config.getSubscription());
        connectProperties.setConnectKey(config.getDeviceSn()); // 使用设备编号作为唯一标识
        if(config.getKeepalive() > 0) {
            connectProperties.setKeepAlive(config.getKeepalive());
        }

        return new MqttClient(component, connectProperties);
    }

    @Override
    public BatchEventModelApi getBatchEventModelApi() {
        return batchEventModelApi;
    }

    @Getter
    @Setter
    public static class ModbusOverMqttNetworkConfig extends NetworkConfigImpl.MqttNetworkConfig {

        private String publish;
        private String subscription;
    }

    @Override
    public Class<ModbusOverMqttNetworkConfig> getNetworkConfigClazz() {
        return ModbusOverMqttNetworkConfig.class;
    }
}
