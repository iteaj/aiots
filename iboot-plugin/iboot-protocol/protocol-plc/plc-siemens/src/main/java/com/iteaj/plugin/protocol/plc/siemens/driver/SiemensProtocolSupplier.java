package com.iteaj.plugin.protocol.plc.siemens.driver;

import com.iteaj.framework.IVOption;
import com.iteaj.framework.ParamMeta;
import com.iteaj.framework.spi.iot.NetworkConfigImpl;
import com.iteaj.framework.spi.iot.consts.*;
import com.iteaj.framework.spi.iot.protocol.DefaultProtocolModel;
import com.iteaj.framework.spi.iot.protocol.ProtocolModel;
import com.iteaj.framework.spi.iot.protocol.ProtocolModelApiConfig;
import com.iteaj.iot.client.ClientConnectProperties;
import com.iteaj.plugin.protocol.plc.driver.AbstractPlcEventProtocolModelApi;
import com.iteaj.plugin.protocol.plc.driver.AbstractPlcFuncProtocolModelApi;
import com.iteaj.plugin.protocol.plc.driver.AbstractPlcProtocolSupplier;
import com.iteaj.plugin.protocol.plc.driver.PlcProtocolConsts;
import com.iteaj.plugin.protocol.plc.siemens.network.SiemensComponent;
import com.iteaj.plugin.protocol.plc.siemens.network.SiemensConnectProperties;
import com.iteaj.plugin.protocol.plc.siemens.network.SiemensPLCS;
import com.iteaj.plugin.protocol.plc.siemens.network.SiemensS7Client;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

/**
 * 西门子PLC协议提供
 */
@Component
public class SiemensProtocolSupplier extends AbstractPlcProtocolSupplier<SiemensProtocolSupplier.SiemensNetworkConfig, SiemensComponent, SiemensS7Client> {

    private DefaultProtocolModel protocolModel = new DefaultProtocolModel("PLC_SIEMENS_S7"
            , CtrlMode.POINT, TransportProtocol.TCP, getModelApis(), getModelAttrs());

    private ParamMeta[] gatewayConfig = new ParamMeta[]{
            ParamMeta.buildRequired(ConstConfig.HOST, "主机", "", "127.0.0.1"),
            ParamMeta.buildRequiredNumber(ConstConfig.PORT, "端口", 102, ""),
            ParamMeta.buildRequiredNumber("readIdle", "读空闲(秒)", 600, "多长时间没有读到数据自动断开"),
            //可选[S200、S300、S400、S1500、S200Smart]
            ParamMeta.buildRequiredSelect("model", "型号", "S1200", null
                    , new IVOption("S200", "S200"), new IVOption("S200Smart", "S200Smart")
                    , new IVOption("S300", "S300"), new IVOption("S400", "S400")
                    , new IVOption("S1200", "S1200"), new IVOption("S1500", "S1500")),
    };

    public SiemensProtocolSupplier() {
        super();
        this.getModelApis().put("readByte", createPlcEventProtocolModelApi(PlcProtocolConsts.PLC_READ_BYTE, "读取字节值", "读取字节值")
                .addDownConfig(ProtocolModelApiConfig.downBuild(PointProtocolConfig.POINT_ADDRESS, PlcProtocolConsts.PLC_ADDRESS_CONFIG_REMARK, 1))
//                .addDownConfig(ProtocolModelApiConfig.downBuild(PlcProtocolConsts.PLC_NUMBER, "要读取的float值数量", 2))
                .addUpConfig(ProtocolModelApiConfig.upBuild(PlcProtocolConsts.PLC_VALUE, "读取的字节值", 1)));

        this.getModelApis().put("readDate", createPlcEventProtocolModelApi(PlcProtocolConsts.PLC_READ_DATE, "读取日期值", "读取日期值")
                .addDownConfig(ProtocolModelApiConfig.downBuild(PointProtocolConfig.POINT_ADDRESS, PlcProtocolConsts.PLC_ADDRESS_CONFIG_REMARK, 1))
//                .addDownConfig(ProtocolModelApiConfig.downBuild(PlcProtocolConsts.PLC_NUMBER, "要读取的float值数量", 2))
                .addUpConfig(ProtocolModelApiConfig.upBuild(PlcProtocolConsts.PLC_VALUE, "读取的日期值", 1)));
        this.getModelApis().put("readDtlDatetime", createPlcEventProtocolModelApi(PlcProtocolConsts.PLC_READ_DTL_DATETIME, "读取DTL时间值", "读取DTL时间值")
                .addDownConfig(ProtocolModelApiConfig.downBuild(PointProtocolConfig.POINT_ADDRESS, PlcProtocolConsts.PLC_ADDRESS_CONFIG_REMARK, 1))
//                .addDownConfig(ProtocolModelApiConfig.downBuild(PlcProtocolConsts.PLC_NUMBER, "要读取的float值数量", 2))
                .addUpConfig(ProtocolModelApiConfig.upBuild(PlcProtocolConsts.PLC_VALUE, "日期格式(yyyy-MM-dd HH:mm:ss)", 1)));

        this.getModelApis().put("writeByte", createPlcFuncProtocolModelApi(PlcProtocolConsts.PLC_WRITE_BYTE, "写byte值", FuncType.W, "写取字节值")
                .addDownConfig(ProtocolModelApiConfig.downBuild(PointProtocolConfig.POINT_ADDRESS, PlcProtocolConsts.PLC_ADDRESS_CONFIG_REMARK, 1))
//                .addDownConfig(ProtocolModelApiConfig.downBuild(PlcProtocolConsts.PLC_NUMBER, "要读取的float值数量", 2))
                .addDownConfig(ProtocolModelApiConfig.downBuild(PlcProtocolConsts.PLC_VALUE, "写入的字节值", 1)));

        this.getModelApis().put("writeDate", createPlcFuncProtocolModelApi(PlcProtocolConsts.PLC_WRITE_DATE, "写日期值", FuncType.W, "写日期值")
                .addDownConfig(ProtocolModelApiConfig.downBuild(PointProtocolConfig.POINT_ADDRESS, PlcProtocolConsts.PLC_ADDRESS_CONFIG_REMARK, 1))
                .addDownConfig(ProtocolModelApiConfig.downBuild(PlcProtocolConsts.PLC_VALUE, "日期格式(yyyy-MM-dd)", 2)));

        this.getModelApis().put("writeDatetime", createPlcFuncProtocolModelApi(PlcProtocolConsts.PLC_WRITE_DTL_DATETIME, "写DTL时间值", FuncType.W, "写DTL时间值")
                .addDownConfig(ProtocolModelApiConfig.downBuild(PointProtocolConfig.POINT_ADDRESS, PlcProtocolConsts.PLC_ADDRESS_CONFIG_REMARK, 1))
                .addDownConfig(ProtocolModelApiConfig.downBuild(PlcProtocolConsts.PLC_VALUE, "时间格式(yyyy-MM-dd HH:mm:ss)", 3)));
    }

    @Override
    public String getDesc() {
        return "西门子S7协议";
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
    public SiemensComponent doCreateComponent(SiemensNetworkConfig config) {
        return new SiemensComponent();
    }

    @Override
    protected ClientConnectProperties createPlcConnectProperties(SiemensNetworkConfig config) {
        return new SiemensConnectProperties(config.getHost(), config.getPort(), config.getModel());
    }

    @Override
    protected AbstractPlcFuncProtocolModelApi createPlcFuncProtocolModelApi(String code, String name, FuncType type, String remark) {
        return new SiemensFuncProtocolModelApi(code, name, type, remark);
    }

    @Override
    protected AbstractPlcEventProtocolModelApi createPlcEventProtocolModelApi(String code, String name, String remark) {
        return new SiemensEventProtocolModelApi(code, name, remark);
    }

    @Getter
    @Setter
    public static class SiemensNetworkConfig extends NetworkConfigImpl.TcpNetworkConfig {

        /**
         * 西门子PLC型号
         */
        private SiemensPLCS model;
    }
}
