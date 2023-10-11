package com.iteaj.iboot.module.iot.consts;

/**
 * 网关类型
 */
public enum GatewayType {

    /**
     * 将应用作为网关
     */
    App("应用网关"),

    /**
     * 边缘设备作为网关
     */
    Edge("边缘网关")
    ;

    private String desc;

    GatewayType(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }
}
