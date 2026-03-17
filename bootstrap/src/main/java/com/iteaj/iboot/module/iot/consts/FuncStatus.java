package com.iteaj.iboot.module.iot.consts;

import com.iteaj.framework.spi.iot.consts.FuncStatusEnum;

public enum FuncStatus implements FuncStatusEnum {

    enabled("启用"), // 启用
    disabled("停用") // 禁用
    ;

    private String desc;

    FuncStatus(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }
}
