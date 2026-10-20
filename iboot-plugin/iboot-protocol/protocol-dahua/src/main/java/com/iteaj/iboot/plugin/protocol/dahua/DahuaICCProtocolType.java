package com.iteaj.iboot.plugin.protocol.dahua;

import com.iteaj.iot.ProtocolType;

public enum DahuaICCProtocolType implements ProtocolType {
    openDoor("开门", "/evo-apigw/evo-accesscontrol/1.0.0/card/accessControl/channelControl/openDoor"),
    closeDoor("关门", "/evo-apigw/evo-accesscontrol/1.0.0/card/accessControl/channelControl/closeDoor"),
    stayOpen("常开", "/evo-apigw/evo-accesscontrol/1.0.0/card/accessControl/channelControl/stayOpen"),
    stayClose("常闭", "/evo-apigw/evo-accesscontrol/1.0.0/card/accessControl/channelControl/stayClose"),
    normal("正常模式", "/evo-apigw/evo-accesscontrol/1.0.0/card/accessControl/channelControl/normal"),

    realtime("获取实时流", "/evo-apigw/admin/API/video/stream/realtime"),
    OperateDirect("云台方向控制", "/evo-apigw/admin/API/DMS/Ptz/OperateDirect"),
    ;

    DahuaICCProtocolType(String desc, String url) {
        this.url = url;
        this.desc = desc;
    }

    private String url;
    private String desc;

    @Override
    public Enum getType() {
        return this;
    }

    public String getUrl() {
        return url;
    }

    @Override
    public String getDesc() {
        return this.desc;
    }
}
