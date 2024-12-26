package com.iteaj.plugin.protocol.plc.melsec.driver;

import com.iteaj.framework.IVOption;
import com.iteaj.framework.ParamMeta;
import com.iteaj.framework.spi.iot.NetworkConfigImpl;
import com.iteaj.framework.spi.iot.consts.*;
import com.iteaj.framework.spi.iot.protocol.DefaultProtocolModel;
import com.iteaj.framework.spi.iot.protocol.ProtocolModel;
import com.iteaj.iot.client.ClientConnectProperties;
import com.iteaj.plugin.protocol.plc.driver.AbstractPlcEventProtocolModelApi;
import com.iteaj.plugin.protocol.plc.driver.AbstractPlcFuncProtocolModelApi;
import com.iteaj.plugin.protocol.plc.driver.AbstractPlcProtocolSupplier;
import com.iteaj.plugin.protocol.plc.melsec.network.MelsecClient;
import com.iteaj.plugin.protocol.plc.melsec.network.MelsecComponent;
import com.iteaj.plugin.protocol.plc.melsec.network.MelsecConnectProperties;
import com.iteaj.plugin.protocol.plc.melsec.network.MelsecProtocolType;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

/**
 * 西门子PLC协议提供
 */
@Component
public class MelsecProtocolSupplier extends AbstractPlcProtocolSupplier<MelsecProtocolSupplier.MelsecNetworkConfig, MelsecComponent, MelsecClient> {

    private DefaultProtocolModel protocolModel = new DefaultProtocolModel("PLC_MELSEC"
            , CtrlMode.POINT, TransportProtocol.TCP, getModelApis(), getModelAttrs());

    private ParamMeta[] gatewayConfig = new ParamMeta[]{
            ParamMeta.buildRequired(ConstConfig.HOST, "主机", "", "127.0.0.1"),
            ParamMeta.buildRequiredNumber(ConstConfig.PORT, "端口", 600, ""),
//            ParamMeta.buildRequiredNumber("readIdle", "读空闲(秒)", 600, "多长时间没有读到数据自动断开"),

            ParamMeta.buildRequiredSelect("protocolType", "协议", "Qna3E", null
                    , new IVOption("Qna3E", "Qna3E"), new IVOption("A1E", "A1E")),
    };

    @Override
    public String getDesc() {
        return "三菱PLC驱动";
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
    public MelsecComponent doCreateComponent(MelsecNetworkConfig config) {
        return new MelsecComponent();
    }

    @Override
    protected ClientConnectProperties createPlcConnectProperties(MelsecNetworkConfig config) {
        return new MelsecConnectProperties(config.getHost(), config.getPort(), config.getProtocolType());
    }

    @Override
    protected AbstractPlcFuncProtocolModelApi createPlcFuncProtocolModelApi(String code, String name, FuncType type, String remark) {
        return new MelsecFuncProtocolModelApi(code, name, type, remark);
    }

    @Override
    protected AbstractPlcEventProtocolModelApi createPlcEventProtocolModelApi(String code, String name, String remark) {
        return new MelsecEventProtocolModelApi(code, name, remark);
    }

    @Getter
    @Setter
    public static class MelsecNetworkConfig extends NetworkConfigImpl.TcpNetworkConfig {

        /**
         * 三菱PLC协议类型
         */
        private MelsecProtocolType protocolType;
    }
}
