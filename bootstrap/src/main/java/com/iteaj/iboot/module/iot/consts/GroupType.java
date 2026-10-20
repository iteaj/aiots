package com.iteaj.iboot.module.iot.consts;

public enum GroupType {
    archive("归档组"), // 归档组
    device("设备组"), // 设备组
    product("产品组") // 产品组
    ;

    private String desc;

    GroupType(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }
}
