package com.iteaj.iboot.module.iot.linkage.calc;

import com.iteaj.iboot.module.iot.linkage.ConditionCalc;

public class ContainConditionCalc implements ConditionCalc {

    @Override
    public String getName() {
        return "包含";
    }

    @Override
    public String getOperational() {
        return "contain";
    }

    @Override
    public boolean calc(Object value, Object condition) {
        return value.toString().contains(condition.toString());
    }
}
