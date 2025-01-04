package com.iteaj.plugin.protocol.plc.ab.driver;

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
import org.springframework.stereotype.Component;

/**
 * 罗克韦尔协议提供
 */
@Component
public class ABProtocolSupplier extends AbstractPlcProtocolSupplier<NetworkConfigImpl.TcpNetworkConfig, ABComponent, ABClient> {

    private DefaultProtocolModel protocolModel = new DefaultProtocolModel("PLC_AB"
            , CtrlMode.POINT, TransportProtocol.TCP, getModelApis(), getModelAttrs());

    private ParamMeta[] gatewayConfig = new ParamMeta[]{
            ParamMeta.buildRequired(ConstConfig.HOST, "主机", "", "127.0.0.1"),
            ParamMeta.buildRequiredNumber(ConstConfig.PORT, "端口", 44818, ""),
            ParamMeta.buildRequiredNumber("readIdle", "读空闲(秒)", 600, "多长时间没有读到数据自动断开"),
    };

    @Override
    public String getDesc() {
        return "罗克韦尔PLC驱动";
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
    public ABComponent doCreateComponent(NetworkConfigImpl.TcpNetworkConfig config) {
        return new ABComponent();
    }

    @Override
    protected ClientConnectProperties createPlcConnectProperties(NetworkConfigImpl.TcpNetworkConfig config) {
        return new PlcConnectProperties(config.getHost(), config.getPort());
    }

    @Override
    protected AbstractPlcFuncProtocolModelApi createPlcFuncProtocolModelApi(String code, String name, FuncType type, String remark) {
        return new ABFuncProtocolModelApi(code, name, type, remark);
    }

    @Override
    protected AbstractPlcEventProtocolModelApi createPlcEventProtocolModelApi(String code, String name, String remark) {
        return new ABEventProtocolModelApi(code, name, remark);
    }
}
