package com.iteaj.iboot.module.iot.consts;

public enum EventSourceType {

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

    EventSourceType(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }
}
