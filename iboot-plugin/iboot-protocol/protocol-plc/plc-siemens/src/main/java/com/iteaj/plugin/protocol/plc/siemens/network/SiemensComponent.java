package com.iteaj.plugin.protocol.plc.siemens.network;

import com.iteaj.iot.client.ClientConnectProperties;
import com.iteaj.plugin.protocol.plc.adapt.PlcClientAdapter;
import com.iteaj.plugin.protocol.plc.adapt.PlcClientComponent;
import com.iteaj.plugin.protocol.plc.adapt.PlcProtocolType;

public class SiemensComponent extends PlcClientComponent<SiemensMessage> {

    public SiemensComponent() {
        this(null);
    }

    public SiemensComponent(SiemensConnectProperties properties) {
        super(PlcProtocolType.Siemens, properties);
    }

    @Override
    public PlcClientAdapter createNewClient(ClientConnectProperties config) {
        return new SiemensS7Client(this, (SiemensConnectProperties) config);
    }

    @Override
    public Class<SiemensMessage> getMessageClass() {
        return SiemensMessage.class;
    }

    @Override
    public String getName() {
        return "西门子PLC组件";
    }

    @Override
    public String getDesc() {
        return "用于和西门子PLC S7序列设备交互的网络组件";
    }
}
