package com.iteaj.plugin.protocol.plc.adapt;

import com.iteaj.iot.ProtocolType;

public enum PlcProtocolType implements ProtocolType {
    Omron("欧姆龙PLC"),
    Siemens("西门子S7系列PLC"),
    ;

    private String desc;

    PlcProtocolType(String desc) {
        this.desc = desc;
    }

    @Override
    public Enum getType() {
        return this;
    }

    @Override
    public String getDesc() {
        return this.desc;
    }
}
