package com.iteaj.plugin.protocol.plc.omron;

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
import com.iteaj.iot.plc.omron.*;
import com.iteaj.plugin.protocol.plc.AbstractPlcEventProtocolModelApi;
import com.iteaj.plugin.protocol.plc.AbstractPlcFuncProtocolModelApi;
import com.iteaj.plugin.protocol.plc.AbstractPlcProtocolSupplier;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * 欧姆龙协议提供
 */
@Component
public class OmronProtocolSupplier extends AbstractPlcProtocolSupplier<NetworkConfigImpl.TcpNetworkConfig, OmronComponent, OmronTcpClient> {

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
                        Object collectValue = dataResolver(upModelAttr, value);
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
    public OmronComponent doCreateComponent(NetworkConfigImpl.TcpNetworkConfig config) {
        FrameworkComponent component = FrameworkManager.getComponent(OmronMessage.class);
        if(component == null) {
            component = new OmronComponent();
        }

        return (OmronComponent) component;
    }

    @Override
    protected ClientConnectProperties createPlcConnectProperties(NetworkConfigImpl.TcpNetworkConfig config) {
        return new OmronConnectProperties(config.getHost(), config.getPort());
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
