package com.iteaj.iboot.module.iot.consts;

import com.iteaj.framework.spi.iot.consts.DeviceStatusEnum;

public enum DeviceStatus implements DeviceStatusEnum {

    online("在线"), // 在线
    offline("离线") // 离线
    ;

    private final String desc;
    DeviceStatus(String desc) {
        this.desc = desc;
    }

    public static DeviceStatus getInstance(String value) {
        if(value.contains(online.name())) {
            return online;
        } else if(value.contains(offline.name())) {
            return offline;
        } else {
            throw new IllegalArgumentException("错误的状态["+value+"]");
        }
    }

    public String getDesc() {
        return desc;
    }
}
