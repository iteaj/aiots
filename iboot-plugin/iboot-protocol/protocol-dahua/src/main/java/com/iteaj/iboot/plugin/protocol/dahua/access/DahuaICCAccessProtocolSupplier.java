package com.iteaj.iboot.plugin.protocol.dahua.access;

import com.iteaj.framework.spi.iot.NetworkConfigImpl;
import com.iteaj.framework.spi.iot.StaticClientProtocolSupplier;
import com.iteaj.framework.spi.iot.consts.CtrlMode;
import com.iteaj.framework.spi.iot.consts.GatewayType;
import com.iteaj.framework.spi.iot.consts.ProtocolImplMode;
import com.iteaj.framework.spi.iot.consts.TransportProtocol;
import com.iteaj.framework.spi.iot.protocol.DefaultProtocolModel;
import com.iteaj.framework.spi.iot.protocol.ProtocolModel;
import com.iteaj.framework.spi.iot.protocol.ProtocolModelApiConfig;
import com.iteaj.framework.spi.iot.protocol.ProtocolModelAttr;
import com.iteaj.iboot.plugin.protocol.dahua.DahuaICCProtocolType;
import com.iteaj.iboot.plugin.protocol.dahua.DahuaOpenComponent;
import com.iteaj.iot.client.http.HttpClient;
import com.iteaj.iot.config.ConnectProperties;

/**
 * 大华门禁协议提供
 */
public class DahuaICCAccessProtocolSupplier extends StaticClientProtocolSupplier<NetworkConfigImpl.TcpNetworkConfig, DahuaOpenComponent, HttpClient> {

    private DefaultProtocolModel protocolModel = new DefaultProtocolModel("DAHUA_ICC_ACCESS", CtrlMode.COMMON, TransportProtocol.HTTP)
            .addModelAttr(new ProtocolModelAttr("channelCodeList", "门禁通道列表", "string", "如:1000003$7$0$0, 多个值可用逗号隔开"))
            .addModelApi(new DahuaICCAccessProtocolModelApi(DahuaICCProtocolType.openDoor.name(), DahuaICCProtocolType.openDoor.getDesc())
                    .addDownConfig(ProtocolModelApiConfig.downBuild("channelCodeList", "门禁通道号")))
            .addModelApi(new DahuaICCAccessProtocolModelApi(DahuaICCProtocolType.closeDoor.name(), DahuaICCProtocolType.closeDoor.getDesc())
                    .addDownConfig(ProtocolModelApiConfig.downBuild("channelCodeList", "门禁通道号")))
            .addModelApi(new DahuaICCAccessProtocolModelApi(DahuaICCProtocolType.stayOpen.name(), DahuaICCProtocolType.stayOpen.getDesc())
                    .addDownConfig(ProtocolModelApiConfig.downBuild("channelCodeList", "门禁通道号")))
            .addModelApi(new DahuaICCAccessProtocolModelApi(DahuaICCProtocolType.stayClose.name(), DahuaICCProtocolType.stayClose.getDesc())
                    .addDownConfig(ProtocolModelApiConfig.downBuild("channelCodeList", "门禁通道号")))
            .addModelApi(new DahuaICCAccessProtocolModelApi(DahuaICCProtocolType.normal.name(), DahuaICCProtocolType.normal.getDesc())
                    .addDownConfig(ProtocolModelApiConfig.downBuild("channelCodeList", "门禁通道号")))
            ;

    @Override
    public String getDesc() {
        return "大华ICC平台门禁";
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
    public GatewayType getGatewayType() {
        return GatewayType.Gateway;
    }

    @Override
    public ProtocolImplMode getImplMode() {
        return ProtocolImplMode.Internal;
    }

    @Override
    public DahuaOpenComponent doCreateComponent(NetworkConfigImpl.TcpNetworkConfig config) {
        return DahuaOpenComponent.instance;
    }

    @Override
    public HttpClient createClient(NetworkConfigImpl.TcpNetworkConfig config) {
        return null;
    }
}
