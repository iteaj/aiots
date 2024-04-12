package com.iteaj.iboot.module.iot.consts;

import com.iteaj.framework.IVOption;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum OperatorType {
    gt("大于"), // >
    gte("大于等于"), // >=
    eq("等于"), // =
    lt("小于"), // <
    lte("小于等于"), // <=
    neq("不等于"), // <=
    contain("包含"), // 包含
    between("在...之间"), // 在...之间
    ;

    private String desc;

    OperatorType(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }

    public static List<IVOption> options() {
        return Arrays.stream(OperatorType.values())
                .map(item -> new IVOption(item.desc, item.name()))
                .collect(Collectors.toList());
    }
}
