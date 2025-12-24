package com.iteaj.iboot.module.iot.consts;

import com.iteaj.framework.IVOption;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum EventSourceType {

    /**
     * 事件源
     */
    event("采集器"),

    /**
     * 定时源
     */
    period("执行器")
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
