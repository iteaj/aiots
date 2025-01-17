package com.iteaj.plugin.protocol.plc.melsec.network;

import com.iteaj.iot.client.ClientConnectProperties;
import com.iteaj.plugin.protocol.plc.adapt.PlcClientAdapter;
import com.iteaj.plugin.protocol.plc.adapt.PlcClientComponent;
import com.iteaj.plugin.protocol.plc.adapt.PlcProtocolType;

public class MelsecComponent extends PlcClientComponent<MelsecMessage> {

    public MelsecComponent() {
        this(null);
    }

    public MelsecComponent(MelsecConnectProperties properties) {
        super(PlcProtocolType.Melsec, properties);
    }

    @Override
    public PlcClientAdapter createNewClient(ClientConnectProperties config) {
        return new MelsecClient(this, (MelsecConnectProperties) config);
    }

    @Override
    public Class<MelsecMessage> getMessageClass() {
        return MelsecMessage.class;
    }

    @Override
    public String getName() {
        return "三菱PLC组件";
    }

    @Override
    public String getDesc() {
        return "用于和三菱PLC设备交互的网络组件";
    }
}
