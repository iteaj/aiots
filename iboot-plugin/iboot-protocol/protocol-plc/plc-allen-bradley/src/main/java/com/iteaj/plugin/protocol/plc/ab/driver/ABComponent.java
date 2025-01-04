package com.iteaj.plugin.protocol.plc.ab.driver;

import com.iteaj.iot.client.ClientConnectProperties;
import com.iteaj.plugin.protocol.plc.adapt.PlcClientAdapter;
import com.iteaj.plugin.protocol.plc.adapt.PlcClientComponent;
import com.iteaj.plugin.protocol.plc.adapt.PlcConnectProperties;
import com.iteaj.plugin.protocol.plc.adapt.PlcProtocolType;

public class ABComponent extends PlcClientComponent<ABMessage> {

    public ABComponent() {
        this(null);
    }

    public ABComponent(PlcConnectProperties properties) {
        super(PlcProtocolType.AB, properties);
    }

    @Override
    public PlcClientAdapter createNewClient(ClientConnectProperties config) {
        return new ABClient(this, (PlcConnectProperties) config);
    }

    @Override
    public Class<ABMessage> getMessageClass() {
        return ABMessage.class;
    }

    @Override
    public String getName() {
        return "罗克韦尔PLC客户端";
    }

    @Override
    public String getDesc() {
        return "用于和罗克韦尔PLC设备交互的网络组件";
    }
}
