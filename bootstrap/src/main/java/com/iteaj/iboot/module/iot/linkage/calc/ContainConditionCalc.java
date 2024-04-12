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
}
