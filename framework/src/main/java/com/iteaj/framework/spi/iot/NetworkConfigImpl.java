package com.iteaj.framework.spi.iot;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.iteaj.framework.ParamMeta;
import lombok.Data;

@Data
public abstract class NetworkConfigImpl implements NetworkConfig {

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
    private Boolean useSsl;

    /**
     * 设备编号
     */
    private String deviceSn;

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
    public Boolean isUseSsl() {
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
        private Long allIdle;

        /**
         * 读空闲
         * @return
         */
        private Long readIdle;

        /**
         * 写空闲
         * @return
         */
        private Long writeIdle;

        @JsonIgnore
        public static ParamMeta[] tcpMetas = new ParamMeta[]{
            ParamMeta.buildRequired("host", "主机", "客户端不校验(跟随设备)", "0.0.0.0"),
            ParamMeta.buildRequiredNumber("port", "端口", 8168, "必须是[1-65535]的整数"),
            ParamMeta.buildNumber("allIdle", "读写空闲(秒)", 0, "必须是大于0的整数"),
            ParamMeta.buildNumber("readIdle", "读空闲(秒)", 0, "必须是大于0的整数"),
            ParamMeta.buildNumber("writeIdle", "写空闲(秒)", 0, "必须是大于0的整数"),
//            ParamMeta.buildHidden("@type", "TCP"),
        };
    }

    /**
     * UDP协议配置
     */
    @Data
    public static class UdpNetworkConfig extends NetworkConfigImpl {
        public static ParamMeta[] udpMetas = new ParamMeta[]{

        };
    }

    /**
     * http客户端协议配置
     */
    @Data
    public static class HttpNetworkConfig extends NetworkConfigImpl {

        public static ParamMeta[] httpMetas = new ParamMeta[]{

        };
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

        public static ParamMeta[] serialMetas = new ParamMeta[]{

        };
    }

    /**
     * websocket配置
     */
    @Data
    public static class WebSocketConfig extends NetworkConfigImpl {
        public static ParamMeta[] websocketMetas = new ParamMeta[]{

        };
    }

    /**
     * mqtt客户端网络配置
     */
    @Data
    public static class MqttNetworkConfig extends TcpNetworkConfig {

        /**
         * 客户端id
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

        /**
         * 订阅的主题(支持分隔符[,])
         */
        private String topics;

        public static ParamMeta[] mqttMetas = new ParamMeta[]{
                ParamMeta.buildRequired("host", "主机", "mqtt服务器地址", "0.0.0.0"),
                ParamMeta.buildRequiredNumber("port", "端口", 1883, "mqtt服务器端口"),
                ParamMeta.buildRequired("username", "用户名"),
                ParamMeta.buildRequired("password", "密码"),
                ParamMeta.buildRequired("clientId", "客户端id"),
                ParamMeta.buildRequired("topics", "订阅主题列表", "支持多个订阅用逗号隔开", ""),
                ParamMeta.buildNumber("keepalive", "保活时间(秒)", 600, "必须是大于0的整数"),
        };
    }
}
