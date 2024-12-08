package com.iteaj.iboot.plugin.protocol.opcua.network;

import com.iteaj.iot.client.ClientConnectProperties;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.eclipse.milo.opcua.sdk.client.api.config.OpcUaClientConfigBuilder;
import org.eclipse.milo.opcua.stack.core.security.SecurityPolicy;

@Getter
@Setter
@Accessors(chain = true)
public class OpcuaClientConfig extends ClientConnectProperties {

    private String username;
    private String password;
    private String endpointUrl;
    private OpcUaClientConfigBuilder builder;
    private SecurityPolicy securityPolicy = SecurityPolicy.None;

    /**
     * @param endpointUrl 格式 opc.tcp://ip:port
     */
    public OpcuaClientConfig(String endpointUrl) {
        this.endpointUrl = endpointUrl;
    }

    /**
     * opcua 服务器的ip和端口
     * @param remoteHost ip地址
     * @param remotePort 端口
     */
    public OpcuaClientConfig(String remoteHost, Integer remotePort) {
        super(remoteHost, remotePort);
        this.endpointUrl = String.format("opc.tcp://%s:%d", remoteHost, remotePort);
    }

    public OpcuaClientConfig(String remoteHost, Integer remotePort, String connectKey) {
        super(remoteHost, remotePort, connectKey);
        this.endpointUrl = String.format("opc.tcp://%s:%d", remoteHost, remotePort);
    }

    public OpcUaClientConfigBuilder getBuilder() {
        if(builder == null) {
            builder = new OpcUaClientConfigBuilder();
        }

        return builder;
    }

}
