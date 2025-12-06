package com.iteaj.iboot.plugin.edge.core.network;

import com.iteaj.iot.ProtocolType;

public enum EdgeProtocolType implements ProtocolType {
    register(0x01, "设备注册"),
    heartbeat(0x02, "心跳"),
    ;

    private byte code;
    private String desc;

    EdgeProtocolType(int code, String desc) {
        this.desc = desc;
        this.code = (byte) code;
    }

    @Override
    public Enum<EdgeProtocolType> getType() {
        return this;
    }

    @Override
    public String getDesc() {
        return this.desc;
    }

    public byte getCode() {
        return code;
    }

    public static EdgeProtocolType getByCode(int code) {
        for (EdgeProtocolType value : values()) {
            if(value.code == code)
                return value;
        }
        return null;
    }
}
