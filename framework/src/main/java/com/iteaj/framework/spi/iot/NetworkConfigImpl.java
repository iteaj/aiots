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
    }

    @Data
    public static class HttpNetworkConfig extends NetworkConfigImpl {

    }
}
