package com.iteaj.iboot.plugin.protocol.dahua.access;

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
import com.iteaj.iboot.plugin.protocol.dahua.video.DahuaICCVideoProtocolModelApi;
import com.iteaj.iot.Protocol;
import com.iteaj.iot.client.http.HttpClient;
import com.iteaj.iot.config.ConnectProperties;

/**
 * 大华协议提供
 */
public class DahuaICCAccessProtocolSupplier extends StaticClientProtocolSupplier<NetworkConfigImpl.TcpNetworkConfig, DahuaOpenComponent, HttpClient> {

    private DefaultProtocolModel protocolModel = new DefaultProtocolModel("DAHUA_ICC_OPEN", CtrlMode.COMMON, TransportProtocol.HTTP)
            .addModelAttr(new ProtocolModelAttr("channelCodeList", "门禁通道列表", "string", "如:1000003$7$0$0, 多个值可用逗号隔开"))
            .addModelAttr(new ProtocolModelAttr("channelId", "通道id", "string", "视频通道编码，第一个$后数字代表通道类型，必须是1，且摄像头类型需是球机(cameraType字段必须是2)"))
            .addModelAttr(new ProtocolModelAttr("direct", "方向", "string", "方向：1=上，2=下，3=左，4=右，5=左上，6=左下，7=右上，8=右下"))
            .addModelAttr(new ProtocolModelAttr("stepX", "水平方向不长", "string", "水平方向步长（1-8的整数）,代表转动幅度大小"))
            .addModelAttr(new ProtocolModelAttr("stepY", "垂直方向步长", "string", "垂直方向步长（1-8的整数）,代表转动幅度大小"))
            .addModelAttr(new ProtocolModelAttr("command", "命令", "string", "0=停止, 1=开启"))
            .addModelAttr(new ProtocolModelAttr("status", "状态", "boolean", "接口响应状态"))
            .addModelApi(new DahuaICCAccessProtocolModelApi(DahuaICCProtocolType.openDoor.name(), DahuaICCProtocolType.openDoor.getDesc(), "开门")
                    .addDownConfig(ProtocolModelApiConfig.downBuild("channelCodeList", "门禁通道号")))
            .addModelApi(new DahuaICCAccessProtocolModelApi(DahuaICCProtocolType.closeDoor.name(), DahuaICCProtocolType.closeDoor.getDesc(), "关门")
                    .addDownConfig(ProtocolModelApiConfig.downBuild("channelCodeList", "门禁通道号")))
            .addModelApi(new DahuaICCAccessProtocolModelApi(DahuaICCProtocolType.stayOpen.name(), DahuaICCProtocolType.stayOpen.getDesc(), "保持门常开")
                    .addDownConfig(ProtocolModelApiConfig.downBuild("channelCodeList", "门禁通道号")))
            .addModelApi(new DahuaICCAccessProtocolModelApi(DahuaICCProtocolType.stayClose.name(), DahuaICCProtocolType.stayClose.getDesc(), "保持门常闭")
                    .addDownConfig(ProtocolModelApiConfig.downBuild("channelCodeList", "门禁通道号")))
            .addModelApi(new DahuaICCAccessProtocolModelApi(DahuaICCProtocolType.normal.name(), DahuaICCProtocolType.normal.getDesc(), "正常模式")
                    .addDownConfig(ProtocolModelApiConfig.downBuild("channelCodeList", "门禁通道号")))
            .addModelApi(new DahuaICCAccessProtocolModelApi(DahuaICCProtocolType.OperateDirect.name(), DahuaICCProtocolType.OperateDirect.getDesc(), "")
                    .addDownConfig(ProtocolModelApiConfig.downBuild("channelId", "通道id"))
                    .addDownConfig(ProtocolModelApiConfig.downBuild("direct", "方向"))
                    .addDownConfig(ProtocolModelApiConfig.downBuild("stepX", "水平方向不长"))
                    .addDownConfig(ProtocolModelApiConfig.downBuild("stepY", "垂直方向步长"))
                    .addDownConfig(ProtocolModelApiConfig.downBuild("command", "命令"))
                    .addUpConfig(ProtocolModelApiConfig.upBuild("status", "状态")));
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
