package com.iteaj.iboot.plugin.protocol.opcua.driver;

import com.iteaj.framework.IVOption;
import com.iteaj.framework.ParamMeta;
import com.iteaj.framework.spi.iot.*;
import com.iteaj.framework.spi.iot.consts.*;
import com.iteaj.framework.spi.iot.protocol.*;
import com.iteaj.iboot.plugin.protocol.opcua.network.*;
import com.iteaj.iot.Protocol;
import com.iteaj.iot.client.ClientConnectProperties;
import lombok.Getter;
import lombok.Setter;
import org.eclipse.milo.opcua.stack.core.types.builtin.DataValue;
import org.eclipse.milo.opcua.stack.core.types.builtin.DateTime;
import org.eclipse.milo.opcua.stack.core.types.builtin.StatusCode;
import org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OpcuaProtocolSupplier extends StaticClientProtocolSupplier<OpcuaProtocolSupplier.OpcUaNetworkConfig, OpcuaComponent, OpcuaClientWrapper> implements BatchProtocolSupplier {


    private final static ParamMeta[] GATEWAY_CONFIG;
    private final static ParamMeta[] DEVICE_CONFIG;
    private final static DefaultProtocolModel protocolModel;
    private final static Map<String, ProtocolModelAttr> modelAttrs;
    private final static OpcuaProtocolEventModelApi batchEventModelApi;
    private final static Map<String, AbstractProtocolModelApi> modelApis;

    static {
        modelApis = new HashMap<>();
        modelAttrs = new HashMap<>();
        protocolModel = new DefaultProtocolModel(ProtocolCodes.OPCUA_DEFAULT_IMPL.getValue(), CtrlMode.POINT, TransportProtocol.TCP, modelApis, modelAttrs);

        List<ProtocolModelApiConfigOption> writeOptions = new ArrayList<>();
        writeOptions.add(new ProtocolModelApiConfigOption("String", "string", "字符串"));
        writeOptions.add(new ProtocolModelApiConfigOption("Byte", "byte", "byte"));
        writeOptions.add(new ProtocolModelApiConfigOption("Short", "short", "int16"));
        writeOptions.add(new ProtocolModelApiConfigOption("Int", "int", "int32"));
        writeOptions.add(new ProtocolModelApiConfigOption("Long", "long", "int64"));
        writeOptions.add(new ProtocolModelApiConfigOption("Boolean", "boolean", "布尔类型"));
        writeOptions.add(new ProtocolModelApiConfigOption("Float", "float", "float"));
        writeOptions.add(new ProtocolModelApiConfigOption("Double", "double", "double"));
        writeOptions.add(new ProtocolModelApiConfigOption("Date", "date", "日期"));
//        writeOptions.add(new ProtocolModelApiConfigOption("readTimestamp", "timestamp", "占4个寄存器(long)"));

        GATEWAY_CONFIG = new ParamMeta[]{
          ParamMeta.buildRequired("host", "主机"),
          ParamMeta.buildRequiredNumber("port", "端口", 49326, ""),
          ParamMeta.build("username", "用户名", "不指定则使用匿名方式"),
          ParamMeta.build("password", "密码"),
        };

        DEVICE_CONFIG = new  ParamMeta[]{
                ParamMeta.buildRequiredNumber("limit", "读最大数量", 128, "单次读取的最大标记数"),
                ParamMeta.buildRequiredSelect("isBatch", "是否批量", "true", "按最大数批量读取"
                        , new IVOption("是", "true")
                        , new IVOption("否", "false")),
        };

        modelAttrs.put("value", new ProtocolModelAttr("value", "值", DataType.t_any, AttrType.IGNORE));
        modelAttrs.put("address", new ProtocolModelAttr("address", "地址", DataType.t_string, AttrType.IGNORE));
        modelAttrs.put("dataType", new ProtocolModelAttr("dataType", "数据类型", DataType.t_string, AttrType.IGNORE));

        // 读变量接口
        OpcuaProtocol.OpcuaProtocolType read = OpcuaProtocol.OpcuaProtocolType.READ;
        protocolModel.addModelApi(batchEventModelApi = (OpcuaProtocolEventModelApi) new OpcuaProtocolEventModelApi(read.name(), read.getDesc(), "读取变量节点值")
                .addDownConfig(ProtocolModelApiConfig.downBuild("address", "ns=2;s=通道.设备.标记"))
                .addUpConfig(ProtocolModelApiConfig.upBuild("value", "读取到的值")));

        // 读对象接口
//        OpcuaProtocol.OpcuaProtocolType readObject = OpcuaProtocol.OpcuaProtocolType.READ_OBJECT;
//        modelApis.put(readObject.name(), new OpcuaProtocolEventModelApi(readObject.name(), readObject.getDesc(), "读取对象节点"));

        // 写数据接口
        OpcuaProtocol.OpcuaProtocolType write = OpcuaProtocol.OpcuaProtocolType.WRITE;
        protocolModel.addModelApi(new OpcuaProtocolFuncModelApi(write.name(), write.getDesc(), "写入变量节点值")
                .addDownConfig(ProtocolModelApiConfig.downBuild("address", "ns=2;s=通道.设备.标记"))
                .addDownConfig(ProtocolModelApiConfig.downBuild("value", "写入的值"))
                .addDownConfig(ProtocolModelApiConfig.downBuild("dataType", "写入值的数据类型").setOptions(writeOptions)));

        // 执行方法接口
        OpcuaProtocol.OpcuaProtocolType execMethod = OpcuaProtocol.OpcuaProtocolType.EXEC_METHOD;
        protocolModel.addModelApi(new OpcuaProtocolFuncModelApi(execMethod.name(), execMethod.getDesc(), "执行方法节点"));

    }

    @Override
    public String getDesc() {
        return "OpcUa协议驱动";
    }

    @Override
    public String getVersion() {
        return "1.0.0";
    }

    @Override
    public ProtocolModel getProtocol() {
        return new DefaultProtocolModel(ProtocolCodes.OPCUA_DEFAULT_IMPL.getValue(), CtrlMode.POINT, TransportProtocol.TCP, modelApis, modelAttrs);
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
    public ParamMeta[] getGatewayConfig() {
        return GATEWAY_CONFIG;
    }

    public ParamMeta[] getDeviceConfig() {
        return DEVICE_CONFIG;
    }

    @Override
    public DataSupplier getDataSupplier(Protocol protocol, ProtocolModelApiInvokeParam param, Object value) {
        if(protocol instanceof OpcuaVariableProtocol) {
            OpcuaProtocol.OpcuaProtocolType protocolType = protocol.protocolType();
            if(protocolType == OpcuaProtocol.OpcuaProtocolType.READ
                    || protocolType == OpcuaProtocol.OpcuaProtocolType.READ_OBJECT) {
                DataValue dataValue = (DataValue) value;
                UpModelAttr upModelAttr = param.listUpAttrs().get(0);
                String address = param.getString("resolverAddress");
                if(dataValue.getStatusCode() == StatusCode.GOOD) {
                    Object resolver = dataResolver(upModelAttr, getValue(dataValue));
                    return new DataSupplier(protocolType.name(), param.buildKey())
                            .addValue(upModelAttr.getId(), address, upModelAttr.getField(), resolver);
                } else {
                    return new DataSupplier(protocolType.name(), param.buildKey())
                            .addValue(upModelAttr.getId(), address, upModelAttr.getField(), CollectStatus.Fail);
                }
            }
        }

        return null;
    }

    public static Object getValue(DataValue read) {
        Object value = read.getValue().getValue();
        if(value instanceof UNumber) {
            if(value instanceof UByte) {
                return ((UByte) value).shortValue();
            } else if(value instanceof UShort) {
                return ((UShort) value).intValue();
            } else if(value instanceof UInteger) {
                return ((UInteger) value).longValue();
            } else if(value instanceof ULong) {
                return ((ULong) value).longValue();
            }
        } else if(value instanceof DateTime) {
            return ((DateTime) value).getJavaDate();
        }

        return value;
    }

    @Override
    protected OpcuaClientWrapper doCreateClient(OpcUaNetworkConfig config, OpcuaComponent component) {
        OpcuaClientConfig properties = new OpcuaClientConfig(config.getHost(), config.getPort(), config.getDeviceSn())
                .setUsername(config.getUsername()).setPassword(config.getPassword());

        return new OpcuaClientWrapper(component, properties);
    }

    @Override
    protected OpcuaComponent doCreateComponent(OpcUaNetworkConfig config) {
        return new OpcuaComponent();
    }

    @Override
    public BatchEventModelApi getBatchEventModelApi() {
        return batchEventModelApi;
    }

    @Getter
    @Setter
    public static class OpcUaNetworkConfig extends NetworkConfigImpl {
        /**
         * 用户名
         */
        private String username;

        /**
         * 密码
         */
        private String password;
    }
}
