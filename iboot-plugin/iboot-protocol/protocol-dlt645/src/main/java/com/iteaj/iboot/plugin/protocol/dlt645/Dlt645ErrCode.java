package com.iteaj.iboot.plugin.protocol.dlt645;

public enum Dlt645ErrCode {

    PWD_ERROR(0x37, "密码错误"),
    UNKNOWN(0x37, "未知错误码"),
    ;

    private int code;
    private String desc;

    Dlt645ErrCode(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public int getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public static Dlt645ErrCode valueOf(int code) {
        switch (code) {
            case 0x37: return PWD_ERROR;
            default: return UNKNOWN;
        }
    }
}
