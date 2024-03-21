package com.iteaj.framework.spi.iot.consts;

/**
 * 功能类型
 */
public enum FuncType {

    read("读"),
    write("写")
    ;

    private String desc;

    FuncType(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }
}
