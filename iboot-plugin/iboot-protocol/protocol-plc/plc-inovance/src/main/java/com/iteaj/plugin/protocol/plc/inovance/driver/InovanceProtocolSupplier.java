package com.iteaj.plugin.protocol.plc.inovance.driver;

import com.iteaj.framework.IVOption;
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
import com.iteaj.plugin.protocol.plc.inovance.profinet.InovanceSeries;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

/**
 * 汇川协议提供
 */
@Component
public class InovanceProtocolSupplier extends AbstractPlcProtocolSupplier<InovanceProtocolSupplier.InovanceNetworkConfig, InovanceComponent, InovanceClient> {

    private DefaultProtocolModel protocolModel = new DefaultProtocolModel("PLC_INOVANCE"
            , CtrlMode.POINT, TransportProtocol.TCP, getModelApis(), getModelAttrs());

    private ParamMeta[] gatewayConfig = new ParamMeta[]{
            ParamMeta.buildRequired(ConstConfig.HOST, "主机", "", "127.0.0.1"),
            ParamMeta.buildRequiredNumber(ConstConfig.PORT, "端口", 502, ""),
            ParamMeta.buildRequiredNumber("station", "站点", (byte) 0xEE, ""),
            ParamMeta.buildRequiredSelect("series", "型号序列", "AM", ""
                    , new IVOption("AM400、AM400_800、AC800等序列", "AM")
                    , new IVOption("H3U,XP等序列", "H3U")
                    , new IVOption("H5U系列", "H5U")),
    };

    @Override
    public String getDesc() {
        return "汇川PLC驱动";
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
    public InovanceComponent doCreateComponent(InovanceNetworkConfig config) {
        return new InovanceComponent();
    }

    @Override
    protected ClientConnectProperties createPlcConnectProperties(InovanceNetworkConfig config) {
        return new InovanceConnectProperties(config.getHost(), config.getPort(), (byte) config.getStation(), config.series);
    }

    @Override
    protected AbstractPlcFuncProtocolModelApi createPlcFuncProtocolModelApi(String code, String name, FuncType type, String remark) {
        return new InovanceFuncProtocolModelApi(code, name, type, remark);
    }

    @Override
    protected AbstractPlcEventProtocolModelApi createPlcEventProtocolModelApi(String code, String name, String remark) {
        return new InovanceEventProtocolModelApi(code, name, remark);
    }

    @Getter
    @Setter
    public class InovanceNetworkConfig extends NetworkConfigImpl.TcpNetworkConfig {

        /**
         * 站号
         */
        private int station;

        /**
         * 序列
         */
        private InovanceSeries series;
    }
}
