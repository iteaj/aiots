package com.iteaj.plugin.protocol.plc.panasonic.driver;

import com.iteaj.framework.ParamMeta;
import com.iteaj.framework.spi.iot.NetworkConfigImpl;
import com.iteaj.framework.spi.iot.consts.*;
import com.iteaj.framework.spi.iot.protocol.DefaultProtocolModel;
import com.iteaj.framework.spi.iot.protocol.ProtocolModel;
import com.iteaj.iot.client.ClientConnectProperties;
import com.iteaj.plugin.protocol.plc.adapt.PlcConnectProperties;
import com.iteaj.plugin.protocol.plc.driver.AbstractPlcEventProtocolModelApi;
import com.iteaj.plugin.protocol.plc.driver.AbstractPlcFuncProtocolModelApi;
import com.iteaj.plugin.protocol.plc.driver.AbstractPlcProtocolSupplier;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

/**
 * 松下协议提供
 */
@Component
public class PanasonicProtocolSupplier extends AbstractPlcProtocolSupplier<PanasonicProtocolSupplier.PanasonicNetworkConfig, PanasonicComponent, PanasonicClient> {

    private DefaultProtocolModel protocolModel = new DefaultProtocolModel("PLC_PANASONIC"
            , CtrlMode.POINT, TransportProtocol.TCP, getModelApis(), getModelAttrs());

    private ParamMeta[] gatewayConfig = new ParamMeta[]{
            ParamMeta.buildRequired(ConstConfig.HOST, "主机", "", "127.0.0.1"),
            ParamMeta.buildRequiredNumber(ConstConfig.PORT, "端口", 44818, ""),
            ParamMeta.buildRequiredNumber("station", "站点", (byte) 0xEE, ""),
    };

    @Override
    public String getDesc() {
        return "松下PLC驱动";
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
    public ParamMeta[] getGatewayConfig() {
        return gatewayConfig;
    }

    @Override
    public PanasonicComponent doCreateComponent(PanasonicNetworkConfig config) {
        return new PanasonicComponent();
    }

    @Override
    protected ClientConnectProperties createPlcConnectProperties(PanasonicNetworkConfig config) {
        return new PanasonicConnectProperties(config.getHost(), config.getPort(), (byte) config.getStation());
    }

    @Override
    protected AbstractPlcFuncProtocolModelApi createPlcFuncProtocolModelApi(String code, String name, FuncType type, String remark) {
        return new PanasonicFuncProtocolModelApi(code, name, type, remark);
    }

    @Override
    protected AbstractPlcEventProtocolModelApi createPlcEventProtocolModelApi(String code, String name, String remark) {
        return new PanasonicEventProtocolModelApi(code, name, remark);
    }

    @Getter
    @Setter
    public class PanasonicNetworkConfig extends NetworkConfigImpl.TcpNetworkConfig {

        private int station;
    }
}
