package com.iteaj.iboot.module.iot.consts;

/**
 * 联动逻辑条件
 */
public enum LinkageLogical {
    or("或"),
    and("且");

    private String desc;

    LinkageLogical(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return this.desc;
    }
}
