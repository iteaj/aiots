package com.iteaj.iboot.plugin.protocol.opcua.driver;

import com.iteaj.framework.spi.iot.*;
import com.iteaj.framework.spi.iot.consts.*;
import com.iteaj.framework.spi.iot.protocol.DefaultProtocolModel;
import com.iteaj.framework.spi.iot.protocol.ProtocolModel;
import com.iteaj.iboot.plugin.protocol.opcua.network.OpcuaClient;
import com.iteaj.iboot.plugin.protocol.opcua.network.OpcuaComponent;
import com.iteaj.iot.Protocol;
import com.iteaj.iot.client.ClientConnectProperties;

public class OpcuaProtocolSupplier extends StaticClientProtocolSupplier<NetworkConfigImpl, OpcuaComponent, OpcuaClient> {

    @Override
    public String getDesc() {
        return "Opcua协议驱动";
    }

    @Override
    public String getVersion() {
        return "1.0.0";
    }

    @Override
    public ProtocolModel getProtocol() {
        return new DefaultProtocolModel(ProtocolCodes.OPCUA_DEFAULT_IMPL.getValue(), CtrlMode.POINT, TransportProtocol.TCP);
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
    public DataSupplier getDataSupplier(Protocol protocol, ProtocolModelApiInvokeParam param, Object value) {
        return null;
    }

    @Override
    protected OpcuaClient doCreateClient(NetworkConfigImpl config, OpcuaComponent component) {
        ClientConnectProperties properties = new ClientConnectProperties(config.getHost(), config.getPort(), config.getDeviceSn());
        return new OpcuaClient(component, properties);
    }

    @Override
    protected OpcuaComponent doCreateComponent(NetworkConfigImpl config) {
        return new OpcuaComponent();
    }
}
