package com.iteaj.plugin.protocol.plc.omron;

import cn.hutool.core.util.StrUtil;
import com.iteaj.framework.exception.ServiceException;
import com.iteaj.framework.spi.iot.NetworkConfigImpl;
import com.iteaj.framework.spi.iot.consts.CtrlMode;
import com.iteaj.framework.spi.iot.consts.ProtocolImplMode;
import com.iteaj.framework.spi.iot.consts.TransportProtocol;
import com.iteaj.framework.spi.iot.protocol.DefaultProtocolModel;
import com.iteaj.framework.spi.iot.protocol.ProtocolModel;
import com.iteaj.iot.FrameworkComponent;
import com.iteaj.iot.FrameworkManager;
import com.iteaj.iot.client.SocketClient;
import com.iteaj.iot.plc.omron.OmronComponent;
import com.iteaj.iot.plc.omron.OmronConnectProperties;
import com.iteaj.iot.plc.omron.OmronMessage;
import com.iteaj.iot.plc.omron.OmronTcpClient;
import com.iteaj.plugin.protocol.plc.AbstractPlcProtocolSupplier;

public class OmronProtocolSupplier extends AbstractPlcProtocolSupplier<NetworkConfigImpl.TcpNetworkConfig, OmronComponent, OmronTcpClient> {

    private DefaultProtocolModel protocolModel = new DefaultProtocolModel("PLC_OMRON_TCP"
            , CtrlMode.POINT, TransportProtocol.TCP, getModelApis(), getModelAttrs());

    @Override
    public String getDesc() {
        return "欧姆龙PLC协议提供";
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
    public OmronComponent doCreateComponent(NetworkConfigImpl.TcpNetworkConfig config) {
        FrameworkComponent component = FrameworkManager.getComponent(OmronMessage.class);
        if(component == null) {
            component = new OmronComponent();
        }

        return (OmronComponent) component;
    }

    @Override
    public synchronized OmronTcpClient createClient(NetworkConfigImpl.TcpNetworkConfig config) {
        OmronComponent component = FrameworkManager.getComponent(OmronMessage.class);
        if(component == null || !component.isStart()) {
            throw new ServiceException("未启动网络组件");
        }

        OmronConnectProperties connectProperties = new OmronConnectProperties(config.getHost(), config.getPort());
        if(StrUtil.isNotBlank(config.getDeviceSn())) {
            connectProperties.setConnectKey(config.getDeviceSn());
        }

        SocketClient client = component.getClient(connectProperties);
        if(client == null) {
            if(config.getAllIdle() != null) {
                connectProperties.setAllIdleTime(config.getAllIdle());
            } else if(config.getReadIdle() != null) {
                connectProperties.setReaderIdleTime(config.getReadIdle());
            } else if(config.getWriteIdle() != null) {
                connectProperties.setWriterIdleTime(config.getWriteIdle());
            }

            client = component.createNewClient(connectProperties);
            client.init(null);
            component.getClientManager().addClient(connectProperties, client);
        } else { // 修改最新的主机和端口配置
            client.getConfig().setHost(config.getHost());
            client.getConfig().setPort(config.getPort());
        }

        return (OmronTcpClient) client;
    }
}
