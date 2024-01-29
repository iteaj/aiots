package com.iteaj.framework.spi.iot.consts;

/**
 * 事件接口类型
 */
public enum ApiEventType {

    /**
     * 设备主动上报事件
     */
    ACTIVE("主动"),

    /**
     * 服务器主动(设备被动)上报事件
     */
    PASSIVE("被动")
    ;

    private String desc;

    ApiEventType(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }
}
