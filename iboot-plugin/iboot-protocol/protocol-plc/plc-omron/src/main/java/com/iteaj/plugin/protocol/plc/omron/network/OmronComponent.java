package com.iteaj.plugin.protocol.plc.omron.network;

import com.iteaj.iot.client.ClientConnectProperties;
import com.iteaj.plugin.protocol.plc.adapt.PlcClientAdapter;
import com.iteaj.plugin.protocol.plc.adapt.PlcClientComponent;
import com.iteaj.plugin.protocol.plc.adapt.PlcConnectProperties;

public class OmronComponent extends PlcClientComponent<OmronMessage> {

    @Override
    public PlcClientAdapter createNewClient(ClientConnectProperties config) {
        return new OmronFinsClient(this, (PlcConnectProperties) config);
    }

    @Override
    public Class<OmronMessage> getMessageClass() {
        return OmronMessage.class;
    }

    @Override
    public String getName() {
        return "欧姆龙Fins客户端";
    }

    @Override
    public String getDesc() {
        return "用于和欧姆龙PLC设备交互的网络组件";
    }
}
