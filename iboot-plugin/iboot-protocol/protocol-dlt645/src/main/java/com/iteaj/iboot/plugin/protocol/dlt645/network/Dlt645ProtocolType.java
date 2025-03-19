package com.iteaj.iboot.plugin.protocol.dlt645.network;

import com.iteaj.iot.ProtocolType;
import lombok.Getter;

public enum Dlt645ProtocolType implements ProtocolType {

    /**
     * 请求
     */
    rts((byte) 0x08, "广播校时"),

    readReq((byte) 0x11, "读数据"),
    readSubReq((byte) 0x12, "读后续数据"),
    readMAReq((byte) 0x13, "读通讯地址"),
    writeReq((byte) 0x14, "写数据"),
    writeMAReq((byte) 0x15, "写通讯地址"),
    fCmd((byte) 0x16, "冻结命令"),
    ffCmd((byte) 0x16, "广播冻结命令"),
    mcr((byte) 0x17, "修改通讯速率"),
    mpd((byte) 0x18, "修改密码"),
    rmd((byte) 0x19, "最大需量清零"),
    rem((byte) 0x1A, "电表清零"),
    rec((byte) 0x1B, "事件清零"),
    ctrl((byte) 0x1C, "控制设备"),

    /**
     * 响应
     */
    readReqResp((byte) 0x91, "读数据响应"),
    readSubReqResp((byte) 0x92, "读后续数据响应"),
    readMAResp((byte) 0x93, "读通讯地址响应"),
    writeResp((byte) 0x94, "写数据响应"),
    writeMAResp((byte) 0x95, "写通讯地址响应"),
    fCmdResp((byte) 0x96, "冻结命令响应"),
    mcrResp((byte) 0x97, "修改通讯速率响应"),
    mpdResp((byte) 0x98, "修改密码响应"),
    rmdResp((byte) 0x99, "最大需量清零响应"),
    remResp((byte) 0x9A, "电表清零响应"),
    recResp((byte) 0x9B, "事件清零响应"),
    readReqSubResp((byte) 0xB1, "读有后续数据响应"),
    readSubReqSubResp((byte) 0xB2, "读有后续的后续数据响应"),

    error((byte) 0xD0, "错误"), // 大于D0的值都为错误协议
    ;

    @Getter
    private final byte code;
    private final String desc;
    Dlt645ProtocolType(byte code, final String desc) {
        this.code = code;
        this.desc = desc;
    }


    @Override
    public Enum<Dlt645ProtocolType> getType() {
        return this;
    }

    @Override
    public String getDesc() {
        return this.desc;
    }

    public static Dlt645ProtocolType valueOf(byte code) {
        final Dlt645ProtocolType[] values = Dlt645ProtocolType.values();
        for (int i = 0; i < values.length; i++) {
            if(values[i].getCode() == code) {
                return values[i];
            }
        }

        return error;
    }
}
