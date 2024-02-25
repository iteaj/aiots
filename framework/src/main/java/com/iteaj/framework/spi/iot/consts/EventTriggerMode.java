package com.iteaj.framework.spi.iot.consts;

/**
 * 事件接口触发方式
 */
public enum EventTriggerMode {

    /**
     * 设备主动上报事件
     */
    active("主动"),

    /**
     * 服务器主动(设备被动)上报事件
     */
    passive("被动")
    ;

    private String desc;

    EventTriggerMode(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }
}
