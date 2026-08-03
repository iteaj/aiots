package com.iteaj.iboot.module.iot.consts;

import com.iteaj.framework.IVOption;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum WarnGrade {

    fatal("严重"),
    urgent("紧急"),
    warn("告警"),
    notice("通知"),
    normal("解除"),
    status("状态"), // 用于一些状态通知
    ;

    private String desc;

    WarnGrade(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }

    public static List<IVOption> options() {
        return Arrays.stream(WarnGrade.values())
                .map(item -> new IVOption(item.desc, item.name()))
                .collect(Collectors.toList());
    }


    @Override
    public String toString() {
        return getDesc();
    }
}
