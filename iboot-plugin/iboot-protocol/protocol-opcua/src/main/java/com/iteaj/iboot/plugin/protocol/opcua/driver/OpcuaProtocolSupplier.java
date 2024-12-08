package com.iteaj.iboot.plugin.protocol.opcua.driver;

import com.iteaj.framework.spi.iot.*;
import com.iteaj.framework.spi.iot.consts.*;
import com.iteaj.framework.spi.iot.protocol.*;
import com.iteaj.iboot.plugin.protocol.opcua.network.OpcuaClientConfig;
import com.iteaj.iboot.plugin.protocol.opcua.network.OpcuaClientWrapper;
import com.iteaj.iboot.plugin.protocol.opcua.network.OpcuaComponent;
import com.iteaj.iboot.plugin.protocol.opcua.network.OpcuaProtocol;
import com.iteaj.iot.Protocol;
import com.iteaj.iot.client.ClientConnectProperties;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

public class OpcuaProtocolSupplier extends StaticClientProtocolSupplier<OpcuaProtocolSupplier.OpcUaNetworkConfig, OpcuaComponent, OpcuaClientWrapper> {

    private final static Map<String, ProtocolModelAttr> modelAttrs;
    private final static Map<String, AbstractProtocolModelApi> modelApis;

    static {
        modelAttrs = new HashMap<>();
        modelApis = new HashMap<>();

        modelAttrs.put("value", new ProtocolModelAttr("value", "值", DataType.t_any, AttrType.IGNORE));
        modelAttrs.put("address", new ProtocolModelAttr("address", "地址", DataType.t_string, AttrType.RW));
        modelAttrs.put("dataType", new ProtocolModelAttr("dataType", "数据类型", DataType.t_string, AttrType.IGNORE));

        // 读变量接口
        OpcuaProtocol.OpcuaProtocolType read = OpcuaProtocol.OpcuaProtocolType.READ;
        modelApis.put(read.name(), new OpcuaProtocolEventModelApi(read.name(), read.getDesc(), "读取变量节点值")
                .addDownConfig(ProtocolModelApiConfig.downBuild("address", "ns=2;s=设备.标记"))
                .addUpConfig(ProtocolModelApiConfig.upBuild("value", "读取到的值")));

        // 读对象接口
//        OpcuaProtocol.OpcuaProtocolType readObject = OpcuaProtocol.OpcuaProtocolType.READ_OBJECT;
//        modelApis.put(readObject.name(), new OpcuaProtocolEventModelApi(readObject.name(), readObject.getDesc(), "读取对象节点"));

        // 写数据接口
        OpcuaProtocol.OpcuaProtocolType write = OpcuaProtocol.OpcuaProtocolType.WRITE;
        modelApis.put(write.name(), new OpcuaProtocolFuncModelApi(write.name(), write.getDesc(), "写入变量节点值")
                .addDownConfig(ProtocolModelApiConfig.downBuild("address", "ns=2;s=设备.标记"))
                .addDownConfig(ProtocolModelApiConfig.downBuild("value", "写入的值")));

        // 执行方法接口
        OpcuaProtocol.OpcuaProtocolType execMethod = OpcuaProtocol.OpcuaProtocolType.EXEC_METHOD;
        modelApis.put(execMethod.name(), new OpcuaProtocolFuncModelApi(execMethod.name(), execMethod.getDesc(), "执行方法节点"));

    }

    @Override
    public String getDesc() {
        return "Opcua协议驱动";
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
    public DataSupplier getDataSupplier(Protocol protocol, ProtocolModelApiInvokeParam param, Object value) {
        return null;
    }

    @Override
    protected OpcuaClientWrapper doCreateClient(OpcUaNetworkConfig config, OpcuaComponent component) {
        OpcuaClientConfig properties = new OpcuaClientConfig(config.getHost(), config.getPort(), config.getDeviceSn());
        return new OpcuaClientWrapper(component, properties);
    }

    @Override
    protected OpcuaComponent doCreateComponent(OpcUaNetworkConfig config) {
        return new OpcuaComponent();
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
