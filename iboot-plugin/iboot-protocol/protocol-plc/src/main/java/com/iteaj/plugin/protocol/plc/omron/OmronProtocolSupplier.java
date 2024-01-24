package com.iteaj.plugin.protocol.plc.omron;

import com.iteaj.framework.spi.iot.NetworkConfigImpl;
import com.iteaj.framework.spi.iot.consts.CtrlMode;
import com.iteaj.framework.spi.iot.consts.ProtocolImplMode;
import com.iteaj.framework.spi.iot.consts.TransportProtocol;
import com.iteaj.framework.spi.iot.protocol.DefaultProtocolModel;
import com.iteaj.framework.spi.iot.protocol.ProtocolModel;
import com.iteaj.iot.FrameworkComponent;
import com.iteaj.plugin.protocol.plc.AbstractPlcProtocolSupplier;

public class OmronProtocolSupplier extends AbstractPlcProtocolSupplier<NetworkConfigImpl.TcpNetworkConfig> {

    private DefaultProtocolModel protocolModel = new DefaultProtocolModel("PLC:OMRON"
            , CtrlMode.POINT, TransportProtocol.TCP, getModelApis(), getModelAttrs());

    @Override
    public String getDesc() {
        return "欧姆龙PLC协议提供";
    }

    @Override
    public String getVersion() {
        return "1.0.0";
    }

    @Override
    public ProtocolModel getProtocol() {
        return protocolModel;
    }

    @Override
    public ProtocolImplMode getImplMode() {
        return ProtocolImplMode.Internal;
    }

    @Override
    public FrameworkComponent createComponent(NetworkConfigImpl.TcpNetworkConfig config) {
        return null;
    }
}
