package com.iteaj.plugin.protocol.plc.melsec.network;

import com.iteaj.iot.ProtocolType;

public enum MelsecProtocolType implements ProtocolType {
    Qna3E("Qna兼容3E帧"),
    Qna3E_R("Qna兼容3E帧(R序列)"),
    FxLinks("FxLinks"),
    FxSerial("FxSerial"),
    A1E("A兼容1E帧");

    private String desc;

    MelsecProtocolType(String desc) {
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
