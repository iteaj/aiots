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
import com.iteaj.iot.Protocol;
import com.iteaj.iot.client.http.HttpClient;

/**
 * 大华协议提供
 */
public class DahuaICCProtocolSupplier extends StaticClientProtocolSupplier<NetworkConfigImpl.TcpNetworkConfig, DahuaOpenComponent, HttpClient> {

    private DefaultProtocolModel protocolModel = new DefaultProtocolModel("DAHUA_ICC_OPEN", CtrlMode.COMMON, TransportProtocol.HTTP)
//            .addModelAttr(new ProtocolModelAttr("channelCodeList", "门禁通道列表", DataType.t_string, AttrType.IGNORE, "如:1000003$7$0$0, 多个值可用逗号隔开"))
//            .addModelAttr(new ProtocolModelAttr("channelId", "通道编码", "string", "视频通道编码，第一个$后数字代表通道类型，必须是1，且摄像头类型需是球机(cameraType字段必须是2)"))
            .addModelAttr(new ProtocolModelAttr("streamType", "码流类型", DataType.t_string, AttrType.IGNORE, "码流类型：1=主码流, 2=辅码流，3=辅码流2"))
            .addModelAttr(new ProtocolModelAttr("type", "协议类型", DataType.t_string, AttrType.IGNORE, "协议类型：hls,hlss,flv,flvs,ws_flv,wss_flv,rtmp"))
            .addModelAttr(new ProtocolModelAttr("direct", "方向", DataType.t_string, AttrType.W, "方向：1=上，2=下，3=左，4=右，5=左上，6=左下，7=右上，8=右下"))
            .addModelAttr(new ProtocolModelAttr("stepX", "水平方向不长", DataType.t_string, AttrType.W, "水平方向步长（1-8的整数）,代表转动幅度大小"))
            .addModelAttr(new ProtocolModelAttr("stepY", "垂直方向步长", DataType.t_string, AttrType.W, "垂直方向步长（1-8的整数）,代表转动幅度大小"))
            .addModelAttr(new ProtocolModelAttr("command", "命令", DataType.t_string, AttrType.IGNORE, "0=停止, 1=开启"))
            .addModelAttr(new ProtocolModelAttr("status", "状态", DataType.t_boolean, AttrType.STATUS, "接口响应状态"))
            .addModelAttr(new ProtocolModelAttr("url", "流地址", DataType.t_string, AttrType.R, "码流播放地址"))
            .addModelApi(new DahuaICCProtocolModelApi(DahuaICCProtocolType.openDoor.name(), DahuaICCProtocolType.openDoor.getDesc(), "开门"))
            .addModelApi(new DahuaICCProtocolModelApi(DahuaICCProtocolType.closeDoor.name(), DahuaICCProtocolType.closeDoor.getDesc(), "关门"))
            .addModelApi(new DahuaICCProtocolModelApi(DahuaICCProtocolType.stayOpen.name(), DahuaICCProtocolType.stayOpen.getDesc(), "保持门常开"))
            .addModelApi(new DahuaICCProtocolModelApi(DahuaICCProtocolType.stayClose.name(), DahuaICCProtocolType.stayClose.getDesc(), "保持门常闭"))
            .addModelApi(new DahuaICCProtocolModelApi(DahuaICCProtocolType.normal.name(), DahuaICCProtocolType.normal.getDesc(), "正常模式"))
            .addModelApi(new DahuaICCProtocolModelApi(DahuaICCProtocolType.realtime.name(), DahuaICCProtocolType.realtime.getDesc(), "用于获取设备实时预览地址")
//                    .addDownConfig(ProtocolModelApiConfig.downBuild("channelId"))
                    .addDownConfig(ProtocolModelApiConfig.downBuild("streamType"))
                    .addDownConfig(ProtocolModelApiConfig.downBuild("type"))
                    .addUpConfig(ProtocolModelApiConfig.upBuild("status"))
                    .addUpConfig(ProtocolModelApiConfig.upBuild("url", "流地址")))
            .addModelApi(new DahuaICCProtocolModelApi(DahuaICCProtocolType.OperateDirect.name(), DahuaICCProtocolType.OperateDirect.getDesc(), "用户控制监控设备的方向")
//                    .addDownConfig(ProtocolModelApiConfig.downBuild("channelId"))
                    .addDownConfig(ProtocolModelApiConfig.downBuild("direct"))
                    .addDownConfig(ProtocolModelApiConfig.downBuild("stepX"))
                    .addDownConfig(ProtocolModelApiConfig.downBuild("stepY"))
                    .addDownConfig(ProtocolModelApiConfig.downBuild("command"))
                    .addUpConfig(ProtocolModelApiConfig.upBuild("status")));

    @Override
    public String getDesc() {
        return "大华ICC开放平台";
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
    protected HttpClient doCreateClient(NetworkConfigImpl.TcpNetworkConfig config, DahuaOpenComponent component) {
        return null;
    }
}
