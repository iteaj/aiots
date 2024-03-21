package com.iteaj.iboot.plugin.protocol.dahua.video;

import com.iteaj.framework.spi.iot.DataSupplier;
import com.iteaj.framework.spi.iot.NetworkConfigImpl;
import com.iteaj.framework.spi.iot.ProtocolModelApiInvokeParam;
import com.iteaj.framework.spi.iot.StaticClientProtocolSupplier;
import com.iteaj.framework.spi.iot.consts.*;
import com.iteaj.framework.spi.iot.protocol.DefaultProtocolModel;
import com.iteaj.framework.spi.iot.protocol.ProtocolModel;
import com.iteaj.framework.spi.iot.protocol.ProtocolModelApiConfig;
import com.iteaj.framework.spi.iot.protocol.ProtocolModelAttr;
import com.iteaj.iboot.plugin.protocol.dahua.DahuaICCProtocolType;
import com.iteaj.iboot.plugin.protocol.dahua.DahuaOpenComponent;
import com.iteaj.iot.Protocol;
import com.iteaj.iot.client.http.HttpClient;
import com.iteaj.iot.config.ConnectProperties;

/**
 * 大华监控摄像头协议提供
 */
public class DahuaICCVideoProtocolSupplier extends StaticClientProtocolSupplier<NetworkConfigImpl.TcpNetworkConfig, DahuaOpenComponent, HttpClient> {

    private DefaultProtocolModel protocolModel = new DefaultProtocolModel("DAHUA_ICC_VIDEO", CtrlMode.COMMON, TransportProtocol.HTTP)
            .addModelAttr(new ProtocolModelAttr("channelId", "通道id", "string", "视频通道编码，第一个$后数字代表通道类型，必须是1，且摄像头类型需是球机(cameraType字段必须是2)"))
            .addModelAttr(new ProtocolModelAttr("direct", "方向", "string", "方向：1=上，2=下，3=左，4=右，5=左上，6=左下，7=右上，8=右下"))
            .addModelAttr(new ProtocolModelAttr("stepX", "水平方向不长", "string", "水平方向步长（1-8的整数）,代表转动幅度大小"))
            .addModelAttr(new ProtocolModelAttr("stepY", "垂直方向步长", "string", "垂直方向步长（1-8的整数）,代表转动幅度大小"))
            .addModelAttr(new ProtocolModelAttr("command", "命令", "string", "0=停止, 1=开启"))
            .addModelAttr(new ProtocolModelAttr("status", "状态", "boolean", "接口响应状态"))
            .addModelApi(new DahuaICCVideoProtocolModelApi(DahuaICCProtocolType.OperateDirect.name(), DahuaICCProtocolType.OperateDirect.getDesc(), FuncType.write)
                    .addDownConfig(ProtocolModelApiConfig.downBuild("channelId"))
                    .addDownConfig(ProtocolModelApiConfig.downBuild("direct"))
                    .addDownConfig(ProtocolModelApiConfig.downBuild("stepX"))
                    .addDownConfig(ProtocolModelApiConfig.downBuild("stepY"))
                    .addDownConfig(ProtocolModelApiConfig.downBuild("command"))
                    .addUpConfig(ProtocolModelApiConfig.upBuild("status")));

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
    public GatewayType getGatewayType() {
        return GatewayType.Gateway;
    }

    @Override
    public ProtocolImplMode getImplMode() {
        return ProtocolImplMode.Internal;
    }

    @Override
    public DataSupplier getDataSupplier(Protocol protocol, ProtocolModelApiInvokeParam param, Object value) {
        return null;
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
