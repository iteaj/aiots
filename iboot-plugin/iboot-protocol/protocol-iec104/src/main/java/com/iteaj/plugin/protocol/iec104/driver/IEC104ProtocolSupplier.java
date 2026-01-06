package com.iteaj.plugin.protocol.iec104.driver;

import com.iteaj.framework.spi.iot.DataSupplier;
import com.iteaj.framework.spi.iot.NetworkConfigImpl;
import com.iteaj.framework.spi.iot.ProtocolModelApiInvokeParam;
import com.iteaj.framework.spi.iot.StaticClientProtocolSupplier;
import com.iteaj.framework.spi.iot.consts.*;
import com.iteaj.framework.spi.iot.protocol.DefaultProtocolModel;
import com.iteaj.framework.spi.iot.protocol.ProtocolModel;
import com.iteaj.framework.spi.iot.protocol.ProtocolModelAttr;
import com.iteaj.iot.Protocol;
import com.iteaj.plugin.protocol.iec104.network.IEC104Client;
import com.iteaj.plugin.protocol.iec104.network.IEC104Component;

public class IEC104ProtocolSupplier extends StaticClientProtocolSupplier<NetworkConfigImpl, IEC104Component, IEC104Client> {

    private static final String DESC = "IEC104协议驱动";
    private ProtocolModel model = new DefaultProtocolModel("IEC104", CtrlMode.POINT, TransportProtocol.TCP)
            .addModelAttr(new ProtocolModelAttr(PointProtocolConfig.POINT_ADDRESS, "地址", DataType.t_string, AttrType.IGNORE, ""));
    @Override
    public String getDesc() {
        return DESC;
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
        return GatewayType.Direct;
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
    protected IEC104Client doCreateClient(NetworkConfigImpl config, IEC104Component component) {
        return new IEC104Client(component, null);
    }

    @Override
    protected IEC104Component doCreateComponent(NetworkConfigImpl config) {
        return new IEC104Component();
    }
}
