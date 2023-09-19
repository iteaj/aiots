package com.iteaj.iboot.module.iot.consts;

import com.iteaj.framework.IVOption;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum ProtocolImplMode {

    Jar("Jar导入"),
    Custom("自定义"),
    Internal("系统内置")

    ;

    private String desc;

    ProtocolImplMode(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }

    public static List<IVOption> options() {
        return Arrays.stream(ProtocolImplMode.values())
                .map(item -> new IVOption(item.getDesc(), item.name()))
                .collect(Collectors.toList());
    }
}
