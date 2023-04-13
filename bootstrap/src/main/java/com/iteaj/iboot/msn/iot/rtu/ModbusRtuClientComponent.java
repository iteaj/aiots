package com.iteaj.iboot.msn.iot.rtu;

import com.iteaj.iot.AbstractProtocol;
import com.iteaj.iot.client.ClientConnectProperties;
import com.iteaj.iot.client.TcpSocketClient;
import com.iteaj.iot.client.component.TcpClientComponent;
import com.iteaj.iot.modbus.client.tcp.ModbusTcpClientMessage;

public class ModbusRtuClientComponent<M extends ModbusTcpClientMessage> extends TcpClientComponent<M> {

    @Override
    public TcpSocketClient createNewClient(ClientConnectProperties config) {
        return new ModbusRtuClient(this, config);
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public String getDesc() {
        return null;
    }

    @Override
    public AbstractProtocol getProtocol(M message) {
        return null;
    }
}
