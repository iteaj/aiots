package com.iteaj.iboot.module.iot.consts;

/**
 * 网关连接类型
 */
public enum GatewayConnectType {
    Client("客户端"), // 客户端
    Server("服务端"), // 服务端
    ;

    private String desc;

    GatewayConnectType(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }
}
