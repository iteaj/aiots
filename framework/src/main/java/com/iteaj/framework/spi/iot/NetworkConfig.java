package com.iteaj.framework.spi.iot;

public interface NetworkConfig {

    /**
     * 主机地址
     * @return
     */
    String getHost();

    /**
     * 端口
     * @return
     */
    Integer getPort();

    /**
     * 是否启用ssl
     * @return
     */
    boolean isUseSsl();

}
