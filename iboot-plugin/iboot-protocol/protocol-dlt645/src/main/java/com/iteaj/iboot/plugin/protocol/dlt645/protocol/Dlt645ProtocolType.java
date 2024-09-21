package com.iteaj.iboot.plugin.protocol.dlt645.protocol;

import com.iteaj.iot.ProtocolType;
import lombok.Getter;

public enum Dlt645ProtocolType implements ProtocolType {

    /**
     * 请求
     */
    rts("08", "广播校时"),

    readReq("11", "读数据"),
    readSubReq("12", "读后续数据"),
    readMAReq("13", "读通讯地址"),
    writeReq("14", "写数据"),
    writeMAReq("15", "写通讯地址"),
    fCmd("16", "冻结命令"),
    mcr("17", "修改通讯速率"),
    mpd("18", "修改密码"),
    rmd("19", "最大需量清零"),
    rem("1A", "电表清零"),
    rec("1B", "事件清零"),


    /**
     * 响应
     */
    readReqResp("91", "读数据响应"),
    readSubReqResp("92", "读后续数据响应"),
    readMAResp("93", "读通讯地址响应"),
    writeResp("94", "写数据响应"),
    writeMAResp("95", "写通讯地址响应"),
    fCmdResp("96", "冻结命令响应"),
    mcrResp("97", "修改通讯速率响应"),
    mpdResp("98", "修改密码响应"),
    rmdResp("99", "最大需量清零响应"),
    remResp("9A", "电表清零响应"),
    recResp("9B", "事件清零响应"),
    readReqSubResp("B1", "读有后续数据响应"),
    readSubReqSubResp("B2", "读有后续的后续数据响应"),

    error("D0", "错误"), // 大于D0的值都为错误协议
    ;

    @Getter
    private final String code;
    private final String desc;
    Dlt645ProtocolType(String code, final String desc) {
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

}
