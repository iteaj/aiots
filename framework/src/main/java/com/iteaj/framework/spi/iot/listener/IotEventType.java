package com.iteaj.framework.spi.iot.listener;

import lombok.Getter;

@Getter
public enum IotEventType {

    Realtime("实时信息"),
    CmdInvoke("指令调用"),
    ScriptInvoke("脚本调用"),
    DeviceStatus("设备状态切换"),
    GatewaySwitch("网关状态切换"),
    ProductSwitch("产品状态切换"),
    DeviceCmdLog("设备指令日志"),
    ;

    private final String desc;

    IotEventType(String desc) {
        this.desc = desc;
    }

}
