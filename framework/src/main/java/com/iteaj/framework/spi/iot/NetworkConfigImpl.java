package com.iteaj.framework.spi.iot;

import lombok.Data;

@Data
public class NetworkConfigImpl implements NetworkConfig {

    /**
     * 主机地址
     */
    private String host;

    /**
     * 端口
     */
    private Integer port;

    /**
     * 是否启用ssl
     */
    private boolean useSsl;

    public NetworkConfigImpl() { }

    public NetworkConfigImpl(Integer port) {
        this.port = port;
    }

    public NetworkConfigImpl(String host, Integer port) {
        this.host = host;
        this.port = port;
    }

    public NetworkConfigImpl(String host, Integer port, boolean useSsl) {
        this.host = host;
        this.port = port;
        this.useSsl = useSsl;
    }

    @Override
    public String getHost() {
        return this.host;
    }

    @Override
    public Integer getPort() {
        return this.port;
    }

    @Override
    public boolean isUseSsl() {
        return this.useSsl;
    }

    /**
     * tcp协议配置
     */
    @Data
    public static class TcpNetworkConfig extends NetworkConfigImpl {

        /**
         * 读写空闲
         * @return
         */
        private long allIdle;

        /**
         * 读空闲
         * @return
         */
        private long readIdle;

        /**
         * 写空闲
         * @return
         */
        private long writeIdle;
    }

    @Data
    public static class HttpNetworkConfig extends NetworkConfigImpl {

    }

    /**
     * 串口配置
     */
    @Data
    public static class SerialConfig extends NetworkConfigImpl {

        public SerialConfig(String com) {
            super(com, null);
        }

        public String getCom() {
            return this.getHost();
        }
    }

    /**
     * mqtt网络配置
     */
    @Data
    public static class MqttNetworkConfig extends TcpNetworkConfig {

        /**
         * 客户端口id
         */
        private String clientId;

        /**
         * 用户名
         */
        private String username;

        /**
         * 密码
         */
        private String password;

        /**
         * 保活时间
         */
        private long keepalive;
    }
}
