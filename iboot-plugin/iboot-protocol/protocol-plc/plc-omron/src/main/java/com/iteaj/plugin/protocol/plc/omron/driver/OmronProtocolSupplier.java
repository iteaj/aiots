package com.iteaj.plugin.protocol.plc.omron.driver;

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
import com.iteaj.plugin.protocol.plc.omron.network.OmronComponent;
import com.iteaj.plugin.protocol.plc.omron.network.OmronFinsClient;
import org.springframework.stereotype.Component;

/**
 * 欧姆龙协议提供
 */
@Component
public class OmronProtocolSupplier extends AbstractPlcProtocolSupplier<NetworkConfigImpl.TcpNetworkConfig, OmronComponent, OmronFinsClient> {

    private DefaultProtocolModel protocolModel = new DefaultProtocolModel("PLC_OMRON_TCP"
            , CtrlMode.POINT, TransportProtocol.TCP, getModelApis(), getModelAttrs());

    private ParamMeta[] gatewayConfig = new ParamMeta[]{
            ParamMeta.buildRequired(ConstConfig.HOST, "主机", "", "127.0.0.1"),
            ParamMeta.buildRequiredNumber(ConstConfig.PORT, "端口", 9600, ""),
            ParamMeta.buildRequiredNumber("readIdle", "读空闲(秒)", 600, "多长时间没有读到数据自动断开"),
    };

    @Override
    public String getDesc() {
        return "欧姆龙PLC协议";
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
    public OmronComponent doCreateComponent(NetworkConfigImpl.TcpNetworkConfig config) {
        return new OmronComponent();
    }

    @Override
    protected ClientConnectProperties createPlcConnectProperties(NetworkConfigImpl.TcpNetworkConfig config) {
        return new PlcConnectProperties(config.getHost(), config.getPort());
    }

    @Override
    protected AbstractPlcFuncProtocolModelApi createPlcFuncProtocolModelApi(String code, String name, FuncType type, String remark) {
        return new OmronFuncProtocolModelApi(code, name, type, remark);
    }

    @Override
    protected AbstractPlcEventProtocolModelApi createPlcEventProtocolModelApi(String code, String name, String remark) {
        return new OmronEventProtocolModelApi(code, name, remark);
    }
}
