package com.iteaj.plguin.protocol.hj212.driver;

import com.iteaj.framework.ParamMeta;
import com.iteaj.framework.spi.iot.*;
import com.iteaj.framework.spi.iot.consts.CtrlMode;
import com.iteaj.framework.spi.iot.consts.GatewayType;
import com.iteaj.framework.spi.iot.consts.ProtocolImplMode;
import com.iteaj.framework.spi.iot.consts.TransportProtocol;
import com.iteaj.framework.spi.iot.data.ModelAttrMeta;
import com.iteaj.framework.spi.iot.protocol.DefaultProtocolModel;
import com.iteaj.framework.spi.iot.protocol.ProtocolModel;
import com.iteaj.iot.Message;
import com.iteaj.iot.Protocol;
import com.iteaj.iot.config.ConnectProperties;
import com.iteaj.plguin.protocol.hj212.network.HJ212Component;
import com.iteaj.plguin.protocol.hj212.network.HJ212Message;
import com.iteaj.plguin.protocol.hj212.network.HJ212RealtimeProtocol;

import java.util.Date;
import java.util.List;
import java.util.Map;

public class HJ212ProtocolSupplier extends DirectStaticServerProtocolSupplier<NetworkConfigImpl.TcpNetworkConfig, HJ212Component> {

    private static final ProtocolModel model = new DefaultProtocolModel("HJ212", CtrlMode.COMMON, TransportProtocol.TCP);

    @Override
    protected void doRefresh(HJ212Component component, NetworkConfigImpl.TcpNetworkConfig config) {
        ConnectProperties ori = component.config();
        ori.setHost(config.getHost());
        ori.setPort(config.getPort());
    }

    @Override
    protected HJ212Component doCreateComponent(NetworkConfigImpl.TcpNetworkConfig config) {
        ConnectProperties connectProperties = new ConnectProperties(config.getHost(), config.getPort());
        if(config.getAllIdle() != null && config.getAllIdle() >= 0) {
            connectProperties.setAllIdleTime(config.getAllIdle());
        }
        if(config.getReadIdle() != null && config.getReadIdle() >= 0) {
            connectProperties.setReaderIdleTime(config.getReadIdle());
        }
        if(config.getWriteIdle() != null && config.getWriteIdle() >= 0) {
            connectProperties.setWriterIdleTime(config.getWriteIdle());
        }

        return new HJ212Component(connectProperties);
    }

    @Override
    public String getDesc() {
        return "HJ212环境监测协议驱动";
    }

    @Override
    public String getVersion() {
        return "1.0.0";
    }

    @Override
    public ParamMeta[] getNetworkConfig() {
        return NetworkConfigImpl.TcpNetworkConfig.tcpMetas;
    }

    @Override
    public ProtocolModel getProtocol() {
        return model;
    }

    @Override
    public GatewayType getGatewayType() {
        return GatewayType.Direct;
    }

    @Override
    public ProtocolImplMode getImplMode() {
        return ProtocolImplMode.Internal;
    }

    @Override
    protected DataSupplier doGetDataSupplier(Protocol protocol, ProtocolModelApiInvokeParam param, List<ModelAttrMeta> metas) {
        DataSupplier result = new DataSupplier(getProtocol().getCode(), param.buildKey());
        if(protocol instanceof HJ212RealtimeProtocol) {
            HJ212Message message = (HJ212Message)protocol.requestMessage();
            Map<String, String> data = ((HJ212RealtimeProtocol) protocol).getData();

            Date collectTime = message.getBody().getCollectTime();
            metas.forEach(meta -> {
                if(data.containsKey(meta.getField())) {
                    String metaValue = data.get(meta.getField());
                    result.addValue(meta.getId(), "-", meta.getField(), metaValue, collectTime);
                }
            });
        }

        return result;
    }
}
