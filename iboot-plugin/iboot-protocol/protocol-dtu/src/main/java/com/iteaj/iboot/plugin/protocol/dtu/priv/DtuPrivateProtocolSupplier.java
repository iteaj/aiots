package com.iteaj.iboot.plugin.protocol.dtu.priv;

import cn.hutool.core.collection.CollectionUtil;
import com.iteaj.framework.ParamMeta;
import com.iteaj.framework.spi.iot.*;
import com.iteaj.framework.spi.iot.consts.*;
import com.iteaj.framework.spi.iot.protocol.*;
import com.iteaj.iboot.plugin.protocol.dtu.AbstractDtuForModbusProtocolSupplier;
import com.iteaj.iboot.plugin.protocol.dtu.ModbusProtocolConsts;
import com.iteaj.iot.AbstractProtocol;
import com.iteaj.iot.Protocol;
import com.iteaj.iot.config.ConnectProperties;
import com.iteaj.iot.server.dtu.impl.CommonDtuMessageAware;
import com.iteaj.iot.server.dtu.impl.CommonDtuServerComponent;
import com.iteaj.iot.server.dtu.impl.CommonDtuServerMessage;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Dtu私有协议
 */
public class DtuPrivateProtocolSupplier extends StaticServerProtocolSupplier<AbstractDtuForModbusProtocolSupplier.DtuNetworkConfig, CommonDtuServerComponent> {

    private final List<ProtocolModelApiConfigOption> options = Arrays.asList(
            new ProtocolModelApiConfigOption("字符串", "string"),
            new ProtocolModelApiConfigOption("十六进制", "hex"));
    private final ProtocolModel protocolModel = new DefaultProtocolModel(ProtocolCodes.DTU_PRIVATE.getValue(), CtrlMode.COMMON, TransportProtocol.TCP)
            .addModelAttr(new ProtocolModelAttr("data", "指令内容", DataType.t_string, AttrType.IGNORE))
            .addModelAttr(new ProtocolModelAttr("value", "值", DataType.t_any, AttrType.IGNORE))
            .addModelAttr(new ProtocolModelAttr("type", "数据类型", DataType.t_any, AttrType.IGNORE))
            .addModelApi(new DtuPrivateEventProtocolModelApi("read", "读数据", "从设备读取数据")
                    .addDownConfig(ProtocolModelApiConfig.downBuild("type", "下发内容的格式").setOptions(options))
                    .addDownConfig(ProtocolModelApiConfig.downBuild("data", "下发给设备的读指令"))
                    .addUpConfig(ProtocolModelApiConfig.upBuild("value", "设备返回的数据")))
            .addModelApi(new DtuPrivateFuncProtocolModelApi("write", "同步写", "写完等待设备响应")
                    .addDownConfig(ProtocolModelApiConfig.downBuild("type", "写内容的格式").setOptions(options))
                    .addDownConfig(ProtocolModelApiConfig.downBuild("data", "写给设备的数据")))
            .addModelApi(new DtuPrivateFuncProtocolModelApi("asyncWrite", "异步写", "写完直接返回")
                    .addDownConfig(ProtocolModelApiConfig.downBuild("type", "写内容的格式").setOptions(options))
                    .addDownConfig(ProtocolModelApiConfig.downBuild("data", "写给设备的数据")));

    @Override
    protected void doRefresh(CommonDtuServerComponent component, AbstractDtuForModbusProtocolSupplier.DtuNetworkConfig config) {
        if(!component.isStart()) {
            ConnectProperties properties = component.config();
            properties.setPort(config.getPort());
            properties.setHost(config.getHost());
            if(config.getReadIdle() != null && config.getReadIdle() > 0) {
                properties.setReaderIdleTime(config.getReadIdle());
            }
        }
    }

    @Override
    protected CommonDtuServerComponent doCreateComponent(AbstractDtuForModbusProtocolSupplier.DtuNetworkConfig config) {
        ConnectProperties connectProperties = new ConnectProperties(config.getHost(), config.getPort());
        if(config.getReadIdle() != null && config.getReadIdle() > 0) {
            connectProperties.setReaderIdleTime(config.getReadIdle());
        }

        return new CommonDtuServerComponent(connectProperties, new CommonDtuMessageAware() {
            @Override
            protected AbstractProtocol getHeartbeatProtocol(CommonDtuServerMessage message) {
                return super.getHeartbeatProtocol(message);
            }
        });
    }

    @Override
    public String getDesc() {
        return "基于DTU的私有协议驱动实现";
    }

    @Override
    public String getVersion() {
        return "1.0.0";
    }

    @Override
    public ProtocolModel getProtocol() {
        return this.protocolModel;
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
    public ParamMeta[] getNetworkConfig() {
        return ModbusProtocolConsts.networkConfig;
    }

    @Override
    public DataSupplier getDataSupplier(Protocol protocol, ProtocolModelApiInvokeParam param, Object value) {
        List<UpModelAttr> upModelAttrs = param.listUpAttrs();
        if(CollectionUtil.isNotEmpty(upModelAttrs)) {
            UpModelAttr upModelAttr = upModelAttrs.get(0);
            Object dataResolver = dataResolver(upModelAttr, value);
            DataSupplier dataSupplier = new DataSupplier(getProtocol().getCode(), param.buildKey());
            if(upModelAttr.getDictMetas() != null && dataResolver instanceof Map) {
                Map<String, SignalOrFieldValue> map = (Map<String, SignalOrFieldValue>) dataResolver;
                upModelAttr.getDictMetas().forEach((dict) -> {
                    SignalOrFieldValue fieldValue = map.remove(dict.getAttrField());
                    if(fieldValue != null) {
                        dataSupplier.addValue(fieldValue.setId(Long.getLong(dict.getDictValue())));
                    }
                });

                if(!map.isEmpty()) {
                    String fields = String.join("|", map.keySet());
                    DeviceLogger.warn("数据解析", "自定义数据", param.getUid(), "模型属性不存在以下字段({})", fields);
                }

                return dataSupplier;
            } else {
                if(value instanceof byte[] && upModelAttr.getDataType() == DataType.t_string) {
                    return dataSupplier.addValue(upModelAttr.getId(), upModelAttr.getField(), new String((byte[]) value));
                }

                return dataSupplier.addValue(upModelAttr.getId(), upModelAttr.getField(), dataResolver);
            }

        } else {
            throw new ProtocolSupplierException("未配置Up模型属性");
        }
    }
}
