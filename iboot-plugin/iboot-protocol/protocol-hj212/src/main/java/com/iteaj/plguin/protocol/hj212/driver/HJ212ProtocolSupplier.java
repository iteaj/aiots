package com.iteaj.plguin.protocol.hj212.driver;

import com.iteaj.framework.ParamMeta;
import com.iteaj.framework.spi.iot.*;
import com.iteaj.framework.spi.iot.consts.*;
import com.iteaj.framework.spi.iot.data.ModelAttrMeta;
import com.iteaj.framework.spi.iot.protocol.*;
import com.iteaj.iot.Protocol;
import com.iteaj.iot.config.ConnectProperties;
import com.iteaj.plguin.protocol.hj212.network.*;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class HJ212ProtocolSupplier extends DirectStaticServerProtocolSupplier<NetworkConfigImpl.TcpNetworkConfig, HJ212Component> {

    private static final List<ProtocolModelApiConfigOption> options = HJ212SysCode.options();
    private static final ProtocolModel model = new DefaultProtocolModel("HJ212", CtrlMode.COMMON, TransportProtocol.TCP)
            .addModelAttr(new ProtocolModelAttr("date", "时间", DataType.t_datetime, AttrType.IGNORE))
            .addModelAttr(new ProtocolModelAttr("pwd", "访问密码", DataType.t_string, AttrType.IGNORE))
            .addModelAttr(new ProtocolModelAttr("ST", "系统编码", DataType.t_string, AttrType.IGNORE))
            .addModelApi(new HJ212FuncModelApi(HJ212CmdCode.上传污染物实时数据.getCode()+"", "上报实时数据", "用于启动现场机上传实时数据").asProtocol()
                    .addDownConfig(ProtocolModelApiConfig.downBuild("ST", "系统编码(参考系统编码表)").setOptions(options))
                    .addDownConfig(ProtocolModelApiConfig.downBuild("pwd", "访问密码")))
            .addModelApi(new HJ212FuncModelApi(HJ212CmdCode.设置实时数据间隔.getCode()+"", "设置实时数据间隔", "指定实时数据上报间隔").asProtocol()
                    .addDownConfig(ProtocolModelApiConfig.downBuild("ST", "系统编码(参考系统编码表)"))
                    .addDownConfig(ProtocolModelApiConfig.downBuild("pwd", "访问密码"))
                    .addDownConfig(ProtocolModelApiConfig.downBuild("RtdInterval", "数据间隔(秒)")))
            .addModelApi(new HJ212FuncModelApi(HJ212CmdCode.提取或上传实时数据间隔.getCode()+"", "提取实时数据间隔", "用于提取现场机的实时数据间隔").asProtocol()
                    .addDownConfig(ProtocolModelApiConfig.downBuild("ST", "系统编码(参考系统编码表)"))
                    .addDownConfig(ProtocolModelApiConfig.downBuild("pwd", "访问密码")))
            ;

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
    protected DataSupplier doGetDataSupplier(Protocol protocol, ProtocolModelApiInvokeParam param, Map<Serializable, ModelAttrMeta> metas) {
        DataSupplier result = new DataSupplier(getProtocol().getCode(), param.buildKey());
        if(protocol instanceof HJ212RealtimeProtocol) {
            HJ212Message message = (HJ212Message)protocol.requestMessage();
            Map<String, String> data = ((HJ212RealtimeProtocol) protocol).getData();

            Date collectTime = message.getBody().getCollectTime();
            metas.values().forEach(meta -> {
                if(data.containsKey(meta.getField())) {
                    String metaValue = data.get(meta.getField());
                    result.addValue(meta.getId(), "", meta.getField(), metaValue, collectTime);
                }
            });
        }

        return result;
    }

}
