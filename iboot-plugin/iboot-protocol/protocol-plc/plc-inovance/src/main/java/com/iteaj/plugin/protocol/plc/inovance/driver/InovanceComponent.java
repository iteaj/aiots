package com.iteaj.plugin.protocol.plc.inovance.driver;

import com.iteaj.iot.client.ClientConnectProperties;
import com.iteaj.plugin.protocol.plc.adapt.PlcClientAdapter;
import com.iteaj.plugin.protocol.plc.adapt.PlcClientComponent;
import com.iteaj.plugin.protocol.plc.adapt.PlcConnectProperties;
import com.iteaj.plugin.protocol.plc.adapt.PlcProtocolType;

    public class InovanceComponent extends PlcClientComponent<InovanceMessage> {

    public InovanceComponent() {
        this(null);
    }

    public InovanceComponent(PlcConnectProperties properties) {
        super(PlcProtocolType.Inovance, properties);
    }

    @Override
    public PlcClientAdapter createNewClient(ClientConnectProperties config) {
        return new InovanceClient(this, (InovanceConnectProperties) config);
    }

    @Override
    public Class<InovanceMessage> getMessageClass() {
        return InovanceMessage.class;
    }

    @Override
    public String getName() {
        return "汇川PLC客户端";
    }

    @Override
    public String getDesc() {
        return "用于和汇川序列PLC设备交互的网络组件";
    }
}
