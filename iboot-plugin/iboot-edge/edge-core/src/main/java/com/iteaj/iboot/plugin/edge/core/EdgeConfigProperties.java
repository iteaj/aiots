package com.iteaj.iboot.plugin.edge.core;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(value = "iot.edge")
public class EdgeConfigProperties {

    /**
     * 服务器端口
     */
    private Integer port;

    /**
     * 读超时
     */
    private Long readIdle;

    /**
     * 边缘客户端配置
     */
    private ClientConfig remote;

    /**
     * 云端服务器配置
     */
    private ServerConfig server;

    @Getter
    @Setter
    private static class TCPConfig {
        /**
         * 主机配置
         */
        private String host;
    }

    @Getter
    @Setter
    public static class ServerConfig extends TCPConfig {

    }

    @Getter
    @Setter
    public static class ClientConfig extends TCPConfig {

    }
}
