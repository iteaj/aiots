package com.iteaj.framework.spi.iot;

import com.iteaj.framework.IVOption;
import com.iteaj.framework.ParamMeta;
import com.iteaj.framework.spi.iot.consts.ConstConfig;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public abstract class NetworkConfigImpl implements NetworkConfig {

    /**
     * 设备唯一id
     */
    private String uid;

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
        private int
            baudRate = 9600, // 波特率
            dataBits = 8, // 数据位
            parity = 0, // 校验位
            stopBits = 1; // 停止位
        public SerialConfig() { }

        public SerialConfig(String com) {
            super(com, null);
        }

        public String getCom() {
            return this.getHost();
        }

        public void setCom(String com) {
            this.setHost(com);
        }

        public static ParamMeta[] serialMetas = new ParamMeta[]{
            ParamMeta.buildRequiredSelect("baudRate", "波特率", "9600", ""
                    , new IVOption("1200", "1200"), new IVOption("2400", "2400")
                    , new IVOption("4800", "4800"), new IVOption("9600", "9600")
                    , new IVOption("14400", "14400"), new IVOption("19200", "19200")
                    , new IVOption("38400", "38400"), new IVOption("56000", "56000")
                    , new IVOption("57600", "57600"), new IVOption("115200", "115200")).setPlaceholder("请输入波特率"),

            ParamMeta.buildRequiredSelect("dataBits", "数据位", "8", ""
                    , new IVOption("5", "5"), new IVOption("6", "6")
                    , new IVOption("7", "7"), new IVOption("8", "8")).setPlaceholder("请输入数据位"),

            ParamMeta.buildRequiredSelect("parity", "校验位", "0", ""
                    , new IVOption("无", "0")
                    , new IVOption("奇校验", "1"), new IVOption("偶校验", "2")
                    , new IVOption("标记校验", "3"), new IVOption("空格校验", "4")).setPlaceholder("请输入校验位"),

            ParamMeta.buildRequiredSelect("stopBits", "停止位", "1", ""
                    , new IVOption("1", "1")
                    , new IVOption("1.5", "2")
                    , new IVOption("2", "3")).setPlaceholder("请输入停止位"),

            ParamMeta.buildRequiredNumber("readIdle", "读超时时间(秒)", 5, "读超时时间"),
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
    @Getter
    @Setter
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
        private int keepalive;

        /**
         * 订阅的主题(支持分隔符[,])
         */
        private String topics;

        /**
         * 订阅等级
         */
        private Integer qos;

        public static ParamMeta[] mqttMetas = new ParamMeta[]{
                ParamMeta.buildRequired(ConstConfig.HOST, "主机", "", "").setPlaceholder("mqtt服务器地址"),
                ParamMeta.buildRequiredNumber(ConstConfig.PORT, "端口", 1883, "").setPlaceholder("mqtt服务器端口"),
                ParamMeta.build("username", "用户名"),
                ParamMeta.build("password", "密码"),
                ParamMeta.buildRequired("clientId", "客户端id"),
                ParamMeta.buildNumber("keepalive", "保活时间(秒)", 3600, "").setPlaceholder("必须是大于0的整数"),
                ParamMeta.buildRequiredNumber("qos", "消息等级", 0, "").setPlaceholder("可选值(0、1、2)"),
                ParamMeta.buildRequired("topics", "订阅的主题", "以网关子设备的产品编码开头", "").setPlaceholder("订阅的主题列表以逗号隔开"),
        };

        @Override
        public String toString() {
            return "{" +
                    "clientId='" + clientId + '\'' +
                    ", username='" + username + '\'' +
                    ", password='" + password + '\'' +
                    ", keepalive=" + keepalive +
                    ", topics='" + topics + '\'' +
                    ", qos=" + qos +
                    '}';
        }
    }
}
