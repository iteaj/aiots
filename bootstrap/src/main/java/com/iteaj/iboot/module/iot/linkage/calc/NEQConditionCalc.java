package com.iteaj.iboot.module.iot.linkage.calc;

import com.iteaj.iboot.module.iot.linkage.ConditionCalc;

public class NEQConditionCalc implements ConditionCalc {
    @Override
    public String getName() {
        return "不等于";
    }

    @Override
    public String getOperational() {
        return "neq";
    }
}
