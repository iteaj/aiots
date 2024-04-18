package com.iteaj.plugin.protocol.plc.omron;

import cn.hutool.core.util.StrUtil;
import com.iteaj.framework.ParamMeta;
import com.iteaj.framework.exception.ServiceException;
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
import com.iteaj.iot.client.SocketClient;
import com.iteaj.iot.consts.ExecStatus;
import com.iteaj.iot.plc.ReadAddress;
import com.iteaj.iot.plc.WriteAddress;
import com.iteaj.iot.plc.omron.*;
import com.iteaj.plugin.protocol.plc.AbstractPlcProtocolSupplier;

public class OmronProtocolSupplier extends AbstractPlcProtocolSupplier<NetworkConfigImpl.TcpNetworkConfig, OmronComponent, OmronTcpClient> {

    private DefaultProtocolModel protocolModel = new DefaultProtocolModel("PLC_OMRON_TCP"
            , CtrlMode.POINT, TransportProtocol.TCP, getModelApis(), getModelAttrs());

    private ParamMeta[] gatewayConfig = new ParamMeta[]{
            ParamMeta.buildRequired(ConstConfig.HOST, "主机", "", "127.0.0.1"),
            ParamMeta.buildRequiredNumber(ConstConfig.PORT, "端口", 9600, ""),
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
                String field = param.getConfig(PointProtocolConfig.POINT_FIELD_KEY + readAddress.getAddress());
                if(execStatus == ExecStatus.success) {
                    dataSupplier.addValue(readAddress.getAddress(), field, value);
                } else {
                    dataSupplier.addValue(readAddress.getAddress(), field, CollectStatus.from(execStatus));
                }
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
    protected OmronTcpClient doCreateClient(NetworkConfigImpl.TcpNetworkConfig config, OmronComponent component) {
        OmronConnectProperties connectProperties = new OmronConnectProperties(config.getHost(), config.getPort());
        if(StrUtil.isNotBlank(config.getDeviceSn())) {
            connectProperties.setConnectKey(config.getDeviceSn());
        }

        if(config.getAllIdle() != null) {
            connectProperties.setAllIdleTime(config.getAllIdle());
        } else if(config.getReadIdle() != null) {
            connectProperties.setReaderIdleTime(config.getReadIdle());
        } else if(config.getWriteIdle() != null) {
            connectProperties.setWriterIdleTime(config.getWriteIdle());
        }

        return (OmronTcpClient) component.createNewClient(connectProperties);
    }
}
