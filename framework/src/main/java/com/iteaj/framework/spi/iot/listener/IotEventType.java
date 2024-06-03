package com.iteaj.framework.spi.iot.listener;

public enum IotEventType {

    DeviceStatus("设备状态切换"),
    GatewaySwitch("网关状态切换"),
    ProductSwitch("产品状态切换"),
    ;

    private String desc;

    IotEventType(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }
}
