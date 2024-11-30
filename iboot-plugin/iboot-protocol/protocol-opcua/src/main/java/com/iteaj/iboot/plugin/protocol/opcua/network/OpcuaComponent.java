package com.iteaj.iboot.plugin.protocol.opcua.network;

import com.iteaj.iot.ProtocolType;
import com.iteaj.iot.client.ClientComponent;
import com.iteaj.iot.client.ClientConnectProperties;
import com.iteaj.iot.client.TcpSocketClient;
import com.iteaj.iot.client.component.TcpClientComponent;
import com.iteaj.iot.client.protocol.ServerInitiativeProtocol;

public class OpcuaComponent extends TcpClientComponent<OpcuaMessage> {

    @Override
    protected ServerInitiativeProtocol<OpcuaMessage> doGetProtocol(OpcuaMessage message, ProtocolType type) {
        return null;
    }

    @Override
    public TcpSocketClient createNewClient(ClientConnectProperties config) {
        return null;
    }

    @Override
    public String getName() {
        return "";
    }

    @Override
    public String getDesc() {
        return "";
    }
}
