package com.iteaj.iboot.module.iot.consts;

import com.iteaj.framework.IVOption;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum EventSourceType {

    /**
     * 事件源
     */
    event("事件源"),

    /**
     * 定时源
     */
    period("定时源")
    ;

    private String desc;

    EventSourceType(String desc) {
        this.desc = desc;
    }

    public static List<IVOption> options() {
        return Arrays.stream(values())
                .map(item -> new IVOption(item.getDesc(), item.name()))
                .collect(Collectors.toList());
    }

    public String getDesc() {
        return desc;
    }
}
