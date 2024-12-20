package com.iteaj.iboot.plugin.protocol.opcua;

import com.iteaj.iboot.plugin.protocol.opcua.driver.OpcuaProtocolSupplier;
import com.iteaj.iot.IotThreadManager;
import io.netty.channel.nio.NioEventLoopGroup;
import org.eclipse.milo.opcua.sdk.client.OpcUaClient;
import org.eclipse.milo.opcua.sdk.client.api.UaClient;
import org.eclipse.milo.opcua.sdk.client.api.config.OpcUaClientConfig;
import org.eclipse.milo.opcua.sdk.client.api.identity.UsernameProvider;
import org.eclipse.milo.opcua.stack.client.DiscoveryClient;
import org.eclipse.milo.opcua.stack.core.security.SecurityPolicy;
import org.eclipse.milo.opcua.stack.core.types.builtin.DataValue;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.eclipse.milo.opcua.stack.core.types.builtin.Variant;
import org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.UInteger;
import org.eclipse.milo.opcua.stack.core.types.structured.EndpointDescription;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Bean;

import java.util.List;

public class OpcuaProtocolSupplierAutoConfiguration implements InitializingBean {

    @Bean
    public OpcuaProtocolSupplier opcuaProtocolSupplier() {
        return new OpcuaProtocolSupplier();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        // 获取端点描述列表
//        List<EndpointDescription> endpoints = DiscoveryClient.getEndpoints("opc.tcp://127.0.0.1:49320").get();
//
//        // 根据安全策略选择合适的端点
//        EndpointDescription endpoint = endpoints.stream()
//                .filter(e -> e.getSecurityPolicyUri().equals(SecurityPolicy.None.getUri()))
//                .findFirst()
//                .orElseThrow(() -> new Exception("没有找到匹配安全策略的端点"));

        // 创建 OPC UA 客户端配置
//        NioEventLoopGroup workerGroup = (NioEventLoopGroup) IotThreadManager.instance().getWorkerGroup();
//        OpcUaClientConfig config = OpcUaClientConfig.builder()
//                .setEndpoint(endpoint)
//                .setEventLoop(workerGroup)
////                .setIdentityProvider(new UsernameProvider("iteaj", "123456"))
//                .setRequestTimeout(UInteger.valueOf(5000))
//                .build();

//        UaClient uaClient = OpcUaClient.create(config).connect().get();
//        NodeId nodeId = NodeId.parse("ns=2;s=通道 1.设备 1.标记 2");
//        DataValue dataValue = uaClient.getAddressSpace().getVariableNode(nodeId).readValue();
//        System.out.printf(dataValue.toString());

//        uaClient.getAddressSpace().getVariableNode(nodeId).writeValue(new Variant(150.3));
//        DataValue dataValue1 = new DataValue(new Variant(135.3));
//        DataValue build = DataValue.newValue().setValue(new Variant(135.3)).build();
//        uaClient.writeValue(nodeId, build);
//        System.out.println();
    }
}
