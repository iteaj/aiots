package com.iteaj.plugin.protocol.plc.siemens;

import com.iteaj.framework.ParamMeta;
import com.iteaj.framework.spi.iot.DataSupplier;
import com.iteaj.framework.spi.iot.NetworkConfigImpl;
import com.iteaj.framework.spi.iot.ProtocolModelApiInvokeParam;
import com.iteaj.framework.spi.iot.consts.*;
import com.iteaj.framework.spi.iot.protocol.DefaultProtocolModel;
import com.iteaj.framework.spi.iot.protocol.ProtocolModel;
import com.iteaj.framework.spi.iot.protocol.ProtocolSupplierException;
import com.iteaj.iot.FrameworkComponent;
import com.iteaj.iot.FrameworkManager;
import com.iteaj.iot.Protocol;
import com.iteaj.iot.client.ClientConnectProperties;
import com.iteaj.iot.consts.ExecStatus;
import com.iteaj.iot.plc.ReadAddress;
import com.iteaj.iot.plc.WriteAddress;
import com.iteaj.iot.plc.omron.OmronTcpProtocol;
import com.iteaj.iot.plc.siemens.*;
import com.iteaj.plugin.protocol.plc.AbstractPlcEventProtocolModelApi;
import com.iteaj.plugin.protocol.plc.AbstractPlcFuncProtocolModelApi;
import com.iteaj.plugin.protocol.plc.AbstractPlcProtocolSupplier;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * 西门子PLC协议提供
 */
public class SiemensProtocolSupplier extends AbstractPlcProtocolSupplier<SiemensProtocolSupplier.SiemensNetworkConfig, SiemensS7Component, SiemensS7Client> {

    private DefaultProtocolModel protocolModel = new DefaultProtocolModel("PLC_SIEMENS_S7"
            , CtrlMode.POINT, TransportProtocol.TCP, getModelApis(), getModelAttrs());

    private ParamMeta[] gatewayConfig = new ParamMeta[]{
            ParamMeta.buildRequired(ConstConfig.HOST, "主机", "", "127.0.0.1"),
            ParamMeta.buildRequiredNumber(ConstConfig.PORT, "端口", 102, ""),
            ParamMeta.buildRequiredNumber("readIdle", "读超时(秒)", 600, ""),
            ParamMeta.buildRequired("model", "型号", "可选[S200、S300、S400、S1500、S200Smart]", "S1200"),
    };

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
    public DataSupplier getDataSupplier(Protocol protocol, ProtocolModelApiInvokeParam param, Object value) {
        DataSupplier dataSupplier = new DataSupplier(getProtocol().getCode(), param.buildKey());
        if(param == null) {
            throw new ProtocolSupplierException("调用参数不存在");
        }

        if(protocol instanceof OmronTcpProtocol) {
            ExecStatus execStatus = ((OmronTcpProtocol) protocol).getExecStatus();
            WriteAddress writeAddress = ((OmronTcpProtocol) protocol).getWriteAddress();
            if(writeAddress == null) {
                ReadAddress readAddress = ((OmronTcpProtocol) protocol).getBatchAddress().get(0);
                param.listUpAttrs().stream().findFirst().ifPresent(upModelAttr -> {
                    if(execStatus == ExecStatus.success) {
                        Object collectValue = value;
                        if(upModelAttr.getDataType() != null && upModelAttr.getAccuracy() != null) {
                            if(upModelAttr.getDataType() == DataType.t_float) {
                                BigDecimal decimal = new BigDecimal(value.toString()).setScale(upModelAttr.getAccuracy(), RoundingMode.DOWN);
                                collectValue = decimal.floatValue();
                            } else if(upModelAttr.getDataType() == DataType.t_double) {
                                BigDecimal decimal = new BigDecimal(value.toString()).setScale(upModelAttr.getAccuracy(), RoundingMode.DOWN);
                                collectValue = decimal.doubleValue();
                            }
                        }

                        dataSupplier.addValue(upModelAttr.getId(), readAddress.getAddress(), upModelAttr.getField(), collectValue);
                    } else {
                        dataSupplier.addValue(upModelAttr.getId(), readAddress.getAddress(), upModelAttr.getField(), CollectStatus.from(execStatus));
                    }
                });
            }
        }

        return dataSupplier;
    }

    @Override
    public SiemensS7Component doCreateComponent(SiemensNetworkConfig config) {
        FrameworkComponent component = FrameworkManager.getComponent(SiemensS7Message.class);
        if(component == null) {
            component = new SiemensS7Component();
        }

        return (SiemensS7Component) component;
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
        private SiemensModel model;
    }
}
