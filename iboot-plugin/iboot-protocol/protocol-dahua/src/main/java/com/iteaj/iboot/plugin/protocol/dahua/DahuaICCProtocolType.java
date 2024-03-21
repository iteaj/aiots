package com.iteaj.iboot.plugin.protocol.dahua;

import com.iteaj.iot.ProtocolType;

public enum DahuaICCProtocolType implements ProtocolType {
    openDoor("开门"),
    closeDoor("关门"),
    stayOpen("常开"),
    stayClose("常闭"),
    normal("正常模式"),
    ;

    DahuaICCProtocolType(String desc) {
        this.desc = desc;
    }

    private String desc;

    @Override
    public Enum getType() {
        return this;
    }

    @Override
    public String getDesc() {
        return this.desc;
    }
}
