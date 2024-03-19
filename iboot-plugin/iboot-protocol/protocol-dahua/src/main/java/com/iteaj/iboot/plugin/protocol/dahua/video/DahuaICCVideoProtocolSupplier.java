package com.iteaj.iboot.plugin.protocol.dahua.video;

import com.iteaj.framework.spi.iot.NetworkConfigImpl;
import com.iteaj.framework.spi.iot.StaticClientProtocolSupplier;
import com.iteaj.framework.spi.iot.consts.CtrlMode;
import com.iteaj.framework.spi.iot.consts.ProtocolImplMode;
import com.iteaj.framework.spi.iot.consts.TransportProtocol;
import com.iteaj.framework.spi.iot.protocol.DefaultProtocolModel;
import com.iteaj.framework.spi.iot.protocol.ProtocolModel;
import com.iteaj.framework.spi.iot.protocol.ProtocolModelAttr;
import com.iteaj.iboot.plugin.protocol.dahua.DahuaOpenComponent;
import com.iteaj.iot.client.http.HttpClient;
import com.iteaj.iot.config.ConnectProperties;

/**
 * 大华监控摄像头协议提供
 */
public class DahuaICCVideoProtocolSupplier extends StaticClientProtocolSupplier<NetworkConfigImpl.TcpNetworkConfig, DahuaOpenComponent, HttpClient> {

    private DefaultProtocolModel protocolModel = new DefaultProtocolModel("DAHUA_ICC_VIDEO", CtrlMode.COMMON, TransportProtocol.HTTP)
            .addModelAttr(new ProtocolModelAttr("childSn", "从机地址", "string", "从机地址(1-255的值)"))
            .addModelAttr(new ProtocolModelAttr("position", "寄存器地址", "string", "寄存器地址"))
            .addModelAttr(new ProtocolModelAttr("value", "点位值", "object", "读/写寄存器的值(跟随数据类型转换)"))
            .addModelAttr(new ProtocolModelAttr("type", "数据类型", "string", "要读取或者写入的值的数据类型"));

    @Override
    public String getDesc() {
        return "大华ICC平台监控摄像头";
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
    public DahuaOpenComponent doCreateComponent(NetworkConfigImpl.TcpNetworkConfig config) {
        ConnectProperties connectProperties = new ConnectProperties(config.getHost(), config.getPort());
        return new DahuaOpenComponent();
    }

    @Override
    public HttpClient createClient(NetworkConfigImpl.TcpNetworkConfig config) {
        return null;
    }
}
