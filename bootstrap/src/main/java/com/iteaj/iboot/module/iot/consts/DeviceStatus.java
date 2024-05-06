package com.iteaj.iboot.module.iot.consts;

public enum DeviceStatus {

    online, // 在线
    offline // 离线
    ;
    public static DeviceStatus getInstance(String value) {
        if(value.contains(online.name())) {
            return online;
        } else if(value.contains(offline.name())) {
            return offline;
        } else {
            throw new IllegalArgumentException("错误的状态["+value+"]");
        }
    }
}
