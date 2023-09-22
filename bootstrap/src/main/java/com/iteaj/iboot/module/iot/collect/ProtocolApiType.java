package com.iteaj.iboot.module.iot.collect;

public enum ProtocolApiType {

    func("功能"),
    event("事件")
    ;

    private String desc;

    ProtocolApiType(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }
}
