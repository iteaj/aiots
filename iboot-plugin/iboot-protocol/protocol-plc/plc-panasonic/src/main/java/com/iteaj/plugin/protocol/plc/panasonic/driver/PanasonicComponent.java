package com.iteaj.plugin.protocol.plc.panasonic.driver;

import com.iteaj.iot.client.ClientConnectProperties;
import com.iteaj.plugin.protocol.plc.adapt.PlcClientAdapter;
import com.iteaj.plugin.protocol.plc.adapt.PlcClientComponent;
import com.iteaj.plugin.protocol.plc.adapt.PlcConnectProperties;
import com.iteaj.plugin.protocol.plc.adapt.PlcProtocolType;

public class PanasonicComponent extends PlcClientComponent<PanasonicMessage> {

    public PanasonicComponent() {
        this(null);
    }

    public PanasonicComponent(PlcConnectProperties properties) {
        super(PlcProtocolType.Panasonic, properties);
    }

    @Override
    public PlcClientAdapter createNewClient(ClientConnectProperties config) {
        return new PanasonicClient(this, (PanasonicConnectProperties) config);
    }

    @Override
    public Class<PanasonicMessage> getMessageClass() {
        return PanasonicMessage.class;
    }

    @Override
    public String getName() {
        return "松下PLC客户端";
    }

    @Override
    public String getDesc() {
        return "用于和松下PLC设备交互的网络组件";
    }
}
